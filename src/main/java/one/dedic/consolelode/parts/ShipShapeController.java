/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package one.dedic.consolelode.parts;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.DoublePrintingTextGraphics;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.WrapBehaviour;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import one.dedic.consolelode.Controller;
import one.dedic.consolelode.GameOptions;
import one.dedic.consolelode.GameState;
import one.dedic.consolelode.data.Cell;
import one.dedic.consolelode.data.ItemTemplate;
import one.dedic.consolelode.model.BoardState;

/**
 *
 * @author sdedic
 */
public class ShipShapeController extends SetupController {
    private static final int SHIP_LIST_COL = 25;
    private static final int SHIP_LIST_ROW = 2;
    
    private static final int SHIP_SLOT_WIDTH = 15;
    private static final int SHIP_SLOT_HEIGHT = 5;

    private TextGraphics shipListG;
    private int selectedIndex;
    private int size;
    private List<ItemTemplate> selectFrom = new ArrayList<>();
    private List<TerminalPosition> promptPositions = new ArrayList<>();
    private int shipsPerRow;
    private int shipWidth;
    private int shipHeight;

    @Override
    public void setup() {
        super.setup();
        shipListG = graphics.newTextGraphics(new TerminalPosition(SHIP_LIST_COL, SHIP_LIST_ROW), 
                new TerminalSize(graphics.getSize().getColumns() - SHIP_LIST_COL, 20));
        
    }

    @Override
    public void initialize() {
        super.initialize();
        promptPositions = new ArrayList<>();
        selectedIndex = 0;
        setShipSize(boardState.getCurrentSize());
    }

    void printInstructions() {
        TextGraphicsWriter writer = new TextGraphicsWriter(g);
        writer.putString("Vyber tvar lodi. Stiskni pismeno odpovidajici tvaru a potvrd ");
        writer.enableModifiers(SGR.BOLD);
        writer.putString("ENTER");
        writer.clearModifiers();
        writer.putString(".\n");
        writer.enableModifiers(SGR.BOLD);
        writer.putString("ESC");
        writer.clearModifiers();
        writer.putString(" - vyber velikosti");
    }

    public ItemTemplate getSelectedShip() {
        return selectFrom.get(selectedIndex);
    }
    
    public void setShipSize(int size) {
        if (this.size == size) {
            return;
        }
        List<ItemTemplate> filtered = new ArrayList<>();
        for (ItemTemplate t : options.getLibrary().getTemplates()) {
            if (t.getShipSize() == size) {
                filtered.add(t);
            }
        }
        Collections.sort(filtered, (a, b) -> a.getId().compareToIgnoreCase(b.getId()));
        this.selectFrom = filtered;
        this.shipWidth = selectFrom.stream().map(s -> s.getName().length() + 4).max(Comparator.naturalOrder()).get();
        this.shipHeight = selectFrom.stream().map(s -> s.getHeight() + 3).max(Comparator.naturalOrder()).get();
    }
    
    int dx = 0;
    int dy = 0;
    int currentIndex;
    TextGraphics shipG;
    
    void paintChoice(int index, int... overrideSelected) {
        TextGraphics ship = shipListG.newTextGraphics(TerminalPosition.TOP_LEFT_CORNER, shipListG.getSize());
        int sel = selectedIndex;
        if (overrideSelected.length > 0) {
            sel = overrideSelected[0];
        }
        if (index == sel) {
            ship.setBackgroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            ship.setForegroundColor(TextColor.ANSI.BLACK);
        }
        ship.setModifiers(EnumSet.of(SGR.BOLD));
        TerminalPosition pos = promptPositions.get(index);
        ship.setCharacter(pos, (char)(index + 'A'));
        ship.clearModifiers();
    }
    
    void printShipSlot(ItemTemplate t) {
        TextGraphicsWriter writer = new TextGraphicsWriter(
                shipG.newTextGraphics(new TerminalPosition(2, 0), 
                        new TerminalSize(shipG.getSize().getColumns() - 2, 4))
        );
        writer.setWrapBehaviour(WrapBehaviour.WORD);
        writer.putString(t.getName());
        
        TextGraphics shape = 
                new DoublePrintingTextGraphics(
                shipG.newTextGraphics(new TerminalPosition(0, 2), 
                        new TerminalSize(shipG.getSize().getColumns(), shipG.getSize().getRows()))
        );
        
        for (int r = 0; r < t.getHeight(); r++) {
            for (int c = 0; c < t.getWidth(); c++) {
                Cell cell = t.getCell(r, c);
                if (cell.isShip()) {
                    shape.setCharacter(c, r, Symbols.BLOCK_DENSE);
                } else {
                    shape.setCharacter(c, r, ' ');
                }
            }
        }
    }
    
    public void drawShipList() {
        shipListG.fill(' ');
        
        promptPositions = new ArrayList<>();
        shipsPerRow = 0;
        currentIndex = 0;
        dx = 0;
        dy = 0;
        int count = 0;
        for (ItemTemplate t : selectFrom) {
            TerminalPosition pos = new TerminalPosition(dx, SHIP_LIST_ROW + dy);
            promptPositions.add(pos);
            shipG = shipListG.newTextGraphics(pos, new TerminalSize(SHIP_SLOT_WIDTH, SHIP_SLOT_HEIGHT));
            printShipSlot(t);
            count++;
            dx += shipWidth;
            if (dx +  shipWidth > shipListG.getSize().getColumns()) {
                dx = 0;
                dy += shipHeight;
                
                // priradi se jen poprve, pote uz bude > 0 a na `count` nebude nijak zalezet
                if (shipsPerRow == 0) {
                    shipsPerRow = count;
                }
            } 
            currentIndex++;
        }
        if (shipsPerRow == 0) {
            shipsPerRow = count;
        }
        
        for (int i = 0; i < selectFrom.size(); i++) {
            paintChoice(i);
        }
    }

    @Override
    protected void printLayout() {
        super.printLayout();
        drawShipList();
    }

    @Override
    protected void printDescription() {
        super.printDescription();
        printInstructions();
    }
    
    void clearPrompt() {
        paintChoice(selectedIndex, -1);
    }

    @Override
    protected GameState handleConfirmed(GameState state) {
        boardState.setSelectedTemplate(this.getSelectedShip());
        return state;
    }
    
    @Override
    protected void printPrompt() {
        paintChoice(selectedIndex);
    }
    
    void moveUpDown(boolean down) {
        int add;
        
        if (down) {
            if (selectedIndex + shipsPerRow >= selectFrom.size()) {
                return;
            }
            add = shipsPerRow;
        } else {
            if (selectedIndex < shipsPerRow) {
                return;
            }
            add = -shipsPerRow;
        }
        clearPrompt();
        selectedIndex += add;
    }
    
    void moveLeftRight(boolean right) {
        int add;
        
        if (right) {
            if (selectedIndex + 1 >= selectFrom.size()) {
                return;
            }
            add = 1;
        } else {
            if (selectedIndex < 1) {
                return;
            }
            add = -1;
        }
        clearPrompt();
        selectedIndex += add;
    }

    @Override
    protected NextState handle(KeyStroke ks) {
        switch (ks.getKeyType()) {
            case ARROW_UP:
                moveUpDown(false);
                break;
            case ARROW_DOWN:
                moveUpDown(true);
                break;

            case ARROW_LEFT:
                moveLeftRight(false);
                break;
            case ARROW_RIGHT:
                moveLeftRight(true);
                break;
                
            case CHARACTER:
                int idx = ks.getCharacter().toString().toLowerCase().charAt(0) - 'a';
                if (idx < 0 || idx >= selectFrom.size()) {
                    break;
                }
                clearPrompt();
                selectedIndex = idx;
                break;
        }
        return null;
    }
}

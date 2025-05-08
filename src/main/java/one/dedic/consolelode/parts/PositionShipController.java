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

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import one.dedic.consolelode.Controller;
import one.dedic.consolelode.GameOptions;
import one.dedic.consolelode.GameState;
import one.dedic.consolelode.data.ItemTemplate;
import one.dedic.consolelode.data.Placement;
import one.dedic.consolelode.model.BoardState;

/**
 *
 * @author sdedic
 */
public class PositionShipController extends SetupController {
    private ItemTemplate selectedItem;
    private Placement placement;

    /**
     * Oblast napravo od hraci plochy, asi jen pro vymaz
     */
    private TextGraphics sideG;

    /**
     * Instrukce, nahore vedle hraci plochys
     */
    private TextGraphics instructionsG;
    
    /**
     * Stavove info, pod instrukcemi
     */
    private TextGraphics statusG;
    
    private boolean placed;
    private boolean statusError;
    
    private static final int SHIP_LIST_COL = 25;
    private static final int STATUS_ROW = 3;

    @Override
    public void setup() {
        super.setup();
        TerminalPosition topLeft = new TerminalPosition(SHIP_LIST_COL, 0);
        instructionsG = graphics.newTextGraphics(topLeft, 
                graphics.getSize().withRelativeColumns(-SHIP_LIST_COL).withRows(6));

        statusG = graphics.newTextGraphics(topLeft.withRelativeRow(instructionsG.getSize().getRows() + 2),
                new TerminalSize(graphics.getSize().getColumns() - SHIP_LIST_COL, 5));
        
        
        TextGraphics g2 = graphics.newTextGraphics(statusG.toScreenPosition(TerminalPosition.TOP_LEFT_CORNER).withRelative(0, statusG.getSize().getRows()),
                new TerminalSize(graphics.getSize().getColumns() - SHIP_LIST_COL, 5));
        setStatusTextGraphics(g2);

        sideG = graphics.newTextGraphics(topLeft, 
                graphics.getSize().withRelativeColumns(-SHIP_LIST_COL).withRows(30));
    }

    public void setSelectedItem(ItemTemplate selectedItem) {
        this.selectedItem = selectedItem;
        if (selectedItem != null) {
            this.placement = new Placement(selectedItem);
        } else {
            this.placement = null;
        }
        boardState.modifyCurrent(null);
    }

    @Override
    public void initialize() {
        super.initialize();
        placed = false;
        setSelectedItem(boardState.getSelectedTemplate());
    }

    @Override
    protected void printDescription() {
        super.printDescription();
        printInstructions();
    }

    @Override
    protected void printLayout() {
        super.printLayout();
        printStatusTemplate();
    }

    void printInstructions() {
        TextGraphicsWriter writer = new TextGraphicsWriter(instructionsG);
        writer.putString("Pouzij sipky, nebo souradnice pismeno-cislo (ENTER za cislem). ");
        writer.putString("PgUp, PgDn - otaceni.\n");
        writer.putString("Home, End - zrcadleni.\n");
        writer.putString("E - oprava existujici lodi\n");
        writer.putString("\nUmisti lod na plan. (ESC) zrusi akci:");
    }
    
    private static final int STATUS_COORDS_ROW = 5;
    private static final int STATUS_COORDS_COL = 17;
    private static final int STATUS_COORDS_COL_ABS = SHIP_LIST_COL + STATUS_COORDS_COL;
    private static final int STATUS_MESSAGE_ROW = 3;

    private static final int STATUS_MESSAGE_ROW_ABS = STATUS_COORDS_ROW + STATUS_MESSAGE_ROW;
    
    void printStatusTemplate() {
        TextGraphicsWriter writer = new TextGraphicsWriter(statusG);
        writer.setCursorPosition(new TerminalPosition(0, 0));
        // Ovlivnuje STATUS_COORDS_COL, ten musi byt za ": ".
        writer.putString("Vybrany sloupec: - ");
        writer.setCursorPosition(new TerminalPosition(0, 1));
        writer.putString("Vybrany radek  : - ");
        
        clearStatusMessage();
    }
    
    void clearStatusMessage() {
        statusPrinter.clearStatusMessage();
    }
    
    void printStatus() {
        if (boardState.getCursorX() != -1) {
            graphics.putString(STATUS_COORDS_COL_ABS, STATUS_MESSAGE_ROW_ABS, "" + (char)('A' + boardState.getCursorX()));
        } else {
            graphics.putString(STATUS_COORDS_COL_ABS, STATUS_MESSAGE_ROW_ABS, "-");
        }
        
        if (rowNumber != -1) {
            String text = (rowNumber + 1) + "?";
            TerminalPosition pos = new TerminalPosition(STATUS_COORDS_COL_ABS, STATUS_MESSAGE_ROW_ABS + 1);
            graphics.putString(pos, text);
            try {
                screen.refresh();
                //screen.setCursorPosition(pos.withRelativeColumn(text.length() - 1));
            } catch (IOException ex) {
                Logger.getLogger(PositionShipController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            screen.setCursorPosition(null);
            
            if (boardState.getCursorY() != -1) {
                graphics.putString(STATUS_COORDS_COL_ABS, STATUS_MESSAGE_ROW_ABS + 1, (1 + boardState.getCursorY()) + " ");
            } else {
                graphics.putString(STATUS_COORDS_COL_ABS, STATUS_MESSAGE_ROW_ABS + 1, "- ");
            }
        }
        
        statusPrinter.printStatus();
    }
    
    void printStatusMessage(String s, boolean error) {
        statusPrinter.printStatusMessage(s, error);
        printStatus();
    }
    
    void printStatusMessage(String s) {
        printStatusMessage(s, true);
    }
    
    private int rowNumber = -1;
    
    void updateCoordinates() {
        boardPrinter.printBoardContents();
        boardPrinter.printBoardLabels();
        printStatus();
    }
    
    void processCharacters(char c) {
        if (c >= 'A' && c <= 'Z') {
            int col = c - 'A';
            boardState.setCursorX(col);
            updateCoordinates();
            
        } else {
            processNumeric(c);
        }
    }
    
    void processNumeric(char c) {
        if (!(c >= '0' && c <= '9')) {
            return;
        }
        int newRow;
        if (rowNumber == -1) {
            newRow = c - '0' - 1;
        } else {
            newRow = rowNumber * 10 + (c - '0' - 1);
        }
        if (newRow > options.getBoardSize()) {
            printStatusMessage("Cislo radku je prilis velke.");
        } else {
            clearStatusMessage();
        }
        rowNumber = newRow;
        printStatus();
    }
    
    void confirmNumberOrShip() {
        if (rowNumber >= 0) {
            if (rowNumber > options.getBoardSize()) {
                rowNumber = -1;
                boardState.setCursorY(-1);
                updateCoordinates();
            } else {
                boardState.setCursorY(rowNumber);
                rowNumber = -1;
                clearStatusMessage();
                updateCoordinates();
            }
            return;
        }
        
        if (placement != null) {
            if (boardState.getCursorX() != -1) {
                placement.setX(boardState.getCursorX());
            }
            if (boardState.getCursorY() != -1) {
                placement.setY(boardState.getCursorY());
            }
            boardState.modifyCurrent(placement);
            boardState.resetCursor();
            
            boardState.refreshState();

            boardPrinter.printBoardLabels();
            boardPrinter.printBoardContents();
            
            clearStatusMessage();
            placed = false;
            switch (boardState.getShipState()) {
                case OutOfGrid:
                    printStatusMessage("Cast lodi je mimo plochu.");
                    break;
                case Overlapping:
                case Touching:
                    printStatusMessage("Lod se prekryva, nebo dotyka jine.");
                    break;
                case OK:
                    placed = true;
                    printStatusMessage("Potvrd umisteni lodi ENTER.", false);
                    break;
            }
        }
    }
    
    void updateCurrentPlacement() {
        boardPrinter.printBoardContents();
    }
    
    void startBoardTargetting() {
        if (!boardState.isTargetting()) {
            if (placement != null) {
                boardState.setCursorX(placement.getX());
                boardState.setCursorY(placement.getY());
            } else {
                boardState.setCursorX(0);
                boardState.setCursorY(0);
            }
                    
        }
    }
    
    void moveLeftRight(boolean left) {
        if (rowNumber >= 0) {
            return;
        }
        startBoardTargetting();
        int max = left ? Integer.MAX_VALUE : options.getBoardSize() - 1;
        int min = left ? 1 : 0;
        
        int x = boardState.getCursorX();
        if (x < min || x >= max) {
            // TODO: neco rict uzivateli ?
            return;
        }
        x += left ? -1 : 1;
        boardState.setCursorX(x);
        updateCoordinates();
    }

    void moveUpDown(boolean up) {
        if (rowNumber >= 0) {
            return;
        }
        startBoardTargetting();
        int max = up ? Integer.MAX_VALUE : options.getBoardSize() - 1;
        int min = up ? 1 : 0;

        int y = boardState.getCursorY();
        if (y < min || y >= max) {
            // TODO: neco rict uzivateli ?
            return;
        }
        y += up ? -1 : 1;
        boardState.setCursorY(y);
        updateCoordinates();
    }
    
    public Result cancelStageOrNumber() {
        if (rowNumber >= 0) {
            rowNumber = -1;
            updateCoordinates();
            return null;
        }
        if (boardState.isTargetting()) {
            boardState.resetCursor();
            updateCoordinates();
            return null;
        } 
        return Result.CANCEL;
    }
    
    Result editCurrentTarget() {
        return null;
    }
    
    @Override
    protected NextState handle(KeyStroke ks) {
        if (rowNumber > -1) {
            switch (ks.getKeyType()) {
                case CHARACTER:
                    processNumeric(ks.getCharacter());
                    return null;
                case ESCAPE:
                    cancelStageOrNumber();
                    return null;
                    
                case ENTER:
                    confirmNumberOrShip();
                    return null;
            }
        }
        
        switch (ks.getKeyType()) {
            case CHARACTER:
                char c = Character.toUpperCase(ks.getCharacter());
                if (c == 'E') {
                    return null;
                }
                processCharacters(c);
                return null;
                
            case ENTER:
                if (!placed) {
                    confirmNumberOrShip();
                    return CONTINUE;
                } else if (boardState.getShipState() == BoardState.ShipState.OK
                        && boardState.getCurrent() != null) {
                    boardState.finishEditing();
                    return NextState.create(Result.OK, GameState.SIZE_SELECT);
                }  else {
                    return CONTINUE;
                }
                
            case ESCAPE:
                Result r = cancelStageOrNumber();
                if (r != null) {
                    if (placed) {
                        boardState.removeItem(placement);
                        boardState.resetCursor();
                        boardPrinter.printBoardContents();
                    }
                }
                return r == null ? CONTINUE : NextState.result(r);
                
            case ARROW_UP:
                moveUpDown(true);
                break;
            case ARROW_DOWN:
                moveUpDown(false);
                break;
                
            case ARROW_LEFT:
                moveLeftRight(true);
                break;
            case ARROW_RIGHT:
                moveLeftRight(false);
                break;
            default:
                return null;
        }
        if (placed) {
            placed = false;
            clearStatusMessage();
        }
        return null;
    }

}

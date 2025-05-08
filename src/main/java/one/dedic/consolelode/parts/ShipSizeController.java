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
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.WrapBehaviour;
import java.io.IOException;
import one.dedic.consolelode.GameState;
import one.dedic.consolelode.model.BoardState;

/**
 *
 * @author sdedic
 */
public class ShipSizeController extends SetupController {
    private int shipSize;

    public BoardState getBoardState() {
        return boardState;
    }

    public int getShipSize() {
        return shipSize;
    }

    @Override
    public void initialize() {
        super.initialize();
        initShipSize();
    }

    @Override
    protected void printLayout() {
        super.printLayout();
        printShipSizeList();
    }

    @Override
    protected NextState testValid() {
        if (shipSize >= 0) {
            return null;
        } else {
            return NextState.create(Result.OK, GameState.CONFIRM);
        }
    }
    
    /**
     *
     */
    @Override
    protected void printDescription() {
        TextGraphicsWriter writer = new TextGraphicsWriter(g);
        writer.setWrapBehaviour(WrapBehaviour.WORD);
        writer.putString("Vyber velikost lodi k umisteni. V seznamu je uvedeny pro kazdou velikost lodi pocet zbyvajicich.\n\n" +
                         "Pro vyber stiskni ");
        writer.enableModifiers(SGR.BOLD);
        writer.putString("cislo velikosti lode");
        writer.disableModifiers(SGR.BOLD);
        writer.putString(" a potvrd ");
        writer.enableModifiers(SGR.BOLD);
        writer.putString("ENTER");
        writer.disableModifiers(SGR.BOLD);
    }
    
    private static final int SHIP_LIST_HEADER = 4;
    private static final int SHIP_LIST_ROW = SHIP_LIST_HEADER + 1;
    
    private static final int SIZE_COL = 4;
    private static final int COUNT_COL = 14;
    private static final int SELECT_COL_ABS = SHIP_LIST_COL + 18;
    
    void initShipSize() {
        for (int sz = options.getMinSize(); sz <= options.getMaxSize(); sz++) {
            int missing = boardState.getMissingCount(sz);
            if (missing > 0) {
                shipSize = sz;
                return;
            }
        }
        shipSize = -1;
    }
    
    @Override
    protected void printPrompt() {
        TerminalPosition pos = new TerminalPosition(SELECT_COL_ABS, SHIP_LIST_ROW - 1 + shipSize);
        screen.setCursorPosition(pos);
        screen.setCharacter(pos, new TextCharacter('?'));
    }
    
    void clearPrompt() {
        TerminalPosition pos = new TerminalPosition(SELECT_COL_ABS, SHIP_LIST_ROW - 1 + shipSize);
        screen.setCharacter(pos, new TextCharacter(' '));
    }
    

    void printShipSizeList() {
        TextGraphics g2 = graphics.newTextGraphics(new TerminalPosition(SHIP_LIST_COL, SHIP_LIST_HEADER), new TerminalSize(60, 15));
        g2.fill(' ');
        
        TextGraphicsWriter writer2 = new TextGraphicsWriter(g2);
        writer2.enableModifiers(SGR.BOLD).putString("Velikost   Zbyva");
        
        for (int sz = options.getMinSize(); sz <= options.getMaxSize(); sz++) {
            writer2.setCursorPosition(new TerminalPosition(SIZE_COL, sz));
            writer2.putString("" + sz);
            writer2.setCursorPosition(new TerminalPosition(COUNT_COL, sz));
            writer2.putString("" + boardState.getMissingCount(sz));
        }
    }
    
    int goUp() {
        int ns = shipSize;
        
        do {
            ns = ns - 1;
            if (ns < 1) {
                ns = options.getMaxSize();
            }
        } while (boardState.getMissingCount(ns) < 1);
        return ns;
    }

    int goDown() {
        int ns = shipSize;
        
        do {
            ns = ns + 1;
            if (ns > options.getMaxSize()) {
                ns = options.getMinSize();
            }
        } while (boardState.getMissingCount(ns) < 1);
        return ns;
    }
    
    public int goTo(int sz) {
        if (boardState.getMissingCount(sz) > 0) {
            return sz;
        } else {
            return shipSize;
        }
    }

    @Override
    protected NextState handle(KeyStroke ks) {
        int ns = -1;
        switch (ks.getKeyType()) {
            case ARROW_DOWN:
                ns = goDown();
                break;
            case ARROW_UP:
                ns = goUp();
                break;
            case CHARACTER:
                char c = java.lang.Character.toLowerCase(ks.getCharacter());
                if (c >= '1' && c < '1' + options.getMaxSize()) {
                    ns = goTo(c - '0');
                } else if (c == 's') {
                    return NextState.create(Result.OK, GameState.SAVE);
                } else if (c == 'l') {
                    
                }
                break;
        }
        if (ns != -1) {
            clearPrompt();
            shipSize = ns;
        }
        return null;
    }

    @Override
    protected GameState handleConfirmed(GameState state) {
        boardState.setCurrentSize(shipSize);
        return state;
    }
}

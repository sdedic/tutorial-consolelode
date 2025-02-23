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
import static com.googlecode.lanterna.input.KeyType.ArrowDown;
import static com.googlecode.lanterna.input.KeyType.ArrowUp;
import static com.googlecode.lanterna.input.KeyType.Character;
import static com.googlecode.lanterna.input.KeyType.Enter;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.WrapBehaviour;
import java.io.IOException;
import one.dedic.consolelode.Controller;
import one.dedic.consolelode.GameOptions;
import one.dedic.consolelode.model.BoardState;

/**
 *
 * @author sdedic
 */
public class ShipSizeController implements Controller {
    private final GameOptions options;
    private final Screen screen;
    private final TextGraphics graphics;
    private BoardState boardState;
    private BoardPrinter boardPrinter;
    
    TextGraphics g;
    
    private int shipSize;

    public ShipSizeController(GameOptions options, Screen screen) {
        this.options = options;
        this.screen = screen;
        graphics = screen.newTextGraphics();
        boardPrinter = new BoardPrinter(options, graphics.
                newTextGraphics(new TerminalPosition(0, 0), new TerminalSize(24, 14)));
        g = graphics.newTextGraphics(new TerminalPosition(SHIP_LIST_COL, 0), new TerminalSize(60, 15));
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
        boardPrinter.setBoardState(boardState);
    }
    
    public int getShipSize() {
        return shipSize;
    }

    @Override
    public Result execute() throws IOException {
        initShipSize();
        printDescription();
        boardPrinter.printBoardOutline();
        printShipSizeList();
        
        Result r = loop();
        g.fill(' ');
        screen.setCursorPosition(null);
        screen.refresh();
        return r;
    }
    
    void printDescription() {
        TextGraphics g = graphics.newTextGraphics(new TerminalPosition(SHIP_LIST_COL, 0), new TerminalSize(60, 15));
        g.fill(' ');
        
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
    
    private static final int SHIP_LIST_COL = 25;
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
        throw new IllegalStateException("No ships");
    }
    
    void printPrompt() {
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
    
    Result loop() throws IOException {
        while (true) {
            printPrompt();
            screen.refresh();
            KeyStroke ks = screen.readInput();

            int ns = -1;
            switch (ks.getKeyType()) {
                case ArrowDown:
                    ns = goDown();
                    break;
                case ArrowUp:
                    ns = goUp();
                    break;
                case Character:
                    char c = ks.getCharacter();
                    if (c >= '1' && c < '1' + options.getMaxSize()) {
                        ns = goTo(c - '0');
                    }
                    break;
                case Enter:
                    return Result.OK;
                case Escape:
                    return Result.CANCEL;
            }
            if (ns != -1) {
                clearPrompt();
                shipSize = ns;
            }
        }
    }
    
    static void vyberVelikostiLodi(Screen scn) throws IOException {
        TextGraphics tg = scn.newTextGraphics();
        TextGraphics g2 = tg.newTextGraphics(new TerminalPosition(25, 4), new TerminalSize(60, 10));
        TextGraphicsWriter writer2 = new TextGraphicsWriter(g2);
        writer2.enableModifiers(SGR.BOLD).putString("Velikost   Zbyva");
        
        writer2.setCursorPosition(new TerminalPosition(4, 1));
        writer2.putString("1");
        writer2.setCursorPosition(new TerminalPosition(13, 1));
        writer2.putString("4");

        writer2.setCursorPosition(new TerminalPosition(4, 2));
        writer2.putString("2");
        writer2.setCursorPosition(new TerminalPosition(13, 2));
        writer2.putString("3");

        writer2.setCursorPosition(new TerminalPosition(4, 3));
        writer2.putString("3");
        writer2.setCursorPosition(new TerminalPosition(13, 3));
        writer2.putString("2");

        writer2.setCursorPosition(new TerminalPosition(4, 4));
        writer2.putString("4");
        writer2.setCursorPosition(new TerminalPosition(13, 4));
        writer2.putString("1");

        writer2.setCursorPosition(new TerminalPosition(4, 5));
        writer2.putString("4");
        writer2.setCursorPosition(new TerminalPosition(13, 5));
        writer2.putString("1");
        
        scn.setCursorPosition(new TerminalPosition(25 + 18, 5));
        scn.setCharacter(25 + 18, 5, new TextCharacter('?'));
        scn.refresh();
        
        int size = 1;
        L: while (true) {
            scn.setCharacter(25 + 18, 5 + size - 1, new TextCharacter(' '));
            KeyStroke ks = scn.readInput();
            
            int ns = -1;
            switch (ks.getKeyType()) {
                case ArrowDown:
                    ns = Math.min(5, size + 1);
                    break;
                case ArrowUp:
                    ns = Math.max(1, size - 1);
                    break;
                case Enter:
                    break L;
                case Character:
                    switch (ks.getCharacter()) {
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            ns = ks.getCharacter() - '0';
                            break;
                        case '\n':
                            break L;
                    }
            }
            if (ns != -1) {
                size = ns;
                scn.setCursorPosition(new TerminalPosition(25 + 18, 5 + (size - 1)));
                scn.setCharacter(25 + 18, 5 + (size - 1), new TextCharacter('?'));
                scn.refresh();
            }
        }
    }
}

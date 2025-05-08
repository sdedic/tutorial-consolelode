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
import one.dedic.consolelode.GameOptions;
import one.dedic.consolelode.data.Cell;
import one.dedic.consolelode.model.BoardState;

/**
 *
 * @author sdedic
 */
public class BoardPrinter {
    private final TextGraphics graphics;
    private final GameOptions options;
    private BoardState boardState;
    private TextGraphics boardG;
    private TextGraphics outlineG;

    public BoardPrinter(GameOptions options, TextGraphics graphics) {
        this.graphics = graphics;
        this.options = options;
        
        TerminalPosition outlineTopLeft = new TerminalPosition(2, 1);
        TerminalSize interiorSize = new TerminalSize(options.getBoardSize() * 2, options.getBoardSize() * 2);
        outlineG = graphics.newTextGraphics(outlineTopLeft, interiorSize.withRelative(2, 2));
        
        this.boardG = 
                new DoublePrintingTextGraphics(
                graphics.newTextGraphics(outlineTopLeft.withRelative(1, 1), interiorSize)
        );
    }

    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }
    
    public void printBoardLabels() {
        // Get TextGraphics for drawing
        for (int i = 1; i <= options.getBoardSize(); i++) {
            if (boardState.isTargetting() && (i - 1) == boardState.getCursorY()) {
                graphics.setForegroundColor(TextColor.ANSI.CYAN);
            }
            graphics.putString(0, i + 1, "" + i, SGR.BOLD);
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
        }
        for (int i = 1; i <= options.getBoardSize(); i++) {
            if (boardState.isTargetting() && (i - 1) == boardState.getCursorX()) {
                graphics.setForegroundColor(TextColor.ANSI.CYAN);
            }
            graphics.putString(i * 2 + 1 , 0, String.valueOf((char)('A' - 1 + i)), SGR.BOLD);
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
        }
    }
    
    public void printBoardOutline() {
        printBoardLabels();
        outlineG.setCharacter(0, 0, Symbols.SINGLE_LINE_TOP_LEFT_CORNER);
        outlineG.drawLine(1, 0, options.getBoardSize() * 2, 0, Symbols.SINGLE_LINE_HORIZONTAL);
        outlineG.setCharacter(1 + options.getBoardSize() * 2, 0, Symbols.SINGLE_LINE_TOP_RIGHT_CORNER);
        outlineG.drawLine(0, 1, 0, options.getBoardSize(), Symbols.SINGLE_LINE_VERTICAL);
        outlineG.drawLine(1 + options.getBoardSize() * 2, 1, 1 + options.getBoardSize() * 2, options.getBoardSize(), Symbols.SINGLE_LINE_VERTICAL);
        outlineG.drawLine(1, 1 + options.getBoardSize(), options.getBoardSize() * 2, 1 + options.getBoardSize(), Symbols.SINGLE_LINE_HORIZONTAL);
        
        outlineG.setCharacter(0, options.getBoardSize() + 1, Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        outlineG.setCharacter(options.getBoardSize() * 2 + 1, options.getBoardSize() + 1, Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
    }
    
    public void printBoardContents() {
        for (int y = 0; y < options.getBoardSize(); y++) {
            for (int x = 0; x < options.getBoardSize(); x++) {
                char toPrint = ' ';
                Cell c = boardState.getCell(x, y);
                TextColor foreColor = TextColor.ANSI.WHITE;
                TextColor backColor = TextColor.ANSI.BLACK;
                
                switch (c) {
                    case UNKNOWN:
                    case EMPTY:
                    case WATER:
                    case RESERVED:
                        break;
                    case MISSED:
                        toPrint = Symbols.BULLET;
                        break;
                    case SHIP_CURRENT:
                    case SHIP:
                        toPrint = Symbols.BLOCK_MIDDLE;
                        break;
                    case HIT:
                        toPrint = Symbols.BLOCK_SOLID;
                        foreColor = TextColor.ANSI.GREEN;
                        break;
                    case DESTROYED:
                        foreColor = TextColor.ANSI.RED;
                        toPrint = Symbols.BLOCK_SOLID;
                        break;
                    default:
                        throw new AssertionError(c.name());
                    
                }
                
                if (c == Cell.SHIP_CURRENT) {
                    switch (boardState.getShipState()) {
                        case OK:
                            foreColor = TextColor.ANSI.YELLOW_BRIGHT;
                            break;
                        case Overlapping:
                        case Touching:
                            foreColor = TextColor.ANSI.MAGENTA_BRIGHT;
                            break;
                        case OutOfGrid:
                            foreColor = TextColor.ANSI.RED_BRIGHT;
                            break;
                        default:
                            throw new AssertionError(boardState.getShipState().name());
                    }
                } else {
                    if (boardState.isTargetting()) {
                        if (y == boardState.getCursorY() || x == boardState.getCursorX()) {
                            backColor = TextColor.ANSI.CYAN;
                        }
                    }
                }
                
                TextColor saveBack = boardG.getBackgroundColor();
                TextColor saveFore = boardG.getForegroundColor();
                
                boardG.setBackgroundColor(backColor);
                boardG.setForegroundColor(foreColor);
                
                boardG.setCharacter(x, y, toPrint);

                boardG.setBackgroundColor(saveBack);
                boardG.setForegroundColor(saveFore);
            }
        }
    }
}

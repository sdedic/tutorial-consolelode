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
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.input.KeyStroke;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import one.dedic.consolelode.GameState;
import one.dedic.consolelode.data.Placement;
import static one.dedic.consolelode.model.BoardState.ShipState.OK;
import static one.dedic.consolelode.model.BoardState.ShipState.OutOfGrid;
import static one.dedic.consolelode.model.BoardState.ShipState.Overlapping;
import static one.dedic.consolelode.model.BoardState.ShipState.Touching;

/**
 *
 * @author sdedic
 */
public class CursorDelegate extends SetupController {
    private int rowNumber = -1;
    private String statusMessage;

    private BoardPrinter boardPrinter;
    private Placement placement;
    private StatusPrinter statusPrinter;

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public BoardPrinter getBoardPrinter() {
        return boardPrinter;
    }

    public void setBoardPrinter(BoardPrinter boardPrinter) {
        this.boardPrinter = boardPrinter;
    }
    
    private static final int STATUS_COORDS_COL = 17;
    
    void printStatusTemplate() {
        TextGraphicsWriter writer = new TextGraphicsWriter(graphics);
        writer.setCursorPosition(new TerminalPosition(0, 0));
        // Ovlivnuje STATUS_COORDS_COL, ten musi byt za ": ".
        writer.putString("Vybrany sloupec: - ");
        writer.setCursorPosition(new TerminalPosition(0, 1));
        writer.putString("Vybrany radek  : - ");
    }
    
    void printStatus() {
        if (boardState.getCursorX() != -1) {
            graphics.putString(0, STATUS_COORDS_COL, "" + (char)('A' + boardState.getCursorX()));
        } else {
            graphics.putString(0, STATUS_COORDS_COL, "-");
        }
        
        if (rowNumber != -1) {
            String text = (rowNumber + 1) + "?";
            TerminalPosition pos = new TerminalPosition(0, 0 + 1);
            graphics.putString(pos, text);
        } else {
            if (boardState.getCursorY() != -1) {
                graphics.putString(0, STATUS_COORDS_COL, (1 + boardState.getCursorY()) + " ");
            } else {
                graphics.putString(0, STATUS_COORDS_COL, "- ");
            }
        }
    }

    void printStatusMessage(String s, boolean error) {
        statusPrinter.printStatusMessage(s, error);
    }
    
    void printStatusMessage(String s) {
        printStatusMessage(s, true);
    }
    
    void updateCoordinates() {
        boardPrinter.printBoardContents();
        boardPrinter.printBoardLabels();
        printStatus();
    }
    
    void processCharacters(char c) {
        if (c >= 'A' && c <= 'Z') {
            boardState.setCursorX(c - 'A');
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
            statusPrinter.printStatusMessage("Cislo radku je prilis velke.");
        } else {
            statusPrinter.clearStatusMessage();
        }
        rowNumber = newRow;
        printStatus();
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

    NextState cancelStageOrNumber() {
        if (rowNumber >= 0) {
            rowNumber = -1;
            updateCoordinates();
            return CONTINUE;
        }
        if (boardState.isTargetting()) {
            boardState.resetCursor();
            updateCoordinates();
            return CONTINUE;
        } 
        return null;
    }
    
    NextState confirmNumberOrShip() {
        if (rowNumber >= 0) {
            if (rowNumber > options.getBoardSize()) {
                rowNumber = -1;
                boardState.setCursorY(-1);
                updateCoordinates();
            } else {
                boardState.setCursorY(rowNumber);
                rowNumber = -1;
                statusPrinter.clearStatusMessage();
                updateCoordinates();
            }
            return CONTINUE;
        } else {
            return null;
        }
    }
    
    @Override
    protected NextState handle(KeyStroke ks) {
        if (rowNumber > -1) {
            switch (ks.getKeyType()) {
                case CHARACTER:
                    processNumeric(ks.getCharacter());
                    return null;
                case ESCAPE:
                    return cancelStageOrNumber();
                    
                case ENTER:
                    return confirmNumberOrShip();
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
                return confirmNumberOrShip();
                
            case ESCAPE:
                return cancelStageOrNumber();
                
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
        return null;
    }

}

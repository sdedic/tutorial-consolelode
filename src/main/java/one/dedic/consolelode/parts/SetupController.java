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
import com.googlecode.lanterna.graphics.TextGraphics;
import one.dedic.consolelode.Controller;
import one.dedic.consolelode.DelegatingController;
import one.dedic.consolelode.model.BoardState;

/**
 *
 * @author sdedic
 */
public abstract class SetupController extends DelegatingController {
    protected static final int SHIP_LIST_COL = 25;

    protected BoardPrinter boardPrinter;
    protected BoardState boardState;
    
    /**
     * Plocha pro uzivatelskou interakci
     */
    protected TextGraphics g;
    
    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    @Override
    protected void setupDelegate(Controller delegate) {
        if (delegate instanceof SetupController sc) {
            sc.setBoardState(boardState);
        }
        super.setupDelegate(delegate);
    }
    
    protected void printLayout() {
        boardPrinter.printBoardOutline();
        super.printLayout();
    }

    @Override
    public void setup() {
        g = graphics.newTextGraphics(new TerminalPosition(SHIP_LIST_COL, 0), new TerminalSize(graphics.getSize().getColumns() - SHIP_LIST_COL, 20));        
        boardPrinter = new BoardPrinter(options, graphics);
        boardPrinter.setBoardState(boardState);
        super.setup();
    }

    @Override
    protected void clear() {
        super.clear();
        g.fill(' ');
    }
}

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
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.input.KeyStroke;
import java.io.IOException;
import one.dedic.consolelode.GameState;
import one.dedic.consolelode.data.ItemTemplate;
import one.dedic.consolelode.data.Placement;

/**
 *
 * @author sdedic
 */
public class EditShipController extends SetupController {
    private ItemTemplate selectedItem;
    private Placement placement;

    /**
     * Instrukce, nahore vedle hraci plochys
     */
    private TextGraphics instructionsG;
    
    private CursorDelegate cursorDelegate;
    
    private static final int SHIP_LIST_COL = 25;
    private static final int STATUS_ROW = 3;

    public EditShipController() {
        cursorDelegate = new CursorDelegate();
        addDelegate(cursorDelegate);
    }

    @Override
    public void setup() {
        super.setup();
        TerminalPosition topLeft = new TerminalPosition(SHIP_LIST_COL, 0);
        instructionsG = graphics.newTextGraphics(topLeft, 
                graphics.getSize().withRelativeColumns(-SHIP_LIST_COL).withRows(6));
        setStatusTextGraphics(graphics.newTextGraphics(topLeft.withRelativeRow(instructionsG.getSize().getRows() + 2),
                new TerminalSize(graphics.getSize().getColumns() - SHIP_LIST_COL, 5)));
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

    void printInstructions() {
        TextGraphicsWriter writer = new TextGraphicsWriter(instructionsG);
        writer.putString("Vyber lod. Pouzij sipky, nebo souradnice pismeno-cislo (ENTER za cislem). ");
    }

    @Override
    public void initialize() {
        super.initialize();
        placement = null;
    }

    @Override
    protected NextState handle(KeyStroke ks) {
        NextState s = super.handle(ks);
        if (s != null) {
            return s;
        }
        switch (ks.getKeyType()) {
            case ENTER:
                return findPlacement();
                
            case ESCAPE:
                placement = null;
                return NextState.create(Result.OK, previousState);
        }
        return null;
    }
    
    NextState findPlacement() {
        int x = boardState.getCursorX();
        int y = boardState.getCursorY();
        if (x < 0 || y < 0) {
            return CONTINUE;
        }
        if (boardState.getCell(x, y).isShip()) {
            placement = boardState.findItem(x, y);
            if (placement != null) {
                boardState.selectCurrent(placement);
                return NextState.create(Result.OK, GameState.POSITION);
            }
        }
        return CONTINUE;
    }
}

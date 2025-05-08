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
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.input.KeyStroke;
import io.GameStateLoader;
import java.io.IOException;
import one.dedic.consolelode.Controller;
import one.dedic.consolelode.GameOptions;
import one.dedic.consolelode.model.BoardState;
import one.dedic.consolelode.model.GameMemento;

/**
 *
 * @author sdedic
 */
public class SaveGameController extends Controller {
    protected static final int SHIP_LIST_COL = 25;

    private BoardState boardState;
    private TextGraphics status;
    
    private IOException error;

    public BoardState getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }
    
    

    @Override
    public void setup() {
        super.setup(); 
        TerminalPosition topLeft = new TerminalPosition(SHIP_LIST_COL, 0);
        status = graphics.newTextGraphics(topLeft, 
                new TerminalSize(30, 2));
    }

    @Override
    public void initialize() {
        super.initialize();
        error = null;
        GameMemento memento = new GameMemento();
        memento.setGameOptions(options);
        memento.setPlayerBoard(boardState.getBoard());
        memento.setPlayerItems(boardState.getPlacedItems());
        try {
            GameStateLoader saver = new GameStateLoader(memento);
            saver.save();
        } catch (IOException ex) {
            error = ex;
        }
    }

    @Override
    protected void printDescription() {
        TextGraphicsWriter writer = new TextGraphicsWriter(status);
        writer.enableModifiers(SGR.BOLD);
        if (error == null) {
            writer.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            writer.putString("The game was saved successfully");
        } else {
            writer.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            writer.putString(error.getLocalizedMessage());
        }
        writer.clearModifiers();
        writer.setForegroundColor(defaultTextColor);
    }
    
    @Override
    protected NextState handle(KeyStroke ks) {
        switch (ks.getKeyType()) {
            case ENTER:
            case ESCAPE:
                return NextState.create(Result.OK, previousState);
        }
        return null;
    }
    
    
}

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
package one.dedic.consolelode;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import one.dedic.consolelode.Controller.Result;
import one.dedic.consolelode.data.GameOptionsLoader;
import one.dedic.consolelode.data.ItemLibrary;
import one.dedic.consolelode.data.PropertyTemplateLoader;
import one.dedic.consolelode.model.BoardState;
import one.dedic.consolelode.parts.PositionShipController;
import one.dedic.consolelode.parts.ShipShapeController;
import one.dedic.consolelode.parts.ShipSizeController;

/**
 *
 * @author sdedic
 */
public class ConsoleLode2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        GameOptions opts = new GameOptions();
        ItemLibrary lbr = new ItemLibrary();
        opts.setLibrary(lbr);
        
        PropertyTemplateLoader ldr = new PropertyTemplateLoader(lbr);
        GameOptionsLoader gldr = new GameOptionsLoader(opts);
        ldr.loadResource("one/dedic/consolelode/data/ships.properties");
        gldr.loadResource("one/dedic/consolelode/data/game.properties");
        
        BoardState state = new BoardState(opts.getBoardSize(), opts);
        
        Terminal t = new DefaultTerminalFactory().createTerminal();
        Screen scn = new TerminalScreen(t);
        
        scn.startScreen();
        
        GameState gs = GameState.SIZE_SELECT;
        ShipSizeController sizeCtrl = new ShipSizeController(opts, scn);
        ShipShapeController shapeCtrl = new ShipShapeController(opts, scn);
        PositionShipController positionCtrl = new PositionShipController(opts, scn);
        
        sizeCtrl.setBoardState(state);
        positionCtrl.setBoardState(state);
        
        Result r;
        while (gs  != GameState.EXIT) {
            switch (gs) {
                case SIZE_SELECT: {

                    
                    r = sizeCtrl.execute();
                    if (r == Result.OK) {
                        shapeCtrl.setShipSize(sizeCtrl.getShipSize());
                        gs = GameState.SHAPE_SELECT;
                    }
                    break;
                }
                case SHAPE_SELECT:
                    r = shapeCtrl.execute();
                    if (r == Result.OK) {
                        positionCtrl.setSelectedItem(shapeCtrl.getSelectedShip());
                        gs = GameState.POSITION;
                    } else if (r == Result.CANCEL) {
                        gs = GameState.SIZE_SELECT;
                    }
                    break;
                case POSITION: 
                    r = positionCtrl.execute();
                    if (r == Result.OK) {
                        
                    } else if (r == Result.CANCEL) {
                        gs = GameState.SHAPE_SELECT;
                    }
                    break;
            }
        }
        scn.stopScreen();
    }
    
}

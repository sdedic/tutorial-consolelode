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

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.virtual.VirtualTerminal;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import one.dedic.consolelode.Controller.NextState;
import one.dedic.consolelode.Controller.Result;
import io.GameOptionsLoader;
import static one.dedic.consolelode.Controller.Result.CANCEL;
import one.dedic.consolelode.data.ItemLibrary;
import one.dedic.consolelode.data.PropertyTemplateLoader;
import one.dedic.consolelode.model.BoardState;
import one.dedic.consolelode.parts.PositionShipController;
import one.dedic.consolelode.parts.SaveGameController;
import one.dedic.consolelode.parts.SetupController;
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
        gldr.loadOptions("one/dedic/consolelode/data/game.properties");
        
        BoardState state = new BoardState(opts.getBoardSize(), opts);
        
        Terminal t = new DefaultTerminalFactory().
                setInitialTerminalSize(new TerminalSize(120, 60)).
                createTerminal();
        if (t instanceof VirtualTerminal vt) {
            vt.setTerminalSize(new TerminalSize(120, 60));
        }
        Screen scn = new TerminalScreen(t);
        
        scn.startScreen();
        
        SaveGameController save = new SaveGameController();
        save.setBoardState(state);
        
        GameState gs = GameState.SIZE_SELECT;
        Map<GameState, SetupController> setupControllers = Map.of(
                GameState.SIZE_SELECT, new ShipSizeController(),
                GameState.SHAPE_SELECT, new ShipShapeController(),
                GameState.POSITION, new PositionShipController()
        );
        setupControllers.values().forEach(c -> c.setBoardState(state));
        
        Map<GameState, Controller> gameMachine = new HashMap<>();
        gameMachine.put(GameState.SAVE, save);
        gameMachine.putAll(setupControllers);
        
        gameMachine.values().forEach(c -> {
            c.setScreen(scn);
            c.setOptions(opts);
            c.setGraphics(scn.newTextGraphics());
            c.setup();
        });
        
        Result r = Result.OK;
        GameState prevState = gs;
        while (gs  != GameState.EXIT) {
            Controller ctrl = gameMachine.get(gs);
            ctrl.setPreviousState(prevState);
            if (ctrl != null) {
                NextState next = ctrl.interact(r);
                if (next == null) {
                    next = NextState.create(Result.OK, null);
                }
                r = next.result;
                if (next.state != null) {
                    gs = next.state;
                } else {
                    switch (r) {
                        case OK:
                            gs = gs.getNext();
                            break;
                        case BACK:
                        case CANCEL:
                            gs = gs.getPrev();
                            break;
                    }
                }
            }
        }
        scn.stopScreen();
    }
    
}

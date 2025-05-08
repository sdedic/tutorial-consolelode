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

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import one.dedic.consolelode.parts.StatusPrinter;

/**
 *
 * @author sdedic
 */
public abstract class Controller {
    /**
     * Nastaveni hry
     */
    protected GameOptions options;
    
    /**
     * Obrazovka
     */
    protected Screen screen;
    
    /**
     * Graphics pro celou obrazovku
     */
    protected TextGraphics graphics;
    
    /**
     * Predchozi stav, pokud by se Controller chtel vratit, napr. pro Save apod.
     */
    protected GameState previousState;
    
    /**
     * Puvodni barva textu
     */
    protected TextColor defaultTextColor;
    
    /**
     * Puvodni barva pozadi
     */
    protected TextColor defaultBackColor;
    
    protected TextGraphics statusTextGraphics;
    
    protected StatusPrinter statusPrinter;
    
    /**
     * Specialni stav 'pokracuj', bude pokracovat ve smycce loop.
     */
    protected static final NextState CONTINUE = NextState.create(null, null);
    
    /**
     * Vysledek vyhodnoceni klavesy. Budto prejde na jinou obrazovku / controller,
     * kdyz je urceno 'state', nebo skonci s OK (prehod dale), ci ESC (prechod zpet).
     */
    public static class NextState {
        public final Result result;
        public final GameState state;

        public NextState(Result result, GameState next) {
            this.result = result;
            this.state = next;
        }
        
        public static NextState result(Result r) {
            if (r == null) {
                return null;
            }
            return create(r, null);
        }
        
        public static NextState create(Result r, GameState s) {
            return new NextState(r, s);
        }
    }
    
    /**
     * Vysledek zpracovani, pouziva se OK a CANCEL.
     */
    public enum Result {
        OK,
        CANCEL,
        BACK,
        EDIT,
    }

    public Controller() {
        statusPrinter = new StatusPrinter();
    }

    public TextGraphics getStatusTextGraphics() {
        return statusTextGraphics;
    }

    public void setStatusTextGraphics(TextGraphics statusTextGraphics) {
        this.statusTextGraphics = statusTextGraphics;
        statusPrinter.setStatusG(statusTextGraphics);
    }

    public GameState getPreviousState() {
        return previousState;
    }

    public void setPreviousState(GameState previousState) {
        this.previousState = previousState;
    }

    public void setOptions(GameOptions options) {
        this.options = options;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public void setGraphics(TextGraphics graphics) {
        this.graphics = graphics;
        defaultBackColor = graphics.getBackgroundColor();
        defaultTextColor = graphics.getForegroundColor();
    }

    /**
     * Provede interakci s hracem, vrati se az kvuli zmene controlleru a stavu hry.
     * Pokud se do Controlleru dostane pres "OK" vysledek, tedy ne cancel/back, 
     * znovu jej cely inicializuje a maze stare hodnoty
     * 
     * @param enterWith zpusob prechodu
     * @return vysledek interakce.
     * @throws IOException 
     */
    public NextState interact(Result enterWith) throws IOException {
        if (graphics == null) {
            graphics = screen.newTextGraphics();
        }
        if (enterWith == Result.OK) {
            initialize();
        }
        NextState st = testValid();
        if (st != null) {
            return st;
        }
        printLayout();
        printDescription();
        st = loop();
        if (st.result == Result.OK) {
            GameState ns = handleConfirmed(st.state);
            if (ns != null) {
                st = NextState.create(st.result, ns);
            }
        }
        clear();
        screen.refresh();
        return st;
    }

    /**
     * Ve smycce vola prompt a handle. Vrati-li handle null, resi klavesy ESC a ENTER.
     * @return
     * @throws IOException 
     */
    protected NextState loop() throws IOException {
        NextState st = null;
        do {
            printPrompt();
            screen.refresh();
            KeyStroke ks = screen.readInput();
            st = handle(ks);
            if (st == null) {
                switch (ks.getKeyType()) {
                    case ESCAPE:
                        return NextState.result(Result.BACK);
                    case ENTER:
                        return NextState.result(Result.OK);
                }
            }
        } while (st == null || st == CONTINUE);
        return st;
    }
    
    /**
     * Vola se po potvrzeni ENTERem
     * @param state
     * @return 
     */
    protected GameState handleConfirmed(GameState state) {
        return state;
    }
    
    /**
     * Nakresli layout/obsah pro interakci, jednou pred zacatkem interakce
     */
    protected void printLayout() {
        
    }
    
    /**
     * Vola se pred kazdym stiskem klavesy
     */
    protected void printPrompt() {
    }
    
    /**
     * Nakresli popisek, jednou pred zacatkem interakce
     */
    protected void printDescription() {
        
    }
    
    public void initialize() {
        // no op
    }
    
    public void setup() {
        // no op
    }
    
    protected void clear() {
        screen.setCursorPosition(null);
    }
    
    protected NextState testValid() {
        return null;
    }
    
    public Result execute() throws IOException { return null; }
    
    protected NextState handle(KeyStroke ks) {
        return NextState.create(Result.OK, GameState.EXIT);
    }
    
}

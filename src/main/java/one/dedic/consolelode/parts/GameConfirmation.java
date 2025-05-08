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
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;
import one.dedic.consolelode.Controller;
import one.dedic.consolelode.GameOptions;

/**
 *
 * @author sdedic
 */
public class GameConfirmation extends SetupController {
    private static final int SHIP_LIST_COL = 25;
    private static final int SHIP_LIST_ROW = 2;
    
    public GameConfirmation() {
        g = graphics.newTextGraphics(new TerminalPosition(SHIP_LIST_COL, 0), new TerminalSize(
                graphics.getSize().getColumns() - SHIP_LIST_COL, 30));
    }

    @Override
    protected void printDescription() {
        TextGraphicsWriter writer = new TextGraphicsWriter(g);
        writer.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        writer.enableModifiers(SGR.BOLD);
        
        writer.putString("Vsechny lode jsou umistene ! Hra muze zacit\n\n");
        writer.putString("Stiskni (U)lozit hru, (O)opravit nebo odstranit lod (ESC), (Z)acit (ENTER)");
    }

    @Override
    protected NextState handle(KeyStroke ks) {
        if (ks.getKeyType() == KeyType.CHARACTER) {
            switch (Character.toLowerCase(ks.getCharacter())) {
                case 'u':
                case 'o':
                
            }
        }
        return null;
    }
}

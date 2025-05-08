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

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;

/**
 * Do zadane oblasti tiskne 'statusovou' zpravu.
 * <ul>
 * <li>error cervene
 * <li>potvrzovaci zpravu zlute.
 * </ul>
 * @author sdedic
 */
public class StatusPrinter {
    private TextGraphics statusG;
    private String statusMessage;
    private boolean statusError;
    
    public StatusPrinter() {
    }

    public TextGraphics getStatusG() {
        return statusG;
    }

    public void setStatusG(TextGraphics statusG) {
        this.statusG = statusG;
        if (statusG != null) {
            printStatus();
        }
    }
    
    void printStatusMessage(String s, boolean error) {
        this.statusMessage = s;
        this.statusError = error;
        printStatus();
    }
    
    public void printStatusMessage(String s) {
        printStatusMessage(s, true);
    }

    public void printStatus() {
        if (statusG == null) {
            return;
        }
        if (statusMessage != null) {
            TextGraphicsWriter writer = new TextGraphicsWriter(statusG);
            TextColor fore = writer.getForegroundColor();
            if (statusError) {
                writer.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            } else {
                writer.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            }
            writer.putString(statusMessage);
            writer.setForegroundColor(fore);
        }
    }

    public void clearStatusMessage() {
        statusMessage = null;
        if (statusG != null) {
            statusG.fill(' ');
        }
    }
}

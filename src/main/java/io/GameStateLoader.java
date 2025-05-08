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
package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import one.dedic.consolelode.data.Cell;
import one.dedic.consolelode.data.ItemTemplate;
import one.dedic.consolelode.data.Placement;
import one.dedic.consolelode.model.GameMemento;

/**
 *
 * @author sdedic
 */
public class GameStateLoader {
    private GameMemento memento;
    private Properties props = new Properties();

    public GameStateLoader(GameMemento memento) {
        this.memento = memento;
    }
    
    public void load() throws IOException {
    }
    
    public void save() throws IOException {
        saveBoard("player", memento.getPlayerBoard());
        saveBoard("computer", memento.getComputerBoard());
        saveItems("player", memento.getPlayerItems());
        saveItems("computer", memento.getComputerItems());
        props.setProperty("gameStarted", Boolean.toString(memento.isGameStarted()));
        
        GameOptionsLoader optLoader = new GameOptionsLoader(memento.getGameOptions());
        optLoader.setProps(props);
        optLoader.saveOptions();
        
        try (OutputStream os = Files.newOutputStream(Paths.get("savedgame.properties"))) {
            props.store(os, null);
        }
    }
    
    void saveItems(String prefix, List<Placement> items) {
        if (items == null) {
            return;
        }
        int cnt = 1;
        for (Placement item : items) {
            String p = String.format("%s.%02d.", prefix, cnt);

            ItemTemplate template = item.getTemplate();
            props.setProperty(p + "id", template.getId());
            props.setProperty(p + "name", item.getName() == null ? "" : item.getName());
            props.setProperty(p + "x", Integer.toString(item.getX()));
            props.setProperty(p + "y", Integer.toString(item.getY()));
            props.setProperty(p + "rotation", item.getRotation().name());
            props.setProperty(p + "mirrorX", Boolean.toString(item.isMirrorX()));
            props.setProperty(p + "mirrorY", Boolean.toString(item.isMirrorY()));
        }
    }
    
    void saveBoard(String prefix, Cell[][] board) {
        if (board == null) {
            return;
        }
        for (int i = 0; i < board.length; i++) {
            String line = Arrays.asList(board[i]).stream().sequential()
                    .map(c -> c == null ? Cell.WATER : c)
                    .map(Cell::name)
                    .collect(Collectors.joining(","));
            String p = String.format("%s.%02d.", prefix, i + 1);
            props.put(p, line);
        }
    }
}

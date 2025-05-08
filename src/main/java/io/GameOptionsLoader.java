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
import java.util.Properties;
import one.dedic.consolelode.GameOptions;

/**
 *
 * @author sdedic
 */
public class GameOptionsLoader {
    private GameOptions options;
    private Properties props = new Properties();

    public GameOptionsLoader(GameOptions options) {
        this.options = options;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public void loadOptions(String resourceName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            props.load(is);
        }
        processProperties(props);
    }
    
    public void loadOptions() {
        processProperties(props);
    }
    
    public void saveOptions() {
        props.setProperty("boardSize", Integer.toString(options.getBoardSize()));
        
        for (int sz = 1; sz <= GameOptions.MAX_SHIP_SIZE; sz++) {
            int count = options.getSizeCount(sz);
            if (count > 0) {
                props.setProperty("shipCount." + sz, Integer.toString(count));
            } else {
                break;
            }
        }
    }
    
    void processProperties(Properties p) {
        int size = Integer.parseInt(p.getProperty("boardSize", "10"));
        options.setBoardSize(size);
        
        for (int sz = 1; sz <= GameOptions.MAX_SHIP_SIZE; sz++) {
            int count = Integer.parseInt(p.getProperty("shipCount." + sz, "0"));
            options.setSizeCount(sz, count);
        }
    }
}

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
package one.dedic.consolelode.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import one.dedic.consolelode.GameOptions;

/**
 *
 * @author sdedic
 */
public class GameOptionsLoader {
    private final GameOptions options;

    public GameOptionsLoader(GameOptions options) {
        this.options = options;
    }

    public void loadResource(String resourceName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            Properties p = new Properties();
            p.load(is);
            processProperties(p);
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

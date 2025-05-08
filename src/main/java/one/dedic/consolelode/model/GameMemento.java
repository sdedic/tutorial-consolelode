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
package one.dedic.consolelode.model;

import java.util.ArrayList;
import java.util.List;
import one.dedic.consolelode.GameOptions;
import one.dedic.consolelode.data.Cell;
import one.dedic.consolelode.data.Placement;

/**
 *
 * @author sdedic
 */
public class GameMemento {
    private Cell[][]    playerBoard;
    private Cell[][]    computerBoard;
    private List<Placement> playerItems;
    private List<Placement> computerItems;
    private boolean     gameStarted;
    private GameOptions gameOptions;

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public Cell[][] getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerBoard(Cell[][] playerBoard) {
        this.playerBoard = playerBoard;
    }

    public Cell[][] getComputerBoard() {
        return computerBoard;
    }

    public void setComputerBoard(Cell[][] computerBoard) {
        this.computerBoard = computerBoard;
    }

    public List<Placement> getPlacedItems() {
        return playerItems;
    }

    public void setPlacedItems(List<Placement> placedItems) {
        this.playerItems = placedItems;
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public void setGameOptions(GameOptions gameOptions) {
        this.gameOptions = gameOptions;
    }

    public List<Placement> getPlayerItems() {
        return playerItems;
    }

    public void setPlayerItems(List<Placement> playerItems) {
        this.playerItems = playerItems;
    }

    public List<Placement> getComputerItems() {
        return computerItems;
    }

    public void setComputerItems(List<Placement> computerItems) {
        this.computerItems = computerItems;
    }
    
    
}

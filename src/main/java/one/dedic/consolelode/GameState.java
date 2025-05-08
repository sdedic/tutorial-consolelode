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

/**
 * Stav, ve kterem se hra nachazi
 * @author sdedic
 */
public enum GameState {
    /**
     * Vyber velikosti
     */
    SIZE_SELECT,
    
    /**
     * Vyber tvaru
     */
    SHAPE_SELECT,
    
    /**
     * Umisteni lodi
     */
    POSITION(SIZE_SELECT),
    
    /**
     * Vybirani jiz umistene lodi k uprave,
     */
    PICKUP(SIZE_SELECT, POSITION),
    
    /**
     * Pozicovani nebo uprava uz umistene lodi
     */
    EDIT(SIZE_SELECT, POSITION),
    
    /**
     * Ukladani hry
     */
    SAVE,
    
    /**
     * Nacitani ulozeneho stavu
     */
    LOAD,
    
    CONFIRM,
    
    /**
     * Tah hrace, vybirani strelecke pozice
     */
    PLAYER,
    
    /**
     * Tah protivnika
     */
    ENEMY,
    
    /**
     * Konec hry
     */
    ENDGAME,
    
    /**
     * Ukonceni programu
     */
    EXIT;

    private GameState next;
    private GameState prev;
    
    private GameState() {}

    private GameState(GameState next) {
        this.next = next;
    }

    private GameState(GameState prev, GameState next) {
        this.next = next;
        this.prev = prev;
    }

    public GameState getNext() {
        if (next != null) {
            return next;
        } else {
            return GameState.values()[ordinal() + 1];
        }
    }

    public GameState getPrev() {
        if (prev != null) {
            return prev;    
        } if (ordinal() > 0) {
            return GameState.values()[ordinal() - 1];
        } else {
            return this;
        }
    }
}

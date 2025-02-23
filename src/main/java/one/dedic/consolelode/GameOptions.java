/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.dedic.consolelode;

import java.util.Arrays;
import one.dedic.consolelode.data.ItemLibrary;
import one.dedic.consolelode.data.ItemTemplate;


/**
 * Konfiguracni objekt, obsahuje nastaveni hry.
 * 
 * @author sdedic
 */
public class GameOptions {
    /**
     * Maximalni uvazovana velikost lodi. Pouziva se napr pro dimenzi poli, kdyz
     * se mi nechce pocitat presnou velikost
     */
    public static int MAX_SHIP_SIZE = 10;
    
    /**
     * Pocty lodi jednotlivych velikosti.
     */
    private int[]   shipSizeCount = new int[MAX_SHIP_SIZE];
    
    /**
     * Knihovna lodi
     */
    private ItemLibrary library;
    
    private int boardSize = 10;

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public ItemLibrary getLibrary() {
        return library;
    }

    public void setLibrary(ItemLibrary library) {
        this.library = library;
    }
    
    /**
     * Vrati nejmensi moznou velikost lode, typicky 1. Vrati 0, pokud nejsou zadne lode -
     * asi chyba v konfiguraci.
     * @return nejmensi znama velikost lodi.
     */
    public int getMinSize() {
        for (int i = 0; i < shipSizeCount.length; i++) {
            if (shipSizeCount[i] > 0) {
                return i + 1;
            }
        }
        return 0;
    }
    
    /**
     * Vrati nejvetsi moznou velikost lode, typicky 5. Vrati 0, pokud nejsou zadne lode -
     * asi chyba v konfiguraci.
     * @return Nejvetsi znama velikost lodi
     */
    public int getMaxSize() {
        for (int i = shipSizeCount.length - 1; i >= 0; i--) {
            if (shipSizeCount[i] > 0) {
                return i + 1;
            }
        }
        return 0;
    }
    
    /**
     * Vrati pocet lodi zadane velikosti.
     * @param shipSize velikost lodi 1.. max
     * @return pocet. 0 pokud nejsou lode
     */
    public int getSizeCount(int shipSize) {
        if (shipSize == 0 || shipSize > shipSizeCount.length) {
            // nejaka blbost
            return 0;
        }
        return shipSizeCount[shipSize - 1];
    }
    
    public void setSizeCount(int size, int count) {
        shipSizeCount[size- 1] = count;
    }
}

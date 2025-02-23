/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
public class BoardState {
    private final GameOptions options;
    private final int size;
    private final Cell[][]    boardState;
    private int[] sizeCounts = new int[GameOptions.MAX_SHIP_SIZE];
    
    private List<Placement> placedItems = new ArrayList<>();
    private Placement current;
    private int cursorX;
    private int cursorY;
    private ShipState shipState = ShipState.OK;
    
    public enum ShipState {
        /**
         * Nic zvlastniho se nedeje
         */
        OK,
        
        /**
         * Umistovana lod zasahuje do jine lodi
         */
        Overlapping,
        
        /**
         * Umistovana lod presahuje hraci prostor
         */
        OutOfGrid,
    }

    public BoardState(int size, GameOptions options) {
        this.options = options;
        this.size = size;
        boardState = new Cell[size][];
        for (int i = 0; i < boardState.length; i++) {
            boardState[i] = new Cell[size];
        }
        resetCursor();
    }

    public ShipState getShipState() {
        return shipState;
    }

    public Placement getCurrent() {
        return current;
    }
    
    public Placement findItem(int x, int y) {
        if (current != null && current.contains(x, y)) {
            return current;
        }
        for (Placement p : placedItems) {
            if (p.contains(x, y)) {
                return p;
            }
        }
        return null;
    }
    
    public boolean isTargetting() {
        return cursorX != -1 || cursorY != -1;
    }
    
    public void resetCursor() {
        cursorX = cursorY = -1;
    }
    
    public void setCursorX(int x) {
        this.cursorX = x;
    }
    
    public void setCursorY(int y) {
        this.cursorY = y;
    }
    
    public void setCursor(int x, int y) {
        this.cursorX = x;
        this.cursorY = y;
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }
    
    public GameOptions getOptions() {
        return options;
    }
    
    public Cell getCell(int x, int y) {
        Cell c = boardState[y][x];
        if (current != null && current.contains(x, y)) {
            c = current.getCell(x, y);
            if (c != null && c.isShip()) {
                c = Cell.SHIP_CURRENT;
            }
        }
        if (c == null) {
            c = Cell.UNKNOWN;
        }
        return c;
    }
    
    public void refreshState() {
        if (current == null) {
            shipState = ShipState.OK;
            return;
        }
        if (current.getX() + current.getWidth() >= options.getBoardSize()) {
            shipState = ShipState.OutOfGrid;
            return;
        }
        if (current.getY() + current.getHeight()>= options.getBoardSize()) {
            shipState = ShipState.OutOfGrid;
            return;
        }
        for (int y = current.getY(); y < current.getY() + current.getHeight(); y++) {
            for (int x = current.getX(); x < current.getX() + current.getWidth(); x++) {
                if (current.contains(x, y)) {
                    Cell c = boardState[y][x];
                    if (c != null && c.isShip()) {
                        shipState = ShipState.Overlapping;
                        return;
                    }
                }
            }
        }
        shipState = ShipState.OK;
    }
    
    public int getMissingCount(int size) {
        if (size < 1 || size > GameOptions.MAX_SHIP_SIZE) {
            return 0;
        }
        int max = options.getSizeCount(size);
        return max - sizeCounts[size - 1];
    }
    
    void eraseItem(Placement placement) {
        int ox = placement.getX();
        int oy = placement.getY();
        Cell[][] shape = placement.getShape();
        for (int i = 0; i < placement.getWidth(); i++) {
            for (int j = 0; j < placement.getWidth(); i++) {
                int x = ox + j;
                int y = oy + i;
                Cell c = shape[i][j];
                if (c == null) {
                    continue;
                }
                boardState[y][x] = null;
            }
        }
    }
    
    void drawItem(Placement placement) {
        int ox = placement.getX();
        int oy = placement.getY();
        Cell[][] shape = placement.getShape();
        for (int i = 0; i < placement.getWidth(); i++) {
            for (int j = 0; j < placement.getWidth(); i++) {
                int x = ox + j;
                int y = oy + i;
                Cell c = shape[i][j];
                if (c == null || c.isClear()) {
                    continue;
                }
                boardState[y][x] = c;
            }
        }
    }
    
    public void finishEditing() {
        selectCurrent(null);
    }
    
    public boolean isComplete() {
        for (int sz = options.getMinSize(); sz <= options.getMaxSize(); sz++) {
            if (getMissingCount(sz) > 0) {
                return false;
            }
        }
        return true;
    }
    
    public void selectCurrent(Placement newCurrent) {
        if (this.current == newCurrent) {
            refreshState();
            return;
        }
        if (current != null) {
            placedItems.add(current);
            drawItem(current);
        }
        current = newCurrent;
        if (placedItems.remove(newCurrent)) {
            eraseItem(newCurrent);
        }
        refreshState();
    }
    
    public void modifyCurrent(Placement current) {
        if (this.current == current) {
            refreshState();
            return;
        }
        if (this.current != null) {
            int shipSize = current.getTemplate().getShipSize();
            if (shipSize > 0) {
                sizeCounts[shipSize - 1]--;
            }
        }
        this.current = current;
        if (current != null) {
            int shipSize = current.getTemplate().getShipSize();
            if (shipSize > 0) {
                sizeCounts[shipSize - 1]++;
            }
        }
        refreshState();
    }
    
    public void removeItem(Placement placement) {
        placedItems.remove(placement);
        eraseItem(placement);
        int shipSize = placement.getTemplate().getShipSize();
        if (shipSize > 0) {
            sizeCounts[shipSize - 1]--;
        }
    }
    
    public void placeItem(Placement placement) {
        placedItems.add(placement);
        drawItem(placement);
        int shipSize = placement.getTemplate().getShipSize();
        if (shipSize > 0) {
            sizeCounts[shipSize - 1]++;
        }
    }
}

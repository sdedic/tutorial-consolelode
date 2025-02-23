package one.dedic.consolelode.data;


import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Rozkres nejake veci, kterou muzeme umistit na plochu. Je to desne slozite, protoze
 * jsem si myslel, ze se treba budou umistovat i nejake obrazky atd
 */
public class ItemTemplate {
    private final String      id;
    private final String      name;
    private final Cell[][]    cells;
    private final int         shipSize;
    
    ItemTemplate(String id, String name, Cell[][] cells) {
        this.id = id;
        this.name = name;
        this.cells = cells;
        // count shipsize:
        int size = 0;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (cells[i][j] != null && cells[i][j].isShip()) {
                    size++;
                }
            }
        }
        shipSize = size;
    }

    public String getId() {
        return id;
    }

    public int getShipSize() {
        return shipSize;
    }

    
    public String getName() {
        return name;
    }

    public Cell[][] getCells() {
        return cells;
    }
    
    public Cell getCell(int row, int col) {
        Cell c = cells[row][col];
        if (c == null) {
            return Cell.WATER;
        } else {
            return c;
        }
    }
    
    public static Builder builder(String id) {
        return new Builder(id);
    }
    
    public int getWidth() {
        return cells[0].length;
    }

    public int getHeight() {
        return cells.length;
    }
    
    public static class Builder {
        private final String id;
        private String name;
        private int maxLen;
        private List<Cell>  currentRow;
        private List<List<Cell>> rows = new ArrayList<>();

        Builder(String name) {
            this.id = name;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder add(Cell c) {
            if (currentRow == null) {
                currentRow = new ArrayList<>();
            }
            currentRow.add(c);
            return this;
        }
        
        public Builder row() {
            rows.add(currentRow);
            if (currentRow.size() > maxLen) {
                maxLen = currentRow.size();
            }
            currentRow = new ArrayList<>();
            return this;
        }
        
        public ItemTemplate build() {
            Cell[][] cells = new Cell[rows.size()][];
            for (int i = 0; i < cells.length; i++) {
                List<Cell> contents = rows.get(i);
                Cell[] row = new Cell[maxLen];
                for (int j = 0; j < contents.size(); j++) {
                    row[j] = contents.get(j);
                }
                cells[i] = row;
            }
            return new ItemTemplate(id, name == null ? id : name, cells);
        }
    }
    
}

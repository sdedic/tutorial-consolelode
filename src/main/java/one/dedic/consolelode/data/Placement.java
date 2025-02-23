package one.dedic.consolelode.data;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Vec umistena na plochu. Kresli se vzdy sablona, ta se da otocit, zrcadlit - a nakonec
 * umistit.
 * @author sdedic
 */
public class Placement {
    /**
     * Puvodni sablona
     */
    private final ItemTemplate template;
    
    /**
     * Jmeno, okopirovane ze sabloby, muze se zmenit
     */
    private String name;
    
    /**
     * Rotace udelena uzivatelem
     */
    private Rotation rotation = Rotation.NORTH;
    
    /**
     * True, je-li zrcadlena horizontalne
     */
    private boolean mirrorX;
    
    /**
     * True, je-li zrcadlena vertikalne
     */
    private boolean mirrorY;
    
    /**
     * Souradnice horniho leveho rohu, X
     */
    private int x;

    /**
     * Souradnice horniho leveho rohu, Y
     */
    private int y;
    
    /**
     * Vlastni tvar lodi, otoceny, zrcadleny, pripraveny k pouziti.
     * Pozor - cist pomoci cell(), ktera vzdy spocita tvar
     */
    private Cell[][] shape;

    public Placement(ItemTemplate template) {
        this.template = template;
        this.shape = template.getCells();
    }
    
    public enum Rotation {
        NORTH,
        EAST, /* 90 deg */
        SOUTH, /* 180 deg */
        WEST /* 270 deg */
    }

    public boolean isMirrorX() {
        return mirrorX;
    }

    public boolean isMirrorY() {
        return mirrorY;
    }

    public void setMirrorX(boolean mirrorX) {
        this.mirrorX = mirrorX;
        shape = null;
    }

    public void setMirrorY(boolean mirrorY) {
        this.mirrorY = mirrorY;
        shape = null;
    }
    
    public Cell getCell(int absX, int absY) {
        int dx = absX - this.x;
        int dy = absY - this.y;
        if (dx < 0 || dy < 0 || dx >= getWidth() || dy >= getHeight()) {
            return null;
        }
        return shape()[dy][dx];
    }

    public Cell[][] getShape() {
        return shape();
    }
    
    public int getWidth() {
        return shape()[0].length;
    }
    
    public int getHeight() {
        return shape().length;
    }

    public String getName() {
        return name;
    }
    
    public String getItemId() {
        return template.getId();
    }
    
    public ItemTemplate getTemplate() {
        return template;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
        shape = null;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public boolean contains(int x, int y) {
        if (x < this.x || y < this.y) {
            return false;
        }
        if (x >= this.x + getWidth() || y >= this.y + getHeight()) {
            return false;
        }
        x -= this.x;
        y -= this.y;
        Cell c = shape()[y][x];
        return c != null && c.isShip();
    }
    
    private Cell[][] shape() {
        if (shape == null) {
            Cell[][] s = template.getCells();
            switch (rotation) {
                case NORTH:
                    break;
                case EAST:
                    s = Transformations.rotate(s, true);
                    break;
                case SOUTH:
                    s = Transformations.rotate(s, true);
                    s = Transformations.rotate(s, true);
                    break;
                case WEST:
                    s = Transformations.rotate(s, false);
                    break;
            }
            if (mirrorX) {
                s = Transformations.mirror(s, true);
            }
            if (mirrorY) {
                s = Transformations.mirror(s, false);
            }
            shape = s;
        }
        return shape;
    }
    
    public Placement copy() {
        Placement p = new Placement(template);
        p.x = x;
        p.y = y;
        p.mirrorX = mirrorX;
        p.mirrorY = mirrorY;
        p.rotation = rotation;
        p.name = name;
        return p;
    }
}

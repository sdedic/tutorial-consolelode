/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.dedic.consolelode.data;

/**
 *
 * @author sdedic
 */
public enum Cell {
    /**
     * Cell contents is not known yet
     */
    UNKNOWN,
    
    /**
     * Empty. For ship template, a transparent blank cell that will not be
     * printed on the player board.
     */
    EMPTY,
    
    /**
     * Water, no attempt to shoot.
     */
    WATER,
    
    /**
     * Water, shot and missed
     */
    MISSED,
    
    /**
     * Water, reserved area around a ship
     */
    RESERVED,
    
    /**
     * Ship, no attempt to shot
     */
    SHIP,
    
    /**
     * Ship currently being placed
     */
    SHIP_CURRENT,
    
    /**
     * Hit ship part, not sunken
     */
    HIT,
    
    /**
     * Destroyed ship
     */
    DESTROYED;
    
    /**
     * @return true, if the cell represents a water, possibly already shot at
     */
    public boolean isClear() {
        return ordinal() < SHIP.ordinal();
    }
    
    /**
     * @return true, if the cell represents a ship part, shot or not
     */
    public boolean isShip() {
        return ordinal() >= SHIP.ordinal();
    }
    
    /**
     * @return true, if the call is a reasonable candidate for a shot.
     */
    public boolean isCandidate() {
        return this == WATER || this == UNKNOWN;
    }
    
    /**
     * Returns true for a missed water tile, damaged or destroyed ship.
     * @return true, if the cell was already fired upon
     */
    public boolean wasShot() {
        return this == HIT || this == DESTROYED || this == MISSED;
    }
    
    public boolean isDefined() {
        return !(this == UNKNOWN || this == EMPTY);
    }
}

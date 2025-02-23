/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.dedic.consolelode.data;

import java.util.Arrays;

/**
 *
 * @author sdedic
 */
public class Transformations {
    
    void x() {
        char[][] arr = new char[10][];
    }
    
    public static <T> T[][] mirror(T[][] original, boolean vertical) {
        T[][] result = Arrays.copyOf(original, original.length);
        
        for (int i = 0; i < result.length; i++) {
            if (vertical) {
                T[] row = Arrays.copyOf(original[i], original[i].length);
                for (int j = 0; j < row.length; j++) {
                    row[row.length - 1 - j] = original[i][j];
                }
            } else {
                result[result.length - 1 - i] = Arrays.copyOf(original[i], original[i].length);
            }
        }
        return result;
    }

    /**
     * Zrcadli pole horizontalne nebo vertikalne. Zachovava rozmery
     * s
     * @param original puvodni pole
     * @param vertical true - vertikalni zrcadlo; false - horizontalni zrcadlo
     * @return 
     */
    public static Cell[][] mirror(Cell[][] original, boolean vertical) {
        Cell[][] result = new Cell[original.length][];
        
        for (int i = 0; i < result.length; i++) {
            if (vertical) {
                Cell[] row = new Cell[original[i].length];
                for (int j = 0; j < row.length; j++) {
                    row[row.length - 1 - j] = original[i][j];
                }
            } else {
                result[result.length - 1 - i] = Arrays.copyOf(original[i], original[i].length);
            }
        }
        return result;
    }
    
    public static Cell[][] rotate(Cell[][] original, boolean clockwise) {
        Cell[][] result = new Cell[original[0].length][];
        int w = result.length;
        int h = result[0].length;
        
        for (int i = 0 ; i < result.length; i++) {
            result[i] = new Cell[original.length];
        }
        if (clockwise) {
            for (int i = 0 ; i < original.length; i++) {
                for (int j = 0; i < original[0].length; j++) {
                    result[j][w - 1 - i] = original[i][j];
                }
            }
        } else {
            for (int i = 0 ; i < original.length; i++) {
                for (int j = 0; i < original[0].length; j++) {
                    result[h - 1 - j][i] = original[i][j];
                }
            }
        }
        return result;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.dedic.consolelode.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static one.dedic.consolelode.data.Cell.*;

/**
 * Nacte definice lodi ze zadaneho souboru do knihovny lodi.
 * @author sdedic
 */
public class PropertyTemplateLoader {
    private final ItemLibrary library;

    public PropertyTemplateLoader(ItemLibrary library) {
        this.library = library;
    }
    
    public void loadResource(String resourceName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            Properties p = new Properties();
            p.load(is);
            processProperties(p);
        }
        Cell[][] x = new Cell[][] {
            { EMPTY }
        };
    }
    
    void processProperties(Properties p) {
        Set<String> shipNames = new HashSet<>();
        for (String n : p.stringPropertyNames()) {
            if (n.startsWith("ship")) {
                int dot = n.indexOf('.');
                if (dot == -1) {
                    continue;
                }
                String id = n.substring(0, dot);
                shipNames.add(id);
            }
        }
        
        for (String s : shipNames) {
            ItemTemplate ship = loadShip(s, p);
            library.addTemplateItem(ship);
        }
    }
    
    Cell createCell(String id, char c) {
        if (c == ' ') {
            return Cell.EMPTY;
        } else {
            return Cell.SHIP;
        }
    }
    
    ItemTemplate loadShip(String id, Properties p) {
        String name = p.getProperty(id + ".name", "Lod " + id);
        ItemTemplate.Builder bld = ItemTemplate.builder(id).name(name);
        
        for (int i = 1; i < 10; i++) {
            String lineKey = id + ".line." + i;
            if (!p.containsKey(lineKey)) {
                break;
            }
            String line = p.getProperty(lineKey);
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                bld.add(createCell(id, c));
            }
            bld.row();
        }
        return bld.build();
    }
}

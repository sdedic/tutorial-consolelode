package one.dedic.consolelode.data;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Knihovna lodi. Mozna casem i dalsich druhu 'objektu' ve hre.
 * @author sdedic
 */
public class ItemLibrary {
    private List<ItemTemplate>  itemTemplates = new ArrayList<>();
    
    public void addTemplateItem(ItemTemplate item) {
        this.itemTemplates.add(item);
    }
    
    public List<ItemTemplate> getTemplates() {
        return Collections.unmodifiableList(itemTemplates);
    }
}

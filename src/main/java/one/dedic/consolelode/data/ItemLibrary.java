package one.dedic.consolelode.data;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Knihovna lodi. Mozna casem i dalsich druhu 'objektu' ve hre.
 * @author sdedic
 */
public class ItemLibrary {
    private Map<String, ItemTemplate> templateMap = new LinkedHashMap<>();
    private List<ItemTemplate>  itemTemplates = new ArrayList<>();
    
    public void addTemplateItem(ItemTemplate item) {
        templateMap.put(item.getId(), item);
        itemTemplates.add(item);
    }
    
    public ItemTemplate find(String id) {
        return templateMap.get(id);
    }
    
    public List<ItemTemplate> getTemplates() {
        return Collections.unmodifiableList(itemTemplates);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.manager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

/**
 *
 * @author drordas
 */

public class PluginColors {
    
    private final HashMap<String,String> rulePlugin;
    private final HashMap<String, Color> pluginColor;
    private Stack<Color> listColor;
    public PluginColors(){
        rulePlugin = new HashMap<>();
        rulePlugin.put("IDLE", "IDLE");
        
        pluginColor = new HashMap<>();
        pluginColor.put("IDLE", Color.WHITE);
        
        listColor = generateDefaultColorList();
    }
    
    public void addValues(String ruleName, String pluginName){
        if(!rulePlugin.containsKey(ruleName)){
            rulePlugin.put(ruleName, pluginName);
            if(!pluginColor.containsKey(pluginName)){
                if(!listColor.isEmpty()){
                    pluginColor.put(pluginName, listColor.pop());
                }
                else{
                    Random rand = new Random();
                    pluginColor.put(pluginName, 
                                    new Color( rand.nextInt(255), 
                                               rand.nextInt(255), 
                                               rand.nextInt(255))
                                             );
                }
            }
        }
    }
    
    public Color getColorForRule(String ruleName){
        if (ruleName.startsWith("IDLE")) 
            return pluginColor.get(rulePlugin.get("IDLE"));
            
        if( rulePlugin.containsKey(ruleName) ){
            return pluginColor.get(rulePlugin.get(ruleName));
        }
        
        return null;
    }
    
    public HashMap<String, Color> getPlugins(){
        return pluginColor;
    }
    
    private Stack<Color> generateDefaultColorList(){
        listColor = new Stack<>();
        listColor.add(new Color(171,134,186));
        listColor.add(new Color(22,156,120));
        listColor.add(new Color(218,79,112));
        listColor.add(new Color(65,160,222));
        listColor.add(new Color(240,191,89));
        listColor.add(new Color(64,75,82));
        listColor.add(new Color(60,47,0));
        return listColor;
    }
}

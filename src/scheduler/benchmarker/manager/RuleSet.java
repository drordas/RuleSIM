/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.benchmarker.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author jf
 */
public class RuleSet {
    private HashMap<String,Rule> rulesSet;
    private HashSet<String> plugins;
    private double pending_add;
    private double pending_substract;
    private final double DEFAULT_REQUIRED_SCORE = 5D;
    private double required_score;
    
    public RuleSet(){
        rulesSet = new HashMap<>();
        plugins = new HashSet<>();
        pending_add = 0D;
        pending_substract = 0D;
        required_score = DEFAULT_REQUIRED_SCORE;
    }
    
    public RuleSet(HashMap map){
        rulesSet= map;
    }
    
    public double getCPUTime(String rule){
        if(rulesSet.containsKey(rule)){
            return rulesSet.get(rule).getCPUTime();
        }
        return 0.0;
    }
    
    public double getIOTime(String rule){
        if(rulesSet.containsKey(rule)){
            return rulesSet.get(rule).getIOTime();
        }
        return 0.0;
    }
    
    
    public String getPluginName(String rule){
          if(rulesSet.containsKey(rule)){
            return rulesSet.get(rule).getPlugin();
        }
        return "";
    }
    
    public double getScore(String rule){
        if(rulesSet.containsKey(rule)){
            return rulesSet.get(rule).getIOTime();
        }
        return 0.0;
    }
    
    public Rule getRule(String name){
        if(rulesSet.containsKey(name)){
            return rulesSet.get(name);
        }
        return null;
    }
    
    
    public void insertRule(String name, Rule info){
        
        if(info.getScore()!=0D){
            rulesSet.put(name, info);
            plugins.add(info.getPlugin());
            if(info.getScore()<0D)
                pending_substract+=info.getScore();
            else pending_add+=info.getScore();
        }
    }
    
    public int getSize(){
        return rulesSet.size();
    }
    
    public boolean isEmpty(){
        return (rulesSet.isEmpty());
    }
    
    @Override
    public String toString(){
        int i=0;
        Iterator iter = rulesSet.entrySet().iterator();
        String cad = "\nRULE LOADED\n"
                   + "---------------------------------------------\n"
                   + "ID RULENAME   CPU   IO   SCORE   PLUGIN         \n"
                   + "---------------------------------------------\n";
        while(iter.hasNext()){
            Map.Entry<String,Rule> entry = (Map.Entry<String,Rule>) iter.next();
            cad += "["+(i++)+"]  " + entry.getKey() + "   " 
                  + entry.getValue().getCPUTime() + "   " 
                  + entry.getValue().getIOTime() + "   " 
                  + entry.getValue().getScore()  + "   " 
                  + entry.getValue().getPlugin() + "\n";
        }
        
        cad += "PENDING ADD: " + pending_add + "\n";
        cad += "PENDING SUBSTRACT: " + pending_substract;
        return cad;
    }
    
    public int countPlugins(){
        return plugins.size();
    }
    
    public HashSet<String> getPlugins(){
        return plugins;
    }
    
    public HashMap<String,Rule> getRules(){
        return rulesSet;
    }
    
    public double getPendingAdd(){
        return pending_add;
    }
    
    public double getPendingSubstract(){
        return pending_substract;
    }
    
    public void removeAll(){
        pending_add = 0D;
        pending_substract = 0D;
        rulesSet.clear();
    }
    
    public void setRequiredScore(double requiredS){
        required_score = requiredS;
    }
    
    public double getRequiredScore(){
        return required_score;
    }
}

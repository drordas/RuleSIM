/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.Rule;
import scheduler.benchmarker.manager.RuleSet;

/**
 *
 * @author drordas
 */
public class PluginSeparation extends Scheduler{
    
    public PluginSeparation(RuleSet ruleSet) {
        super("Plugin Separation",ruleSet);
    }
    
    @Override
    public Filter executeScheduler() {
        
        if(rSet.isEmpty()) return null;

        List<Rule> rulesSorted = new ArrayList<>();
        HashMap<String, Stack<Rule>> pluginIndex = new HashMap<>();
        Iterator<Rule> iter = rSet.getRules().values().iterator();
        
        while(iter.hasNext()){
            Rule r = iter.next();
            if(pluginIndex.containsKey(r.getPlugin())){
                Stack<Rule> pluginStack = pluginIndex.get(r.getPlugin());
                pluginStack.add(r);
            }else{
                Stack<Rule> pluginStack = new Stack<>();
                pluginStack.add(r);
                pluginIndex.put(r.getPlugin(), pluginStack);
            }
        }
        
        List<Stack<Rule>> listRules = new ArrayList<>(pluginIndex.values());
        
        int index = 0;
        while(!listRules.isEmpty()){
            index = index%listRules.size();
            Stack<Rule> r = listRules.get(index);
            if( r.isEmpty() ) 
                listRules.remove(r);
            else{
                rulesSorted.add(r.lastElement());
                r.remove(r.lastElement());
            }
            index++;
        }
        
        rFilter = new Filter();
        rFilter.setRules(rulesSorted);
        rFilter.setRequired_score(rSet.getRequiredScore());
        
        return rFilter;
    }
    
}

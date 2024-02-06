/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.Rule;
import scheduler.benchmarker.manager.RuleSet;

/**
 *
 * @author drordas
 */
public class PluginGreaterSignificance extends Scheduler{
    
    public PluginGreaterSignificance(RuleSet ruleSet) {
        super("Greater Significance",ruleSet);
    }
    
    @Override
    public Filter executeScheduler() {
        List<Rule> rules = new ArrayList<>(rSet.getRules().values());
        Collections.sort(rules,new Comparator<Rule>(){
            @Override
            public int compare(Rule o1, Rule o2) {
               return ( o1.getAvgPluginOverload() < o2.getAvgPluginOverload()?1:
                      ( o1.getAvgPluginOverload() > o2.getAvgPluginOverload()?-1:0) );
            }
        });
        
        rFilter = new Filter();
        rFilter.setRules(rules);
        rFilter.setRequired_score(rSet.getRequiredScore());
        return rFilter;
    }
    
}

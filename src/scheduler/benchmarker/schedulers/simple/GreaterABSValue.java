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
public class GreaterABSValue extends Scheduler{
    
    public GreaterABSValue(RuleSet ruleSet) {
        super("Greater ABS Value",ruleSet);
    }
    
    @Override
    public Filter executeScheduler() {
        List<Rule> rules = new ArrayList<>(rSet.getRules().values());
        Collections.sort(rules,new Comparator<Rule>(){
            @Override
            public int compare(Rule o1, Rule o2) {
                return (Math.abs(o1.getScore()) < Math.abs(o2.getScore())?1:
                       (Math.abs(o1.getScore()) > Math.abs(o2.getScore())?-1:0));
            }
        });
        
        rFilter = new Filter();
        rFilter.setRules(rules);
        rFilter.setRequired_score(getRequiredScore());
        return rFilter;
    }
    
}

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
public class PositiveFirst extends Scheduler{
    
    public PositiveFirst(RuleSet ruleSet) {
        super("Positive First",ruleSet);
    }
    
//    @Override
//    public int compareRules(Rule a, Rule b){
//        if( a.getScore() > b.getScore() ) 
//            return 1;
//        else{
//            if(a.getScore() < b.getScore())
//                return -1;
//            else return 0;
//        }
//    }
    
    @Override
    public Filter executeScheduler() {
        List<Rule> rules = new ArrayList<>(rSet.getRules().values());
        Collections.sort(rules,new Comparator<Rule>(){
            @Override
            public int compare(Rule o1, Rule o2) {
                return (o1.getScore() < o2.getScore()?1:(o1.getScore()>o2.getScore()?-1:0));
            }
        });
        
        rFilter = new Filter();
        rFilter.setRules(rules);
        rFilter.setRequired_score(rSet.getRequiredScore());
        
        return rFilter;
    }
    
}

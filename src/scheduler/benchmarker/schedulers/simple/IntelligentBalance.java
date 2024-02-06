/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.Rule;
import scheduler.benchmarker.manager.RuleSet;

/**
 *
 * @author drordas
 */
public class IntelligentBalance extends Scheduler{
    
    public IntelligentBalance(RuleSet ruleSet) {
        super("Intelligent Balance",ruleSet);
    }
    
    @Override
    public Filter executeScheduler() {
        List<Rule> posRules = new ArrayList<>();
        List<Rule> negRules = new ArrayList<>();
        List<Rule> totalRules = new ArrayList<>();
        
        Iterator<Rule> iter =  rSet.getRules().values().iterator();
        while(iter.hasNext()){
            Rule r = (Rule)iter.next();
            if(r.getScore() >= 0D){
                posRules.add(r);
            }else negRules.add(r);
        }
        
        Collections.sort(posRules,new Comparator<Rule>(){
            @Override
            public int compare(Rule o1, Rule o2) {
                return (o1.getScore() < o2.getScore()?1:(o1.getScore()>o2.getScore()?-1:0));
            }
        });
        
        Collections.sort(negRules,new Comparator<Rule>(){
            @Override
            public int compare(Rule o1, Rule o2) {
                return (o1.getScore() > o2.getScore()?1:(o1.getScore()<o2.getScore()?-1:0));
            }
        });
        
        if( Math.abs(rSet.getPendingAdd()) >= 
            Math.abs(rSet.getPendingSubstract()) )
        {
            Iterator<Rule> posIter = posRules.iterator();
            while(posIter.hasNext()){
                Rule r = posIter.next();
                totalRules.add(r);
            }
            
            Iterator<Rule> negIter = negRules.iterator();
            while(negIter.hasNext()){
                Rule r = negIter.next();
                totalRules.add(r);
            }
        }else{
            Iterator<Rule> negIter = negRules.iterator();
            while(negIter.hasNext()){
                Rule r = negIter.next();
                totalRules.add(r);
            }
            Iterator<Rule> posIter = posRules.iterator();
            while(posIter.hasNext()){
                Rule r = posIter.next();
                totalRules.add(r);
            }
        }
        
        rFilter = new Filter();
        rFilter.setRules(totalRules);
        rFilter.setRequired_score(rSet.getRequiredScore());
        return rFilter;
    }
    
}

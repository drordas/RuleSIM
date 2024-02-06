/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.simple;

import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.RuleSet;

/**
 *
 * @author drordas
 */
public interface SchedulerInterface {
    
    public String getName();
    
    public void setName(String name);
    
    public Filter getPlan();
    
    public void setPlan(Filter filter);
    
    public void setRuleSet(RuleSet ruleSet);
    
    public Filter executeScheduler();
    
    public double getRequiredScore();
    
    //public int compareRules(Rule a, Rule b);
    
    public void toScreen();
}

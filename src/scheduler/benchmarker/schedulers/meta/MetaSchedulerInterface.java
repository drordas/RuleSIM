/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.meta;

import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.RuleSet;

/**
 *
 * @author drordas
 */
public interface MetaSchedulerInterface {
    
    public String getName();
    
    public void setName(String name);
    
    public Filter getPlan();
    
    public void setPlan(Filter filter);
    
    public void setRuleSet(RuleSet ruleSet);
    
    public Filter executeScheduler();
    
    public double getRequiredScore();
    
    public void toScreen();
}

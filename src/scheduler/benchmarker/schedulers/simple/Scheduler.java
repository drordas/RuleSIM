/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.simple;

import java.util.Iterator;
import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.Rule;
import scheduler.benchmarker.manager.RuleSet;

/**
 *
 * @author drordas
 */
public abstract class Scheduler implements SchedulerInterface{
    
    protected RuleSet rSet;
    protected Filter rFilter;
    protected String sName;
    
    public Scheduler(String name, RuleSet ruleSet){
        sName = name;
        rSet = ruleSet;
        rFilter = null;
    }
    
    @Override
    public Filter getPlan() {
        return rFilter;
    }
    
    @Override
    public void setPlan(Filter filter) {
        rFilter = filter;
    }
    
    @Override
    public void setRuleSet(RuleSet ruleset){
        rSet = ruleset;
    }
    
    @Override
    public double getRequiredScore(){
        return rSet.getRequiredScore();
    }

    @Override
    public String getName() {
        return sName;
    }

    @Override
    public void setName(String name) {
        sName = name;
    }
    
    @Override
    public void toScreen(){
        if( rFilter != null && !rFilter.getRules().isEmpty() ){
            Iterator<Rule> iter = rFilter.getRules().iterator();
            System.out.println("EVALUATION ORDER FOR '"+sName+"' SCHEDULER");
            System.out.println(" + REQUIRED_SCORE: "+String.format("%2.2f",rFilter.getRequired_score()));
            double cpuCounter = 0D;
            double ioCounter = 0D;
            while(iter.hasNext()){
                Rule r = iter.next();
                cpuCounter += r.getCPUTime();
                ioCounter += r.getIOTime();
                System.out.println("<-->ACUM. VALUES<-->");
                System.out.println("  +CPU VALUE: " + String.format("%2.2f",cpuCounter));
                System.out.println("  +IO VALUE: " + String.format("%2.2f",ioCounter));
                System.out.println("<------------------>");
                System.out.println("<-->RULE   INFO<-->");
                System.out.println("  +NAME: " +r.getName());
                System.out.println("  +CPU: " +r.getCPUTime());
                System.out.println("  +IO: "  + r.getIOTime());
                System.out.println("  +SCORE: "  + r.getScore());
                System.out.println("  +PLUGIN: "  + r.getPlugin());
                System.out.println("  +OVERLOAD: "  + r.getAvgPluginOverload());
                System.out.println("<------------------>");
            }
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.simple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.Rule;
import scheduler.benchmarker.manager.RuleSet;

/**
 *
 * @author drordas
 */
public class PluginOverloadSeparation extends Scheduler{
    
    public PluginOverloadSeparation(RuleSet ruleSet) {
        super("Plugin Overload Separation",ruleSet);
    }
    
    @Override
    public Filter executeScheduler() {
        ArrayList<Rule> rules = new ArrayList<>(rSet.getRules().values());
        rFilter = new Filter();
        rFilter.setRules(new ArrayList<Rule>());
        //RECORRO EL ARRAY PARA BUSCAR LA REGLA CON MENOS IO/CPU
        
        Rule min = rules.get(0);
        
        for(Iterator<Rule> it = rules.iterator();it.hasNext();)
        {
            Rule next = it.next();
            if( (min.getIOTime()  + min.getCPUTime()) > 
                (next.getIOTime() + next.getCPUTime()) )
            min = next;
        }
        
        //QUITO LA REGLA Y LA AÑADO A LA LISTA;
        rules.remove(min);
        
        rFilter.getRules().add(min);
        
        double actualIOTime = min.getIOTime();
        double actualCPUTime = min.getCPUTime();
        
        //VOY BALANCEANDO CON LAS QUE QUEDAN.
        for(Iterator<Rule> it = rules.iterator();it.hasNext();){
            Rule r = getBestMatchingRule(actualCPUTime, actualIOTime, rules);
            actualCPUTime += r.getCPUTime();
            actualIOTime += r.getIOTime();
            rules.remove(r);
            rFilter.getRules().add(r);
        }
        
        rFilter.setRequired_score(rSet.getRequiredScore());
        
        return rFilter;
    }
    
    private Rule getBestMatchingRule(double cpuTime, double ioTime, List<Rule> rule){
        Rule r = null;
        double distance = Double.MAX_VALUE;
        double partialDistance;
        
        for(Iterator<Rule> it = rule.iterator();it.hasNext();)
        {
            Rule next = it.next();
            partialDistance = Math.abs( ( cpuTime + next.getCPUTime() ) - ( ioTime + next.getIOTime() ) );
            if( partialDistance < distance )
            {
                r  = next;
                distance = partialDistance;
            }
        }
        return r;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.simple;

import java.util.ArrayList;
import java.util.List;
import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.Rule;
import scheduler.benchmarker.manager.RuleSet;

/**
 *
 * @author drordas
 */
public class DefaultBehaviour extends Scheduler{
    
    public DefaultBehaviour(RuleSet ruleSet) {
        super("Default Scheduler",ruleSet);
    }
    
    @Override
    public Filter executeScheduler() {
        List<Rule> rules = new ArrayList<>(rSet.getRules().values());
        rFilter = new Filter();
        rFilter.setRules(rules);
        rFilter.setRequired_score(getRequiredScore());
        return rFilter;
    }
    
}

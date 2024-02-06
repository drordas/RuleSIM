/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.schedulers.meta;

import scheduler.benchmarker.manager.Filter;
import scheduler.benchmarker.manager.Rule;
import scheduler.benchmarker.manager.RuleSet;
import scheduler.benchmarker.schedulers.simple.Scheduler;

/**
 *
 * @author drordas
 */
public class MetaHeuristicEvaluator extends MetaScheduler{

    public MetaHeuristicEvaluator(Scheduler primaryS, Scheduler secondaryS, 
                                  RuleSet ruleSet) {
        super("Meta Heuristic Evaluator", primaryS, secondaryS, ruleSet);
    }

    @Override
    public Filter executeScheduler() {
        System.out.println("PENDING!!!!");
        return null;
    }
}

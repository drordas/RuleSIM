/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.benchmarker.manager;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jf
 */
public class Filter {
    
    private List<Rule> rules;
    private double required_score;

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public double getRequired_score() {
        return required_score;
    }

    public void setRequired_score(double required_score) {
        this.required_score = required_score;
    }       
    
}

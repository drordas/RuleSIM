/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.manager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author drordas
 */
public class SimulatorResults {
    
    private ArrayList<PlanningResult> planningInfo;
    private PluginColors ruleColors;
    private double globalCPUTime;
    private double globalIOTime;
    private double minCPUTime;
    private double maxCPUTime;
    private double minIOTime;
    private double maxIOTime;
    private int size;
    private int numSfeExecutions;
    private int numUnexecutedRules;
    private int numExecutedRules;
    private final String schedulerUsed;
    private double globalCPULockTime;
    
    public SimulatorResults(){
        planningInfo = null;
        globalCPUTime = 0D;
        globalIOTime = 0D;
        minCPUTime = Double.MAX_VALUE;
        maxCPUTime = Double.MIN_VALUE;
        minIOTime = Double.MAX_VALUE;
        maxIOTime = Double.MIN_VALUE;
        numSfeExecutions = 0;
        numUnexecutedRules = 0;
        numExecutedRules = 0;
        size = 0;
        schedulerUsed = null;
        globalCPULockTime = 0D;
    }
    
    public SimulatorResults(ArrayList<PlanningResult> result,
                            PluginColors rColors, String schedulerName){
        planningInfo = result;
        ruleColors = rColors;
        schedulerUsed = schedulerName;
        calculateGlobalValues();
    }
    
    public void setPlanningResult(ArrayList<PlanningResult> result){
        planningInfo = result;
    }
    
    public ArrayList<PlanningResult> getPlanningResult(){
        return planningInfo;
    }
    
    public int getSize(){
        return size;
    }
    
    public double getGlobalCPUTime(){
        return globalCPUTime;
    }
    
    public double getGlobalIOTime(){
        return globalIOTime;
    }
    
    public double getMinCPUTime(){
        return minCPUTime;
    }
    
    public double getMaxCPUTime(){
        return maxCPUTime;
    }
    
    public double getMinIOTime(){
        return minIOTime;
    }
    
    public double getMaxIOTime(){
        return maxIOTime;
    }
    
    public double getGlobalCPULockTime(){
        return globalCPULockTime;
    }
    
    public double getAverageCPUTime(){
        return globalCPUTime / size;
    }
    
    public double getAverageIOTime(){
        return globalIOTime / size;
    }
    
    public int getNumSFEActivations(){
        return numSfeExecutions;
    }
    
    public int getNumUnexecutedRules(){
        return numUnexecutedRules;
    }
    
    public double getAvgUnexecutedRules(){
        return numUnexecutedRules / planningInfo.size();
    }
    
    public double getSFEPercentage(){
        return ( getNumSFEActivations() * 100 ) / planningInfo.size();
    }
    
    public double getUnexecutedRulesPercentage(){
        return ( getNumUnexecutedRules() * 100) / numExecutedRules;
    }
    
    public PluginColors getRuleColors(){
        return ruleColors;
    }
    
    private void calculateGlobalValues(){
        globalCPUTime = 0D;
        globalIOTime = 0D;
        minCPUTime = Double.MAX_VALUE;
        maxCPUTime = Double.MIN_VALUE;
        minIOTime = Double.MAX_VALUE;
        maxIOTime = Double.MIN_VALUE;
        size = planningInfo.size();
        numSfeExecutions = 0;
        numUnexecutedRules = 0;
        numExecutedRules = 0;
        
        for (Iterator<PlanningResult> it = planningInfo.iterator(); it.hasNext();) {
            PlanningResult result = it.next();
            globalCPUTime+=result.getTotalCPUTime();
            globalIOTime+=result.getTotalIOTime();
            globalCPULockTime+=result.getBlockedTime();
            maxCPUTime = Math.max(maxCPUTime, result.getTotalCPUTime());
            minCPUTime = Math.min(minCPUTime, result.getTotalCPUTime());
            maxIOTime = Math.max(maxIOTime, result.getTotalIOTime());
            minIOTime = Math.min(minIOTime, result.getTotalIOTime());
            numUnexecutedRules += result.getSFERulesAvoided();
            numExecutedRules += result.getExecutedRules();
            if(result.isSFEActivated()) numSfeExecutions++;
        }
    }
    
    public String getSchedulerUsed(){
        return schedulerUsed;
    }
    
    public void printSimulatorResults(JTextField[] textFieldArray){
        textFieldArray[0].setText(String.format("%.2f",getGlobalCPUTime()));
        textFieldArray[1].setText(String.format("%.2f",getGlobalCPUTime()+getGlobalCPULockTime()));
        textFieldArray[2].setText(String.format("%.2f",getMaxCPUTime()));
        textFieldArray[3].setText(String.format("%.2f",getMinCPUTime()));
        textFieldArray[4].setText(String.format("%.2f",getAverageCPUTime()));
        textFieldArray[5].setText(String.format("%.2f",getGlobalCPULockTime()));
        
        textFieldArray[6].setText(String.format("%.2f",getGlobalIOTime()));
        textFieldArray[7].setText(String.format("%.2f",getMaxIOTime()));
        textFieldArray[8].setText(String.format("%.2f",getMinIOTime()));
        textFieldArray[9].setText(String.format("%.2f",getAverageIOTime()));
        
        textFieldArray[10].setText(String.valueOf(getNumSFEActivations()));
        textFieldArray[11].setText(String.format("%.2f",getSFEPercentage()));
        textFieldArray[12].setText(String.valueOf(getNumUnexecutedRules()));
        textFieldArray[13].setText(String.format("%.2f",getUnexecutedRulesPercentage()));
    }
    
}

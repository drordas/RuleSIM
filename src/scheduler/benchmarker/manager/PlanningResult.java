/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.manager;

import java.util.List;
import java.util.Stack;

/**
 *
 * @author drordas
 */
public class PlanningResult {
    private String emailName;
    private double total_cpu_time; // calculos de los tiempos de todas las CPUS.
    private double total_io_time; // Calculo tiempo IO total.
    
    private double sum_total_time; //tarda en total.
    private double sum_exec_rules; // tiempo de ejecución de las reglas.
    
    //private double avg_io_time; //tiempo promedio de las reglas cpu
    //private double avg_cpu_time; //tiempo promdio de las reglas io;
    //private double min_cpu_time; //minimo tiempo de CPU de una regla.
    //private double min_io_time; //minimo tiempo de IO de una regla.
    private int sfe_rules_avoided; //reglas evitadas por SFE.
    private int totalExecutedRules; //reglas evitadas por SFE.
    private boolean sfeActivated;
    
    private double blocked_time; //Tiempo que el procesamiento de reglas se bloquea por 
             //la restricción de no poder ejecutar al mismo tiempo dos reglas que ejecuten
             //funciones del mismo plugin.
    
    private Stack<Pair<Double,Rule>> processorOccupation[];
    List<Pair<Double,Rule>> ioTimeOccupation;
    
    public PlanningResult(){
        emailName = null;
        total_cpu_time = 0D;
        total_io_time = 0D;
        sum_total_time = 0D;
        sum_exec_rules = 0D;
        sfe_rules_avoided = 0;
        processorOccupation = null;
        ioTimeOccupation = null;
        sfeActivated = false;
        totalExecutedRules = 0;
    }
    
    public PlanningResult( String eName, double tot_cpu, double tot_io,double tot_time, 
                           double tot_exec, int sfe_rule, boolean sfeExecuted,
                           int executedRules,
                           Stack<Pair<Double,Rule>> proc_Occup[], 
                           List<Pair<Double,Rule>> ioTimeOccup , double blck_time)
    {
        emailName = eName;
        total_cpu_time = tot_cpu;
        total_io_time = tot_io;
        sum_total_time = tot_time;
        sum_exec_rules = tot_exec;
        sfe_rules_avoided = sfe_rule;
        processorOccupation = proc_Occup;
        ioTimeOccupation = ioTimeOccup;
        sfeActivated = sfeExecuted;
        totalExecutedRules = executedRules;
        blocked_time=blck_time;
    }
    
    public String getEmailName(){
        return emailName;
    }
    
    public double getTotalCPUTime(){
        return total_cpu_time;
    }
    
    public double getTotalIOTime(){
        return total_io_time;
    }
    
    public double getSumTotalTime(){
        return sum_total_time;
    }
    
    public double getSumExecRules(){
        return sum_exec_rules;
    }
    
    public int getSFERulesAvoided(){
        return sfe_rules_avoided;
    }
    
    public Stack<Pair<Double,Rule>>[] getProccesorOccupation(){
        return processorOccupation;
    }
    
    public List<Pair<Double,Rule>> getIOTimeOccupation(){
        return ioTimeOccupation;
    }
    
    public void setEmailName(String eName){
        emailName = eName;
    }
    
    public void setTotalCPUTime(double tot_cpu){
        total_cpu_time = tot_cpu;
    }
    
    public void setTotalIOTime(double tot_io){
        total_io_time = tot_io;
    }
    
    public void setSumTotalTime(double tot_time){
        sum_total_time = tot_time;
    }
    
    public void setSumExecRules(double exec_rules){
        sum_exec_rules = exec_rules;
    }
    
    public void setSFEActivated(boolean isActivated){
        sfeActivated = isActivated;
    }
    
    public void setSFERulesAvoided(int sfe){
        sfe_rules_avoided = sfe;
    }
    
    public void setProccesorOccupation(Stack<Pair<Double,Rule>>[] proc_Occup){
        processorOccupation = proc_Occup;
    }
    
    public void setIOTimeOccupation(List<Pair<Double,Rule>> ioTimeOccup){
        ioTimeOccupation = ioTimeOccup;
    }
    
    public int getExecutedRules(){
        return totalExecutedRules;
    }
    
    public void setExecutedRules(int executedRules){
        totalExecutedRules = executedRules;
    }
    
    public boolean isSFEActivated(){
        return sfeActivated;
    }
    
    public double getBlockedTime(){
        return blocked_time;
    }
}

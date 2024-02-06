/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.benchmarker.manager;

//import java.util.Objects;

/**
 *
 * @author jf
 */


public class Rule {
    private String name;
    private String plugin;
    private double IOTime;
    private double CPUTime;
    private double totalTime;
    private double score;
    private double avgPluginOverload;

    public Rule(String name, String plugin, double IOTime, double CPUTime, double score) {
        this.name = name;
        this.plugin = plugin;
        this.IOTime = IOTime;
        this.CPUTime = CPUTime;
        this.totalTime = IOTime + CPUTime;
        this.score = score;
        this.avgPluginOverload = Math.abs(score) / ((CPUTime + IOTime)/ 2);
    }
    
    public double getAvgPluginOverload(){
        return avgPluginOverload;
    }
    
    public void setAvgPluginOverload(double avgOverload){
        avgPluginOverload = avgOverload;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public double getIOTime() {
        return IOTime;
    }

    public void setIOTime(double IOTime) {
        this.IOTime = IOTime;
    }

    public double getCPUTime() {
        return CPUTime;
    }

    public void setCPUTime(double CPUTime) {
        this.CPUTime = CPUTime;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

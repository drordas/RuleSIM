/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.benchmarker.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTextPane;
import scheduler.benchmarker.schedulers.simple.Scheduler;

/**
 *
 * @author drordas
 */
public class FullLoader {

    private  Results emails;
    private  RuleSet ruleSet;
    private  JTextPane lPanel;
    private  String requiredScore;
    private  HashMap<String,Scheduler> schedulers;
    
    public FullLoader(String ePath, String rPath, String rScore, JTextPane logPanel) {
        lPanel = logPanel;
        requiredScore = rScore;
        
        ruleSet = loadRules(rPath);
        emails = loadEmails(ePath); 
        
        schedulers = new HashMap<>();
    }
    
    public FullLoader(String ePath, String rPath, String rScore) {
        lPanel = null;
        requiredScore = rScore;
        
        ruleSet = loadRules(rPath);
        emails = loadEmails(ePath); 
        
        schedulers = new HashMap<>();
    }

    private Results loadEmails(String filepath) {
        Results toret = null;
        String cad;
        BufferedReader bf;

        JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
        JLog.insertLog(lPanel, " Loading emails from: " + filepath + "\n", JLog.INFO);
        JLog.insertLog(lPanel, "===================================\n", JLog.INFO);

        try {
            bf = new BufferedReader(new FileReader(filepath));
            toret = new Results();
            bf.readLine(); //quitar la primera línea
            while ((cad = bf.readLine()) != null) {
                String[] parts = cad.split(",");
                List<String> rules = new ArrayList<>();
                String[] parts2 = parts[2].replaceAll("[\\(\\)]", "").split("\\|");
                for (int i = 1; i < parts2.length; i++) {
                    if (existPlugin(parts2[i])) {
                        rules.add(parts2[i]);
                    } else {
                        JLog.insertLog(lPanel, " + "+parts[0]+": Rule '" + parts2[i] + "' is included but not defined. Rule ignored.\n", JLog.FAIL);
                    }
                }
                toret.insert(parts[0], rules);
            }
            bf.close();
            JLog.insertLog(lPanel, " + "+toret.getSize()+" emails succesfully loaded\n", JLog.SUCCESS);
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
        } catch (IOException e) {
            JLog.insertLog(lPanel, "Error while loading emails:\n", JLog.FAIL);
            JLog.insertLog(lPanel, e.getMessage(), JLog.FAIL);
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
        }

        return toret;
    }

    private RuleSet loadRules(String filepath) {
        RuleSet toret = null;
        String cad;
        BufferedReader bf;

        JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
        JLog.insertLog(lPanel, " Loading rules from: " + filepath + "\n", JLog.INFO);
        JLog.insertLog(lPanel, "===================================\n", JLog.INFO);

        try {
            bf = new BufferedReader(new FileReader(filepath));
            bf.readLine();
            toret = new RuleSet();
            while ((cad = bf.readLine()) != null) {
                String[] parts = cad.split(",");
                Rule rule = new Rule(parts[0], parts[3],
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[4])
                );
                toret.insertRule(parts[0], rule);
            }
            bf.close();
            toret.setRequiredScore(Double.valueOf(requiredScore));
            JLog.insertLog(lPanel, " + "+toret.getSize()+" rules succesfully loaded\n", JLog.SUCCESS);
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
        } catch (IOException | ArrayIndexOutOfBoundsException e) {

            JLog.insertLog(lPanel, "Error while loading rules:\n", JLog.FAIL);
            JLog.insertLog(lPanel, e.getMessage(), JLog.FAIL);
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);

            return toret;
        }

        return toret;
    }

    private boolean existPlugin(String ruleName) {
        return (ruleSet.getRule(ruleName) != null);
    }
    
    public void executeScheduler(String sName){
        if(ruleSet == null || ruleSet.isEmpty()){
            JLog.insertLog(lPanel, "Error: Ruleset is empty. No rules loaded\n", JLog.FAIL);
        }
        
        if(schedulers.containsKey(sName)){
            JLog.insertLog(lPanel, "Scheduler: "+sName+" is already loaded\n", JLog.FAIL);
        }else{
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
            JLog.insertLog(lPanel, "Loading scheduler: "+sName+"\n", JLog.INFO);
            try {
                Scheduler sched = (Scheduler)(Class.forName(sName).getConstructor(RuleSet.class).newInstance(ruleSet));
                sched.executeScheduler();
                //sched.toScreen();
                schedulers.put(sName, sched);
            }catch( ClassNotFoundException | IllegalAccessException | IllegalArgumentException | 
                   InvocationTargetException | NoSuchMethodException | 
                   SecurityException | InstantiationException ex)
            {
                JLog.insertLog(lPanel, "Error loading scheduler: "+ex.toString()+"\n", JLog.FAIL);
            }
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
        }
    }
    
    public Results getEmails() {
        return emails;
    }

    public RuleSet getRuleset() {
        return ruleSet;
    }
    
    public HashMap<String, Scheduler> getSchedulers(){
        return schedulers;
    }
    
    public Scheduler getSchedulerByName(String sName){
        if(schedulers.containsKey(sName)){
            return schedulers.get(sName);
        }else return null;
    }

}

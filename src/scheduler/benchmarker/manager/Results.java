/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.benchmarker.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTable;

/**
 *
 * @author jf
 */
public class Results {
    private HashMap<String,List<String>> results; 
    
    public Results(){
        results = new HashMap<>();
    }
    
    public Results(HashMap<String,List<String>> map){
        results=map;
    }

    public boolean getResultOfRuleForEmail(String emailno, String rule){
       if (results.containsKey(emailno)){
           List<String> lista = results.get(emailno);
           return lista.contains(rule);
       }
       return false;
    }

    public List<String> getEmailResult(String email){
        return results.get(email);
    }

    public Set<String> getEmails(){
        return results.keySet(); 
    }
    
    public HashMap<String,List<String>> getResults(){
        return results;
    }
    
    public void insert(String key, List<String> rules){
        results.put(key, rules);
    }
    
    public void remove(String key){
        results.remove(key);
    }
    
    public int getSize(){
        return results.size();
    }
    
    @Override
    public String toString(){
        Iterator iter = results.entrySet().iterator();
        int i=0;
        String cad = "-------------------------------------------\n"
                   + "ID   EMAIL_NAME   RULES_MATCHED            \n"
                   + "-------------------------------------------\n";
        while (iter.hasNext()){
            Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>) iter.next();
            cad += "["+ (i++) +"]  " + entry.getKey() + " -> " 
                 + entry.getValue().toString() + "\n";
        }
        return cad;
    }
    
    public JTable toTable(){
       String[] columnNames = {"ID", "EMAIL", "RULES MATCHED"};
       String[][] rowData = new String [results.size()][3];
       Iterator iter = results.entrySet().iterator();
       int i=0;
       while(iter.hasNext()){
           Map.Entry<String,List<String>> entry = (Map.Entry<String,List<String>>) iter.next();
           rowData[i][0] = String.valueOf(i);
           rowData[i][1] = entry.getKey();
           rowData[i][2] = entry.getValue().toString();
           i++;
       }
       JTable table = new JTable(rowData, columnNames);
       table.setVisible(true);
       return table;    
    }
    
}

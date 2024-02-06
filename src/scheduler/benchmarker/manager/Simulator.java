/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.benchmarker.manager;

//import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import org.jfree.chart.ChartPanel;
import scheduler.benchmarker.schedulers.simple.Scheduler;
//import org.math.plot.Plot2DPanel;

/**
 *
 * @author DrOrdas & Moncho
 */
/*
 */
public class Simulator {
    static final double LOOSE_TIME=0.002;
    private final int CPUcores;

    // Resultados
    private double blocked_time;
    private double sum_total_time;
    private double sum_exec_rules;
    private double total_io_time;
    private double total_cpu_time;
    private int total_sfe;
    private final JTextPane lPanel;
    private boolean sfe_out;
    private int numberOfExecutedRules;
    private final PluginColors pColors;
    private final HashMap <String,Pair<ArrayList<PlanningResult>,SimulatorResults>> schedulers;
    
    private Stack<Pair<Double,Rule>> processorOccupation[];
    private List<Pair<Double,Rule>> ioTimeFinished;
    private final FullLoader loadedValues;

    public Simulator(FullLoader val, int cpuCores, JTextPane logInfo) {
        CPUcores = cpuCores;
        loadedValues = val;
        processorOccupation= null;
        ioTimeFinished = null;
        sfe_out = false;
        numberOfExecutedRules = 0;
        pColors = new PluginColors();
        lPanel = logInfo;
        schedulers = new HashMap();
    }

    // Tiempo de ejecución y número de reglas ejecutadas
    private void getSimulatedTimeForPlan(Filter filter, List<String> results) {
        sum_total_time=0D; //Lo que tarda en total.
        sum_exec_rules=0D; // Numero de reglas ejecutadas.
        total_io_time=0D; // Lo que ha tardado en tiempo de IO.
        total_cpu_time=0D; // Lo que ha tardado en tiempo de CPU.
        total_sfe=0; //Numero de reglas no ejecutadas gracias al SFE.
        blocked_time=0D; //Tiempo de bloqueos de la cola de reglas.
        sfe_out = false;
        
        double required_score = loadedValues.getRuleset().getRequiredScore();
        double pending_add_initial = loadedValues.getRuleset().getPendingAdd();
        double pending_substract_initial = loadedValues.getRuleset().getPendingSubstract();
        
        //Cores
        //Para cada CPU hay que guardar el momento en el que empieza el tiempo de CPU de una regla y la regla para saber cuando va 
        //a terminar
        processorOccupation=(Stack<Pair<Double,Rule>> [])new Stack[CPUcores]; 
        for (int i=0;i<processorOccupation.length;i++) processorOccupation[i]=new Stack<>();            

        //Para cada uno de los tiempos de IO se va a guardar cuando se empezó a esperar y la regla que lo generó para saber cuando 
        //terminamos
        List<Pair<Double,Rule>> ioTimeInProcess=new LinkedList<>();
        //Cuando se termine una espera (IO) vamos a colocarla en el vector de esperas terminadas (para temas de información)
        ioTimeFinished=new LinkedList<>();
        
        /*********************************
        VAMOS POR LAS REGLAS
        *************************************/
        
        double currentTime=0; //Esta variable representa el tiempo que ha pasado desde que se ha empezado a ejecutar el filtro
        //boolean sfe_out=false;
        for (Iterator<Rule> iter = filter.getRules().iterator(); iter.hasNext();) {
            
            Rule rule = (iter.next());
            
            //GENERAR VECTOR DE COLORES PARA LOS PLUGINS
            pColors.addValues(rule.getName(), rule.getPlugin());
            
            /******* COMPROBAR SFE ********************************/
            double pending_add=pending_add_initial;
            double pending_substract=pending_substract_initial;
            double current_score=0;
            numberOfExecutedRules=0;
            for (Stack<Pair<Double,Rule>> currentPO:processorOccupation){
            	for (Pair<Double,Rule> ocupation:currentPO){
                    if (ocupation.getElementX()+ocupation.getElementY().getTotalTime()<=currentTime){ //si es una actividad finalizada
                        numberOfExecutedRules++;

                        if (results.contains(ocupation.getElementY().getName())) //Si la regla encaja
                           current_score+=ocupation.getElementY().getScore(); //sumo scores

                        if(ocupation.getElementY().getScore()>0){ //actualizo pending_add y pending_substract
                            pending_add-=ocupation.getElementY().getScore();
                        }else pending_substract-=ocupation.getElementY().getScore();
                    }
            	}
            }
                      
            if (current_score < required_score - pending_add || //condicion SFE
                current_score >= required_score - pending_substract )
            {
                sfe_out=true;
                break; //del bucle for que itera sobre las reglas
            }
            
            /******* BLOQUEO 2 REGLAS DEL MISMO PLUGIN ************/
            
            //Tengo que generar tiempo idle si hay una regla del mismo plugin ejecutándose.
            //Repaso todas las reglas en ejecución y si encuentro una que no se ha terminado de ejecutar y es del mismo
            //   plugin que la regla que voy a meter, tengo que esperar a que esta regla termine
            for (int i=0;i<CPUcores;i++){
            	if (!processorOccupation[i].empty()){
                    Pair<Double,Rule> lastProcess=processorOccupation[i].peek();

                    //Si el proceso actual es del mismo plugin y además no se acabó de ejecutar
                    //System.out.println("lastProcess is of Plugin: "+lastProcess.getElementY().getPlugin()+" rule plugin: "+rule.getPlugin());
                    if ( lastProcess.getElementY().getPlugin().equals(rule.getPlugin()) &&
                         currentTime<=(lastProcess.getElementX()+lastProcess.getElementY().getCPUTime()))
                    {
                        blocked_time+=((lastProcess.getElementX()+lastProcess.getElementY().getCPUTime())- currentTime);
                        currentTime=lastProcess.getElementX()+lastProcess.getElementY().getCPUTime(); //Compute amount of bloked time
                        //System.out.println("      Waiting for the execution of "+lastProcess.getElementY().getName()+" until "+currentTime);
                    }
            	}
            }

            
            //Hacer lo mismo con IO.
            Iterator<Pair<Double,Rule>> iterIO = ioTimeInProcess.iterator();
            while ( iterIO.hasNext()){
            	Pair<Double,Rule> ioEntry=iterIO.next();
            	if (ioEntry.getElementX()+ioEntry.getElementY().getIOTime() <= currentTime){
            	    ioTimeFinished.add(ioEntry);
            	    iterIO.remove();
            	}else{
                    if (ioEntry.getElementY().getPlugin().equals(rule.getPlugin())){
                        ioTimeFinished.add(ioEntry);
                        iterIO.remove();	            		
                        currentTime=(ioEntry.getElementX()+ioEntry.getElementY().getIOTime()>currentTime?ioEntry.getElementX()+ioEntry.getElementY().getIOTime():currentTime);
                    }            		
            	}
            	
            }
            
            /******* BUSCAR HUECO PARA EJECUTAR LA REGLA Y PA DENTRO ************/
            
            //Actualizar el currentTime al primer momento que se queda una CPU libre y buscar el procesador donde se va a ejecutar
            double menor=Double.MAX_VALUE; //Es el momento en el que podré meter la regla (suponiendo que ejecuto la regla en el procesador
               //que termine antes.
            int selectedCPU=-1;   //Es el número de CPU que va a ejecutar la tarea
            for (int i=0;i<CPUcores;i++){
            	if (processorOccupation[i].empty()){
                    menor=currentTime;
                    selectedCPU=i;
                    break;
            	}
            	
                Pair<Double,Rule> lastProcess=processorOccupation[i].peek();
                if (menor > lastProcess.getElementX()+lastProcess.getElementY().getCPUTime()){
                    menor=lastProcess.getElementX()+lastProcess.getElementY().getCPUTime();
                    selectedCPU=i;
                }
            }
            
            if(currentTime>menor) currentTime+=LOOSE_TIME;
            else currentTime=menor+LOOSE_TIME;
            
            //Ponemos a ejecutar la regla
            processorOccupation[selectedCPU].push(new Pair<>(currentTime,rule));
            //Colocamos el IO de la regla
            if(rule.getIOTime() > 0){
                ioTimeInProcess.add(new Pair<>(currentTime+rule.getCPUTime(),rule));
            }
        }

        //AQUI QUEDA HACER EL POSTPROCESSING PARA CUANDO SE HAYAN METIDO TODAS LAS REGLAS PERO... TODAVÍA NO SE HAYA
        //LLEGADO A UNA SOLUCIÓN FINAL (PORQUE NO HA SALTADO SFE)        
        if (!sfe_out){
            for (Stack<Pair<Double,Rule>> currentProcessorOccupation: processorOccupation)
                for(Pair<Double,Rule> ocupation: currentProcessorOccupation)
                    if (ocupation.getElementX()+ocupation.getElementY().getTotalTime() > currentTime)
                         currentTime=ocupation.getElementX()+ocupation.getElementY().getTotalTime();
        
            Iterator<Pair<Double,Rule>> iterIO=ioTimeInProcess.iterator();
            while(iterIO.hasNext()){
                Pair<Double,Rule> iotime=iterIO.next();
                if (iotime.getElementX()+iotime.getElementY().getIOTime() > currentTime)
                     currentTime=iotime.getElementX()+iotime.getElementY().getIOTime();
                ioTimeFinished.add(iotime);
                iterIO.remove();
            }
        }else{
            Iterator<Pair<Double,Rule>> iterIO=ioTimeInProcess.iterator();
            while(iterIO.hasNext()){
                Pair<Double,Rule> iotime=iterIO.next();
                ioTimeFinished.add(iotime);
                iterIO.remove();
            }            
        }
        
        //En este momento tenemos ya la solución aunque puede haber reglas que no se han terminado
        //currentTime es lo que tardó en total
        sum_total_time=currentTime;
        
        //Los calculos de la CPU hay que hacerlos iterando sobre el array de stacks processorOccupation 
        total_cpu_time=0;
        for (Stack<Pair<Double,Rule>> currentProcessorOccupation: processorOccupation){
            double consumo=0;
            for(Pair<Double,Rule> ocupation: currentProcessorOccupation){
                if (ocupation.getElementX()+ocupation.getElementY().getCPUTime() <= currentTime)
                   consumo+=ocupation.getElementY().getCPUTime();
                else consumo+=(currentTime-ocupation.getElementX());
            }
            total_cpu_time=(consumo > total_cpu_time)?consumo:total_cpu_time;
        }
	        	         
        //Los cálculos de la IO se harán sólo si hace falta
        //TOCAR AQUI. ESTO NO TIENE EN CUENTA QUE LOS IO PUEDEN ESTAR SOLAPADOS.
        //NO SERIA MEJOR HACERLO DESPUES DEL SORT?? (LINEA 258)
//        total_io_time=0; 
//        Iterator<Pair<Double,Rule>> iter = ioTimeFinished.iterator();
//        while (iter.hasNext()) {
//            Pair<Double,Rule> element = iter.next();
//            total_io_time+=element.getElementY().getIOTime();
//        }
        
        //Los calculos de suma de los tiempos de ejecución se harán sólo si hace falta
        sum_exec_rules=(sfe_out)?numberOfExecutedRules:filter.getRules().size(); 
        
        //El número de reglas ejecutadas es numberOfExecutedRules y se puede usar para SFE
        if (sfe_out)
            total_sfe=filter.getRules().size()-numberOfExecutedRules;
        else total_sfe=0;
        
        //Ordenar los ioTimeFinished por el momento en el que empiezan esos IO. 
        //De esta forma será más fácil agruparlos para obtener una representación
        //gráfica
        
        Collections.sort(ioTimeFinished,new Comparator<Pair<Double, Rule>>(){
            @Override
            public int compare(Pair<Double, Rule> o1, Pair<Double, Rule> o2) {
                return o1.getElementX()>o2.getElementX()?1:(o1.getElementX()<o2.getElementX()?-1:0);
            }
        });
        
        total_io_time = 0D; 
        double endTime = 0D;
//        double beginTime = 0D;
//        System.out.println("================ IO TIME STATUS ================");
//        for(Pair<Double, Rule> r: ioTimeFinished){
//            System.out.println("RULE: "+ r.getElementY().getName() +"\n\tINIT: " + r.getElementX() + "\n\tEND: " + (r.getElementY().getIOTime()+r.getElementX())+"\n\tDURATION: "+r.getElementY().getIOTime());
//        }
//        System.out.println();
//        System.out.println("================       END      ================\n");
        
        Iterator<Pair<Double,Rule>> iter = ioTimeFinished.iterator();
//        System.out.println("================COMPUTING IO TIME================");
        while (iter.hasNext()) {
            Pair<Double,Rule> ioElement = iter.next();
//            System.out.println("RULE: "+ ioElement.getElementY().getName());
            if(endTime==0D){
                endTime = ioElement.getElementY().getIOTime()+ioElement.getElementX();
//              beginTime = ioElement.getElementX();
                total_io_time = ioElement.getElementY().getIOTime();
//                System.out.println("(endTime==0D)");
//                System.out.println("endTime="+endTime);
//                System.out.println("io="+ioElement.getElementY().getIOTime());
            }
            else{
                if(ioElement.getElementX() < endTime){
                    total_io_time += (ioElement.getElementX() + ioElement.getElementY().getIOTime()) - endTime;
//                    System.out.println("(ioElement.getElementX() < endTime)");
//                    System.out.println("endTime= "+endTime);
//                    System.out.println("io= ("+(ioElement.getElementX() + ioElement.getElementY().getIOTime())+" - "+endTime+")= " + ((ioElement.getElementX() + ioElement.getElementY().getIOTime()) - endTime));
                    endTime = ioElement.getElementX() + ioElement.getElementY().getIOTime();
                }else{
                    endTime = ioElement.getElementX() + ioElement.getElementY().getIOTime();
                    total_io_time += endTime - ioElement.getElementX();
//                    System.out.println("(ioElement.getElementX() >= endTime)");
//                    System.out.println("endTime= "+endTime);
//                    System.out.println("io= ("+ endTime + "-" + ioElement.getElementX()+") = "+(endTime - ioElement.getElementX()));
                }
            }
//            System.out.println(" + BEGIN: "+ beginTime);
//            System.out.println(" + END: "+ endTime);
//            System.out.println(" + IO: "+ total_io_time);
        }
//        System.out.println("FINAL IO TIME: "+ total_io_time);
//        System.out.println("================       END       ================");
    }
    
    public void beginSimulation(){
        Set<String> emails = loadedValues.getEmails().getEmails();
        
        for (Iterator<Scheduler> it = loadedValues.getSchedulers().values().iterator(); 
            it.hasNext();) 
        {
            Scheduler sched = it.next();
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
            JLog.insertLog(lPanel, "Executing simulation for: '"+sched.getName()+"'.......\n", JLog.INFO);
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
            ArrayList<PlanningResult> emailPlanning = new ArrayList<>();
            for(String email: emails){
                JLog.insertLog(lPanel, "+ Simulating behaviour of: '"+email+"'\n", JLog.INFO);
                getSimulatedTimeForPlan(sched.getPlan(), loadedValues.getEmails().getEmailResult(email));
                emailPlanning.add(new PlanningResult(email,total_cpu_time, total_io_time, 
                                                    sum_total_time, sum_exec_rules, 
                                                    total_sfe,sfe_out,numberOfExecutedRules,
                                                    processorOccupation,
                                                    ioTimeFinished, blocked_time));
            }
            schedulers.put(sched.getName(), new Pair(emailPlanning, new SimulatorResults(emailPlanning, pColors,sched.getName())));
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
            JLog.insertLog(lPanel, "Finish Simulation for '"+sched.getName()+"'\n", JLog.SUCCESS);
            JLog.insertLog(lPanel, "===================================\n", JLog.INFO);
        }  
    }
    
    private SimulatorResults getResultsForScheduler(String schedName){
        if(schedulers.containsKey(schedName)){
            return schedulers.get(schedName).getElementY();
        }
        return null;
    }
    
    public List<String> getExecutedSchedulers(){
        return new ArrayList<>(schedulers.keySet());
    }
    
    public List<SimulatorResults> getSimulatorResults(){
        
        if (schedulers.values().isEmpty()) return null;
        
        ArrayList<SimulatorResults> sResults = new ArrayList<>();
        
        for(Pair<ArrayList<PlanningResult>,SimulatorResults> p : schedulers.values()){
           sResults.add(p.getElementY());
        }
        
        return sResults;
    }
    
    public boolean isExecuted(){
        return !(schedulers.isEmpty());
    }
    
    public void plotResults(JPanel panel, String sName){  
        // based on the dataset we create the chart
        SimulatorResults results = getResultsForScheduler(sName);
        CreateSimpleSplineChart splineChart = new CreateSimpleSplineChart(sName,results);
        ChartPanel chartPanel = splineChart.createChartPanel();
        chartPanel.setSize(panel.getSize());
        chartPanel.setBackground(panel.getBackground());
        chartPanel.setForeground(panel.getForeground());
        chartPanel.setVisible(true);
        panel.removeAll();
        panel.add(chartPanel);
        panel.validate();
        panel.setVisible(true);
    }
    
    public void combinedPlotResults(JPanel panel, String[] schedulers){
        List<SimulatorResults> simulatorRes = getSimulatorResults();
        CreateCombinedSplineChart combinedChart = new CreateCombinedSplineChart(schedulers, simulatorRes);
        ChartPanel chartPanel = combinedChart.createChartPanel();
        chartPanel.setSize(panel.getSize());
        chartPanel.setBackground(panel.getBackground());
        chartPanel.setForeground(panel.getForeground());
        panel.removeAll();
        panel.add(chartPanel);
        panel.validate();
        panel.setVisible(true);
    }
}
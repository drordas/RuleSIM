/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.manager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jfree.chart.ChartPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;


public class CreateCombinedCategoryPlot {

    private final PlanningResult[] dataSource;
    private final String[] schedNames;
    private final String title;
    private ChartPanel cPanel;
    private final PluginColors pluginColors;
    
    public CreateCombinedCategoryPlot(PlanningResult [] plans,
                                    PluginColors[] colors, String chartTitle, 
                                    String[] sNames)
    {
        dataSource = plans;
        title = chartTitle;
        cPanel = null;
        
        pluginColors = colors[0];
        
        schedNames = sNames;
    }
    
    
    private CategoryDataset createDataset1(){
        
        DefaultCategoryDataset defaultDataset = new DefaultCategoryDataset();
        for(int i=0;i<dataSource[0].getProccesorOccupation().length;i++){
            String cpuNumber = "CPU "+ (i+1);
            double initPosition=0D;
            double idleTime;
            Iterator<Pair<Double,Rule>> cpu = dataSource[0].getProccesorOccupation()[i].iterator();

            while(cpu.hasNext()){
                Pair<Double,Rule> rulePlan = cpu.next();
                if(initPosition < rulePlan.getElementX()){
                    idleTime = rulePlan.getElementX()-initPosition;
                    defaultDataset.addValue(idleTime, "IDLE: "+rulePlan.getElementY().getName(), cpuNumber);
                    initPosition = rulePlan.getElementX();
                }
                defaultDataset.addValue(rulePlan.getElementY().getCPUTime(), rulePlan.getElementY().getName(), cpuNumber);
                initPosition = initPosition+rulePlan.getElementY().getCPUTime();
            }
        }
        
        HashMap <String,Double> initPosition=new HashMap<>();
        //double initPosition = 0D;
        double idleTime;
        Iterator<Pair<Double,Rule>> io = dataSource[0].getIOTimeOccupation().iterator();
        
        while ( io.hasNext() ){
            Pair<Double,Rule> rulePlan = io.next();
            double ipv=initPosition.containsKey(rulePlan.getElementY().getPlugin())?initPosition.get(rulePlan.getElementY().getPlugin()):0D;
            if(ipv < rulePlan.getElementX()){
                idleTime = rulePlan.getElementX()-ipv;
                defaultDataset.addValue(idleTime, "IDLE: "+rulePlan.getElementY().getName(), "IO: "+rulePlan.getElementY().getPlugin());
                ipv = rulePlan.getElementX();
            }
            defaultDataset.addValue(rulePlan.getElementY().getIOTime(), rulePlan.getElementY().getName(), "IO: "+rulePlan.getElementY().getPlugin());
            initPosition.put(rulePlan.getElementY().getPlugin(), ipv+rulePlan.getElementY().getIOTime());
            
        }
        
        return defaultDataset;
    }
    
    
    private CategoryDataset createDataset2(){
        
        DefaultCategoryDataset defaultDataset = new DefaultCategoryDataset();
        for(int i=0;i<dataSource[1].getProccesorOccupation().length;i++){
            String cpuNumber = "CPU "+ (i+1);
            double initPosition=0D;
            double idleTime;
            Iterator<Pair<Double,Rule>> cpu = dataSource[1].getProccesorOccupation()[i].iterator();

            while(cpu.hasNext()){
                Pair<Double,Rule> rulePlan = cpu.next();
                if(initPosition < rulePlan.getElementX()){
                    idleTime = rulePlan.getElementX()-initPosition;
                    defaultDataset.addValue(idleTime, "IDLE: "+rulePlan.getElementY().getName(), cpuNumber);
                    initPosition = rulePlan.getElementX();
                }
                defaultDataset.addValue(rulePlan.getElementY().getCPUTime(), rulePlan.getElementY().getName(), cpuNumber);
                initPosition = initPosition+rulePlan.getElementY().getCPUTime();
            }
        }
        
        HashMap <String,Double> initPosition=new HashMap<>();
        //double initPosition = 0D;
        double idleTime;
        Iterator<Pair<Double,Rule>> io = dataSource[1].getIOTimeOccupation().iterator();
        
        while ( io.hasNext() ){
            Pair<Double,Rule> rulePlan = io.next();
            double ipv=initPosition.containsKey(rulePlan.getElementY().getPlugin())?initPosition.get(rulePlan.getElementY().getPlugin()):0D;
            if(ipv < rulePlan.getElementX()){
                idleTime = rulePlan.getElementX()-ipv;
                defaultDataset.addValue(idleTime, "IDLE: "+rulePlan.getElementY().getName(), "IO: "+rulePlan.getElementY().getPlugin());
                ipv = rulePlan.getElementX();
            }
            defaultDataset.addValue(rulePlan.getElementY().getIOTime(), rulePlan.getElementY().getName(), "IO: "+rulePlan.getElementY().getPlugin());
            initPosition.put(rulePlan.getElementY().getPlugin(), ipv+rulePlan.getElementY().getIOTime());
            
        }
        
        return defaultDataset;
    }


    public ChartPanel createChartPanel(){
        CustomBarRenderer renderer = new CustomBarRenderer(pluginColors);
        CategoryPlot subplot1 = new CategoryPlot(createDataset1(), new CategoryAxis("Category"), new NumberAxis("Value"), renderer);
        subplot1.setDomainGridlinesVisible(true);
        
        CategoryPlot subplot2 = new CategoryPlot(createDataset2(), new CategoryAxis("Category"), new NumberAxis("Value"), renderer);
        subplot2.setDomainGridlinesVisible(true);
        
        final CategoryAxis domainAxis = new CategoryAxis("Category");
        final CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainAxis);
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        plot.setFixedLegendItems(createCustomLegend());
        plot.setRenderer(renderer);
        
        subplot1.setBackgroundPaint(new Color(246,244,242));
        subplot2.setBackgroundPaint(new Color(246,244,242));
        
        subplot1.addRangeMarker(generateMarker("CLASSIFICATION FINISH FOR '"+ schedNames[0] +"'", dataSource[0].getSumTotalTime()),Layer.FOREGROUND);
        subplot2.addRangeMarker(generateMarker("CLASSIFICATION FINISH FOR '"+ schedNames[1] +"'", dataSource[1].getSumTotalTime()),Layer.FOREGROUND);
        
        final JFreeChart result = new JFreeChart(
            title,
            new Font("SansSerif", Font.BOLD, 12),
            plot,
            true
        );
        
        cPanel = new ChartPanel(result);
        cPanel.setForeground(new Color(76,76,76));
        cPanel.setBackground(new Color(246,244,242));
        
        return cPanel;
        
    }
    
    private ValueMarker generateMarker(String title, double value){
        ValueMarker marker = new ValueMarker(value);
        marker.setLabel(title);
        marker.setPaint(Color.RED);
        marker.setLabelPaint(Color.RED);
        marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        marker.setLabelFont(new Font(Font.SERIF,Font.BOLD,10));
        return marker;
    }
    
    private LegendItemCollection createCustomLegend(){
        LegendItemCollection legend = new LegendItemCollection();
        Shape shape = new Rectangle(10,10);
        Stroke stroke = new BasicStroke(1F);
        HashMap<String, Color> pColors = pluginColors.getPlugins();
        Iterator it = pColors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            legend.add(new LegendItem( String.valueOf(pairs.getKey()),null, null, 
                                       null, shape, (Color)pairs.getValue(),stroke,Color.BLACK));        
        }        
        return legend;
    }
    
    private LegendItemCollection createLegend(){
        LegendItemCollection legend = new LegendItemCollection();
        Shape shape = new Rectangle(10,10);
        Stroke stroke = new BasicStroke(1F);
        return legend;
    }
    
    public ChartPanel getChartPanel(){
        return cPanel;
    }
}

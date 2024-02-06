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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author drordas
 */
public class CreateStackedBarChart3D {
    
    private final PlanningResult dataSource;
    private final String title;
    private ChartPanel cPanel;
    private final PluginColors pluginColors;
    
    public CreateStackedBarChart3D( PlanningResult planning,
                                    PluginColors colors, 
                                    String chartTitle)
    {
        dataSource = planning;
        title = chartTitle;
        cPanel = null;
        pluginColors = colors;
    }
    
    private CategoryDataset createDataset(){
        
        DefaultCategoryDataset defaultDataset = new DefaultCategoryDataset();
        for(int i=0;i<dataSource.getProccesorOccupation().length;i++){
            String cpuNumber = "CPU "+ (i+1);
            double initPosition=0D;
            double idleTime;
            Iterator<Pair<Double,Rule>> cpu = dataSource.getProccesorOccupation()[i].iterator();

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
        Iterator<Pair<Double,Rule>> io = dataSource.getIOTimeOccupation().iterator();
        
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
        JFreeChart jfreechart = ChartFactory.createStackedBarChart3D(title, 
                "Category", "Value", createDataset(), PlotOrientation.HORIZONTAL, 
                true, true, false);
        jfreechart.setBackgroundPaint(new Color(214,217,223));
        
        CustomBarRenderer cRenderer = new CustomBarRenderer(pluginColors);
        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
        
        ValueMarker marker = new ValueMarker(dataSource.getSumTotalTime());

        marker.setLabel("CLASSIFICATION FINISH");
        marker.setPaint(Color.RED);
        marker.setLabelPaint(Color.RED);
        marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        marker.setLabelOffsetType(LengthAdjustmentType.EXPAND);
        marker.setLabelFont(new Font(Font.SERIF,Font.BOLD,12));
        
        categoryplot.addRangeMarker(marker,Layer.FOREGROUND);
        categoryplot.setFixedLegendItems(createCustomLegend());
        categoryplot.setRenderer(cRenderer);
        cPanel = new ChartPanel(jfreechart,true);
        return cPanel;
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
    
    public ChartPanel getChartPanel(){
        return cPanel;
    }
    
}

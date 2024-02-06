/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.manager;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.*;

public class CreateSimpleSplineChart
{
    
    private final ArrayList<PlanningResult> dataSource;
    private final PluginColors pluginColor;
    private final String sName;
    ChartPanel chartPanel;
    
    public CreateSimpleSplineChart( String scheduler, SimulatorResults results)
    {
        dataSource = results.getPlanningResult();
        pluginColor = results.getRuleColors();
        chartPanel = null;
        sName = scheduler;
    }
    
    private XYDataset createDataset(){
        XYSeries cpuTime = new XYSeries(sName+ ": CPU");
        XYSeries ioTime = new XYSeries(sName+ ": IO");
        double i=0D;
        
        for (PlanningResult planningResult : dataSource) {
            cpuTime.add(i,planningResult.getTotalCPUTime());
            ioTime.add(i,planningResult.getTotalIOTime());
            i++;
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(cpuTime);
        dataset.addSeries(ioTime);
        return dataset;
    }
    
    public ChartPanel createChartPanel(){
        XYDataset dataset = createDataset();
        NumberAxis numberaxis = new NumberAxis("EMAILS");
        numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setRange(0, dataset.getItemCount(1));
        numberaxis.setVisible(false);
        NumberAxis numberaxis1 = new NumberAxis("TIME CONSUMED");
        numberaxis1.setAutoRangeIncludesZero(false);
        XYSplineRenderer xysplinerenderer = new XYSplineRenderer();
        XYPlot xyplot = new XYPlot(dataset, numberaxis, numberaxis1, xysplinerenderer);
        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        //xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
        JFreeChart jfreechart = new JFreeChart("PLAN VALUES FOR '"+sName+"' SCHEDULER", new Font(Font.SANS_SERIF, Font.PLAIN, 11), xyplot, true);
        chartPanel = new ChartPanel(jfreechart, true);
        
        //Creating listener
        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent e) {
                ChartEntity entity = e.getEntity();
                if(entity != null && (entity instanceof XYItemEntity)){
                    XYItemEntity item = (XYItemEntity)entity;
                    String chartTitle = "RULE ARRANGEMENT INFORMATION FOR EMAIL \"" + dataSource.get(item.getItem()).getEmailName() + "\"";
                    createSubChart(new CreateStackedBarChart3D(dataSource.get(item.getItem()),pluginColor,chartTitle).createChartPanel());
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent e) {
                //DO NOTHING
            }
            
        });
        return chartPanel;
    }
    
    private void createSubChart(ChartPanel chart){
        JFrame frameGraph = new JFrame();//new JFrame("FINAL RULES ARRANGEMENT");
        frameGraph.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameGraph.setForeground(new Color(76,76,76));
        frameGraph.setBackground(new Color(246,244,242));

        Dimension window = Toolkit.getDefaultToolkit().getScreenSize();
        if(window.width < 1074 && window.height<800)
            frameGraph.setPreferredSize(new Dimension(window.width, window.height));
        else frameGraph.setPreferredSize(new Dimension(1074, 800));
        
        frameGraph.setLocation( (window.width-frameGraph.getPreferredSize().width)/2,
                                (window.height-frameGraph.getPreferredSize().height)/2);
        frameGraph.setResizable(true);
        frameGraph.add(chart);
        frameGraph.pack();
        frameGraph.setVisible(true);
    }
    
}

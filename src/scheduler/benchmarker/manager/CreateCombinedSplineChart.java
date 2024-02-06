/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.manager;

import java.awt.*;
import java.util.List;
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

public class CreateCombinedSplineChart
{
    
    private final List<SimulatorResults> dataSource;
    private final PluginColors[] pluginColor;
    private final String[] sName;
    ChartPanel chartPanel;
    
    public CreateCombinedSplineChart( String[] schedulers, List<SimulatorResults> sResults)
    {
        dataSource = sResults;
        chartPanel = null;
        sName = schedulers;
        pluginColor = new PluginColors[]{sResults.get(0).getRuleColors(),sResults.get(1).getRuleColors()};
    }
    
    private XYDataset createDataset(){
        XYSeriesCollection dataset = new XYSeriesCollection();

        int index=0;
        for (SimulatorResults simulatedResult : dataSource) {
            XYSeries cpuTime = new XYSeries(sName[index]+": CPU");
            XYSeries ioTime = new XYSeries(sName[index]+ ": IO");
            double i=0D;
            for( PlanningResult p: simulatedResult.getPlanningResult()){
                cpuTime.add(i,p.getTotalCPUTime());
                ioTime.add(i,p.getTotalIOTime());
                i++;
            }
            dataset.addSeries(cpuTime);
            dataset.addSeries(ioTime);
            index++;
        }
        return dataset;
    }
    
    public ChartPanel createChartPanel(){
        XYDataset dataset = createDataset();
        NumberAxis numberaxis = new NumberAxis("EMAILS");
        numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setRange(0, dataset.getItemCount(1));
        numberaxis.setVisible(false);
        NumberAxis numberaxis1 = new NumberAxis("TIME CONSUMED");
        numberaxis.setAutoRangeIncludesZero(false);
        XYSplineRenderer xysplinerenderer = new XYSplineRenderer();
        XYPlot xyplot = new XYPlot(dataset, numberaxis, numberaxis1, xysplinerenderer);
        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setFixedLegendItems(null);
        JFreeChart jfreechart = new JFreeChart("PLAN VALUES FOR '"+sName[0]+"' AND '"+ sName[1]+"' SCHEDULERS", new Font(Font.SANS_SERIF, Font.PLAIN, 11), xyplot, true);
        chartPanel = new ChartPanel(jfreechart, true);
        
        //Creating listener
        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent e) {
                ChartEntity entity = e.getEntity();
                if(entity != null && (entity instanceof XYItemEntity)){
                    XYItemEntity item = (XYItemEntity)entity;
                    
                    String chartTitle = "COMPARISON OF '"+sName[0]+"' AND '"+sName[1]+"' BEHAVIOUR FOR EMAIL '" + dataSource.get(0).getPlanningResult().get(item.getItem()).getEmailName() + "'";
                    createSubChart(new CreateCombinedCategoryPlot( 
                         new  PlanningResult[]{ dataSource.get(0).getPlanningResult().get(item.getItem()),
                                                dataSource.get(1).getPlanningResult().get(item.getItem())
                                              },pluginColor,chartTitle,new String[]{dataSource.get(0).getSchedulerUsed(),dataSource.get(1).getSchedulerUsed()}).createChartPanel());
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
        JFrame frameGraph = new JFrame();
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

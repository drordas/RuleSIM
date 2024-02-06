/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.manager;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author drordas
 */
public class CustomBarRenderer extends StackedBarRenderer3D {
    
    private final PluginColors pluginColors;
    
    public CustomBarRenderer(PluginColors colors) {
        //super();
        pluginColors = colors;
        
        setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        setBaseItemLabelsVisible(true);
        setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    }
    
    @Override
    public Paint getItemPaint(int x_row, int x_col) {
        CategoryDataset cDataset = getPlot().getDataset();
        String ruleName = (String)cDataset.getRowKey(x_row);        
        return pluginColors.getColorForRule(ruleName);
    }
    
    @Override
    public Paint getItemLabelPaint(int x_row, int x_col) {
        CategoryDataset cDataset = getPlot().getDataset();
        String ruleName = (String)cDataset.getRowKey(x_row);  
        if (ruleName.startsWith("IDLE")) return pluginColors.getColorForRule("IDLE");
        else return Color.BLACK;
    }
}

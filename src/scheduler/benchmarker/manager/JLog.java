/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.manager;

import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author drordas
 */
public class JLog {
    
    private static final StyleContext style = StyleContext.getDefaultStyleContext();
    public static final AttributeSet FAIL = style.addAttribute( style.getEmptySet(), 
                                                     StyleConstants.Foreground, Color.RED);
    public static final AttributeSet SUCCESS = style.addAttribute( style.getEmptySet(), 
                                                  StyleConstants.Foreground, Color.BLUE);
    public static final AttributeSet INFO = style.addAttribute( style.getEmptySet(), 
                                                  StyleConstants.Foreground, Color.BLACK);
    
    
    public static void insertLog(JTextPane logPanel, String logInfo, AttributeSet attr){
        DefaultStyledDocument sDoc;
        
        if( !(logPanel.getDocument() instanceof DefaultStyledDocument) ||
            (sDoc = (DefaultStyledDocument)logPanel.getDocument()) == null )
        {
            sDoc = new DefaultStyledDocument();
        }
        
        try {
            sDoc.insertString(logPanel.getCaretPosition(), logInfo, attr);
        } catch (BadLocationException ex) {
            JOptionPane.showMessageDialog( null, ex.toString(), "Error", 
                                           JOptionPane.ERROR_MESSAGE );
        }
    } 
    
}

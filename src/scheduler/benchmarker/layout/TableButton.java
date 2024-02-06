/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scheduler.benchmarker.layout;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author drordas
 */

public class TableButton extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
{
    private static final long serialVersionUID = 5647725208335645741L;

    public interface TableButtonPressedHandler
    {
        /**
         * Called when the button is pressed.
         * @param row The row in which the button is in the table.
         * @param column The column the button is in in the table.
         */
        void onButtonPress(int row, int column);
    }

    private final List<TableButtonPressedHandler> handlers;
    private final Hashtable<Integer, JButton> buttons;

    public TableButton()
    {
        handlers = new ArrayList<>();
        buttons = new Hashtable<>();
    }

    /**
     * Add a slide callback handler
     * @param handler
     */
    public void addHandler(TableButtonPressedHandler handler)
    {
        if (handlers != null)
        {
            handlers.add(handler);
        }
    }

    /**
     * Remove a slide callback handler
     * @param handler
     */
    public void removeHandler(TableButtonPressedHandler handler)
    {
        if (handlers != null)
        {
            handlers.remove(handler);
        }
    }


    /**
     * Removes the component at that row index
     * @param row The row index which was just removed
     */
    public void removeRow(int row)
    {
    	if(buttons.containsKey(row))
        {
            buttons.remove(row);
        }
    }

    /**
     * Moves the component at oldRow index to newRow index
     * @param oldRow The old row index
     * @param newRow THe new row index
     */
    public void moveRow(int oldRow, int newRow)
    {
    	if(buttons.containsKey(oldRow))
        {
    		JButton button = buttons.remove(oldRow);
    		buttons.put(newRow, button);
        }
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, final int row, final int column)
    {
        JButton button = null;
        if(buttons.containsKey(row))
        {
            button = buttons.get(row);
        }
        else
        {
            button = new JButton();
            if(value != null && value instanceof String)
            {
                button.setText((String)value);
            }
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if(handlers != null)
                    {
                        for(TableButtonPressedHandler handler : handlers)
                        {
                            handler.onButtonPress(row, column);
                        }
                    }
                }
            });
            buttons.put(row, button);
        }

        return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column)
    {
        JButton button = null;
        if(buttons.containsKey(row))
        {
            button = buttons.get(row);
        }
        else
        {
            button = new JButton();
            if(value != null && value instanceof String)
            {
                button.setText((String)value);
            }

            buttons.put(row, button);
        }

        return button;
    }

    public void setButtonText(int row, String text)
    {
        JButton button = null;
        if(buttons.containsKey(row))
        {
            button = buttons.get(row);
            button.setText(text);
        }
    }

    @Override
    public Object getCellEditorValue()
    {
        return null;
    }

    public void dispose()
    {
        if (handlers != null)
        {
            handlers.clear();
        }
    }
}

/*
 * Created on 15 fvr. 2004
 */
package b01.foc.gui.table.cellControler.editor;

import java.awt.Component;
import javax.swing.*;

import b01.foc.gui.table.FTable;

import java.awt.event.*;
import java.util.EventObject;

/**
 * @author 01Barmaja
 */
public class FDefaultCellEditor extends DefaultCellEditor implements FocusListener {
  JTextField tf;

  public FDefaultCellEditor(JTextField tf) {
    super(tf);
    this.tf = tf;
    tf.addFocusListener(this);
    super.setClickCountToStart(2);
  }

  public void dispose(){
    tf.removeFocusListener(this);
    tf = null;
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    Component comp = null;
    if(!isSelected){
      stopCellEditing();
      return null;
    }
    FTable t = (FTable)table;
    if(t.requestToEditCell() && tf != null && value != null ){
      tf.setText(value.toString());
      comp = tf;
    }
    return comp;    
  }

  //ATTENTION ATTENTION
  /*
  public boolean isCellEditable(EventObject anEvent) {
    //return super.isCellEditable(anEvent);
    boolean ret = super.isCellEditable(anEvent);
    if (anEvent instanceof MouseEvent) {
      MouseEvent evt = (MouseEvent) anEvent;
      if (evt.getClickCount() == 2) {
        b01.foc.Globals.getDisplayManager().popupMessage("It works !!");
        ret = false;
      }
    }
    return ret;
  }
  */  
  
  public boolean shouldSelectCell(EventObject anEvent) {
    boolean toti = false;
    //b01.foc.Globals.logString("Should SELECT CELL");
    if(anEvent.getClass() == KeyEvent.class){
      KeyEvent ke = (KeyEvent)anEvent;
      
      if(ke.getKeyCode() != KeyEvent.VK_INSERT && ke.getKeyCode() != KeyEvent.VK_DELETE){
        toti = super.shouldSelectCell(anEvent);
        if(toti){
          tf.selectAll();
        }
      }
    }
    return toti;
  }
    
  public void focusGained(FocusEvent e) {
    //b01.foc.Globals.logString("CELL FOCUS GAINED");
    tf.selectAll();
  }

  public void focusLost(FocusEvent e) {
  }
}

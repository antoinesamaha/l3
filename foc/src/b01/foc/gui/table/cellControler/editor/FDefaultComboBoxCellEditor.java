/*
 * Created on 15 fvr. 2004
 */
package b01.foc.gui.table.cellControler.editor;

import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.property.*;

import java.awt.Component;
import javax.swing.*;
import java.awt.event.*;
import java.util.EventObject;

/**
 * @author 01Barmaja
 */
public class FDefaultComboBoxCellEditor extends DefaultCellEditor /*implements ActionListener, ItemListener*/{
  private FGAbstractComboBox comboBox = null;

  public FDefaultComboBoxCellEditor(FGAbstractComboBox comboBox) {
    super(comboBox);
    this.comboBox = comboBox;
    super.setClickCountToStart(2);
  }
  
  public void dispose(){
    comboBox = null;
  }

  public void setComboBoxProperty(JTable jTable, int row, int column){    
    if(comboBox != null && jTable != null){
      FTable table = (FTable)jTable;
      FAbstractTableModel model = (FAbstractTableModel) table.getModel();
      FTableView view = model.getTableView();
      FProperty prop = (FProperty) model.getFProperty(row, view.getVisibleColumnIndex(column));
      comboBox.setProperty(prop);
    }
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    Component comp = null;
    if(!isSelected){
      stopCellEditing();
      return null;
    }
    setComboBoxProperty(table, row, column);
    FTable t = (FTable)table;
    if (t.requestToEditCell()){
      comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    return comp;
  }

  public boolean shouldSelectCell(EventObject anEvent) {
    boolean toti = false;
    if(anEvent.getClass() == KeyEvent.class){
      KeyEvent ke = (KeyEvent)anEvent;
      if(ke.getKeyCode() != KeyEvent.VK_INSERT && ke.getKeyCode() != KeyEvent.VK_DELETE){
        toti = super.shouldSelectCell(anEvent);
      }
    }
    return toti;
  }
  
  public boolean stopCellEditing() {
    boolean b = super.stopCellEditing();
    comboBox.setProperty(null);
    return b;
  }
}

/*
 * Created on 24-Mar-2005
 */
package b01.foc.gui.table.cellControler.renderer;

import b01.foc.gui.*;

import java.awt.*;

import javax.swing.*;

/**
 * @author 01Barmaja
 */
public class FDateCellRenderer extends FDefaultCellRenderer{
  
  public void dispose(){
    
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    FGDateField date = new FGDateField();
    date.setValue(value);
    setCellShape(date, table, value, isSelected, hasFocus, row, column);
    /*
    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if(comp != null){
      FTableModel model = (FTableModel) table.getModel();
      if(!model.isCellEditable(row, column)){
        comp.setEnabled(false);
      }else{
        comp.setEnabled(true);      
      }
    }
    return comp;
    */
    return date;
  }
}

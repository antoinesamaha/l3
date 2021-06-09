/*
 * Created on 24-Mar-2005
 */
package b01.foc.gui.table.cellControler.renderer;

import b01.foc.desc.FocObject;
import b01.foc.gui.*;
import b01.foc.gui.table.FTable;

import java.awt.*;

import javax.swing.*;

/**
 * @author 01Barmaja
 */
public class FCheckBoxCellRenderer extends FDefaultCellRenderer{
  
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3257004362944295737L;

  private FGCheckBox checkBox = null;
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if(checkBox == null){
      checkBox = new FGCheckBox();
    }
    //Globals
    FTable ft = (FTable) table;
    FocObject obj = ft.getTableModel().getRowFocObject(row);
    Boolean boolValue = (Boolean) value; 
    //BElie
    if( boolValue != null ){
      checkBox.setSelected(boolValue.booleanValue());    
    }
    //EElie
    setCellShape(checkBox, table, value, isSelected, hasFocus, row, column);
    //setToolTipTextAccordingToField(checkBox);    
    return checkBox;
  }
  
  public void dispose(){
    
  }
}

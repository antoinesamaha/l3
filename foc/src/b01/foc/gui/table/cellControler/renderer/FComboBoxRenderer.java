/*
 * Created on 24-Mar-2005
 */
package b01.foc.gui.table.cellControler.renderer;

import java.awt.*;

import java.util.*;
import javax.swing.*;

import b01.foc.gui.table.FAbstractTableModel;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FComboBoxRenderer extends FDefaultCellRenderer{

  private FGComboBoxInTable box = null;
  private HashMap map = null;
  
  public void dispose(){
  	if(box != null){
  		box.dispose();
  	}
    box = null;
    map = null;
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = null;
    FAbstractTableModel model = (FAbstractTableModel) table.getModel();
    int modelCol = model.getTableView().getVisibleColumnIndex(column);
    if(model.isCellEditable(row, modelCol)){
      if(map == null){
        map = new HashMap();
      }
      if(box == null){
        box = new FGComboBoxInTable(table);
        /*
        ComboBoxUI ui = (ComboBoxUI)box.getUI();
        ui.
        */
        //box.setUI(new javax.swing.plaf.metal.MetalComboBoxUI());
        
        //javax.swing.plaf.metal.MetalLookAndFeel metal = new javax.swing.plaf.metal.MetalLookAndFeel();
        //metal.getC
        //UIManager.getLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel());
        //javax.swing.plaf.metal.MetalLookAndFeel
        //box.setUI()
      }
      if(map.get(value) == null){
        map.put(value, value);
        box.addItem(value);
      }
      box.setSelectedItem(value);      
      comp = box;
    }else{
      comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
    /*
    if(isSelected){
      comp.setBackground(table.getSelectionBackground());
    }else{
      comp.setBackground(table.getBackground());
    }
    */
    setCellColor(comp, table,value,isSelected,hasFocus,row,column);    
    return comp;
  }
}

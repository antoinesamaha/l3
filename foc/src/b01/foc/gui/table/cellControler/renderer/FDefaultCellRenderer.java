/*
 * Created on 24-Mar-2005
 */
package b01.foc.gui.table.cellControler.renderer;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import b01.foc.Globals;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FNumField;
import b01.foc.gui.table.FAbstractTableModel;
import b01.foc.gui.table.FTable;
import b01.foc.gui.table.FTableModel;
import b01.foc.gui.treeTable.FTreeTableModel;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FDefaultCellRenderer extends DefaultTableCellRenderer{

  private static final Color COLOR_FOR_LINE_GROUPING_BY_3 = new Color(235, 235, 235);
  
  public FDefaultCellRenderer(){
    super();
  }
  
  public void dispose(){
    
  }

  /*
  protected void setToolTipTextAccordingToField(Component comp){
    try{
      JComponent jComp = (JComponent)comp;
      jComp.setToolTipText("werwdd");
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  */
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = null;
    comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setCellShape(comp, table, value, isSelected, hasFocus, row, column);
    //setToolTipTextAccordingToField(comp);    
    //setBackground(Color.RED);
    //Globals.logString(""+column);
    return comp;
  }
  
  public static void setCellColorStatic(Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){    
    FTable fTable = (FTable) table;
 
    Color cellColor = null;
    FAbstractTableModel model = (FAbstractTableModel) table.getModel();
    int modelCol = model.getTableView().getVisibleColumnIndex(column);
    boolean editable = model.isCellEditable(row, modelCol);
    /*if(modelCol == 2){
      Globals.logString("  -Table cell "+modelCol+" editable:"+editable);
    }*/

    cellColor = model.getCellColor(row, modelCol);
    if(cellColor == null){
      if(isSelected){
        cellColor = table.getSelectionBackground();
        //Globals.logString("Red="+cellColor.getRed()+" Green="+cellColor.getGreen()+" Blue="+cellColor.getBlue());        
        if(hasFocus){

          int red = 255;
          int green = 255;
          int blue = 255;   
          if(!editable){
            int colorDiff = 20;
            red = Math.min(255, cellColor.getRed()+colorDiff);
            green = Math.min(255, cellColor.getGreen()+colorDiff);
            blue = Math.min(255, cellColor.getBlue()+colorDiff);   
          }
         
          cellColor = new Color(red, green, blue);
        }
      }else{          
        cellColor = table.getBackground();
        
        FAbstractTableModel fModel = (FAbstractTableModel) table.getModel();
        cellColor = fModel.getDefaultBackgroundColor(cellColor, comp, table, value, isSelected, hasFocus, row, column);

//        if(row % 6 >= 3){
//          cellColor = COLOR_FOR_LINE_GROUPING_BY_3;          
//        }
      }
    }    
    
    if(fTable != null && fTable.getCurrentMouseRow() >= 0){
      if(fTable.getCurrentMouseRow() == row){
        cellColor = table.getSelectionBackground(); 
      }else{
        cellColor = table.getBackground(); 
      }
    }
    
    if(cellColor != null){
      comp.setBackground(cellColor);
    }
    if(!editable){
      comp.setFont(Globals.getDisplayManager().getDefaultNotEditableFont());
      //comp.setForeground(Color.GRAY);
    }else{
      comp.setFont(Globals.getDisplayManager().getDefaultFont());
    }
    
    //BElie
    FProperty prop = model.getFProperty(row, modelCol);
    if(prop != null && prop.getFocField().isWithInheritance() && prop.isInherited()){
      comp.setForeground(Color.LIGHT_GRAY);
      //FTreeTableModel
    }else{
      comp.setForeground(Color.BLACK);
    }
    //EELie
    if(prop != null){
	    FField field = prop.getFocField(); 
	    if(field instanceof FNumField && comp instanceof DefaultTableCellRenderer){
	    	((DefaultTableCellRenderer) comp).setHorizontalAlignment(JTextField.RIGHT);
	    }
    }
  }
  
  protected void setCellColor(Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
  	setCellColorStatic(comp, table, value, isSelected, hasFocus, row, column);
  }
  
  protected void setCellShape(Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
    if(comp != null){
      FAbstractTableModel model = (FAbstractTableModel) table.getModel();
      /*
      if(!model.isCellEditable(row, column)){
        comp.setEnabled(false);
      }else{
        comp.setEnabled(true);      
      }*/
      setCellColor(comp, table, value, isSelected, hasFocus, row, column);      
    }
  }
}

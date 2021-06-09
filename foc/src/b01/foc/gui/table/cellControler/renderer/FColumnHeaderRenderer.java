/*
 * Created on Nov 23, 2005
 */
package b01.foc.gui.table.cellControler.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public class FColumnHeaderRenderer extends DefaultTableCellRenderer{
  TableCellRenderer originalRenderer = null;
  private Color backgroundColor = Color.ORANGE;
  
  public FColumnHeaderRenderer(TableCellRenderer renderer){
    super();
    this.originalRenderer = renderer;    
  }
  
  public FColumnHeaderRenderer(TableCellRenderer renderer,Color backGroundColor){
  	this(renderer);
  	if(backGroundColor != null){
  		setBackGroundColor(backGroundColor);
  	}
  }
  
  public void dispose(){
    originalRenderer = null;
  }
  
  public void setBackGroundColor(Color backGroundColor){
  	this.backgroundColor = backGroundColor;
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = null; 
    if(originalRenderer != null){
      comp = originalRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }else{
      comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);      
    }
    comp.setFont(Globals.getDisplayManager().getDefaultFont());
    //comp.setBackground(Color.ORANGE);
    comp.setBackground(backgroundColor);
    JComponent text = (JComponent) comp;
    text.setBorder(BorderFactory.createLineBorder(Color.GRAY));    
    if(text instanceof JLabel) {
      JLabel label = (JLabel) comp;
      label.setHorizontalAlignment(JLabel.CENTER);
    }
    return comp;
  }
}

/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;

import b01.foc.Globals;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FGCheckBox extends JCheckBox implements ActionListener, FPropertyListener{
  protected FBoolean booleanProperty = null;
  
  public FGCheckBox() {
    setFont(Globals.getDisplayManager().getDefaultFont());
    addActionListener(this);    
    setBackground(Globals.getDisplayManager().getBackgroundColor());
  }
  
  public void dispose(){
    removeActionListener(this);
    if(booleanProperty != null){
      booleanProperty.removeListener(this);
      booleanProperty = null;
    }
  }
  
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    StaticComponent.setEnabledNoBackground(this, b);
  }
  
  public void setProperty(FProperty prop){
    if(prop != booleanProperty){
      if(booleanProperty != null){
        booleanProperty.removeListener(this);
      }
      booleanProperty = (FBoolean) prop;
      propertyModified(booleanProperty);
      if(booleanProperty != null){
        booleanProperty.addListener(this);
      }
    }
  }
  
  /* (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    if(booleanProperty != null){
      booleanProperty.setBoolean(isSelected());
    }
  }
  
  /* (non-Javadoc)
   * @see b01.foc.property.FPropertyListener#propertyModified(b01.foc.property.FProperty)
   */
  public void propertyModified(FProperty property) {
    if(booleanProperty != null){
      setSelected(booleanProperty.getBoolean());
    }
  }
}

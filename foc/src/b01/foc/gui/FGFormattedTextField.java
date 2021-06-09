/*
 * Created on Aug 15, 2005
 */
package b01.foc.gui;

import java.text.Format;

import javax.swing.*;

import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public abstract class FGFormattedTextField extends JFormattedTextField{
  
  public abstract void dispose();
  
  /**
   * 
   */
  public FGFormattedTextField() {
    super();
    setFont(Globals.getDisplayManager().getDefaultFont());
    setDisabledTextColor(Globals.getDisplayManager().getDisabledTextColor());
  }   
  
  /**
   * @param format
   */
  public FGFormattedTextField(Format format) {
    super(format);
    setFont(Globals.getDisplayManager().getDefaultFont());
    setDisabledTextColor(Globals.getDisplayManager().getDisabledTextColor());
  }
  
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    StaticComponent.setEnabled(this, b);
  }
}

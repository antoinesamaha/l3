/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import javax.swing.*;

import b01.foc.Globals;
import b01.foc.gui.table.*;

import java.awt.*;

/**
 * @author 01Barmaja
 */
public class FGToggleButton extends JToggleButton {
  
  private boolean doNotAdjustSize = false;
  private boolean disableValidationProcess = false;
  private FTable table = null;
  
  /**
   * @param arg0
   */
  public FGToggleButton(String label) {
    super(label);
    /*
    if(label != null){
      Graphics2D g = (Graphics2D) this.getGraphics();
      Font font = this.getFont();
      if(font == null) Globals.logString("font null");
      if(g == null) Globals.logString("g null");
      font.getf
      font.getStringBounds("dsd", g.getFontRenderContext());
      int width = label.length() * 11;
      int height = Globals.CHAR_HEIGHT;
      Dimension dim = new Dimension(width, height);
      setPreferredSize(dim);
    } 
    */   
  }

  /**
   * @param arg0
   */
  public FGToggleButton(Icon icon) {
    super(icon);
    int width = icon.getIconWidth();
    int height = icon.getIconHeight();
    Dimension dim = new Dimension(width+4, height+4);
    setPreferredSize(dim);
    doNotAdjustSize = true;
  }

  /**
   * 
   */
  public FGToggleButton() {
    super();
  }
  
  public void dispose(){
    table = null;
  }
  
  public void adjustSizeToCharacters(){
    String text = this.getText();
    Globals.logString("Button text:" + text);
    int width = text.length()*9;   
    int height = 16;
    Dimension dim = new Dimension(width+4, height);
    setPreferredSize(dim);
  }
  
  public Dimension getPreferredSize() {
    Dimension dim = super.getPreferredSize();
    if(!doNotAdjustSize) dim.height = dim.height - 5; 
    return dim;
  }
  
  public boolean isDisableValidationProcess() {
    return disableValidationProcess && table != null;
  }
  
  public void setDisableValidationProcess(boolean disableValidationProcess, FTable table) {
    this.disableValidationProcess = disableValidationProcess;
    this.table = table;
  }
  
  public synchronized void requestFocus() {
    InputVerifier backup = null;
    if(isDisableValidationProcess()){
      backup = table.getInputVerifier();
      table.setInputVerifier(null);
    }
    super.requestFocus();
    if(isDisableValidationProcess()){
      table.setInputVerifier(backup);
    }
  }

  public synchronized boolean requestFocusInWindow() {
    InputVerifier backup = null;
    if(isDisableValidationProcess()){
      backup = table.getInputVerifier();
      table.setInputVerifier(null);
    }
    boolean b = super.requestFocusInWindow();
    if(isDisableValidationProcess()){
      table.setInputVerifier(backup);
    }
    return b;
  }

}

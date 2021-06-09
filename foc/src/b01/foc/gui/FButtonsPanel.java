/*
 * Created on 28-Mar-2005
 */
package b01.foc.gui;

import java.awt.GridBagConstraints;

/**
 * @author 01Barmaja
 */
public class FButtonsPanel extends FPanel {
  int x = 0;
  
  public void addButton(FGButton button){
    //add(button, x++, 0);
    add(button, x++, 0, 1, 1, 0.1, 0.2, GridBagConstraints.EAST, GridBagConstraints.NONE);
  }
}

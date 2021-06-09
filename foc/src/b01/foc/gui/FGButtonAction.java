/*
 * Created on Sep 14, 2005
 */
package b01.foc.gui;

import b01.foc.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * @author 01Barmaja
 */
public abstract class FGButtonAction extends AbstractAction {
  private FGButton associatedButton = null;
  
  public abstract void focActionPerformed(ActionEvent e);
  
  public FGButtonAction(FGButton associatedButton){
    this.associatedButton = associatedButton;   
  }
  
  public synchronized void actionPerformed(ActionEvent e) {
    Globals.getDisplayManager().stopEditingLockFocus();
    boolean ok = true;
    if(associatedButton != null && !associatedButton.hasFocus()){
      ok = associatedButton.requestFocusInWindow();
    }
    if(ok){
      focActionPerformed(e);
    }
  }
  
  public FGButton getAssociatedButton() {
    return associatedButton;
  }
  
  public void setAssociatedButton(FGButton associatedButton) {
    this.associatedButton = associatedButton;
  }
}

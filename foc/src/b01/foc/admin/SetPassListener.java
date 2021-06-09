/*
 * Created on 22-May-2005
 */
package b01.foc.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import b01.foc.Globals;
import b01.foc.gui.FPanel;
import b01.foc.gui.FListPanel;

/**
 * @author 01Barmaja
 */
public class SetPassListener implements ActionListener{
  FListPanel selPanel = null;
  
  public SetPassListener(FListPanel selPanel){
    this.selPanel = selPanel;
  }
  
  public void actionPerformed(ActionEvent e){
    FocUser user = (FocUser) selPanel.getSelectedObject();
    FPanel panel = user.newDetailsPanel(FocUser.SET_PASSWORD_VIEW_ID);
    Globals.getDisplayManager().changePanel(panel);
  }
}

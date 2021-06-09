/*
 * Created on 24-May-2005
 */
package b01.foc.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.*;

import b01.foc.ConfigInfo;
import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public class FDialog extends JDialog{

	private FPanelSequence panelSequence = null;
	
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3257564027180954932L;

  /**
   * @param owner
   * @param title
   * @param modal
   * @throws java.awt.HeadlessException
   */
  public FDialog(Frame owner, String title, boolean modal) throws HeadlessException {
    super(owner, title, modal);
    
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    
    panelSequence = new FPanelSequence();
    setContentPane(panelSequence.getCenterPanel());//AUTOSIZE
  }
  
  public void setPanel(final FPanel panel){
    FPanelSequence panelSeq = getPanelSequence();
    panelSeq.setMainPanel(panel);
    panelSeq.violentRefresh();
    
    addWindowListener(new WindowListener(){

      public void windowActivated(WindowEvent e) {
      }

      public void windowClosed(WindowEvent e) {
      }

      public void windowClosing(WindowEvent e) {
        if (panel.getValidationPanel() != null){
          panel.getValidationPanel().cancelAction();
        }
      }

      public void windowDeactivated(WindowEvent e) {
      }

      public void windowDeiconified(WindowEvent e) {
      }

      public void windowIconified(WindowEvent e) {
      }

      public void windowOpened(WindowEvent e) {
      }
    });
  }
  
  public void dispose(){
  	if(panelSequence != null){
  		//panelSequence.dispose();
  	}
  	panelSequence = null;
  	super.dispose();
  }
  
  public FPanelSequence getPanelSequence() {
    return panelSequence;
  }
  
  public Dimension getPreferredSize(){
  	Dimension newDimension = super.getPreferredSize();
  	int newHeight =  newDimension.height +15;
  	int configInfoNavigatorHeight = ConfigInfo.getGuiNavigatorHeight();
  	if (newHeight > configInfoNavigatorHeight){
  		newHeight = configInfoNavigatorHeight;
  	}
  	newDimension.height = newHeight;
  	return newDimension;
  }
}

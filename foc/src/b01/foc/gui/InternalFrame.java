/*
 * Created on 22-May-2005
 */
package b01.foc.gui;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class InternalFrame extends JInternalFrame{
  private FPanelSequence panelSequence = null;
  
  public InternalFrame(String title){
    super(title, true, false, true, true);
    //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    addInternalFrameListener(new InternalFrameListener(){
      public void internalFrameActivated(InternalFrameEvent e) {
      }

      public void internalFrameClosed(InternalFrameEvent e) {
      }

      public void internalFrameClosing(InternalFrameEvent e) {
        b01.foc.Globals.logString("Closing event");
      }

      public void internalFrameDeactivated(InternalFrameEvent e) {
      }

      public void internalFrameDeiconified(InternalFrameEvent e) {
      }

      public void internalFrameIconified(InternalFrameEvent e) {
      }

      public void internalFrameOpened(InternalFrameEvent e) {
      }
    });
    
    panelSequence = new FPanelSequence();
    
    //GridBagLayout layout = new GridBagLayout();
    //setLayout(layout);
    setContentPane(panelSequence.getCenterPanel());//AUTOSIZE
    //layout.setConstraints(contentPane, constr);
    
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
}

/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import b01.foc.*;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author 01Barmaja
 */
public class MainFrame extends JFrame {

   /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3256999947734954806L;
  private FPanelSequence panelSequence = null;
  
  /**
   * @throws java.awt.HeadlessException
   */
  public MainFrame(){
    super();

    setFont(Globals.getDisplayManager().getDefaultFont());
    
    setWindowTitle();
    
    try{
    	this.setIconImage(Globals.getIcons().getLogoIcon().getImage());
    }catch(Exception e){
    	Globals.getDisplayManager().popupMessage("Logo icon not fount");
    }
    panelSequence = new FPanelSequence();
    setContentPane(panelSequence.getCenterPanel());
    
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    WindowListener exitListener = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        //Window window = e.getWindow();
        //window.setVisible(false);
        //window.dispose();
        Globals.getApp().exit();        
      }
    };
    
    addWindowListener(exitListener);    
  }

  public void setWindowTitle(){
    String windTitle = ConfigInfo.getWindowTitle();
    if(windTitle != null && windTitle.trim().compareTo("") != 0){
      setTitle("01Barmaja   ->  "+windTitle);
    }else{
      setTitle("01Barmaja");
    }
  }
  
  public void initSize(){
    if(ConfigInfo.getGuiNavigatorWidth() == 0 && ConfigInfo.getGuiNavigatorHeight() == 0){
      setExtendedState(Frame.MAXIMIZED_BOTH);
      Rectangle rect = getBounds();
      ConfigInfo.setGuiNavigatorWidth(rect.width);   
      ConfigInfo.setGuiNavigatorHeight(rect.height);
    }else{
      setPreferredSize(new Dimension(ConfigInfo.getGuiNavigatorWidth(), ConfigInfo.getGuiNavigatorHeight()));
    }
  }
  
  public JComponent getMainPanel() {
    return panelSequence.getMainPanel();
  }

  private void adjustSize(Dimension dim) {
    Dimension realDim = dim;
    if (realDim.width < 700) {
      realDim.width = 700;
    }
    if (realDim.height < 550) {
      realDim.height = 550;
    }
    this.setMinimumSize(realDim);
    this.setPreferredSize(realDim);
    // this.mainPanel.setMinimumSize(realDim);
    // this.mainPanel.setPreferredSize(realDim);
    pack();
  }

  public void setMainPanel(FPanel mainPanel) {
    panelSequence.setMainPanel(mainPanel);
  }
  
  public FPanelSequence getPanelSequence() {
    return panelSequence;
  }
}

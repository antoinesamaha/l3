/*
 * Created on 23-May-2005
 */
package b01.foc.gui;

import java.awt.Dimension;

import b01.foc.ConfigInfo;
import b01.foc.menu.*;

/**
 * @author 01Barmaja
 */
public class MonoFrame implements Navigator {

  private MainFrame mainFrame = null;
  
  public MonoFrame(MainFrame mainFrame){
    this.mainFrame = mainFrame;
    mainFrame.initSize();
    mainFrame.setPreferredSize(new Dimension(ConfigInfo.getGuiNavigatorWidth(), ConfigInfo.getGuiNavigatorHeight()));
    //mainFrame.pack();
  }
    
  public Dimension getViewportDimension(){
    return mainFrame.getContentPane().getSize();
  }
  
  public FPanelSequence getActivePanelSequence(){
    return mainFrame.getPanelSequence();
  }
  
  public void changeView(FPanel panel) {
    FPanelSequence panSeq = mainFrame.getPanelSequence();
    panSeq.changePanel(panel);
    mainFrame.pack();
  }
  
  public boolean goBack() {
    FPanelSequence panSeq = mainFrame.getPanelSequence();
    boolean exit = panSeq.goBack(true);
    if(!exit){
      mainFrame.pack();
    }
    return exit;
  }
  
  public InternalFrame newView(FPanel panel) {
    changeView(panel);
    return null;
  }
  
  public void packActiveFrame(){
    mainFrame.pack();
  }
  
  public MenuConstructor getMainMenuConstructor(){
    MonoFrameMenuConstructor mc = new MonoFrameMenuConstructor();
    return mc;
  }
}

/*
 * Created on 22-May-2005
 */
package b01.foc.gui;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import b01.foc.*;

/**
 * @author 01Barmaja
 */
public class FPanelSequence {

  private FPanel mainPanel = null;
  private JScrollPane scrollPane = null; 
  private JPanel centerPanel;

  private GridBagLayout gridBagLayout;
  private GridBagConstraints gridBagConstraints;
  
  private ArrayList panelSequence = null;

  public FPanelSequence(){
    panelSequence = new ArrayList();

    gridBagLayout = new GridBagLayout();
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.fill = GridBagConstraints.BOTH;
   
    centerPanel = new JPanel();
    centerPanel.setLayout(new GridBagLayout());
    //scrollPane = new JScrollPane(borderLayoutPanel);
    scrollPane = new JScrollPane(centerPanel);
    //scrollPane.setLayout(new GridBagLayout());
    scrollPane.setAutoscrolls(true);
  }

  public FPanel getMainPanel() {
    return mainPanel;
  }

  private void adjustSizeNotToExceedDesktop() {
    //Dimension centerDim = centerPanel.getPreferredSize();
    //Dimension scrollDim = scrollPane.getPreferredSize();
    
    Dimension realDim = scrollPane.getPreferredSize();
    Dimension maxDim = Globals.getDisplayManager().getNavigator().getViewportDimension();
    boolean setSize = false;
    
    //First we make sure the internal frame does not exceed the navigator viewPort
    if (realDim.width > maxDim.width) {
      realDim.width = maxDim.width;
      setSize = true;
    }
    
    if (realDim.height > maxDim.height) {
      realDim.height = maxDim.height;
      setSize = true;
    }

    //Second we apply the main panel Sizing effect.
    if(getMainPanel().getMainPanelSising() == FPanel.MAIN_PANEL_FILL_VERTICAL){
      realDim.height = Double.valueOf(maxDim.height).intValue();
      setSize = true;
    }else if(getMainPanel().getMainPanelSising() == FPanel.MAIN_PANEL_FILL_HORIZONTAL){
      realDim.width = Double.valueOf(maxDim.width).intValue();
      setSize = true;
    }else if(getMainPanel().getMainPanelSising() == FPanel.MAIN_PANEL_FILL_BOTH){
      realDim.width = Double.valueOf(maxDim.width).intValue();
      realDim.height = Double.valueOf(maxDim.height).intValue();
      setSize = true;
    }
    
    //Second we make sure the panel has a minimum size. This is to make sure that emty tables show with a minimum number of lines.
    /*
    if(!setSize){
      if(realDim.height < Double.valueOf(maxDim.height * 0.75).intValue()){
        realDim.height = Double.valueOf(maxDim.height * 0.75).intValue();
        setSize = true;
      }
      if(realDim.width < Double.valueOf(maxDim.width * 0.75).intValue()){
        realDim.width = Double.valueOf(maxDim.width * 0.75).intValue();
        setSize = true;
      }
    }
    */
    
    if(setSize){
      //realDim.width += 10;
      //realDim.height += 10;
      scrollPane.setPreferredSize((Dimension)realDim.clone()); 
    }
  }

  private void cleanCentralPanel() {
    this.mainPanel = null;
    centerPanel.removeAll();
  }

  private void fillCentralPanel(FPanel mainPanel) {
    this.mainPanel = mainPanel;
    
    //centerPanel.repaint();
    //this.mainPanel.repaint();
    //scrollPane
    centerPanel.setBackground(Globals.getDisplayManager().getBackgroundColor());
    gridBagConstraints.fill = mainPanel.getFill();
    gridBagConstraints.weightx = 0.9;
    gridBagConstraints.weighty = 0.9;
       
    //Dimension centerBefore = centerPanel.getPreferredSize();
    //Dimension scrollBefore = scrollPane.getPreferredSize();
    //Dimension main = mainPanel.getPreferredSize();
    
    centerPanel.add(mainPanel, gridBagConstraints);
        
    //Dimension centerAfter = centerPanel.getPreferredSize();
    //Dimension scrollAfter = scrollPane.getPreferredSize();
    centerPanel.setPreferredSize(null);
    scrollPane.setPreferredSize(null);
    
    this.adjustSizeNotToExceedDesktop();
    centerPanel.repaint();
    mainPanel.refreshFocus();
  }
  
  public void setMainPanel(FPanel mainPanel) {
    cleanCentralPanel();
    fillCentralPanel(mainPanel);
  }
  
  public void setInitialPanel(FPanel panel){
    panel.setBackground(Globals.getDisplayManager().getBackgroundColor());
    setMainPanel(panel);
  }

  private void changePanel(FPanel jNewPanel, boolean noBackup) {
    if (jNewPanel != null) {
      JComponent jComp = getMainPanel();
      if (jComp != null && !noBackup) {
        panelSequence.add(jComp);
      }
      jNewPanel.setBackground(Globals.getDisplayManager().getBackgroundColor());
      setMainPanel(jNewPanel);
    }
  }
  
  public void changePanel(FPanel jNewPanel) {
    changePanel(jNewPanel, false);
  }

  public void violentRefresh() {
    FPanel jComp = getMainPanel();
    if (jComp != null) {
      boolean endOfPanels = goBack(false);
      changePanel(jComp, endOfPanels);      
    }
  }

  /**
   * Go up one Panel
   */
  public boolean goBack(boolean dispose) {
    boolean noScreenLeft = false;
    int index = 0;
    if (panelSequence != null) {
      FPanel removedPanel = null;

      if(dispose){
        removedPanel = getMainPanel();
      }

      index = panelSequence.size() - 1;
      noScreenLeft = index < 0;
      if (!noScreenLeft) {
        FPanel panel = (FPanel) panelSequence.get(index);
        setMainPanel(panel);
        panelSequence.remove(index);
      }else{
        if(dispose){
          cleanCentralPanel();
        }
      }
      
      if(dispose){
        StaticComponent.cleanComponent(removedPanel);
      }
    }    
    if(!Globals.getApp().isMdi() && Globals.getApp().isWithLogin()){
      noScreenLeft = index <= 0;
    }
    return noScreenLeft;
  }
  
  public int getPanelDeepness(){
    return panelSequence.size();
  }
  
  public Container getCenterPanel() {
    return scrollPane;
  }
}

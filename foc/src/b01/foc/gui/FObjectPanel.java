/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import b01.foc.*;
import b01.foc.property.*;
import b01.foc.desc.*;

import javax.swing.*;

import java.awt.event.*;

/**
 * @author 01Barmaja
 */
public class FObjectPanel extends FPanel implements FPropertyListener {
  private FObject objProp = null;
  private int viewID = FocObject.DEFAULT_VIEW_ID;
  private int zoomViewID = FocObject.DEFAULT_VIEW_ID;
  private boolean selectButtonVisible = false;
  private boolean zoomButtonVisible = false;
  private int buttonAllignment = HORIZONTAL; 
  
  private FPanel detailsPanel = null;

  private FGButton selectButton = null;
  private FGButton zoomButton = null;
  private FocObject focObjectCreatedToGeneratePanelForNULLValues = null;  

  public static final int HORIZONTAL = 1;
  public static final int VERTICAL = 2;
  
  public FObjectPanel() {
    showEastPanel(true);
    setFill(FPanel.FILL_NONE);    
  }
  
  public void dispose(){
    super.dispose();
    
    if(objProp != null){
      objProp.removeListener(this);
      objProp = null;      
    }
    
    if(detailsPanel != null){
      detailsPanel.dispose();
      detailsPanel = null;
    }

    if(selectButton != null){
      selectButton = null;
    }
    
    if(zoomButton != null){
      zoomButton = null;
    }
    
    if(focObjectCreatedToGeneratePanelForNULLValues != null){
      focObjectCreatedToGeneratePanelForNULLValues.dispose();
      focObjectCreatedToGeneratePanelForNULLValues = null;
    }
  }
  
  public void setProperty(FProperty property){
    this.objProp = (FObject) property;
    objProp.addListener(this);
    propertyModified(objProp);
  }
  
  public void setViewID(int viewID){
    this.viewID = viewID;
  }
  
  public FObject getFObject() {
    return objProp;
  }

  public FocObject getFocObject() {
    FocObject obj = null;
    if (objProp != null) {
      FObject objProp2 = (FObject) objProp;
      obj = (FocObject) objProp2.getObject();
    }
    return obj;
  }

  private void setDetailsPanel(FPanel detailsPanel) {
    if (this.detailsPanel != detailsPanel){
      if(this.detailsPanel != null) {
        this.detailsPanel.setVisible(false);
        this.remove(this.detailsPanel);
      }
      
      this.detailsPanel = detailsPanel;
      this.add(detailsPanel, 0, 0);
      this.repaint();
      this.setVisible(false);
      this.setVisible(true);
    }
  }

  private void adaptGuiToFlags() {
    boolean showPanel = selectButtonVisible || zoomButtonVisible;
    FPanel eastPanel = getEastPanel();
    int xSel = 0;
    int ySel = 0;
    int xEdit = 1;
    int yEdit = 0;
    
    if(buttonAllignment == VERTICAL){
      xSel = 0;
      ySel = 0;
      xEdit = 0;
      yEdit = 1;      
    }
    
    if (selectButton == null) {
      selectButton = new FGButton(Globals.getIcons().getSelectIcon());
      eastPanel.add(selectButton, xSel, ySel);

      AbstractAction selectAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          try {
            FObject objProp = getFObject();
            if (objProp != null) {
              objProp.popupSelectionPanel();
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
      selectButton.addActionListener(selectAction);

    }
    if (zoomButton == null) {
      zoomButton = new FGButton(Globals.getIcons().getEditIcon());
      eastPanel.add(zoomButton, xEdit, yEdit);
      AbstractAction zoomAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          try {
            FObject objProp = getFObject();
            if (objProp != null) {
              FocObject obj = (FocObject)objProp.getObject();
              if(obj != null){
                obj.forceControler(true);
                FPanel panel = obj.newDetailsPanel(zoomViewID);
                Globals.getDisplayManager().changePanel(panel);
              }
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
      zoomButton.addActionListener(zoomAction);      
    }    
    
    selectButton.setVisible(selectButtonVisible);
    zoomButton.setVisible(zoomButtonVisible);
  }

  public void setSelectButtonVisible(boolean b) {
    selectButtonVisible = b;
    adaptGuiToFlags();
  }

  public void setZoomButtonVisible(boolean b) {
    zoomButtonVisible = b;
    adaptGuiToFlags();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.property.FPropertyListener#propertyModified(b01.foc.property.FProperty)
   */
  public void propertyModified(FProperty property) {
    if (objProp != null) {
      //FocObject debugObj = (FocObject) objProp.getObject_CreateIfNeeded();
      FocObject focObj = (FocObject) objProp.getObject_CreateIfNeeded();
      if(focObj == null){
        if(focObjectCreatedToGeneratePanelForNULLValues == null){
          FocConstructor constr = new FocConstructor(objProp.getFocDesc(), null);
          focObjectCreatedToGeneratePanelForNULLValues = constr.newItem();
        }
        focObj = focObjectCreatedToGeneratePanelForNULLValues;
      }
      FPanel panel = focObj.newDetailsPanel(viewID);
      setDetailsPanel(panel);
    }
  }
  
  public int getButtonAllignment() {
    return buttonAllignment;
  }
  
  public void setButtonAllignment(int buttonAllignment) {
    this.buttonAllignment = buttonAllignment;
  }
  
  public int getZoomViewID() {
    return zoomViewID;
  }
  
  public void setZoomViewID(int zoomViewID) {
    this.zoomViewID = zoomViewID;
  }
  
  public void setSelectButtonName(String name){
    this.selectButton.setName(name);
  }
}
/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.basic.*;

import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.list.*;
import b01.foc.property.*;
import b01.foc.gui.table.*;

/**
 * @author 01Barmaja
 */
public class FGObjectComboBox extends FGAbstractComboBox {
  private int displayField = -99;
  private HashMap stringObjectMap = null;
  private HashMap refObjectMap = null;
  private FTableView tableView = null;

  //public static final String NONE_CHOICE = "-- NONE --";
  
  private void init1(int displayField) {
    this.displayField = displayField;
    makeWithAutoCompletion();
  }
  
  private void init2() {
    this.addFocusListener(this);
    this.addActionListener(this);
 }
  
  private void initListWidthCustomization(FTableView tableView){
    if(tableView != null){
      int totalWidth = tableView.getTotalWidth();
      setUI (new FGComboBoxUI ());
      addPopupMenuListener (new FGComboBoxPopupListener (totalWidth));
    }
  }
  
  /**
   * @param field
   */
  public FGObjectComboBox(int displayField) {
    init1(displayField);
    init2();
  }

  public FGObjectComboBox(/*FocList choicesObjects,*/ int displayField, FTableView tableView) {
    this.tableView = tableView;
    init1(displayField);
    initListWidthCustomization(tableView);      
    init2();    
  }
  
  /**
   * @param prop
   */
  public FGObjectComboBox(FProperty prop, int displayField) {
    init1(displayField);
    init2();    
    setProperty(prop);
    if(prop.getFocObject() != null && prop.getFocObject().getThisFocDesc() != null && prop.getFocObject().getThisFocDesc().getStorageName() != null && prop.getFocField() != null && prop.getFocField().getName() != null){
    	setName(prop.getFocObject().getThisFocDesc().getStorageName()+"."+prop.getFocField().getName());
    }
  }

  public FGObjectComboBox(FProperty prop, int displayField, FTableView tableView) {
    this.tableView = tableView;    
    init1(displayField);
    initListWidthCustomization(tableView);    
    init2();
    if(prop.getFocObject() != null && prop.getFocObject().getThisFocDesc() != null && prop.getFocObject().getThisFocDesc().getStorageName() != null && prop.getFocField() != null && prop.getFocField().getName() != null){
    	setName(prop.getFocObject().getThisFocDesc().getStorageName()+"."+prop.getFocField().getName());
    }    
  }

  public void dispose(){
    super.dispose();
    
    if(stringObjectMap != null){
      stringObjectMap.clear();
      stringObjectMap = null;
    }
    if(refObjectMap != null){
      refObjectMap.clear();
      refObjectMap = null;      
    }
    if(tableView != null){
      tableView.dispose();
      tableView = null;
    }
  }
  
  /*protected void fillChoices(FocList choicesObjects, int displayField, int nullValueMode) {
    if(stringObjectMap == null){
      stringObjectMap = new HashMap();
    }
    if(refObjectMap == null){
      refObjectMap = new HashMap();
    }
    stringObjectMap.clear();
    refObjectMap.clear();
    removeAllItems();

    if (choicesObjects != null) {
      choicesObjects.sort();
      
      if(nullValueMode == FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN){
        String str = NONE_CHOICE;
        stringObjectMap.put(str, null);
        refObjectMap.put("0", null);
        addItem(str);
      }      
      
      for (int i = 0; i < choicesObjects.size(); i++) {
        FocListElement elmt = choicesObjects.getFocListElement(i);
        FocObject obj = elmt.getFocObject();
        if (obj != null) {
          FProperty idProp = obj.getIdentifierProperty();
          FProperty displayProp = obj.getFocProperty(displayField);
          if (displayProp != null && idProp != null) {
            stringObjectMap.put(displayProp.getString(), obj);
            refObjectMap.put(idProp.getString(), obj);
            String str = displayProp.getString();
            if(!elmt.isHide()){
              addItem(str);
            }
          }
        }
      }
    }
  }*/
  
  protected void fillChoices(FocList choicesObjects, int displayField, FObject fObject) {
    if(stringObjectMap == null){
      stringObjectMap = new HashMap();
    }
    if(refObjectMap == null){
      refObjectMap = new HashMap();
    }
    stringObjectMap.clear();
    refObjectMap.clear();
    removeAllItems();

    if (choicesObjects != null) {
      choicesObjects.sort();
      int nullValueMode = fObject != null ? fObject.getNullValueMode() : -1;
      if(fObject != null && nullValueMode == FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN){
        String str = fObject.getNullValueDisplayString();
        stringObjectMap.put(str, null);
        refObjectMap.put("0", null);
        addItem(str);
      }      
      
      for (int i = 0; i < choicesObjects.size(); i++) {
        FocListElement elmt = choicesObjects.getFocListElement(i);
        FocObject obj = elmt.getFocObject();
        if (obj != null) {
          FProperty idProp = obj.getIdentifierProperty();
          FProperty displayProp = obj.getFocProperty(displayField);
          if (displayProp != null && idProp != null) {
            stringObjectMap.put(displayProp.getString(), obj);
            refObjectMap.put(idProp.getString(), obj);
            String str = displayProp.getString();
            if(!elmt.isHide()){
              addItem(str);
            }
          }
        }
      }
    }
  }

  public void refreshList(FObject objProp){
    if(objProp != null){
      FocList choicesObjects = objProp.getPropertySourceList();
      if(tableView != null){
        FGMultiColumnComboListRenderer tableListRenderer = new FGMultiColumnComboListRenderer(choicesObjects, displayField, tableView);
        setRenderer(tableListRenderer);
      }
      fillChoices(choicesObjects, displayField, objProp);
    }
  }
  
  protected void setPropertyStringValue(String strValue) {
    if (property != null && stringObjectMap != null) {
      FocObject obj = (FocObject) stringObjectMap.get(strValue);
      property.setObject(obj);
    }
  }

  protected String getPropertyStringValue() {
    String displayString = null;
    FObject objProperty = (FObject)property;
    FocObject obj = objProperty != null ? (FocObject) objProperty.getObject_CreateIfNeeded() : null;
    FProperty displayProp = obj != null ? obj.getFocProperty(displayField) : null;
    displayString = displayProp != null ? displayProp.getString() : null;
    if(displayString == null){
      //displayString = NONE_CHOICE;
    	displayString = objProperty != null ? objProperty.getNullValueDisplayString() : FObjectField.NONE_CHOICE;
    }
    return displayString;
  }
  
  public void setProperty(FProperty prop) {
    refreshList((FObject)prop);
    super.setProperty(prop);
    if(prop != null){
      setEnabled(!prop.isValueLocked());
    }
    
    /*
    FObject objProp = (FObject) prop;
    if(objProp != null){
      FocList choicesObjects = objProp.getPropertySourceList();
      if(tableView != null){
        FGMultiColumnComboListRenderer tableListRenderer = new FGMultiColumnComboListRenderer(choicesObjects, displayField, tableView);
        setRenderer(tableListRenderer);
      }
      fillChoices(choicesObjects, displayField);
    }
    */
  }
  
  public class FGComboBoxUI extends MetalComboBoxUI {
    private BasicComboPopup Cpu;
    private JScrollPane sPane;

    FGComboBoxUI() {
      super();
    }

    protected ComboPopup createPopup() {
      Cpu = (BasicComboPopup) (super.createPopup());
      sPane = (JScrollPane) (Cpu.getComponent(0)); //the popup container has 1 component (i.e. the scrollPane)
      sPane.setAlignmentX(0); //default is centered. make items left aligned
      sPane.setAlignmentY(0); //default is centered. make items left aligned 
      return Cpu;
    }

    public JScrollPane getJScrollPane() {
      return sPane;
    }
  }

  public class FGComboBoxPopupListener implements PopupMenuListener {
    int totalWidth = 0;

    FGComboBoxPopupListener () {
      super();
    }
    
    FGComboBoxPopupListener (int totalWidth) {
      super();
      this.totalWidth = totalWidth;
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
    }
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }
    
    public void popupMenuWillBecomeVisible (PopupMenuEvent e) {
      FGObjectComboBox cb = (FGObjectComboBox)(e.getSource());
      if(totalWidth > 0){
        FGComboBoxUI cbUI = (FGComboBoxUI)(cb.getUI());
        JScrollPane sPane = cbUI.getJScrollPane();
        sPane.setPreferredSize (new Dimension (totalWidth, 100));
        sPane.setMaximumSize (new Dimension (totalWidth, 100)); //the layout manager sets the maximum size to size of comboBox so you need to change it here
      }
    }
  }
}

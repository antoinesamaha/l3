/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import b01.foc.desc.*;
import b01.foc.gui.*;
import b01.foc.gui.table.cellControler.*;
import b01.foc.list.*;
import b01.foc.list.filter.FilterCondition;
import b01.foc.list.filter.FocDescForFilter;
import b01.foc.list.filter.ObjectCondition;
import b01.foc.property.*;
import b01.foc.gui.table.*;

import java.awt.Component;
import java.sql.Types;

/**
 * @author 01Barmaja
 */
public class FObjectField extends FField {
  public static final String NONE_CHOICE = "-- NONE --";

  private FocDesc focDesc = null;
  private String keyPrefix = null;

  private boolean withList = true;
  private FocList selectionList = null;
  private int editorType = SELECTION_PANEL_EDITOR;
  private String nullValueDisplayString = NONE_CHOICE;
  //For Panel editor
  private int detailsPanelViewID = FocObject.DEFAULT_VIEW_ID;

  //For Combo box editor
  private int comboBoxDisplayField = -900;
  private FTableView multiColTableView = null;
  
  private int nullValueMode = NULL_VALUE_NOT_ALLOWED;
  private int listFieldIdInMaster = -1;  

  public static final int SELECTION_PANEL_EDITOR = 0;
  public static final int COMBO_BOX_EDITOR = 1;
  public static final int MULIT_COL_COMBO_BOX_EDITOR = 2;

  public static final int NULL_VALUE_NOT_CUSTOMIZED = -1;//This is used for FObject
  public static final int NULL_VALUE_NOT_ALLOWED = 0;
  public static final int NULL_VALUE_ALLOWED = 1;
  public static final int NULL_VALUE_ALLOWED_AND_SHOWN = 2;
  
  public FObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix) {
    /*super(name, title, id, key, 0, 0);
    this.focDesc = focDesc;
    this.keyPrefix = keyPrefix;*/
  	this(name, title, id, key, focDesc, keyPrefix, null, -1);
  }
  
  public FObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix, FocDesc slaveFocDesc, int listFieldIdInMaster, FocDescForFilter focDescForFilter) {
  	super(name, title, id, key, 0, 0);
    this.focDesc = focDesc;
    this.keyPrefix = keyPrefix;
    setListFieldInMaster(listFieldIdInMaster, slaveFocDesc, focDescForFilter);
  }

  public FObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix, FocDesc slaveFocDesc, int listFieldIdInMaster) {
    this(name, title, id, key, focDesc, keyPrefix, slaveFocDesc, listFieldIdInMaster, null);
  }
  
  public void dispose(){
    super.dispose();
    
    focDesc = null;
    keyPrefix = null;
    
    selectionList = null;
    if(multiColTableView != null){
      multiColTableView.dispose();
      multiColTableView = null;
    }
  }
  
  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FObject(masterObj, getID(), (FocObject) defaultValue);
  }

  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, null);
  }
  
  public void copyInteralProperties(FObjectField sourceField){
    setWithList(sourceField.isWithList());
    setSelectionList(sourceField.getSelectionList());
    switch(sourceField.getEditorType()){
    case SELECTION_PANEL_EDITOR:

      break;
    case COMBO_BOX_EDITOR:
      setComboBoxCellEditor(sourceField.getDisplayField());      
      break;
    case MULIT_COL_COMBO_BOX_EDITOR:
      editorType = MULIT_COL_COMBO_BOX_EDITOR;
      setMultiLineComboBoxCellEditor(getDisplayField(), sourceField.multiColTableView);
      break;     
    }
    
    nullValueMode = NULL_VALUE_ALLOWED_AND_SHOWN;    
  }
  
  public FocList getSelectionList() {
    if (selectionList == null && focDesc != null && isWithList()){
      FocLink link = new FocLinkSimple(focDesc);
      selectionList = new FocList(link);
    }
    if (selectionList != null) {
      selectionList.loadIfNotLoadedFromDB();
    }
    return selectionList;
  }

  public void setSelectionList(FocList selectionList) {
    this.selectionList = selectionList;
    setWithList(selectionList != null);
  }

  public int getSqlType() {
    return Types.JAVA_OBJECT;
  }

  public String getCreationString(String name) {
    return "";
  }

  /**
   * @return
   */
  public String getKeyPrefix() {
    return keyPrefix;
  }
  
  public String getDBName(){
    String name = keyPrefix != null ? keyPrefix :"";
    return name + FField.REF_FIELD_NAME;
  }
  
  public boolean isMasterDetailsLink(){
  	return listFieldIdInMaster > 0 || listFieldIdInMaster == FField.FLD_PROPERTY_FORMULA_LIST;
  }
  
  protected void setListFieldInMaster(int listFieldIdInMaster, FocDesc slaveFocDesc, FocDescForFilter focDescForFilter){
  	this.listFieldIdInMaster = listFieldIdInMaster;
  	if(isMasterDetailsLink()){
      FocDesc masterDesc = getFocDesc();
      if(masterDesc != null){
        if(focDescForFilter != null){
          masterDesc.addListField(slaveFocDesc, getID(), listFieldIdInMaster, focDescForFilter);
        }else{
          masterDesc.addListField(slaveFocDesc, getID(), listFieldIdInMaster);
        }
      }
  	}
  }

  /*
  public Object clone() throws CloneNotSupportedException {
    return null;
  }
  */
  
  public Object clone() throws CloneNotSupportedException {
    FField zClone = (FField)super.clone();
    setListFieldInMaster(-1, null, null);
    return zClone;
  }

  public void setNullValueDisplayString(String string){
  	this.nullValueDisplayString = string;
  }
  
  public String getNullValueDisplayString(){
  	return this.nullValueDisplayString;
  }
  
  public Component getGuiComponent_Panel(FProperty prop){
    FObjectPanel objPanel = new FObjectPanel();
    objPanel.setViewID(detailsPanelViewID);
    if (prop != null) objPanel.setProperty(prop);
    return objPanel;  	
  }

  public Component getGuiComponent_ComboBox(FProperty prop){
    return new FGObjectComboBox(prop, comboBoxDisplayField);
  }

  public Component getGuiComponent_MultiColumnComboBox(FProperty prop){
    return new FGObjectComboBox(prop, comboBoxDisplayField, multiColTableView);
  }

  public Component getGuiComponent(FProperty prop) {
    Component comp = null;

    switch (editorType) {
    case SELECTION_PANEL_EDITOR: {
    	comp = getGuiComponent_Panel(prop);
    }
      break;
    case COMBO_BOX_EDITOR: {
    	comp = getGuiComponent_ComboBox(prop);
    }
      break;
    case MULIT_COL_COMBO_BOX_EDITOR: {
    	comp = getGuiComponent_MultiColumnComboBox(prop);
    	break;
    }
    }

    return comp;
  }

  public AbstractCellControler getTableCellEditor(FProperty prop) {
    AbstractCellControler cellEditor = null;
    switch (editorType) {
    case SELECTION_PANEL_EDITOR:
      cellEditor = new ObjectCellControler();
      break;
    case COMBO_BOX_EDITOR:
      cellEditor = new ComboBoxCellControler(/*getSelectionList(), */this.comboBoxDisplayField);
      break;
    case MULIT_COL_COMBO_BOX_EDITOR:
      cellEditor = new ComboBoxCellControler(/*getSelectionList(), */this.comboBoxDisplayField, this.multiColTableView);
      break;
    }
    return cellEditor;
  }

  public boolean isObjectContainer() {
    return true;
  }

  /**
   * @return
   */
  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc) {
  	if(!isMasterDetailsLink()){
	    FocDesc targetDesc = getFocDesc();
	
	    //Adding a field reference checker
	    ReferenceCheckerAdapter refCheck = new ReferenceCheckerAdapter(pointerDesc, getID());
	    if (targetDesc != null) {
	      targetDesc.addReferenceLocation(refCheck);
	    }
  	}
  }
  
  public void setDisplayField(int displayField) {
    this.comboBoxDisplayField = displayField;
  }

  public int getDisplayField() {
    return comboBoxDisplayField;
  }

  public void setComboBoxCellEditor(int displayField) {
    editorType = COMBO_BOX_EDITOR;
    this.comboBoxDisplayField = displayField;
  }

  public void setMultiLineComboBoxCellEditor(int displayField, FTableView tableView) {
    editorType = MULIT_COL_COMBO_BOX_EDITOR;
    this.comboBoxDisplayField = displayField;
    multiColTableView = tableView;
  }

  public int getDetailsPanelViewID() {
    return detailsPanelViewID;
  }

  public void setDetailsPanelViewID(int detailsPanelViewID) {
    this.detailsPanelViewID = detailsPanelViewID;
  }

  public int getFieldDisplaySize() {
    FField field = getFocDesc().getFieldByID(comboBoxDisplayField);
    return field != null ? field.getFieldDisplaySize() : 0;
  }

  public boolean isWithList() {
    return withList;
  }

  public void setWithList(boolean withList) {
    this.withList = withList;
  }

  public int getNullValueMode() {
    return nullValueMode;
  }

  public void setNullValueMode(int nullValueMode) {
    this.nullValueMode = nullValueMode;
  }
  
  public int getEditorType() {
    return editorType;
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		ObjectCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new ObjectCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}
  
  public String getNameInSourceTable(){
    return getKeyPrefix()+FField.REF_FIELD_NAME;
  }
  public String getNameInTargetTable(){
    return FField.REF_FIELD_NAME;
  }
  
  public String createLinkCondition(String firstTableName){//We have to implement this function in FInLineObject also
    FocDesc fieldDesc = getFocDesc();
    StringBuffer link = new StringBuffer("");
    if(fieldDesc != null){
      link.append(firstTableName+"."+getKeyPrefix()+FField.REF_FIELD_NAME);
      link.append(fieldDesc.getStorageName()+"."+FField.REF_FIELD_NAME);
    }
    return link.toString();
  }
  
  //BElie
  private boolean displayNullValues = true;
  public boolean isDisplayNullValues() {
    return displayNullValues;
  }
  
  public void setDisplayNullValues(boolean displayNullValues) {
    this.displayNullValues = displayNullValues;
  }
  
  //EElie
}

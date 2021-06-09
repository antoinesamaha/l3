// MAIN
// COMPARE
// CONCURRENT ACCESS
// ACCESS
// LISTENERS
// REFERENCE
// DATABASE
// LIST
// XML

/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc;

import b01.foc.*;
import b01.foc.db.*;
import b01.foc.desc.field.*;
import b01.foc.event.*;
import b01.foc.formula.Formula;
import b01.foc.formula.PropertyFormula;
import b01.foc.formula.PropertyFormulaContext;
import b01.foc.formula.PropertyFormulaDesc;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.*;
import b01.foc.gui.*;
import b01.foc.access.*;
import b01.foc.admin.*;
import java.lang.reflect.*;
import java.text.Format;
import java.util.*;
import java.awt.*;
import javax.swing.JOptionPane;
import org.w3c.dom.*;

/**
 * @author 01Barmaja
 */
public abstract class FocObject extends AccessSubject implements FocListener {
	private FocDesc thisFocDesc  = null;
  private boolean loadedFromDB = true;
  private boolean isTempReference = false;
  private FocObject masterObject = null;
  private ArrayList<FocListener> listeners = null;
  //private Map properties = null;
  private FProperty propertiesArray[] = null;
  private boolean duringLoad = false;
  private boolean lockedByConcurrence = false;
  private boolean deletable = true;

  private boolean contentValidMessageOn = false;
  protected FPanel detailsPanel = null;
  protected static FPanel browsePanel = null;  
  private ArrayList<Component> relatedGuiComponents = null;
  
  public final static int SUMMARY_VIEW_ID = -1;
  public final static int DEFAULT_VIEW_ID = 0;
  
  public static final int LOCK_STATUS_NOT_LOCKED             = 1;
  public static final int LOCK_STATUS_LOCKED_BY_CURRENT_USER = 2;
  public static final int LOCK_STATUS_LOCKED_BY_OTHER_USER   = 3;
  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private void initPropertiesArray(){
    //FocDesc focDesc = getThisFocDesc();
    propertiesArray = new FProperty[thisFocDesc.getPropertyArrayLength()];    
  }
  
  protected void constructFocObject(FocConstructor constr) {
    try{
      //properties = new HashMap();
    	thisFocDesc = constr.getFocDesc();//getThisFocDesc();
      initPropertiesArray();
  
      if(constr != null){
        initIdentifierProperty(constr.getIdentifierValue());      
        masterObject = constr.getMasterObject();
      }else{
        initIdentifierProperty(null);
      }
      
      if(thisFocDesc != null && thisFocDesc.getRightsByLevelMode() != FocDesc.RIGHTS_BY_LEVEL_MODE_NONE){
        FObject user = new FObject(this, FField.RIGHT_LEVEL_USER_FIELD_ID, null);
        FInt level = new FInt(this, FField.RIGHT_LEVEL_FIELD_ID, 0);
        FString dateTime = new FString(this, FField.RIGHT_LEVEL_DATETIME_FIELD_ID, "");
        user.setValueLocked(true);
        level.setValueLocked(true);
        dateTime.setValueLocked(true);
      }
      //BElie
      if(thisFocDesc != null && thisFocDesc.isRevisionSupportEnabled()){
        new FInt(this, FField.CREATION_REVISION_FIELD_ID, 0);
        new FInt(this, FField.DELETION_REVISION_FIELD_ID, 0);
        new FObject(this, FField.NEW_ITEM_FIELD_ID, null);
      }
      
      if(thisFocDesc != null && thisFocDesc.isParentRevisionSupportEnabled()){
        new FInt(this, FField.REVISION_FIELD_ID, 1);
      }
      
      if(thisFocDesc != null){
        FField field = thisFocDesc.getFieldByID(FField.FLD_PROPERTY_FORMULA_LIST);
        if( field != null ){
          field.newProperty(this);  
        }
      }
      
      //EElie
      
      //BElias
      FCharField nameField = (FCharField)thisFocDesc.getFieldByID(FField.FLD_NAME);
      if(nameField != null){
      	nameField.newProperty(this);
      }
      
      FObjectField fatherNodeField = (FObjectField) thisFocDesc.getFieldByID(FField.FLD_FATHER_NODE_FIELD_ID);
      if(fatherNodeField != null){
      	fatherNodeField.newProperty(this);
      }
      
      //EElias
      //rr Begin
      FBoolField deprecatedField = (FBoolField)thisFocDesc.getFieldByID(FField.FLD_DEPRECATED_FIELD);
      if(deprecatedField != null){
        deprecatedField.newProperty(this);
      }
      //rr End
      
      if(thisFocDesc != null && thisFocDesc.isConcurrenceLockEnabled()){
        FObject user = new FObject(this, FField.LOCK_USER_FIELD_ID, null);
        user.setValueLocked(true);
      }

      if(thisFocDesc != null && thisFocDesc.isAddMasterMirror()){
      	new FMaster(this);
      }
      
    } catch (Exception e){
      Globals.logException(e);
    }
  }
     
  public FocObject(FocConstructor constr) {
    super(Globals.getDefaultAccessControl());
    constructFocObject(constr);
  }
  
  public FocObject(FocDesc desc){
  	super(Globals.getDefaultAccessControl());
  	FocConstructor constr = new FocConstructor(desc, null);  	
    constructFocObject(constr);
  }

  public FocObject(AccessControl accessControl) {
    super(accessControl);
  }

  public void dispose(){
    super.dispose();
    
    masterObject = null;
    
    if(listeners != null){
      listeners.clear();
      listeners = null;
    }
        
    if(propertiesArray != null){
      /*FocFieldEnum iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        iter.next();
        FProperty prop = (FProperty) iter.getProperty();
        if(prop != null){
          prop.dispose();
        }
      }

      iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        iter.next();        
        FProperty prop = (FProperty) iter.getProperty();
        if(prop != null){
          prop.dispose();
        }
      }*/
      
      for( int i = 0; i < propertiesArray.length; i++ ){
        FProperty prop = propertiesArray[i];
        if(prop != null){
          prop.dispose();
        }
      }
      
      propertiesArray = null;
    }
  }
  
  //BELie
  public void setCreationRevisionData(){
    if(thisFocDesc != null  && thisFocDesc.isRevisionSupportEnabled()){
      FProperty revNumber =  thisFocDesc.getRevisionPath().getPropertyFromObject(this);
      if(revNumber != null ){
        FInt creationRevision = new FInt(this, FField.CREATION_REVISION_FIELD_ID, 0);
        creationRevision.setInteger(revNumber.getInteger());  
      }
    }
  }
  
  public void setDeletionRevisionData(){
    if(thisFocDesc != null  && thisFocDesc.isRevisionSupportEnabled()){
      FProperty revNumber =  thisFocDesc.getRevisionPath().getPropertyFromObject(this);
      if(revNumber != null ){
        FInt deleteRevision = new FInt(this, FField.DELETION_REVISION_FIELD_ID, 0);
        deleteRevision.setInteger(revNumber.getInteger());
      }
    }
  }
  
  public void setModificationRevisionData(FocObject focObj) {
    if(thisFocDesc != null  && thisFocDesc.isRevisionSupportEnabled()){
      FProperty revNumber =  thisFocDesc.getRevisionPath().getPropertyFromObject(this);
      if(revNumber != null ){
        setPropertyObject(FField.NEW_ITEM_FIELD_ID, focObj);
        setPropertyInteger(FField.CREATION_REVISION_FIELD_ID, revNumber.getInteger());
      }
    }
  }
  
  public void incrementRevision(){
    setPropertyInteger(FField.REVISION_FIELD_ID, getPropertyInteger(FField.REVISION_FIELD_ID)+1);
    save();
  }
    
  public void addRelatedGuiComponent(Component comp){
    if(relatedGuiComponents == null){
      relatedGuiComponents = new ArrayList<Component>();  
    }
    relatedGuiComponents.add(comp);
  }
  
  public void removeRelatedGuiComponent(Component comp){
    if(relatedGuiComponents == null && relatedGuiComponents.contains(comp)){
      relatedGuiComponents.remove(comp);
    }
  }
  
  public void disableRelatedGuiComponents() {
    for( int i = 0; i < relatedGuiComponents.size(); i++){
      relatedGuiComponents.get(i).setEnabled(false);
    }
  }
  
  public void enableRelatedGuiComponents() {
    for( int i = 0; i < relatedGuiComponents.size(); i++){
      relatedGuiComponents.get(i).setEnabled(true);
    }
  }
  
  public void unlockPropertiesForRevision(){
    FProperty []props = propertiesArray;
    for(int i = 0; i < props.length; i++){
      if(!props[i].getFocField().isLockValueAfterCreation() || Globals.getApp().getGroup().allowNamingModif()){
        props[i].setValueLocked(false);
      }
    }
  }
  
  public void lockPropertiesForRevision(){
    FProperty []props = propertiesArray;
    for(int i = 0; i < props.length; i++){
      props[i].setValueLocked(true);  
    }
  }
  //EElie

  /*public void newFocProperties(){ 
	  FocFieldEnum enumer = new FocFieldEnum(getThisFocDesc(), FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
	  while(enumer != null && enumer.hasNext()){
	    FField field = (FField) enumer.next();
	    FField[] fieldArray = enumer.getFieldPath().getFieldArrayFromDesc(getThisFocDesc());
	    field = fieldArray[0];
	    if(field != null && field.getID() > 0){
	      FProperty property = field.newProperty(this);
	      property.setValueLocked(field.isAllwaysLocked() || field.isWithFormula());
	    }
    }
	  
	  enumer = new FocFieldEnum(getThisFocDesc(), FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
	  while(enumer != null && enumer.hasNext()){
	    FField field = (FField) enumer.next();
	    if(field != null && field.getID() > 0){
	      field.newProperty(this);
	    }
	  }
	  
	  forceControler(true);
  }*/
  
  public void newFocProperties(){
  	FocDesc thisFocDesc = getThisFocDesc();
  	int fieldsSize = thisFocDesc.getFieldsSize();
  	for(int i = 0; i < fieldsSize; i++){
  		FField field = thisFocDesc.getFieldAt(i);
  		if(field != null && field.getID() > 0){
	      FProperty property = field.newProperty(this);
	      property.setValueLocked(field.isAllwaysLocked() || field.isWithFormula());
	    }
  	}
	  forceControler(true);
	  computePropertiesWithFormula();
  }
  
  private void computePropertiesWithFormula(){
  	FocDesc thisFocDesc = getThisFocDesc();
  	int fieldsSize = thisFocDesc.getFieldsSize();
  	for(int i = 0; i < fieldsSize; i++){
  		FField field = thisFocDesc.getFieldAt(i);
  		if(field != null && field.isWithFormula()){
	      FProperty property = getFocProperty(field.getID());
	      field.computePropertyUsingFormulaIfNeeded(property);
	    }
  	}
  }

  public String getPropertyString(int fieldID){
  	FProperty prop = getFocProperty(fieldID);  	
  	return prop != null ? prop.getString() : "";
  }

  public String getPropertyString(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyString(field.getID()) : null;
  }

  public void setPropertyString(int fieldID, String str){
  	FProperty prop = getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setString(str);
  	}
  }

  public java.sql.Date getPropertyDate(int fieldID){
  	FDate prop = (FDate) getFocProperty(fieldID);  	
  	return prop != null ? prop.getDate() : null;
  }

  public java.sql.Date getPropertyDate(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyDate(field.getID()) : null;
  }

  public void setPropertyDate(int fieldID, java.sql.Date date){
  	FDate prop = (FDate)getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setDate(date);
  	}
  }

  public java.sql.Time getPropertyTime(int fieldID){
  	FTime prop = (FTime) getFocProperty(fieldID);  	
  	return prop != null ? prop.getTime() : null;
  }

  public java.sql.Time getPropertyTime(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyTime(field.getID()) : null;
  }

  public void setPropertyTime(int fieldID, java.sql.Time time){
  	FTime prop = (FTime)getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setTime(time);
  	}
  }

  public int getPropertyInteger(int fieldID){
  	FProperty prop = getFocProperty(fieldID);  	
  	return prop != null ? prop.getInteger() : 0;
  }

  public void setPropertyInteger(int fieldID, int val){
  	FProperty prop = getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setInteger(val);
  	}
  }

  public double getPropertyDouble(int fieldID){
  	FProperty prop = getFocProperty(fieldID);  	
  	return prop != null ? prop.getDouble() : 0;
  }

  public double getPropertyDouble(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyDouble(field.getID()) : 0;
  }

  public void setPropertyDouble(int fieldID, double val){
  	FProperty prop = getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setDouble(val);
  	}
  }

  public FocObject getPropertyObject(int fieldID){
  	FObject prop = (FObject)getFocProperty(fieldID);  	
  	return prop != null ? (FocObject) prop.getObject_CreateIfNeeded() : null;
  }

  public FocObject getPropertyObject(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyObject(field.getID()) : null;
  }

  public void setPropertyObject(int fieldID, FocObject obj){
  	FProperty prop = getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setObject(obj);
  	}
  }

  public FocList getPropertyList(int fieldID){
  	FList prop = (FList)getFocProperty(fieldID);  	
  	return prop != null ? (FocList) prop.getList() : null;
  }

  public FocList getPropertyList(int fieldID, int mode){
  	FocList list = null;
  	FList prop = (FList)getFocProperty(fieldID);
  	if(prop != null){
  		list = prop.getListWithoutLoad();  		
	  	if(mode == FocList.FORCE_RELOAD){
	  		list.reloadFromDB();
	  	}else if(mode == FocList.LOAD_IF_NEEDED){
	  		list.loadIfNotLoadedFromDB();
	  	}
  	}
  	return list;
  }
    
  public boolean getPropertyBoolean(int fieldID){
  	int i = getPropertyInteger(fieldID);
  	return i != 0;
  }

  public boolean getPropertyBoolean(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyBoolean(field.getID()) : null;
  }
  
  public void setPropertyBoolean(int fieldID, boolean b){
  	setPropertyInteger(fieldID, b ? 1 : 0);
  }

  public int getPropertyMultiChoice(int fieldID){
  	return getPropertyInteger(fieldID);
  }

  public void setPropertyMultiChoice(int fieldID, int choice){
  	setPropertyInteger(fieldID, choice);
  }

  public FocDesc getPropertyDesc(int fieldID){
  	IFDescProperty prop = (IFDescProperty) getFocProperty(fieldID);  	
  	return prop != null ? prop.getSelectedFocDesc() : null;
  }
  
  public void putFocPropertyWithSpecifiedIndex(FProperty prop, int index) {
    if (propertiesArray == null) {
      initPropertiesArray();
    }
    if (propertiesArray != null) {
      if(propertiesArray.length <= index){
        Globals.logString("++++++++++++++++++++ index = " + index + " lenght = " + propertiesArray.length + " Title " + prop.getFocField().getTitle());
      }
      propertiesArray[index] = prop;
    }
  }

  public void putFocProperty(FProperty prop) {
    if(prop != null){
      FField field = prop.getFocField();
      if (field != null) {        
        putFocPropertyWithSpecifiedIndex(prop, field.getIndexOfPropertyInArray());
      }
    }
  }
  
  public String getPropertyMultipleChoiceStringBased(int fieldID){
  	return getPropertyString(fieldID);
  }
  
  public void setPropertyMultipleChoiceStringBase(int fieldID, String choice){
  	setPropertyString(fieldID, choice);
  }
  
  public boolean isContentValid(boolean displayMessage){
    boolean valid = !isContentValidMessageOn();
    if(valid){
      FocDesc focDesc = getThisFocDesc();      
      for(int i=0; i<focDesc.mandatoryFieldCount() && valid; i++){
        FField field = focDesc.mandatoryFieldAt(i);
        FProperty prop = getFocProperty(field.getID());
        
        valid = !prop.isEmpty();
        if(!valid && displayMessage){
          StringBuffer message = new StringBuffer("Field \""+ field.getName() + " (" + field.getTitle()+")"+"\""+" in table \""+getThisFocDesc().getStorageName()+"\" cannot remain empty!\n");
          message.append("Do one of the following:\n");
          message.append("  -    Fill the mandatory fields\n");
          message.append("  - or If you are in a table, you can delete the record by hitting 'Del'\n");
          message.append("  - or If you are in a normal entry field, you can use the 'Cancel' or 'exit' button\n");
          popupContentValidMessage(message);
        }
      }
    }
    return valid;
  }
  
  public boolean isContentValidMessageOn() {
    return contentValidMessageOn;
  }

  public void setContentValidMessageOn(boolean contentValidMessageOn) {
    this.contentValidMessageOn = contentValidMessageOn;
  }

  public void popupContentValidMessage(String message){
    setContentValidMessageOn(true);
    Globals.getDisplayManager().popupMessage(message);
    setContentValidMessageOn(false);    
  }

  public void popupContentValidMessage(StringBuffer message){
    popupContentValidMessage(message.toString());
  }

  public boolean isDeletable() {
    return deletable;
  }
  
  public void setDeletable(boolean deletable) {
    this.deletable = deletable;
  }
  
  /**
   * @param fieldID
   * @return
   */
  public FProperty getFocProperty(int fieldID) {
    FProperty property = null;
    FField field = null;
    
    //BAntoineS - AUTOINCREMENT
    if (fieldID == FField.MASTER_REF_FIELD_ID) {
      if (masterObject != null) {
      	if(masterObject.needsAssignReference()){
      		
      	}
        //masterObject.assignReferenceIfNeeded();
        property = masterObject.getIdentifierProperty();
      }
    }
    //EAntoineS - AUTOINCREMENT
    if(property == null){
      if (fieldID != FField.REF_FIELD_ID && !loadedFromDB && !isCreated()) {
        load();
      }
      
      field = field == null ? getThisFocDesc().getFieldArrayByID(fieldID) : field;      
      field = field == null ? getThisFocDesc().getFieldByID(fieldID) : field;      

      if(field != null && propertiesArray != null){
        if(propertiesArray == null){
          Globals.logString("Null propertiesArray :"+this.getClass().getName());          
        }
        property = (FProperty) propertiesArray[field.getIndexOfPropertyInArray()];
      }
    }

    //If Property not found we check if it is for ArrayField
    if(property == null){
      FocDesc thisFocDesc = getThisFocDesc();
      if(thisFocDesc != null){
        FFieldArray fieldArray = (FFieldArray)thisFocDesc.getFieldArrayByID(fieldID);
        if(fieldArray != null){
          FField curField = fieldArray.getCurrentField();
          if(curField != null && curField.getID() != fieldID){
            property = (FProperty) propertiesArray[curField.getIndexOfPropertyInArray()];
          }
        }
      }
    }
    
    return property;
  }

  public void lockAllproperties(){
    FocFieldEnum iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if(prop != null){
        prop.setValueLocked(true);
      }
    }
  }
  
  public void unlockAllProperties(){
    FocFieldEnum iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if(prop != null){
        prop.setValueLocked(false);
      }
    }
  }
  
  public String toString() {
    String str = "";
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
    while (enumer != null && enumer.hasNext()) {
      enumer.next();
      FProperty prop = enumer.getProperty();
      if (prop != null) {
        str = str + prop.getString();
      }
    }
    return str;
  }

  public String toString_Super() {
    return super.toString();
  }

  public FPanel newUserLevelPanel(){
    FPanel panel = null;
    FInt levelProp = (FInt)getFocProperty(FField.RIGHT_LEVEL_FIELD_ID);
    FString dateTime = (FString) getFocProperty(FField.RIGHT_LEVEL_DATETIME_FIELD_ID);
    
    if(levelProp.getInteger() > 0){
      panel = new FPanel();
      FObject userProp = (FObject)getFocProperty(FField.RIGHT_LEVEL_USER_FIELD_ID);
      FocUser objUser = (FocUser) userProp.getObject_CreateIfNeeded();
      
      FGLabel label = new FGLabel(levelProp.getInteger() + ":" + objUser.getName() + " " + dateTime.getString());
      
      panel.add(label, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
    }
    return panel;
  }
  
  public int getRightsLevel(){
    FInt levelProp = (FInt)getFocProperty(FField.RIGHT_LEVEL_FIELD_ID);
    return levelProp != null ? levelProp.getInteger() : 0;
  }

  public FocUser getRightsLevelLastUser(){
    FObject userProp = (FObject)getFocProperty(FField.RIGHT_LEVEL_USER_FIELD_ID);
    return userProp != null ? (FocUser) userProp.getObject_CreateIfNeeded() : null;
  }

  public FPanel popup() {
    FPanel pan = newDetailsPanel(FocObject.DEFAULT_VIEW_ID);
    Globals.getDisplayManager().changePanel(pan);
    return pan;
  }

  public FocObject getThis() {
    return this;
  }

  /**
   * @return
   */
  public FocDesc getThisFocDesc() {
  	return thisFocDesc;
  }

  public FPanel newDetailsPanel(int viewID){
    FPanel panel = null;
    try {
    	Class cls = getThisFocDesc().getGuiDetailsPanelClass();
    	Class[]     argsDeclare = {FocObject.class, int.class};
    	Object[]    args        = {this, viewID };
    	Constructor constr      = cls.getConstructor(argsDeclare);
    	
      panel = (FPanel) constr.newInstance(args);

    } catch (Exception e) {
      Globals.logException(e);
    }
    return panel;  	
  }
  
  public FocFieldEnum newFocFieldEnum(int category, int level) {
    return new FocFieldEnum(this.getThisFocDesc(), this, category, level);
  }

  /**
   * @return
   */
  public FocObject getMasterObject() {
    return masterObject;
  }

  /**
   * @param object
   */
  public void setMasterObject(FocObject object) {
    masterObject = object;
  }

  public void copyPropertiesFrom(FocObject sourceObj){
    FocDesc focDesc = this.getThisFocDesc();
    FField identifierField = focDesc.getIdentifierField();
    
    FocFieldEnum iter = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      FField field = (FField) iter.next();
      if(field != null && field != identifierField && field.getID() != FField.REF_FIELD_ID && field.getID() != FField.MASTER_REF_FIELD_ID){
        FProperty thisProp = this.getFocProperty(field.getID());
        FProperty srcProp = sourceObj.getFocProperty(field.getID());
        thisProp.copy(srcProp);
      }
    }
    //Globals.logString(dupObj.getDebugInfo());        
    iter = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      FField field = (FField) iter.next();
      if(field != null && field != identifierField){
        FProperty thisProp = this.getFocProperty(field.getID());
        FProperty srcProp = sourceObj.getFocProperty(field.getID());
        thisProp.copy(srcProp);

        FList dupListProp = (FList) thisProp;
        FocList list = (FocList) dupListProp.getList();
        list.setLoaded(true);
      }
    }
  }
  
  /*public FocObject duplicate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification){
    FocObject dupObj = null;
    try{
      FocDesc focDesc = this.getThisFocDesc();
      
      if(initialTargetObj == null){
        FocConstructor constr = new FocConstructor(focDesc, null, newMasterObject);
        dupObj = constr.newItem();
        //dupObj = focDesc.newClassInstance(constr);
      }else{
        dupObj = initialTargetObj;
        dupObj.setMasterObject(newMasterObject);
      }
      
      //Globals.logString(dupObj.getDebugInfo());
      if(dupObj != null && focDesc != null){
        dupObj.setCreated(true);
        dupObj.copyPropertiesFrom(this);
        
        //B-DUP
        if(callDuplicateModification){
          dupObj.duplicationModification(this);
        }
        dupObj.validate(false);
        //E-DUP
      }
    }catch (Exception e){
      Globals.logException(e);
    }
    return dupObj;
  }*/
  
  public FocObject duplicate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification){
    return duplicate(initialTargetObj, newMasterObject, callDuplicateModification, true);
  }
  
  public FocObject duplicateNoValidate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification){
    return duplicate(initialTargetObj, newMasterObject, callDuplicateModification, false);
  }
  
  private FocObject duplicate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification, boolean validate){
    FocObject dupObj = null;
    try{
      FocDesc focDesc = this.getThisFocDesc();
      
      if(initialTargetObj == null){
        FocConstructor constr = new FocConstructor(focDesc, null, newMasterObject);
        dupObj = constr.newItem();
        //dupObj = focDesc.newClassInstance(constr);
      }else{
        dupObj = initialTargetObj;
        dupObj.setMasterObject(newMasterObject);
      }
      
      //Globals.logString(dupObj.getDebugInfo());
      if(dupObj != null && focDesc != null){
        dupObj.setCreated(true);
        dupObj.copyPropertiesFrom(this);
        
        //B-DUP
        if(callDuplicateModification){
          dupObj.duplicationModification(this);
        }
        if(validate){
        	dupObj.validate(false);
        }
        //E-DUP
      }
    }catch (Exception e){
      Globals.logException(e);
    }
    return dupObj;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.access.AccessSubject#generalFocActionPerformed(b01.foc.event.FocEvent)
   */
  public void focActionPerformed(FocEvent evt) {
    if (evt != null && evt.getSourceType() == FocEvent.TYPE_ORDER) {
      switch (evt.getID()) {
      case FocEvent.ID_RESTORE:
        // this.restore();
        break;
      }
    }
  }
  
  public StringBuffer getDebugInfo(){
    StringBuffer str = new StringBuffer();
    FocDesc focDesc = getThisFocDesc();
    if(focDesc != null){
      str.append(focDesc.getStorageName());
      str.append("\n");
      FocFieldEnum iter = new FocFieldEnum(focDesc , this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        FField field = (FField) iter.next();
        FProperty prop = iter.getProperty();
        if(field != null && prop != null){
          str.append(field.getName());
          str.append(";");        
          str.append(field.getTitle());        
          str.append(";");        
          str.append(prop.getString());
          str.append(";");        
          str.append(prop);
          str.append("\n");          
        }
      }
      iter = new FocFieldEnum(focDesc , this, FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        FField field = (FField) iter.next();
        FProperty prop = iter.getProperty();
        if(field != null && prop != null){
          FList listProp = (FList) prop;
          
          str.append(field.getName());
          str.append(";");        
          str.append(field.getTitle());        
          str.append(";");        
          str.append(listProp.getList().size());
          str.append("\n");          
        }
      }
    }
    
    return str;
  }
  
  public Component getGuiComponent(int fieldID){
    FProperty prop = getFocProperty(fieldID);
    return prop != null ? prop.getGuiComponent() : null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // COMPARE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public int compareUniqueKey(FocObject focObj2){
    int compare = -1;
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
    if(enumer.hasNext()){
      compare = 0;
      while (enumer.hasNext() && compare == 0) {
        FField fld = (FField) enumer.next();
        FProperty thisProp = enumer.getProperty();
        FProperty otherProp = (fld != null) ? focObj2.getFocProperty(fld.getID()) : null;
        if (thisProp != null && otherProp != null) {
          compare = thisProp.compareTo(otherProp);
        }
      }
    }
    return compare;
  }
  
  public int compareTo(Object obj) {
    FocObject focObj2 = (FocObject) obj;
    FocDesc focDesc = getThisFocDesc();
    FocDesc focDesc2 = null;
    int compare = 0;

    if (obj == null) compare = 1;

    if (compare == 0) {
      focDesc2 = focObj2.getThisFocDesc();
      if (focDesc2 == null) compare = 0;
      else if (!focDesc.equals(focDesc2)) {
        compare = 1;
      }
    }

    boolean compDone = false;
    if (compare == 0) {
      FProperty idProp = this.getIdentifierProperty();
      FProperty idProp2 = focObj2.getIdentifierProperty();
      FField field = focDesc.getIdentifierField();
      if (idProp != null && idProp2 != null && field != null) {
        if (field.getID() != FField.REF_FIELD_ID || idProp.getInteger() == 0 || idProp2.getInteger() == 0) {

        } else {
          compare = idProp.compareTo(idProp2);
          compDone = true;
        }
      }
    }

    if (compare == 0 && !compDone) {
      compare = compareUniqueKey(focObj2);
      /*
      FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
      while (enumer.hasNext() && compare == 0) {
        FField fld = (FField) enumer.next();
        FProperty thisProp = enumer.getProperty();
        FProperty otherProp = (fld != null) ? focObj2.getFocProperty(fld.getID()) : null;
        if (thisProp != null && otherProp != null) {
          compare = thisProp.compareTo(otherProp);
        }
      }
      */
    }

    return compare;
  }

  //B-HASHCODE  
  /*
  public boolean equals(Object obj) {
    return (compareTo((FocObject) obj) == 0);
  }
  */
  //E-HASHCODE  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // CONCURRENT ACCESS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public void updateLockFields(){
    dbUpdate(true);
  }
  
  private void lock(){
    FObject lockUserProp = (FObject) getFocProperty(FField.LOCK_USER_FIELD_ID);
    lockUserProp.setObject(Globals.getApp().getUser());
    updateLockFields();    
    Globals.getDBManager().addLockedObject(this);
  }

  public void unlock(){
    FObject lockUserProp = (FObject) getFocProperty(FField.LOCK_USER_FIELD_ID);
    if(lockUserProp != null){
      lockUserProp.setObject(null);    
    }
    updateLockFields();
    Globals.getDBManager().removeLockedObject(this);
  }

  public boolean isLocked(boolean popupMessage){
  	return isLocked(popupMessage, null);
  }
  
  public boolean isLocked(boolean popupMessage, String objectDescription){    
    boolean isLocked = false;
    FocDesc desc = getThisFocDesc();
    if(desc.isConcurrenceLockEnabled()){
      FObject lockUserProp = (FObject) getFocProperty(FField.LOCK_USER_FIELD_ID);
      lockUserProp.setObject(null);
      load();
      FocUser lockUser = (FocUser) lockUserProp.getObject_CreateIfNeeded();
      isLocked = lockUser != null && lockUser.getReference().getInteger() > 0;
      
      if(isLocked && popupMessage){
      	if(lockUser.getReference().getInteger() == Globals.getApp().getUser().getReference().getInteger()){
      		String opts[] = {"Unlock", "Keep Locked"};
      		int dialogRet = JOptionPane.NO_OPTION;
          dialogRet = JOptionPane.showOptionDialog(Globals.getDisplayManager().getMainFrame(), 
          		"Object locked by yourself '"+lockUser.getName()+"' do you wish to:\n  1- UNLOCK the object and get full access\n  2- KEEP LOCKED and get read only access\n\nNB: If you select UNLOCK, make sure you don't have the object opened in another window.\n    otherwise you will create access conflict.",
          		objectDescription != null ? "Locked object : "+objectDescription : "Locked object", 
          		JOptionPane.YES_NO_OPTION,
          		JOptionPane.WARNING_MESSAGE,
          		null,
          		opts, "Keep Locked");
          isLocked = dialogRet == JOptionPane.NO_OPTION;                   
      	}else{
          Globals.getDisplayManager().popupMessage("Object locked by user "+lockUser.getName()+"\nYour access will be in read only mode.");          
      	}
      }
      
      if(isLocked){
        setLockedByConcurrence(true);
      }else{
        setLockedByConcurrence(false);
        lock();
      }
    } 
    return isLocked;
  }
  
  public boolean isLockedByConcurrence() {
    return lockedByConcurrence;
  }
  
  public void setLockedByConcurrence(boolean lockedByConcurrence) {
    this.lockedByConcurrence = lockedByConcurrence;
  }
  
  public int getLockStatus(){
    int res = LOCK_STATUS_NOT_LOCKED;
    FObject lockUserProp = (FObject) getFocProperty(FField.LOCK_USER_FIELD_ID);
    lockUserProp.setObject(null);
    load();
    FocUser lockUser = (FocUser) lockUserProp.getObject_CreateIfNeeded();
    if(lockUser != null){
      if(Globals.getApp().getUser().getName().equals(lockUser.getName())){
        res = LOCK_STATUS_LOCKED_BY_CURRENT_USER;
      }else{
        res = LOCK_STATUS_LOCKED_BY_OTHER_USER;
      }
    }
    return res;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ACCESS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public void setFatherSubject(AccessSubject fatherSubject) {
    super.setFatherSubject(fatherSubject);
    if(getThisFocDesc().isTreeDesc() && fatherSubject instanceof FocList){
      FObject prop = (FObject) getFocProperty(getThisFocDesc().getFObjectTreeFatherNodeID());
      if(prop != null){
        prop.setLocalSourceList((FocList) fatherSubject);
      }
    }
  }
  
  /* (non-Javadoc)
   * @see b01.foc.access.AccessSubject#statusModification(int)
   */
  protected void statusModification(int statusModified) {
    if(statusModified == STATUS_CREATED){
      boolean lockNamesValues = true;
      if(isCreated()){
        lockNamesValues = false;
      }else{
        FocGroup focGroup = Globals.getApp().getGroup();
        boolean groupAllowNamingModif = focGroup != null && focGroup.allowNamingModif();         
        lockNamesValues = !groupAllowNamingModif;
      }
      
      if(lockNamesValues){
        Iterator iter = this.getThisFocDesc().newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
        while(iter != null && iter.hasNext()){
          FField field = (FField) iter.next();
          if(field != null && field.isLockValueAfterCreation()){
            FProperty thisProp = this.getFocProperty(field.getID());
            thisProp.setValueLocked(lockNamesValues);
          }
        }
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.access.AccessSubject#childFocActionPerformed(b01.foc.event.FocEvent)
   */
  public void childStatusModification(AccessSubject child) {
    if(child.isModified()){
      FocDesc desc = getThisFocDesc();
      for(int i=0; i<desc.getFieldsSize(); i++){
        FField field = desc.getFieldAt(i);
        if(FInLineObjectField.class.isInstance(field)){
          FInLineObject obj = (FInLineObject) getFocProperty(field.getID());
          FocObject inLineFocObj = (FocObject) obj.getObject();
          if(inLineFocObj == child){
            setModified(true);
          }
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.access.AccessSubject#childStatusUndo(b01.foc.access.AccessSubject)
   */
  public void childStatusUndo(AccessSubject childSubject) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.access.AccessSubject#childValidated(b01.foc.access.AccessSubject)
   */
  public void childValidated(AccessSubject childSubject) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.access.AccessSubject#executeFocAction(b01.foc.event.FocEvent)
   */
  
  public void commitStatusToDatabase(){
    if(isDbResident()){
      if (this.isDeleted()) {
        this.delete();
      }else if (this.isCreated() || this.isModified()) {
        if(getThisFocDesc().getRightsByLevelMode() != FocDesc.RIGHTS_BY_LEVEL_MODE_NONE){
          FObject userProp = (FObject)getFocProperty(FField.RIGHT_LEVEL_USER_FIELD_ID);
          userProp.setObject(Globals.getApp().getUser());
          FInt rightsLevelProp = (FInt)getFocProperty(FField.RIGHT_LEVEL_FIELD_ID);
          rightsLevelProp.setInteger(Globals.getApp().getUser().getRightsLevel());
          FString dateTimeProp = (FString)getFocProperty(FField.RIGHT_LEVEL_DATETIME_FIELD_ID);
          dateTimeProp.setString(Globals.getDBManager().getCurrentTimeStamp());
        }
        
        //BElias
        FocFieldEnum fEnum = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
        while(fEnum != null && fEnum.hasNext()){
        	fEnum.nextField();
        	FProperty prop = fEnum.getProperty();
          if( prop != null && prop instanceof FObject){
            FocObject obj = (FocObject)prop.getObject();
            if( obj != null && obj.isCreated()){
              obj.commitStatusToDatabaseWithPropagation();
            }
          }
        }
        /*
        for( int i = 0; i < propertiesArray.length; i++){
          FProperty prop = propertiesArray[i];
          if( prop != null && prop instanceof FObject){
            FocObject obj = (FocObject)prop.getObject();
            if( obj != null && obj.isCreated()){
              obj.commitStatusToDatabaseWithPropagation();
            }
          }
        }
        */
        //EElias
        
        this.save();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.access.AccessSubject#undoStatus()
   */
  public void undoStatus() {
    if (this.isModified()) {
      this.restore();
    }else if(this.isCreated() && hasRealReference()){
      //In this case we have maybe saved info related to this object
      //even though we did not validate the object itself
      //Example: We are creating an item, we entered into sub screens and saved some slaves
      //         then we decided not to save the main item.
      this.delete();
    }
    this.resetStatus();
  }

  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.access.AccessSubject#doBackup()
   */  
  public void doBackup(){
    this.backup();
  }  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LISTENERS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public void addListener(FocListener listener) {
    if (listeners == null) {
      listeners = new ArrayList<FocListener>();
    }
    listeners.add(listener);
  }

  public void removeListener(FocListener listener) {
    if (listeners != null) {
      listeners.remove(listener);
    }
  }

  public void fireEvent(int id) {
    if (listeners != null) {
      FocEvent focEvt = new FocEvent(this, FocEvent.composeId(FocEvent.TYPE_OBJECT, id), "");
      for (int i = 0; i < listeners.size(); i++) {
        FocListener listener = (FocListener) listeners.get(i);
        if (listener != null) {
          listener.focActionPerformed(focEvt);
        }
      }
    }
  }  

  public void scanPropertiesAndNotifyListeners(){
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while (enumer.hasNext()) {
      FField fld = (FField) enumer.next();
      if(fld.getID() != FField.MASTER_REF_FIELD_ID){
        FProperty prop = enumer.getProperty();
        if(prop != null){
          prop.notifyListeners();
        }
      }
    }
  }
  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // REFERENCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public FReference getReference() {
    FocDesc focDesc = getThisFocDesc();
    FReference ref = (FReference) this.getFocProperty(FField.REF_FIELD_ID);
    if (ref == null && focDesc.getWithReference()) {
      ref = new FReference(this);
    }
    return ref;
  }
  
  public void setReference(int ref){
    FReference refProp = getReference();

    if (refProp != null) {      
      refProp.setInteger(ref);
    }
    
    this.isTempReference = false;
  }

  public boolean referenceNeeded() {
    FocDesc focDesc = this.getThisFocDesc();
    return focDesc != null && focDesc.getWithReference() && getReference().getInteger() == 0;
  }

  public boolean setTemporaryReferenceIfNeeded(int ref) {
    boolean needed = referenceNeeded(); 
    if (referenceNeeded()) {
      setReference(ref);
      this.isTempReference = true;
    }
    return needed;
  }

  //BAntoineS - AUTOINCREMENT
  public boolean needsAssignReference(){
    FocDesc focDesc = this.getThisFocDesc();    
    return (referenceNeeded() || isTempReference) && focDesc.getDBResident();     
  }
  //EAntoineS - AUTOINCREMENT
  
  public void assignReferenceIfNeeded(boolean callFromInsertWithProviderSpecificTreatment) {
    FocDesc focDesc = this.getThisFocDesc();
    //BAntoineS - AUTOINCREMENT
    if (needsAssignReference()) {
    //EAntoineS - AUTOINCREMENT
    	//FocConstructor constr = new FocConstructor(NumericalReference.getFocDesc(), focDesc.getStorageName(), null);
    	//NumericalReference numRef = (NumericalReference)constr.newItem();
    	
      //BAntoineS - AUTOINCREMENT
      if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
        if(!callFromInsertWithProviderSpecificTreatment){
        	Globals.logString("!!! Call used in Oracle, but will not work for MySQL!!!");
        }
      	int ref = 0;
      	try{
      		ref = focDesc.getNextSequence();
          setReference(ref);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
      }else{
        if(!callFromInsertWithProviderSpecificTreatment){
        	Globals.logString("!!! REF = 0 SAVED !!! Counld not Assign Reference because AUTOINCREMENT !!!");
        }
      }
      //EAntoineS - AUTOINCREMENT
      
      setCreated(true);
    }
  }
  
  public boolean hasRealReference(){
    boolean hasRealRef = false;
    FocDesc focDesc = this.getThisFocDesc();
    if(focDesc != null){
      hasRealRef = focDesc.getWithReference() && getReference().getInteger() > 0 && !isTempReference;
    }
    return hasRealRef;
  }
  
  public FProperty getIdentifierProperty() {
    FProperty property = null;
    FocDesc focDesc = this.getThisFocDesc();
    if (focDesc != null) {
      FField refField = focDesc.getIdentifierField();
      if (refField != null) {
        property = this.getFocProperty(refField.getID());
      }
    }
    if(property == null){
      //Globals.logException(new Exception("FocObject has no identifier property!"));
    }
    return property;
  }
  
  public FProperty initIdentifierProperty(Object identifierObj) {
    FocDesc focDesc = getThisFocDesc();
    FReference ref = (FReference) this.getFocProperty(FField.REF_FIELD_ID);
    if (focDesc != null && ref == null && focDesc.getWithReference()) {
      FocRef focRef = identifierObj != null ? (FocRef) identifierObj : null;
      //FocRef focRef = new FocRef(intVal.intValue());
      ref = new FReference(this, FField.REF_FIELD_ID, focRef != null ? (FocRef)focRef.clone() : null);
    }
    return ref; 
  }
  
  public void loadReferenceFromDatabaseAccordingToKey(){
  	SQLSelectFindReferenceForUniqueKey selectReference = new SQLSelectFindReferenceForUniqueKey(this);
  	selectReference.execute();
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DATABASE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public int referenceCheck_GetNumber(StringBuffer message){
    int nbOfReferences = 0;
    FocDesc focDesc = getThisFocDesc();
    
    //Globals.logString("Getting ref locations for obj: "+getDebugInfo());
    
    Iterator iter = focDesc.referenceLocationIterator();
    while(iter != null && iter.hasNext()){
      ReferenceChecker refCheck = (ReferenceChecker)iter.next();
      if(refCheck != null){
        nbOfReferences += refCheck.getNumberOfReferences(this, message);
      }
    }
    
    return nbOfReferences;
  }

  public void referenceCheck_PopupDialog(int nbOfReferences, StringBuffer message){
    if(nbOfReferences > 0){
      FocDesc focDesc = getThisFocDesc();
      
      JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
          "Object "+focDesc.getStorageName()+":"+getIdentifierProperty().getString()+" is referenced "+nbOfReferences+" times.\nPlease call 01Barmaja for further assistance on deleting this object.\nReferenced in:"+message,
          "01Barmaja",
          JOptionPane.DEFAULT_OPTION,
          JOptionPane.INFORMATION_MESSAGE,
          null);
    }
  }
  
  public void referenceCheck_RedirectToNewFocObject(FocObject focObjectToRedirectTo){
  	Iterator iter = getThisFocDesc().referenceLocationIterator();
    while(iter != null && iter.hasNext()){
      ReferenceChecker refCheck = (ReferenceChecker)iter.next();
      if(refCheck != null){
      	refCheck.redirectReferencesToNewFocObject(this, focObjectToRedirectTo);
      }
    }
  }
  
  public boolean dbDelete() {
    boolean successfull = false;
    
    DBManager dbManager = Globals.getDBManager();
    if (dbManager != null) {
      FocDesc focDesc = getThisFocDesc();

      if (focDesc != null) {
        if(!focDesc.getWithReference() || hasRealReference()){
          StringBuffer message = new StringBuffer();
          //Here we should make sure that the object is not referenced before deleting
          int nbOfReferences = referenceCheck_GetNumber(message);
          
          if(nbOfReferences == 0){
            SQLFilter filter = new SQLFilter(this, SQLFilter.FILTER_ON_IDENTIFIER);
            SQLDelete sqlDelete = new SQLDelete(focDesc, filter);
            sqlDelete.execute();
            successfull = true;
          }else{
            referenceCheck_PopupDialog(nbOfReferences, message);
          }
        }else{
          successfull = true;
        }
      }      
    }
  
    if(successfull){
      setDeletionExecuted(true);
    }
    
    return successfull;
  }

  public void dbInsert() {
    DBManager dbManager = Globals.getDBManager();
    if (dbManager != null) {
      FocDesc focDesc = getThisFocDesc();

      if (focDesc != null) {
        SQLInsert sqlInsert = new SQLInsert(focDesc, this);
        sqlInsert.execute();
      }
    }
  }

  private void dbUpdate(boolean forLockFieldsOnly) {
    DBManager dbManager = Globals.getDBManager();
    if (dbManager != null) {
      FocDesc focDesc = getThisFocDesc();

      if (focDesc != null) {
        SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
        if(forLockFieldsOnly){
          sqlUpdate.addQueryField(FField.LOCK_USER_FIELD_ID);
        }
        sqlUpdate.execute();
      }
    }
  }

  private void postUpdate(){
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while (enumer.hasNext()) {   
      FField focField = (FField) enumer.next();
      FProperty prop = getFocProperty(focField.getID());
      if( !focField.isIncludeInDBRequests() && prop.isModified() ){
        SQLUpdate sqlUpdate = new SQLUpdate(getThisFocDesc(), this);
        sqlUpdate.addQueryField(focField.getID());
        sqlUpdate.execute();
        prop.setModified(false);
      }
    }
  }
  
  public void dbUpdate() {
    dbUpdate(false);
    postUpdate();
  }

  protected boolean dbSelect() {
    boolean error = true;
    if(this.getThisFocDesc().getDBResident()){
      setLoadedFromDB(true);
      DBManager dbManager = Globals.getDBManager();
      if (dbManager != null) {
        FocDesc focDesc = getThisFocDesc();
        SQLFilter filter = new SQLFilter(this, SQLFilter.FILTER_ON_IDENTIFIER);
  
        if (focDesc != null) {
          SQLSelect sqlSelect = new SQLSelect(this, focDesc, filter);
          if(sqlSelect.execute()){
            setLoadedFromDB(false);
            error = false;
          }
        }
      }
      
      if(Globals.getApp().getRightsByLevel() != null){
        Globals.getApp().getRightsByLevel().lockValuesIfNecessary(this);
      }
      setModified(false);
    }
    return error;
  }

  public void save() {
    /*
    if(isCreated() || isModified()){
      FocFieldEnum fieldsIterator = getThisFocDesc().newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
      while (fieldsIterator.hasNext()) {
        FField focField = (FField) fieldsIterator.next();
      }
    }
    */
    //BELie
    for( int i = 0; i < propertiesArray.length; i++){
      FProperty prop = propertiesArray[i];
      if( prop != null && prop instanceof FObject){
        FocObject obj = (FocObject)prop.getObject();
        if( obj != null && obj.isCreated()){
        	//should not get here because the object must have bean validated 
        	//upon commitStatusToDataBase() when we have iterate the properties 
        	//and invoked commitStatusToDataBaseWithPropagation() on the focObjects
          obj.save();
        }
      }
    }
    //EELie
    if (isCreated()) {
      
      
      dbInsert();
      setCreated(false);// Is useless if the command is comming from the Access
                        // Subject Interface
    } else if (isModified()) {
      dbUpdate();
      setModified(false);// Is useless if the command is comming from the
                          // Access Subject Interface
    }
    //B-DUP
    setLoadedFromDB(true);
    //E-DUP
    fireEvent(FocEvent.ID_SAVE);
  }

/*  public boolean validate( boolean checkValidity){
    boolean validated = true;
    for( int i = 0; i < propertiesArray.length; i++){
      FProperty prop = propertiesArray[i];
      if( prop != null && prop instanceof FObject){
        FocObject obj = (FocObject)prop.getObject();
        if( obj != null && obj.isCreated()){
          validated = validated && obj.validate(false);
        }
      }
    }
    validated = validated && super.validate(checkValidity);
    return validated;
  }*/
  
  
  public boolean delete() {
    boolean succcess = dbDelete();

    if(succcess){
      FocDesc focDesc = getThisFocDesc();
      FocFieldEnum fieldsIterator = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
      fieldsIterator.reverseOrder();
      while (fieldsIterator.hasNext()) {
        FField focField = (FField) fieldsIterator.next();
  
        if (focField.isDBResident()) {
          FFieldPath fieldPath = fieldsIterator.getFieldPath();
          FList list = (FList) fieldPath.getPropertyFromObject(this);
          FocList focListToDelete = list.getList();
          focListToDelete.deleteFromDB();
        }
      }
      fireEvent(FocEvent.ID_DELETE);      
    }   
    return succcess;
  }

  public boolean load() {
    boolean error = true;
    if(!isCreated() && !duringLoad){
      duringLoad = true;
      fireEvent(FocEvent.ID_BEFORE_LOAD);
      error = dbSelect();
      fireEvent(FocEvent.ID_LOAD);
      duringLoad = false;
    }
    return error;
  }

  private void backupRestore(boolean backup) {
    FocDesc focDesc = getThisFocDesc();
    if (backup || !isCreated()) {
      Iterator iter = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        FField focField = (FField) iter.next();
        if(focField != null){
          FProperty prop = this.getFocProperty(focField.getID());
          if(prop != null){
            if (backup) {
              prop.backup();
            } else {
              prop.restore();
            }
          } 
        }
      }
    }
  }

  public void restore() {
    backupRestore(false);
    fireEvent(FocEvent.ID_RESTORE);
  }

  public void backup() {
    backupRestore(true);
    fireEvent(FocEvent.ID_BACKUP);
  }

  /**
   * @return
   */
  public boolean isLoadedFromDB() {
    return loadedFromDB;
  }

  /**
   * @param b
   */
  public void setLoadedFromDB(boolean b) {
    loadedFromDB = b;
    /*
    if(b){
      this.backup();
    }
    */
  }
  
  public void duplicationModification(FocObject source){
    
  }
  
  public boolean isTempReference() {
    return isTempReference;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(FocList focList, FocDesc focDesc, int mode){
    return focDesc.getList(focList, mode);
  }

  public static FocList getList(FocList focList, FocDesc focDesc, int mode, FocListOrder order){
    return focDesc.getList(focList, mode, order);
  }

  public static FocList getList(FocList focList, FocDesc focDesc, int mode, FocListOrder order, SQLFilter filter){
    return focDesc.getList(focList, mode, order, filter);
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // XML
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public Element writeXML_MainNode(Document doc, Element root, String nodeName){
    Element mainNode = doc.createElement(nodeName);
    if(root == null){
      doc.appendChild(mainNode);
    }else{
      root.appendChild(mainNode);        
    }
    return mainNode;
  }
  
  public Element writeXML_MainNode(Document doc, Element root){
    return writeXML_MainNode(doc, root, getThisFocDesc().getStorageName());
  }

  public boolean writeXML_DoPrintListItem(FList listProp, FocObject object){
    return true;
  }
  
  public void writeXML_ForProperty(Document doc, Element father, FField field, FProperty property){
    Element elmt = doc.createElement(field.getName());
    father.appendChild(elmt);
    
    Text txt = doc.createTextNode((String)property.getTableDisplayObject(null));
    elmt.appendChild(txt);
  }

  public void writeXML_ForList(Document doc, Element father, FListField listField, FList listProp, boolean includeReference, boolean includeLists){
    /*FocList focList = listProp.getList();
    if(focList != null && focList.size() > 0){
      Element elmt = doc.createElement(listField.getName());
      father.appendChild(elmt);
      
      for(int i=0; i<focList.size(); i++){
        FocObject focObj = (FocObject) focList.getFocObject(i);
        if(focObj != null && writeXML_DoPrintListItem(listProp, focObj)){
          focObj.writeXML(doc, elmt, includeReference, includeLists);
        }
      }            
    }*/
  	writeXML_ForList(doc, father, listField.getName(), listProp, includeReference, includeLists);
  }
  
  public void writeXML_ForList(Document doc, Element father, String nodeTitle, FList listProp, boolean includeReference, boolean includeLists){
    FocList focList = listProp.getList();
    if(focList != null && focList.size() > 0){
      Element elmt = doc.createElement(nodeTitle);
      father.appendChild(elmt);
      
      for(int i=0; i<focList.size(); i++){
        FocObject focObj = (FocObject) focList.getFocObject(i);
        if(focObj != null && writeXML_DoPrintListItem(listProp, focObj)){
          focObj.writeXML(doc, elmt, includeReference, includeLists);
        }
      }            
    }
  }

  public void writeXML(Document doc, Element root, boolean includeReference, boolean includeLists){
    FocDesc desc = getThisFocDesc();
    if(desc != null){
      Element mainNode = writeXML_MainNode(doc, root);
      
      FocFieldEnum enumer = this.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(enumer != null && enumer.hasNext()){
        FField field = (FField) enumer.next();        
        FProperty prop = (FProperty) enumer.getProperty();
        
        if(includeReference || (   field.getID() != FField.REF_FIELD_ID 
                                && field.getID() != FField.MASTER_REF_FIELD_ID 
                                && field.getID() != FField.SLAVE_REF_FIELD_ID)
           ){
          writeXML_ForProperty(doc, mainNode, field, prop);
        }
      }

      if(includeLists){
        enumer = this.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
        while(enumer != null && enumer.hasNext()){
          FListField field = (FListField) enumer.next();        
          FList listProp = (FList) enumer.getProperty();
          
          writeXML_ForList(doc, mainNode, field, listProp, includeReference, includeLists);
        }
      }
    }
  }
  
  //BElias & ELIE
  
  public void disabelValidationPanelButtonsWithPropagation(){
    disabelValidationPanelButtons();
    FocObject masterObject = (FocObject)getMasterObject();
    if (masterObject != null){
      masterObject.disabelValidationPanelButtonsWithPropagation();
    }else{
      if (browsePanel != null){
        ((FValidationPanel)browsePanel.getValidationPanel()).disabelButtons();
      }
    }
  }
  public void disabelValidationPanelButtons(){
    if (detailsPanel != null){
      FValidationPanel validPanel = (FValidationPanel) detailsPanel.getValidationPanel();
      if (validPanel != null){
        validPanel.disabelButtons();
      }
    }
  }
  
  
  public void enabelValidationPanelButtonsWithPropagation(){
    enabelValidationPanelButtons();
    FocObject masterObject = (FocObject)getMasterObject();
    if (masterObject != null){
      masterObject.enabelValidationPanelButtonsWithPropagation();
    }if (browsePanel != null){
      ((FValidationPanel)browsePanel.getValidationPanel()).enabelButtons();
    }
  }
  
  public void enabelValidationPanelButtons(){
    if (detailsPanel != null){
      FValidationPanel validPanel = (FValidationPanel)detailsPanel.getValidationPanel();
      if (validPanel != null){
        validPanel.enabelButtons();
      }
    }
  }
  //EElias
  
  //BElie
  
  public void deactivatePropertyListeners(boolean mode){
    FocFieldEnum iter = new FocFieldEnum(this.getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      prop.setDesactivateListeners(mode);
    }
  }

  public FProperty getFirstCustomizedProperty(int fieldID){
    FProperty prop = getFocProperty(fieldID);
  	if(prop != null && prop.isEmpty()){
  		prop = getFirstAncestorCustomizedProperty(fieldID);
  	}
  	return prop ;
  }
  
  public FProperty getFirstAncestorCustomizedProperty(int fieldID){
    
    FocObject fatherObject = getPropertyObject(getThisFocDesc().getFObjectTreeFatherNodeID());
    FProperty firstAncestorCustomizedProperty = null;
    
    if( getFocProperty(fieldID).getFocField().isWithInheritance() ){
      while(fatherObject != null && firstAncestorCustomizedProperty == null){
        
        if(!fatherObject.getFocProperty(fieldID).isEmpty() ){
          firstAncestorCustomizedProperty = fatherObject.getFocProperty(fieldID);
        }
        
        //TODO
        if( fatherObject == fatherObject.getPropertyObject(getThisFocDesc().getFObjectTreeFatherNodeID())){
          break;
        }
        fatherObject = fatherObject.getPropertyObject(getThisFocDesc().getFObjectTreeFatherNodeID());
      }  
    }
    
    return firstAncestorCustomizedProperty;
  }

  public Object getFirstAncestorTableDisplayObject(int fieldID, Format format){
    Object objDisplay = null;
    FProperty firstAncestorCustomized = getFirstAncestorCustomizedProperty(fieldID);
    if( firstAncestorCustomized != null ){
      objDisplay = firstAncestorCustomized.getTableDisplayObject(format);
    }
    return objDisplay;
  }
  
  public FocList getPropertyFormulaList(){
    FocList list = getPropertyList(FField.FLD_PROPERTY_FORMULA_LIST);
    if( list != null ){
      list.setDirectlyEditable(true);  
    }
    return list;  
  }
  
  public void loadPropertyFormulas(){
    
    FocList propertyFormulaList = getPropertyFormulaList();
    FocFieldEnum iter = new FocFieldEnum(this.getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      String fieldName = prop.getFocField().getDBName();
      
      PropertyFormula propertyFormula = (PropertyFormula)propertyFormulaList.searchByProperyStringValue(PropertyFormulaDesc.FLD_FIELD_NAME, fieldName, true);
      if( propertyFormula != null ){
        prop.setPropertyFormulaContext(newPropertyFormulaContext(prop, propertyFormula));
      }
    }
  }
  
  public PropertyFormulaContext newPropertyFormulaContext(FProperty property, PropertyFormula propertyFormula) {
    PropertyFormulaContext propertyFormulaContext = null;
    Class klass = getPropertyFormulaContextClass();
    if( klass != null ){
      try {
        Class[] argsDeclare = new Class[]{Formula.class, FFieldPath.class};
        Object[] args       = new Object[]{propertyFormula.getFormula(), FFieldPath.newFieldPath(property.getFocField().getID())};
        Constructor constr  = klass.getConstructor(argsDeclare);  
        propertyFormulaContext = (PropertyFormulaContext)constr.newInstance(args);
        propertyFormulaContext.setPropertyFormula(propertyFormula);
        propertyFormulaContext.setOriginProperty(property);
      }catch( Exception e){
        Globals.logException(e);
      }
    }
    return propertyFormulaContext;
  }  
  
  public Class getPropertyFormulaContextClass(){
    return PropertyFormulaContext.class;
  }
  //EElie

  public void beforePropertyModified(FProperty property) {
  }

  public void afterPropertyModified(FProperty property) {
  }
  
//rr Begin
  public boolean isDeprecated(){
    return getPropertyBoolean(FField.FLD_DEPRECATED_FIELD);
  }
  
  public void setDeprecated(boolean deprecated){
    setPropertyBoolean(FField.FLD_DEPRECATED_FIELD, deprecated);
  }
  //rr End
}
/*
 * Created on 13 fvr. 2004
 */
package b01.foc.property;

import b01.foc.*;
import b01.foc.db.DBManager;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.formula.Formula;
import b01.foc.formula.PropertyFormula;
import b01.foc.formula.PropertyFormulaContext;
import b01.foc.formula.PropertyFormulaDesc;
import b01.foc.list.FocList;
import b01.foc.property.validators.FPropertyValidator;

import java.sql.ResultSet;
import java.text.Format;
import java.util.*;
import java.awt.*;

import javax.swing.JComponent;

/**
 * @author 01Barmaja
 */
public class FProperty implements Cloneable{

  private static final char FLG_VALUE_LOCKED = 1;
  private static final char FLG_DESACTIVATE_LISTENERS = 2;
  private static final char FLG_LAST_MODIFIED_BY_SET_SQL_STRING= 4;
  //private static final char FLG_VALUE_LOCKED = 8;
  
  private ArrayList listeners = null;
  private FocObject focObject = null;
  //private int fieldID = 0;
  private FField focField = null;
  
  private char flags = 0;
  
  //private boolean desactivateListeners = false;
  //private boolean lastModifiedBySetSQLString = false;
  //private boolean valueLocked = false;
  private Color background = null;
  private FPropertyValidator propertyValidator = null;
  //private PropertyFormulaContainer propertyFormulaContainer = null;
  private PropertyFormulaContext propertyFormulaContext = null;
  
  protected void initStateVariables(){
    focField = null;
    listeners = null;
    focObject = null;
    //valueLocked = false;
    //desactivateListeners = false;
    //lastModifiedBySetSQLString = false;
  }
  
  public void attachToObject(FocObject focObj){
    this.focObject = focObj;
    if (focObj != null) focObj.putFocProperty(this);
  }
  
  public void init(FocObject focObj, int fieldID, boolean forPropertyArray){
    initStateVariables();
    
    if (focObj != null) {
      FocDesc focDesc = focObj.getThisFocDesc();
      if (focDesc != null) {
        if(forPropertyArray){
          focField = focDesc.getFieldArrayByID(fieldID);          
        }else{
          focField = focDesc.getFieldByID(fieldID);
        }
      }
    }
    attachToObject(focObj);
  }
  
  public FProperty(FocObject focObj, int fieldID) {
    init(focObj, fieldID, false);
  }

  public FProperty(FocObject focObj, int fieldID, boolean forPropertyArray) {
    init(focObj, fieldID, forPropertyArray);
  }
  
  public void dispose(){
    
    if(listeners != null){
      for(int i=0; listeners != null && i<listeners.size(); i++){
        FPropertyListener propList = (FPropertyListener) listeners.get(i);
        propList.dispose();
      }
      if(listeners != null) listeners.clear();
      listeners = null;
    }
    
    focObject = null;
    focField = null;
    
    background = null;
    /* NOT disposed here
    if(propertyValidator != null){
      propertyValidator.dispose();
      propertyValidator= null;      
    }
    */
    if( propertyFormulaContext != null ){
      propertyFormulaContext.dispose();
      propertyFormulaContext = null;
    }
    
  }
  
  public boolean isWithFormula(){
    return propertyFormulaContext != null && propertyFormulaContext.getFormula() != null; 
  }
  
  public PropertyFormulaContext getPropertyFormulaContext(){
    return propertyFormulaContext;
  }
  
  public void setPropertyFormulaContext(PropertyFormulaContext propertyFormulaContext){
    this.propertyFormulaContext = propertyFormulaContext;
    this.propertyFormulaContext.setOwner(true);
    this.propertyFormulaContext.setPropertyLockBackUp(isValueLocked());
    setValueLocked(true);
    this.propertyFormulaContext.plugListeners();
  }

  public void removeFormula(){
    if( propertyFormulaContext != null ){
      setValueLocked(propertyFormulaContext.getPropertyLockBackUp());
      propertyFormulaContext.dispose();
      propertyFormulaContext = null;
    }
  }
  
  public Formula getFormula(){
    return propertyFormulaContext != null ? propertyFormulaContext.getFormula() : null;
  }
  
  public void changeFormula(String expression){
    FocObject focObj = getFocObject();
    
    FocList propertyFormulaList = focObj.getPropertyFormulaList();
    if( propertyFormulaList != null ){
      String fieldName = getFocField().getDBName();
      PropertyFormula propertyFormula = (PropertyFormula)propertyFormulaList.searchByProperyStringValue(PropertyFormulaDesc.FLD_FIELD_NAME, fieldName, true);
      if( propertyFormula != null ){
        propertyFormula.setExpression(expression);
        removeFormula();
        setPropertyFormulaContext(focObj.newPropertyFormulaContext(this, propertyFormula));
      }else{
        newFormula(expression);
      }
    }
  }
  
  public void newFormula(){
    newFormula("");
  }
  
  public void newFormula(String expression){
    FocObject focObj = getFocObject();
    removeFormula();
    FocList propertyFormulaList = focObj.getPropertyFormulaList();
    PropertyFormula propertyFormula = (PropertyFormula)propertyFormulaList.newEmptyItem();
    propertyFormula.setFieldName(getFocField().getDBName());
    propertyFormula.setExpression(expression);
    setPropertyFormulaContext(focObj.newPropertyFormulaContext(this, propertyFormula));
  }
  
  public FProperty getFirstAncestorCustomizedProperty(){
    FProperty firstAncestorCustomizedProperty = null;
    FocObject focObj = getFocObject();
    if( focObj != null ){
      FField field = getFocField();
      if( field != null ){
        firstAncestorCustomizedProperty = focObj.getFirstAncestorCustomizedProperty(field.getID());  
      }
    }
    return firstAncestorCustomizedProperty;
  }
  
  protected Object clone() throws CloneNotSupportedException {
    FProperty zClone = (FProperty)super.clone();
    zClone.initStateVariables();
    return zClone;
  }

  public FProperty clone(FocObject object, FField field){
    FProperty zClone = null;
    try{
      zClone = (FProperty) clone();
      zClone.setFocField(field);
      zClone.attachToObject(object);
    }catch(Exception e){
      Globals.logException(e);
    }
    return zClone;
  }    
  
  public int hashCode() {
    return getString().hashCode();
  }

  public int compareTo(FProperty prop) {
    return (prop != null) ? this.getString().compareTo(prop.getString()) : 1;
  }

  public boolean equals(Object obj) {
    boolean equals = false;
    if (obj != null && obj.getClass() == FProperty.class) {
      equals = compareTo((FProperty) obj) == 0;
    }
    return equals;
  }

  public FField getFocField() {
    return focField;
    /*
    FField focField = null;
    if (focObject != null) {
      FocDesc focDesc = focObject.getThisFocDesc();
      if (focDesc != null) {
        focField = focDesc.getFieldByID(fieldID);
      }
    }
    return focField;
    */
  }

  public void setFocField(FField field) {
    focField = field;
  }  
  
  public void backup() {
    Globals.logString("Backup not implemented");
  }

  public void restore() {
    Globals.logString("Backup not implemented");
  }

  public void addListener(FPropertyListener propListener) {
    if(propListener != null){
      if (listeners == null) {
        listeners = new ArrayList();
      }
      listeners.add(propListener);
    }
  }

  public void removeListener(FPropertyListener propListener) {
    if (listeners != null && propListener != null) {
      listeners.remove(propListener);
    }
    if(listeners != null && listeners.size() == 0){
      listeners.clear();
      listeners = null;
    }
  }

  /*public boolean validateProperty(){
    boolean ok = false;
    if(propertyValidator != null){
      boolean desactivateListenersBackup = isDesactivateListeners();
      setDesactivateListeners(true);
      ok = propertyValidator.validateProperty(this);
      //BElias
      FField field = getFocField();
      if(field != null){
      	FPropertyValidator propertyValidatorFromField = field.getPropertyValidator();
      	if(propertyValidatorFromField != null){
      		ok = propertyValidatorFromField.validateProperty(this);
      	}
      }
      //EElias
      setDesactivateListeners(desactivateListenersBackup) ;
    }
    return ok;
  }*/
  
  public boolean validateProperty(){
    boolean ok = true;
    boolean desactivateListenersBackup = isDesactivateListeners();
    setDesactivateListeners(true);
    if(propertyValidator != null){
    	ok = propertyValidator.validateProperty(this);
    }
    //BElias
    FField field = getFocField();
    if(field != null){
    	FPropertyValidator propertyValidatorFromField = field.getPropertyValidator();
    	if(propertyValidatorFromField != null){
    		ok = propertyValidatorFromField.validateProperty(this);
    	}
    }
    //EElias
    setDesactivateListeners(desactivateListenersBackup) ;
    return ok;
  }
  
  public void notifyListeners() {
    if(!isDesactivateListeners()){
      if(!isLastModifiedBySetSQLString()) validateProperty();      
      if (listeners != null) {
        if( focObject != null ){
          focObject.beforePropertyModified(this);  
        }
        for(int i=0; i<listeners.size(); i++){
          FPropertyListener porpListener = (FPropertyListener) listeners.get(i);
          if (porpListener != null) {
            porpListener.propertyModified(this);
          }
        }
        if( focObject != null ){
          focObject.afterPropertyModified(this);  
        }
      }
      notifyFieldListeners();      
      if (focObject != null && getFocField() != null && getFocField().isDBResident()) {
        focObject.setModified(true);
      }
    }
    setLastModifiedBySetSQLString(false);
  }
  
  private void notifyFieldListeners(){
    FField field = getFocField();
    if(field != null){
      field.notifyPropertyListeners(this);
    }
  }

  public void setString(String str) {
  }

  public String getString() {
    return "";
  }

  protected void setSqlStringInternal(String str) {
    setString(str);
  }
  
  public void setSqlString(String str) {
    setDesactivateListeners(true);
    //rr Begin
    if (Globals.getDBManager()!= null){
      if (Globals.getDBManager().getProvider()== DBManager.PROVIDER_ORACLE){
        if (str == null){
          str = "";
        }
      }
    } 
    //rr End
    setSqlStringInternal(str);
    setDesactivateListeners(false);
    setLastModifiedBySetSQLString(true);
  }

  public String getSqlString() {
    return getString();
  }

  public void setInteger(int iVal) {
  }

  public void setLong(long lVal){
    
  }
  
  public int getInteger() {
    return 0;
  }
  
  public long getLong() {
    return 0;
  }

  public void setDouble(double dVal) {
  }

  public double getDouble() {
    return 0.0;
  }

  public void setObject(Object obj) {
  }

  public Object getObject() {
    return null;
  }

  public int getDisplaySize() {
    int size = 0;
    FField field = getFocField();
    if (field != null) {
      size = field.getSize();
    }
    return size;
  }

  /**
   * @return
   */
  public FocObject getFocObject() {
    return focObject;
  }

  /**
   * @param abstract1
   */
  public void setFocObject(FocObject abstract1) {
    focObject = abstract1;
  }

  public Object getTableDisplayObject(Format format) {
    return getObject();
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setObject(obj);
  }
  
  public void copy(FProperty sourceProp){
  	if(sourceProp != null){
  		this.setObject(sourceProp.getObject());
  	}
  }
  
  public boolean isObjectProperty(){
    boolean isObjProp = false;
    FField field = this.getFocField();
    if(field != null){
      isObjProp = field.isObjectContainer();
    }
    return isObjProp;
  }
  
  /**
   * @return Returns the lastModifiedBySetSQLString.
   */
  public boolean isLastModifiedBySetSQLString() {
    return (flags & FLG_LAST_MODIFIED_BY_SET_SQL_STRING) != 0;
  }
  
  public void setLastModifiedBySetSQLString(boolean lastModifiedBySetSQLString) {
    if(lastModifiedBySetSQLString){
      flags = (char)(flags | FLG_LAST_MODIFIED_BY_SET_SQL_STRING);
    }else{
      flags = (char)(flags & ~FLG_LAST_MODIFIED_BY_SET_SQL_STRING);
    }
  }

  protected void adaptGuiComponentEnableAttribute(Component comp){
    if(comp != null && this.isValueLocked()){
      comp.setEnabled(false);
    }
  }

  public Component getGuiComponent(){
    FField field = this.getFocField();
    Component comp = field != null ? field.getGuiComponent(this) : null;
    adaptGuiComponentEnableAttribute(comp);
    
    FocObject focObj = getFocObject();
    if(focObj != null){
    	FocDesc focDesc = focObj.getThisFocDesc();    	
    	if(focDesc != null && field != null){
        String name = focDesc.getFieldGuiName(field);
        comp.setName(name);
        if(ConfigInfo.isUnitDevMode()){
          JComponent jcomp = (JComponent)comp;
          jcomp.setToolTipText(name);
        }
    	}
    }
   
    return comp;
  }
  
  /**
   * @return Returns the valueLocked.
   */
  public boolean isValueLocked() {
    return (flags & FLG_VALUE_LOCKED) != 0;
  }
  
  /**
   * @param valueLocked The valueLocked to set.
   */
  public void setValueLocked(boolean valueLocked) {
    if(valueLocked){
      flags = (char)(flags | FLG_VALUE_LOCKED);
    }else{
      flags = (char)(flags & ~FLG_VALUE_LOCKED);
    }
  }

  /**
   * @return Returns the background.
   */
  public Color getBackground() {
    return background;
  }

  /**
   * @param background The background to set.
   */
  public void setBackground(Color background) {
    boolean doNotify = false;
    if(background != null && this.background != null){
      doNotify = background.getBlue() != this.background.getBlue() || background.getRed() != this.background.getRed() || background.getGreen() != this.background.getGreen();
    }else{
      doNotify = background != this.background;
    }
    this.background = background;
    if(doNotify){
      notifyListeners();
    }
  }

  public FPropertyValidator getPropertyValidator() {
    return propertyValidator;
  }
  
  public void setPropertyValidator(FPropertyValidator propertyValidator) {
    this.propertyValidator = propertyValidator;
  }
  
  public FProperty getFocProperty(int fldId){
    return null;
  }  
  
  public boolean isDesactivateListeners() {
    return (flags & FLG_DESACTIVATE_LISTENERS) != 0;
  }
  
  public void setDesactivateListeners(boolean desactivateListeners) {
    if (desactivateListeners){
      flags = (char)(flags | FLG_DESACTIVATE_LISTENERS);
    }else{
      flags = (char)(flags & ~FLG_DESACTIVATE_LISTENERS);
    }
  }
  
  public boolean isEmpty(){
    return false;
  }
  
  //BElie
  private boolean inherited = true;

  public boolean isInherited() {
    return inherited;
  }

  public void setInherited(boolean inherited) {
    this.inherited = inherited;
  }
  //EElie

  public boolean isModified() {
    return true;
  }

  public void setModified(boolean modified) {
  }
  
  //BAntoineS-HSG-ORACLE-BLOB
  public void getValueFromResultSet(ResultSet resultSet, int i){
    String value = null; 
    try{
      value = resultSet.getString(i);
    }catch(Exception e){
      value = "";
      Globals.logException(e);
    }
    setSqlString(value);
  }
  //EAntoineS-HSG-ORACLE-BLOB
}

/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.sql.Types;
import java.util.ArrayList;

import b01.foc.Globals;
import b01.foc.list.FocLink;
import b01.foc.list.FocList;
import b01.foc.list.filter.FilterCondition;
import b01.foc.formula.FieldFormulaContext;
import b01.foc.formula.Formula;
import b01.foc.gui.table.cellControler.*;
import b01.foc.property.*;
import b01.foc.property.validators.FPropertyValidator;
import b01.foc.db.DBManager;
import b01.foc.desc.*;

import java.awt.*;


/**
 * @author 01Barmaja
 */
public abstract class FField implements Cloneable{
  protected String name = "";
  protected String title = "";
  protected int id = 0;
  protected boolean key = false;
  protected int size = 0;
  protected int decimals = 0;
  //BAnoineS - AUTOINCREMENT
  private boolean autoIncrement = false;
  //EAnoineS - AUTOINCREMENT
  private boolean isDBResident = true;
  private boolean lockValueAfterCreation = false;
  private boolean allwaysLocked = false;
  private boolean isMandatory = false;
  private boolean includeInDBRequests = true;
  private int indexOfPropertyInArray = -1;
  private boolean withInheritance = false;
  private String formulaString = null;
  private FieldFormulaContext formulaContext = null;
  private FPropertyValidator validator = null;
  
  private ArrayList listeners = null;

  abstract public int getSqlType();
  abstract public String getCreationString(String name);
  abstract public Component getGuiComponent(FProperty prop);
  abstract public AbstractCellControler getTableCellEditor(FProperty prop);
  abstract public boolean isObjectContainer();
  abstract public FocDesc getFocDesc();
  abstract public void addReferenceLocations(FocDesc pointerDesc);
  abstract public FProperty newProperty(FocObject masterObj, Object defaultValue);
  abstract public FProperty newProperty(FocObject masterObj);
  abstract protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix);
  
  final public static int REF_FIELD_ID = 0;
  final public static int SELECTION_FIELD_ID = -2;
  final public static int MASTER_REF_FIELD_ID = -3;
  final public static int SLAVE_REF_FIELD_ID = -4;
  final public static int RIGHT_LEVEL_USER_FIELD_ID = -5;
  final public static int RIGHT_LEVEL_FIELD_ID = -6;
  final public static int RIGHT_LEVEL_DATETIME_FIELD_ID = -7;
  final public static int STATUS_FIELD_ID = -8;
  final public static int LOCK_USER_FIELD_ID = -9;
  final public static int LINE_NUMBER_FIELD_ID = -10;
  final public static int TREE_FIELD_ID = -11;
  final public static int MASTER_MIRROR_ID = -12;
  //BELie
  final public static int CREATION_REVISION_FIELD_ID = -13;
  final public static int DELETION_REVISION_FIELD_ID = -14;
  final public static int NEW_ITEM_FIELD_ID = -15;
  final public static int REVISION_FIELD_ID = -16;
  //EElie
  final public static int FLD_ID_GANTT_CHART = -17;
  final public static int NO_LIST_FIELD_ID = -18;
  final public static int FLD_NAME = -19;
  final public static int FLD_ID_DRAWING = -20;
  final public static int ORDER_FIELD_ID = -21;
  final public static int FLD_PROPERTY_FORMULA_LIST = -22;
  final public static int FLD_FATHER_NODE_FIELD_ID = -23;
  //rr 
  final public static int FLD_DEPRECATED_FIELD = -24;
  
  final public static int NO_FIELD_ID = -99;
  
  //BElie
  final public static String REVISION_FIELD_ID_NAME = "R_ID";
  final public static String CREATION_REVISION_FIELD_ID_NAME = "C_R";
  final public static String DELETION_REVISION_FIELD_ID_NAME = "D_R";
  final public static String NEW_ITEM_FIELD_ID_NAME = "N_I";
  final public static String NEW_ITEM_FIELD_ID_NAME_PREFIX = "REV_";
  final public static String ORDER_FIELD_NAME = "ORDER";
  //EElie
  final public static String REF_FIELD_NAME = "REF";
  final public static String SELECTION_FIELD_NAME = "SELECTION";
  final public static String MASTER_REF_FIELD_NAME = "M_REF";
  final public static String SLAVE_REF_FIELD_NAME = "S_REF";
  final public static String RIGHT_LEVEL_USER_FIELD_NAME = "USER_REF";
  final public static String RIGHT_LEVEL_FIELD_NAME = "RGHT_LVL";
  final public static String RIGHT_LEVEL_DATETIME_FIELD_NAME = "RGHT_LVL_DATE";
  final public static String LOCK_USER_FIELD_NAME = "LOCK_USER_REF";
  final public static String CONCURRENCY_LOCK_USER_FIELD_PREFIX = "LK_USER_";
  final public static String LINE_NUMBER_FIELD_LBL = "LINE_NBR";
  final public static String LIST_FIELD_SUFFIX = "_LIST";
  final public static String FATHER_NODE_FIELD_NAME = "FATHER_NODE";

  final public static String FLD_NAME_PARAM_SET_COST_QUANTITY = "COST_QUANTITY";
  
  public void init(String name, String title, int id, boolean key, int size, int decimals) {
    this.name = name.toUpperCase();
    //Begin rr
    if (Globals.getDBManager()!= null){
      if((Globals.getDBManager().getProvider()== DBManager.PROVIDER_ORACLE) && (name == "MODE")){
          this.name = "MODE_O";
      }
    }
    //End rr
    this.title = title;
    this.id = id;
    this.key = key;
    this.size = size;
    this.decimals = decimals;
  }

  public FField(String name, String title, int id, boolean key, int size, int decimals) {
    init(name, title, id, key, size, decimals);
  }
  
  public void dispose(){
    name = null;
    title = null;
    formulaString = null;
    
    if(listeners != null){
      for(int i=0; i<listeners.size(); i++){
        FPropertyListener propList = (FPropertyListener) listeners.get(i);
        propList.dispose();        
      }
      listeners.clear();
      listeners = null;
    }
    
    if(formulaContext != null){
    	formulaContext.dispose();
    	formulaContext = null;
    }
    if(validator != null){
    	validator.dispose();
    	validator = null;
    }
  }
  
  public static FField newField(int sqlType, String name, int id, int size, int decimals, boolean autoIncrement) {
    FField field = null;
    
    switch (sqlType) {
    //rr begin
    case Types.NUMERIC:
      if(decimals > 0){
        field = new FNumField(name, name, id, false, size, decimals);
      }else{
        field = new FIntField(name, name, id, false, size);
      }
      break;
    //rr End
    case Types.SMALLINT:
    case Types.INTEGER:    
      field = new FIntField(name, name, id, false, size);
      break;
    case Types.DOUBLE:
    case Types.REAL:
    case Types.FLOAT:      
      field = new FNumField(name, name, id, false, size, decimals);
      break;
    case Types.VARCHAR:
    case Types.LONGVARCHAR:    	
      field = new FCharField(name, name, id, false, size);
      break;
    case Types.CHAR:
      field = new FCharField(name, name, id, false, size);
      break;
    //BElie  
    case Types.BLOB:
    case Types.LONGVARBINARY:  
      field = new FBlobStringField(name, name, id, false, 0, 0);
      break;
    //EElie  
    case Types.DATE:
      field = new FDateField(name, name, id, false);
      break;
    case Types.TIMESTAMP:
        field = new FDateTimeField(name, name, id, false);
        break;    	
    case Types.TIME:
      field = new FTimeField(name, name, id, false);
      break;
    case Types.JAVA_OBJECT:
      // field = FObjectField(name, name, id, false, size, decimals);
      break;
    case Types.ARRAY:
      // field = FIntField(name, name, id, false, size, decimals);
      break;
      default:
      	b01.foc.Globals.logString("SQL type : "+sqlType);
    }
    
    if(field == null){
      b01.foc.Globals.logString("Could not find type : "+sqlType);
    }else{
    	field.setAutoIncrement(autoIncrement);
    }
    
    return field;
  }
    
  public String createLinkCondition(String firstTableName){
    return "";
  }
  
  public String getNameInSourceTable(){
    return "";
  }
  
  public String getNameInTargetTable(){
    return "";
  }
  
  public FilterCondition getFilterCondition(FFieldPath fieldPath, FocDesc fieldPathBaseFocDesc){
  	FilterCondition condition = null;
		if(fieldPath != null && fieldPathBaseFocDesc != null){
			String conditionPrefix = fieldPath.getFieldCompleteName(fieldPathBaseFocDesc);
			condition = getFilterCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}

  private static FIntField lineNumberField = null;
  
  public static FIntField getLineNumberField(){
    if(lineNumberField == null){
      lineNumberField = new FIntField(FField.LINE_NUMBER_FIELD_LBL, "Line number", FField.LINE_NUMBER_FIELD_ID, false, 5);
    }
    return lineNumberField;
  }
  
  public Object clone() throws CloneNotSupportedException {
    FField zClone = (FField)super.clone();
    zClone.key = false;
    return zClone;
  }

  public Object cloneExact() throws CloneNotSupportedException {
    FField zClone = (FField)super.clone();    
    return zClone;
  }
  
  public int compareSQLDeclaration(FField field){
    return 0;
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
  }
  
  public void notifyPropertyListeners(FProperty property) {
      if (listeners != null) {
        for(int i=0; i<listeners.size(); i++){
          FPropertyListener porpListener = (FPropertyListener) listeners.get(i);
          if (porpListener != null) {
            porpListener.propertyModified(property);
          }
        }
      }
  }
  
  public void setPropertyValidator(FPropertyValidator validator){
  	this.validator = validator;
  }
  
  public FPropertyValidator getPropertyValidator(){
  	return this.validator;
  }
  
  public int getID() {
    return id;
  }

  public String getKeyPrefix() {
    return "";
  }

  public String getName() {
    return name;
  }
  
  public String getDBName(){
  	return getName();
  }

  public boolean getKey() {
    return key;
  }

  public void setKey(boolean key) {
    this.key = key;
  }

  public int getSize() {
    getDecimals();//In case we are Oracle, the first call to getDecimal 
                  //might adapt (modify) the size value
    return size;
  }

  public int getDecimals() {
    return decimals;
  }

  public void setStorageInfo(String name, int size) {
    this.name = name;
    this.size = size;
  }

  public void setStorageInfo(String name, int size, int decimals) {
    setStorageInfo(name, size);
    this.decimals = decimals;
  }

  /**
   * @param i
   */
  public void setId(int i) {
    id = i;
  }

  /**
   * @param string
   */
  public void setName(String string) {
    name = string;
  }

  public FocLink getLink() {
    return null;
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return Returns the isDBResident.
   */
  public boolean isDBResident() {
    return isDBResident;
  }

  /**
   * @param isDBResident
   *          The isDBResident to set.
   */
  public void setDBResident(boolean isDBResident) {
    this.isDBResident = isDBResident;
  }
    
  public FocList getSelectionList() {
    return null;
  }
  
  /**
   * @return Returns the lockValueAfterCreation.
   */
  public boolean isLockValueAfterCreation() {
    return lockValueAfterCreation;
  }
  
  /**
   * @param lockValueAfterCreation The lockValueAfterCreation to set.
   */
  public void setLockValueAfterCreation(boolean lockValueAfterCreation) {
    this.lockValueAfterCreation = lockValueAfterCreation;
  }
  
  public boolean isAllwaysLocked(){
  	return this.allwaysLocked;
  }
  
  public void setAllwaysLocked(boolean allwaysLocked){
  	this.allwaysLocked = allwaysLocked;
  }
  
  public int getFieldDisplaySize(){
    return size;
  }  
  
  public boolean isMandatory() {
    return isMandatory;
  }
    
  public void setMandatory(boolean isMandatory) {
    this.isMandatory = isMandatory;
  }
  
  public int getIndexOfPropertyInArray() {
    return indexOfPropertyInArray;
  }
  
  public void setIndexOfPropertyInArray(int indexOfPropertyInArray) {
    this.indexOfPropertyInArray = indexOfPropertyInArray;
  }
  
  public void setFormulaString(String formulaString){
  	this.formulaString = formulaString;
  }
  
  public String getFormulaString(){
  	return this.formulaString;
  }
  
  public void createFormula(FocDesc focDesc){
  	String formulaString = getFormulaString();
  	if(formulaString != null && !formulaString.equals("")){
	  	Formula formula = null;
	  	try{
	  		formula = new Formula(formulaString);
	  		FieldFormulaContext context = new FieldFormulaContext(formula, FFieldPath.newFieldPath(getID()), focDesc);
	  		setFormulaContext(context);
	  		
	  	}catch(Exception e){
	  		Globals.logString("Formula Syntax Error, Formula rejected!");
	  		Globals.logException(e);
	  	}
  	}
  }
  
  /*private void setFormula(Formula formula){
  	this.formula = formula;
  }*/
  
  private void setFormulaContext(FieldFormulaContext formulaContext){
  	this.formulaContext = formulaContext;
  }
  
  private FieldFormulaContext getFormulaContext(){
  	return this.formulaContext;
  }
  
  public boolean isWithFormula(){
  	return getFormulaString() != null;
  }
  
  public void computePropertyUsingFormulaIfNeeded(FProperty property){
  	FieldFormulaContext formulaContext = getFormulaContext();
  	if(formulaContext != null && formulaContext.computeUponConstruction()){
  		formulaContext.compute();
  	}
  }
  
  public boolean isWithInheritance() {
    return withInheritance;
  }
  
  public void setWithInheritance(boolean withInheritance) {
    this.withInheritance = withInheritance;
  }
  
  public boolean isIncludeInDBRequests() {
    return includeInDBRequests;
  }
  
  public void setIncludeInDBRequests(boolean includeInDBRequests) {
    this.includeInDBRequests = includeInDBRequests;
	}
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
}

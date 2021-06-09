// PROPERTIES
// MAIN
// ADAPT DATABASE
// INVOKE
// INDEX
// FIELD LIST
//    FIELDS
//    KEY FLD
//    ALL FLD
//    ARRAY FIELDS
// REFERENCE LOCATION LIST
// LIST
// REVISION

/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc;

import b01.foc.*;
import b01.foc.admin.*;
import b01.foc.db.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.list.*;
import b01.foc.list.filter.FocDescForFilter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.lang.reflect.*;

/**
 * @author 01Barmaja
 */
public class FocDesc implements Cloneable{
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PROPERTIES
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
  private Class focObjectClass       = null;
  private Class guiBrowsePanelClass  = null;
  private Class guiDetailsPanelClass = null;
  
  private boolean dbResident = false;
  //rr
  private boolean deprecated = false;
  
  private String storageName = "";
  private String title = "";
  private FFieldContainer keyFields = new FFieldContainer();
  private boolean isKeyUnique = true;
  private FFieldContainer fields = new FFieldContainer();
  private FFieldContainer arrayFields = null;    
  private FField identifierField = null;
  private HashMap<String, DBIndex> indexes = null;
  private ArrayList referenceLocationList = null;
  private ArrayList mandatoryFields = null;

  private int rightsByLevelMode = RIGHTS_BY_LEVEL_MODE_NONE;
  private boolean concurrenceLockEnabled = false;
  private ArrayList concurrenceLockView = null;
  
  // For link Tables descriptions
  private FocLink n2nLink = null;

  private int propertyArrayLength = 0;
  private boolean addMasterMirror = false;
  
  final public static boolean DB_RESIDENT = true;
  final public static boolean NOT_DB_RESIDENT = false;
  
  final public static int RIGHTS_BY_LEVEL_MODE_NONE = 0;
  final public static int RIGHTS_BY_LEVEL_MODE_TRACE_ONLY = 1;
  final public static int RIGHTS_BY_LEVEL_MODE_TRACE_AND_ACCESS = 2;

  final public static String INDEX_IDENTIFIER = "IDENTIFIER";
  
  private FFieldPath revisionPath = null;
  private boolean parentRevisionSupport = false;
  
  public void dispose(){
    focObjectClass       = null;
    guiBrowsePanelClass  = null;
    guiDetailsPanelClass = null;
    n2nLink = null;
    identifierField = null;
    
    storageName = null;
    title = null;
    
    if( indexes != null ){
      indexes.clear();
      indexes = null;
    }
    
    if( fields != null ){
      fields.dispose();
      fields = null;
    }
    
    if( keyFields != null ){
      keyFields.dispose();
      keyFields = null;  
    }
    
    if( arrayFields != null ){
      arrayFields.dispose();
      arrayFields = null;  
    }
    
    if( referenceLocationList != null ){
      referenceLocationList.clear();
      referenceLocationList = null;
    }
    
    if( concurrenceLockView != null ){
      concurrenceLockView.clear();
      concurrenceLockView = null;
    }
    
    if( mandatoryFields != null ){
      mandatoryFields.clear();
      mandatoryFields = null;
    }
    
    revisionPath = null;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private void init(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, FocLink link) {
    this.focObjectClass = focObjectClass;
    this.dbResident = dbResident;
    setStorageName(storageName);
    this.isKeyUnique = isKeyUnique;
    this.n2nLink = link;
    this.propertyArrayLength = 0;
  }

  public FocDesc(Class focObjectClass) {
    init(focObjectClass, false, "", false, null);
  }

  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique) {
    init(focObjectClass, dbResident, storageName, isKeyUnique, null);
  }

  //rr Begin
  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, boolean deprecated) {
    init(focObjectClass, dbResident, storageName, isKeyUnique, null);
    this.deprecated = deprecated;
  }
  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, FocLink link, boolean deprecated) {
    init(focObjectClass, dbResident, storageName, false, link);
    this.deprecated = deprecated;
  }
  //rr End
  
  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, FocLink link) {
    init(focObjectClass, dbResident, storageName, false, link);
  }
 
  //BAntoineS - AUTOINCREMENT
  public String getSequenceName() {
  	return getStorageName()+"_SEQUENCE";
  }
  
  private int getNextOrCurrentSequence(boolean next) throws FocDBException, SequenceDoesNotExistException, SQLException{
  	//This is oracle specific
  	if(Globals.getDBManager().getProvider() != DBManager.PROVIDER_ORACLE){
  		throw new FocDBException("Illegal Call of an Oracle Specific fucntion");
  	}
  	
  	int nextSequence = -1;
		Statement stm = Globals.getDBManager().lockStatement();
		String function = next ? "nextval" : "currval";
		String req = "SELECT "+getSequenceName()+"."+function+" from dual";
		try{
			if(ConfigInfo.isLogDBSelectActive()) Globals.logString(req);
			stm.execute(req);
			ResultSet rs = stm.getResultSet();
			if(rs != null && rs.next()){
				nextSequence = rs.getInt(1);
			}
			Globals.getDBManager().unlockStatement(stm);
		}catch(SQLException e){
			Globals.getDBManager().unlockStatement(stm);
			throw new SequenceDoesNotExistException("Sequence does not exist");
		}
		return nextSequence;
  }

  public int getNextSequence() throws FocDBException, SequenceDoesNotExistException, SQLException{
  	return getNextOrCurrentSequence(true);
  }

  public int getCurrentSequence() throws FocDBException, SequenceDoesNotExistException, SQLException{
  	return getNextOrCurrentSequence(false);
  }
  //EAntoineS - AUTOINCREMENT
  
	public boolean isKeyUnique() {
    return isKeyUnique;
  }
	
	public void setKeyUnique(boolean keyUnique){
		this.isKeyUnique = keyUnique;
	}

  public String getStorageName() {
    return storageName;
  }

  public void setStorageName(String storageName) {
    this.storageName = storageName.toUpperCase();
  }
  
  public boolean getDBResident() {
    return dbResident;
  }

  public boolean getWithReference() {
    return getFieldByID(FField.REF_FIELD_ID) != null;
  }

  public void setFocObjectClass(Class focObjectClass){
  	this.focObjectClass = focObjectClass;
  }
  
  public Class getFocObjectClass() {
    return focObjectClass;
  }
  
  public int getRightsByLevelMode(){
    return rightsByLevelMode;
  }
  
  public void setRightsByLevelEnabled(int rightsByLevelMode) {
    this.rightsByLevelMode = RIGHTS_BY_LEVEL_MODE_NONE;
    if(Globals.getApp().isWithLogin()){
      this.rightsByLevelMode = rightsByLevelMode;
    }
    
    if(this.rightsByLevelMode != RIGHTS_BY_LEVEL_MODE_NONE){
      FObjectField userField = (FObjectField) getFieldByID(FField.RIGHT_LEVEL_USER_FIELD_ID);
      if (userField == null) {
        userField = new FObjectField(FField.RIGHT_LEVEL_USER_FIELD_NAME, FField.RIGHT_LEVEL_USER_FIELD_NAME, FField.RIGHT_LEVEL_USER_FIELD_ID, false, FocUser.getFocDesc(), "USER_");       
        userField.setSelectionList(FocUser.getList(FocList.NONE));
        userField.setDisplayField(FocUserDesc.FLD_NAME);
        userField.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
        addField(userField);
      }
      
      FField levelField = getFieldByID(FField.RIGHT_LEVEL_FIELD_ID);
      if (levelField == null) {
        levelField = new FIntField(FField.RIGHT_LEVEL_FIELD_NAME, FField.RIGHT_LEVEL_FIELD_NAME, FField.RIGHT_LEVEL_FIELD_ID, false, 2);
        addField(levelField);        
      }
      
      FField dateTimeField = getFieldByID(FField.RIGHT_LEVEL_DATETIME_FIELD_ID);
      if (dateTimeField == null) {
        dateTimeField = new FCharField(FField.RIGHT_LEVEL_DATETIME_FIELD_NAME, FField.RIGHT_LEVEL_DATETIME_FIELD_NAME, FField.RIGHT_LEVEL_DATETIME_FIELD_ID, false, 20);
        addField(dateTimeField);
      }
    }
  }
  
  public boolean isConcurrenceLockEnabled() {
    return concurrenceLockEnabled;
  } 
  
  public void setConcurrenceLockEnabled(boolean concurrenceLockEnabled) {
    this.concurrenceLockEnabled = concurrenceLockEnabled && Globals.getApp().isWithLogin();
    if(this.concurrenceLockEnabled){
      FObjectField userField = (FObjectField) getFieldByID(FField.LOCK_USER_FIELD_ID);
      if (userField == null) {
        userField = new FObjectField(FField.LOCK_USER_FIELD_NAME, FField.LOCK_USER_FIELD_NAME, FField.LOCK_USER_FIELD_ID, false, FocUser.getFocDesc(), FField.CONCURRENCY_LOCK_USER_FIELD_PREFIX);
        userField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
        /*userField.setSelectionList(FocUser.getList(FocList.NONE));
        userField.setDisplayField(FocUserDesc.FLD_NAME);
        userField.setComboBoxCellEditor(FocUserDesc.FLD_NAME);*/
        userField.setWithList(false);
        addField(userField);
      }
    }
    
    this.concurrenceLockEnabled = concurrenceLockEnabled;
  }
  
  public void concurrenceLockView_AddField(int fieldID){
    if(concurrenceLockView == null){
      concurrenceLockView = new ArrayList();
    }
    concurrenceLockView.add(Integer.valueOf(fieldID));
  }

  public int concurrenceLockView_FieldNumber(){
    return concurrenceLockView != null ? concurrenceLockView.size() : 0;
  }

  public int concurrenceLockView_FieldAt(int at){
    return concurrenceLockView != null ? ((Integer)concurrenceLockView.get(at)).intValue() : 0;
  }

  public void setFieldSelectionListNotLoaded(){
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while (enumer.hasNext()) {
      FField focField = (FField) enumer.next();
      FocList list = focField.getSelectionList();
      if(list != null){
        list.setLoaded(false);
      }
    }
  }
  
  public void fillTableModelWithKeyFields(FAbstractTableModel fTableModel, boolean withSelectionCheckBox) {
    if (fTableModel != null) {
      FTableView view = fTableModel.getTableView();

      if (withSelectionCheckBox) {
        view.addSelectionColumn();
      }

      for (int i = 0; i < getKeyFieldsSize(); i++) {
        FField field = getKeyFieldAt(i);
        if (field != null) {
          view.addColumn(this, field);
        }
      }
    }
  }  
  
  public FocDesc clone() {
    FocDesc zFocDesc = null;
    try {
			zFocDesc = (FocDesc)super.clone();
		} catch (CloneNotSupportedException e) {
			Globals.logException(e);
		}
  	return zFocDesc;
  }
  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ADAPT DATABASE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void createDBTable() {
    if (dbResident) {
      Globals.getDBManager();
      SQLCreateTable create = new SQLCreateTable(this);
      create.buildRequest();
      create.execute();
    }
  }

  public boolean adaptDBTableFields() {
  	boolean adapted = false;
    SQLTableDetails table = new SQLTableDetails(this);
    table.buildRequest();
    table.execute();

    Hashtable actualFields = table.getFieldsHashtable();

    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while (enumer.hasNext()) {
      FField focField = (FField) enumer.next();
      FField realField = null;
      realField = (FField) actualFields.get(enumer.getFieldCompleteName(this));
      if (realField == null) {
        new SQLTableDetails(this);
        SQLAlterTable alter = new SQLAlterTable(this, focField, enumer.getFieldCompleteName(this), SQLAlterTable.ADD);
        alter.execute();
        adapted = true;
      } else if (realField.getSqlType() != focField.getSqlType()) {
        Exception e = new Exception("Field type conflict "+getStorageName()+"."+focField.getDBName()+" db:" + realField.getName() + " " + realField.getClass().getName() + " app:" + enumer.getFieldCompleteName(this) + " " + focField.getClass().getName());
        Globals.logException(e);
      } else if (focField.compareSQLDeclaration(realField) != 0) {
        //focField.compareSQLDeclaration(realField);        
        SQLAlterTable alter = new SQLAlterTable(this, focField, enumer.getFieldCompleteName(this), SQLAlterTable.MODIFY);
        alter.execute();
        //BAntoineS - AUTOINCREMENT
      } else if (!realField.isAutoIncrement() && focField.isAutoIncrement() && realField.getName().equals(FField.REF_FIELD_NAME) && Globals.getDBManager().getProvider() == DBManager.PROVIDER_MYSQL){
        SQLAlterTable alter = new SQLAlterTable(this, focField, enumer.getFieldCompleteName(this), SQLAlterTable.MODIFY);
        alter.execute();
        //EAntoineS - AUTOINCREMENT
      }
      if (realField != null) {
        actualFields.remove(realField.getName());
      }
    }
    adapted = adapted || actualFields.values().size() > 0;
    for (Enumeration actEnum = actualFields.elements(); actEnum.hasMoreElements();) {
      FField actualField = (FField) actEnum.nextElement();

      SQLAlterTable alter = new SQLAlterTable(this, actualField.getName());
      alter.execute();
    }
    return adapted;
  }

  public void beforeAdaptTableModel(){
  }
  
  public void afterAdaptTableModel(){
  }
  
  public void beforeLogin() {
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INVOKE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getFocDescription(Class focObjectClass) {
    FocDesc focDesc = null;

    try {
      if (focObjectClass != null) {
        //FocObject focObj = (FocObject) focObjectClass.newInstance();
        //FocObject focObj = newClassInstance(null);        
        //focObj.getThisFocDesc();
        Class[] argsDeclare = null;
        Object[] args = null;
        Method methodGetFocDesc = null;
        try{
        	methodGetFocDesc = focObjectClass.getMethod("getFocDesc", argsDeclare);
        }catch(NoSuchMethodException e){
        	Class cls = Class.forName(focObjectClass.getName()+"Desc");
        	methodGetFocDesc = cls.getMethod("getInstance", argsDeclare);
        }
        
        if(methodGetFocDesc != null){
        	focDesc = (FocDesc) methodGetFocDesc.invoke(null, args);
        }
      }
    } catch (Exception e) {
      Globals.getDisplayManager().popupMessage("getFocDescription");      
    	Globals.logString("Exception while getting FocDesc for class : "+focObjectClass.getName());
      Globals.logException(e);
    }
    
    return focDesc;
  }

  public static FocDesc getFocDescriptionForDescClassX(Class descClass) {
    FocDesc focDesc = null;

    try {
      if (descClass != null) {
        //FocObject focObj = (FocObject) focObjectClass.newInstance();
        //FocObject focObj = newClassInstance(null);        
        //focObj.getThisFocDesc();
        Class[] argsDeclare = null;
        Object[] args = null;
        Method methodGetFocDesc = null;
        try{
        	methodGetFocDesc = descClass.getMethod("getInstance", argsDeclare);
        }catch(NoSuchMethodException e){
        	methodGetFocDesc = descClass.getMethod("getFocDesc", argsDeclare);
        }
        if(methodGetFocDesc != null){
        	focDesc = (FocDesc) methodGetFocDesc.invoke(null, args);
        }
      }
    } catch (Exception e) {
      Globals.getDisplayManager().popupMessage("getFocDescriptionForDescClassX");
    	Globals.logString("Exception while getting FocDesc for class : "+descClass.getName());
      Globals.logException(e);
    }
    
    return focDesc;
  }
  
  public FPanel callNewBrowsePanel(Class guiClass, FocList list, int viewID) {
    FPanel panel = null;
    try {
      if (guiClass != null) {
        Class[] param = new Class[2];
        param[0] = FocList.class;
        param[1] = Integer.TYPE;
        
        Object[] args = new Object[2];
        args[0] = list;
        args[1] = Integer.valueOf(viewID);
        
      	Constructor constr = guiClass.getConstructor(param);
      	panel = (FPanel) constr.newInstance(args);
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
    return panel;
  }
  
  public FPanel callNewBrowsePanel(FocList list, int viewID) {
    FPanel panel = null;
    try {
      if (focObjectClass != null) {
        Class[] param = new Class[2];
        param[0] = FocList.class;
        param[1] = Integer.TYPE;
        
        Object[] args = new Object[2];
        args[0] = list;
        args[1] = Integer.valueOf(viewID);
        
        try{
          Method method = null;        	
        	method = focObjectClass.getMethod("newBrowsePanel", param);
          panel = (FPanel) method.invoke(null, args);        	
        }catch(NoSuchMethodException e){
        	Class cls = getGuiBrowsePanelClass();
        	Constructor constr = cls.getConstructor(param);
        	panel = (FPanel) constr.newInstance(args);
        }
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
    return panel;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INDEX
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private void indexFill(){
    if(indexes.get(INDEX_IDENTIFIER) == null){
      //IDENTIFIER field
      FField identifierField = getIdentifierField();
      if(identifierField != null){
        DBIndex idIndex = new DBIndex(INDEX_IDENTIFIER, this, true);
        idIndex.addField(identifierField.getID());
        indexes.put(INDEX_IDENTIFIER, idIndex);
      }
    }

    if(indexes.get("MASTER") == null){
      //MASTER REF field
      FField masterRefField = this.getFieldByID(FField.MASTER_REF_FIELD_ID);
      if(masterRefField != null){
        DBIndex masterIndex = new DBIndex("MASTER", this, false);
        masterIndex.addField(masterRefField.getID());
        indexes.put("MASTER", masterIndex);        
      }
    }

    if(indexes.get("MAIN_KEY") == null){
      //UNIQUE KEY fields
      if(isKeyUnique()){
        DBIndex mainKeyIndex = null;
                
        Iterator iter = newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
        while(iter != null && iter.hasNext()){
        	if(mainKeyIndex == null){
        		mainKeyIndex = new DBIndex("MAIN_KEY", this, isKeyUnique());
        	}
        	FField field = (FField)iter.next();
          mainKeyIndex.addField(field.getID());
        }
        if(mainKeyIndex != null){
        	indexes.put("MAIN_KEY", mainKeyIndex);
        }else{
        	Globals.getDisplayManager().popupMessage("Missing key fields for "+getStorageName());
        }
      }
    }
  }
  
  private void indexCreate(){
    if(indexes == null){
      indexes = new HashMap<String, DBIndex>();
    }
  }
  
  public void indexAdd(DBIndex index){
    indexCreate();
    indexes.put(index.getName(), index);
  }
  
  public Iterator indexIterator(){
    Iterator iter = null;
    indexCreate();
    indexFill();
    if(indexes != null && indexes.values() != null){
      iter = indexes.values().iterator();
    }
    return iter;
  }
  
  /*
  public int indexCount(){
    indexInitialize();    
    return indexes != null ? indexes.size() : 0;
  }
  
  public DBIndex indexAt(int i){
    indexInitialize();    
    return indexes != null ? (DBIndex)indexes.get(i) : null;
  }
  */ 
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // FIELD LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    FIELDS
  // ---------------------------------
  public FocFieldEnum newFocFieldEnum(int category, int level) {
    return new FocFieldEnum(this, category, level);
  }

  public FField getIdentifierField() {
    FField refField = getFieldByID(FField.REF_FIELD_ID);
    if (refField == null) {
      refField = identifierField;
    }
    return refField;
  }
  
  public void setIdentifierField(FField identifierField) {
    if(getFieldByID(FField.REF_FIELD_ID) == null){
      this.identifierField = identifierField;      
    }
  }
  
  public FField getFieldByID(int id) {
    FField focField = null;
    if( fields == null ){
      int debug = 0;
    }
    
    focField = fields.getByID(id);
    
    //If Not found we should look for ArrayFields and take the current index
    if(focField == null){
      FFieldArray fldArray = arrayFields != null ? (FFieldArray) arrayFields.getByID(id) : null;
      if(fldArray != null){
        focField = fldArray.getCurrentField();
      }
    }

    return focField;
  }

  public FField getFieldByName(String name) {
    return fields.getByName(name);
  }

  public FField getFieldByDBCompleteName(String name) {
    FField found = null;
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while(enumer != null && enumer.hasNext()){
      enumer.next();
      String fldName = enumer.getFieldCompleteName(this);
      if(fldName.compareTo(name) == 0){
        found = getFieldByID(enumer.getFieldPath().get(0));
        break;
      }
    }
    return found;
  }

  public FField getFieldByDBCompleteName_GetDBLevelField(String name) {
    FField found = null;
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while(enumer != null && enumer.hasNext()){
      enumer.next();
      String fldName = enumer.getFieldCompleteName(this);
      if(fldName.compareTo(name) == 0){
        found = getFieldByID(enumer.getFieldPath().get(enumer.getFieldPath().size()-1));
        break;
      }
    }
    return found;
  }
  
  public FField getFieldByPath(FFieldPath path) {
    FField retField = null;

    FField curField = null;
    FocDesc curDesc = this;
    if (path != null) {
      int i = 0;
      for (i = 0; i < path.size(); i++) {
        int fieldID = path.get(i);
        curField = curDesc.getFieldByID(fieldID);
        curDesc = curField.getFocDesc();
      }
      if (i == path.size()) {
        retField = curField;
      }
    }

    return retField;
  }  
  
  public String getFieldGuiName(int fieldID){
    FField fld = getFieldByID(fieldID);
    return getFieldGuiName(fld);
  }
  
  public String getFieldGuiName(FField fld){
    return fld != null ? getStorageName()+"."+fld.getName() : "";    
  }
  
  public FField addReferenceField() {
  	FReferenceField refField = (FReferenceField)getFieldByID(FField.REF_FIELD_ID);
    if (refField == null) {
      refField = new FReferenceField(FField.REF_FIELD_NAME, FField.REF_FIELD_NAME);
      refField.setAutoIncrement(true);
      addField(refField);
    }
    
    return refField;
  }

  //rr Begin
  public FBoolField addDeprecatedField(){
    FBoolField field = (FBoolField)getFieldByID(FField.FLD_DEPRECATED_FIELD);
    if(field == null) {
      field = new FBoolField("DEPRECATED", "Deprecate", FField.FLD_DEPRECATED_FIELD, false);
      addField(field);
    }
    return field;
  }
  //rr End
  
  public FField addOrderField() {
    FIntField orderField = (FIntField)getFieldByID(FField.ORDER_FIELD_ID);
    if (orderField == null) {
      orderField = new FIntField(FField.ORDER_FIELD_NAME, FField.ORDER_FIELD_NAME, FField.ORDER_FIELD_ID, false, 10);
      addField(orderField);
    }
    return orderField;
  }
  
  public FCharField addNameField(){
  	FCharField field = (FCharField)getFieldByID(FField.FLD_NAME);
    if(field == null) {
    	field = new FCharField("NAME", "Name", FField.FLD_NAME, true, 20);
      addField(field);
    }
  	return field;
  }
  
  public FField addMasterReferenceField(FocDesc masterDesc) {
    FField masterField = getFieldByID(FField.MASTER_REF_FIELD_ID);    
    try{
      if (masterField == null && masterDesc != null) {
        FField masterRefFieldInMaster = null;
  
        // Getting the master reference field in the master description
        FocFieldEnum focFieldEnum = masterDesc.newFocFieldEnum(FocFieldEnum.CAT_REF, FocFieldEnum.LEVEL_DB);
        if (focFieldEnum != null && focFieldEnum.hasNext()) {
          masterRefFieldInMaster = (FField) focFieldEnum.next();
        }
  
        // Duplicate - change the name - add
        if (masterRefFieldInMaster != null) {
          masterField = (FField)masterRefFieldInMaster.clone();
          masterField.setId(FField.MASTER_REF_FIELD_ID);
          masterField.setName(FField.MASTER_REF_FIELD_NAME);
          this.addField(masterField);
          
          if(isAddMasterMirror()){
	          FMasterField masterMirrorField = new FMasterField(masterDesc);
	          this.addField(masterMirrorField);
          }
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return masterField;
  }
  
  public FocLink getListFieldLink(int fieldId){
    FocLink link = null;
    FListField listField = (FListField) getFieldByID(fieldId);
    if(listField != null){
      link = listField.getLink();
    }
    return link;
  }
  
  
  public final void afterConstructionInternal(){
  	scanFieldsAndCreateFormulas();
  	afterConstruction();
  }
  
  protected void afterConstruction(){
  	
  }
  
  private void scanFieldsAndCreateFormulas(){
  	FocFieldEnum enumeration = newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
  	while(enumeration != null && enumeration.hasNext()){
  		FField field = enumeration.nextField();
  		if(field != null){
  			field.createFormula(this);
  		}
  	}
  }

  // ---------------------------------
  //    KEY FLD
  // ---------------------------------

  private ArrayList getMandatoryFields(){
    if(mandatoryFields == null){
      mandatoryFields = new ArrayList();
      for(int i=0; i<getFieldsSize(); i++){
        FField field = getFieldAt(i);
        if(field.isMandatory()){
          mandatoryFields.add(field);
        }
      }
    }
    return mandatoryFields;
  }
  
  public int mandatoryFieldCount(){
    ArrayList arrList = getMandatoryFields();
    return arrList != null ? arrList.size() : 0;
  }

  public FField mandatoryFieldAt(int i){
    ArrayList arrList = getMandatoryFields();
    return arrList != null ? (FField) arrList.get(i) : null;
  }
  
  // ---------------------------------
  //    KEY FLD
  // ---------------------------------

  private void addFieldToKey(FField keyFld) {
    keyFields.add(keyFld);
    keyFld.setKey(true);
    //if (keyFields.size() > 1 && this.isKeyUnique()) {
    //  this.addReferenceField();
    //}
  }

  private void removeFieldFromKey(FField keyFld) {
    keyFields.remove(keyFld);
  }  
  
  protected int getKeyFieldsSize() {
    return keyFields.size();
  }

  protected FField getKeyFieldAt(int i) {
    return (keyFields != null) ? (FField) keyFields.get(i) : null;
  }
  
  public void setFieldAsNonKey(FField nonKeyField){
  	nonKeyField.setKey(false);
  	removeFieldFromKey(nonKeyField);
  }
  
  // ---------------------------------
  //    ALL FLD
  // ---------------------------------

  public int getFieldsSize() {
    return fields.size();
  }

  protected FField getFieldAt(int i) {
    return (fields != null) ? (FField) fields.get(i) : null;
  }

  public void addField(FField fld) {
    
    if(fld.getClass() == FFieldArray.class){
      
    }else{
      fld.setIndexOfPropertyInArray(propertyArrayLength++);
      fields.add(fld);
      if (fld.getKey()) {
        addFieldToKey(fld);
      }
      
      if(fld.getClass() == FInLineObjectField.class){
        FInLineObjectField inLineField = (FInLineObjectField) fld;
        inLineField.getFocDesc().setPropertyArrayLength(inLineField.getFocDesc().getPropertyArrayLength()+1);//+1:pour reserver un field pour le maser object
        //NON NON NON parce que ca cree une column dans la table inLineField.getFocDesc().addMasterReferenceField(this);
      }
      
    }
  }

  // ---------------------------------
  //    ARRAY FIELDS
  // ---------------------------------

  public void addFieldArray(FFieldArray fieldArray){
    if(arrayFields == null){
      arrayFields = new FFieldContainer();
    }
    fieldArray.setIndexOfPropertyInArray(propertyArrayLength++);
    arrayFields.add(fieldArray);
    for(int i=0; i<fieldArray.getSize(); i++){
      FField fld = fieldArray.getFieldAt(i);
      addField(fld);
    }
  }

  public FField getFieldArrayByID(int id){
    return arrayFields != null ? arrayFields.getByID(id) : null;
  }  
    
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // REFERENCE LOCATION LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public ArrayList getReferenceLocationList(boolean create){
    if(referenceLocationList == null && create){
      referenceLocationList = new ArrayList();
    }
    return referenceLocationList;
  }
  
  public void addReferenceLocation(ReferenceChecker refLoc){
    ArrayList list = getReferenceLocationList(true);
    if(refLoc != null && list != null){
      list.add(refLoc);
    }
  }
  
  public Iterator referenceLocationIterator(){
    Iterator iter = null;
    ArrayList list = getReferenceLocationList(false);
    if(list != null){
      iter = list.iterator();
    }
    return iter;
  }
  
  public void scanFieldsAndAddReferenceLocations(){
    for(int i=0 ;i<getFieldsSize(); i++){
      FField focField = getFieldAt(i);
      if(focField.isDBResident()){
        focField.addReferenceLocations(this);
      }
    } 
    
    /*
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
    while (enumer.hasNext()) {
      FField focField = (FField) enumer.next();      
      focField.addReferenceLocations(this);
    }
    */
  }
  
  public FField addListField(FFieldPath descPropertyPath, int listIdInMaster, FocDescForFilter focDescForFilter){
    FListField listField = null;
    FocLinkConditionalForeignKey focLink = new FocLinkConditionalForeignKey(descPropertyPath, true);
    if(focLink != null){
      String fieldName = focLink.getSlaveDesc().getStorageName();
      fieldName += FField.LIST_FIELD_SUFFIX;
      listField = new FListField(fieldName, fieldName, listIdInMaster, focLink, focDescForFilter);
      addField(listField);
    }
    return listField;
  }
  
  public FField addListField(FocDesc slaveDesc, int foreignKeyId, int listIdInMaster, FocDescForFilter focDescForFilter){
    FListField listField = null;
    FocLinkForeignKey focLink = new FocLinkForeignKey(slaveDesc, foreignKeyId, true);
    if(focLink != null){
      String fieldName = focLink.getSlaveDesc().getStorageName();
      fieldName += FField.LIST_FIELD_SUFFIX;
      listField = new FListField(fieldName, fieldName, listIdInMaster, focLink, focDescForFilter);
      addField(listField);
    }
    return listField;
  }
  
  public FField addListField(FocDesc slaveDesc, int foreignKeyId, int listIdInMaster){
    return addListField(slaveDesc, foreignKeyId, listIdInMaster, null);    
  }
  
  public FocLink getN2nLink() {
    return n2nLink;
  }  
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public int getPropertyArrayLength() {
    return propertyArrayLength;
  }
  
  public void setPropertyArrayLength(int propertyArrayLength) {
    this.propertyArrayLength = propertyArrayLength;
  }

	public boolean isDbResident() {
		return dbResident;
	}

	public void setDbResident(boolean dbResident) {
		this.dbResident = dbResident;
	}
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	private FocList defaultFocList = null;
	
	public FocList getDefaultFocList(int mode){
		if(defaultFocList == null){
			FocListOrder order = new FocListOrder();
			
			FocFieldEnum enumer = new FocFieldEnum(this, FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
			while(enumer != null && enumer.hasNext()){
				enumer.next();				
				order.addField(enumer.getFieldPath());
			}
			
			defaultFocList = getList(defaultFocList, mode, order);
		}
		return defaultFocList;
	}
	
  public FocList getList(FocList focList, int mode){
    return getList(focList, mode, null);
  }

  public FocList getList(FocList focList, int mode, FocListOrder order){
    return getList(focList, mode, order, null);
  }

  public FocList getList(FocList focList, int mode, FocListOrder order, SQLFilter filter){
    if(focList == null){
      FocLink link = new FocLinkSimple(this);
      focList = new FocList(null, link, filter);
    }
    
    if(mode == FocList.LOAD_IF_NEEDED){
      focList.loadIfNotLoadedFromDB();      
    }else if(mode == FocList.FORCE_RELOAD){
      focList.removeAll();
      focList.reloadFromDB();
    }

    if(order != null){
    	focList.setListOrder(order);
    }else{
    	focList.sort();
    }
    
    return focList;
  }

	public void setGuiBrowsePanelClass(Class focObjectBrowsePanelClass) {
		this.guiBrowsePanelClass = focObjectBrowsePanelClass;
	}

	public void setGuiDetailsPanelClass(Class focObjectDetailsPanelClass) {
		this.guiDetailsPanelClass = focObjectDetailsPanelClass;
	}

	public Class getGuiBrowsePanelClass() {
		return guiBrowsePanelClass;
	}

	public Class getGuiDetailsPanelClass() {
		return guiDetailsPanelClass;
	}

	public boolean isAddMasterMirror() {
		return addMasterMirror;
	}

	public void setAddMasterMirror(boolean addMasterMirror) {
		this.addMasterMirror = addMasterMirror;
	}

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // REVISION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  public boolean isRevisionSupportEnabled() {
    return getRevisionPath() != null;
  } 
  
  public boolean isParentRevisionSupportEnabled() {
    return parentRevisionSupport;
  }
  
  public FFieldPath getRevisionPath(){
    return revisionPath;
  }
  
  public void setChildRevisionSupportEnabled(FFieldPath revisionPath/*boolean revisionSupport*/) {
    this.revisionPath = revisionPath;

    FIntField creationRevisionField = (FIntField) getFieldByID(FField.CREATION_REVISION_FIELD_ID);
    FIntField deletionRevisionField = (FIntField) getFieldByID(FField.DELETION_REVISION_FIELD_ID);
    FObjectField newItemField = (FObjectField) getFieldByID(FField.NEW_ITEM_FIELD_ID);
    
    if (creationRevisionField == null && deletionRevisionField == null && newItemField == null ) {
      
      creationRevisionField = new FIntField(FField.CREATION_REVISION_FIELD_ID_NAME, FField.CREATION_REVISION_FIELD_ID_NAME, FField.CREATION_REVISION_FIELD_ID, false, 10);
      addField(creationRevisionField);
      
      deletionRevisionField = new FIntField(FField.DELETION_REVISION_FIELD_ID_NAME, FField.DELETION_REVISION_FIELD_ID_NAME, FField.DELETION_REVISION_FIELD_ID, false, 10);
      addField(deletionRevisionField);
      
      newItemField = new FObjectField(FField.NEW_ITEM_FIELD_ID_NAME, FField.NEW_ITEM_FIELD_ID_NAME, FField.NEW_ITEM_FIELD_ID, false, this, FField.NEW_ITEM_FIELD_ID_NAME_PREFIX);
      newItemField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
      newItemField.setComboBoxCellEditor(FField.REF_FIELD_ID);
      addField(newItemField);
    }
  }
  
  public void setParentRevisionSupportEnabled() {
    this.parentRevisionSupport = true;
    if(this.parentRevisionSupport){
      FIntField revisionField = (FIntField) getFieldByID(FField.REVISION_FIELD_ID);
      if (revisionField == null ) {
        revisionField = new FIntField(FField.REVISION_FIELD_ID_NAME, FField.REVISION_FIELD_ID_NAME, FField.REVISION_FIELD_ID, false, 5);
        addField(revisionField);
      }
    }
  }
  
  protected FObjectField setWithObjectTree(){
  	FObjectField fatherNode = new FObjectField(FField.FATHER_NODE_FIELD_NAME, FField.FATHER_NODE_FIELD_NAME, FField.FLD_FATHER_NODE_FIELD_ID, false, this, "FATHERNODE_");
    fatherNode.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fatherNode.setComboBoxCellEditor(FField.REF_FIELD_ID);
    addField(fatherNode);
    setFObjectTreeFatherNodeID(FField.FLD_FATHER_NODE_FIELD_ID);
    return fatherNode;
  }
  
  //BElie
  private int fObjectTreeFatherNodeID = -5;

  public int getFObjectTreeFatherNodeID() {
    return fObjectTreeFatherNodeID;
  }

  public void setFObjectTreeFatherNodeID(int objectTreeFatherNodeID) {
    fObjectTreeFatherNodeID = objectTreeFatherNodeID;
  }
  
  public boolean isTreeDesc(){
    return fObjectTreeFatherNodeID > 0 || fObjectTreeFatherNodeID == FField.FLD_FATHER_NODE_FIELD_ID;
  }
  //EElie

  //rr Begin
  public boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }
  //rr End
}
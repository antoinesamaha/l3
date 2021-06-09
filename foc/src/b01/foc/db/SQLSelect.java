/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import java.sql.*;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.list.*;
import b01.foc.property.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class SQLSelect extends SQLRequest {
  protected final static int LOAD_IN_OBJECT = 0;
  protected final static int LOAD_IN_EMPTY_LIST = 1;
  protected final static int LOAD_IN_EXISTING_LIST = 2;
  
  protected int loadMode = LOAD_IN_OBJECT;// We have 2 modes: 1-Updating an
                                        // existing FocList 2-Creating a new
                                        // FocList as a result
  protected FocList focList = null;
  private FocObject focObjectToBeFilled = null;
  
  public SQLSelect(FocObject focObject, FocDesc focDesc, SQLFilter filter) {
    super(focDesc, filter);
    this.focObjectToBeFilled = focObject;
    loadMode = LOAD_IN_OBJECT;
  }
  
  public SQLSelect(FocList initialList, FocDesc focDesc, SQLFilter filter) {
    super(focDesc, filter);
    this.focList = initialList;
    if(focList == null){
      loadMode = LOAD_IN_OBJECT;
    }else if(focList.size() > 0){
      loadMode = LOAD_IN_EXISTING_LIST;
    }else{
      //loadMode = LOAD_IN_EMPTY_LIST;
      loadMode = LOAD_IN_EXISTING_LIST;
    }
  }
  
  public void dispose(){
    super.dispose();
    focList = null;
    focObjectToBeFilled = null;
  }

  public FocObject getFocObjectToBeFilled(){
  	return focObjectToBeFilled;
  }
  
  public boolean buildRequest() {
    request = new StringBuffer("");
    boolean error = false;

    if (focDesc != null && focDesc.getDBResident()) {
      boolean firstField = true;
      request.append("SELECT ");
      
      String mainTableAlias = filter.getJoinMap().getMainTableAlias();
      String PrefixForMainTable = filter.hasJoinMap()?mainTableAlias+".":"";
     
      FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
      while (enumer.hasNext()) {
        FField focField = (FField) enumer.next();
        FFieldPath path = enumer.getFieldPath();
        if (focField != null && isFieldInQuery(path)) {
          if (!firstField) {
            request.append(",");
          }
          //request.append(enumer.getFieldCompleteName(focDesc)+" \""+enumer.getFieldCompleteName(focDesc)+"\"");
          request.append(PrefixForMainTable+enumer.getFieldCompleteName(focDesc));
          firstField = false;
        }
      }
      addFrom();
      error = addWhere();
    }
    return error;
  }
  
  protected int findFieldPositionInMetaData(ResultSetMetaData metaData, String fieldName){
    int foundPos = -1;
    
    try {
      for(int i=1; i<metaData.getColumnCount() && foundPos < 0; i++){
        String colName = metaData.getColumnName(i);
        if(colName.trim().toUpperCase().compareTo(fieldName.trim().toUpperCase()) == 0){
          foundPos = i;
        }
      }
    } catch (SQLException e) {
      Globals.logException(e);
    }
    
    return foundPos;
  }
  
  private void notifyPropertiesListenersForObject(FocObject obj){
    if(obj != null){ 
      obj.setDesactivateSubjectNotifications(true);
      obj.scanPropertiesAndNotifyListeners();
      obj.backup();
      if(Globals.getApp().getRightsByLevel() != null){
        Globals.getApp().getRightsByLevel().lockValuesIfNecessary(obj);                  
      }
      obj.setDesactivateSubjectNotifications(false);
    }
  }
  
  protected void treatResultSet(ResultSet resultSet){
    try {
      HashMap<FocObject, FocObject> visitedObjects = null;
      if (loadMode == LOAD_IN_EXISTING_LIST) {
        // If we are in update list mode, we should keep track of visited objects
        // to remove unvisited objects from initial list
        visitedObjects = new HashMap<FocObject, FocObject>();
        focList.setSleepListeners(true);
      }

      ResultSetMetaData meta = resultSet.getMetaData();
      // The identifier field position ensures the possibility to
      // directly read the identifier without having to scan all fields
      int posInSelectForIdentifierField = -1;
      FField identifierField = focDesc.getIdentifierField();
      if(identifierField != null){
        posInSelectForIdentifierField = findFieldPositionInMetaData(meta, identifierField.getName());
      }           
          
      //This temp object is only to read the identifier property
      FocObject tempObject = null;
      if (loadMode == LOAD_IN_EXISTING_LIST) {
        FocConstructor constr = new FocConstructor(focDesc, null, focList.getMasterObject());
        tempObject = constr.newItem();
        tempObject.setDbResident(false);
      }
      
      boolean firstResult = true;
      while (resultSet.next()) {
        FocObject addedObject = null;
        //Reading identifier property into tempObject
        //-------------------------------------------
        String identifierValue = null;
        if(posInSelectForIdentifierField >= 0){
          identifierValue = resultSet.getString(posInSelectForIdentifierField);
        }
        //-------------------------------------------
        
        if (loadMode == LOAD_IN_EXISTING_LIST || loadMode == LOAD_IN_EMPTY_LIST) {
          FProperty identifierProp = null;
          
          if(loadMode == LOAD_IN_EXISTING_LIST){            
            identifierProp = tempObject.getIdentifierProperty();
            if(identifierProp != null){
              identifierProp.setSqlString(identifierValue);
            }
            if(tempObject.getReference() != null){
              //addedObject = focList.searchByReference(tempObject.getReference().getInteger());
              addedObject = focList.searchByRealReferenceOnly(tempObject.getReference().getInteger());
            }
          }
          if (addedObject == null) {
            //boolean backup = focList.isDesactivateSubjectNotifications();
            //focList.setDesactivateSubjectNotifications(true);
            addedObject = focList.newItem(identifierProp);
            focList.add(addedObject);
            //Globals.logString("dans added object == null, debugCount : "+debugCount+ " foc list apres "+ focList.size() );
            //focList.setDesactivateSubjectNotifications(backup);
          }
        }else{
          FProperty identifierProp = focObjectToBeFilled.getIdentifierProperty();
          if(identifierProp != null){
            String initialString = identifierProp.getString();                
            identifierProp.setSqlString(identifierValue);
            String newString = identifierProp.getString();
            identifierProp.setString(initialString);
            
            if(initialString.compareTo(newString) == 0){
              if(!firstResult){
                Globals.logString("Warning! duplicate items detected in " + focDesc.getStorageName());
              }
              addedObject = focObjectToBeFilled;
              firstResult = false;
            }
          }
        }                                         

        if(addedObject != null){
          addedObject.setDesactivateSubjectNotifications(true);
          addedObject.setLoadedFromDB(true);
          if (visitedObjects != null) {
            visitedObjects.put(addedObject, addedObject);
          }
          
          int i = 1;
          FocFieldEnum enumer = addedObject.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
          while (enumer.hasNext()) {
            FField focField = (FField) enumer.next();
            FFieldPath path = enumer.getFieldPath();
            if (focField != null && isFieldInQuery(path)) {
	            FProperty prop = enumer.getProperty();
	            if (prop != null ) {
                //BAntoineS-HSG-ORACLE-BLOB
                prop.getValueFromResultSet(resultSet, i);
                //EAntoineS-HSG-ORACLE-BLOB
	            }
            
	            i++;
            }
          }
          addedObject.resetStatus();
          addedObject.setDesactivateSubjectNotifications(false);          
        }
      }

      //Re-Scan properties and notify listeners
      //---------------------------------------
      if (loadMode == LOAD_IN_EXISTING_LIST){
        if (visitedObjects != null) {
          Iterator iter = visitedObjects.keySet().iterator();
          while(iter != null && iter.hasNext()){
            FocObject addedObject = (FocObject) iter.next();
            notifyPropertiesListenersForObject(addedObject);
          }
        }        
      }else if (loadMode == LOAD_IN_EMPTY_LIST){
        for(int i=0; i<focList.size(); i++){
          FocObject addedObject = (FocObject) focList.getFocObject(i);
          notifyPropertiesListenersForObject(addedObject);
        }
      }else{
        notifyPropertiesListenersForObject(focObjectToBeFilled);
      }
      //---------------------------------------      
      
      if(tempObject != null){
        tempObject.setFatherSubject(null);
        tempObject.dispose();
        tempObject = null;
      }
            
      // Removing from the list objects that are not in DBF any more
      if (loadMode == LOAD_IN_EXISTING_LIST) {
        Iterator iter = focList.focObjectIterator();
        while (iter != null && iter.hasNext()) {
          FocObject obj = (FocObject) iter.next();
          if (visitedObjects.containsKey(obj) == false) {
            //BElias
            //focList.removeCurrentObjectFromIterator(iter);
            focList.elementHash_removeCurrentObjectFromIterator(iter,obj);
            //EElias
          }
        }
        
        focList.setSleepListeners(false);
      }            

    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  public boolean execute() {
    boolean error = Globals.getDBManager() == null;  
    if(!error && focDesc != null && focDesc.getDBResident() == FocDesc.DB_RESIDENT){
      Statement stmt = Globals.getDBManager().lockStatement();
  
      ResultSet resultSet = null;
      if (stmt != null) {
        error = buildRequest();
        if(!error){
          try {
            String reqAdapted = getRequestAdaptedToProvider();
          	if(ConfigInfo.isLogDBRequestActive() && ConfigInfo.isLogDBSelectActive()){
          		Globals.logString(reqAdapted);
          	}
            resultSet = stmt.executeQuery(reqAdapted);              
          } catch (Exception e) {
            Globals.logException(e);
          }
        }
      }
      
      if(!error){
        if (resultSet != null) {
          treatResultSet(resultSet);
          try{
            resultSet.close();
          }catch(Exception e){
            Globals.logException(e);
          }
        }
      }      
      Globals.getDBManager().unlockStatement(stmt);
      
    }
    return error;
  }

  public FocList getFocList() {
    return focList;
  }
}
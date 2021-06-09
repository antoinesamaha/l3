/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.desc.*;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author 01Barmaja
 */
public class SQLRequest {

  protected FocDesc focDesc = null;

  protected SQLFilter filter = null;
  protected StringBuffer request = null;
  //protected ResultSet resultSet = null;
  private ArrayList<Integer> queryFieldList = null;
  
  public SQLRequest(FocDesc focDesc, SQLFilter filter) {
    this.focDesc = focDesc;
    this.filter = filter;
    queryFieldList = null;
  }

  public SQLRequest(FocDesc focDesc) {
    this.focDesc = focDesc;
    this.filter = null;
  }
  
  public void dispose(){
    focDesc = null;
    filter = null;
    request = null;
    if(queryFieldList != null){
      queryFieldList.clear();
      queryFieldList = null;
    }
  }

  private ArrayList<Integer> getQueryFieldList(boolean create){
    if(queryFieldList == null && create){
      queryFieldList = new ArrayList<Integer>();
    }
    return queryFieldList;
  }

  public int getQueryFieldCount(){
    ArrayList arrayList = getQueryFieldList(false);
    return (arrayList != null) ? arrayList.size() : 0;
  }

  public int getQueryFieldAt(int i){
    ArrayList arrayList = getQueryFieldList(false);
    return (arrayList != null) ? ((Integer)(arrayList.get(i))).intValue() : 0;
  }

  public void addQueryField(int i){
    ArrayList<Integer> arrayList = getQueryFieldList(true);
    arrayList.add(Integer.valueOf(i));
  }

  //BElie
  public boolean isFieldInQuery(int fieldID){
    FField field = focDesc.getFieldByID(fieldID);
    boolean ret = queryFieldList == null && field.isIncludeInDBRequests();
    
    for(int i=0; i<getQueryFieldCount() && !ret; i++){      
      ret = getQueryFieldAt(i) == fieldID;
    }
    return ret ;
  }
  //EElie
  public boolean isFieldInQuery(FFieldPath path){
    return path != null ? isFieldInQuery(path.get(0)) : false;
  }
  
  public void addFrom(boolean withJoin) {
    if (focDesc != null && request != null) {
      request.append(" FROM ");
      
      if(filter != null && filter.hasJoinMap()){
        Iterator iter = filter.getJoinMap().getJoinMapIterator();
        boolean firstTable = true;
        while (iter.hasNext()){
          SQLJoin join = (SQLJoin)iter.next();
          if(firstTable){
            request.append(focDesc.getStorageName()+" "+filter.getJoinMap().getMainTableAlias());
            firstTable = false;
          }
          request.append(", ");
          request.append(join.getNewTableName() +" "+join.getNewAlias());  
        }
      }else{
      	if(withJoin && filter != null && filter.hasJoinMap()){
      		request.append(focDesc.getStorageName()+" "+filter.getJoinMap().getMainTableAlias());
      	}else{
      		request.append(focDesc.getStorageName());
      	}
      }
    }
  }
  
  public void addFrom() {
  	addFrom(true);
  }

  public void append(String str) {
    if (request != null) request.append(str);
  }

  /*
   * private boolean addFieldToWhere(FocObject templateFocObj, String fldName,
   * int fieldID, boolean isFirst) { FProperty objProp =
   * templateFocObj.getFocProperty(fieldID); FField propField =
   * objProp.getFocField(); String value = objProp.getString(); String sqlValue =
   * objProp.getSqlString(); boolean errorAddingField = true; boolean
   * valueNotNull = (fieldID == FField.REF_FIELD_ID) ? value.compareTo("0") != 0 :
   * value.compareTo("") != 0;
   * 
   * if (valueNotNull && fldName.compareTo("") != 0) { if (isFirst) {
   * request.append(" WHERE ("); } else { request.append(" and ("); }
   * request.append(fldName); request.append("="); request.append(sqlValue); if
   * (!isFirst) { request.append(")"); } errorAddingField = false; } return
   * errorAddingField; }
   * 
   * protected void addWhere() { if (focDesc != null && filter != null &&
   * request != null) { boolean isFirst = true; boolean atLeastOneFieldAdded =
   * false;
   * 
   * //Building Where on Template fields //---------------------------------
   * FocObject focObj = filter.getObjectTemplate();
   * 
   * if (focObj != null) { //We start with the idetifier property field
   * FProperty idProp = focObj.getIdentifierProperty(); FField idField = (idProp !=
   * null) ? idProp.getFocField() : null; if (idField != null) { boolean
   * errorAdding = addFieldToWhere(focObj, idField.getName(), idField.getID(),
   * isFirst); isFirst = isFirst && errorAdding; atLeastOneFieldAdded =
   * !errorAdding; }
   * 
   * //If the identifier is not added we work on the key fields if(isFirst){
   * Enumeration enum = focDesc.newFocFieldEnum(FocFieldEnum.CAT_KEY,
   * FocFieldEnum.LEVEL_DB_FIELDS); while (enum.hasMoreElements()) { FField
   * focField = (FField) enum.nextElement(); boolean errorAdding =
   * addFieldToWhere(focObj, focField.getName(), focField.getID(), isFirst);
   * isFirst = isFirst && errorAdding; atLeastOneFieldAdded = !errorAdding; } } }
   * 
   * //Building Where on Master fields //-------------------------------
   * FocObject masterObj = filter.getMasterObject(); if (masterObj != null) {
   * FField focSlaveField = (FField)
   * focDesc.getFieldByID(FField.MASTER_REF_FIELD_ID); Enumeration enum =
   * masterObj.getThisFocDesc().newFocFieldEnum(FocFieldEnum.CAT_REF,
   * FocFieldEnum.LEVEL_DB_FIELDS); if (enum != null && enum.hasMoreElements() &&
   * focSlaveField != null) { FField focMasterField = (FField)
   * enum.nextElement(); boolean errorAdding = addFieldToWhere(masterObj,
   * focSlaveField.getName(), focMasterField.getID(), isFirst); isFirst =
   * isFirst && errorAdding; atLeastOneFieldAdded = !errorAdding; } }
   * 
   * if (!isFirst && atLeastOneFieldAdded) { request.append(")"); } } }
   */

  public boolean addWhere(boolean withJoin) {
    boolean requestNotValid = false;
    if (filter != null) {
      requestNotValid = filter.addWhereToRequest(this);
      if(filter.hasJoinMap()){
        Iterator iter = filter.getJoinMap().getJoinMapIterator();
        request.append(" ");
        boolean firstJoin = true;
        while(iter.hasNext()){
          SQLJoin join = (SQLJoin)iter.next();
          request.append("and ");
          if(firstJoin){
            request.append("( ");
            firstJoin = false;
          }
          request.append("( "+join.getLinkCondition()+" )"+" ");
        }
        request.append(")");
      }
    }
    return requestNotValid;
  }

  public boolean addWhere() {
    return addWhere(true);
  }

  public boolean buildRequest() {
    return false;
  }

  public static String adapteRequestToDBProvider(StringBuffer request){
    String req = request.toString();
    if (Globals.getDBManager()!= null && Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
      req = req.replace('"', '\'');
    }
    return req;
  }
  
  public String getRequestAdaptedToProvider(){
  	return adapteRequestToDBProvider(request);
  }
  
  //BAntoineS - AUTOINCREMENT
  protected void executeUpdate(Statement stmt, String req) throws Exception{
  	stmt.executeUpdate(req);
  }
  //BAntoineS - AUTOINCREMENT
  
  public boolean execute(){
    boolean error = false;
    Statement stmt = Globals.getDBManager().lockStatement();
    if (stmt != null && focDesc.getDBResident()) {
      error = buildRequest();

      try {
        String req = getRequestAdaptedToProvider();
        if(ConfigInfo.isLogDBRequestActive()){
        	Globals.logString(req);
        }
        //BAntoineS - AUTOINCREMENT
        executeUpdate(stmt, req);
        //EAntoineS - AUTOINCREMENT
      } catch (Exception e) {
        SQLException sqlE = (SQLException) e;  
        Globals.logString(sqlE.getMessage());
        error = true;
        Globals.logException(e);
      }
      Globals.getDBManager().unlockStatement(stmt);      
    }

    return error;
  }

  /**
   * @return
   */
  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  protected void setFocDesc(FocDesc focDesc) {
    this.focDesc = focDesc;
  }
}
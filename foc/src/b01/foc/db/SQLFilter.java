/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.property.*;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class SQLFilter {
  private FocObject focObjTemplate = null;
  private FocObject masterObject = null;
  private int filterFields = 0;
  private ArrayList<Integer> selectedFields = null;
  private StringBuffer additionalWhere = null;
  private SQLJoinMap joinMap = null;
  
  public static final String KEY_FILTER_BUTTON_ADDITINAL_WHERE = "FIL_BUT";
  public static final String KEY_MASTER_FIELD_ADDITINAL_WHERE  = "MST_FLD";
  public static final String KEY_NO_KEY_ADDITINAL_WHERE        = "NO_KEY";
  public static final String KEY_FOREIGN_KEY_ADDITINAL_WHERE   = "FRGN_KEY";
  
  private HashMap<String, String> additionalWhereMap = null;
  
  
  //private int aliasCount = 1;
  //BTOADD
  /*
  private HashMap joinHashMap = null; //String nom de la table, Join Object ok
  
  public void addJoin(table, Join)  ok
  
  create the class Join in the foc.db sqlFilter  ok
  class Join{ ok
    private String linkRequest = ""; ok
    
    get ok
    set ok
  }
  
  In the buildRequest for select
    If the SQLFilter has additional joins -> We include the storage name before all fields
    In the From we add the new tables
    In the where we add a section saying AND (cond_for_join1 and cond_for_join2 and cond_for_join3)
  
  To create the join condition:
    we should ask the FField to do it.
    All FFields alreaady implement 
      1- getFocDesc() which returns the new table (desc) to link to
      2- have to implement :CREATE LINK CONDITION WITCH USES GETfOCdESC to buikd the MAINTABLE.MASTER FIELD = SECTABLE.REF 
  */
  //ETOADD  

  public static final int FILTER_ON_IDENTIFIER = 1;
  public static final int FILTER_ON_KEY = 2;
  public static final int FILTER_ON_ALL = 3;
  public static final int FILTER_ON_SELECTED = 4;

  public SQLFilter(FocObject focObjTemplate, int filterFields) {
    this.focObjTemplate = focObjTemplate;
    this.filterFields = filterFields;
    this.joinMap = null;
    if(filterFields == FILTER_ON_SELECTED){
      selectedFields = new ArrayList<Integer>();
    }
  }
  
  public void dispose(){
    if(focObjTemplate != null){
      focObjTemplate.dispose();
      focObjTemplate = null;
    }
    
    masterObject = null;
    if(selectedFields != null){
      selectedFields.clear();
      selectedFields = null;
    }
    
    if(additionalWhere != null){      
      additionalWhere = null;
    }
    if(additionalWhereMap != null){
      additionalWhere = null;
    }
  }
  
  public void setFilterFields(int filterFields) {
    this.filterFields = filterFields;
  }
  
  public void addSelectedField(int field){
    if(selectedFields == null){
      selectedFields = new ArrayList<Integer>();
    }
    selectedFields.add(Integer.valueOf(field));       
  }

  public void resetSelectedFields(){
    if(selectedFields != null){
      selectedFields.clear();
    }       
  }

  public FocObject getObjectTemplate() {
    return focObjTemplate;
  }

  public void setObjectTemplate(FocObject focObjTemplate) {
    this.focObjTemplate = focObjTemplate;
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
  
/*  public SQLJoin addJoin (SQLJoin join){
    if(joinHashMap == null){
      joinHashMap = new HashMap<String, SQLJoin>();
    }
    SQLJoin newJoin = joinHashMap.get(join.getKey());
    if(newJoin == null){
      newJoin = join;
      String newTableName = join.getNewTableName();
      String newAlias = "T"+aliasCount++;
      joinHashMap.put(newJoin.getKey(), newJoin);      
      newJoin.setNewAlias(newAlias);
      addTableToMap(newTableName, newAlias);
    }
    return newJoin;
  }*/
  
  public boolean hasJoinMap(){
    return joinMap != null && joinMap.size() > 0;
  }
  
  public SQLJoinMap getJoinMap(){
    if (joinMap == null){
      joinMap = new SQLJoinMap();
    }
    return joinMap;
  }
  
  private boolean addFieldToWhere(SQLRequest sql, String fldName, String sqlValue, boolean isFirst) {
    //String value = objProp.getString();
    boolean errorAddingField = true;
    boolean valueNotNull = true;// (fieldID == FField.REF_FIELD_ID) ?
                                // value.compareTo("0") != 0 :
                                // value.compareTo("") != 0;

    if (valueNotNull) {
      if (isFirst) {
        sql.append(" WHERE (");
      } else {
        sql.append(" and (");
      }
      sql.append(fldName);
      sql.append("=");
      sql.append(sqlValue);
      if (!isFirst) {
        sql.append(")");
      }
      errorAddingField = false;
    }
    return errorAddingField;
  }
  
  private boolean addFieldToWhere(SQLRequest sql, FocObject template, String fldName, int fieldID, boolean isFirst) {
    FProperty objProp = template.getFocProperty(fieldID);
    //String value = objProp.getString();
    String sqlValue = objProp.getSqlString();
    return addFieldToWhere(sql, fldName, sqlValue, isFirst);
  }

  public boolean addWhereToRequest(SQLRequest sql) {
    boolean requestNotValid = false;
    if (sql != null) {
      boolean isFirst = true;
      boolean atLeastOneFieldAdded = false;

      // Building Where on Template fields
      // ---------------------------------
      FocObject focObj = getObjectTemplate();

      if (focObj != null) {
        // We start with the idetifier property field
        if (filterFields == FILTER_ON_IDENTIFIER) {
          FProperty idProp = focObj.getIdentifierProperty();
          FField idField = (idProp != null) ? idProp.getFocField() : null;
          //This is to prevent useless requests with where ref=0
          if(idProp != null && idField.getID() == FField.REF_FIELD_ID && idProp.getInteger() == 0){
            requestNotValid = true;        
          }
          if (idField != null) {
            boolean errorAdding = addFieldToWhere(sql, focObj, idField.getName(), idField.getID(), isFirst);
            isFirst = isFirst && errorAdding;
            atLeastOneFieldAdded = !errorAdding;
          }
        }

        // If the identifier is not added we work on the key fields
        if (filterFields == FILTER_ON_KEY) {
          FocFieldEnum enumer = new FocFieldEnum(sql.getFocDesc(), focObjTemplate, FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_DB);
          while (enumer.hasNext()) {
            enumer.next();
            FFieldPath path = enumer.getFieldPath();
            if(path != null){
              FProperty prop = enumer.getProperty();
              boolean errorAdding = addFieldToWhere(sql, enumer.getFieldCompleteName(sql.getFocDesc()), prop.getSqlString(), isFirst);
              //boolean errorAdding = addFieldToWhere(sql, focObj, enumer.getFieldCompleteName(sql.getFocDesc()), focField.getID(), isFirst);
              isFirst = isFirst && errorAdding;
              atLeastOneFieldAdded = !errorAdding;
            }
            
            /*
            FField focField = (FField) enumer.next();
            boolean errorAdding = addFieldToWhere(sql, focObj, enumer.getFieldCompleteName(sql.getFocDesc()), focField.getID(), isFirst);
            isFirst = isFirst && errorAdding;
            atLeastOneFieldAdded = !errorAdding;
            */
          }
        }
        
        if (filterFields == FILTER_ON_SELECTED) {
          for(int i=0; i<selectedFields.size(); i++){
            int fieldId = ((Integer)selectedFields.get(i)).intValue();
            FField focField = focObjTemplate.getThisFocDesc().getFieldByID(fieldId);
            if(focField != null){
              //Here we scan all the fields and find the ones with father focField to put a where on them
              FocFieldEnum enumer = new FocFieldEnum(sql.getFocDesc(), focObjTemplate, FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
              while (enumer.hasNext()) {
                enumer.next();
                FFieldPath path = enumer.getFieldPath();
                if(path != null){
                  int dbRootFieldId = path.get(0);
                  if(dbRootFieldId == fieldId){
                    FProperty prop = enumer.getProperty();
                    boolean errorAdding = addFieldToWhere(sql, enumer.getFieldCompleteName(sql.getFocDesc()), prop.getSqlString(), isFirst);
                    //boolean errorAdding = addFieldToWhere(sql, focObj, enumer.getFieldCompleteName(sql.getFocDesc()), focField.getID(), isFirst);
                    isFirst = isFirst && errorAdding;
                    atLeastOneFieldAdded = !errorAdding;
                  }
                }
              }              
            }
          }
        }
      }

      // Building Where on Master fields
      // -------------------------------
      FocObject masterObj = getMasterObject();
      if (masterObj != null) {
        FField focSlaveField = (FField) sql.getFocDesc().getFieldByID(FField.MASTER_REF_FIELD_ID);
        if(focSlaveField != null){
          FProperty masterIdentifier = masterObj.getIdentifierProperty();
          FField focMasterField = masterObj.getThisFocDesc().getIdentifierField();
          boolean errorAdding = addFieldToWhere(sql, masterObj, focSlaveField.getName(), focMasterField.getID(), isFirst);
          isFirst = isFirst && errorAdding;
          atLeastOneFieldAdded = !errorAdding;
          //This is to prevent useless requests with where M_REF=0
          if(masterIdentifier.getInteger() == 0){
            requestNotValid = true;        
          }
        }
      }

      if (!isFirst && atLeastOneFieldAdded) {
        sql.append(")");
      }
      
      StringBuffer additionalWhere = getAdditionalWhere();
      if(additionalWhere != null && additionalWhere.length() > 0){
        if(atLeastOneFieldAdded){
          sql.append(" and (");
        }else{
          sql.append(" WHERE (");
        }
        sql.append(additionalWhere.toString());
        sql.append(")");
      }
    }

    return requestNotValid;
  }

  public void copy_SelectedFieldsValues_From_Template_To_Object(FocObject obj){
    if(filterFields == FILTER_ON_SELECTED && focObjTemplate != null && obj != null){
      for(int i=0; i<selectedFields.size(); i++){
        int fieldId = ((Integer)selectedFields.get(i)).intValue();
        obj.getFocProperty(fieldId).copy(focObjTemplate.getFocProperty(fieldId));
      }
    }
  }  
  
  public StringBuffer getAdditionalWhere() {
    StringBuffer res = new StringBuffer();
    boolean firstCondition = true;
    if(additionalWhere != null && additionalWhere.length() > 0){
      res.append("( ");
      res.append("( "+ additionalWhere+" ) ");
      firstCondition = false;
    }
    if(additionalWhereMap != null){
      Iterator iter = additionalWhereMap.keySet().iterator();
      while(iter != null && iter.hasNext()){
        String condition = (String)additionalWhereMap.get(iter.next());
        if(condition != null){
          if(!firstCondition){
            res.append("and ");
          }else{
            res.append(" ( ");
            firstCondition = false;
          }
          res.append("( "+condition +") ");
        }
      }
    }
    if(res.length() > 0){
      res.append(") ");
    }
    return res;
  }

  public void setAdditionalWhere(StringBuffer buff) {
    additionalWhere = buff;
  }
  
  public void putAdditionalWhere(String key, String additionnalWhere){
    if(additionalWhereMap == null){
      additionalWhereMap = new HashMap<String, String>();
    }
    additionalWhereMap.put(key, additionnalWhere);
  }
  
  public String getAdditionalWhere(String key){
    return additionalWhereMap != null ? additionalWhereMap.get(key):null;
  }
  
  @Deprecated
  public void addAdditionalWhere(StringBuffer additionalWhere) {
    if(this.additionalWhere == null){
      this.additionalWhere = new StringBuffer(additionalWhere);
    }else{
      this.additionalWhere.append(additionalWhere);  
    }
  }
}

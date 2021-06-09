/*
 * Created on Jan 9, 2006
 */
package b01.foc.join;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;

/**
 * @author 01Barmaja
 */
public class FocRequestDesc {
  private FocDesc focDesc = null;
  private FocDesc mainDesc = null;
  private HashMap aliasMap = null;
  private ArrayList fieldList = null;
  private TableAlias rootTableAlias = null;
  
  public FocRequestDesc(FocDesc mainDesc){
    this.mainDesc = mainDesc;
    aliasMap = new HashMap();
    fieldList = new ArrayList();
  }
  
  public TableAlias getRootTableAlias(){
    return rootTableAlias;
  }
  
  public void addTableAlias(TableAlias tableAlias){
    aliasMap.put(tableAlias.getAlias(), tableAlias);
    if(tableAlias.getJoin() == null){
      rootTableAlias = tableAlias ;
    }
  }
  
  public void addField(FocRequestField reqField){
    fieldList.add(reqField);
  }

  public Iterator newFieldIterator(){
    return fieldList.iterator();
  }

  public Iterator newAliasIterator(){
    return aliasMap.values().iterator();
  }
  
  public void fillFocDesc(FocDesc focDesc){
    if(focDesc != null){
      this.focDesc = focDesc;
      Iterator iter = fieldList.iterator();
      while(iter != null && iter.hasNext()){
        FocRequestField reqField = (FocRequestField) iter.next();
        if(reqField != null){
          FField field = reqField.getField();
          FField newField = null;
          try {
            newField = (FField) field.clone();
            newField.setName(reqField.getTableAlias().getAlias()+"."+field.getName());
            newField.setId(reqField.getId());            
          } catch (CloneNotSupportedException e) {
            Globals.logException(e);            
          }
          focDesc.addField(newField);
        }
      }
    }
  }
  
  public void fillRequestDescWithJoinFields(){
    int firstFieldID = 200;
    
    FocRequestField reqFld = new FocRequestField(firstFieldID++, getRootTableAlias(), FField.REF_FIELD_ID);
    addField(reqFld);
    
    Iterator iter = aliasMap.values().iterator();
    while(iter != null && iter.hasNext()){
      TableAlias tableAlias = (TableAlias) iter.next();
      if(tableAlias != null){
        Join join = tableAlias.getJoin();
        if(join != null){
          firstFieldID = join.fillRequestDescWithJoinFields(this, firstFieldID);
        }
      }
    }
  }

  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  public String getLinkCondition(){
    StringBuffer str = new StringBuffer();
    
    boolean firstCondition = true;
    
    Iterator iter = newAliasIterator();
    while(iter != null && iter.hasNext()){
      TableAlias alias =(TableAlias) iter.next();
      if(alias != null){
        Join join = alias.getJoin();
        if(join != null){
          String joinCondition = join.getLinkCondition();
          
          if(joinCondition != null && joinCondition.length() > 0){
            if(!firstCondition){
              str.append(" AND "); 
            }
            str.append("(");
            str.append(joinCondition);
            str.append(")");
            firstCondition = false;
          }
        }
      }
    }

    return str.toString();
  }
}

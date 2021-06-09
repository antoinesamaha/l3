/*
 * Created on Sep 9, 2005
 */
package b01.foc.list.filter;

import java.util.*;

import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FilterDesc {
  private FocDesc subjectFocDesc = null;
  private ArrayList conditionList = null;
  
  public FilterDesc(FocDesc subjectFocDesc){
    this.subjectFocDesc = subjectFocDesc ;
    
    if (subjectFocDesc != null && subjectFocDesc.isRevisionSupportEnabled()){
      RevisionCondition cond = new RevisionCondition(FFieldPath.newFieldPath(FField.CREATION_REVISION_FIELD_ID), "C_R");
      addCondition(cond);
      
    }
  }
  
  private ArrayList getConditionList(boolean create){
    if(conditionList == null && create){
      conditionList = new ArrayList();
    }
    return conditionList;
  }
  
  public void addCondition(FilterCondition cond){
    ArrayList condLst = getConditionList(true);
    condLst.add(cond);
    cond.setFilterDesc(this);
  }

  public int getConditionCount(){
    ArrayList condLst = getConditionList(false);
    return condLst != null ? condLst.size() : 0; 
  }
  
  public FilterCondition getConditionAt(int i){
    ArrayList condLst = getConditionList(false);
    return condLst != null ? (FilterCondition) condLst.get(i) : null;
  }
  
  public FilterCondition findConditionByFieldPrefix(String prefix){
    FilterCondition foundCond = null;
    for(int i=0; i<getConditionCount(); i++){
      FilterCondition cond = getConditionAt(i);
      if(cond.getFieldPrefix().compareTo(prefix) == 0){
        foundCond = cond;
        break;
      }
    }       
    return foundCond;
  }
  
  public void fillProperties(FocObject fatherObject){    
    for(int i=0; i<getConditionCount(); i++){
      FilterCondition cond = getConditionAt(i);
      cond.fillProperties(fatherObject);
    }
  }
  
  public int fillDesc(FocDesc focDesc, int idStart){
    for(int i=0; i<getConditionCount(); i++){
      FilterCondition cond = getConditionAt(i);
      idStart = cond.fillDesc(focDesc, idStart);
    }
    return idStart;
  }
  
  public FocDesc getSubjectFocDesc() {
    return subjectFocDesc;
  }
}

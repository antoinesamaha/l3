/*
 * Created on Sep 9, 2005
 */
package b01.foc.list.filter;

import b01.foc.desc.*;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FPanel;

/**
 * @author 01Barmaja
 */
public abstract class FilterCondition {
  private FilterDesc filterDesc = null;
  private FFieldPath fieldPath = null;
  private String fieldPrefix = null;
  private int firstFieldID = 0;
  private String fieldLabel = null;
  
  public abstract int fillDesc(FocDesc focDesc, int startID);
  public abstract void fillProperties(FocObject focFatherObject);
  public abstract boolean includeObject(FocListFilter filter, FocObject object);
  public abstract StringBuffer buildSQLWhere(FocListFilter filter, String fieldName);
  public abstract GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y);
  public abstract boolean isValueLocked(FocListFilter filter);
  public abstract void resetToDefaultValue(FocListFilter filter);
  
  public FilterCondition(FFieldPath filterFieldPath, String filterFieldPrefix){
    this.fieldPath = filterFieldPath;
    this.fieldPrefix = filterFieldPrefix;
  }
  
  public FFieldPath getFieldPath() {
    return fieldPath;
  }
  
  public String getFieldPrefix() {
    return fieldPrefix;
  }
  
  public int getFirstFieldID() {
    return firstFieldID;
  }
  
  public void setFirstFieldID(int firstFieldID) {
    this.firstFieldID = firstFieldID;
  }
  
  public FilterDesc getFilterDesc() {
    return filterDesc;
  }
  
  public void setFilterDesc(FilterDesc filterDesc) {
    this.filterDesc = filterDesc;
  }
  
  public String getFieldLabel(){
    String str = null;
    if(fieldLabel == null){
      FField field = getFieldPath().getFieldFromDesc(filterDesc.getSubjectFocDesc());
      str = field.getTitle();
    }else{
      str = fieldLabel;
    }
    return str;
  }

  public String getDBFieldName(){
    FField field = getFieldPath().getFieldFromDesc(filterDesc.getSubjectFocDesc());
    return field != null ? field.getName() : null;
  }

  public void setFieldLabel(String fieldLabel) {
    this.fieldLabel = fieldLabel;
  }
}

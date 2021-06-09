/*
 * Created on Jan 9, 2006
 */
package b01.foc.join;

import b01.foc.desc.field.FField;

/**
 * @author 01Barmaja
 */
public class FocRequestField {
  private int id = 0;
  private TableAlias tableAlias = null;
  private int fieldId = FField.NO_FIELD_ID;
  
  public FocRequestField(int id, TableAlias tableAlias, int fieldId){
    this.id = id;
    this.tableAlias = tableAlias;
    this.fieldId = fieldId;
  }

  public int getId() {
    return id;
  }
    
  public TableAlias getTableAlias() {
    return tableAlias;
  }

  public FField getField() {
    return tableAlias.getFocDesc().getFieldByID(fieldId);
  }
}

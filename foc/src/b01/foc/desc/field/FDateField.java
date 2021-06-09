/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import javax.swing.JTextField;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGDateField;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.gui.table.cellControler.TextCellControler;
import b01.foc.list.filter.DateCondition;
import b01.foc.list.filter.FilterCondition;
import b01.foc.property.*;

/**
 * @author Tony
 */
public class FDateField extends FField {

  public FDateField(String name, String title, int id, boolean key) {
    super(name, title, id, key, 0, 0);
  }

  public int getSqlType() {
    return Types.DATE;
  }

  public String getCreationString(String name) {
    return " " + name + " DATE";
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FDate(masterObj, getID(), (java.sql.Date)defaultValue);
  }

  public FProperty newProperty(FocObject masterObj){
    return new FDate(masterObj, getID(), new java.sql.Date(0));
  }

  public Component getGuiComponent(FProperty prop){
    FGDateField dateField = new FGDateField();
    dateField.setColumns(Double.valueOf(10 * Globals.CHAR_SIZE_FACTOR).intValue());
    if(prop != null) dateField.setProperty(prop);
    return dateField;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    return new TextCellControler(guiComp);
  }  
   
  public int getFieldDisplaySize() {
    return 10;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		DateCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new DateCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}
}

/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.awt.Component;
import javax.swing.*;

import java.sql.Types;

import b01.foc.Globals;
import b01.foc.list.filter.FilterCondition;
import b01.foc.list.filter.StringCondition;
import b01.foc.property.*;
import b01.foc.db.DBManager;
import b01.foc.desc.*;
import b01.foc.gui.*;
import b01.foc.gui.table.cellControler.*;

/**
 * @author 01Barmaja
 */
public class FCharField extends FField {

  public static final int NAME_LEN = 20;
  public static final int DESC_LEN = 50;
  
  private boolean capital = false; 
  
  public FCharField(String name, String title, int id, boolean key, int size) {
    super(name, title, id, key, size, 0);
  }

  public static int SqlType() {
    return Types.VARCHAR;
  }

  public int getSqlType() {
    return SqlType();
  }

  public String getCreationString(String name) {
    //rr Begin
    if (Globals.getDBManager().getProvider()== DBManager.PROVIDER_ORACLE){
      return " " + name + " VARCHAR2" + "(" + getSize() + ")";
    }else{
    //rr End
      return " " + name + " VARCHAR" + "(" + getSize() + ")";
    }
  }
  
  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FString(masterObj, getID(), (String)defaultValue);
  }
  
  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, "");
  }
  
  public Component getGuiComponent(FProperty prop){
    FGTextField textField = new FGTextField();
    textField.setColumns(Double.valueOf(this.getSize() * Globals.CHAR_SIZE_FACTOR).intValue());
    textField.setColumnsLimit(this.getSize());
    textField.setCapital(capital);
    if(prop != null) textField.setProperty(prop);
    return textField;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    return new TextCellControler(guiComp);
  }
  
  public boolean isCapital() {
    return capital;
  }
  
  public void setCapital(boolean capital) {
    this.capital = capital;
  } 
  
  public int compareSQLDeclaration(FField field){
    return this.getSize() - field.getSize();
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  
	@Override
	protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		StringCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new StringCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}
  
  /*private boolean displayEmptyValues = true;
  public boolean isDisplayEmptyValues() {
    return displayEmptyValues;
  }
  
  public void setDisplayEmptyValues(boolean displayEmptyValues) {
    this.displayEmptyValues = displayEmptyValues;
  }*/
}

/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.awt.Component;
import java.sql.Types;
import java.text.NumberFormat;

import javax.swing.JTextField;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGNumField;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.gui.table.cellControler.TextCellControler;
import b01.foc.list.filter.FilterCondition;
import b01.foc.property.FProperty;
import b01.foc.property.FInt;

/**
 * @author 01Barmaja
 */
public class FIntField extends FField {
  private NumberFormat format = null;
  private boolean isGroupingUsed = true;
  private boolean displayZeroValues = true;
  
  public FIntField(String name, String title, int id, boolean key, int size) {
    this(name, title, id, key, size, true);
  }
  
  //rr Begin
  public FIntField(String name, String title, int id, boolean key, int size, boolean isGroupingUsed) {
    super(name, title, id, key, size, 0);
    this.isGroupingUsed = isGroupingUsed;
  }
  //rr End
  
  public static int SqlType() {
    return Types.INTEGER;
  }

  public int getSqlType() {
    return SqlType();
  }

  public String getCreationString(String name) {
    //rr Begin
    if(Globals.getDBManager().getProvider()== DBManager.PROVIDER_ORACLE){
      return " " + name + " NUMBER("+ getSize()+")";
    }else{
    //rr End
      return " " + name + " INT" ;//+ "(" + getSize() + ")";
    }
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FInt(masterObj, getID(), (defaultValue != null) ? ((Integer)defaultValue).intValue() : 0);
  }

  public FProperty newProperty(FocObject masterObj){
    return new FInt(masterObj, getID(), 0);
  }

  /*
  public FField duplicate() {
    return new FIntField(getName(), getTitle(), getID(), false, getSize(), 0);
  }  
  */
  
  public int getFieldDisplaySize(){ 
    return 1 + getSize() + getSize() / 3;
  }
  
  //BElias
  public boolean isGroupingUsed(){
  	return this.isGroupingUsed;
  }
  //EElias

  public NumberFormat getFormat(){
    if(format == null){
      format = FGNumField.newNumberFormat(this.getSize(), this.getDecimals(), this.isGroupingUsed);
    }
    return format;
  }
  
  public Component getGuiComponent(FProperty prop){
    NumberFormat format = getFormat();
    FGNumField numField = new FGNumField(format, getFieldDisplaySize());
    if(prop != null) numField.setProperty(prop);
    return numField;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    TextCellControler textCellControler = new TextCellControler(guiComp);
    textCellControler.setFormat(getFormat());
    return textCellControler;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc sourceDesc){
    
  }
  
  //rr Begin
  public boolean isDisplayZeroValues() {
    return displayZeroValues;
  }
  
  public void setDisplayZeroValues(boolean displayZeroValues) {
    this.displayZeroValues = displayZeroValues;
  }
  //rr End
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }
  
}

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
import b01.foc.property.FDouble;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FNumField extends FField {
  private NumberFormat format = null;
  private boolean displayZeroValues = true;
  private boolean groupingUsed = true;
  
  public FNumField(String name, String title, int id, boolean key, int size, int decimals) {
    this(name, title, id, key, size, decimals, true);
  }

  public FNumField(String name, String title, int id, boolean key, int size, int decimals, boolean groupingUsed) {
    super(name, title, id, key, size, decimals);
    this.groupingUsed = groupingUsed;
  }

  public int getDecimals() {
    if(decimals == 0 && Globals.getDBManager().getProvider()== DBManager.PROVIDER_ORACLE){
      size = size+2;
      if(size > 38) size = 38;
      decimals = 2;
    }
    return decimals;
  }
  
  public static int SqlType() {
    return Types.DOUBLE;
  }
  
  public int getSqlType() {
    return SqlType();
  }

  public String getCreationString(String name) {//rr BINARY_
    if (Globals.getDBManager().getProvider()== DBManager.PROVIDER_ORACLE){
      return " " + name + " NUMERIC" + "(" + getSize() + "," + getDecimals() + ")";
    }else{
      return " " + name + " DOUBLE" ;//+ "(" + getSize() + "," + getDecimals() + ")";
    }
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FDouble(masterObj, getID(), defaultValue != null ? ((Double)defaultValue).doubleValue() : 0);
  }

  public FProperty newProperty(FocObject masterObj){
    return new FDouble(masterObj, getID(), 0);
  }
  
  public NumberFormat getFormat(){
    if(format == null){
      format = FGNumField.newNumberFormat(this.getSize(), this.getDecimals(), this.groupingUsed);
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
    JTextField guiCompForEditor = (JTextField) getGuiComponent(prop);
    guiCompForEditor.setHorizontalAlignment(JTextField.RIGHT);
    TextCellControler textCellCont = new TextCellControler(guiCompForEditor);
    textCellCont.setFormat(getFormat());
    return textCellCont;
  }

  public int getFieldDisplaySize(){ 
    int width = 1 + getSize() + getDecimals();
    if(getDecimals() > 0){
      width += 1;
    }
    width += getSize() / 3;
    return width;
  }
  
  public boolean isGroupingUsed(){
  	return this.groupingUsed;
  }

  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }  
  
  public boolean isDisplayZeroValues() {
    return displayZeroValues;
  }
  
  public void setDisplayZeroValues(boolean displayZeroValues) {
    this.displayZeroValues = displayZeroValues;
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }
}

/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.awt.Component;
import java.sql.Types;
import java.text.NumberFormat;

import javax.swing.JTextField;

import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGNumField;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.gui.table.cellControler.TextCellControler;
import b01.foc.list.filter.FilterCondition;
import b01.foc.property.FLong;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FLongField extends FField {
  private NumberFormat format = null;
  
  public FLongField(String name, String title, int id, boolean key, int size) {
    super(name, title, id, key, size, 0);
  }

  public static int SqlType() {
    return Types.BIGINT;
  }

  public int getSqlType() {
    return SqlType();
  }

  public String getCreationString(String name) {
    return " " + name + " LONG" ;//+ "(" + getSize() + ")";
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FLong(masterObj, getID(), (defaultValue != null) ? ((Long)defaultValue).longValue() : 0);
  }

  public FProperty newProperty(FocObject masterObj){
    return new FLong(masterObj, getID(), 0);
  }

  /*
  public FField duplicate() {
    return new FIntField(getName(), getTitle(), getID(), false, getSize(), 0);
  }  
  */
  
  public int getFieldDisplaySize(){ 
    return 1 + getSize() + getSize() / 3;
  }

  public NumberFormat getFormat(){
    if(format == null){
      format = FGNumField.newNumberFormat(this.getSize(), this.getDecimals());
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
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }
}

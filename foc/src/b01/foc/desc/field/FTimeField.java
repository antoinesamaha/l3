/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import b01.foc.*;

import java.awt.Component;
import java.sql.Types;

import javax.swing.JTextField;

import b01.foc.db.DBManager;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGTimeField;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.gui.table.cellControler.TextCellControler;
import b01.foc.property.FProperty;
import b01.foc.property.FTime;

/**
 * @author 01Barmaja
 */
public class FTimeField extends FDateField {

  public FTimeField(String name, String title, int id, boolean key) {
    super(name, title, id, key);
  }

  public int getSqlType() {
  	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
  		return super.getSqlType();
  	}else{
  		return Types.TIME;
  	}
  }

  public String getCreationString(String name) {
  	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
  		return super.getCreationString(name);
  	}else{
  		return " " + name + " TIME";
  	}  	
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
  	return new FTime(masterObj, getID(), (java.sql.Time)defaultValue);
  }
  
  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, new java.sql.Time(0));
  }
  
  /*
  public FField duplicate() {
    return new FTimeField(getName(), getTitle(), getID(), false, 0);
  }*/
  
  public Component getGuiComponent(FProperty prop){
    FGTimeField timeField = new FGTimeField();
    timeField.setColumns(Double.valueOf(10 * Globals.CHAR_SIZE_FACTOR).intValue());
    if(prop != null) timeField.setProperty(prop);
    return timeField;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    return new TextCellControler(guiComp);
  }  
  
  /*
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  */ 
}

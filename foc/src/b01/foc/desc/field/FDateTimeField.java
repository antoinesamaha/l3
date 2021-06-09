/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGDateField;
import b01.foc.gui.FGDateTimeField;
import b01.foc.property.*;

/**
 * @author Tony
 */
public class FDateTimeField extends FDateField {
	private boolean timeRelevant = true;
	
  public FDateTimeField(String name, String title, int id, boolean key) {
    super(name, title, id, key);
  }

  public boolean isTimeRelevant(){
  	return timeRelevant;
  }
  
  public void setTimeRelevant(boolean timeRelevant){
  	this.timeRelevant = timeRelevant;
  }

  public int getSqlType() {
    return Types.TIMESTAMP;
  }

  public String getCreationString(String name) {
    return " " + name + " DATETIME";
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FDateTime(masterObj, getID(), (java.sql.Date)defaultValue);
  }

  public FProperty newProperty(FocObject masterObj){
    return new FDateTime(masterObj, getID(), new java.sql.Date(0));
  }

  public Component getGuiComponent(FProperty prop){
  	FGDateField dateField = null;
  	if(prop != null && ((FDateTime)prop).isTimeRelevant()){
  		dateField = new FGDateTimeField();
  		dateField.setColumns(Double.valueOf(17 * Globals.CHAR_SIZE_FACTOR).intValue());
  	}else{
  		/*dateField = new FGDateField();
  		dateField.setColumns(Double.valueOf(10 * Globals.CHAR_SIZE_FACTOR).intValue());*/
      return super.getGuiComponent(prop);
  	}
    if(prop != null) dateField.setProperty(prop);
    return dateField;
  }
     
  public int getFieldDisplaySize() {
    return 17;
  }
}

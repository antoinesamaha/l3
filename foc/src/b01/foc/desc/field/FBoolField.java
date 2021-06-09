/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.awt.*;

import b01.foc.desc.FocObject;
import b01.foc.gui.*;
import b01.foc.gui.table.cellControler.*;
import b01.foc.list.filter.BooleanCondition;
import b01.foc.list.filter.FilterCondition;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FBoolField extends FIntField {

  public FBoolField(String name, String title, int id, boolean key) {
    super(name, title, id, key, 1);
  }
  
  public String getCreationString(String name) {
    //return " " + name + " INT(1)";
   
 /*   //rr Begin
    if(Globals.getDBManager().getProvider()== Globals.getDBManager().PROVIDER_ORACLE){
      return " " + name + " NUMBER" ;
    }else{
    //rr End
*/  	return " " + name + " INT";
    //}
  }
  
  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FBoolean(masterObj, getID(), (defaultValue != null) ? ((Boolean)defaultValue).booleanValue() : false);
  }

  public FProperty newProperty(FocObject masterObj){
    return newProperty(masterObj, null);
  }
  

/*
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  */
  /*
  public FField duplicate() {
    return new FBoolField(getName(), getTitle(), getID(), false, 0);
  }
  */
  
  public Component getGuiComponent(FProperty prop){
    FGCheckBox box = new FGCheckBox();
    box.setText(getTitle());
    if(prop != null) box.setProperty(prop);
    return box;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    /*
    JTextField guiComp = (JTextField) getGuiComponent(prop);
    return new TextCellEditor(guiComp);
    */
    CheckBoxCellControler checkBoxCellConstroler = new CheckBoxCellControler(); 
    return checkBoxCellConstroler;
  }  
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		BooleanCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new BooleanCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}
}

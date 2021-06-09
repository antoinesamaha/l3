package b01.foc.desc.field;

import java.awt.Component;
import java.sql.Blob;
import java.sql.Types;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.list.filter.FilterCondition;
import b01.foc.property.FBlobProperty;
import b01.foc.property.FProperty;

public class FBlobField extends FField{

  
  public FBlobField(String name, String title, int id, boolean key) {
    super(name, title, id, key, 0, 0);
    setIncludeInDBRequests(false);
  }

  public static int SqlType() {
    return Types.BLOB;
  }

  public int getSqlType() {
    return SqlType();
  }

  public String getCreationString(String name) {
    if (Globals.getDBManager().getProvider()== DBManager.PROVIDER_ORACLE){
      return " " + name + " BLOB";
    }else{
      return " " + name + " BLOB";
    }
  }
 
  public Component getGuiComponent(FProperty prop){
    return null;
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    return null;
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
    return null;
  }
  @Override
  public FProperty newProperty(FocObject masterObj, Object defaultValue) {
    return new FBlobProperty(masterObj, getID(), (Blob)defaultValue);
  }
  @Override
  public FProperty newProperty(FocObject masterObj) {
    return newProperty(masterObj, null);
  }
}

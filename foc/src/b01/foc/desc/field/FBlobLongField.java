package b01.foc.desc.field;

import java.sql.Blob;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.desc.FocObject;
import b01.foc.property.FBlobLongProperty;
import b01.foc.property.FProperty;

public class FBlobLongField extends FBlobField{

  public FBlobLongField(String name, String title, int id, boolean key) {
    super(name, title, id, key);
    setIncludeInDBRequests(false);
  }
  
  public String getCreationString(String name) {
    if (Globals.getDBManager().getProvider()== DBManager.PROVIDER_ORACLE){
      return " " + name + " BLOB";
    }else{
      return " " + name + " LONGBLOB";
    }
  }
  
  @Override
  public FProperty newProperty(FocObject masterObj, Object defaultValue) {
    return new FBlobLongProperty(masterObj, getID(), (Blob)defaultValue);
  }
}

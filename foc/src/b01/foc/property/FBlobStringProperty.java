package b01.foc.property;

import java.sql.Clob;
import java.sql.ResultSet;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.db.SQLFilter;
import b01.foc.db.SQLSelect;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;

public class FBlobStringProperty extends FString {

  private boolean modified = false;
  public FBlobStringProperty(FocObject focObj, int fieldID, String str) {
    super(focObj, fieldID, str);
    setModified(false);
  }
  
  private void requestPropertyValue(){
    FocObject focObj = getFocObject();
    FocDesc focDesc = focObj.getThisFocDesc();
    if( focDesc != null ){
      SQLFilter filter = new SQLFilter(focObj, SQLFilter.FILTER_ON_IDENTIFIER);
      SQLSelect sqlSelect = new SQLSelect(focObj, focObj.getThisFocDesc(), filter);
      sqlSelect.addQueryField(focObj.getReference().getFocField().getID());
      sqlSelect.addQueryField(getFocField().getID());
      if(sqlSelect.execute()){
        focObj.setLoadedFromDB(false);
      }
    }

  }
  
  private boolean duringSelect = false;
  public String getString() {
    if( super.getString() == null || super.getString().equals("") ){
      if( !duringSelect ){
        duringSelect = true;
        requestPropertyValue();
        setModified(false);
        duringSelect = false;
      }
    }
    return super.getString();
  }
  
  public void setString(String str) {
    if( doSetString(str)){
      super.setString(str);
      setModified(true);  
    }
  }
  
  public boolean isModified() {
    return modified;
  }

  public void setModified(boolean modified) {
    this.modified = modified;
  }

  //BAntoineS-HSG-ORACLE-BLOB
  public void getValueFromResultSet(ResultSet resultSet, int i){
    if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
      Clob clob = null;
      String value = "";
      try{
        clob = resultSet.getClob(i);
        if(clob != null){
          value = clob.getSubString(1, (int)clob.length());
        }
      }catch(Exception e){
        Globals.logException(e);
      }
      setSqlString(value);
    }else{
      super.getValueFromResultSet(resultSet, i);
    }
  }
  //EAntoineS-HSG-ORACLE-BLOB
}

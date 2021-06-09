/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import java.sql.*;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.list.*;

/**
 * @author 01Barmaja
 */
public class SQLSelectString extends SQLSelect{
  
  protected StringBuffer request = null;
  
  public SQLSelectString(StringBuffer request) {
    super((FocObject)null, null, null);
    this.request = request;
  }
 
  public boolean buildRequest() {
    return false;
  }
  
  public boolean execute() {
    Statement stmt = Globals.getDBManager().lockStatement();
    ResultSet resultSet = null;
    
    if (stmt != null) {
      try {
        resultSet = stmt.executeQuery(request.toString());
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
      
    //Constructing a FocDesc
    if (resultSet != null) {
      try {
        ResultSetMetaData meta = resultSet.getMetaData();
        FocDesc focDesc = new FocDesc(FocObjectGeneral.class, FocDesc.NOT_DB_RESIDENT, meta.getCatalogName(1), false);
        FocObjectGeneral.setFocDesc(focDesc);
        
        for (int i = 1; i <= meta.getColumnCount(); i++) {
          String colLabel = meta.getColumnLabel(i);
          int colType = meta.getColumnType(i);
          int size = meta.getColumnDisplaySize(i);
          int scale = meta.getScale(i);
          //BAntoineS - AUTOINCREMENT
          boolean autoIncrement = meta.isAutoIncrement(i);
          FField focField = FField.newField(colType, colLabel, i, size, scale, autoIncrement);
          //EAntoineS - AUTOINCREMENT
          focDesc.addField(focField);
        }
        setFocDesc(focDesc);
      }catch(Exception e){
        Globals.logException(e);
      }
    }
        
    
    loadMode = SQLSelect.LOAD_IN_EMPTY_LIST;
    focList = new FocList(new FocLinkSimple(focDesc));

    treatResultSet(resultSet);
    
    Globals.getDBManager().unlockStatement(stmt);
    return false;
  }

}

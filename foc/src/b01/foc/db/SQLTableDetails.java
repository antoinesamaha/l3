/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;

import java.sql.*;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class SQLTableDetails extends SQLRequest {

  ResultSet resultSet = null;
  Hashtable<String, FField> hash = null;

  public SQLTableDetails(FocDesc focDesc) {
    super(focDesc);
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.getDBResident()) {
      request.append("SELECT *");
      addFrom();
      request.append(" WHERE 1=2");
    }
    return false;
  }

  public boolean execute() {
    boolean error = false;
    Statement stmt = Globals.getDBManager().lockStatement();
    hash = new Hashtable<String, FField>();

    if (hash != null) {
      error = buildRequest();
      
      /*if(request.toString().compareTo("SELECT * FROM L3TEST WHERE 1=2") == 0){
        int debug = 4;
      }*/
      
      if(!error){
        try {
          Globals.logString(request.toString());
          resultSet = stmt.executeQuery(request.toString());
        } catch (Exception e) {
          Globals.logException(e);
        }
      }
    }
    
    if(!error){
      if (resultSet != null) {
        try {
          ResultSetMetaData meta = resultSet.getMetaData();
  
          if (meta != null) {
            for (int i = 1; i <= meta.getColumnCount(); i++) {
              String colLabel = meta.getColumnLabel(i);
              int colType = meta.getColumnType(i);
              int size = meta.getColumnDisplaySize(i);
              int scale = meta.getScale(i);
              //BAntoineS-HSG-ORACLE-BLOB
              int precision = (colType != Types.BLOB) ? meta.getPrecision(i) : 0;
              //EAntoineS-HSG-ORACLE-BLOB
              //BAntoineS - AUTOINCREMENT
              boolean autoIncrement = meta.isAutoIncrement(i);
              FField focField = FField.newField(colType, colLabel, 0, precision ==0 ? size : precision, scale, autoIncrement);
              //EAntoineS - AUTOINCREMENT
              hash.put(focField.getName(), focField);
            }
          }
        } catch (Exception e) {
          Globals.logException(e);
        }
        Globals.getDBManager().unlockStatement(stmt);
      }
    }
    return error;
  }

  public Hashtable getFieldsHashtable() {
    return hash;
  }
}

/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import java.sql.*;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.property.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class SQLSelectFields extends SQLRequest {
  private ArrayList<Integer> fields = null;
  private ArrayList lines = null;
  
  public SQLSelectFields(FocDesc focDesc, int fieldID, SQLFilter filter) {
    super(focDesc, filter);
    fields = new ArrayList<Integer>();
    addField(fieldID);
  }
  
  public void addField(int fieldID){
    fields.add(Integer.valueOf(fieldID));
  }

  public boolean buildRequest(String selectCommand) {
    request = new StringBuffer("");
    boolean error = false;

    if (focDesc != null && focDesc.getDBResident()) {
      request.append(selectCommand+" ");

      for(int i=0; i<fields.size(); i++){
        Integer fieldIDInt = (Integer) fields.get(i);
        int fieldID = fieldIDInt.intValue();
        FField field = focDesc.getFieldByID(fieldID);
        
        if (i > 0) {
          request.append(",");
        }
        request.append(field.getDBName());
      }
      
      addFrom();
      error = addWhere();
    }
    return error;
  }

  public boolean buildRequest() {
    return buildRequest("SELECT");
  }

  public boolean execute() {
    boolean error = Globals.getDBManager() == null;     
    if(!error && focDesc != null && focDesc.getDBResident() == FocDesc.DB_RESIDENT){
      Statement stmt = Globals.getDBManager().lockStatement();
  
      ResultSet resultSet = null;
  
      if (stmt != null) {
        error = buildRequest();
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
            lines = new ArrayList();
            
            ResultSetMetaData meta = resultSet.getMetaData();
            meta.getColumnCount();                       

            while (resultSet.next()) {
              //Initialize the property array
              FProperty[] props = new FProperty[fields.size()];
              for(int i=0; i<fields.size(); i++){
                Integer fieldID = (Integer) fields.get(i);
                FField field = focDesc.getFieldByID(fieldID.intValue()); 
                props[i] = field.newProperty(null, null);
              }            
                  
              for(int col=1; col <= meta.getColumnCount(); col++){
                String value = resultSet.getString(col);
                props[col-1].setSqlString(value);
              }
              
              lines.add(props);
            }
            
          } catch (Exception e) {
            Globals.logException(e);
          }
        }
      }
      Globals.getDBManager().unlockStatement(stmt);
    }
    return error;
  }

  public int getLineNumber() {
    return lines != null ? lines.size() : 0;
  }
  
  public int getColumnNumber() {
    return fields != null ? fields.size() : 0;
  }
  
  public FProperty getPropertyAt(int line, int col){
    FProperty prop = null;
    if(lines != null){
      FProperty[] lineAt = (FProperty[]) lines.get(line);
      if(lineAt != null){
        prop = lineAt[col];
      }
    }
    return prop;
  }
}

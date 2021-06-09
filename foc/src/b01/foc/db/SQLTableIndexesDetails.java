/*
 * Created on 26 Avril 2005
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
public class SQLTableIndexesDetails{

  Hashtable indexesContainer = null;

  public SQLTableIndexesDetails(FocDesc focDesc) {
    try{
      indexesContainer = new Hashtable(); 
      
      DatabaseMetaData dbmd = Globals.getDBManager().getConnection().getMetaData();
      ResultSet rs = dbmd.getIndexInfo(null, null, focDesc.getStorageName(), false, false);

      //Getting the idx of the interesting columns
      //------------------------------------------
      int nameIdx = -1;
      int uniqueIdx = -1;
      int columnIdx = -1;
      
      ResultSetMetaData rsMeta = rs.getMetaData();
      for(int i=1; i<=rsMeta.getColumnCount(); i++){
        if(rsMeta.getColumnName(i).compareTo("COLUMN_NAME") == 0){
          columnIdx = i;
        }else if(rsMeta.getColumnName(i).compareTo("INDEX_NAME") == 0){
          nameIdx = i;          
        }else if(rsMeta.getColumnName(i).compareTo("NON_UNIQUE") == 0){
          uniqueIdx = i;          
        }
      }

      //rr Begin
      // Display MetaData 
      /*
      while (rs != null && rs.next()) {
         Object nameValue;
         for(int i=1; i<rsMeta.getColumnCount(); i++){
           System.out.print(nameValue = rsMeta.getColumnLabel(i)+" , ");
         }

        for(int i=1; i<rsMeta.getColumnCount(); i++){
          System.out.print(nameValue = rs.getObject(i)+" , ");
        }
        System.out.println();
      }
      */
      //rr End
      
      //Scan the results and read the interesting columns and fill the hash table
      //-------------------------------------------------------------------------
      while (rs != null && rs.next()) {
        String nameValue = rs.getString(nameIdx);
        boolean uniqueValue = !rs.getBoolean(uniqueIdx);
        String columnValue = rs.getString(columnIdx);

        if (nameValue != null){ //rr
          DBIndex dbIndex = (DBIndex) indexesContainer.get(nameValue);
          if(dbIndex == null){
            dbIndex = new DBIndex(nameValue, focDesc, uniqueValue, false);
            indexesContainer.put(nameValue, dbIndex);
          }
          FField field = focDesc.getFieldByDBCompleteName(columnValue); 
          if(field == null){
            Globals.logString("COl value : "+columnValue+" table:"+focDesc.getStorageName());
            field = focDesc.getFieldByName(columnValue);
          }
          dbIndex.addField(field.getID());
        }
      }
    }catch (Exception e){
      Globals.logException(e);
    }
  }
  
  public DBIndex getIndex(String name){
    return indexesContainer != null ? (DBIndex)indexesContainer.get(name) : null;
  }
  
  public void removeIndex(String name){
    indexesContainer.remove(name);
  }
  
  public Iterator iterator(){
    return indexesContainer.values().iterator();
  }
}

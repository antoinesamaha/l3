/*
 * Created on Feb 10, 2006
 */
package b01.foc.db;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Iterator;

import b01.foc.*;
import b01.foc.db.DBManager;
import b01.foc.db.SQLSelectString;
import b01.foc.desc.FocFieldEnum;
import b01.foc.desc.FocObject;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class DatabaseExtractor {
    
  /**
   * 
   */
  public DatabaseExtractor() {
  }
  
  public void extractMemoryProblems(){
    Application app = Globals.getApp();
    DBManager dbManager = app.getDBManager();
    try{
      if(dbManager != null){
        Hashtable tables = dbManager.getAllRealTables();
        
        Iterator iter = tables.values().iterator();
        while(iter != null && iter.hasNext()){          
          String tableName = (String) iter.next();
          
          if(tableName != null){
            Globals.logString("Table :"+tableName);
            PrintStream logFile = new PrintStream("c:/avisleb_txt/"+tableName+".csv");  
            
            StringBuffer request = new StringBuffer("SELECT * from ");
            request.append(tableName);
            
            Globals.logString(request);
            SQLSelectString sqlSelect = new SQLSelectString(request);
            sqlSelect.execute();
            FocList list = sqlSelect.getFocList();
            Iterator objIter = list.focObjectIterator();
            while(objIter != null && objIter.hasNext()){
              FocObject obj = (FocObject) objIter.next();
              
              FocFieldEnum enumer = new FocFieldEnum(list.getFocDesc(), obj, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
              while(enumer != null && enumer.hasNext()){
                enumer.next();
                FProperty prop = enumer.getProperty();
                logFile.print(prop.getString()+",");
              }
              logFile.println();
            }
            logFile.close();
            logFile = null;
            Globals.logString("End Table :"+tableName);
          }
        }
        
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
      
  public static void runRequestAndPrintFile(StringBuffer request, String fileName){
    try{
      //Making the request
      Statement stmt = Globals.getDBManager().lockStatement();
      ResultSet resultSet = null;
      
      if (stmt != null) {
        resultSet = stmt.executeQuery(request.toString());
      }
    
      ResultSetMetaData meta = resultSet.getMetaData();
      
      //Title line
      if (resultSet != null) {
        PrintStream logFile = null ;
        boolean firstLine = true;
                                 
        while (resultSet.next()) {
          if(firstLine){ 
            logFile = new PrintStream(fileName);
            for (int i = 1; i <= meta.getColumnCount(); i++) {
              String colLabel = meta.getColumnLabel(i);
              logFile.print(colLabel+",");
            }
            logFile.println();
            firstLine = false;
          }
          
          for (int i = 1; i <= meta.getColumnCount(); i++) {
            logFile.print(resultSet.getString(i)+",");
          }
          logFile.println();
        }
    
        if(logFile != null){
          logFile.close();
          logFile = null;
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public void extract_AllTables(){
    Application app = Globals.getApp();
    DBManager dbManager = app.getDBManager();
    try{
      if(dbManager != null){
        Hashtable tables = dbManager.getAllRealTables();
        
        Iterator iter = tables.values().iterator();
        while(iter != null && iter.hasNext()){          
          String tableName = (String) iter.next();
          
          if(tableName != null){  
            StringBuffer request = new StringBuffer("SELECT * from ");
            request.append(tableName);

            runRequestAndPrintFile(request, "c:/temp/dbCopy/"+tableName+".csv");
          }
        }
        
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public void extract_AllTablesNames(){
    Application app = Globals.getApp();
    DBManager dbManager = app.getDBManager();
    try{
      if(dbManager != null){
        Hashtable tables = dbManager.getAllRealTables();
        
        Iterator iter = tables.values().iterator();
        while(iter != null && iter.hasNext()){          
          String tableName = (String) iter.next();

          Globals.logString("Table = "+tableName);
        }
        
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }

  public void extract(){
    Application app = Globals.getApp();
    DBManager dbManager = app.getDBManager();
    try{
      StringBuffer request = new StringBuffer();
      
      request.append("select ");
      request.append("MAKE1.NAME, ");
      request.append("CAR1.LICENSE_NO, ");
      request.append("DEB.DEBITOR_NAME, ");
      request.append("AG.EXF_CHECK_IN_DATE, ");
      request.append("AG.CHECK_IN_DATE, ");
      request.append("AG.AGREEMENT_NO, ");
      request.append("AG_bis.AGREEMENT_NO, ");
          
      request.append("SUB_AG.CHECK_IN_DATE, ");
      request.append("DAMAG.DAMAGE_DATE, ");
      request.append("AS2.GARAGE_IN_DATE, ");
      request.append("DAMAG.DAMAGEMINT_NO, ");
      request.append("DAMAG.AGREE_NO, ");
      //request.append("ITEMS_WO.DAMAGEMINT_NO, ");
      request.append("W.UNIT_NO, ");
      request.append("W.WORK_ORDER_NO, ");
      request.append("W.GARAGE_NO, ");
      //request.append("AS2.AGREEMENT_NO, ");
      request.append("W.APPROVAL ");
      
      request.append("from ");
      request.append("COMB_WO W, ");
      request.append("AGREEMENTS_SECTION2 AS2, ");
      request.append("SUBAGREEMENTS SUB_AG, ");
      request.append("AGREEMENTS AG, ");
      request.append("CAR_TECHNICAL CAR1, ");
      request.append("CAR_MAKES MAKE1, ");
      //request.append("SELECTED_ITEMS_WO ITEMS_WO, ");
      request.append("DAMAGESMINTENANCE DAMAG, ");
      request.append("CONTRACT_HIRE_DETAILS HIRE, ");
      request.append("CONTRACT_HIRE_MASTER HIRE_M, ");
      request.append("DEBITORS DEB, ");
      request.append("AGREEMENTS AG_bis ");
      
      request.append("where ");
      request.append("W.GARAGE_NO = 52 and DAMAG.AGREE_NO <> 0 and ");
      request.append("W.WORK_ORDER_NO = DAMAG.WORK_ORDER_NO and ");
      request.append("DAMAG.AGREE_NO = HIRE.AGREEMENT_NO and ");
      request.append("HIRE.CONTRACT_NUMBER = HIRE_M.CONTRACT_NUMBER and ");
      request.append("HIRE_M.DEBITOR_CODE = DEB.DEBITOR_CODE and ");

      request.append("W.UNIT_NO = CAR1.UNIT_NO and ");
      request.append("CAR1.CAR_MAKE = MAKE1.CODE and ");
      
      request.append("W.WORK_ORDER_NO=AS2.WORK_ORDER_NO and ");
      request.append("AS2.AGREEMENT_NO=SUB_AG.FATHER_AGREEMENT_NO and ");
      request.append("AS2.AGREEMENT_NO=AG.AGREEMENT_NO and ");
      
      request.append("AG_bis.DRIVER_LAST_NAME like '%'+ltrim(rtrim(CAR1.LICENSE_NO))+'%'");
      /*
      request.append("ITEMS_WO.WORK_ORDER_NO=AS2.WORK_ORDER_NO and ");
      request.append("DAMAG.DAMAGEMINT_NO=ITEMS_WO.DAMAGEMINT_NO and ");
      */

      


      Globals.logString(request);
      runRequestAndPrintFile(request, "c:/work/avis/request.csv");
    }catch(Exception e){
      Globals.logException(e);
    }
  }

  

}


/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import java.sql.ResultSet;
import java.sql.Statement;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class SQLInsert extends SQLRequest {
  private FocObject focObj = null;

  public SQLInsert(FocDesc focDesc, FocObject focObj) {
    super(focDesc);
    this.focObj = focObj;
  }

  //BAntoineS - AUTOINCREMENT
  private boolean excludeFieldFromInsert(String fieldName){
  	return fieldName.equals(FField.REF_FIELD_NAME) && Globals.getDBManager().getProvider() == DBManager.PROVIDER_MYSQL;
  }
  //EAntoineS - AUTOINCREMENT
  
  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.getDBResident()) {
      focObj.assignReferenceIfNeeded(true);//Usefull for Oracle only.
      boolean firstField = true;
      request.append("INSERT INTO ");
      request.append(focDesc.getStorageName());
      request.append(" (");
      
      FocFieldEnum enumer = focObj.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
      while (enumer.hasNext()) {
        
        FField focField = (FField) enumer.next();
        //BAntoineS - AUTOINCREMENT
        if (focField != null && !excludeFieldFromInsert(enumer.getFieldCompleteName(focDesc))) {
        //EAntoineS - AUTOINCREMENT
          if (!firstField) {
            request.append(",");
          }
          request.append(enumer.getFieldCompleteName(focDesc));
          firstField = false;
        }
      }
      request.append(") VALUES (");
      firstField = true;

      enumer.reset();
      while (enumer.hasNext()) {
        FField focField = (FField) enumer.next();
        //BAntoineS - AUTOINCREMENT
        if (focField != null && !excludeFieldFromInsert(enumer.getFieldCompleteName(focDesc))) {
        //EAntoineS - AUTOINCREMENT

          int id = focField.getID();
          FProperty prop = enumer.getProperty();          
          
          if(prop == null){
          	Globals.logString("Property null : "+id);
            prop = enumer.getProperty();
          }
                    
          //If the property is reference I need to make sure the ref is not Temp
        	//We are not relying on this assignReferenceIfNeeded because there is a code that calls the commitStatusToDatabaseWithPropagation
        	//in the commitStatusToDatabase of this same father FocObject.
          if(prop.getFocField() != null && prop.getFocField().getID() == FField.REF_FIELD_ID){
            FocObject propFocObj = prop.getFocObject();
            
            if(propFocObj != null){
              propFocObj.assignReferenceIfNeeded(false);
            }
          }
          if (!firstField) {
            request.append(",");
          }

          try {
            request.append(prop.getSqlString());
          } catch (Exception e) {
            if (prop == null) {
              Globals.logString("prop null for " + focDesc.getFocObjectClass() + " id = " + id);
            }
            Globals.logException(e);
          }

          firstField = false;
        }
      }
      request.append(")");
    }
    return false;
  }
  
  //BAntoineS - AUTOINCREMENT
  protected void executeUpdate(Statement stmt, String req) throws Exception{
  	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_MYSQL){
	  	stmt.executeUpdate(req, Statement.RETURN_GENERATED_KEYS);
	  	ResultSet rs = stmt.getGeneratedKeys();
	  	while(rs.next()){
	  		int ref = rs.getInt(1);
	  		focObj.setReference(ref);
	  	}
  	}else if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
  		stmt.executeUpdate(req);
  	}
  }
  //EAntoineS - AUTOINCREMENT
}
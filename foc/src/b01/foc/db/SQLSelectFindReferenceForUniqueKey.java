/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import b01.foc.Globals;
import b01.foc.desc.*;
import b01.foc.desc.field.FField;

/**
 * @author 01Barmaja
 */
public class SQLSelectFindReferenceForUniqueKey extends SQLSelect {
   
  public SQLSelectFindReferenceForUniqueKey(FocObject obj) {
    super(obj, obj.getThisFocDesc(), null);
    filter = new SQLFilter(obj, SQLFilter.FILTER_ON_KEY);
  }
 
  public void dispose(){
  	if(filter != null){
	  	filter.dispose();
	  	filter = null;
  	}
  	super.dispose();
  }
  
  public boolean buildRequest() {
  	boolean error = getFocDesc().getFieldByID(FField.REF_FIELD_ID) == null;
 		error = error || !getFocDesc().isKeyUnique();
  	if(!error){
    	request = new StringBuffer("SELECT ");
    	request.append(FField.REF_FIELD_NAME);
   		request.append(" FROM ");
    	request.append(getFocDesc().getStorageName());
    	addWhere();
  	}else{
  		Globals.logString("TABLE : "+getFocDesc().getStorageName());
  		if(getFocDesc().getFieldByID(FField.REF_FIELD_ID) == null){
  			Globals.logString("Ref field is null");
  		}else{
  			Globals.logString("Not unique key");
  		}
  	}
  	return error;
  }
  
  public boolean execute() {
    Statement stmt = Globals.getDBManager().lockStatement();
    ResultSet resultSet = null;
    
    if (stmt != null) {
      try {
      	buildRequest();
      	String req = getRequestAdaptedToProvider();
      	Globals.logString(req);
        resultSet = stmt.executeQuery(req);
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
      
    try {
			if (resultSet != null && resultSet.next()) {
				int reference = resultSet.getInt(1);
				getFocObjectToBeFilled().setReference(reference);
				getFocObjectToBeFilled().setCreated(false);
			}
		} catch (SQLException e) {
			Globals.logException(e);
		}
    
    Globals.getDBManager().unlockStatement(stmt);
    return false;
  }
}

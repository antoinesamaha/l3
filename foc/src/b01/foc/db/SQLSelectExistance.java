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
public class SQLSelectExistance extends SQLSelect {
  
	private int exist = EXIST_UNDETERMINED; 
  private StringBuffer sqlWhere = null;
  
  public static int EXIST_YES          = 1;
  public static int EXIST_NO           = 2;
  public static int EXIST_UNDETERMINED = 0;
  
  public SQLSelectExistance(FocDesc desc, StringBuffer sqlWhere) {
    super((FocObject)null, desc, null);
    this.sqlWhere = sqlWhere;
    exist = EXIST_UNDETERMINED;
  }
 
  public boolean buildRequest() {
  	request = new StringBuffer("SELECT ");
//    if (getFocDesc().getFieldByID(FField.REF_FIELD_ID) != null) {
//    	request.append(FField.REF_FIELD_NAME);
//    }else{
    	request.append("count(*)");
//    }
 		request.append(" FROM ");
  	request.append(getFocDesc().getStorageName());
  	request.append(" WHERE "+sqlWhere);
    return false;
  }
  
  public boolean execute() {
    Statement stmt = Globals.getDBManager().lockStatement();
    ResultSet resultSet = null;
    
    if (stmt != null) {
      try {
      	buildRequest();
      	String req = request.toString();
      	Globals.logString(req);
        resultSet = stmt.executeQuery(req);
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
      
    try {
			if (resultSet != null && resultSet.next()) {
				if(resultSet.getInt(1) == 0){
					exist = EXIST_NO;
				}else{
					exist = EXIST_YES;
				}
			}else{
				exist = EXIST_UNDETERMINED;
			}
		} catch (SQLException e) {
			exist = EXIST_UNDETERMINED;
			Globals.logException(e);
		}
    
    Globals.getDBManager().unlockStatement(stmt);
    return false;
  }
  
  public int getExist(){
  	return exist;
  }
}

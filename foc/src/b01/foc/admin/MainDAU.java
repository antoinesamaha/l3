/*
 * Created on Sep 26, 2005
 */
package b01.foc.admin;

import java.sql.SQLException;
import java.sql.Statement;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.db.*;

/**
 * @author 01Barmaja
 */
public class MainDAU {
  
  private static class DeleteAdminUser extends SQLRequest{

    /**
     * @param focDesc
     */
    public DeleteAdminUser() {
      super(null);
    }

    public boolean buildRequest() {
      request = new StringBuffer("");
      
      int i;
      boolean firstField = true;
      request.append("DELETE FROM ");
      request.append(FocUser.DB_TABLE_NAME);
      request.append(" WHERE "+FocUser.FLDNAME_NAME+"=\""+AdminModule.ADMIN_USER+"\"");
      return false ;
    }
    
    public boolean execute(){
      boolean error = false;
      Statement stmt = Globals.getDBManager().lockStatement();
      if (stmt != null) {
        error = buildRequest();
        try {
          String req = request.toString();
          Globals.logString(req);
          stmt.executeUpdate(req);
        } catch (Exception e) {
          SQLException sqlE = (SQLException) e;  
          Globals.logString(sqlE.getMessage());
          error = true;
          Globals.logException(e);
        }
        Globals.getDBManager().unlockStatement(stmt);      
      }

      return error;
    }
  }
  
  public static void main(String args[]){
    Application app = Globals.newApplication(true, true, true);
    app.initDBManager();
    DeleteAdminUser request = new DeleteAdminUser();
    request.execute();
    app.exit();
  }
}

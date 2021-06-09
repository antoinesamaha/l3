/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public class SQLDropIndex extends SQLRequest {
  DBIndex index = null;
  
  public SQLDropIndex(DBIndex index) {
    super(index.getFocDesc());
    this.index = index;
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (index != null && index.getFieldCount() > 0 && focDesc != null && focDesc.getDBResident()) {
      request.append("DROP INDEX ");
      request.append(index.getName());
      if(Globals.getDBManager().getProvider() != DBManager.PROVIDER_ORACLE){
        request.append(" ON ");
        request.append(focDesc.getStorageName());
      }
    }
    return false;
  }
}

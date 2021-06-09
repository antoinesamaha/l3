/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class SQLDelete extends SQLRequest {

  public SQLDelete(FocDesc focDesc, SQLFilter filter) {
    super(focDesc, filter);
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.getDBResident()) {
      request.append("DELETE ");
      addFrom(false);
      addWhere(false);
    }
    return false;
  }  
}

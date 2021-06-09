/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class SQLSelectDistinct extends SQLSelectFields {
  
  public SQLSelectDistinct(FocDesc focDesc, int fieldID, SQLFilter filter) {
    super(focDesc, fieldID, filter);
  }
  
  public boolean buildRequest() {
    return buildRequest("SELECT DISTINCT");
  }
}

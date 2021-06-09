/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class SQLCreateIndex extends SQLRequest {
  DBIndex index = null;
  
  public SQLCreateIndex(DBIndex index) {
    super(index.getFocDesc());
    this.index = index;
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (index != null && index.getFieldCount() > 0 && focDesc != null && focDesc.getDBResident()) {
      request.append("CREATE ");
      if(index.isUnique()){
        request.append("UNIQUE ");
      }
      request.append("INDEX ");      
      request.append(index.getName());
      
      request.append(" ON ");
      request.append(focDesc.getStorageName());
            
      boolean firstField = true;
      request.append("(");      
      for(int i=0; i<index.getFieldCount(); i++){
        int fieldID = index.getFieldAt(i);
        FField field = focDesc.getFieldByID(fieldID);
        if(field != null){
          if (!firstField) request.append(",");
          request.append(field.getDBName());
          firstField = false;
        }
        
      }
      request.append(")");
    }
    return false;
  }
}

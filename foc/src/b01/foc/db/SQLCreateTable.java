/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.Globals;
import b01.foc.desc.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class SQLCreateTable extends SQLRequest {

  public SQLCreateTable(FocDesc focDesc) {
    super(focDesc);
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.getDBResident()) {
      request.append("CREATE TABLE ");
      request.append(focDesc.getStorageName());
      request.append("(");

      boolean firstField = true;
    	//BAntoineS - AUTOINCREMENT
      boolean addPrimaryKey = false;
    	//EAntoineS - AUTOINCREMENT

      FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
      while (enumer.hasNext()) {
        FField focField = (FField) enumer.next();
        if (focField != null) {
          if (!firstField) request.append(",");
          request.append(focField.getCreationString(enumer.getFieldCompleteName(focDesc)));
          if (Globals.getDBManager().getProvider() != DBManager.PROVIDER_ORACLE){
            request.append(" NOT NULL ");
          }
        	//BAntoineS - AUTOINCREMENT
          if(enumer.getFieldCompleteName(focDesc).equals(FField.REF_FIELD_NAME) && Globals.getDBManager().getProvider()== DBManager.PROVIDER_MYSQL){
            request.append("AUTO_INCREMENT ");
            addPrimaryKey = true;
          }
          //EAntoineS - AUTOINCREMENT
          firstField = false;
        }
      }
      //BAntoineS - AUTOINCREMENT
      if(addPrimaryKey) request.append(", UNIQUE INDEX "+FocDesc.INDEX_IDENTIFIER+" ("+FField.REF_FIELD_NAME+")");
      //BAntoineS - AUTOINCREMENT
      request.append(")");
    }
    return false;
  }
}

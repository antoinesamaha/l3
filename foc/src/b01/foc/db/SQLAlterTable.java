/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.desc.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class SQLAlterTable extends SQLRequest {

  final public static int ADD = 1;
  final public static int MODIFY = 2;
  final public static int DROP = 3;

  int id;
  int action;
  String fieldNameToDrop = "";
  String fieldToAlterName = "";
  FField fieldToAlter = null;

  public SQLAlterTable(FocDesc focDesc, FField fieldToAlter, String fieldToAlterName, int action) {
    super(focDesc);
    this.id = fieldToAlter != null ? fieldToAlter.getID() : 0;
    this.action = action;
    this.fieldToAlter = fieldToAlter;
    this.fieldToAlterName = fieldToAlterName; 
  }

  public SQLAlterTable(FocDesc focDesc, String fieldNameToDrop) {
    super(focDesc);
    this.fieldNameToDrop = fieldNameToDrop;
    this.action = DROP;
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.getDBResident()) {           
      request.append("ALTER TABLE ");
      request.append(focDesc.getStorageName());
      request.append(" ");

      switch (action) {
      case ADD:
        request.append(" ADD ");
        break;
      case MODIFY:
        request.append(" MODIFY ");
        break;
      case DROP:
        request.append(" DROP COLUMN ");
        request.append(fieldNameToDrop);
        break;
      }

      if (action != DROP) {
        if (fieldToAlter != null) {
          request.append(fieldToAlter.getCreationString(fieldToAlterName));
          if (Globals.getDBManager().getProvider()!= DBManager.PROVIDER_ORACLE){
            request.append(" NOT NULL ");
          }
        	//BAntoineS - AUTOINCREMENT
          if(fieldToAlterName.equals(FField.REF_FIELD_NAME) && Globals.getDBManager().getProvider()== DBManager.PROVIDER_MYSQL){
            request.append("AUTO_INCREMENT ");
            //Check if unique index exists otherwise create it in this same request
            boolean identifierIndexExists = false;
            SQLTableIndexesDetails indexesDetails = new SQLTableIndexesDetails(focDesc);
            Iterator iter = indexesDetails.iterator();
            while(iter != null && iter.hasNext() && !identifierIndexExists){
              DBIndex index = (DBIndex) iter.next();
             	identifierIndexExists = index != null && index.getName().equals(FocDesc.INDEX_IDENTIFIER);   	
            }
            if(!identifierIndexExists){
            	request.append(", ADD UNIQUE INDEX "+FocDesc.INDEX_IDENTIFIER+" ("+FField.REF_FIELD_NAME+")");
            }
          }
          //EAntoineS - AUTOINCREMENT
        }
      }
    }
    return false;
  }
}

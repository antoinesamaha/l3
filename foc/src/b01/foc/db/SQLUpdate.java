/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class SQLUpdate extends SQLRequest {
  private FocObject focObj = null;

  public SQLUpdate(FocDesc focDesc, FocObject focObj) {
    super(focDesc);
    this.focObj = focObj;
    filter = new SQLFilter(focObj, SQLFilter.FILTER_ON_IDENTIFIER);
  }
  
  public SQLUpdate(FocDesc focDesc, FocObject focObj, SQLFilter filter){
  	super(focDesc, filter);
  	this.focObj = focObj;
  }

  public boolean buildRequest() {
    boolean error = false;
    request = new StringBuffer("");

    if (focDesc != null && focDesc.getDBResident()) {
      boolean firstField = true;
      request.append("UPDATE ");
      request.append(focDesc.getStorageName());
      
      FocFieldEnum enumer = focObj.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
      while (enumer.hasNext()) {   
        FField focField = (FField) enumer.next();
        FFieldPath path = enumer.getFieldPath();
        if(isFieldInQuery(path)){
          FProperty prop = enumer.getProperty();                
             
          if(prop != null){ 
            //If the property is reference I need to make sure the ref is not Temp
          	//We are not relying on this assignReferenceIfNeeded because there is a code that calls the commitStatusToDatabaseWithPropagation
          	//in the commitStatusToDatabase of this same father FocObject.
            if(prop.getFocField() != null && prop.getFocField().getID() == FField.REF_FIELD_ID){
              FocObject propFocObj = prop.getFocObject();
              propFocObj.assignReferenceIfNeeded(false);
            }
            
            if (focField != null) {
              if (!firstField) {
                request.append(",");
              } else {
                request.append(" SET ");
              }
              request.append(enumer.getFieldCompleteName(focDesc) + "=");
              request.append(prop.getSqlString());
              firstField = false;
            }
          }
        }
      }

      error = addWhere();
    }
    return error;
  }
}

/*
 * Created on Oct 14, 2004
 */
package b01.foc.db;

import b01.foc.Globals;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class NumericalReference extends FocObject {
  private static FocDesc focDesc = null;
  final public static int FLD_TABLE = 1;
  final public static int FLD_CURRENT_REF = 2;
  
  private FString tableName ;
  private FInt tableRef ;

  public void init() {
    tableRef = new FInt(this, FLD_CURRENT_REF, 0);
  }

  public NumericalReference(FocConstructor constr){
    super(constr);
    init();
  }
  
  public FProperty initIdentifierProperty(Object identifierValue) {
    if(tableName == null){
      String str = (String) identifierValue;
      if(str == null){
        str = "";
      }
      tableName = new FString(this, FLD_TABLE, str);
    }
    return tableName ;
  }

  public synchronized int getNewReference() {
    //Begin transaction
    //BElisas
    Globals.getDBManager().beginTransaction();
    //EElisas
    load();
    int currRef = tableRef.getInteger(); 
    if (currRef <= 0) {
      currRef = 1;
      tableRef.setInteger(currRef);
      setCreated(true);
    } else {
      currRef++;
      tableRef.setInteger(currRef);
      setModified(true);
    }
    validate(false);
    //BElisas
    Globals.getDBManager().commitTransaction();
    //EElisas
    //End transaction    
    return tableRef.getInteger();
  }

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(NumericalReference.class, FocDesc.DB_RESIDENT, "NUM_REF", false);//rr it was true

      focFld = new FCharField("TABLE_NAME", "Table name", FLD_TABLE, false, 30);//rr it was true
      focDesc.addField(focFld);
      focDesc.setIdentifierField(focFld);

      focFld = new FIntField("CURR_REF", "Current reference", FLD_CURRENT_REF, false, 10);
      focDesc.addField(focFld);
    }
    return focDesc;
  }

  public FPanel newDetailsPanel(int viewID) {
    return null;
  }
}
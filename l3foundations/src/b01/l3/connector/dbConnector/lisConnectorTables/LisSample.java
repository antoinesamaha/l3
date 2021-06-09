package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class LisSample extends FocObject{

  public LisSample(FocConstructor constr) {
    super(constr);
    newFocProperties();
    setPropertyDate(LisSampleDesc.FLD_CURRENT_DATE_TIME, Globals.getApp().getSystemDate());
  }
}

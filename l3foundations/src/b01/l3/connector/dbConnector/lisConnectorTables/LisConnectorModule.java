package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.desc.FocModuleInterface;

public class LisConnectorModule implements FocModuleInterface {

  public LisConnectorModule(){
  }

  public void declareFocObjects() {
    Application app = Globals.getApp();
    app.declaredObjectList_DeclareDescription(LisSampleDesc.class);
    app.declaredObjectList_DeclareDescription(LisTestDesc.class);
  }

}
package b01.l3.unit;

import b01.foc.fUnit.FocTestCase;
import b01.foc.fUnit.FocTestSuite;
import b01.foc.gui.FGButton;
import b01.foc.gui.FListPanel;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.connector.LisConnector;

public class L3TestCase extends FocTestCase {
	String connectorEmulators = null;
	String connectorAnalizers = null;

  public L3TestCase(FocTestSuite testSuite, String functionName, String connectorAnalizers, String connectorEmulators){
    super(testSuite, functionName);
  	this.connectorEmulators = connectorEmulators;
  	this.connectorAnalizers = connectorAnalizers;
  }
	
	public void connector_Edit(String connectorName){ 
	  menu_Click("LIS Connector");
	  setDefaultFocDesc(LisConnector.getFocDesc());
	  table_FindAndSelectRow(connectorName);
	  button_Click(FListPanel.BUTTON_EDIT);
	  sleep(1);
  }

	public void connector_Instrument_SetConnect(String instrumentName, boolean connect){ 
    setDefaultFocDesc(Instrument.getFocDesc());
    table_SetValue(table_FindAndSelectRow(instrumentName), InstrumentDesc.FLD_CONNECTED, connect);
  }
	
	public void connector_ClickConnect(){ 
    FGButton button = (FGButton)getChildNamed("LisConnector", false);
    button.doClick();
	}
	
	public void connector_ConnectConnectorDriverAndEmulator(String instrumentName, String emulatorName){
  	connector_Edit(connectorEmulators);
  	connector_Instrument_SetConnect(emulatorName, true);

  	connector_Edit(connectorAnalizers);
  	//connector_ClickConnect();
  	connector_Instrument_SetConnect(instrumentName, true);
	}
	
	public void connector_DisconnectConnectorDriverAndEmulator(String instrumentName, String emulatorName){
		connector_Edit(connectorAnalizers);
  	connector_Instrument_SetConnect(instrumentName, false);
  	//connector_ClickConnect(); 
    sleep(1);
    
    //button_ClickValidate("");
    //sleep(1);

    connector_Edit(connectorEmulators);
  	connector_Instrument_SetConnect(emulatorName, false);
    //button_ClickValidate("");
    //sleep(1);
    
    //button_ClickValidate("");
	}
}

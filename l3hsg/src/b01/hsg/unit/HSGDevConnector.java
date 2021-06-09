package b01.hsg.unit;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.fUnit.FocTestSuite;
import b01.foc.list.FocList;
import b01.foc.property.FDate;
import b01.hsg.unit.HSGUnitGlobals;
import b01.l3.Instrument;
import b01.l3.PoolKernel;
import b01.l3.connector.LisConnector;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.coulter.CoulterEmulator;
import b01.l3.emulator.EmulatorRobot;
import b01.l3.unit.L3TestCase;

public class HSGDevConnector extends L3TestCase implements HSGUnitGlobals{
  
	private static int testTableSequence = 1;
	
  public HSGDevConnector(FocTestSuite testSuite, String functionName){
    super(testSuite, functionName, CONNECTOR_ANALYSERS, CONNECTOR_EMULATORS);
  }

  public void test_CONNECTOR_Connect(){
	  LisConnector connector = LisConnector.getConnector("Connector");
	  if(connector != null){
		  connector.switchOn();
	  }
	  
	  try {
		Thread.sleep(120000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  public void test_CONNECTOR_Disconnect(){
	  LisConnector connector = LisConnector.getConnector("Connector");
	  if(connector != null){
		  connector.switchOff();
	  }
  }

  
}

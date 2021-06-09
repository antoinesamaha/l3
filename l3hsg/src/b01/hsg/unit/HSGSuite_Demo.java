package b01.hsg.unit;

import b01.foc.fUnit.FocTestCase;
import b01.foc.fUnit.FocTestSuite;
import junit.framework.Test;

public class HSGSuite_Demo extends FocTestSuite {

  public static void addTests(FocTestSuite suite){
    suite.addTest(new FocTestCase(suite, "testLogin", "CONNECT", b01.Main.class));
    //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_SelectSomeData"));
 //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_ResetLISTablesAndLocks"));
    //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_Connect"));
    //suite.addTest(new HSGDevComplete(suite, "test_DRIVER_InsertOneSample"));
    //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_InsertSomeLines"));
    
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_CoulterSmallTest"));
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_HelenaJunior24"));
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_Modular"));
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_Coulter"));
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_BCT"));
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_Elecsys"));
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_Dispatcher"));
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_COBASE411"));
    //suite.addTest(new HSGDevDriver(suite, "test_DRIVER_CobasC311"));
    
    suite.addTest(new HSGDevDriver_Pentra(suite, "test_DRIVER_Pentra120"));
    //suite.addTest(new HSGDevDriver_Architect(suite, "test_DRIVER_Architect"));

//Test the connector
//    suite.addTest(new HSGDevConnector(suite, "test_CONNECTOR_Connect"));
//    suite.addTest(new HSGDevConnector(suite, "test_CONNECTOR_Disconnect"));
    
    //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_InsertSomeLines2"));
    //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_InsertSample2Times"));
    //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_Disconnect"));
    suite.addTest(new FocTestCase(suite, "testExit"));
  }

  public static Test suite() {
    HSGSuite_Demo suite = new HSGSuite_Demo();
    suite.setName("HSG Complete Suite");
    
    HSGSuite_Demo.addTests(suite);
    return suite;    
  }
}

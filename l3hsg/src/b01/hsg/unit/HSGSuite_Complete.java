package b01.hsg.unit;

import b01.foc.fUnit.FocTestCase;
import b01.foc.fUnit.FocTestSuite;
import junit.framework.Test;

public class HSGSuite_Complete extends FocTestSuite {

  public static void addTests(FocTestSuite suite){
    suite.addTest(new FocTestCase(suite, "testLogin", "CONNECT", b01.Main.class));
    suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_ResetLISTablesAndLocks"));
    suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_Connect"));
    suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_InsertSampleThenOneTestForThatSample1"));
    //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_SendNewResults()"));
    //suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_InsertSomeLines"));
    suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_Disconnect"));
    suite.addTest(new HSGDevComplete(suite, "test_COMPLETE_InsertSampleThenOneTestForThatSample2"));

    suite.addTest(new FocTestCase(suite, "testExit"));
  }

  public static Test suite() {
    HSGSuite_Complete suite = new HSGSuite_Complete();
    suite.setName("HSG Complete Suite");
    
    HSGSuite_Complete.addTests(suite);
    return suite;    
  }
}

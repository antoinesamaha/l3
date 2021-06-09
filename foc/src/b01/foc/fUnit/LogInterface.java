package b01.foc.fUnit;

import junit.framework.TestResult;

public interface LogInterface {

  public void logSuiteName(String suiteName);
  
  public void logBeginTest(String testName);
	public void logEndTest(String testName);
	
  public void logBeginStep(String message);
  public void logEndStep();
  
  public void logBeginGuiAction(String comp, String compName, String action);
  public void logEndGuiAction();
  
  public void logBeginAssert(String message);
  public void logEndAssert();
  public void logStatus(boolean success);

  public void setTestResult(TestResult testResult);
  public void logTestResult(int testCount);
  
  public void dispose();
}

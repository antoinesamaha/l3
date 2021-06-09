package b01.foc.fUnit;

import junit.framework.*;

public class FocTestSuite extends TestSuite {

  private LogInterface logFile = null;
  
  public FocTestSuite(Class<? extends TestCase> cls) {
    super(cls);
  }

  public FocTestSuite() {
  }

  public void run(TestResult result) {
    start(result);
    super.run(result);
    end();
  }

  public LogInterface getLogFile() {
    return logFile;
  }

  public void initializeLogFile() {
    if (logFile == null && getName()!= null) {
      //logFile = new LogFile(getName());
      logFile = new LogXMLFile(getName());
    }
  }

  public void logTestResult(){
    logFile.logTestResult(testCount());
  }
  
  public void start(TestResult result) {
    initializeLogFile();
    logFile.setTestResult(result);
  }
  
  public void end() {
    logTestResult();
    disposeLogFileObject();
  }
  
  public void disposeLogFileObject() {
    if(getName()!= null){
      logFile.dispose();
//      try {
//        Runtime.getRuntime().exec("notepad "+getName()+".txt");
//      } catch (IOException e) {
//        e.printStackTrace();
//      }  
    }
  }
}

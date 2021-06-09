/*
 * Created on 20 fvr. 2004
 */
package b01.foc;

import b01.foc.db.*;
import b01.foc.desc.*;
import b01.foc.gui.*;
import b01.foc.access.*;

import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

/**
 * @author 01Barmaja
 */
public class Globals{
  public static FocObject debugObj = null;
  
  private static Application application = null;

  //private static int CHAR_WIDTH = 8;
  //private static int CHAR_HEIGHT = 12;
  public static double CHAR_SIZE_FACTOR = 0.7;

  public static final int MNEMONIC_VALIDATION_S = KeyEvent.VK_S;  
  public static final int MNEMONIC_VALIDATION_O = KeyEvent.VK_O;  
  public static final int MNEMONIC_CANCELATION_C = KeyEvent.VK_C;  
    
  public static final long DAY_TIME = (24 * 60 * 60 * 1000);
  
  public static final String ARG_LOG_FILE_PREFIX = "logFilePrefix";
  
  /*
  public static void setCharDimensions(int width, int height){
    CHAR_WIDTH = width;
    CHAR_HEIGHT = height;      
  }
  */
  
  public static Application newApplication(boolean withDB, boolean withAdmin, boolean mdi){
    return newApplication(withDB, withAdmin, mdi, null, null);
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, boolean mdi, String[] args){
    return newApplication(withDB, withAdmin, mdi, args, null);
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, boolean mdi, String[] args, String appName){
    return newApplication(withDB, withAdmin, mdi, args, appName, 0); 
  }
 
  public static Application newApplication(boolean withDB, boolean withAdmin, boolean mdi, String[] args, String appName, int trialPeriod){
  	return newApplication(withDB, withAdmin, mdi ? DisplayManager.GUI_NAVIGATOR_MDI : DisplayManager.GUI_NAVIGATOR_MONOFRAME, args, appName, trialPeriod);
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, int navigatorType, String[] args, String appName, int trialPeriod){
  	Application.initArgs(args);
    application = new Application(withDB, withAdmin, navigatorType, appName, trialPeriod);
    application.init(args);
    return application;
  }

  public static Application newApplication(boolean withDB, boolean withAdmin, int navigatorType, String[] args){
  	Application.initArgs(args);
    application = new Application(withDB, withAdmin, navigatorType, null, 0);
    application.init(args);
    return application;
  }

  public static Application getApp() {
    return application;
  }

  public static AccessControl getDefaultAccessControl() {
    return (application != null) ? application.getDefaultAccessControl() : null;
  }

/*  public static InputStream getInputStream( String path, boolean fromJar ){
    InputStream in = null;
    try{
      if( fromJar){
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        in = url.openStream();
      }else{
        File file = new File(path);
        if( file.exists() ){
          in = new FileInputStream(path);  
        }
      }
    }catch(Exception e){
      logException(e);
    }
    return in;
  }*/
  
  public static InputStream getInputStream( String path ){
    InputStream in = null;
    try{
      File file = new File(path);
      if( file.exists() ){
        in = new FileInputStream(path);  
      }
      if( in == null ){
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if( url != null ){
          in = url.openStream();  
        }
      }
    }catch(Exception e){
      logException(e);
    }
    return in;
    
  }
  
  public static DBManager getDBManager() {
    Application app = getApp();
    return app != null ? app.getDBManager() : null;
  }

  public static DisplayManager getDisplayManager() {
    Application app = getApp();
    return app != null ? app.getDisplayManager() : null;
  }

  private static PrintStream getLogFile() {
    Application app = getApp();
    return app != null ? app.getLogFile() : null;
  }
  
  public static void logExceptionWithoutPopup(Exception e) {
  	if(ConfigInfo.isLogConsoleActive()){
  		e.printStackTrace();
  	}
    PrintStream logFile = getLogFile();
    if(logFile != null){
      e.printStackTrace(logFile);
    }
    String mess = e.getMessage();
    if(mess == null || mess.compareTo("") == 0){
      mess = new String("Exception occured");
    }
    
    if(getApp().isUnitTesting()){
      getApp().exit(true);
      //getApp().getFocTestSuite().exceptionOccured(e);
    }
  }
  
  public static void logException(Exception e) {
    logExceptionWithoutPopup(e);
    if(Globals.getDisplayManager() != null && ConfigInfo.isPopupExceptionDialog() && !getApp().isUnitTesting()){
      JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(), e.getClass().getName()+": "+e.getMessage(),
          "01Barmaja",
          JOptionPane.ERROR_MESSAGE,
          null);
    }
  }

  private static SimpleDateFormat dateFormat = null;
  
  public static SimpleDateFormat getLogFileTimeFormat(){
  	if(dateFormat == null){
  		dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
  	}
  	return dateFormat;
  }
  
  public static synchronized void logString(String str) {
  	if(ConfigInfo.isLogFileWithTime()){
  		str = getLogFileTimeFormat().format(new Date(System.currentTimeMillis())) + " : "+ str;
  	}
    PrintStream logFile = getLogFile();
    if(logFile != null){ 
      logFile.println(str);
      logFile.flush();
    }
    if(ConfigInfo.isLogConsoleActive()){
      System.out.println(str);
      System.out.flush();
    }
  }

  public static void logString(StringBuffer str) {
    Globals.logString(str.toString());
  }
  
  public static void logDetail(String str){
  	if(ConfigInfo.isLogDetails()){
  		logString(str);
  	}
  }

  public static void logDebug(String str){
  	if(ConfigInfo.isLogDebug()){
  		logString(str);
  	}
  }

  public static String logMemory(String str) {
    long total = Runtime.getRuntime().totalMemory();
    long free = Runtime.getRuntime().freeMemory();
    long used = total - free;
    
    NumberFormat _numFmt = NumberFormat.getNumberInstance();
    _numFmt.format(total);
    
    str = str+"Used = "+ _numFmt.format(total) + " - " + _numFmt.format(free) + " = "+_numFmt.format(used); 
    logString(str);
    return str;
  }

  public static FocIcons getIcons() {
    Application app = getApp();
    FocIcons focIcons = app.getFocIcons();
    if (focIcons == null) {
      focIcons = new FocIcons();
    }
    return focIcons;
  }
  
  /**
   * @param application The application to set.
   */
  public static void setApp(Application application) {
    Globals.application = application;
  }
  
  public static void printDebugObj(String prefix){
    if(debugObj != null){
      if(prefix != null){
        Globals.logString(prefix+debugObj.getDebugInfo());
      }else{
        Globals.logString(debugObj.getDebugInfo());
      }
    }
  }
  
  public static boolean logFile_CheckLogDir(){
  	boolean error = true;
  	String archDir = ConfigInfo.getLogDir();
  	if(archDir != null && archDir.compareTo("") != 0){
  		File file = new File(archDir);
  		error = !file.exists() || !file.isDirectory();
  	}
  	return error;
  }
  
  public static String logFile_GetFileName(String suffix, String extension){
  	long time = System.currentTimeMillis();
  	Time currentTime = new Time(time);
  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
  	String archDir = ConfigInfo.getLogDir();
  	String archiveName = sdf.format(currentTime);
  	if(suffix != null && suffix.compareTo("") != 0){
  		suffix = "_" + suffix;
  	}
  	String fileName = archDir+"/"+archiveName+suffix+"."+extension;
  	return fileName;
  }
}

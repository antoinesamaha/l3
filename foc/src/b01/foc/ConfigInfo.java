/*
 * Created on Jul 20, 2005
 */
package b01.foc;

import b01.foc.gui.DisplayManager;
import b01.foc.gui.MainFrame;
import b01.foc.gui.table.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * @author 01Barmaja
 */
public class ConfigInfo {
	
  private static String appDirectory = null; 
  
  private static String jdbcDrivers = null; 
  private static String jdbcURL = null;  
  private static String jdbcUserName = null;
  private static String jdbcPassword = null;
  
  private static String windowTitle = null;  

  private static int guiNavigatorWidth = 0; 
  private static int guiNavigatorHeight = 0;
  private static int fontSize = 14;
      
  private static String  logDir = null;
  private static boolean logDetails = false;
  private static boolean logDebug = false;
  private static boolean logConsoleActive = true;
  private static boolean logDBRequest = true;
  private static boolean logDBSelect = true;
  private static boolean logFileActive = false; 
  private static boolean logFileWithTime = false;
  private static boolean unitDevMode = true;
  private static boolean popupExceptionDialog = false;  
  private static boolean showStatusColumn = false;
  private static String workPath = "";
  private static String defaultConfigInfoFilePath = "testDB/defaultConfig.properties";
    
  public static void loadFile(boolean unitTesting){
    loadFile(unitTesting, false);
  }
  
  public static void loadFile(boolean unitTesting, boolean installionWizard) {
    try{
      Properties props = new Properties();
      //FileInputStream in = null;
      InputStream in = null;
      
      String directory = Globals.getApp().getDefaultAppDirectory();
      String environment = Globals.getApp().getDefaultEnvironment();
      String homePath = directory+"/"+environment;
      
      if(unitTesting){
      	//in = new FileInputStream(workPath+"properties/configUnit.properties");
        in = Globals.getInputStream(homePath+"properties/configUnit.properties");
      }else if( !installionWizard ){
        in = Globals.getInputStream(homePath+"/properties/config.properties");
      }
      
      if(Globals.getApp().isWithRegistry() && in == null){
        in = Globals.getInputStream(workPath+defaultConfigInfoFilePath);
      }
      
      if( in == null ){
        in = Globals.getInputStream(workPath+"properties/config.properties");
      }
      
      props.load(in);
      in.close();
      
      String str = null;
      
      appDirectory = props.getProperty("appDirectory"); 
        
      jdbcDrivers = props.getProperty("jdbc.drivers");
      jdbcURL = props.getProperty("jdbc.url");
      jdbcUserName = props.getProperty("jdbc.username");
      jdbcPassword = props.getProperty("jdbc.password");

      //Setting default navigator size to full screen
      //Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
      //guiNavigatorWidth = (int)screenDim.getWidth();
      //guiNavigatorHeight = (int)screenDim.getHeight();
      //Globals.logString(new String(guiNavigatorWidth + ","+guiNavigatorHeight));
      //System.out.print(new String(guiNavigatorWidth + ","+guiNavigatorHeight));
            
      windowTitle = props.getProperty("gui.windowTitle");      
      str = props.getProperty("gui.navigator.width");
      if(str != null) guiNavigatorWidth = Integer.valueOf(str).intValue();
      str = props.getProperty("gui.navigator.height");      
      if(str != null) guiNavigatorHeight = Integer.valueOf(str).intValue(); 
      str = props.getProperty("gui.table.maxWidth");
      if(str != null) FTable.MAX_WIDTH = Integer.valueOf(str).intValue();
      str = props.getProperty("gui.table.maxHeight");
      if(str != null) FTable.MAX_HEIGHT = Integer.valueOf(str).intValue();
      str = props.getProperty("gui.table.minWidth");
      if(str != null) FTable.MIN_WIDTH = Integer.valueOf(str).intValue();
      str = props.getProperty("gui.table.minHeight");
      if(str != null) FTable.MIN_HEIGHT = Integer.valueOf(str).intValue();
      
      str = props.getProperty("unitDevMode");
      unitDevMode = str != null ? str.compareTo("1") == 0 : false;

      logDir = props.getProperty("log.dir");
      if(logDir == null) logDir = ".";
      
      str = props.getProperty("log.fileActive");
      logFileActive = str != null ? str.compareTo("1") == 0 : false;
      
      str = props.getProperty("log.fileWithTime");
      logFileWithTime = str != null ? str.compareTo("1") == 0 : false;

      str = props.getProperty("log.ConsoleActive");
      logConsoleActive = str != null ? str.compareTo("1") == 0 : false;

      str = props.getProperty("log.dbRequest");
      logDBRequest = str != null ? str.compareTo("1") == 0 : false;

      str = props.getProperty("log.dbSelect");
      logDBSelect = str != null ? str.compareTo("1") == 0 : false;

      str = props.getProperty("log.details");
      logDetails = str != null ? str.compareTo("1") == 0 : false;

      str = props.getProperty("log.debug");
      logDebug = str != null ? str.compareTo("1") == 0 : false;
      
      str = props.getProperty("log.popupExceptionDialog");
      popupExceptionDialog = str != null ? str.compareTo("1") == 0 : false;
      
      str = props.getProperty("debug.showStatusColumn");
      showStatusColumn = str != null ? str.compareTo("1") == 0 : false;     

      str = props.getProperty("gui.font.size");
      if(str != null){
        fontSize = Integer.valueOf(str).intValue();  
      }
      DisplayManager dm = Globals.getDisplayManager();
      if(dm != null){
      	MainFrame mainFrame = dm.getMainFrame();
      	if(mainFrame != null){
      		mainFrame.setWindowTitle();
      	}
      }
    }catch(Exception e){
      e.printStackTrace();
      JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
          "Error during config file load : properties/config.properties\n" + e.getMessage(),
          "01Barmaja",
          JOptionPane.ERROR_MESSAGE,
          null);
    }
    
  }
  
  public static String getWindowTitle() {
    return windowTitle;
  }
  
  public static String getAppDirectory() {
    return appDirectory;
  }
  
  public static int getGuiNavigatorHeight() {
    return guiNavigatorHeight;
  }
  
  public static int getGuiNavigatorWidth() {
    return guiNavigatorWidth;
  }
  
  public static void setGuiNavigatorHeight(int guiNavigatorHeight) {
    ConfigInfo.guiNavigatorHeight = guiNavigatorHeight;
  }
  
  public static void setGuiNavigatorWidth(int guiNavigatorWidth) {
    ConfigInfo.guiNavigatorWidth = guiNavigatorWidth;
  }
  
  public static String getJdbcDrivers() {
    return jdbcDrivers;
  }
  
  public static String getJdbcPassword() {
    return jdbcPassword;
  }
  
  public static String getJdbcURL() {
    return jdbcURL;
  }
  
  public static String getJdbcUserName() {
    return jdbcUserName;
  }

  public static boolean isUnitDevMode() {
    return unitDevMode;
  }

  public static String getLogDir(){
  	return logDir;
  }
  
  public static boolean isLogFileActive() {
    return logFileActive;
  }

  public static boolean isLogFileWithTime() {
  	return logFileWithTime;
  }
  
  public static boolean isLogConsoleActive() {
    return logConsoleActive;
  }
  
  public static void setLogConsoleActive(boolean logConsActive) {
    logConsoleActive = logConsActive;
  }

  public static boolean isLogDBRequestActive() {
    return logDBRequest;
  }

  public static boolean isLogDBSelectActive() {
    return logDBSelect;
  }

  public static boolean isShowStatusColumn() {
    return showStatusColumn;
  }

  public static boolean isPopupExceptionDialog() {
    return popupExceptionDialog;
  }
  
  public static boolean isLogDetails(){
  	return logDetails;
  }

  public static boolean isLogDebug(){
  	return logDebug;
  }

  public static int getFontSize() {
    return fontSize;
  }

  public static String getWorkPath() {
    return workPath;
  }

  public static void setWorkPath(String workPath) {
    ConfigInfo.workPath = workPath;
  }
  
  public static boolean isRemoveIndexesDuringAdaptDataModel(){
	return false;
  }
}

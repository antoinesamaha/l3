/*
 * Created on Jul 20, 2005
 */
package b01.l3;

import b01.foc.Globals;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

/**
 * @author 01Barmaja
 */
public class L3ConfigInfo {

  private static boolean propertiesFileLoaded = false;
  private static boolean debugMode            = false;
  private static boolean emulationMode        = false;
  
  private static void loadFileIfNeeded() {
    if(!propertiesFileLoaded){
      try{
        File inFile = new File("properties/l3Config.properties");
        if(inFile.isFile() && inFile.exists()){ 
          Properties props = new Properties();
          FileInputStream in = new FileInputStream("properties/l3Config.properties");          
          props.load(in);
          in.close();
          
          String str = null;
          str = props.getProperty("debugMode");
          debugMode = str != null ? str.compareTo("1") == 0 : false;
          
          str = props.getProperty("emulationMode");
          emulationMode = str != null ? str.compareTo("1") == 0 : false;          
        }
        
        propertiesFileLoaded = true;
      }catch(Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
            "Error during config file load : properties/l3Config.properties\n" + e.getMessage(),
            "01Barmaja",
            JOptionPane.ERROR_MESSAGE,
            null);
      }
    }    
  }
  
  public static boolean getDebugMode() {
    loadFileIfNeeded();
    return debugMode;
  }
  
  public static boolean getEmulationMode() {
    loadFileIfNeeded();
    return emulationMode;
  }
  
  public static void setEmulationMode(boolean emulMode) {
    loadFileIfNeeded();
    emulationMode = emulMode;
  }  
}


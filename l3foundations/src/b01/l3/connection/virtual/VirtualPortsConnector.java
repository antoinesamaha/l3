package b01.l3.connection.virtual;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JOptionPane;

import b01.foc.Globals;

public class VirtualPortsConnector {
  
  private static VirtualPortsConnector virtualPortsConnector = null;
  private HashMap<String, String> tableConnection = null;
  private HashMap<String, VirtualSerialPort> listOfExistedPorts = null;
  
  public VirtualPortsConnector() {
    tableConnection = new HashMap<String, String>();
    listOfExistedPorts = new HashMap<String, VirtualSerialPort>();
    loadFile();
  }

  public static VirtualPortsConnector getInstance(){
    synchronized(VirtualPortsConnector.class) {
      if (virtualPortsConnector == null){
        virtualPortsConnector = new VirtualPortsConnector();
      }
    }
    return virtualPortsConnector;
  }
  
  private void loadFile() {
    try{
      File inFile = new File("properties/virtualPorts.properties");
      if(inFile.isFile() && inFile.exists()){ 
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(inFile);          
        props.load(in);
        in.close();
        
        Iterator tableConnectionIter  = props.keySet().iterator();
        while (tableConnectionIter.hasNext()){
          String key   = (String)tableConnectionIter.next();
          String value = (String)props.get(key);
          tableConnection.put(key, value);
          tableConnection.put(value, key);
        }
      }
    }catch(Exception e){
      Globals.logException(e);
      JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
          "Error during config file load : properties/virtualPorts.properties\n" + e.getMessage(),
          "01Barmaja",
          JOptionPane.ERROR_MESSAGE,
          null);
    }
  }
  
  public void putVirtualPort(String portName, VirtualSerialPort virtualPort){
    try{
      if(portName != null && virtualPort != null){
        listOfExistedPorts.put(portName, virtualPort);
        String otherPortName = (String)tableConnection.get(portName);
        if(otherPortName != null){
          VirtualSerialPort otherVirtualPort = listOfExistedPorts.get(otherPortName);
          if (otherVirtualPort != null){
            ConnectedInputStream inputStream = null;
            ConnectedOutputStream outputStream = null;
            
            inputStream = (ConnectedInputStream)virtualPort.getInputStream();
            outputStream = (ConnectedOutputStream)otherVirtualPort.getOutputStream();
            outputStream.setInputStream(inputStream);
            
            inputStream = (ConnectedInputStream) otherVirtualPort.getInputStream();
            outputStream = (ConnectedOutputStream) virtualPort.getOutputStream();
            outputStream.setInputStream(inputStream);
          }
        }
      }
    }catch(Exception e){
      Globals.logException(e);      
    }
  }
}





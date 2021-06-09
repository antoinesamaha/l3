package b01.l3.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEventListener;

import b01.foc.Globals;
import b01.l3.exceptions.L3SerialPortOpeningException;

public class RealSerialPort implements SerialPortInterface{
  private SerialPort serialPort = null;  

  private String portName = null;
  private int baudrate    = 0;
  private int dataBits    = 0;
  private int stopBits    = 0;
  private int parity      = 0;
  
  public RealSerialPort(Properties props) throws Exception{
    setParametersFromProperties(props);  
  }
  
  public void dispose(){
    closeConnection();
    if(serialPort != null){
      serialPort = null;
    }
  }
  
	public boolean isSerialPortNull() {
		return serialPort == null;
	}
  
  public InputStream getInputStream() throws IOException {
    return serialPort.getInputStream();
  }

  public OutputStream getOutputStream() throws IOException {
    return serialPort.getOutputStream();
  }
  
  private void setParameters(String portName, int baudrate, int dataBits, int stopBits, int parity){
    this.portName = portName;
    this.baudrate = baudrate;
    this.dataBits = dataBits;
    this.stopBits = stopBits;
    this.parity   = parity;
  }
  
  public void setParametersFromProperties(Properties props) throws Exception {
    if(props != null){
      String portName = "COM3";
      int baudrate = 38400;
      int dataBits = SerialPort.DATABITS_8;
      int stopBits = SerialPort.STOPBITS_1;
      int parity = SerialPort.PARITY_NONE;
      
      String str = null;
      
      str = props.getProperty("serialPort.name");
      if(str != null && str.compareTo("") != 0){
        portName = str ;      
      }

      str = props.getProperty("serialPort.baudrate");
      if(str != null && str.compareTo("") != 0){
        baudrate = Integer.valueOf(str);
      }

      str = props.getProperty("serialPort.databits");
      if(str != null && str.compareTo("") != 0){
        int cases = Integer.valueOf(str);
        switch(cases){
        case 5:
          dataBits = SerialPort.DATABITS_5;
          break;
        case 6:
          dataBits = SerialPort.DATABITS_6;
          break;
        case 7:
          dataBits = SerialPort.DATABITS_7;
          break;
        case 8:
          dataBits = SerialPort.DATABITS_8;
          break;
        }
      }
      
      str = props.getProperty("serialPort.stopbit");
      if(str != null && str.compareTo("") != 0){
        int cases = Integer.valueOf(str);
        switch(cases){
        case 1:
          stopBits = SerialPort.STOPBITS_1;
          break;
        case 15:
          stopBits = SerialPort.STOPBITS_1_5;
          break;
        case 2:
          stopBits = SerialPort.STOPBITS_2;
          break;
        }
      }
      
      str = props.getProperty("serialPort.parity");
      if(str != null && str.compareTo("") != 0){
        if(str.compareTo("NONE") == 0){
          parity = SerialPort.PARITY_NONE;  
        }else if(str.compareTo("EVEN") == 0){
          parity = SerialPort.PARITY_EVEN;
        }else if(str.compareTo("MARK") == 0){
          parity = SerialPort.PARITY_MARK;
        }else if(str.compareTo("ODD") == 0){
          parity = SerialPort.PARITY_ODD;
        }else if(str.compareTo("SPACE") == 0){
          parity = SerialPort.PARITY_SPACE;
        }
      }
      
      setParameters(portName, baudrate, dataBits, stopBits, parity);
    }
  }

  public void closeConnection() {
    if(serialPort != null){
      serialPort.close();        
      serialPort = null;
    }
  }
  
  public void openConnection() throws L3SerialPortOpeningException {
    if(serialPort == null){
    	Globals.logString("Serial connecting...");
    	Globals.logString("   "+portName+" baude="+baudrate+" dataBits="+dataBits+" stop="+stopBits+" parity="+parity);
      boolean portFound = false;
      Enumeration iter = CommPortIdentifier.getPortIdentifiers();
      while(iter != null && iter.hasMoreElements()){
        CommPortIdentifier portId = (CommPortIdentifier) iter.nextElement();
        Globals.logString("System port :"+portId.getName()+"Type :"+portId.getPortType()+" p/p "+CommPortIdentifier.PORT_SERIAL);
        if(portId != null && portId.getPortType() == CommPortIdentifier.PORT_SERIAL && portId.getName().compareTo(portName) == 0){
          portFound = true;
          try{
            serialPort = (SerialPort) portId.open("01Barmaja.L3", 2000);
          }catch (PortInUseException e) {
            Globals.logException(e);
            throw new L3SerialPortOpeningException("L3X - Port "+portName+" is in use", e);
          }
        }
      }
    
      if(!portFound){
        throw new L3SerialPortOpeningException("L3X - Port "+portName+" not found");
      }
    }else{
      throw new L3SerialPortOpeningException("Connection already opened");
    }

    if(serialPort != null){
      try {
        serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
        serialPort.notifyOnDataAvailable(true);
      	Globals.logString("Serial connection success");
      } catch (Exception e) {
        Globals.logException(e);
      }
    } 
  }

  public void addEventListener(SerialPortEventListener cumulationListener) throws Exception{
    if(serialPort != null){
      serialPort.addEventListener(cumulationListener);
    }
  }

  public void removeEventListener() {
    if(serialPort != null){
      serialPort.removeEventListener();
    }
  }
}

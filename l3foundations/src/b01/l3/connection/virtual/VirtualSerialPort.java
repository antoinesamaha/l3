package b01.l3.connection.virtual;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

import b01.l3.connection.SerialPortInterface;
import b01.l3.exceptions.L3SerialPortOpeningException;

public class VirtualSerialPort implements SerialPortInterface{
  protected ConnectedOutputStream outputStream = null;
  protected ConnectedInputStream inputStream = null;
  
  private SerialPortEventListener serialPortListener = null;
  
  private String portName = null;
  private boolean connected = false;
  
  private SerialPort serialPort = null; 
  
  public VirtualSerialPort(Properties props) throws Exception{
    outputStream = new ConnectedOutputStream();
    inputStream = new ConnectedInputStream();    
    
    setParametersFromProperties(props);
    inputStream.setSerialPort(this);
    serialPortListener = null;
  }

  public void dispose() {
    if (outputStream != null){
      outputStream.dispose();
      outputStream = null;
    }
    
    if (inputStream != null){
      inputStream = null;
    }
    
    if (serialPortListener != null){
      serialPortListener = null;
    }
  }
  
  protected synchronized void dataAvailable()throws IOException {
    inputStream.read();
  }

	public boolean isSerialPortNull() {
		return false;
	}
  
  public InputStream getInputStream() throws IOException {
    return inputStream;
  }

  public OutputStream getOutputStream() throws IOException {
    return outputStream;
  }
  
  public void closeConnection() {
    // TODO Auto-generated method stub
    connected = false;
  }

  public void setParametersFromProperties(Properties props) throws Exception {
    if(props != null){
      String str = null;
      
      str = props.getProperty("serialPort.name");
      if(str != null && str.compareTo("") != 0){
        this.portName = str ;      
      }
      VirtualPortsConnector.getInstance().putVirtualPort(str, this);
    }
  }

  public void addEventListener(SerialPortEventListener serialPortListener) throws Exception {
    if(this.serialPortListener != null){
      throw new TooManyListenersException();
    }else{
      this.serialPortListener = serialPortListener;
    }
  }

  public void removeEventListener(){
    this.serialPortListener = null;
  }
  
  private void notifyListeners(SerialPortEvent e){
    //Globals.logString("PT2: In Notifying Listeners");
    if(this.serialPortListener != null){
      //Globals.logString("PT3: Calling the serial Event");
      this.serialPortListener.serialEvent(e);
    }
  }
  
  public void notifyListenersThatDataAvailable(){
    if(serialPort == null){
      serialPort = new EmptySerialPort();
    }
    SerialPortEvent e = new SerialPortEvent(serialPort, SerialPortEvent.DATA_AVAILABLE, false, false);
    notifyListeners(e);
  }

  public void openConnection() throws Exception {
    if(connected) throw new L3SerialPortOpeningException("Virtual port already opened");
    connected = true;
  }
}

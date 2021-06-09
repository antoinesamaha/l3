package b01.l3.connection.virtual;

import java.io.IOException;
import java.io.InputStream;

public class ConnectedInputStream extends InputStream {

  private int length  = 0;
  private byte[] b = new byte[1024];
  private VirtualSerialPort serialPort = null;
  
  @Override
  public int read() throws IOException {
    int ret = -1;
    if(length > 0){
      ret = b[0];
      for(int i=0; i<length-1; i++){
        b[i] = b[i+1];
      }
      length--;
    }    
    return ret;
  }
  
  public int available() throws IOException {
    return length; 
  }
  
  public void write(int bt) throws IOException {
    b[length] = (byte)bt;
    length++;
    //Globals.logString("PT1: InputStream notifying the OutputStream");
    serialPort.notifyListenersThatDataAvailable();
  }
  
  public void setSerialPort(VirtualSerialPort serialPort) {
    this.serialPort = serialPort;
  }
}

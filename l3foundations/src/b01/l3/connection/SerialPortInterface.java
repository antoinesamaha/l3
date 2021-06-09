package b01.l3.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.comm.SerialPortEventListener;

public interface SerialPortInterface {
  public void setParametersFromProperties(Properties props) throws Exception;
  public void dispose();
  
  public void openConnection() throws Exception;
  public void closeConnection();
  public boolean isSerialPortNull();
  
  public void addEventListener(SerialPortEventListener serialPortListener) throws Exception;
  public void removeEventListener();
  
  public InputStream getInputStream() throws IOException;
  public OutputStream getOutputStream() throws IOException;
}

/*
 * Created on Jun 2, 2006
 */
package b01.l3.connection;

import b01.l3.L3Frame;

/**
 * @author 01Barmaja
 */
public interface L3SerialPortListener {  
  public void received(L3Frame frame);
}

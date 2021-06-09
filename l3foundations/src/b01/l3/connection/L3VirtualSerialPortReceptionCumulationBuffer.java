/*
 * Created on Jun 5, 2006
 */
package b01.l3.connection;

import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public class L3VirtualSerialPortReceptionCumulationBuffer extends L3SerialPortReceptionCumulationBuffer{
  
  protected synchronized void dataAvailable(){
    try {
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
}

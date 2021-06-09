/*
 * Created on 16-Jun-2005
 */
package b01.foc.util;

import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public class TestUtil {
  public static void main(String args[]){
    String mac = PCID.getUniqueID();
    BCifer bc = new BCifer(); 
    String enc = bc.encode(mac);
    String dec = bc.decode(enc);
    
    Globals.logString("MAC ="+mac);
    Globals.logString("Encode ="+enc);
    Globals.logString("Decode ="+dec);
    
    ASCII ascii = new ASCII("properties/machine");
    ascii.getASCIIArray();
  }
}

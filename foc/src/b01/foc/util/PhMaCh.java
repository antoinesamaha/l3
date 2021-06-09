/*
 * Created on 17-Jun-2005
 */
package b01.foc.util;

import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public class PhMaCh {
  public static boolean isPhysicalMachine(){
    boolean b = false;
    String refStr = PhMaInfo.getID();
    
    BCifer bc = new BCifer();
    String locStr = bc.encode(PCID.getUniqueID());
    
    b = locStr.compareTo(refStr) == 0;
    return b;
  }
}

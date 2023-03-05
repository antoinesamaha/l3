/*
 * Created on 17-Jun-2005
 */
package b01.l3.drivers;

import javax.swing.JOptionPane;

import b01.foc.Globals;
import b01.foc.util.*;
import b01.l3.L3ConfigInfo;

/**
 * @author 01Barmaja
 */
public class PhMaCh {
  public static boolean isPhysicalMachine(IPhMaInfo phMachineInfo){
//    boolean b = false;
//    String refStr = phMachineInfo != null ? phMachineInfo.getID() : "3242FGsw3";
//    
//    BCifer bc = new BCifer();
//    String locStr = bc.encode(PCID.getUniqueID());
//    
//    b = locStr.compareTo(refStr) == 0;
//    b = true;
//    if(L3ConfigInfo.getEmulationMode()){
//    	b = true;
//    }
//    if(!b){
//      if(!L3ConfigInfo.getEmulationMode()){
//      	if(Globals.getDisplayManager() != null){
//	        JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
//	            "Security Error!",
//	            "01Barmaja",
//	            JOptionPane.ERROR_MESSAGE,
//	            null);
//      	}
//      	b01.foc.Globals.logString("Security Error! Physical="+locStr+" JAR="+refStr);
//      	
//      	if(		 locStr.compareTo("DECECED1CDC9D1BBC9D1BCC6D1CBCAD1CBB9") == 0
//      			|| locStr.compareTo("DECECED1CECED1CECED1CECED1CECED1CECE") == 0//This is for My Virtual machine Windows 7 - 32bits Basic
//      			|| locStr.compareTo("DECECED1CEB9D1CBC9D1B8CAD1B8BBD1CEC6") == 0 
//      			|| locStr.compareTo("DECECED1CDC8D1BBB8D1C8C5D1CCCDD1BCC8") == 0
//      			|| locStr.compareTo("DECECED1CDC8D1BBB8D1C8C5D1CCCDD1BCC8") == 0){//This is for VIRLIS new 11.10
//        	if(Globals.getDisplayManager() != null){
//	          JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
//	              "YOU ARE USING BARMAJA NOTEBOOK!",
//	              "01Barmaja",
//	              JOptionPane.ERROR_MESSAGE,
//	              null);
//        	}
//        	b01.foc.Globals.logString("YOU ARE USING BARMAJA NOTEBOOK!");
//      		b = true;
//      	}
//      }else{
//        b = true;
//      }
//    }
//    return b;
	  return true;
  }
}

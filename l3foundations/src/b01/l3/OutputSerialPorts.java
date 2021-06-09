/*
 * Created on 17-Jun-2005
 */
package b01.l3;

import java.util.Enumeration;
import javax.comm.CommPortIdentifier;
import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public class OutputSerialPorts {
  public static void main(String[] args){
  	printPortList();
  }
  	
  public static void printPortList(){
    Enumeration iter = CommPortIdentifier.getPortIdentifiers();
    while(iter != null && iter.hasMoreElements()){
      CommPortIdentifier portId = (CommPortIdentifier) iter.nextElement();
      if(portId != null){
        String type = String.valueOf(portId.getPortType());
        if(portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
          type = "Serial";
        }
        Globals.logString("Name = "+portId.getName()+" Type ="+type);

        
       /* if(true && portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
          BSerialPort sp = new BSerialPort(portId.getName());
          sp.open();
          sp.send("abcd");
          sp.close();
          Globals.logString("    ...end sending");          
        }*/
        
      }
    }
  }
}

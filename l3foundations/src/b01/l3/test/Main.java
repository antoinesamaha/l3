package b01.l3.test;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.swing.AbstractAction;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.admin.FocVersion;
import b01.foc.menu.FMenuItem;
import b01.foc.menu.FMenuList;
import b01.l3.L3KernelModule;
import b01.l3.PoolKernel;

public class Main {

  private static void declareMenu(){
    Application app = Globals.getApp();
    
    FMenuList mainMenu = new FMenuList("ROOT", 'R');
    FMenuList poolMenu = new FMenuList("Pool", 'P');
    
    mainMenu.addMenu(poolMenu);
    
    FMenuItem poolItem = new FMenuItem("Pool list", 'p', new AbstractAction(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -5997537287163846340L;

			public void actionPerformed(ActionEvent e) {
				PoolKernel.newBrowsePanel(null, 0);
			}
    });
    poolMenu.addMenu(poolItem);      
    
    app.setMainAppMenu(mainMenu);
  }  
  
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    try {
      Application app = Globals.newApplication(true, true, true);
      FocVersion.addVersion("L3", "l3-1.0" , 1000);
      app.declareModule(new L3KernelModule());
      
      app.login();
      //Declare the local application menu
      declareMenu();
      app.entry();
    } catch (Exception e) {
      Globals.logException(e);
    }
	}
	
	public static void serialTest(){
		SerialPort p10 = null;
		SerialPort p9 = null;
		
		try{
	    Enumeration iter = CommPortIdentifier.getPortIdentifiers();
	    while(iter != null && iter.hasMoreElements()){
	      CommPortIdentifier portId = (CommPortIdentifier) iter.nextElement();
	      Globals.logString("System port :"+portId.getName()+"Type :"+portId.getPortType()+" p/p "+CommPortIdentifier.PORT_SERIAL);
	      if(portId != null && portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
	      	if(portId.getName().compareTo("COM10") == 0){
	      		p10 = (SerialPort) portId.open("01Barmaja.L3", 2000);
	      		p10.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	      		p10.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT );
	      		p10.notifyOnCTS(true);
	      		p10.notifyOnDSR(true);
	      		p10.notifyOnDataAvailable(true);
	      		p10.addEventListener(new SerialPortEventListener(){
							public void serialEvent(SerialPortEvent arg0) {
								System.out.println("p10 event:"+arg0.getEventType());
							}
	      		});
	      	}else if(portId.getName().compareTo("COM9") == 0){
	      		p9 = (SerialPort) portId.open("01Barmaja.L3", 2000);
	      		p9.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	      		p9.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT );
	      		p9.notifyOnCTS(true);
	      		p9.notifyOnDSR(true);
	      		p9.notifyOnDataAvailable(true);
	      		p9.addEventListener(new SerialPortEventListener(){
							public void serialEvent(SerialPortEvent arg0) {
								System.out.println("p9 event:"+arg0.getEventType());
							}
	      		});
	      	}
	      }
	    }
	    
	    System.out.print(p10.isDSR());
	    System.out.print(p10.isCTS());
	    System.out.print(p10.isDTR());
	    System.out.println(p10.isRTS());
	    
	    p9.setDTR(false);
	    //p9.setRTS(false);
	    
	    Thread.sleep(1000);
	    
	    System.out.print(p10.isDSR());
	    System.out.print(p10.isCTS());
	    System.out.print(p10.isDTR());
	    System.out.println(p10.isRTS());

	    p9.setDTR(true);
	    //p9.setRTS(true);
	    
	    Thread.sleep(1000);
	    
	    System.out.print(p10.isDSR());
	    System.out.print(p10.isCTS());
	    System.out.print(p10.isDTR());
	    System.out.print(p10.isRTS());

	    p9.getOutputStream().write(44);
	    
		}catch(Exception e){
			e.printStackTrace();
		}
    //serialPort.notifyOnDataAvailable(true);
	}
}

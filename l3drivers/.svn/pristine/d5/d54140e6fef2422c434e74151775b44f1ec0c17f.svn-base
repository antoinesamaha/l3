package b01.l3.drivers.astm;

import java.util.Properties;

import b01.foc.Globals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.data.L3Message;
import b01.l3.emulator.EmulatorRobot;
import b01.l3.emulator.IEmulator;

public class AstmEmulator extends AstmDriver implements IEmulator {
	EmulatorRobot robot = null;
  
	public void init(Instrument instrument, Properties props) throws Exception {
		super.init(instrument, props);
		setDriverReceiver(new AstmEmulatorReceiver(this));
		robot = new EmulatorRobot(this, props);
	}

	public void dispose() {
		if (robot != null) {
			robot.dispose();
			robot = null;
		}
	}

	public void sendMessageWithResults(L3Message message) throws Exception {
		resetFrameArray();
		frameCreator.buildFrameArray(this, message, false);
		sendFramesArray(true);
	}

  public EmulatorRobot getRobot() {
    return robot;
  }
  
  public String testMaps_getInstCode(String lisCode){
  	String str = null; 
  	try{
  		str = ((DriverSerialPort)getRobot().getRelatedInstrument().getDriver()).testMaps_getInstCode(lisCode);
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  	return str;
  }
}

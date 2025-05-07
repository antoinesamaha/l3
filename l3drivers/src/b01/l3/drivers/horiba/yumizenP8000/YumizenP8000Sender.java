package b01.l3.drivers.horiba.yumizenP8000;

import b01.foc.util.ASCII;
import b01.sbs.BClientKeepOpened;

public class YumizenP8000Sender {
	private YumizenP8000Driver driver  = null;	
	private BClientKeepOpened  bClient = null;
	
	public YumizenP8000Sender(YumizenP8000Driver driver) {
		this.driver = driver;
	}
	
	public void dispose() {
		driver = null;
		if(bClient != null) {
			bClient.dispose();
			bClient = null;
		}
	}
	
	private void logString(String str) {
		if(driver != null && driver.getInstrument() != null) {
			driver.getInstrument().logString(str);
		}
	}
	
	public void init() {
	//ATTENTION
		bClient = new BClientKeepOpened("192.168.170.12", 10001);
//		bClient = new BClientKeepOpened("192.168.0.1", 10001);
//		bClient.sendMessage(messageToSend);
	}

	public void connect() {
		if(bClient != null) {
			logString("Connecting YumizenSender");
			bClient.open();
			logString("Connected YumizenSender");
		} else {
			logString("Could not connect Yumizen Sender: bClient is null");
		}
	}
	
	public void disconnect() {
		if(bClient != null) {
			logString("Closing YumizenSender");
			bClient.close();
			logString("Closed YumizenSender");
		} else {
			logString("Could not close Yumizen Sender: bClient is null");
		}
	}
	
	public boolean sendMessage(String messageToSend) {
		boolean error = false;
		if(bClient != null) {
			String loggableMessage = ASCII.convertNonCharactersToDescriptions(messageToSend);
			logString("Sending using YumizenSender: "+loggableMessage);
			String answer = bClient.sendMessage(messageToSend);
			logString("YumizenSender Received: "+answer);
			if(answer == null || !answer.contains("MSA|AA")) {
				error = true;
			}
		} else {
			logString("Could not send using Yumizen Sender: bClient is null");
		}
		return error;
	}
}

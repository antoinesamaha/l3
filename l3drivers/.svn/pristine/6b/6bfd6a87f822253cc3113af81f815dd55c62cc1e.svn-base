package b01.l3.drivers.helena.junior24;

import java.util.Date;
import java.util.Properties;

import b01.foc.Globals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.L3ConfigInfo;
import b01.l3.data.L3Message; 
import b01.l3.drivers.PhMaCh;

public class Junior24Driver extends DriverSerialPort {

  public static int MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS = 3;
  
  public void init(Instrument instrument, Properties props) throws Exception {
    if(L3ConfigInfo.getDebugMode()){
      Globals.getDisplayManager().popupMessage("01BARMAJA you are in Debug MODE !!!!");
      //RESPONSE_TIMEOUT = 1000 * 60 * 5 ;
    }
    setDriverReceiver(new Junior24Receiver(this));
    super.init(instrument, props);

    if (!PhMaCh.isPhysicalMachine(new b01.l3.drivers.helena.junior24.PhMaInfo())) {
      getInstrument().logString("L3 date: " + (new Date(System.currentTimeMillis())).toString());
      newSecureSerialPort(props);
    } else {
      getInstrument().logString("L3 date : " + (new Date(System.currentTimeMillis())).toString());
      newSerialPort(props);
    }
    testMaps_fillFromProperties(props);   
    Junior24Frame answerFrame = new Junior24Frame(getInstrument());
    getL3SerialPort().setAnswerFrame(answerFrame);
  }   
  
  
  @Override
  protected void synchronizeSequenceID() throws Exception {
  }

  public void send(L3Message message) throws Exception {
    if(getInstrument().isLogBufferDetails()){
      getInstrument().logString("Inst code : " + message.sampleIterator());
    }
    resetFrameArray();
    buildFrameArray(message, true);
    sendFramesArray(true);
  }
  
  public void buildFrameArray(L3Message message, boolean fromDriver)throws Exception{
  }
  
  public void sendFramesArray(boolean createDataWithFrame) throws Exception {
  }
  
	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
	}
}

package b01.l3.drivers.bectonDickinson.sedi15;

import java.util.Date;
import java.util.Properties;

import b01.foc.Globals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.L3ConfigInfo;
import b01.l3.data.L3Message;
import b01.l3.drivers.PhMaCh;

public class Sedi15Driver extends DriverSerialPort {
  
  public static int MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS = 2;
  public final static String SEDI_TEST_AFTER_1_HOUR = "ESR1";
  public final static String SEDI_TEST_AFTER_2_HOUR = "ESR2";
  
  public void init(Instrument instrument, Properties props) throws Exception {
    if(L3ConfigInfo.getDebugMode()){
      Globals.getDisplayManager().popupMessage("01BARMAJA you are in Debug MODE !!!!");
    }
    setDriverReceiver(new Sedi15Receiver(this));
    super.init(instrument, props);

    if (!PhMaCh.isPhysicalMachine(new b01.l3.drivers.bectonDickinson.sedi15.PhMaInfo())) {
      getInstrument().logString("L3 date: " + (new Date(System.currentTimeMillis())).toString());
      newSecureSerialPort(props);
    } else {
      getInstrument().logString("L3 date : " + (new Date(System.currentTimeMillis())).toString());
      newSerialPort(props);
    }
    testMaps_fillFromProperties(props);   
    Sedi15Frame answerFrame = new Sedi15Frame(getInstrument());
    getL3SerialPort().setAnswerFrame(answerFrame);
  }   
  
  @Override
  protected void synchronizeSequenceID() throws Exception {
  }

  public void send(L3Message message) throws Exception {
    resetFrameArray();
    buildFrameArray(message, true);
    sendFramesArray(true);
  }
  
  public void buildFrameArray(L3Message message, boolean fromDriver) throws Exception {
  }

  public void sendFramesArray(boolean createDataWithFrame) throws Exception{
  }
  
	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
	}
}

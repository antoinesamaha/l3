package b01.l3.drivers.bectonDickinson.sedi15;

import java.util.Properties;

import b01.foc.Globals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.L3Frame;
import b01.l3.data.L3Message;
import b01.l3.emulator.EmulatorRobot;
import b01.l3.emulator.IEmulator;
import b01.l3.exceptions.L3AnalyzerErrorReturnException;
import b01.l3.exceptions.L3TimeOutException;
import b01.l3.exceptions.L3UnexpectedFrameTypeException;

public class Sedi15Emulator extends Sedi15Driver implements IEmulator {

  EmulatorRobot robot = null;
  
  public void init(Instrument instrument, Properties props) throws Exception {
    super.init(instrument, props);
    setDriverReceiver(new Sedi15EmulatorReceiver(this));
    robot = new EmulatorRobot(this, props);
  }
 
  public void dispose() {
    if (robot != null) {
      robot.dispose();
      robot = null;
    }
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
  
  public void buildFrameArray(L3Message message, boolean fromDriver) throws Exception {
    // SOH frame
    // -----------
    Sedi15Frame frame = Sedi15Frame.newStartOfHeadingFrame(getInstrument());
    addFrame(frame);
    // -----------
    
    // record 
    
    // EOT frame
    frame = Sedi15Frame.newEndOfTransmissionFrame(getInstrument());
    addFrame(frame);
    // -------------
  }

  public void sendMessageWithResults(L3Message message) throws Exception {
    resetFrameArray();
    buildFrameArray(message, false);
    sendFramesArray(true);     
  }

  @Override
  public void sendFramesArray(boolean createDataWithFrame) throws Exception {
  if (createDataWithFrame) {
    for (int i = 0; i < getFrameCount(); i++) {
      L3Frame frame = getFrameAt(i);
      if (frame != null) {
        frame.createDataWithFrame();
      }
    }
  }
    
  int timeOutDelay = getInstrument().getPropertyInteger(InstrumentDesc.FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE);
  
  int i = 0;
  int numberOfFailures = 0;
  for (i = 0; i < getFrameCount() && numberOfFailures < MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS; i++){
    Sedi15Frame frame = (Sedi15Frame) getFrameAt(i);
    if (frame != null) {
      String strToSend = frame.getDataWithFrame().toString();
      Sedi15Frame resFrame = null;
      boolean firstTry = true;
      try{
        resFrame = (Sedi15Frame)getL3SerialPort().sendAndWaitForResponse(strToSend, timeOutDelay);
      }catch(L3TimeOutException e){
        if(firstTry){
          Globals.logString("SECOND TRY AFTER TIME OUT");
          resFrame = (Sedi15Frame)getL3SerialPort().sendAndWaitForResponse(strToSend, timeOutDelay);
          firstTry = false;
        }else{
          throw e;
        }
        Globals.logException(e);
      }
      
      resFrame.extractDataFromFrame();
      int type = resFrame.getType();
      if (type == Sedi15Frame.FRAME_TYPE_ACK) {
        Globals.logString("Receive ACK");//rr
        numberOfFailures = 0;
      } else if (type == Sedi15Frame.FRAME_TYPE_NACK){
        Globals.logString("Receive NACK");//rr
        numberOfFailures++;
        i--;
      }else {
        throw new L3UnexpectedFrameTypeException("Unexpected answer frame type: " + resFrame.getDataWithFrame()+ "\n For request " + frame.getDataWithFrame());
      }
    }
  }
  if(numberOfFailures == MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS){
    throw new L3AnalyzerErrorReturnException("Frame rejected "+ MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS +" times :" + getFrameAt(i).getDataWithFrame());
  }
  }
}

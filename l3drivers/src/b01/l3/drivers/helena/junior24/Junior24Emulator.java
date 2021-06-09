package b01.l3.drivers.helena.junior24;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import b01.foc.Globals;
import b01.foc.list.FocList;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.L3Frame;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.emulator.EmulatorRobot;
import b01.l3.emulator.IEmulator;
import b01.l3.exceptions.L3AnalyzerErrorReturnException;
import b01.l3.exceptions.L3TimeOutException;
import b01.l3.exceptions.L3UnexpectedFrameTypeException;

public class Junior24Emulator extends Junior24Driver implements IEmulator {
  Random rand;
  EmulatorRobot robot = null;
  
  public void init(Instrument instrument, Properties props) throws Exception {
    super.init(instrument, props);
    setDriverReceiver(new Junior24EmulatorReceiver(this));
    rand = new Random();
    robot = new EmulatorRobot(this, props);
  }

  public void dispose() {
    if (robot != null) {
      robot.dispose();
      robot = null;
    }
  }

  public void treatCurrentlyAnalysedSample(FocList currentlyAnalysedSampleList) {
    int sampleSequence = 0;
    L3Message message = null;

    // convert the sample to a message
    try {
      // get all the samples
      Iterator sampleIterator = currentlyAnalysedSampleList.focObjectIterator();
      if (sampleIterator != null && sampleIterator.hasNext()) {
        L3Sample sample = (L3Sample) sampleIterator.next();
        // get all the tests of the sample
        FocList orderList = sample.getTestList();
        Iterator testIterator = orderList.focObjectIterator();
        sampleSequence++;
        while(testIterator != null && testIterator.hasNext()) {
          L3Test test = (L3Test) testIterator.next();
          double rVal = rand.nextDouble();
          while(rVal == 0) rVal = rand.nextDouble();
          test.setValue((rVal * 10.0));
        }
        message = new L3Message();
        message.addSample(sample);
        resetFrameArray();
        buildFrameArray(message, false);
        sendFramesArray(true);
      }
    } catch (Exception e) {
      getInstrument().logException(e);
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

  @Override
  public void buildFrameArray(L3Message message, boolean fromDriver) throws Exception {
  	Junior24Frame_AccordingToDocumentation frame ;
    int seqNumber = 0;
    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      L3Sample sample = (L3Sample) sIter.next();
      if (sample != null) {
        //int testCount = sample.getTestList().size(); 
        //frame = JR24Frame.newDataFrame(getInstrument(), 0, "", sample);
        frame = Junior24Frame_AccordingToDocumentation.newDataFrame(getInstrument(), seqNumber, sample);
        addFrame(frame);
      }
      seqNumber++;  
    }
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
      Junior24Frame frame = (Junior24Frame) getFrameAt(i);
      if (frame != null) {
        String strToSend = frame.getDataWithFrame().toString();
        Junior24Frame_AccordingToDocumentation resFrame = null;
        boolean firstTry = true;
        try{
          resFrame = (Junior24Frame_AccordingToDocumentation)getL3SerialPort().sendAndWaitForResponse(strToSend, timeOutDelay);
        }catch(L3TimeOutException e){
          if(firstTry){
            Globals.logString("SECOND TRY AFTER TIME OUT");
            resFrame = (Junior24Frame_AccordingToDocumentation)getL3SerialPort().sendAndWaitForResponse(strToSend, timeOutDelay);
            firstTry = false;
          }else{
            throw e;
          }
          Globals.logException(e);
        }
          
        resFrame.extractDataFromFrame();
        int type = resFrame.getType();
        if (type == Junior24Frame_AccordingToDocumentation.FRAME_TYPE_ACK) {
          numberOfFailures = 0;
        } else if (type == Junior24Frame_AccordingToDocumentation.FRAME_TYPE_NACK){
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

  public void sendMessageWithResults(L3Message message) throws Exception {
  }
}

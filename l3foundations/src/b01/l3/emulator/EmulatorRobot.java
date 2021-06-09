package b01.l3.emulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import b01.foc.ConfigInfo;
import b01.foc.Globals;
import b01.foc.list.FocList;
import b01.l3.Driver;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.MessageListener;
import b01.l3.PoolKernel;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3SampleTestJoinDesc;
import b01.l3.data.L3SampleTestJoinFilter;
import b01.l3.data.L3Test;
import b01.l3.data.L3TestDesc;

public class EmulatorRobot implements Runnable, MessageListener {
  public static final int MODE_GET_SAMPLES_FROM_DB_AND_CALL_EMULATOR = 0;
  public static final int MODE_PREDEFINED_FRAME_SENDING              = 1;
  public static final int MODE_PREDEFINED_FRAME_RECEPTION            = 2;
  
  private L3SampleTestJoinFilter currentlyAnalysedSampleTestFilter = null;

  private String relatedInstrumentCode = null;
  private Instrument relatedInstrument = null;
  private Properties props = null;
  private IEmulator myEmulator = null;
  private int mode;
  private L3Message message = null; 
  private L3Message resultMessageToSend = null;
  
  //rr Begin
  private String firstName, lastName, testLabel;
  private Double testValue;
  private FocList testList;
  //rr End
  
  
  private ArrayList<String> predefinedFrames_List = null;
  private int predefinedFrames_FrameWithError = -1;
  private int predefinedFrames_FrameIndex = 0;
  private String predefinedFrames_ReceivedFrameWithError = null;
  
  private Random rand;

  public EmulatorRobot(IEmulator emulator, Properties props) {
    mode = MODE_GET_SAMPLES_FROM_DB_AND_CALL_EMULATOR;
    myEmulator = emulator;
    this.props = props;
    Thread thread = new Thread(this);

    String instrumentCode = props.getProperty("relatedInstrument.code");
    setRelatedInstrument(instrumentCode);
    predefinedFrames_List = new ArrayList<String>();
    
    thread.start();
    rand = new Random();
  }

  public void dispose() {
  	disposeResultMessage();
  	
    myEmulator = null;
    props = null;
    predefinedFrames_ReceivedFrameWithError = null;
    if(predefinedFrames_List != null){
      predefinedFrames_List.clear();
    }
    predefinedFrames_List = null;
    if(currentlyAnalysedSampleTestFilter != null){
    	currentlyAnalysedSampleTestFilter.dispose();
    	currentlyAnalysedSampleTestFilter = null;
    }
    rand = null;
  }

  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }
  
  public void setRelatedInstrument(String code) {
    relatedInstrumentCode = code;
  }

  public Instrument getRelatedInstrument() {
    if (relatedInstrument == null) {
      PoolKernel pool = PoolKernel.getPoolForInstrument(relatedInstrumentCode);
      relatedInstrument = pool.getInstrument(relatedInstrumentCode);
    }
    return relatedInstrument;
  }

  private void disposeResultMessage(){
  	if(resultMessageToSend != null){
	  	resultMessageToSend.dispose();
	  	resultMessageToSend = null;
  	}
  }
  
  public L3Message newResultMessageToSend(){
  	disposeResultMessage();
  	resultMessageToSend = new L3Message();
  	return resultMessageToSend;
  }
  
  public void fillResultListForResultMessage(){
  	if(resultMessageToSend != null){
  		try{
  			fillResultListForMessage(resultMessageToSend);
  		}catch(Exception e){
  			Globals.logException(e);
  		}
  		disposeResultMessage();
  	}
  }
  
  public void fillResultListForMessage(L3Message message) throws Exception{
    for(int i=0; i<message.getNumberOfSamples(); i++){
    	L3Sample sample = message.getSample(i);
    	if(sample != null){
				// get all the tests of the sample
				FocList orderList = sample.getTestList();
				Iterator testIterator = orderList.focObjectIterator();
				while(testIterator != null && testIterator.hasNext()) {
					L3Test test = (L3Test) testIterator.next();
          double rVal = rand.nextDouble();
          while(rVal == 0) rVal = rand.nextDouble();
          rVal = rVal * 10;
          
          rVal = rVal * 100;
          rVal = Double.valueOf(Math.round(rVal)).doubleValue();
          rVal = rVal / 100;
          
          test.setValue((rVal * 10.0));
				}
				
				myEmulator.sendMessageWithResults(message);
    	}
    }
  }
  
  public void fillResultList(Properties props) {
    Instrument relatedInstrument = getRelatedInstrument();
    if (relatedInstrument != null && relatedInstrument.isConnected()) {
      if (currentlyAnalysedSampleTestFilter == null) {
      	currentlyAnalysedSampleTestFilter = L3SampleTestJoinDesc.newListWithFilter();
      	currentlyAnalysedSampleTestFilter.setInstrumentStatus(relatedInstrument, L3TestDesc.TEST_STATUS_ANALYSING);
      }
      
      currentlyAnalysedSampleTestFilter.setActive(true);
      FocList list = currentlyAnalysedSampleTestFilter.getFocList();
      
      if (list.size() > 0) {
        Driver emulatorDriver = (Driver) myEmulator;
        boolean treated = false;
        while (!treated) {
          if (!emulatorDriver.reserve()) {
            treated = true;
            L3Message message = currentlyAnalysedSampleTestFilter.convertToMessage();
            try{
            	fillResultListForMessage(message);
            }catch(Exception e){
            	Globals.logException(e);
            }
            message.dispose();
            message = null;
            emulatorDriver.release();
          }else {
            try {
              Thread.sleep(5000);
            } catch (Exception e) {
              Globals.logException(e);
            }
          }
        }
      }
    }
  }

  public void run() {
    try {
      while (true) {
        Thread.sleep(10000);
        fillResultListForResultMessage();
        fillResultList(props);        
      }
    } catch (Exception e) {
        Globals.logException(e);
    }
  }
  
  public int predefinedFrames_GetFrameIndexWithError(){
    return predefinedFrames_FrameWithError;
  }

  public String predefinedFrames_GetReceivedFrameWithError(){
    return predefinedFrames_ReceivedFrameWithError;
  }

  public int predefinedFrames_GetFrameIndex(){
    return predefinedFrames_FrameIndex;
  }
  
  public void predefinedFrames_RemoveAll(){
    if (predefinedFrames_List != null && !predefinedFrames_List.isEmpty()){
      predefinedFrames_List.clear();
      predefinedFrames_FrameWithError = -1;
    }
  }
  
  public void predefinedFrames_Add(String str){
    predefinedFrames_List.add(str);
  }

  public String predefinedFrames_GetFrameAt(int index){
    return predefinedFrames_List.get(index);
  }

  public void predefinedFrame_Treat(L3Frame frame){
    if((ConfigInfo.isUnitDevMode() && mode == MODE_PREDEFINED_FRAME_RECEPTION) /*|| (ConfigInfo.isUnitDevMode() && mode == MODE_PREDEFINED_FRAME_SENDING)*/){
      String predefinedFrame = null;
      predefinedFrame = predefinedFrames_List.get(predefinedFrames_FrameIndex);
      if (predefinedFrames_FrameWithError < 0 && !frame.getDataWithFrame().toString().equals(predefinedFrame)){
        predefinedFrames_FrameWithError = predefinedFrames_FrameIndex;
        predefinedFrames_ReceivedFrameWithError = frame.getDataWithFrame().toString();
      }
      predefinedFrames_FrameIndex++;
    }
  }
  
  public int predefinedFrames_GetFrameCountNumber(){
    return predefinedFrames_List.size();
  }
  
  public void predefinedFrame_ReplaceEmulator(){
    if (ConfigInfo.isUnitDevMode() && mode == MODE_PREDEFINED_FRAME_SENDING){
      Instrument axsymInstrument = getRelatedInstrument();
      axsymInstrument.removeMessageListener(axsymInstrument);
      axsymInstrument.addMessageListener(this);
    }
  }

  public void messageReceived(L3Message message) {
    if (message != null){
      this.message = message;
      getFromReceivedMessage_Content();
    }
  }

  public void getFromReceivedMessage_Content(){
    L3Sample sample = message.getSample(0);
    firstName = sample.getFirstName();
    lastName = sample.getLastName();
    testList = sample.getTestList();
    for(int i=0; i<testList.size();i++){
      L3Test test = (L3Test) testList.getFocObject(i);
      testValue = test.getValue();
      testLabel = test.getLabel();
    }
  }
  
  public String getFromReceivedMessage_FirstName(){
    return firstName;
  }
  
  public String getFromReceivedMessage_LastName(){
    return lastName;
  }
  
  public String getFromReceivedMessage_TestLabel(){
    return testLabel;
  }

  public Double getFromReceivedMessage_TestValue(){
    return testValue;
  }
}

package b01.l3.drivers.coulter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.foc.util.Int2ByteConverter;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.L3ConfigInfo;
import b01.l3.L3Frame;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.drivers.PhMaCh;
import b01.l3.drivers.coulter.dataBlockStructure.DataBlockStructureSTKS2A;
import b01.l3.drivers.coulter.dataBlockStructure.IDataBlock;
import b01.l3.exceptions.L3AnalyzerErrorReturnException;
import b01.l3.exceptions.L3Exception;
import b01.l3.exceptions.L3InstrumentDoesNotRespondTryLaterException;
import b01.l3.exceptions.L3TimeOutException;
import b01.l3.exceptions.L3UnexpectedFrameTypeException;

public class CoulterDriver extends DriverSerialPort{
  
  protected int blockSequence = 0;
  public static int MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS = 3;
  private IDataBlock dbStructure = null;
  private boolean waitingForDLE = false;
  private char    charDLE       = ' ';
  
  public CoulterDriver(){
    super();
  }

  public void dispose(){
  	super.dispose();
  	if(dbStructure != null){
  		dbStructure.dispose();
  		dbStructure = null;
  	}
  }
  
  public void init(Instrument instrument, Properties props) throws Exception {
    if(L3ConfigInfo.getDebugMode()){
      Globals.getDisplayManager().popupMessage("01BARMAJA you are in Debug MODE !!!!");
      //RESPONSE_TIMEOUT = 1000 * 60 * 5 ;
    }
    setDriverReceiver(new CoulterReceiver(this));
    super.init(instrument, props);

    if (!PhMaCh.isPhysicalMachine(new b01.l3.drivers.coulter.PhMaInfo())) {
      getInstrument().logString("L3 date: " + (new Date(System.currentTimeMillis())).toString());
      newSecureSerialPort(props);
    } else {
      getInstrument().logString("L3 date : " + (new Date(System.currentTimeMillis())).toString());
      newSerialPort(props);
    }
    testMaps_fillFromProperties(props);
    dbStructure = new DataBlockStructureSTKS2A(this);
    CoulterFrame answerFrame = new CoulterFrame(getInstrument());//Here set the CHAR before the data block SYN
    answerFrame.setFrameCharBeforeBlockCount(ASCII.SYN);
    getL3SerialPort().setAnswerFrame(answerFrame);
  }   
    
  public IDataBlock getDataBlockStructure(){
  	return dbStructure;
  }
  
  protected int addAndSplitDataFrame(CoulterFrame frame, int previousNbrOfDataFrames){
  	int nbrOfDataFrames = previousNbrOfDataFrames;
    while(frame.getData().length() > CoulterFrame.FRAME_EXACT_SIZE){
    	CoulterFrame singleFrame = CoulterFrame.newDataBlockFrame(getInstrument(), ++nbrOfDataFrames);
    	StringBuffer buff = new StringBuffer(frame.getData().subSequence(0, CoulterFrame.FRAME_EXACT_SIZE));
    	singleFrame.setData(buff);
    	addFrame(singleFrame);
    	frame.getData().replace(0, CoulterFrame.FRAME_EXACT_SIZE, "");
    }

    int additionalSpaces = CoulterFrame.FRAME_EXACT_SIZE - frame.getData().length();
    for(int i=0; i<additionalSpaces; i++){
    	frame.getData().append(" ");
    }
    
    frame.setBlockNumber(++nbrOfDataFrames);
    addFrame(frame);
    
    return nbrOfDataFrames;
  }
  
  public void buildFrameArray(L3Message message, boolean fromDriver) throws Exception {
    Int2ByteConverter conv = null;
    
    // ENQ frame
    // -----------
    CoulterFrame frame = CoulterFrame.newEnquiryFrame(getInstrument());
    addFrame(frame);
    // -----------
    
    // Data Block Number Frame
    //------------------
    CoulterFrame dataBlockCountFrame = CoulterFrame.newDataBlockCountFrame(getInstrument());
    addFrame(dataBlockCountFrame);
    //------------------
    
    int nbrOfDataFrames = 0;
    // Data Block Stucture Frame
    //-----------------
    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      L3Sample sample = (L3Sample) sIter.next();
      if (sample != null) {
        frame = new CoulterFrame(getInstrument(), CoulterFrame.FRAME_TYPE_DATA);
        
        // INTRO
        frame.append2Data("WL");
        frame.append2Data("AD");
        frame.appendFieldEnd();

        frame.append2Data("I2");
        String fullName = sample.getLastName()+" "+sample.getFirstName();
        frame.append2Data(fullName, 16);
        frame.appendFieldEnd();

        /*
        frame.append2Data("SN");
        String sequence = sample.getId();
        frame.append2Data(sequence, 6);
        fram.appendFieldEnd();
        */
        
        frame.append2Data("U1");
        frame.append2Data(sample.getFirstName(), 16);
        frame.appendFieldEnd();
        
        frame.append2Data("U2");
        frame.append2Data(sample.getLastName(), 16);
        frame.appendFieldEnd();
        
        frame.append2Data("U3");
        //frame.append2Data(sample.getMiddleInitial());
        frame.append2Data(sample.getId());
        frame.appendFieldEnd();
        
        ///////////////////////
        frame.append2Data("ED");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        if(sample.getEntryDate().getTime() < 24*60*60*1000){
          Globals.logString("!!!!!!!!!    Entry date < 1 day    !!!!!!!!!!!"+sample.getEntryDate().getTime());
          frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
        }else{
          frame.append2Data(sdf.format(sample.getEntryDate()));
        }
        frame.appendFieldEnd();
        
        sdf = new SimpleDateFormat("HH:mm");
        frame.append2Data("ET");
        frame.append2Data(sdf.format(sample.getEntryDate()));
        frame.appendFieldEnd();
        /////////////
        
        frame.append2Data("TS");
        frame.append2Data("CBC,"+sample.getId()+",");
        frame.appendFieldEnd();        
        
        frame.appendHEaderSection_SOH();
        
        nbrOfDataFrames = addAndSplitDataFrame(frame, nbrOfDataFrames);
      }
    }      
    //-----------------
    
    conv = new Int2ByteConverter(nbrOfDataFrames);
    dataBlockCountFrame.append2Data(conv.getHighByte());
    dataBlockCountFrame.append2Data(conv.getLowByte());
    
    // ENQ frame
    frame = CoulterFrame.newEnquiryFrame(getInstrument());
    addFrame(frame);
    // -------------
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

  public void sendFramesArray(boolean createDataWithFrame) throws Exception {
  	int timeOutDelay = getInstrument().getPropertyInteger(InstrumentDesc.FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE);
    if (createDataWithFrame) {
      for (int i = 0; i < getFrameCount(); i++) {
        L3Frame frame = getFrameAt(i);
        if (frame != null) {
          frame.createDataWithFrame();
        }
      }
    }
    int i = 0;
    int numberOfFailures = 0;
    CoulterFrame resFrame = null;
    for (i = 0; i < getFrameCount() && numberOfFailures < MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS; i++) {
      CoulterFrame frame = (CoulterFrame) getFrameAt(i);
      setWaitingForDLE(false);
      if (frame != null) {
        String strToSend = String.valueOf(frame.getDataWithFrame());
        resFrame = null;
        boolean firstTry = true;
        try{
        	if(i == getFrameCount() -1){
        		setWaitingForDLE(true);
        		charDLE = ' ';
        	}else{
        		setWaitingForDLE(false);
        	}
          resFrame = (CoulterFrame)getL3SerialPort().sendAndWaitForResponse(strToSend, timeOutDelay);
        }catch(L3TimeOutException e){
//        	if(frame.getType() == CoulterFrame.FRAME_TYPE_SYN){
//        		throw new L3TryLaterException("Try Later because Time out while waiting for SYN response");
//        	}
        	if(i == 0){
        		throw new L3InstrumentDoesNotRespondTryLaterException();
        	}
      		setWaitingForDLE(false);
          if(firstTry){
          	firstTry = false;
            Globals.logString("SECOND TRY AFTER TIME OUT");
            resFrame = (CoulterFrame)getL3SerialPort().sendAndWaitForResponse(strToSend, timeOutDelay);
          }else{
            throw e;
          }
          Globals.logException(e);
          resFrame.extractDataFromFrame();
          int type = resFrame.getType();
          if (type == CoulterFrame.FRAME_TYPE_ACK || type == CoulterFrame.FRAME_TYPE_ENQ /*|| type == CoulterFrame.FRAME_TYPE_SYN*/) {
            numberOfFailures = 0;
          } else if (type == CoulterFrame.FRAME_TYPE_NACK) {
            numberOfFailures++;
            i--;
          } else if (type == CoulterFrame.FRAME_TYPE_SYN){
          	throw new L3InstrumentDoesNotRespondTryLaterException("Instr Response SYN, Driver will Try sending later, priority to Instr.");          	
          } else {          	
            throw new L3UnexpectedFrameTypeException("Unexpected answer frame type: " + resFrame.getDataWithFrame()+ "\n For request " + frame.getDataWithFrame());
          }
        }
        resFrame.extractDataFromFrame();
        if(resFrame.getType() == CoulterFrame.FRAME_TYPE_SYN && i == 0){
        	throw new L3InstrumentDoesNotRespondTryLaterException("Instr Response SYN, Driver will Try sending later, priority to Instr.");
        }
      }
    }
    
    if(numberOfFailures == MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS){
      throw new L3AnalyzerErrorReturnException("Frame rejected "+ MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS +" times :" + getFrameAt(i).getDataWithFrame());
    }
    if(resFrame.getType() != CoulterFrame.FRAME_TYPE_DLE){
      waitForDLE();
    }
  }
  
  public void setDLE(char charDLE){
  	if(waitingForDLE){
	  	this.charDLE = charDLE;
	  	setWaitingForDLE(false);
  	}
  }
  
  public void setWaitingForDLE(boolean b){
  	waitingForDLE = b;
  }
  
  public void stopWaitingForDLE(){
  	waitingForDLE = false;
  }
  
  public void waitForDLE() throws Exception{
    long time = 0;
    long inc = 100;
    while(waitingForDLE){
    	Thread.sleep(inc);
    	time = time + inc;
    	if(time > 10000){
    		setWaitingForDLE(false);
    		throw new L3Exception("Did not receive DLE response");
    	}
    }
    
    if(charDLE != 'A' && charDLE != 'B'){
    	throw new L3Exception("Could not send this message: DLE = "+charDLE);
    }
  }
  
	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
	}
}

/*
 * Created on May 31, 2006
 */
package b01.l3.drivers.roches.cobas;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import b01.foc.Globals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.exceptions.L3AnalyzerErrorReturnException;
import b01.l3.exceptions.L3NullResponseException;
import b01.l3.exceptions.L3TimeOutException;
import b01.l3.exceptions.L3UnexpectedFrameSequenceException;

/**
 * @author 01Barmaja
 */
public abstract class CobasDriver extends DriverSerialPort implements Runnable {

	protected int    COBAS_INSTRUMENT_TYPE   = 14;
	protected String COBAS_INSTRUMENT_NAME   = "COBAS INTEGRA";
	
	protected abstract boolean isPhysicalMachine();
	public abstract void buildFrameArray(L3Message message) throws Exception;
	protected abstract CobasFrame newResultRequestFrame();
	
	protected abstract L3Sample resultParsing_createSampleFromOrderIdLine(L3Message message, String line);
	protected abstract L3Test resultParsing_createTestFromTestIdLine(L3Sample sample, String line);
	protected abstract void resultParsing_createTestFromTestIdLine(L3Test test, String line);
	
	protected int BLOCK_CODE_SYNCHRONIZATION =  0;
	protected int BLOCK_CODE_ORDER_ENTRY     = 10;
	protected int BLOCK_CODE_RESULT_REQUEST  =  9;
	protected int BLOCK_CODE_RESULT_DATA     =  4;

	protected String LINE_CODE_PATIENT_ID          = "50";
	protected String LINE_CODE_PATIENT_INFORMATION = "51";	
	protected String LINE_CODE_ORDER_ID            = "53";
	protected String LINE_CODE_ORDER_INFORMATION   = "54";
	protected String LINE_CODE_TEST_ID             = "55";
	protected String LINE_CODE_TEST_RESULT         = "00";
	
	public final static char SOH = 1;
	public final static char STX = 2;
	public final static char ETX = 3;
	public final static char EOT = 4;
	public final static char LF  = 10;
	public final static char CR  = 13;
	public final static char SPACE = 32;
	public final static char DC1 = 17;
	public final static char DC3 = 19;

	protected long RESPONSE_TIME_OUT = 30000;
	protected long TIME_DELAY_BETWEEN_RESULT_REQUESTS = 30000;
	
	private int sequence = -1;
	private boolean disableLoopingForResults = false;

	private HashMap<String, String> mapLISLabels2CobasCode = null;
	private HashMap<String, String> mapCobasCode2LISLabels = null;
	
	private CobasFrame askForResult = null;
	
	public CobasDriver() {
	}

	public void dispose() {
		if(mapLISLabels2CobasCode != null){
			mapLISLabels2CobasCode.clear();
			mapLISLabels2CobasCode = null;
		}
		if(mapCobasCode2LISLabels != null){
			mapCobasCode2LISLabels.clear();
			mapCobasCode2LISLabels = null;
		}
		super.dispose();
	}

	protected int getNextSequence() throws Exception {
		if (sequence < 0) {			
			synchronizeSequenceID();			
		} else {
			sequence = (++sequence) % 2;
		}
		return sequence;
	}
	
	protected int getSequenceWithoutIncrement(){
		return sequence;
	}

	public void init(Instrument instr, Properties props) throws Exception {
		super.init(instr, props);
    
    if(!isPhysicalMachine()){
      getInstrument().logString("L3 date:"+(new Date(System.currentTimeMillis())).toString());
      newSecureSerialPort(props);
    }else{
      getInstrument().logString("L3 date : "+(new Date(System.currentTimeMillis())).toString());
      newSerialPort(props);
    }
    
		getL3SerialPort().setAnswerFrame(new CobasFrame(getInstrument()));

		mapLISLabels2CobasCode = new HashMap<String, String>();
		mapCobasCode2LISLabels = new HashMap<String, String>();
		Enumeration enumer = props.keys();
		while (enumer != null && enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			if (key.contains("test.")) {
				mapLISLabels2CobasCode.put(key.substring(5), (String) props.get(key));
				mapCobasCode2LISLabels.put((String) props.get(key), key.substring(5));
			}
		}
		askForResult = newResultRequestFrame();
		
		Thread resultRequestThread = new Thread(this);
		resultRequestThread.start();
	}

	public String getCobasTestCodeFromLisTestLabel(String testLabel){
		return mapLISLabels2CobasCode.get(testLabel);	
	}

	public String getLisTestLabelFromCobasTestCode(String testLabel){
		return mapCobasCode2LISLabels.get(testLabel);	
	}

	public void forceSequenceForUnitTesting() {
		sequence = 0;
	}
  
  public void setDriverReceiver(L3SerialPortListener driverReceiver) {
    super.setDriverReceiver(driverReceiver);
  }

	protected void synchronizeSequenceID() throws Exception {		
		CobasFrame frame = new CobasFrame(getInstrument(), COBAS_INSTRUMENT_TYPE, COBAS_INSTRUMENT_NAME, BLOCK_CODE_SYNCHRONIZATION, 0);
		frame.createDataWithFrame();		
    CobasFrame resFrame = null;
    try{
		  resFrame = (CobasFrame) getL3SerialPort().sendAndWaitForResponse(frame.getDataWithFrame().toString(), RESPONSE_TIME_OUT);
    }catch(Exception e){
      getInstrument().logException(e);
    }
		if (resFrame == null) {
      throw new L3NullResponseException("Null response for SYNC request: "+ frame.getDataWithFrame());
		} else {
			resFrame.extractDataFromFrame();
			sequence = resFrame.getSequence();
		}
	}

	public void send(L3Message message) throws Exception {
		resetFrameArray();

		buildFrameArray(message);
		for (int i = 0; i < getFrameCount(); i++) {
			L3Frame frame = getFrameAt(i);
			if (frame != null) {
				frame.createDataWithFrame();
			}
		}
		for (int i = 0; i < getFrameCount(); i++) {
			L3Frame frame = getFrameAt(i);
			if (frame != null) {
				CobasFrame resFrame = (CobasFrame) getL3SerialPort().sendAndWaitForResponse(frame.getDataWithFrame().toString(), RESPONSE_TIME_OUT);
				resFrame.extractDataFromFrame();
			
				if (resFrame.getSequence() != sequence) {
					throw new L3UnexpectedFrameSequenceException("Transmission error: Unexpected sequence");
				}
			}
		}
	}
	
	private Boolean askForData (CobasFrame frm)throws Exception{
		CobasFrame f;
		Boolean resultFound = false;
    Boolean CobasLogicError = false;
    String errorMessage = "";
		frm.setSequence(getNextSequence());				
		frm.createDataWithFrame();	
    
    try{
  		f = (CobasFrame)(getL3SerialPort().sendAndWaitForResponse(frm.getDataWithFrame().toString(), RESPONSE_TIME_OUT));
  		try{
  		  f.extractDataFromFrame();
      }catch (L3AnalyzerErrorReturnException e){
        CobasLogicError = true;
        errorMessage = e.getMessage();
      }
  		if (f.getSequence() != sequence && errorMessage.equals("")){// il ne faut pas lancer une erreure. il faut voir le frame si c'est un sample result on le traite normalment et on met dans l'attribut message que ce rusult est venu avec unecxpected frame sequance
  			//throw new L3UnexpectedFrameSequenceException("Transmission error: Unexpected sequence");
        errorMessage = "Result updated with unexcpected sequence";
      }else{
      }
  		if (f.getBlockCode() == BLOCK_CODE_RESULT_DATA){
        resultFound = true;
  			// build the message then notify the listener
  			L3Message message = new L3Message();
  			L3Sample sample = null;
  			L3Test test = null;
  			
  			for(int i=0; i<f.lineCount(); i++){
  				String line =(String) f.lineAt(i);
  				if (line.length()>=3){
  					String lineCode = line.substring(0, 2);
  					if (lineCode.equals(LINE_CODE_ORDER_ID)){
  						sample = resultParsing_createSampleFromOrderIdLine(message, line);
              Iterator testIter = sample.testIterator();
              while(testIter != null && testIter.hasNext()){
              	L3Test curTest = (L3Test) testIter.next();
              	curTest.setBlocked(CobasLogicError );
              	curTest.setNotificationMessage(errorMessage);
              }
  					}else if (lineCode.equals(LINE_CODE_TEST_ID)){
  						test = resultParsing_createTestFromTestIdLine(sample, line);
 						}else if (lineCode.equals(LINE_CODE_TEST_RESULT)){
 							resultParsing_createTestFromTestIdLine(test, line);
  					}
  				}
  			}
  			//message.addSample(new L3Sample(f.getDataWithFrame().toString()));
        message.setInstrumentCode(getInstrument().getCode());
  			notifyListeners(message);
  		}
    }catch (L3TimeOutException e){
      Globals.logString("Cobas 400 Integra not responding : " + new Date(System.currentTimeMillis()).toString()); 
      getInstrument().logString("Cobas 400 Integra not responding : " + new Date(System.currentTimeMillis()).toString());
    }
		
		return resultFound;
	}
		
	public void run() {
		while (true) {
      boolean resultFound = true;
      //Adding a condition on the serialPort Variable because the first time it doesn't have the time ti connect
			if (getInstrument().isConnected() && !getL3SerialPort().isSerialPortNull() && !isDisableLoopingForResults()) {
			  if(!reserve()){
          while(resultFound){
            try {
    					resultFound = askForData(askForResult);
    				}catch (Exception e1) {
              release(); // if a problem occurs and the driver is timed out waiting for response, the dirver have to be released
              Globals.logString("dans catch execption et result found : " + resultFound );
              resultFound = false;
              getInstrument().logException(e1);
    				}
          }
  				release();
        }
			}
      
      try {
        Thread.sleep(TIME_DELAY_BETWEEN_RESULT_REQUESTS);
      } catch (InterruptedException e) {
        getInstrument().logException(e);
      }
		}
	}

	public boolean isDisableLoopingForResults() {
		return disableLoopingForResults;
	}

	public void setDisableLoopingForResults(boolean disableLoopingForResults) {
		this.disableLoopingForResults = disableLoopingForResults;
	}
	
	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
	}
}
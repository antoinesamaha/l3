/*
 * Created on May 31, 2006
 */
package b01.l3.drivers.roches;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import b01.l3.drivers.PhMaCh;
import b01.l3.exceptions.L3AnalyzerErrorReturnException;
import b01.l3.exceptions.L3BadOrderAstmDataException;
import b01.l3.exceptions.L3NullResponseException;
import b01.l3.exceptions.L3TimeOutException;
import b01.l3.exceptions.L3UnexpectedFrameSequenceException;

/**
 * @author 01Barmaja
 */
public class Cobas400IntegraDriver extends DriverSerialPort implements Runnable {

	public static int COBAS_400 = 14;
	public static int BLOCK_CODE_SYNCHRONIZATION = 0;
	public static int BLOCK_CODE_ORDER_ENTRY = 10;
	
  public static String LINE_CODE_PATIENT_ID = "50";
  public static String LINE_CODE_PATIENT_INFORMATION = "51";
  public static String LINE_CODE_ORDER_ID = "53";
	public static String LINE_CODE_ORDER_INFORMATION = "54";
	public static String LINE_CODE_TEST_ID = "55";
	public static String LINE_CODE_TEST_RESULT = "00";
	
	public static char SOH = 1;
	public static char STX = 2;
	public static char ETX = 3;
	public static char EOT = 4;
	public static char LF = 10;
	public static char CR = 13;
	public static char SPACE = 32;
	public static char DC1 = 17;
	public static char DC3 = 19;

	public static long RESPONSE_TIME_OUT = 5000;//should be 30000
	public static long TIME_DELAY_BETWEEN_RESULT_REQUESTS = 30000;
	
	private int sequence = -1;
	private boolean disableLoopingForResults = false;

	private HashMap<String, Integer> mapLISLabels2CobasCode = null;
	private HashMap<Integer, String> mapCobasCode2LISLabels = null;
	
	private Cobas400Frame askForResult;
	public Cobas400IntegraDriver() {
	}

	public void dispose() {
		super.dispose();
	}

	private int getNextSequence() throws Exception {
		if (sequence < 0) {			
			synchronizeSequenceID();			
		} else {
			sequence = (++sequence) % 2;
		}
		return sequence;
	}

	public void init(Instrument instr, Properties props) throws Exception {
		super.init(instr, props);
    
    if(!PhMaCh.isPhysicalMachine(new b01.l3.drivers.roches.PhMaInfo())){
      getInstrument().logString("L3 date:"+(new Date(System.currentTimeMillis())).toString());
      newSecureSerialPort(props);
    }else{
      getInstrument().logString("L3 date : "+(new Date(System.currentTimeMillis())).toString());
      newSerialPort(props);
    }
    
		//newSerialPort(props);
		getL3SerialPort().setAnswerFrame(new Cobas400Frame(getInstrument()));

		mapLISLabels2CobasCode = new HashMap<String, Integer>();
		mapCobasCode2LISLabels = new HashMap<Integer, String>();
		Enumeration enumer = props.keys();
		while (enumer != null && enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			if (key.contains("test.")) {
				mapLISLabels2CobasCode.put(key.substring(5), Integer.valueOf((String) props.get(key)));
				mapCobasCode2LISLabels.put(Integer.valueOf((String) props.get(key)), key.substring(5));
			}
		}
		askForResult = new Cobas400Frame(getInstrument(), 9,"COBAS INTEGRA   ",9,sequence);
		askForResult.append2Data("10 04"+Cobas400IntegraDriver.LF);
		
		Thread resultRequestThread = new Thread(this);
		resultRequestThread.start();
	}

	public void forceSequenceForUnitTesting() {
		sequence = 0;
	}
  
  public void setDriverReceiver(L3SerialPortListener driverReceiver) {
    Globals.logString("dans set driver receiver");
   super.setDriverReceiver(driverReceiver);
  }

	protected void synchronizeSequenceID() throws Exception {		
		Cobas400Frame frame = new Cobas400Frame(getInstrument(), COBAS_400, "COBAS INTEGRA", BLOCK_CODE_SYNCHRONIZATION, 0);
		frame.createDataWithFrame();		
    Cobas400Frame resFrame = null;
    try{
		  resFrame = (Cobas400Frame) getL3SerialPort().sendAndWaitForResponse(frame.getDataWithFrame().toString(), RESPONSE_TIME_OUT);
    }catch(Exception e){
      getInstrument().logException(e);
    }
		if (resFrame == null) {
      Globals.logString("dans if de synchronize");
      throw new L3NullResponseException("Null response for request: "+ frame.getDataWithFrame());
		} else {
			resFrame.extractDataFromFrame();
			sequence = resFrame.getSequence();
		}
	}

	public void buildFrameArray(L3Message message) throws Exception {
		// Date
		Cobas400Frame frame = new Cobas400Frame(getInstrument(), COBAS_400, "COBAS INTEGRA",
		BLOCK_CODE_ORDER_ENTRY, getNextSequence());
		Iterator sIter = message.sampleIterator();
		while (sIter != null && sIter.hasNext()) {
			L3Sample sam = (L3Sample) sIter.next();
			if (sam != null){
				// Getting the sample date
				long dateAndTime = sam.getDateAndTime();
				if (dateAndTime == 0) {
					dateAndTime = System.currentTimeMillis();
				}
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String strDateAndTime = dateFormat.format(new Date(dateAndTime));
				// -----------------------

				boolean firstTest = true;

				Iterator tIter = sam.testIterator();
				while (tIter != null && tIter.hasNext()) {
					L3Test test = (L3Test) tIter.next();

					if (firstTest) {
            //Patient id
            frame.append2Data(LINE_CODE_PATIENT_ID);//50
            frame.append2Data(SPACE);
            frame.append2Data(sam.getPatientId());
            frame.append2Data(LF);
            
            //Patient information
            frame.append2Data(LINE_CODE_PATIENT_INFORMATION);//51
            frame.append2Data(SPACE);
            frame.append2Data("00/00/0000");
            frame.append2Data(SPACE);
            String sexe = sam.getSexe();
            if(sexe == ""){
              sexe = "X";
            }
            frame.append2Data(sexe);
            frame.append2Data(SPACE);
            frame.append2Data(sam.getFirstName()+ " "+sam.getMiddleInitial() +" "+sam.getLastName(), 31);
            frame.append2Data(LF);
            
						// Order number
						String orderNumber = sam.getId();
						if (orderNumber == null || orderNumber.length() == 0) {
							throw new L3BadOrderAstmDataException("Order Number is empty");
						} else if (orderNumber.length() > 15) {
							throw new L3BadOrderAstmDataException(
									"Order Number size is grater than 15");
						}

						// Liquid
						int liquidType = sam.getLiquidType();

						// Order line
						// ----------

						frame.append2Data(LINE_CODE_ORDER_ID);//53
						frame.append2Data(SPACE);
						frame.append2Data(orderNumber, 15);
						frame.append2Data(SPACE);
						frame.append2Data(strDateAndTime);

						if (liquidType != L3Sample.LIQUID_TYPE_EMPTY) {
							// frame.append2Data(SPACE);
							// frame.append2Data(liquidType);
						}

						frame.append2Data(LF);
						// ------------

						// Order info & position
						// ---------------------
						frame.append2Data(LINE_CODE_ORDER_INFORMATION);//54
						frame.append2Data(SPACE);
						frame.append2Data("  0  0 S");// Because position is determied by
																					// codebar
						frame.append2Data(LF);
						// ------------
					}    

					// Order line
					// ----------
					// Test id
					String testLabel = test.getLabel();
					int testID = 0;
					if (testLabel == null || testLabel.length() == 0){
						throw new L3BadOrderAstmDataException("Test message is empty");
					}else{
						try{
							testID = mapLISLabels2CobasCode.get(testLabel).intValue();
							// Integer.valueOf(testID).intValue();
						}catch (Exception e) {
							L3BadOrderAstmDataException l3X = new L3BadOrderAstmDataException(
									"Test ID \"" + testID + "\" error", e);
							throw l3X;
						}
					}

					frame.append2Data(LINE_CODE_TEST_ID);//55
					frame.append2Data(SPACE);
					frame.append2Data(String.valueOf(testID), 3);
					frame.append2Data(LF);

					// ------------

					firstTest = false;
				}
			}
		}
		addFrame(frame);
	}

	public void send(L3Message message) throws Exception {
		//reserve();
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
				Cobas400Frame resFrame = (Cobas400Frame) getL3SerialPort().sendAndWaitForResponse(frame.getDataWithFrame().toString(), RESPONSE_TIME_OUT);
				resFrame.extractDataFromFrame();
			
				if (resFrame.getSequence() != sequence) {
					throw new L3UnexpectedFrameSequenceException("Transmission error: Unexpected sequence");
				}
			}
		}
		/*Iterator sampleIterator = message.sampleIterator();
    while (sampleIterator.hasNext()){
      L3Sample sample = (L3Sample)sampleIterator.next();
      sample.updateStatus(L3SampleDesc.SAMPLE_STATUS_ANALYSING);
    }*/
		//release();
	}
	
	public Boolean askForData (Cobas400Frame frm)throws Exception{
		
		Cobas400Frame f;
		Boolean resultFound = false;
    Boolean CobasLogicError = false;
    String errorMessage = "";
		frm.setSequence(getNextSequence());				
		frm.createDataWithFrame();	
		
    
    try{
  		f = (Cobas400Frame)(getL3SerialPort().sendAndWaitForResponse(frm.getDataWithFrame().toString(), RESPONSE_TIME_OUT));
  		try{
  		  f.extractDataFromFrame();
      }catch (L3AnalyzerErrorReturnException e){
        CobasLogicError = true;
        errorMessage = e.getMessage();
      }
  		if (f.getSequence() != sequence && errorMessage.equals("")){// il ne faut pas lancer une erruer. il faut voir le frame si c'est un sample result on le traite normalment et on met dans l'attribut message que ce rusult est venu avec unecxpected frame sequance
  			//throw new L3UnexpectedFrameSequenceException("Transmission error: Unexpected sequence");
        errorMessage = "Result updated with unexcpected sequence";
      }
      
  		if (f.getBlockCode() == 04){
        resultFound = true;
  			// build the message then notify the listener
  			L3Message message = new L3Message();
  			L3Sample sample = null;
  			L3Test test = null;
  			
  			for(int i=0; i<f.lineCount(); i++){
  				String line =(String) f.lineAt(i);
  				if (line.length()>=3){
  					if (line.substring(0, 2).equals(LINE_CODE_ORDER_ID)){
  						sample = new L3Sample(line.substring(3, 18));// avec string label
              sample.setDbResident(false);
              Iterator testIter = sample.testIterator();
              while(testIter != null && testIter.hasNext()){
              	L3Test curTest = (L3Test) testIter.next();
              	curTest.setBlocked(CobasLogicError );
              	curTest.setNotificationMessage(errorMessage);
              }
  						message.addSample(sample);
  						if (line.length()>=29){							
  							Calendar cal = Calendar.getInstance();
  							cal.set(Integer.valueOf(line.substring(25, 29)).intValue(),Integer.valueOf( line.substring(22, 24)).intValue(),Integer.valueOf( line.substring(19, 21)).intValue());
  							sample.setDateAndTime(cal.getTimeInMillis());
  						}
  						/*if (element.length()>=33){
  								sample.setLiquidType(element.substring(30, 33))
  						}*/
  					}else{
  						if (line.substring(0, 2).equals(LINE_CODE_TEST_ID)){
                test = null;
  							String testLabel = line.substring(3, 6).trim();
  							String testLabelInLIS = mapCobasCode2LISLabels.get(Integer.valueOf(testLabel).intValue());
  							if(testLabelInLIS != null){
	  							test = sample.addTest();
	  							test.setLabel(testLabelInLIS);
                }
  						}else{				
  							if (line.substring(0, 2).equals(LINE_CODE_TEST_RESULT)){
  								if(test != null){
	  								test.setValue(Double.valueOf( line.substring(3, 16)).doubleValue());
	  								test.setUnitLabel(line.substring(16,22));
	  								test.setResultOk(true);
  								}
  							}
  						}
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
     // Globals.logString("Dans run de instrument : " + getInstrument().getCode());
      boolean resultFound = true;
      //Adding a condition on the serialPort Variable because the first time it doesn't have the time to connect
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
  				//setBusy(false);		
  				release();
        }
			/*	try {
					Thread.sleep(TIME_DELAY_BETWEEN_RESULT_REQUESTS);
				} catch (InterruptedException e) {
					getInstrument().logException(e);
				}*/
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
	
	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
	}
}
/*
 * Created on May 31, 2006
 */
package b01.l3.drivers.roches.cobas.integra;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import b01.l3.Instrument;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.PhMaCh;
import b01.l3.drivers.roches.cobas.CobasDriver;
import b01.l3.drivers.roches.cobas.CobasFrame;
import b01.l3.exceptions.L3BadOrderAstmDataException;

/**
 * @author 01Barmaja
 */
public class CobasIntegraDriver extends CobasDriver {

	protected int    COBAS_INSTRUMENT_TYPE   = 9;
	protected String COBAS_INSTRUMENT_NAME   = "COBAS INTEGRA";

	protected String LINE_CODE_ORDER_ID      = "53";
	
	private boolean doNotSendPatientInfo = true;
	
	public void init(Instrument instr, Properties props) throws Exception {
		super.init(instr, props);
		String str = props.getProperty("cobas.DoNotSendPatientInfo");
		if(str != null && str.compareTo("1") == 0){
			doNotSendPatientInfo = true;
		}
	}
	
	protected boolean isPhysicalMachine(){
		return PhMaCh.isPhysicalMachine(new b01.l3.drivers.roches.cobas.integra.PhMaInfo());
	}
	
	protected CobasFrame newResultRequestFrame(){
		CobasFrame requestFrame = new CobasFrame(getInstrument(), COBAS_INSTRUMENT_TYPE, COBAS_INSTRUMENT_NAME, BLOCK_CODE_RESULT_REQUEST, getSequenceWithoutIncrement());
		requestFrame.append2Data("10 04"+LF);
		return requestFrame;
	}
	
	protected L3Sample resultParsing_createSampleFromOrderIdLine(L3Message message, String line){
		L3Sample sample = new L3Sample(line.substring(3, 18));// avec string label
    sample.setDbResident(false);
		message.addSample(sample);
		if (line.length()>=29){							
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.valueOf(line.substring(25, 29)).intValue(),Integer.valueOf( line.substring(22, 24)).intValue(),Integer.valueOf( line.substring(19, 21)).intValue());
			sample.setDateAndTime(cal.getTimeInMillis());
		}
		return sample;
	}
	
	protected L3Test resultParsing_createTestFromTestIdLine(L3Sample sample, String line){
		L3Test test = null;
		String testLabel      = line.substring(3, 6).trim();
		String testLabelInLIS = getLisTestLabelFromCobasTestCode(testLabel);
		if(testLabelInLIS != null){
			test = sample.addTest();
			test.setLabel(testLabelInLIS);
		}
		return test;
	}
	
	protected void resultParsing_createTestFromTestIdLine(L3Test test, String line){
		if(test != null){
			test.setValue(Double.valueOf( line.substring(3, 16)).doubleValue());
			test.setUnitLabel(line.substring(16,22));
			test.setResultOk(true);
		}
	}
	
	public void buildFrameArray(L3Message message) throws Exception {
		// Date
		CobasFrame frame = new CobasFrame(getInstrument(), COBAS_INSTRUMENT_TYPE, COBAS_INSTRUMENT_NAME, BLOCK_CODE_ORDER_ENTRY, getNextSequence());
		Iterator sIter = message.sampleIterator();
		while (sIter != null && sIter.hasNext()) {
			L3Sample sam = (L3Sample) sIter.next();
			if (sam != null) {
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
						if(!doNotSendPatientInfo){
	            //Patient id
	            frame.append2Data(LINE_CODE_PATIENT_ID);
	            frame.append2Data(SPACE);
	            frame.append2Data(sam.getPatientId(), 15);
	            frame.append2Data(LF);
	            
	            //Patient information
	            frame.append2Data(LINE_CODE_PATIENT_INFORMATION);
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
						}
						
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

						frame.append2Data(LINE_CODE_ORDER_ID);
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
						frame.append2Data(LINE_CODE_ORDER_INFORMATION);
						frame.append2Data(SPACE);
						frame.append2Data("  0  0 A");// Because position is determied by
																					// codebar
            //Patient Info as USER FIELDS in the machine attached to the order.
						//These fields titles are written under the configuration of the machine
            frame.append2Data(SPACE);
            frame.append2Data(sam.getFirstName() + " " + sam.getLastName(), 21);
            frame.append2Data(SPACE);
            frame.append2Data(String.valueOf(sam.getAge()), 21);					
						
						frame.append2Data(LF);
						// ------------
					}    

					// Order line
					// ----------
					// Test id
					String testLabel = test.getLabel();
					String testID = null;
					if (testLabel == null || testLabel.length() == 0) {
						throw new L3BadOrderAstmDataException("Test message is empty");
					} else {
						try {
							testID = getCobasTestCodeFromLisTestLabel(testLabel);
						} catch (Exception e) {
							L3BadOrderAstmDataException l3X = new L3BadOrderAstmDataException("Test ID \"" + testID + "\" error", e);
							throw l3X;
						}
					}

					if(testID != null){
						frame.append2Data(LINE_CODE_TEST_ID);
						frame.append2Data(SPACE);
						frame.append2Data(testID, 3);
						frame.append2Data(LF);
					}
					
					// ------------

					firstTest = false;
				}
			}
		}
		addFrame(frame);
	}
	
	public boolean isResendAllPendingTests() {
		return false;
	}
}
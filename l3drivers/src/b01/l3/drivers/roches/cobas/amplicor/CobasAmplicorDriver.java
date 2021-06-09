/*
 * Created on May 31, 2006
 */
package b01.l3.drivers.roches.cobas.amplicor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

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
public class CobasAmplicorDriver extends CobasDriver {
	protected int    COBAS_INSTRUMENT_TYPE   = 15;
	protected String COBAS_INSTRUMENT_NAME   = "COBAmplicor HOST";
	
	protected int BLOCK_CODE_RESULT_DATA     =  8;

	protected String LINE_CODE_RESULT_SELECTION = "00";
	protected String LINE_CODE_ORDER_ID         = "01";
	protected String LINE_CODE_DATE_TIME        = "02";
	protected String LINE_CODE_ORDER_RUN_MODE   = "03";
	protected String LINE_CODE_TUBE_POSITION    = "04";
	protected String LINE_CODE_ORDER_TYPE       = "05";
	protected String LINE_CODE_TEST_ID          = "07";
	protected String LINE_CODE_TEST_RESULT      = "13";
	
	protected boolean isPhysicalMachine(){
		return PhMaCh.isPhysicalMachine(new b01.l3.drivers.roches.cobas.amplicor.PhMaInfo());
	}
	
	protected CobasFrame newResultRequestFrame(){
		CobasFrame requestFrame = new CobasFrame(getInstrument(), COBAS_INSTRUMENT_TYPE, COBAS_INSTRUMENT_NAME, BLOCK_CODE_RESULT_REQUEST, getSequenceWithoutIncrement());
		requestFrame.append2Data(LINE_CODE_RESULT_SELECTION);
		requestFrame.append2Data(SPACE);
		requestFrame.append2Data('0');
		requestFrame.append2Data(LF);		
		return requestFrame;
	}

	protected L3Sample resultParsing_createSampleFromOrderIdLine(L3Message message, String line){
		L3Sample sample = new L3Sample(line.substring(3, 9));// avec string label
    sample.setDbResident(false);
		message.addSample(sample);
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
			test.setValue(Float.valueOf( line.substring(3, 13)).floatValue());
			test.setUnitLabel("NA");
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
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
				String strDateAndTime = dateFormat.format(new Date(dateAndTime));
				// -----------------------

				boolean firstTest = true;

				Iterator tIter = sam.testIterator();
				while (tIter != null && tIter.hasNext()) {
					L3Test test = (L3Test) tIter.next();

					if (firstTest) {
						// Order number
						String orderNumber = sam.getId();
						if (orderNumber == null || orderNumber.length() == 0) {
							throw new L3BadOrderAstmDataException("Order Number is empty");
						} else if (orderNumber.length() > 15) {
							throw new L3BadOrderAstmDataException(
									"Order Number size is grater than 15");
						}

						// Order line
						// ----------

						frame.append2Data(LINE_CODE_ORDER_ID);
						frame.append2Data(SPACE);
						frame.append2Data(orderNumber, 6);
						frame.append2Data(LF);
						
						frame.append2Data(LINE_CODE_DATE_TIME);
						frame.append2Data(SPACE);
						frame.append2Data(strDateAndTime);
						frame.append2Data(LF);

						frame.append2Data(LINE_CODE_ORDER_RUN_MODE);
						frame.append2Data(SPACE);
						frame.append2Data('3');
						frame.append2Data(LF);

						frame.append2Data(LINE_CODE_TUBE_POSITION);
						frame.append2Data(SPACE);
						frame.append2Data("00");
						frame.append2Data(LF);

						frame.append2Data(LINE_CODE_ORDER_TYPE);
						frame.append2Data(SPACE);
						frame.append2Data('2');
						frame.append2Data(LF);
						// ------------
					}    

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
						frame.append2Data(String.valueOf(testID), 3);
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
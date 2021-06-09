/*
 * Created on May 7, 2006
 */
package b01.l3.drivers.kermit;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import b01.foc.list.FocList;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.TestLabelMap;
import b01.l3.TestLabelMapDesc;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.PhMaCh;
import b01.l3.drivers.roches.PhMaInfo;
import b01.l3.exceptions.L3UnexpectedFrameSequenceException;
import b01.l3.exceptions.L3UnexpectedFrameTypeException;

/**
 * @author 01Barmaja
 * 
 * We only have to fill the type of the message with the data part other parts
 * are automaticallly completed by the KermitFrame object
 * 
 * The handshake character that is optional is not implemented
 * 
 * We only imlemented the one character checksum
 * 
 */
public class Vitros250Driver extends DriverSerialPort {

	private String[] arrayLISTestLabels = null;

	private HashMap<String, Integer> mapLISLabels2Index = null;

	private int frameSequence = 0;

	public static int RESPONSE_TIMEOUT = 30000;

	/**
	 * 
	 * <code>MAXL_analysor</code> Is the maximum frame length that the analysor
	 * can take. Default value is set to 94
	 * 
	 * <code>MAXL</code> Is the maximum frame length that the computer can take.
	 * The analysor will respect this limit
	 */

	public Vitros250Driver() {
		mapLISLabels2Index = new HashMap<String, Integer>();
		setDriverReceiver(new Vitros250Receiver(this));
	}
  
	public void dispose() {
		super.dispose();

		Vitros250Receiver receiver = (Vitros250Receiver) getDriverReceiver();
		if (receiver != null) {
			receiver.dispose();
			receiver = null;
		}
		arrayLISTestLabels = null;
		mapLISLabels2Index = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.Driver#init(java.util.Properties)
	 */
	public void init(Instrument instrument, Properties props) throws Exception {
		super.init(instrument, props);

		if (!PhMaCh.isPhysicalMachine(new b01.l3.drivers.kermit.PhMaInfo())) {
			getInstrument().logString(
					"L3 date:" + (new Date(System.currentTimeMillis())).toString());
			newSecureSerialPort(props);
		} else {
			getInstrument().logString(
					"L3 date : " + (new Date(System.currentTimeMillis())).toString());
			newSerialPort(props);
		}

		getL3SerialPort().setAnswerFrame(new KermitFrame(getInstrument()));

		String[] arrayVitrosTestStdLabels = newArrayOfVitrosTestLabels();
		arrayLISTestLabels = new String[arrayVitrosTestStdLabels.length];
		for (int i = 0; i < arrayVitrosTestStdLabels.length; i++) {
			String test = arrayVitrosTestStdLabels[i];
			if (test != null) {
				String testLISLabel = props.getProperty(test);
				if (testLISLabel == null || testLISLabel.trim().compareTo("") == 0) {
					testLISLabel = test;
					arrayLISTestLabels[i] = arrayVitrosTestStdLabels[i];
				}
				mapLISLabels2Index.put(testLISLabel, Integer.valueOf(i));
				arrayLISTestLabels[i] = testLISLabel;
			}
		}
	}

	@Override
	public void completeListOfAvailableTests(FocList testList) throws Exception {
		if(testList != null){
			if(arrayLISTestLabels != null){
				for(int i=0; i<arrayLISTestLabels.length; i++){
					String str = arrayLISTestLabels[i];
					if(str != null && str.trim().compareTo("") != 0){
						TestLabelMap testMapper = (TestLabelMap) testList.searchByProperyStringValue(TestLabelMapDesc.FLD_INSTRUMENT_TEST_CODE, str);
						if(testMapper == null){
							testMapper = (TestLabelMap) testList.newEmptyItem();
							testMapper.setCode(str);
							testMapper.setLisTestLabel(str);
							
							char c = VitrosUtil.chr(i);
							if(c != '"'){
								testMapper.setDescription(str+" ("+(int)c+" - '"+c+"')");
							}else{
								testMapper.setDescription(str+" ("+(int)c+")");
							}
							testList.add(testMapper);
						}
					}
				}
			}
		}
	}

	private int getSequenceAndIncrement() {
		frameSequence++;
		frameSequence = frameSequence % 64;
		return frameSequence;
	}

	public void buildFrameArray(L3Message message) throws Exception {
		frameSequence = 0;

		// Start frame
		// -----------
		KermitFrame frame = KermitFrame.newStartFrame(getInstrument());
		addFrame(frame);
		// -----------

		int sampleCount = 0;

		Iterator sIter = message.sampleIterator();
		while (sIter != null && sIter.hasNext()) {
			L3Sample sam = (L3Sample) sIter.next();
			if (sam != null) {
				boolean repeatSampleForSecondaryTests = false;
				do{
					if (sampleCount % 10 == 0) {// Each file can contain 10 samples only
						// File Frame
						// ----------
						long currentTime = System.currentTimeMillis();
						String fineName = String.valueOf(currentTime);
						fineName = fineName.substring(0, 7) + ((int) (sampleCount / 10));
	
						frame = KermitFrame.newFileStartFrame(getInstrument(),
								getSequenceAndIncrement(), fineName);
	
						addFrame(frame);
						// ----------
					}
	
					// We are omiting the tray name and cup
					// It should be entered manually
					
					// Data Frame
					// ----------
					frame = KermitFrame.newSampleFrame(getInstrument(), getSequenceAndIncrement(), message, sam, repeatSampleForSecondaryTests /*onlyForTIBC*/);
					addFrame(frame);
					// ----------
	
					//We check for TIBC tests to send them separately with a sample ID + 'Y'
					//-----------
					if(!repeatSampleForSecondaryTests){
				    Iterator tIter = sam.testIterator(); 
				    while(tIter != null && tIter.hasNext() && !repeatSampleForSecondaryTests){
				      L3Test test = (L3Test) tIter.next();
				      if(test != null){
				      	repeatSampleForSecondaryTests = isTestTIBC(test.getLabel());
				      }
				    }
					}else{
						repeatSampleForSecondaryTests = false;
					}
					//-----------
					
					sampleCount++;
					if (sampleCount % 10 == 0) {
						// File Frame
						// ----------
						frame = KermitFrame.newFileEndFrame(getInstrument(),
								getSequenceAndIncrement());
						addFrame(frame);
						// ----------
					}
					
				}while(repeatSampleForSecondaryTests);
			}
		}

		if (sampleCount % 10 != 0) {
			// File Frame
			// ----------
			frame = KermitFrame.newFileEndFrame(getInstrument(),
					getSequenceAndIncrement());
			addFrame(frame);
			// ----------
		}

		// Break Frame
		// ----------
		frame = KermitFrame.newBreakFrame(getInstrument(),
				getSequenceAndIncrement());
		addFrame(frame);
		// ----------
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.Driver#sendProperties(java.util.Properties)
	 */
	public void send(L3Message message) throws Exception {
		// reserve();
		getInstrument().logString(message.toStringBuffer());
		resetFrameArray();
		buildFrameArray(message);

		sendFramesArray(true);

		// release();
	}

	public void sendFramesArray(boolean createDataWithFrame) throws Exception {

		if (createDataWithFrame) {
			for (int i = 0; i < getFrameCount(); i++) {
				L3Frame frame = getFrameAt(i);
				if (frame != null) {
					frame.createDataWithFrame();
				}
			}
		}

		for (int i = 0; i < getFrameCount(); i++) {
			KermitFrame frame = (KermitFrame) getFrameAt(i);
			if (frame != null) {
				String strToSend = frame.getDataWithFrame().toString();
				KermitFrame resFrame = (KermitFrame) getL3SerialPort()
						.sendAndWaitForResponse(strToSend, RESPONSE_TIMEOUT);
				resFrame.extractDataFromFrame();
				char type = resFrame.getType();
				if (type == KermitFrame.FRAME_TYPE_YES) {
					if (resFrame.getSequence() == frame.getSequence()) {

					} else {
						throw new L3UnexpectedFrameSequenceException(
								"Unexpected answer sequence " + resFrame.getSequence()
										+ " instead of " + frame.getSequence() + "\n request : "
										+ frame.getDataWithFrame() + "\n Answer : "
										+ resFrame.getDataWithFrame());
					}
				} else if (type == KermitFrame.FRAME_TYPE_NO) {
					i--;
				} else {
					throw new L3UnexpectedFrameTypeException(
							"Unexpected answer frame type; " + resFrame.getDataWithFrame()
									+ "\n For request " + frame.getDataWithFrame());
				}
			}
		}
	}

	public void sendYesFrame(int sequence, boolean isYesToStart) throws Exception {
		KermitFrame yesFrame = null;
		if (isYesToStart) {
			yesFrame = KermitFrame.newYesToStartFrame(getInstrument());
		} else {
			yesFrame = KermitFrame.newYesFrame(getInstrument(), sequence);
		}
		yesFrame.createDataWithFrame();
		getL3SerialPort().send(yesFrame.getDataWithFrame().toString());
	}

	public int getTestIndexFromLabel(String label) {
		int index = -1;
		Integer intObj = mapLISLabels2Index.get(label);
		if (intObj != null) {
			index = intObj.intValue();
		}
		return index;
	}

	public boolean isTestTIBC(String label) {
		return label.compareTo("TIBC") == 0;
	}

	public boolean isDerivedTest(int testIndex) {
		return VitrosUtil.chr(testIndex) >= CHAR_CODE_OF_FIRST_DERIVED_TEST;
	}
	
	public boolean isTestPercentageDeSaturation(String label){
		return label.compareTo("%SATU") == 0;
	}

	public String getTestLabelFromIndex(int index) {
		String str = null;
		if(index >= 0 && index < arrayLISTestLabels.length){
			str = arrayLISTestLabels[index];
		}
		return str;
	}

	public static final int CHAR_CODE_OF_FIRST_DERIVED_TEST = 96;
	public String[] newArrayOfVitrosTestLabels() {
		String[] array = { "GLU", "TP", "URIC", "ALB", "TRIG", "CHOL", "AMYL",
				"Cl", "K+", "Na+", "ECO2", "PHOS", "LAC", "NH3", "CREA", "BUN", "HDLC",
				"Bu", "Ca", "TBIL", "AST", "ALKP", "ALT", "LDH", "CK", "LIPA", "GGT",
				"Bc", "THEO", "CKMB", "Mg", "Fe", "TIBC", "PROT", "SALI", "ALC",
				"AMON", "CHE", "ACP", "ACPB", "Li", "DGXN", "PHBR", "PHYT", "CRP",
				"CRBM", "", "ACET", "UPRO", "", "", "", "", "", "CRPJ", "ALTJ", "ASTJ", "dHDL",
				//0  1   2   3   4   5   6      7        8        9       
				"", "", "", "", "", "", "B/CR", "AGPK", "AGP",	"A/G",
				"NBIL", "DBIL", "DELB", "%MB", "OSMO", "%SATU", "GLOB", "", "",	"",
				"LDL", "C/H"};
		return array;
	}

	@Override
	protected void synchronizeSequenceID() throws Exception {
		// TODO Auto-generated method stub

	}

	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
	}
}
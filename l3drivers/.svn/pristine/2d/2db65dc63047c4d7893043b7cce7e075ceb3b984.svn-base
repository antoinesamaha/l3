/*
 * Created on Jun 6, 2006
 */
package b01.l3.drivers.roches.cobas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.exceptions.L3AnalyzerErrorReturnException;
import b01.l3.exceptions.L3CheckSumException;

/**
 * @author 01Barmaja
 */
public class CobasFrame extends L3Frame {

	private static HashMap<String, String> error = null;
	private static final String FRAME_START = String.valueOf(CobasDriver.SOH)+ String.valueOf(CobasDriver.LF);
	private static final String FRAME_END = String.valueOf(CobasDriver.EOT)+ String.valueOf(CobasDriver.LF);

	private int instrumentCode = 14;
	private String instrumentName = "COBAS INSTRUMENT";// Any string of 16 digits
																											// will do
	private int blockCode = 0;
	private int sequence = 0;

	private ArrayList<String> lineList = null;

	public CobasFrame(Instrument instrument, int instrumentCode, String instrumentName,	int blockCode, int sequence) {
		super(instrument);
		this.instrumentCode = instrumentCode;
		this.instrumentName = instrumentName;
		this.blockCode = blockCode;
		this.sequence = sequence;
		fillError();
	}

	public CobasFrame(Instrument instrument) {
		super(instrument);
		this.instrumentCode = 0;
		this.instrumentName = "";
		this.blockCode = 0;
		this.sequence = 0;
		fillError();
	}

	public void dispose() {
		instrumentName = null;
		super.dispose();
	}
  
	public CobasDriver getDriver() throws Exception {
		CobasDriver driver = null;
		Instrument instrument = getInstrument();
		if(instrument != null){
			driver = (CobasDriver) instrument.getDriver();
		}
		return driver; 
	}
	
  private void fillError(){
    if(error == null){
      error = new HashMap<String, String>();

      error.put("96 13", "Instrument error: DataBase Time out");
      error.put("96 22", "Instrument error: Sample id already exists");
      error.put("99 01", "Transmission error: Unexpected Block check sum");
      error.put("99 02", "Transmission error: Unexpected parity");
      error.put("99 06", "Instrument error: Invalid block code");
      error.put("99 07", "Instrument error: Invalid line code");
      error.put("99 08", "Instrument error: Invalid line format");
    }
  }

	public int lineCount(){
		return lineList != null ? lineList.size() : 0;
	}

	public String lineAt(int at){
		return lineList != null ? lineList.get(at) : null;
	}

	public void lineAdd(String line){
		if(lineList == null){
			lineList = new ArrayList<String>();
		}
		lineList.add(line);
	}

	public static int computeCheckSum(StringBuffer buff, int length) {
		int checkSum = 0;
		int max = length < 0 ? buff.length() : length;

		for (int i = 0; i < max; i++) {			
				checkSum += buff.charAt(i);	
		}

		checkSum = checkSum % 1000;
		return checkSum;
	}
	
	private int readSequenceNumber() {
		int seqNumber = -1;
		StringBuffer dataWFrame = getDataWithFrame();
		if (dataWFrame != null && dataWFrame.length() > 8) {
			char[] dst = new char[1];
			dataWFrame.getChars(dataWFrame.length() - 8, dataWFrame.length() - 7,	dst, 0);
			seqNumber = Integer.valueOf(String.valueOf(dst)).intValue();
		}

		return seqNumber;
	}

	private boolean checkCheckSum() {
		int checkSum = -1;
		StringBuffer dataWFrame = getDataWithFrame();
		if (dataWFrame != null && dataWFrame.length() > 6) {
			char[] dst = new char[3];

			dataWFrame.getChars(dataWFrame.length() - 6, dataWFrame.length() - 3,	dst, 0);
			checkSum = Integer.valueOf(String.valueOf(dst).trim()).intValue();
		}
		
		return checkSum == computeCheckSum(dataWFrame, dataWFrame.length() - 6);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.L3Frame#createDataWithFrame()
	 */
	public void createDataWithFrame() throws Exception {
		StringBuffer data = getData();
		StringBuffer dataWithFrame = new StringBuffer();

		setDataWithFrame(dataWithFrame);

		append2DataWithFrame(FRAME_START);

		// Header block
		
		if (instrumentCode < 10) {
			append2DataWithFrame(String.valueOf(0));
		}
		append2DataWithFrame(String.valueOf(instrumentCode));

		append2DataWithFrame(CobasDriver.SPACE);
		append2DataWithFrame(instrumentName, 16);
		append2DataWithFrame(CobasDriver.SPACE);

		if (blockCode < 10) {
			append2DataWithFrame(String.valueOf(0));
		}
		append2DataWithFrame(String.valueOf(blockCode));

		append2DataWithFrame(CobasDriver.LF);
		// ------------

		append2DataWithFrame(CobasDriver.STX);
		append2DataWithFrame(CobasDriver.LF);

		append2DataWithFrame(data);

		append2DataWithFrame(CobasDriver.ETX);
		append2DataWithFrame(CobasDriver.LF);
		
		if (blockCode != getDriver().BLOCK_CODE_SYNCHRONIZATION) {
			append2DataWithFrame(String.valueOf(sequence));
			append2DataWithFrame(CobasDriver.LF);
			
      String checkSum = String.valueOf(computeCheckSum(getDataWithFrame(), -1));
			for (int i=0; i < 3-checkSum.length();i++){
			  append2DataWithFrame(String.valueOf(0));
			}
      //append2DataWithFrame(String.valueOf(computeCheckSum(getDataWithFrame(),	-1)), 3);
      append2DataWithFrame(checkSum, checkSum.length());
			append2DataWithFrame(CobasDriver.LF);
		}

		append2DataWithFrame(FRAME_END);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.L3Frame#extractDataFromFrame()
	 */
	public void extractDataFromFrame() throws Exception {
		
		sequence = readSequenceNumber();
		
		StringTokenizer stt = new StringTokenizer(getDataWithFrame().toString(), String.valueOf(CobasDriver.LF));
		
		//While parsing the lines we only need the line >=3 until the ETX
		//The line 1 gives the block code
		//Before adding each line we check for the error string and break if error 
		//If everything is ok we end up with a lineList filled with the lines from 3 to ETX
		int lineIdx = 0;
		while (stt.hasMoreElements()) {
			String line = stt.nextToken();
			if(line.compareTo(String.valueOf(CobasDriver.ETX)) == 0){
				break;
			}
			if(lineIdx == 1) {
        blockCode = Integer.valueOf(line.substring(20,22)).intValue();
				
				if (blockCode != getDriver().BLOCK_CODE_SYNCHRONIZATION && !checkCheckSum()) {
					throw new L3CheckSumException ("Transmission error: Unexpected check sum");
				}			
			}
			if(lineIdx >= 3){
				String errorMessage = error.get(line);
				if(errorMessage != null){
					throw new L3AnalyzerErrorReturnException(errorMessage);
				}
				
				lineAdd(line);
			}
			lineIdx++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.L3Frame#extractDataFromBuffer(java.lang.StringBuffer)
	 */
	public boolean extractAnswerFromBuffer(StringBuffer buffer) {
		StringBuffer str = buffer;		
		int sIdx = str.indexOf(FRAME_START);
		int eIdx = -1;
		if (sIdx >= 0) {
			eIdx = str.indexOf(FRAME_END);
		}
		
		if (eIdx >= 0 && eIdx > sIdx) {
			StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx + 2));
			setDataWithFrame(response);
			buffer.replace(sIdx, eIdx + 2, "");
		}

		return eIdx > 0;
	}

	public int getBlockCode() {
		return blockCode;
	}

	public void setBlockCode(int blockCode) {
		this.blockCode = blockCode;
	}

	private void append2DataWithLeftBlank(StringBuffer buffer, String inc, int fixedLength) {
		buffer.append(inc);
		for (int i = 0; i < fixedLength - inc.length(); i++) {
			buffer.append(CobasDriver.SPACE);
		}
	}

	public void append2Data(String inc, int fixedLength) {
		append2DataWithLeftBlank(getData(), inc, fixedLength);
	}

	public void append2DataWithFrame(String inc, int fixedLength) {
		append2DataWithLeftBlank(getDataWithFrame(), inc, fixedLength);
	}

	public int getSequence() {
		return sequence;
	}
	
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
}
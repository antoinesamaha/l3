package b01.l3.drivers.hitachi.hitachi912;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.DriverSerialPort;
import b01.l3.L3Frame;
import b01.l3.Instrument;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.data.L3TestDesc;
import b01.l3.exceptions.L3CheckSumException;

public class Hitachi912Frame extends L3Frame {

	public static final int FRAME_TYPE_ERROR = 0;

	// SENDER: HOST
	public static final int FRAME_TYPE_MORE = 1;

	// The host indicates that it wants to suspend the communication with the CU
	// for a specified time
	// because it is neither possible to respond to test selection inquiry nor
	// possible to receive analytical data
	public static final int FRAME_TYPE_RECEIVED = 2;

	// SENDER: CU
	public static final int FRAME_TYPE_ANY = 3;

	public static final int FRAME_TYPE_FRAME1 = 4;

	public static final int FRAME_TYPE_FRAME2 = 5;

	public static final int FRAME_TYPE_END = 6;

	// SENDER: CU - HOST
	public static final int FRAME_TYPE_SPECIFIC_TS = 7;

	public static final int FRAME_TYPE_REPEAT = 8;

	public static final int FRAME_TYPE_SUSPEND = 9;

	// Frame character
	public static final char ANY_MOR = '>'; // 1 - 3

	public static final char REC = 'A'; // 2

	public static final char FR1 = '1'; // 4

	public static final char FR2 = '2'; // 5

	public static final char END = ':'; // 6

	public static final char SPE = ';'; // 7

	public static final char REP = '?'; // 8

	public static final char SUS = '@'; // 9

	// FRAME NOT USED
	public static final char RES = '<'; // RESULT REQUEST FROM HOST TO CU

	// Function Character
	public static final char TS_ROUTINE_SAMPLE = 'A';

	private int type;

	public static final char space = 32;

	public static final int FRAME_EXACT_SIZE = 512;

	public static final int TEST_NUMBER_IN_FRAME = (FRAME_EXACT_SIZE - 47) / 9;

	public Hitachi912Frame(Instrument instrument, int type) {
		super(instrument);
		this.type = type;
	}

	public void dispose() {
		super.dispose();
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static Hitachi912Frame newMOReFrame(Instrument instrument) {
		Hitachi912Frame frame = new Hitachi912Frame(instrument, FRAME_TYPE_MORE);
		frame.append2Data(ANY_MOR);
		return frame;
	}

	public static Hitachi912Frame newRECeivedFrame(Instrument instrument) {
		Hitachi912Frame frame = new Hitachi912Frame(instrument, FRAME_TYPE_RECEIVED);
		frame.append2Data(REC);
		return frame;
	}

	public static Hitachi912Frame newSPEcificTestSelectionFrame(Instrument instrument, L3Sample sample, DriverSerialPort driver) {
		Hitachi912Frame frame = new Hitachi912Frame(instrument, FRAME_TYPE_SPECIFIC_TS);
		frame.append2Data(SPE);
		frame.append2Data(TS_ROUTINE_SAMPLE);

		String sampleType = "1";
		switch (sample.getLiquidType()) {
		case L3Sample.LIQUID_TYPE_SERUM:
			sampleType = "1";
			break;
		case L3Sample.LIQUID_TYPE_URIN:
			sampleType = "2";
			break;
		case L3Sample.LIQUID_TYPE_CSF:
			sampleType = "3";
			break;
		case L3Sample.LIQUID_TYPE_STOOL:// SPRNT
			sampleType = "4";
			break;
		case L3Sample.LIQUID_TYPE_BODY_FLUID:// OTHER
			sampleType = "5";
			break;
		}
		frame.append2Data(sampleType);

		String seqNb = sample.getId();
		if (seqNb.length() >= 5 && !seqNb.equals("10000")) {
			seqNb = sample.getId().substring(sample.getId().length() - 4);
		}
		frame.append2Data(addSpaces(seqNb, 5));

		for (int i = 0; i < 4; i++) {
			frame.append2Data(space);
		}

		frame.append2Data(addSpaces(sample.getId(), 13));

		frame.append2Data(addSpaces(String.valueOf(sample.getAge()), 3));
		frame.append2Data("3"); // AgeFactor 3= year

		if (sample.getSexe().compareTo("M") == 0) {
			frame.append2Data("1");
		} else if (sample.getSexe().compareTo("F") == 0) {
			frame.append2Data("2");
		} else {
			frame.append2Data("0");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MMddyyHHmm");
		if (sample.getEntryDate().getTime() < 24 * 60 * 60 * 1000) {
			frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
		} else {
			frame.append2Data(sdf.format(sample.getEntryDate()));
		}

		frame.append2Data("48");
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < 49; i++) {
			str.append("0");
		}
		Iterator tIter = sample.testIterator();
		while (tIter != null && tIter.hasNext()) {
			L3Test test = (L3Test) tIter.next();
			String lisTesLabel = String.valueOf(test.getPropertyInteger(L3TestDesc.FLD_LABEL));
			String instrumentCode = driver.testMaps_getInstCode(lisTesLabel);
			int pos = 0;
			try{
				pos = Integer.valueOf(instrumentCode);
			}catch(NumberFormatException e){
				Globals.logString("!Integer Format exception while parsing instrumentCode '"+instrumentCode+"' for lisTestLabel '"+lisTesLabel+"'");
				Globals.logException(e);
			}
			if (pos > 0 && pos < str.length()) {
				str.setCharAt(pos - 1, '1');
			}
		}
		frame.append2Data(str.toString(), 48);

		// Comments
		frame.append2Data("100000");
		String patientName = sample.getFirstName() + " " + sample.getLastName();
		frame.append2Data(patientName);
		for (int i = 0; i < 30 - patientName.length(); i++) {
			frame.append2Data(space);
		}
		Globals.logString("frame: " + frame.toString());
		return frame;
	}

	public static Hitachi912Frame newREPeatFrame(Instrument instrument) {
		Hitachi912Frame frame = new Hitachi912Frame(instrument, FRAME_TYPE_REPEAT);
		frame.append2Data(REP);
		return frame;
	}

	public static Hitachi912Frame newSUSpendFrame(Instrument instrument) {
		Hitachi912Frame frame = new Hitachi912Frame(instrument, FRAME_TYPE_SUSPEND);
		frame.append2Data(SUS);
		return frame;
	}

	@Override
	public void createDataWithFrame() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ASCII.STX);
		StringBuffer bufferForChecksum = new StringBuffer();
		bufferForChecksum.append(String.valueOf(getData()));
		String checkSum = computeChecksum(bufferForChecksum);
		buffer.append(bufferForChecksum);
		buffer.append(ASCII.ETX);
		buffer.append(checkSum);
		buffer.append(ASCII.CR);
		setDataWithFrame(buffer);
	}

	@Override
	public void extractDataFromFrame() throws Exception {
		// [STX];N1 [STX]:q1[ETX]D6[CR]
		StringBuffer dataWithFrame = getDataWithFrame();
		if (dataWithFrame.length() > 0) {
			char strChar = dataWithFrame.toString().charAt(1);
			if (strChar == ANY_MOR) {
				setType(Hitachi912Frame.FRAME_TYPE_ANY);
			} else if (strChar == REP) {
				setType(Hitachi912Frame.FRAME_TYPE_REPEAT);
			} else if (strChar == SUS) {
				setType(Hitachi912Frame.FRAME_TYPE_SUSPEND);
			} else { // FRAME_TYPE_FRAME1 or FRAME_TYPE_FRAME2 or FRAME_TYPE_END
				if (strChar == FR1) {
					setType(Hitachi912Frame.FRAME_TYPE_FRAME1);
				} else if (strChar == FR2) {
					setType(Hitachi912Frame.FRAME_TYPE_FRAME2);
				} else {
					setType(Hitachi912Frame.FRAME_TYPE_END);
				}

				StringBuffer data = new StringBuffer(dataWithFrame.substring(1, dataWithFrame.length() - 4));
				setData(data);

				// BEGIN Verify the checksum
				StringBuffer bufferForChecksum = new StringBuffer(dataWithFrame);
				bufferForChecksum.replace(0, 1, "");
				bufferForChecksum.replace(dataWithFrame.length() - 5, dataWithFrame.length(), "");
				char[] realCheckSum = new char[2];
				realCheckSum[0] = dataWithFrame.charAt(dataWithFrame.length() - 3);
				realCheckSum[1] = dataWithFrame.charAt(dataWithFrame.length() - 2);
				String computedCheckSum = computeChecksum(bufferForChecksum);

				if (realCheckSum[0] != computedCheckSum.charAt(computedCheckSum.length() - 2) || realCheckSum[1] != computedCheckSum.charAt(computedCheckSum.length() - 1)) {
					logString("buffer that was checked:" + bufferForChecksum);
					throw new L3CheckSumException("Checksum exception: expected:" + String.valueOf(computedCheckSum) + " found:" + String.valueOf(realCheckSum));
				}
				// END Verify the checksum
			}
		}
	}

	@Override
	public boolean extractAnswerFromBuffer(StringBuffer buffer) {
		String str = buffer.toString();
		boolean extractionDone = false;
		String start = String.valueOf(ASCII.STX);
		String end = String.valueOf(ASCII.CR);
		int sIdx = str.lastIndexOf(start);
		int eIdx = -1;
		if (sIdx >= 0) {
			eIdx = str.lastIndexOf(end);
		}
		if (eIdx > 0 && eIdx > sIdx) {
			StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx + 1));
			setDataWithFrame(response);
			buffer.replace(0, eIdx + 1, "");
			extractionDone = true;
		}
		return extractionDone;
	}

	// FRAME UTILITY
	public static String addSpaces(String str, int maxLenght) {
		StringBuffer newString = new StringBuffer();
		int strLenght = str.length();
		int diff = maxLenght - strLenght;

		if (str != null && strLenght < maxLenght) {
			for (int i = 0; i < diff; i++) {
				newString.append(" ");
			}
		} else {
			str = str.substring(0, maxLenght);
		}
		newString.append(str);
		return newString.toString();
	}

	public String computeChecksum(StringBuffer strBuffer) {
		String parity;
		char highLow[] = new char[2];
		highLow = calculateHighLow(strBuffer);
		parity = String.valueOf(highLow[0]);
		parity += String.valueOf(highLow[1]);
		return parity;
	}

	public char[] calculateHighLow(StringBuffer strBuffer) {
		char parity[] = new char[2];

		byte bt[] = strBuffer.toString().getBytes();

		// Calculate Checksum high/low
		int sum = 0;
		for (int i = 0; i < bt.length; i++) {
			sum += bt[i];
		}
		String strHEXA = convert_Decimal_To_Hexadecimal(sum);
		parity[0] = strHEXA.charAt(strHEXA.length() - 2);
		parity[1] = strHEXA.charAt(strHEXA.length() - 1);
		return parity;
	}

	public char calculateBlockCheckCharacter(StringBuffer strBuffer) {
		char char1 = strBuffer.charAt(0);
		for (int i = 1; i < strBuffer.length(); i++) {
			char char2 = strBuffer.charAt(i);
			char1 = (char) (char1 ^ char2);
		}
		return char1;
	}

	public String convert_Decimal_To_Hexadecimal(int w) {
		int rem;
		String output = "";
		String digit;
		String backwards = "";

		do {
			rem = w % 16;
			digit = DtoHex(rem);
			w = w / 16;
			output += digit;
		} while (w / 16 != 0);

		rem = w % 16;
		digit = DtoHex(rem);
		output = output + digit;
		for (int i = output.length() - 1; i >= 0; i--) {
			backwards += output.charAt(i);
		}
		return backwards;
	}

	public String DtoHex(int rem) {
		String str1 = String.valueOf(rem);
		if (str1.equals("10"))
			str1 = "A";
		else if (str1.equals("11"))
			str1 = "B";
		else if (str1.equals("12"))
			str1 = "C";
		else if (str1.equals("13"))
			str1 = "D";
		else if (str1.equals("14"))
			str1 = "E";
		else if (str1.equals("15"))
			str1 = "F";
		return str1;
	}

}
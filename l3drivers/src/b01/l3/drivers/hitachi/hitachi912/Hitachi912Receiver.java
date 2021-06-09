/*
 * Created on Jun 14, 2006
 */
package b01.l3.drivers.hitachi.hitachi912;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.exceptions.L3UnexpectedFrameTypeException;

/**
 * @author 01Barmaja
 */
public class Hitachi912Receiver implements L3SerialPortListener {
	protected Hitachi912Driver driver = null;

	protected L3Message message = null;

	protected L3Sample sample = null;

	protected L3Test test = null;

	private Hitachi912Frame morFrame = null; // to send positive response

	private Hitachi912Frame recFrame = null; // to send interruption request

	private Hitachi912Frame susFrame = null; // to send negative response

	private boolean WaitingMoreData = false;

	public Hitachi912Receiver(Hitachi912Driver driver) throws Exception {
		this.driver = driver;
		message = null;
		sample = null;
		test = null;

		morFrame = Hitachi912Frame.newMOReFrame(driver.getInstrument());
		morFrame.createDataWithFrame();

		recFrame = Hitachi912Frame.newRECeivedFrame(driver.getInstrument());
		recFrame.createDataWithFrame();

		susFrame = Hitachi912Frame.newSUSpendFrame(driver.getInstrument());
		susFrame.createDataWithFrame();
	}

	public void disposeMessage() {
		if (message != null) {
			message.dispose();
		}
		message = null;
		sample = null;
		test = null;
	}

	public void dispose() {
		driver = null;
		disposeMessage();
	}

	public Hitachi912Driver getDriver() {
		return driver;
	}

	protected void initMessage() {
		disposeMessage();
		message = new L3Message();
	}

	protected void sendMessageBackToInstrument() {
		driver.notifyListeners(message);
	}

	protected void parseResultFrame(StringBuffer data, int frameType) {
		// 1n1[.][.][.][.]70[.]7[.][.][.][.][.][.][.][.][.][.][.][.][.]K[.][.][.][.]01127071416[.][.][.][.][.][.]19[.]1[.][.][.][.]41[.][.]2[.][.][.]112[.][.]3[.][.][.]150[.][.]4[.][.][.]8.1[.][.]5[.][.]1.11[.][.]6[.][.][.]177[.][.]7[.][.][.]9.2[.][.]8[.][.][.]3.7[.]11[.][.][.][.]43[.]12[.][.][.][.]24[.]13[.][.][.][.]35[.]14[.][.][.]213[.]15[.][.][.][.]80[.]28[.][.]24.8[.]34[.][.][.][.]33[.]47[.][.][.]141[.]48[.][.][.]4.2[.]49[.][.][.]105[.]57[.][.][.][.]82[.]
		// 1- Frame Identification (1 byte: data.charAt(0): 1 => FR1)
		// 2a- Function Character (2 bytes: n1)
		//BAntoineS
		//int sampleType = Integer.valueOf(data.charAt(2));
		int sampleType = Integer.valueOf(data.substring(2, 3));
		//EAntoineS		
		// 2b- Sample Information (37 bytes)
		String sampleId = data.substring(3, 8).trim();
		String diskNb = String.valueOf(data.charAt(8));
		String Pos = data.substring(9, 11);
		String idNb = data.substring(12, 25).trim();
		String age = data.substring(25, 29);
		String sex = String.valueOf(data.charAt(29));
		String date = data.substring(30, 36);
		String time = data.substring(36, 40);
		// 3- Result Data (Operator ID (6 bytes) + variable length)
		String OpId = data.substring(40, 46);
		int NbofRes = Integer.valueOf(data.substring(46, 48).trim());
		Globals.logString("sampleType: " + sampleType + " / sampleId: " + sampleId + " / diskNb: " + diskNb + " / Pos: " + Pos + " / idNb: " + idNb + " / age: " + age + " / sex: " + sex + " / date: " + date + " / time: " + time + " / OPID: " + OpId + " / NbOfResults: " + NbofRes);

		if(sampleId == null || sampleId.compareTo("") == 0){
			sampleId = idNb;
		}
		
		sample = new L3Sample(sampleId);
		sample.setLiquidType(sampleType);
		sample.setPatientId(idNb);
		sample.setSexe(sex);
		message.addSample(sample);

		/*
		 * Format: ccvvvvvva cc: Test no. Range: b1 - 46 Photometry assay 47 - 49
		 * Electrolyte (Na, K and Cl) 50 - 52 Serum index (lipemia, hemolysis and
		 * icterus) 53 - 60 Calculated tests vvvvvv: Result value a: Data alarm
		 */
		int diff = 9;
		int startPos = 48;

		for (int i = 0; i < NbofRes; i++) {
			int endPos = startPos + diff;
			String resultData = data.substring(startPos, endPos).toString();

			String testNo = resultData.substring(0, 2);
			String strValue = resultData.substring(2, 8);
			if (strValue.trim().compareTo("") != 0) {
				double value = Double.valueOf(resultData.substring(2, 8));
				Globals.logString("testNo: " + testNo + " value: " + value);
				String lisTestLabel = driver.testMaps_getLisCode(testNo.replaceAll("" + ASCII.SPACE, ""));

				if (lisTestLabel != null && lisTestLabel.trim().compareTo("") != 0) {
					test = sample.addTest();
					test.setLabel(lisTestLabel);
					test.setResultOk(true);
					test.setValue(value);
				}
			}

			startPos = endPos;
		}

		if (frameType == Hitachi912Frame.FRAME_TYPE_END) {
			// 4- Comment Information (6 to 126 bytes)
			int endCommentFlagPos = startPos + 6;
			String commentFlags = data.substring(startPos, endCommentFlagPos);
			if (commentFlags.charAt(0) == '1') {
				String patientName = data.substring(endCommentFlagPos, endCommentFlagPos + 30).trim();
				Globals.logString("patient Name: " + patientName);
				sample.setFirstName(patientName);
				sample.setLastName(patientName);
			}
		}
	}

	protected void treatReceivedFrame(Hitachi912Frame receivedFrame) throws Exception {
		if (receivedFrame != null) {
			try {
				receivedFrame.extractDataFromFrame();
				switch (receivedFrame.getType()) {
				case Hitachi912Frame.FRAME_TYPE_ANY:
					Globals.logString("Received ANY: MOR frame to send");
					driver.sendOneFrame(morFrame);
					if (!WaitingMoreData) {
						driver.release();
					}
					break;
				case Hitachi912Frame.FRAME_TYPE_SUSPEND:
					if (driver.reserve()) { // not ok
						Globals.logString("Received SUSPEND: could not reserve driver");
					} else {
						Globals.logString("Received SUSPEND: driver reserved to interrupt sending to CU");
					}
					break;
				case Hitachi912Frame.FRAME_TYPE_REPEAT:
				case Hitachi912Frame.FRAME_TYPE_SPECIFIC_TS:
					if (driver.reserve()) { // not ok
						Globals.logString("REP or SPE received: SUS frame to send: could not reserve driver");
					} else {
						Globals.logString("REP or SPE received: SUS frame to send: driver reserved to interrupt sending to CU");
					}
					driver.sendAndTreatOneFrame(susFrame, true);
					throw new L3UnexpectedFrameTypeException("Unexpected received frame type: " + receivedFrame.getDataWithFrame() + "\n");
				case Hitachi912Frame.FRAME_TYPE_FRAME1:
				case Hitachi912Frame.FRAME_TYPE_FRAME2:
				case Hitachi912Frame.FRAME_TYPE_END:
					boolean reserved = false;

					if (WaitingMoreData) { // FR1 or FR2 already received => driver
																	// already reserved by this thread and new
																	// frame is FR2 or END
						reserved = true;
					} else {
						if (!driver.reserve()) { // ok
							reserved = true;
						} else {
							driver.sendAndTreatOneFrame(susFrame, true);
						}
					}
					if (reserved) {
						// ok
						Globals.logString("Received FR1/FR2/END: REC frame to send");
						driver.sendOneFrame(recFrame);

						initMessage();
						parseResultFrame(receivedFrame.getData(), receivedFrame.getType());
						sendMessageBackToInstrument();

						if (receivedFrame.getType() == Hitachi912Frame.FRAME_TYPE_END) {
							driver.release();
							WaitingMoreData = false;
						} else {
							WaitingMoreData = true;
							Globals.logString("MOR frame to send to get more data");
							driver.sendOneFrame(morFrame);
						}
					}
					break;
				}
			} catch (Exception e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.connection.L3SerialPortListener#received(b01.l3.L3Frame)
	 */

	public void received(L3Frame frame) {
		try {
			treatReceivedFrame((Hitachi912Frame) frame);
		} catch (Exception e) {
			Globals.logException(e);
		}
	}

}

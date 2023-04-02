/*
 * Created on Jun 14, 2006
 */
package b01.l3.drivers.astm;

import java.util.StringTokenizer;

import b01.foc.ConfigInfo;
import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.exceptions.L3UnexpectedFrameSequenceException;

/**
 * @author 01Barmaja
 */
public class AstmReceiver implements L3SerialPortListener {
	protected AstmDriver driver = null;
	protected L3Message message = null;
	protected L3Sample sample = null;
	protected L3Test test = null;

	private int expectedSequence = -1;

	private AstmFrame ackFrame = null;
	private AstmFrame nackFrame = null;

	private PatientLineReader patientLineReader = null;
	private OrderLineReader orderLineReader = null;
	private CommentLineReader commentLineReader = null;
	private ResultLineReader resultLineReader = null;
	private CommentResultReader commentResultReader = null;
	private InformationInquiryReader informationEnquiryReader = null;

	private StringBuffer concatenationBuffer = null;

	private int previousAnswer = 0;
	private String previousFrame = null;
	private char lastFrameTypeBeforeComment = AstmFrame.FRAME_TYPE_NONE;

	public AstmReceiver(AstmDriver driver) {
		this.driver = driver;
		message = null;
		sample = null;
		test = null;

		patientLineReader = new PatientLineReader();
		orderLineReader = new OrderLineReader(driver.getAstmParams().getSampleID_FieldPosition(), driver.getAstmParams().getSampleID_ComponentPosition());
		commentLineReader = new CommentLineReader();
		resultLineReader = new ResultLineReader(driver);
		commentResultReader = new CommentResultReader();
		informationEnquiryReader = new InformationInquiryReader();
	}

	public void disposeMessage() {
		if (message != null) {
			message.dispose();
		}
		message = null;
		sample = null;
		test = null;

		disposePreviousMessage();
	}

	public void disposePreviousMessage() {
		previousFrame = null;
	}

	public void disposeConcatenationBuffer() {
		concatenationBuffer = null;
	}

	public void dispose() {
		driver = null;

		disposeMessage();
		disposeConcatenationBuffer();

		if (ackFrame != null) {
			ackFrame.dispose();
			ackFrame = null;
		}

		if (nackFrame != null) {
			nackFrame.dispose();
			nackFrame = null;
		}

		if (patientLineReader != null) {
			patientLineReader.dispose();
			patientLineReader = null;
		}

		if (orderLineReader != null) {
			orderLineReader.dispose();
			orderLineReader = null;
		}

		if (commentLineReader != null) {
			commentLineReader.dispose();
			commentLineReader = null;
		}

		if (resultLineReader != null) {
			resultLineReader.dispose();
			resultLineReader = null;
		}

		if (commentResultReader != null) {
			commentResultReader.dispose();
			commentResultReader = null;
		}

		if (informationEnquiryReader != null) {
			informationEnquiryReader.dispose();
			informationEnquiryReader = null;
		}
	}

	public AstmDriver getDriver() {
		return driver;
	}
	
	public void setResultLineReader(ResultLineReader resultLineReader) {
		this.resultLineReader = resultLineReader;
	}
	
	public void setCommentResultReader(CommentResultReader commentResultReader) {
		this.commentResultReader = commentResultReader;
	}

	private StringBuffer getConcatenationBuffer() {
		if (concatenationBuffer == null) {
			concatenationBuffer = new StringBuffer();
		}
		return concatenationBuffer;
	}

	private AstmFrame getAckFrame() {
		if (ackFrame == null) {
			ackFrame = AstmFrame.newAcknowlegeFrame(driver.getInstrument());
			try {
				ackFrame.createDataWithFrame();
			} catch (Exception e) {
				ackFrame.logException(e);
			}
		}
		return ackFrame;
	}

	private AstmFrame getNackFrame() {
		if (nackFrame == null) {
			nackFrame = AstmFrame.newNotAcknowlegFrame(driver.getInstrument());
			try {
				nackFrame.createDataWithFrame();
			} catch (Exception e) {
				nackFrame.logException(e);
			}
		}
		return nackFrame;
	}

	private void setExpectedSequence(int sequence) {
		expectedSequence = sequence;
	}

	private void resetExpectedSequence() {
		expectedSequence = -1;
	}

	private int getNextExpectedSequence() {
		int next = expectedSequence + 1;
		if (next == 8)
			next = 0;
		return next;
	}

	private int incrementExpectedSequence() {
		expectedSequence++;
		if (expectedSequence == 8)
			expectedSequence = 0;
		return expectedSequence;
	}

	protected void parsePatientFrame(StringBuffer data) {
		patientLineReader.scanTokens(data);
	}

	protected L3Test addTest(L3Sample sample, String lisTestCode){
		L3Test test = sample.addTest();
		test.setLabel(lisTestCode);
		return test;
	}
	
	protected void parseOrderFrame(StringBuffer data) {
		orderLineReader.scanTokens(data);

		Globals.logDebug("- Parsed sample ID : " + orderLineReader.getSampleId());
		if (sample != null) {
			Globals.logDebug("- Current sample ID : " + sample.getId());

			// if(sample != null && sample.getId() != null &&
			// orderLineReader.getSampleId() != null ){
			if (sample.getId().compareTo(orderLineReader.getSampleId()) != 0) {
				sample = null;
				sample = message.findSample(orderLineReader.getSampleId());
				if (sample != null) {
					Globals.logDebug("- Found sample ID : " + sample.getId());
				} else {
					Globals.logDebug("- sample ID Not Found ");
				}
				test = null;
			}
		}
		if (sample == null) {
			sample = new L3Sample(orderLineReader.getSampleId());
			sample.setFirstName(patientLineReader.getFirstName());
			sample.setLastName(patientLineReader.getLastName());
			sample.setMiddleInitial(patientLineReader.getMidInitial());
			message.addSample(sample);
		}

		String lisTestCode = driver.testMaps_getLisCode(orderLineReader.getTestLabel());
		if (lisTestCode != null && lisTestCode.trim().compareTo("") != 0) {
			if (driver.getAstmParams().isCheckResultFrameTestCodeWithOrderFrameTestCode()) {
				test = addTest(sample, lisTestCode);
			}
		}
	}

	private void pushCommentAsMessage(String comment) {
		// Il faudrait le faire ds getSqlString() plutot
		if (comment != null && comment.contains("\'")) {
			int idxQuote = comment.indexOf('\'');
			String str = comment.substring(0, idxQuote + 1) + "\'" + comment.substring(idxQuote + 1);
			comment = str;
		}
		if (comment != null && !comment.isEmpty()) {
			sample.pushMessage(getDriver().getInstrument(), comment);
		}
	}

	protected void parseCommentFrame(StringBuffer data) {
		if (driver.getAstmParams().isReadAnyComment()) {
			commentLineReader.scanTokens(data);

			if (getDriver().getAstmParams().isReadComment1()) {
				String comment1 = commentLineReader.getComment1();
				pushCommentAsMessage(comment1);
			}

			if (getDriver().getAstmParams().isReadComment2()) {
				String comment2 = commentLineReader.getComment2();
				pushCommentAsMessage(comment2);
			}

			if (getDriver().getAstmParams().isReadComment3()) {
				String comment3 = commentLineReader.getComment3();
				pushCommentAsMessage(comment3);
			}

			/*
			 * if(comment3.isEmpty()) comment3 =
			 * commentLineReader.getComment1(); // Il faudrait le faire ds
			 * getSqlString() plutot if (comment3!=null &&
			 * comment3.contains("\'")) { int idxQuote = comment3.indexOf('\'');
			 * String str =
			 * comment3.substring(0,idxQuote+1)+"\'"+comment3.substring
			 * (idxQuote+1); comment3 = str; }
			 * sample.pushMessage(getDriver().getInstrument(), comment3);
			 */
		}
	}

	protected void parseInformationInquiryFrame(StringBuffer data) {
		if (getDriver().isInquiryBased() && informationEnquiryReader != null) {
			informationEnquiryReader.scanTokens(data);
		}
	}
	
	protected void parseResultFrame(StringBuffer data) {
		resultLineReader.setSample(sample);
		resultLineReader.setTest(test);
		resultLineReader.scanTokens(data);
		test = resultLineReader.getTest();
	}

	protected void parseCommentAfterResultFrame(StringBuffer data) {
		if (driver.getAstmParams().isReadResultComment()) {
			commentResultReader.scanTokens(data);
			char alarmCode = commentResultReader.getCommentOnResult();
			if (test != null && alarmCode != 0) {
				String alarmMsg = "" + alarmCode;
				if (!test.getNotificationMessage().equals("")) {
					alarmMsg = test.getNotificationMessage() + ", " + alarmCode;
				}
				test.setNotificationMessage(alarmMsg);
			}

		}
	}

	protected void initMessage() {
		message = new L3Message();
	}

	protected void sendMessageBackToInstrument() {
		driver.notifyListeners(message);
	}

	protected void treatResultFrame(AstmFrame frame) {
		StringBuffer data = frame.getData();

		if (frame.getType() == AstmFrame.FRAME_TYPE_HEADER) {
			if(getDriver().isInquiryBased()) {
				informationEnquiryReader.clear();
			}
			
		} else if (frame.getType() == AstmFrame.FRAME_TYPE_LAST) {
			// sendMessageBackToInstrument();
			// disposeMessage();
		} else if (frame.getType() == AstmFrame.FRAME_TYPE_PATIENT) {
			// initMessage();
			parsePatientFrame(data);
		} else if (frame.getType() == AstmFrame.FRAME_TYPE_INFORMATION_INQUIRY) {
			Globals.logString("Frame Type Q Enquiry detected");
			if(getDriver().isInquiryBased()) parseInformationInquiryFrame(data);			
		} else if (frame.getType() == AstmFrame.FRAME_TYPE_ORDER) {
			Globals.logString("Frame Type O Order detected");
			parseOrderFrame(data);
		} else if (frame.getType() == AstmFrame.FRAME_TYPE_RESULT) {
			parseResultFrame(data);
		} else if (frame.getType() == AstmFrame.FRAME_TYPE_COMMENT) {
			if (lastFrameTypeBeforeComment == AstmFrame.FRAME_TYPE_ORDER) {
				parseCommentFrame(data);
			} else if (lastFrameTypeBeforeComment == AstmFrame.FRAME_TYPE_RESULT) {
				parseCommentAfterResultFrame(data);
			}

		}

		if (frame.getType() != AstmFrame.FRAME_TYPE_COMMENT) {
			lastFrameTypeBeforeComment = frame.getType();
		}
	}
	
	private int executionAfterReceivingENQ_ReserveAndResetSequence(){
		int frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
		Globals.logDetail("Receive ENQ");
		if (driver.reserve(true)) {
			driver.getInstrument().logString("Returning NACK because driver is Reserved");
			frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
		} else {
			disposeConcatenationBuffer();
			getConcatenationBuffer();
			resetExpectedSequence();
			getNextExpectedSequence();
			frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
		}
		return frameTypeToReturn;
	}
	
	private boolean isAcceptToStartWithHeaderDirectly(){
		return getDriver() != null && getDriver().getAstmParams() != null && getDriver().getAstmParams().isAcceptToStartAtFrame1Directly();
	}

	protected int concatenateResultFrame(AstmFrame frame) {
		int frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
		try {
			frame.extractDataFromFrame();

			int expectedSequence = getNextExpectedSequence();
			if (frame.getSequence() != AstmFrame.SEQUENCE_IRRELEVANT && expectedSequence != frame.getSequence()) {
				if (isAcceptToStartWithHeaderDirectly() && frame.getType() == AstmFrame.FRAME_TYPE_HEADER) {
					frameTypeToReturn = executionAfterReceivingENQ_ReserveAndResetSequence();
					if(frameTypeToReturn == AstmFrame.FRAME_TYPE_ACK){
						setExpectedSequence(frame.getSequence());
					}else{
						throw new L3UnexpectedFrameSequenceException("Received Header without ENQ but could not reserve driver!");
					}
				} else {
					throw new L3UnexpectedFrameSequenceException("Sequence expected : " + expectedSequence + " found:" + frame.getSequence());
				}
			}

			if (frame.getType() == AstmFrame.FRAME_TYPE_ENQ) {
				frameTypeToReturn = executionAfterReceivingENQ_ReserveAndResetSequence();
//				Globals.logDetail("Receive ENQ");
//				if (driver.reserve()) {
//					driver.getInstrument().logString("Returning NACK because driver is Reserved");
//					frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
//				} else {
//					disposeConcatenationBuffer();
//					getConcatenationBuffer();
//					resetExpectedSequence();
//					getNextExpectedSequence();
//					frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
//				}
			} else if (frame.getType() == AstmFrame.FRAME_TYPE_EOT) {
				Globals.logDetail("Receive EOT");
				frameTypeToReturn = AstmFrame.FRAME_TYPE_NONE;

				Globals.logDebug("Concatenation Buffer = " + getConcatenationBuffer());

				disposeMessage();

				StringTokenizer strTokenizer = new StringTokenizer(getConcatenationBuffer().toString(), String.valueOf(ASCII.CR), false);
				while (strTokenizer.hasMoreTokens()) {
					String token = strTokenizer.nextToken();
					if (ConfigInfo.isLogDebug()) {
						Globals.logDebug("Concatenated Frame : " + ASCII.convertNonCharactersToDescriptions(token));
					}
					AstmFrame concatFrame = new AstmFrame(driver.getInstrument());
					if (token.length() > 0) {
						concatFrame.setDataWithFrame(new StringBuffer(token));
						concatFrame.extractDataFromConcatenatedFrame();
						if (message == null) {
							initMessage();
						}
						Globals.logDebug("Treat result frame");
						treatResultFrame(concatFrame);
					}
				}
				sendMessageBackToInstrument();
				disposeMessage();
				disposeConcatenationBuffer();

				driver.release();

				Globals.logDebug("Calling Respond to Inquiry if necessary");
				respondToInquiryIfNecessary();

			} else {
				StringBuffer sb = getConcatenationBuffer();
				frame.concatenateToBuffer(sb);
			}
		} catch (Exception e) {
			driver.getInstrument().logException(e);
			frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
		}

		return frameTypeToReturn;
	}

	public void respondToInquiryIfNecessary() {
		if (		getDriver() != null && getDriver().isInquiryBased()
				&&  informationEnquiryReader != null
				&& 	informationEnquiryReader.getSampleId() != null
				&& !informationEnquiryReader.getSampleId().isEmpty()
				&&  informationEnquiryReader.getSampleIdAttrib().equals("B")
				) {
			Globals.logDebug("Calling sendASampleAnsweringInquiry : "+informationEnquiryReader.getRackNumber() +" "+ informationEnquiryReader.getTubePosition() +" "+ informationEnquiryReader.getSampleId());
			Instrument instrument = driver.getInstrument();
			instrument.sendASampleAnsweringInquiry(informationEnquiryReader.getRackNumber(), informationEnquiryReader.getTubePosition() ,informationEnquiryReader.getSampleId());
		} else {
			if (getDriver() == null || !getDriver().isInquiryBased()){
				Globals.logDebug("Will not respond to Enquiry: Driver not Inquiry Based");
			} else if (informationEnquiryReader.getSampleId() == null || informationEnquiryReader.getSampleId().isEmpty()){
				Globals.logDebug("Will not respond to Enquiry: Sample Id is empty");
			} else if (informationEnquiryReader.getSampleIdAttrib() != null){
				Globals.logDebug("Will not respond to Enquiry: Sample Id Attribute "+informationEnquiryReader.getSampleIdAttrib());
			} else {
				Globals.logDebug("Will not respond to Enquiry: Other reason");
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
			AstmFrame f = (AstmFrame) frame;
			if (f != null) {
				String newFrameStr = f.getDataWithFrame().toString();
				int answer = AstmFrame.FRAME_TYPE_NONE;
				boolean repeatedFrame = false;

				if (previousFrame != null && newFrameStr.toString().equals(previousFrame)) {
					answer = previousAnswer;
					if (answer == AstmFrame.FRAME_TYPE_NACK)
						driver.getInstrument().logString("Sending NACK because received a repeated message");
					repeatedFrame = true;
				} else {
					answer = concatenateResultFrame(f);
				}

				switch (answer) {
				case AstmFrame.FRAME_TYPE_ACK:
					if (!repeatedFrame){
						incrementExpectedSequence();
					}
					
					if(!f.isDoNotAnswerThatFrameBecauseReceivedAnotherMessage()){
						driver.send(getAckFrame().getDataWithFrame().toString());
					}else{
						Globals.logString("Was Sending : "+AstmFrame.ACK);
					}
					break;
				case AstmFrame.FRAME_TYPE_NACK:
					if(!f.isDoNotAnswerThatFrameBecauseReceivedAnotherMessage()){
						driver.getInstrument().logString("sending NACK from AstmReceiver.received(L3Frame frame)");
						driver.send(getNackFrame().getDataWithFrame().toString());
					}else{
						Globals.logString("Was Sending : "+AstmFrame.NACK);
					}
					break;
				case AstmFrame.FRAME_TYPE_NONE:
					break;
				}

				previousFrame = newFrameStr;
				previousAnswer = answer;
				// If the previous frame was ENQ and the previous answer is NACK
				// we do not repeat the answer without checking driver
				// availability.
				// Otherwise we get stuck
				if (f.getType() == AstmFrame.FRAME_TYPE_ENQ && previousAnswer == AstmFrame.FRAME_TYPE_NACK) {
					previousFrame = null;
					previousAnswer = 0;
				}
			}
		} catch (Exception e) {
			Globals.logException(e);
		}
	}
	
	public void unitTesting_treatResultFrame(AstmFrame frame) {
		if (message == null) {
			initMessage();
		}
		treatResultFrame(frame);
	}
	
	public void unitTesting_PrintMessage() {
		if(message != null) {
			for(int i=0; i<message.getNumberOfSamples(); i++) {
				L3Sample sample = message.getSample(i);
				Globals.logString("- Sample: ID:"+sample.getId()+" FN:"+sample.getFirstName()+" LN:"+sample.getLastName());
				for(int t=0; t<sample.getTestListWithoutLoad().size(); t++) {
					L3Test test = (L3Test) sample.getTestListWithoutLoad().getFocObject(t);
					if(test != null) {
						Globals.logString("  - Test: Lbl:"+test.getLabel()+" Value:"+test.getValue()+" Notes:"+test.getValueNotes()+" Alarm:"+test.getAlarm());
					}
				}
			}
		}
	}

	public CommentResultReader getCommentResultReader() {
		return commentResultReader;
	}
}

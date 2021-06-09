package b01.l3.drivers.hitachi.hitachi912;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import b01.foc.Globals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.L3ConfigInfo;
import b01.l3.L3Frame;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.drivers.PhMaCh;
import b01.l3.exceptions.L3AnalyzerErrorReturnException;
import b01.l3.exceptions.L3TimeOutException;
import b01.l3.exceptions.L3TryLaterException;
import b01.l3.exceptions.L3UnexpectedFrameTypeException;

public class Hitachi912Driver extends DriverSerialPort {

	public Hitachi912Driver() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.Driver#init(java.util.Properties)
	 */
	public void init(Instrument instrument, Properties props) throws Exception {
		if (L3ConfigInfo.getDebugMode()) {
			Globals.getDisplayManager().popupMessage("01BARMAJA you are in Debug MODE !!!!");
		}
		setDriverReceiver(new Hitachi912Receiver(this));
		super.init(instrument, props);

		if (!PhMaCh.isPhysicalMachine(new b01.l3.drivers.hitachi.hitachi912.PhMaInfo())) {
			getInstrument().logString("L3 date: " + (new Date(System.currentTimeMillis())).toString());
			newSecureSerialPort(props);
		} else {
			getInstrument().logString("L3 date : " + (new Date(System.currentTimeMillis())).toString());
			newSerialPort(props);
		}
		testMaps_fillFromProperties(props);
		Hitachi912Frame answerFrame = new Hitachi912Frame(getInstrument(), Hitachi912Frame.FRAME_TYPE_ANY);
		getL3SerialPort().setAnswerFrame(answerFrame);
	}

	@Override
	protected void synchronizeSequenceID() throws Exception {
	}

	public void treatReplyFromCU(Hitachi912Frame frame, Hitachi912Frame replyFromCU, boolean firstTry) throws Exception {
		if (replyFromCU != null) {
			replyFromCU.extractDataFromFrame();
			switch (replyFromCU.getType()) {
			case Hitachi912Frame.FRAME_TYPE_ANY:
				// Expected answer, SPE sent OK or REC sent OK or MOR sent OK or REC
				// sent OK
				// driver will not reply => CU will send another ANY for retry
				// communication that will be catched by the driverListener
				Globals.logString("Received ANY: frame well sent");
				break;
			case Hitachi912Frame.FRAME_TYPE_REPEAT:
				// The CU sends REP only when the response from the host is invalid or
				// destroyed.
				if (firstTry) {
					Globals.logString("Received REP: SECOND TRY");
					sendAndTreatOneFrame(frame, false);
					break;
				} else {
					throw new L3AnalyzerErrorReturnException("Frame rejected (sent twice)");
				}
			case Hitachi912Frame.FRAME_TYPE_SUSPEND:
				// SPE must be resend after a new ANY/MOR cycle
				// driver will not reply => CU will send another ANY for retry
				// communication that will be catched by the driverListener
				Globals.logString("Received SUS: Wait for CU to restore communication");
				throw new L3TryLaterException();
			default:
				throw new L3UnexpectedFrameTypeException("Unexpected answer frame type: " + replyFromCU.getDataWithFrame() + "\n");
			}
		}
	}

	public void sendAndTreatOneFrame(Hitachi912Frame frame, boolean firstTry) throws Exception {
		if (frame != null) {
			int timeOutDelay = getInstrument().getPropertyInteger(InstrumentDesc.FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE);
			try {
				Hitachi912Frame replyFrame = (Hitachi912Frame) getL3SerialPort().sendAndWaitForResponse(frame.getDataWithFrame().toString(), timeOutDelay);
				treatReplyFromCU(frame, replyFrame, firstTry);
			} catch (L3TimeOutException e) {
				// No answer from CU => SPE must be resend after a new ANY/MOR cycle
				// (restore communication)
				// driver will not reply => CU will send another ANY for retry
				// communication that will be catched by the driverListener
				Globals.logString("Reception time-out error occurred: Retry Later");
				throw new L3TryLaterException();
			}
		}
	}

	public void sendOneFrame(Hitachi912Frame frame) throws Exception {
		if (frame != null) {
			getL3SerialPort().send(frame.getDataWithFrame().toString());
		}
	}

	public void sendFramesArray(boolean createDataWithFrame) throws Exception {
		L3Frame l3Frame = getFrameAt(0); // SPE
		if (l3Frame != null) {
			if (createDataWithFrame) {
				l3Frame.createDataWithFrame();
			}
			Hitachi912Frame frame = (Hitachi912Frame) l3Frame;
			sendAndTreatOneFrame(frame, true);
		}
		//BAntoineS
		l3Frame = getFrameAt(1); // More
		if (l3Frame != null) {
			Hitachi912Frame frame = (Hitachi912Frame) l3Frame;
			l3Frame.createDataWithFrame();
			sendOneFrame(frame);
		}
		//EAntoineS
	}

	public void buildFrameArray(L3Message message, boolean fromDriver) throws Exception {
		// Test Selection Frame From Host
		int sampleSequence = 1;
		Iterator sIter = message.sampleIterator();
		while (sIter != null && sIter.hasNext()) {
			L3Sample sample = (L3Sample) sIter.next();
			if (sample != null) {
				Hitachi912Frame frame = Hitachi912Frame.newSPEcificTestSelectionFrame(getInstrument(), sample, this);
				addFrame(frame);
				sampleSequence++;
			}
		}
		//BAntoineS
		Hitachi912Frame frame = Hitachi912Frame.newMOReFrame(getInstrument());
		addFrame(frame);
		//EAntoineS
	}

	public void send(L3Message message) throws Exception {
		if (getInstrument().isLogBufferDetails()) {
			getInstrument().logString("Inst code : " + message.sampleIterator());
		}
		resetFrameArray();
		buildFrameArray(message, true);
		/*
		 * Hitachi912Frame recFrame =
		 * Hitachi912Frame.newRECeivedFrame(getInstrument());
		 * recFrame.createDataWithFrame(); sendAndTreatOneFrame(recFrame, true);
		 */
		//BAntoineS
		Hitachi912Frame frame = Hitachi912Frame.newMOReFrame(getInstrument());
		frame.createDataWithFrame();
		sendAndTreatOneFrame(frame, true);
		//EAntoineS
		
		sendFramesArray(true);
	}

	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
		// TODO Auto-generated method stub
		
	}
}
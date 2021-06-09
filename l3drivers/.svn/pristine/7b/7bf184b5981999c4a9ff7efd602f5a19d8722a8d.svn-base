package b01.l3.drivers.hitachi.hitachi912;

import java.util.Iterator;
import java.util.Properties;

import b01.foc.Globals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.emulator.EmulatorRobot;
import b01.l3.emulator.IEmulator;

public class Hitachi912Emulator extends Hitachi912Driver implements IEmulator {
	EmulatorRobot robot = null;

	public void init(Instrument instrument, Properties props) throws Exception {
		super.init(instrument, props);
		setDriverReceiver(new Hitachi912EmulatorReceiver(this));
		robot = new EmulatorRobot(this, props);
	}

	public void dispose() {
		if (robot != null) {
			robot.dispose();
			robot = null;
		}
	}

	/*
	 * public void treatCurrentlyAnalysedSample(FocList
	 * currentlyAnalysedSampleList) { int sampleSequence = 0; L3Message message =
	 * null;
	 *  // convert the sample to a message try { // get all the samples Iterator
	 * sampleIterator = currentlyAnalysedSampleList.focObjectIterator(); if
	 * (sampleIterator != null && sampleIterator.hasNext()) { L3Sample sample =
	 * (L3Sample) sampleIterator.next(); // get all the tests of the sample
	 * FocList orderList = sample.getTestList(); Iterator testIterator =
	 * orderList.focObjectIterator(); sampleSequence++; while(testIterator != null &&
	 * testIterator.hasNext()) { L3Test test = (L3Test) testIterator.next();
	 * double rVal = rand.nextDouble(); while(rVal == 0) rVal = rand.nextDouble();
	 * test.setValue((rVal * 10.0)); } message = new L3Message();
	 * message.addSample(sample); resetFrameArray(); buildFrameArray(message,
	 * false); sendFramesArray(true); } } catch (Exception e) {
	 * getInstrument().logException(e); } }
	 */

	public EmulatorRobot getRobot() {
		return robot;
	}

	public String testMaps_getInstCode(String lisCode) {
		String str = null;
		try {
			str = ((DriverSerialPort) getRobot().getRelatedInstrument().getDriver()).testMaps_getInstCode(lisCode);
		} catch (Exception e) {
			Globals.logException(e);
		}
		return str;
	}

	@Override
	public void buildFrameArray(L3Message message, boolean fromDriver) throws Exception {
		// ENQ frame
		// -----------
		// Hitachi912Frame frame = Hitachi912Frame.newANYFrame(getInstrument());
		// addFrame(frame);
		// -----------

		// Test Selection Frame Host Sender
		int sampleSequence = 1;
		Iterator sIter = message.sampleIterator();
		while (sIter != null && sIter.hasNext()) {
			L3Sample sam = (L3Sample) sIter.next();
			if (sam != null) {
				int testCount = sam.getTestList().size();
				int maxTestInFrame = Hitachi912Frame.TEST_NUMBER_IN_FRAME;
				int diff = testCount - maxTestInFrame;

				int startTestNumber = 0;
				int endTestNumber = Math.abs(diff);

				while (diff > 0) {
					// frame = Hitachi912Frame.newResult_CUSender(getInstrument(),
					// Hitachi912Frame.FRAME_TYPE_RESULT, startTestNumber, endTestNumber,
					// sam, 0, 0, 0, "User", "", "");
					// addFrame(frame);
					endTestNumber = diff;
					diff++;
					startTestNumber = (testCount - diff);

					diff = testCount - maxTestInFrame;
				}
				// frame = Hitachi912Frame.newResult_CUSender(getInstrument(),
				// Hitachi912Frame.FRAME_TYPE_END, startTestNumber, testCount, sam, 0,
				// 0, 0, "User", "", "");
				// addFrame(frame);
			}
			sampleSequence++;
		}
	}

	/*
	 * //ENQ frame // ----------- Hitachi912Frame frame =
	 * Hitachi912Frame.newANY_MORE_Frame(getInstrument()); addFrame(frame); //
	 * -----------
	 *  // Test Selection Frame Host Sender int sampleSequence = 1; Iterator sIter =
	 * message.sampleIterator(); while (sIter != null && sIter.hasNext()) {
	 * L3Sample sam = (L3Sample) sIter.next(); if (sam != null) { frame =
	 * Hitachi912Frame.newResult_CUSender(getInstrument(), sam, 0, 0, 0, "User",
	 * "", ""); addFrame(frame); sampleSequence++; } } }
	 */

	public void sendMessageWithResults(L3Message message) throws Exception {
		resetFrameArray();
		buildFrameArray(message, false);
		sendFramesArray(true);
	}
}

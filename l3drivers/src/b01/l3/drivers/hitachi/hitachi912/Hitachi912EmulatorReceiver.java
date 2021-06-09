package b01.l3.drivers.hitachi.hitachi912;

import b01.l3.L3Frame;

public class Hitachi912EmulatorReceiver extends Hitachi912Receiver {

	public Hitachi912EmulatorReceiver(Hitachi912Emulator driver) throws Exception {
		super(driver);
	}

	// @Override
	protected void parseResultFrame(StringBuffer data) {
	}

	@Override
	protected void initMessage() {
	}

	@Override
	protected void sendMessageBackToInstrument() {
	}

	public void received(L3Frame frame) {
		((Hitachi912Emulator) getDriver()).getRobot().predefinedFrame_Treat(frame);
		super.received(frame);
	}

}

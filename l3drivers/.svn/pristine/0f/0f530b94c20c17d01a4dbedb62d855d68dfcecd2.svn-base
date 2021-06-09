package b01.l3.drivers.dadeBehring.bct;

import b01.l3.drivers.astm.AstmDriver;

public class BCTDriver extends AstmDriver {
	public BCTDriver(){
		super();
		frameCreator = new BCTFrameCreator();
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.dadeBehring.bct.PhMaInfo());
	}

	@Override
	protected void initDriverReceiver() {
		setDriverReceiver(new BCTReceiver(this));
	}

	@Override
	protected void initAnswerFrame() {
		BCTFrame answerFrame = new BCTFrame(getInstrument());
		getL3SerialPort().setAnswerFrame(answerFrame);
	}

	@Override
	public boolean isResendAllPendingTests() {
		return true;
	}
}

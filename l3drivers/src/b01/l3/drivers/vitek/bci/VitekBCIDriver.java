package b01.l3.drivers.vitek.bci;

import java.util.Properties;

import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.dadeBehring.bct.BCTFrame;
import b01.l3.drivers.dadeBehring.bct.BCTReceiver;

public class VitekBCIDriver extends AstmDriver {
	public VitekBCIDriver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new VitekBCIFrameCreator();
		getAstmParams().setResultFrame_ComponentPositionForResultType(-1);
		getAstmParams().setConcatenatedFrames(true);
		//getAstmParams().setTestCodeLength(3);
		//getAstmParams().setIgnoreLastTestCodeDigit(true);
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.vitek.bci.PhMaInfo());
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
	}
	
	@Override
	public void init(Instrument instrument, Properties props) throws Exception {
		if(props != null){
			props.put("tcpip", "1");
		}
		super.init(instrument, props);
	}	

	@Override
	protected void initDriverReceiver() {
		setDriverReceiver(new VitekBCIReceiver(this));
	}

	@Override
	protected void initAnswerFrame() {
		VitekBCIFrame answerFrame = new VitekBCIFrame(getInstrument());
		getL3SerialPort().setAnswerFrame(answerFrame);
	}
	
}

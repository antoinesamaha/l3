package b01.l3.drivers.octa;

import b01.foc.Globals;
import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.astm.AstmReceiver;

import java.util.Properties;

public class OctaDriver extends AstmDriver{
	public OctaDriver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new OctaFrameCreator();
		
		getAstmParams().setResultFrame_ComponentPositionForResultType(-7);
		getAstmParams().setTestCodeLength(3);
		getAstmParams().setPhysicalMachineInfo(new PhMaInfo());
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
		getAstmParams().setConcatenatedFrames(true);
		getAstmParams().setReadComment3(true);
		getAstmParams().setReadResultComment(true);
		getAstmParams().setTreatHigherLessAlarmSeparately(false);
	}
	
	@Override
	public void init(Instrument instrument, Properties props) throws Exception {
		Globals.logString("Init OctaDriver");
		if(props != null){
			Globals.logString("OctaDriver Set to TCPIP");
			props.put("tcpip", "1");
		}
		super.init(instrument, props);

		OctaFrame answerFrame = new OctaFrame(instrument);
		getL3SerialPort().setAnswerFrame(answerFrame);
	}

	protected void initDriverReceiver() {
		Globals.logString("OctaDriver initDriverReceiver");
		setDriverReceiver(new OctaReceiver(this));
	}
}


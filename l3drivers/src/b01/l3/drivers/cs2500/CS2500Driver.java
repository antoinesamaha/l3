package b01.l3.drivers.cs2500;

import java.util.Properties;

import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmDriver;

public class CS2500Driver extends AstmDriver{
	public CS2500Driver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new CS2500FrameCreator();
		
		getAstmParams().setResultFrame_ComponentPositionForResultType(-7);
		getAstmParams().setTestCodeLength(3);
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.cs2500.PhMaInfo());
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
		getAstmParams().setConcatenatedFrames(true);
		getAstmParams().setReadComment3(true);
		getAstmParams().setReadResultComment(true);
		getAstmParams().setTreatHigherLessAlarmSeparately(false);
	}
	
	@Override
	public void init(Instrument instrument, Properties props) throws Exception {
		if(props != null){
			props.put("tcpip", "1");
		}
		super.init(instrument, props);
	}

}


package b01.l3.drivers.maglumi;

import java.util.Properties;

import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmDriver;

public class MaglumiDriver extends AstmDriver{
	public MaglumiDriver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new MaglumiFrameCreator();
		
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
		if(props != null){
			props.put("tcpip", "1");
		}
		super.init(instrument, props);
	}

}


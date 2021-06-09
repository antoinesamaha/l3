package b01.l3.drivers.roches.cobas.u601;

import java.util.Properties;

import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.astm.AstmReceiver;

public class CobasU601Driver extends AstmDriver {
	public CobasU601Driver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new CobasU601FrameCreator();

		getAstmParams().setResultFrame_ComponentPositionForResultType(1);
		getAstmParams().setTestCodeLength(3);
		getAstmParams().setIgnoreLastTestCodeDigit(false);
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.roches.cobas.u601.PhMaInfo());
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
		getAstmParams().setSendCommentFrameFromHost(false);
		//20160129-B
		getAstmParams().setDoNotSendOrdersBecauseOneWay(true);
		//20160129-E
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
		super.initDriverReceiver();
		initReceiver(0);
	}
	
	protected void initReceiver(int posForTestCode) {
		AstmReceiver astmReceiver = (AstmReceiver) getDriverReceiver();
		astmReceiver.setResultLineReader(new CobasU601_ResultLineReader(this, posForTestCode));		
	}
}

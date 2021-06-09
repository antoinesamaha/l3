package b01.l3.drivers.hitachi.elecsys2010;

import b01.l3.drivers.astm.AstmDriver;

public class ElecsysDriver extends AstmDriver {
	public ElecsysDriver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new ElecsysFrameCreator();

		getAstmParams().setResultFrame_ComponentPositionForResultType(-1);
		getAstmParams().setTestCodeLength(3);
		getAstmParams().setIgnoreLastTestCodeDigit(true);
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.hitachi.elecsys2010.PhMaInfo());
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
	}
}

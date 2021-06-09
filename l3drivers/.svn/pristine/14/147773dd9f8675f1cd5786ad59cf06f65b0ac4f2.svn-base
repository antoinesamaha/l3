package b01.l3.drivers.roches.cobas.e411;

import b01.l3.drivers.astm.AstmDriver;

public class CobasE411Driver extends AstmDriver {
	public CobasE411Driver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new CobasE411FrameCreator();

		getAstmParams().setResultFrame_ComponentPositionForResultType(-1);
		getAstmParams().setTestCodeLength(3);
		getAstmParams().setIgnoreLastTestCodeDigit(false);
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.roches.cobas.e411.PhMaInfo());
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
		getAstmParams().setSendCommentFrameFromHost(false);
	}
}

package b01.l3.drivers.roches.cobas.cobas501;

import b01.l3.drivers.astm.AstmDriver;

public class Cobas501Driver extends AstmDriver {
	public Cobas501Driver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new Cobas501FrameCreator();

		getAstmParams().setSendPatientIdToInstrument(true);
		getAstmParams().setResultFrame_ComponentPositionForResultType(-1);
		getAstmParams().setTestCodeLength(3);
		getAstmParams().setIgnoreLastTestCodeDigit(false);
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.roches.cobas.cobas501.PhMaInfo());
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
		getAstmParams().setSendCommentFrameFromHost(false);
		getAstmParams().setReadComment3(true);
		getAstmParams().setReadResultComment(true);		
		getAstmParams().setConcatenatedFrames(true);
	}
}

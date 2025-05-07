package b01.l3.drivers.alcor.ised;

import b01.l3.drivers.astm.AstmDriver;

public class ISEDDriver extends AstmDriver {
	public ISEDDriver() {
		super();
//		if(frameCreator != null){
//			frameCreator.dispose();
//			frameCreator = null;
//		}
//		frameCreator = new ISEDFrameCreator();
		
		getAstmParams().setDoNotSendOrdersBecauseOneWay(true);
		getAstmParams().setResultFrame_ComponentPositionForResultType(-7);
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.alcor.ised.PhMaInfo());
		getAstmParams().setTestCodeLength(3);
		
//		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
//		getAstmParams().setConcatenatedFrames(true);
//		getAstmParams().setReadComment3(true);
//		getAstmParams().setReadResultComment(true);
//		getAstmParams().setTreatHigherLessAlarmSeparately(false);
	}
}


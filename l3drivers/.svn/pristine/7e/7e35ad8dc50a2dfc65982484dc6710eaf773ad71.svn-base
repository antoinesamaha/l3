package b01.l3.drivers.roches.cobas.c311;

import b01.l3.drivers.astm.AstmDriver;

public class CobasC311Driver extends AstmDriver{
	public CobasC311Driver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new CobasC311FrameCreator();
		
		getAstmParams().setResultFrame_ComponentPositionForResultType(-7);
		getAstmParams().setTestCodeLength(3);
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.abbott.modular.PhMaInfo());
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
		getAstmParams().setConcatenatedFrames(true);
		getAstmParams().setReadComment3(true);
		getAstmParams().setReadResultComment(true);		
		getAstmParams().setTreatHigherLessAlarmSeparately(false);
		
//		getAstmParams().setSampleID_FieldPosition(2);
//		getAstmParams().setSampleID_ComponentPosition(1);
	}
}


package b01.l3.drivers.abbott.axsym;

import b01.l3.drivers.astm.AstmDriver;

public class AxsymDriver extends AstmDriver {
	public AxsymDriver(){
		super();
		frameCreator.dispose();
  	frameCreator = new AxsymFrameCreator();
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.abbott.axsym.PhMaInfo());
	}
}

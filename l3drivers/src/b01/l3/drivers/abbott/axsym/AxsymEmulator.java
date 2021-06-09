package b01.l3.drivers.abbott.axsym;

import b01.l3.drivers.astm.AstmEmulator;

public class AxsymEmulator extends AstmEmulator{
	public AxsymEmulator(){
		super();
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.abbott.axsym.PhMaInfo());
	}
}

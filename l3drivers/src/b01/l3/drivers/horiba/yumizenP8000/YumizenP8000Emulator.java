package b01.l3.drivers.horiba.yumizenP8000;

import b01.l3.drivers.astm.AstmEmulator;

public class YumizenP8000Emulator extends AstmEmulator{
	public YumizenP8000Emulator(){
		super();
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.abbott.axsym.PhMaInfo());
	}
}

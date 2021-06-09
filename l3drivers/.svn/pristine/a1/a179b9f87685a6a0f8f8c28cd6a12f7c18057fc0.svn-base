package b01.l3.drivers.dadeBehring.bcs;

import java.util.Properties;

import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.dadeBehring.bct.BCTDriver;
import b01.l3.drivers.dadeBehring.bct.BCTFrameCreator;
import b01.l3.drivers.dadeBehring.bct.BCTReceiver;

public class BCSDriver extends AstmDriver {
	public BCSDriver(){
		super();
		frameCreator = new BCSFrameCreator();
		getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.dadeBehring.bcs.PhMaInfo());
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
		setDriverReceiver(new BCSReceiver(this));
	}
}

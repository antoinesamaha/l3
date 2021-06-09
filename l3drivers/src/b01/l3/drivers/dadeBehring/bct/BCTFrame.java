package b01.l3.drivers.dadeBehring.bct;

import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmFrame;

public class BCTFrame extends AstmFrame{

	public BCTFrame(Instrument instrument, int sequence, char type) {
		super(instrument, sequence, type);
	}

	public BCTFrame(Instrument instrument) {
		super(instrument);
	}
	
	
}
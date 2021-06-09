package b01.l3.drivers.roches.cobas.u601701;

import b01.l3.drivers.astm.AstmReceiver;
import b01.l3.drivers.roches.cobas.u601.CobasU601Driver;

public class CobasU601701Driver extends CobasU601Driver {
	public CobasU601701Driver(){
		super();
	}
	
	@Override
	protected void initDriverReceiver() {
		super.initDriverReceiver();
		initReceiver(1);//When 601 and 701 are hooked together we need to read the index 1 instead of the index 0
	}
	
	protected void initReceiver(int posForTestCode) {
		AstmReceiver astmReceiver = (AstmReceiver) getDriverReceiver();
		astmReceiver.setResultLineReader(new CobasU601701_ResultLineReader(this, posForTestCode));		
	}
}

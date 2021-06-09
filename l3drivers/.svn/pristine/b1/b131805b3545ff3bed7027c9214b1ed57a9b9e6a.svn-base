/*
 * Created on Jun 14, 2006
 */
package b01.l3.drivers.dadeBehring.bcs;

import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmReceiver;

/**
 * @author 01Barmaja
 */
public class BCSReceiver extends AstmReceiver {

	public BCSReceiver(BCSDriver driver){
		super(driver);
	}
	
	@Override
	protected L3Test addTest(L3Sample sample, String lisTestCode){
		L3Test test = super.addTest(sample, lisTestCode);
		if(test != null){
			test.setRoundingPrecision(0.01);
		}
		return test;
	}
}
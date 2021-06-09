package b01.l3.data;

import b01.foc.desc.FocConstructor;
import b01.foc.gui.FPanel;
import b01.foc.join.FocRequestLine;

public class L3SampleTestJoin extends FocRequestLine{

  public L3SampleTestJoin(FocConstructor constr){
    super(constr, L3SampleTestJoinDesc.getInstance().getFocRequestDesc());
    newFocProperties();
  }
	
	@Override
	public FPanel newDetailsPanel(int viewID) {
		return null;
	}

}

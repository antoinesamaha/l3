package b01.foc.admin.defaultappgroup;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;

public class DefaultAppGroup extends FocObject {

	public DefaultAppGroup(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public FPanel newDetailsPanel(int viewID){
		return new FPanel();
	}

}

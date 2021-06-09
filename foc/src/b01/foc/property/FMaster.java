package b01.foc.property;

import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;

public class FMaster extends FProperty{

	public FMaster(FocObject focObj) {
		super(focObj, FField.MASTER_MIRROR_ID);
	}
	
  public Object getObject() {
    return getFocObject().getMasterObject();
  }
}

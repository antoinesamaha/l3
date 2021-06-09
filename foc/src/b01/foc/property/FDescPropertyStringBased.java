package b01.foc.property;

import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FDescFieldStringBased;

public class FDescPropertyStringBased extends FMultipleChoiceStringBased implements IFDescProperty {

	public FDescPropertyStringBased(FocObject focObj, int fieldID, String str) {
		super(focObj, fieldID, (String) (str == null ? "" : str));
	}

	public FocDesc getSelectedFocDesc() {
		String focDescName = getString();
		FDescFieldStringBased descField = (FDescFieldStringBased)getFocField();
		return descField.getFocDesc(focDescName);
	}
	
	public void setString(String str){
		super.setString(str);
	}

}

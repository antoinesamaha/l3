package b01.fab.parameterSheet;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class ParameterSheetSelector extends FocObject{
	
	public ParameterSheetSelector(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public void setParameteSetID(int id){
		setPropertyInteger(ParameterSheetSelectorDesc.FLD_PARAM_SET_ID, id);
	}
	
	public int getParameterSetID(){
		return getPropertyInteger(ParameterSheetSelectorDesc.FLD_PARAM_SET_ID);
	}
	
	public void setParameterSetName(String name){
		setPropertyString(ParameterSheetSelectorDesc.FLD_PARAM_SET_NAME, name);
	}
	
	public String getParameterSetName(){
		return getPropertyString(ParameterSheetSelectorDesc.FLD_PARAM_SET_NAME);
	}
	
	public void setTableName(String tableName){
		setPropertyString(ParameterSheetSelectorDesc.FLD_TABLE_NAME, tableName);
	}
	
	public String getTableName(){
		return getPropertyString(ParameterSheetSelectorDesc.FLD_TABLE_NAME);
	}
}

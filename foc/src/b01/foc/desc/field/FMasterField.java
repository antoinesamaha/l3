package b01.foc.desc.field;

import java.awt.Component;

import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.list.filter.FilterCondition;
import b01.foc.property.FMaster;
import b01.foc.property.FProperty;

public class FMasterField extends FField{
	FocDesc masterFocDesc = null;
	
	public FMasterField(FocDesc masterFocDesc){
		super("MASTER_MIRROR", "Master Mirror", MASTER_MIRROR_ID, false, 0, 0);
		setDBResident(false);
		this.masterFocDesc = masterFocDesc;
	}

	@Override
	public void addReferenceLocations(FocDesc pointerDesc) {
	}

	@Override
	public String getCreationString(String name) {
		return "";
	}

	@Override
	public FocDesc getFocDesc() {
		return masterFocDesc;
	}

	@Override
	public Component getGuiComponent(FProperty prop) {		
		return null;
	}

	@Override
	public int getSqlType() {
		return 0;
	}

	@Override
	public AbstractCellControler getTableCellEditor(FProperty prop) {
		return null;
	}

	@Override
	public boolean isObjectContainer() {
		return true;
	}

	@Override
	public FProperty newProperty(FocObject masterObj, Object defaultValue) {
		return new FMaster(masterObj);
	}

	@Override
	public FProperty newProperty(FocObject masterObj) {
		return new FMaster(masterObj);
	}
	
	protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		return null;
	}
}

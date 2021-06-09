package b01.fab.gui.browse;

import b01.fab.model.table.FieldDefinition;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class GuiBrowseColumn extends FocObject {
	
	public GuiBrowseColumn(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public void setBrowseViewDefinition(GuiBrowse definition){
		setPropertyObject(GuiBrowseColumnDesc.FLD_BROWSE_VIEW, definition);
	}
	
	public GuiBrowse getBrowseViewDefinition(){
		return (GuiBrowse)getPropertyObject(GuiBrowseColumnDesc.FLD_BROWSE_VIEW);
	}
	
	public void setFieldDefinition(FieldDefinition fieldDefinition){
		setPropertyObject(GuiBrowseColumnDesc.FLD_FIELD_DEFINITION, fieldDefinition);
	}
	
	public FieldDefinition getFieldDefinition(){
		return (FieldDefinition)getPropertyObject(GuiBrowseColumnDesc.FLD_FIELD_DEFINITION);
	}
	
	public void setViewId(int viewId){
		setPropertyInteger(GuiBrowseColumnDesc.FLD_VIEW_ID, viewId);
	}
	
	public int getViewId(){
		return getPropertyInteger(GuiBrowseColumnDesc.FLD_VIEW_ID);
	}

}

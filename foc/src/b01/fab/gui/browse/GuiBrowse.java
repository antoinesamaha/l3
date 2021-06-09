package b01.fab.gui.browse;

import b01.fab.gui.details.GuiDetails;
import b01.fab.model.table.TableDefinition;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.foc.list.FocList;

public class GuiBrowse extends FocObject {
	
	public static final int VIEW_TYPE_ID_NORMAL = 1;
	public static final int VIEW_TYPE_ID_TABBED_PANEL = 2;
	
	public static final String VIEW_TYPE_LABEL_NORMAL = "NORMAL";
	public static final String VIEW_TYPE_LABEL_TABBED_PANEL = "TABBED PANEL";

	public GuiBrowse(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public void setDescription(String description){
		setPropertyString(GuiBrowseDesc.FLD_DESCRIPTION, description);
	}
	
	public String getDescription(){
		return getPropertyString(GuiBrowseDesc.FLD_DESCRIPTION);
	}
	
	public void setDetailsViewDefinition(GuiDetails definition){
		setPropertyObject(GuiBrowseDesc.FLD_USER_DETAILS_VIEW_DEFINITION, definition);
	}

	public GuiDetails getDetailsViewDefinition(){
		return (GuiDetails)getPropertyObject(GuiBrowseDesc.FLD_USER_DETAILS_VIEW_DEFINITION);
	}
	
	public void setTableViewDefinition(TableDefinition tableDefinition){
		setPropertyObject(GuiBrowseDesc.FLD_TABLE_DEFINITION, tableDefinition);
	}
	
	public TableDefinition getTableDefinition(){
		return (TableDefinition)getPropertyObject(GuiBrowseDesc.FLD_TABLE_DEFINITION);
	}
	
	public FocList getBrowseColumnList(){
		FocList list = getPropertyList(GuiBrowseDesc.FLD_BROWSE_COLUMN_LIST);
		return list;
	}
	
	public void setShowEditButton(boolean show){
		setPropertyBoolean(GuiBrowseDesc.FLD_SHOW_EDIT_BUTTON, show);
	}
	
	public boolean isShowEditButton(){
		return getPropertyBoolean(GuiBrowseDesc.FLD_SHOW_EDIT_BUTTON);
	}
	
	/*public void setViewId(int viewId){
		setPropertyInteger(GuiBrowseDesc.FLD_VIEW_ID, viewId);
	}
	
	public int getViewId(){
		return getPropertyInteger(GuiBrowseDesc.FLD_VIEW_ID);
	}*/
	
	public void setShowValidationPanel(boolean show){
		setPropertyBoolean(GuiBrowseDesc.FLD_SHOW_VALIDATION_PANEL, show);
	}
	
	public boolean isShowValidationPanel(){
		return getPropertyBoolean(GuiBrowseDesc.FLD_SHOW_VALIDATION_PANEL);
	}
	
	public void setViewType(int viewTypeId){
		setPropertyInteger(GuiBrowseDesc.FLD_BROWSE_VIEW_TYPE, viewTypeId);
	}
	
	public int getViewType(){
		return getPropertyInteger(GuiBrowseDesc.FLD_BROWSE_VIEW_TYPE);
	}
}

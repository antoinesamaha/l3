package b01.fab.gui.details;

import java.awt.Component;

import javax.swing.JComponent;

import b01.fab.gui.browse.GuiBrowse;
import b01.fab.model.table.FieldDefinition;
import b01.fab.model.table.TableDefinition;
import b01.fab.model.table.TableDefinitionDesc;
import b01.fab.model.table.UserDefinedObjectGuiBrowsePanel;
import b01.fab.model.table.UserDefinedObjectGuiDetailsPanel;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.list.FocList;
import b01.foc.property.FObject;

public class GuiDetailsComponent extends FocObject{
	
	//public static final int VIEW_ID_DONT_SHOW_PANEL_FOR_FOBJECT_FIELD = -1;
	private static final String NULL_FIELD_DEFINITION = "THIS";

	public GuiDetailsComponent(FocConstructor constr) {
		super(constr);
		newFocProperties();
		FObject fieldDefinitionProp = (FObject)getFocProperty(GuiDetailsComponentDesc.FLD_FIELD_DEFINITION);
		if(fieldDefinitionProp != null){
			fieldDefinitionProp.setNullValueDisplayString(NULL_FIELD_DEFINITION);
		}
		//setViewId(VIEW_ID_DONT_SHOW_PANEL_FOR_FOBJECT_FIELD);
	}
	
	public void setDetailsView(GuiDetails definition){
		setPropertyObject(GuiDetailsComponentDesc.FLD_GUI_DETAILS, definition);
	}
	
	public GuiDetails getDetailsView(){
		return (GuiDetails)getPropertyObject(GuiDetailsComponentDesc.FLD_GUI_DETAILS);
	}
	
	public void setFieldDefinition(FieldDefinition fieldDefinition){
		setPropertyObject(GuiDetailsComponentDesc.FLD_FIELD_DEFINITION, fieldDefinition);
	}
	
	public FieldDefinition getFieldDefinition(){
		return (FieldDefinition)getPropertyObject(GuiDetailsComponentDesc.FLD_FIELD_DEFINITION);
	}
	
	public void setX(int x){
		setPropertyInteger(GuiDetailsComponentDesc.FLD_X, x);
	}
	
	public int getX(){
		return getPropertyInteger(GuiDetailsComponentDesc.FLD_X);
	}
	
	public void setY(int y){
		setPropertyInteger(GuiDetailsComponentDesc.FLD_Y, y);
	}
	
	public int getY(){
		return getPropertyInteger(GuiDetailsComponentDesc.FLD_Y);
	}
	
	/*public void setViewId(int viewId){
		setPropertyInteger(GuiDetailsComponentDesc.FLD_VIEW_ID, viewId);
	}
	
	public int getViewId(){
		return getPropertyInteger(GuiDetailsComponentDesc.FLD_VIEW_ID);
	}*/
	
	public GuiDetails getComponentGuiDetails(){
		return (GuiDetails) getPropertyObject(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
	}
	
	public GuiBrowse getComponentGuiBrowse(){
		return (GuiBrowse) getPropertyObject(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
	}
	
	/*public Component getComponentForFocObject(FocObject focObject){
		Component comp = null;
		FieldDefinition fieldDefinition = getFieldDefinition();
		if(fieldDefinition != null){
			if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FLIST_FIELD){
				FocList list = focObject.getPropertyList(fieldDefinition.getID());
				comp = new UserDefinedObjectGuiBrowsePanel(list, getViewId());
			}else if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FOBJECT_FIELD && getViewId() != VIEW_ID_DONT_SHOW_PANEL_FOR_FOBJECT_FIELD){
				FocObject focObj = focObject.getPropertyObject(fieldDefinition.getID());
				comp = new UserDefinedObjectGuiDetailsPanel(focObj, getViewId());
				((UserDefinedObjectGuiDetailsPanel)comp).createBorder();
			}else{
				FField field = focObject.getThisFocDesc().getFieldByID(fieldDefinition.getID());
				comp = focObject.getGuiComponent(field.getID());
				((JComponent)comp).setToolTipText(field.getName());
			}
		}else{
			comp = new UserDefinedObjectGuiDetailsPanel(focObject, getViewId());
			((UserDefinedObjectGuiDetailsPanel)comp).createBorder();
		}
		return comp;
	}*/
	
	public Component getComponentForFocObject(FocObject focObject){
		Component comp = null;
		FieldDefinition fieldDefinition = getFieldDefinition();
		if(fieldDefinition != null){
			if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FLIST_FIELD){
				FocList list = focObject.getPropertyList(fieldDefinition.getID());
				GuiBrowse componentGuiBrowse = getComponentGuiBrowse();
				if(componentGuiBrowse != null){
					comp = new UserDefinedObjectGuiBrowsePanel(list, componentGuiBrowse.getReference().getInteger());
				}
			}else if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FOBJECT_FIELD && getComponentGuiDetails() != null){
				FocObject focObj = focObject.getPropertyObject(fieldDefinition.getID());
				comp = new UserDefinedObjectGuiDetailsPanel(focObj, getComponentGuiDetails().getReference().getInteger());
				((UserDefinedObjectGuiDetailsPanel)comp).createBorder();
			}else{
				FField field = focObject.getThisFocDesc().getFieldByID(fieldDefinition.getID());
				comp = focObject.getGuiComponent(field.getID());
				((JComponent)comp).setToolTipText(field.getName());
			}
		}else{
			GuiDetails componentGuiDetails = getComponentGuiDetails();
			if(componentGuiDetails != null){
				comp = new UserDefinedObjectGuiDetailsPanel(focObject, componentGuiDetails.getReference().getInteger());
				((UserDefinedObjectGuiDetailsPanel)comp).createBorder();
			}
		}
		return comp;
	}
	
	public void adjustGuiDetailsAndGuiBrowseProperties(){//This function locks the guiDetails and guiBrowse properties and adjusts their localSourceList

		FieldDefinition fieldDefinition = (FieldDefinition) getFieldDefinition();
		if(fieldDefinition != null){
			if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FOBJECT_FIELD){
				FocDesc focDesc = fieldDefinition.getFocDesc();
				if(focDesc != null){
					FocList tableDefinitionList = TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
					TableDefinition tableDefinition = (TableDefinition) tableDefinitionList.searchByProperyStringValue(TableDefinitionDesc.FLD_NAME, focDesc.getStorageName());
					FocList GuiDetailsDefinitionList = null;
					if(tableDefinition != null){
						GuiDetailsDefinitionList = tableDefinition.getDetailsViewDefinitionList();
					}
					FObject guiDetailsProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
					if(guiDetailsProp != null){
						guiDetailsProp.setValueLocked(false);
						guiDetailsProp.setLocalSourceList(GuiDetailsDefinitionList);
					}
				}
				FObject guiBrowseProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
				if(guiBrowseProp != null){
					guiBrowseProp.setValueLocked(true);
					guiBrowseProp.setLocalSourceList(null);
				}
			}else if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FLIST_FIELD){
				FocDesc slaveFocDesc = fieldDefinition.getSlaveFocDesc();
				if(slaveFocDesc != null){
					FocList tableDefinitionList = TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
					TableDefinition tableDefinition = (TableDefinition) tableDefinitionList.searchByProperyStringValue(TableDefinitionDesc.FLD_NAME, slaveFocDesc.getStorageName());
					FocList GuiBrowseDefinitionList = null;
					if(tableDefinition != null){
						GuiBrowseDefinitionList = tableDefinition.getBrowseViewDefinitionList();
					}
					FObject guiBrowseProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
					if(guiBrowseProp != null){
						guiBrowseProp.setValueLocked(false);
						guiBrowseProp.setLocalSourceList(GuiBrowseDefinitionList);
					}
				}
				FObject guiDetailsProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
				if(guiDetailsProp != null){
					guiDetailsProp.setValueLocked(true);
					guiDetailsProp.setLocalSourceList(null);
				}
			}else{
				FObject objProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
				if(objProp != null){
					objProp.setValueLocked(true);
					objProp.setLocalSourceList(null);
				}
				
				objProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
				if(objProp != null){
					objProp.setValueLocked(true);
					objProp.setLocalSourceList(null);
				}
			}
		}else{
			GuiDetails guiDetails = getDetailsView();
			if(guiDetails != null){
				TableDefinition tableDefinition = guiDetails.getTableDefinition();
				if(tableDefinition != null){
					FocList GuiDetailsDefinitionList = tableDefinition.getDetailsViewDefinitionList();
					FObject guiDetailsProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
					if(guiDetailsProp != null){
						guiDetailsProp.setValueLocked(false);
						guiDetailsProp.setLocalSourceList(GuiDetailsDefinitionList);
					}
				}
			}
			
			FObject guiBrowseProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
			if(guiBrowseProp != null){
				guiBrowseProp.setValueLocked(true);
				guiBrowseProp.setLocalSourceList(null);
			}
		}
		

		FObject guiDetailsProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
		if(guiDetailsProp != null){
			FocObject existingDetails = (GuiDetails) guiDetailsProp.getObject_CreateIfNeeded();
			FocObject newDetails = null;
			FocList sourceList = guiDetailsProp.getPropertySourceList();
			if(existingDetails != null && sourceList != null){
				newDetails = (GuiDetails) sourceList.searchByRealReferenceOnly(existingDetails.getReference().getInteger());
			}
			guiDetailsProp.setObject(newDetails);
		}
		
		FObject guiBrowseProp = (FObject) getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
		if(guiBrowseProp != null){
			FocObject existingBrowse = (GuiDetails) guiDetailsProp.getObject_CreateIfNeeded();
			FocObject newBrowse = null;
			FocList sourceList = guiBrowseProp.getPropertySourceList();
			if(existingBrowse != null && sourceList != null){
				newBrowse = (GuiDetails) sourceList.searchByRealReferenceOnly(existingBrowse.getReference().getInteger());
			}
			guiBrowseProp.setObject(newBrowse);
		}
	
	}
}

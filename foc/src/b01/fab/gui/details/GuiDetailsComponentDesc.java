package b01.fab.gui.details;

import b01.fab.gui.browse.GuiBrowseDesc;
import b01.fab.model.table.FieldDefinitionDesc;
import b01.fab.model.table.TableDefinition;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.property.FObject;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

public class GuiDetailsComponentDesc extends FocDesc {
	
	public static final int FLD_GUI_DETAILS = 1;
	public static final int FLD_FIELD_DEFINITION = 2;
	public static final int FLD_X = 3;
	public static final int FLD_Y = 4;
	//public static final int FLD_VIEW_ID = 5;
	public static final int FLD_COMPONENT_GUI_DETAILS = 6;
	public static final int FLD_COMPONENT_GUI_BROWSE = 7;
	
	
	private FPropertyListener detailsViewListener = null;
	public FPropertyListener getDetailsViewListener(){
		if(detailsViewListener == null){
			detailsViewListener = new FPropertyListener(){

				public void propertyModified(FProperty property) {
					GuiDetailsComponent guiDetailsComponent = (GuiDetailsComponent)property.getFocObject();
					if(guiDetailsComponent != null){
						GuiDetails detailsViewDefinition = guiDetailsComponent.getDetailsView();
						if(detailsViewDefinition != null){
							TableDefinition tableDefinition = detailsViewDefinition.getTableDefinition();
              if(tableDefinition != null){
  							FocList fieldDefinitionList = tableDefinition.getFieldDefinitionList();
  							FObject fieldDefinitionProp = (FObject)guiDetailsComponent.getFocProperty(FLD_FIELD_DEFINITION);
                fieldDefinitionProp.setLocalSourceList(fieldDefinitionList);
                guiDetailsComponent.adjustGuiDetailsAndGuiBrowseProperties();
              }
						}
					}
				}

				public void dispose() {
				}
				
			};
		}
		return detailsViewListener;
	}
	
	private FPropertyListener fieldDefinitionListener = null;
	private FPropertyListener getFieldDefinitionListener(){
		if(fieldDefinitionListener == null){
			fieldDefinitionListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					GuiDetailsComponent detailsComponent = (GuiDetailsComponent) property.getFocObject();
					if(detailsComponent != null){
						detailsComponent.adjustGuiDetailsAndGuiBrowseProperties();
						/*
						FieldDefinition fieldDefinition = (FieldDefinition) detailsComponent.getFieldDefinition();
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
									FObject guiDetailsProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
									if(guiDetailsProp != null){
										guiDetailsProp.setValueLocked(false);
										guiDetailsProp.setLocalSourceList(GuiDetailsDefinitionList);
									}
								}
								FObject guiBrowseProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
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
									FObject guiBrowseProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
									if(guiBrowseProp != null){
										guiBrowseProp.setValueLocked(false);
										guiBrowseProp.setLocalSourceList(GuiBrowseDefinitionList);
									}
								}
								FObject guiDetailsProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
								if(guiDetailsProp != null){
									guiDetailsProp.setValueLocked(true);
									guiDetailsProp.setLocalSourceList(null);
								}
							}else{
								FObject objProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
								if(objProp != null){
									objProp.setValueLocked(true);
									objProp.setLocalSourceList(null);
								}
								
								objProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
								if(objProp != null){
									objProp.setValueLocked(true);
									objProp.setLocalSourceList(null);
								}
							}
						}else{
							TableDefinition tableDefinition = detailsComponent.getDetailsView().getTableDefinition();
							if(tableDefinition != null){
								FocList GuiDetailsDefinitionList = tableDefinition.getDetailsViewDefinitionList();
								FObject guiDetailsProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
								if(guiDetailsProp != null){
									guiDetailsProp.setValueLocked(false);
									guiDetailsProp.setLocalSourceList(GuiDetailsDefinitionList);
								}
							}
							FObject guiBrowseProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
							if(guiBrowseProp != null){
								guiBrowseProp.setValueLocked(true);
								guiBrowseProp.setLocalSourceList(null);
							}
						}
						

						FObject guiDetailsProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
						if(guiDetailsProp != null){
							FocObject existingDetails = (GuiDetails) guiDetailsProp.getObject_CreateIfNeeded();
							FocObject newDetails = null;
							FocList sourceList = guiDetailsProp.getPropertySourceList();
							if(existingDetails != null && sourceList != null){
								newDetails = (GuiDetails) sourceList.searchByRealReferenceOnly(existingDetails.getReference().getInteger());
							}
							guiDetailsProp.setObject(newDetails);
						}
						
						FObject guiBrowseProp = (FObject) detailsComponent.getFocProperty(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
						if(guiBrowseProp != null){
							FocObject existingBrowse = (GuiDetails) guiDetailsProp.getObject_CreateIfNeeded();
							FocObject newBrowse = null;
							FocList sourceList = guiBrowseProp.getPropertySourceList();
							if(existingBrowse != null && sourceList != null){
								newBrowse = (GuiDetails) sourceList.searchByRealReferenceOnly(existingBrowse.getReference().getInteger());
							}
							guiBrowseProp.setObject(newBrowse);
						}*/
					}
				}
				
				public void dispose() {
				}
				
			};
		}
		return fieldDefinitionListener;
	}
	
	public GuiDetailsComponentDesc(){
		super(GuiDetailsComponent.class, FocDesc.DB_RESIDENT, "DETAILS_FEILD_DEFINITION", false);
		FField fld = addReferenceField();
		
		FObjectField objFld = new FObjectField("DETAILS_VIEW", "Details view", FLD_GUI_DETAILS, false, GuiDetailsDesc.getInstance(), "DETAILS_VIEW_", this, GuiDetailsDesc.FLD_DETAILS_FIELD_LIST);
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		objFld.addListener(getDetailsViewListener());
		addField(objFld);
		
		objFld = new FObjectField("FIELD_DEFINITION", "Field definition", FLD_FIELD_DEFINITION, false, FieldDefinitionDesc.getInstance(), "FIELD_DEF_");
		objFld.setDisplayField(FieldDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(FieldDefinitionDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setWithList(false);
    objFld.addListener(getFieldDefinitionListener());
		addField(objFld);
		
		fld = new FIntField("X", "X",FLD_X, false, 2);
		addField(fld);
		
		fld = new FIntField("Y", "Y", FLD_Y, false, 2);
		addField(fld);
		
		/*fld = new FIntField("VIEW_ID", "View id", FLD_VIEW_ID, false, 2);
		addField(fld);*/
		
		objFld = new FObjectField("GUI_DETAILS", "Gui details", FLD_COMPONENT_GUI_DETAILS, false, GuiDetailsDesc.getInstance(), "GUI_DETAILS_");
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
		
		objFld = new FObjectField("GUI_BROWSE", "Gui browse", FLD_COMPONENT_GUI_BROWSE, false, GuiBrowseDesc.getInstance(), "GUI_BROWSE_");
		objFld.setDisplayField(GuiBrowseDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiBrowseDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
		
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/GuiDetailsComponentDesc();;
    }
    return focDesc;
  }
}

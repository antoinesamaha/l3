package b01.fab.gui.browse;

import b01.fab.gui.details.GuiDetailsDesc;
import b01.fab.model.table.TableDefinition;
import b01.fab.model.table.TableDefinitionDesc;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.property.FObject;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

public class GuiBrowseDesc extends FocDesc {
	
	public static final int FLD_DESCRIPTION = 1;
	public static final int FLD_TABLE_DEFINITION = 2;
	public static final int FLD_USER_DETAILS_VIEW_DEFINITION = 3;
	public static final int FLD_BROWSE_COLUMN_LIST = 4;
	public static final int FLD_SHOW_EDIT_BUTTON = 5;
	//public static final int FLD_VIEW_ID = 6;
	public static final int FLD_SHOW_VALIDATION_PANEL = 7;
	public static final int FLD_BROWSE_VIEW_TYPE = 8;
	
	private static FPropertyListener tableDefinitionListener = null;
	public static FPropertyListener getTableDefinitionListener(){
		if(tableDefinitionListener == null){
			tableDefinitionListener = new FPropertyListener(){

				public void propertyModified(FProperty property) {
					GuiBrowse browseViewDefinition = (GuiBrowse)property.getFocObject();
					if(browseViewDefinition != null){
						TableDefinition tableDefinition = browseViewDefinition.getTableDefinition();
						if(tableDefinition != null){
							FocList detailsViewDefinitionList = tableDefinition.getDetailsViewDefinitionList();
							FObject detailsViewDefinitionProp = (FObject)browseViewDefinition.getFocProperty(GuiBrowseDesc.FLD_USER_DETAILS_VIEW_DEFINITION);
							detailsViewDefinitionProp.setLocalSourceList(detailsViewDefinitionList);
						}
					}
				}
				
				public void dispose() {
				}

				
			};
		}
		return tableDefinitionListener;
	}

	public GuiBrowseDesc(){
		super(GuiBrowse.class, FocDesc.DB_RESIDENT, "USER_BROWSE_VIEW_DEFINITION", false);
		
		setGuiBrowsePanelClass(GuiBrowseGuiBrowsePanel.class);
		setGuiDetailsPanelClass(GuiBrowseGuiDetailsPanel.class);
		
		addReferenceField();

		FField fld = new FCharField("DESCRIPTION", "Description", FLD_DESCRIPTION, false, 50);
		addField(fld);
		
		FObjectField objFld = new FObjectField("TABLE_DEFINITION", "table definition", FLD_TABLE_DEFINITION, false, TableDefinitionDesc.getInstance(), "TABLE_DEF_", this, TableDefinitionDesc.FLD_BROWSE_VIEW_LIST);
		objFld.setDisplayField(TableDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		//objFld.setSelectionList(TableDefinitionDesc.getList(FocList.NONE));
		objFld.setWithList(false);
		objFld.addListener(getTableDefinitionListener());
		addField(objFld);
		
		objFld = new FObjectField("USER_DETAILS_DEFINITION", "User details view definition", FLD_USER_DETAILS_VIEW_DEFINITION, false, GuiDetailsDesc.getInstance(), "USER_DETAILS_VIEW_DEFINITION_");
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		addField(objFld);
		
		fld = new FBoolField("SHOW_EDIT_BUTTON", "Show edit button", FLD_SHOW_EDIT_BUTTON, false);
		addField(fld);
		
		/*fld = new FIntField("VIEW_ID", "View id", FLD_VIEW_ID, false, 2);
		addField(fld);*/
		
		fld = new FBoolField("SHOW_VALIDATION_PANEL", "Show validation panel", FLD_SHOW_VALIDATION_PANEL, false);
		addField(fld);
		
		FMultipleChoiceField multipleChoice = new FMultipleChoiceField("VIEW_TYPE", "View type", FLD_BROWSE_VIEW_TYPE, false, 1);
		multipleChoice.addChoice(GuiBrowse.VIEW_TYPE_ID_NORMAL, GuiBrowse.VIEW_TYPE_LABEL_NORMAL);
		multipleChoice.addChoice(GuiBrowse.VIEW_TYPE_ID_TABBED_PANEL, GuiBrowse.VIEW_TYPE_LABEL_TABBED_PANEL);
		addField(multipleChoice);
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/GuiBrowseDesc();
    }
    return focDesc;
  }

}

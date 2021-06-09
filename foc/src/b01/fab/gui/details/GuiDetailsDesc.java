package b01.fab.gui.details;

import b01.fab.model.table.TableDefinitionDesc;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FObjectField;

public class GuiDetailsDesc extends FocDesc {
	
	public static final int FLD_DESCRIPTION = 1;
	public static final int FLD_TABLE_DEFINITION = 2;
	public static final int FLD_DETAILS_FIELD_LIST = 3;
	//public static final int FLD_VIEW_ID = 4;
	public static final int FLD_ADD_SUBJECT_TO_VALIDATION_PANEL = 5;
	public static final int FLD_SHOW_VALIDATION_PANEL = 6;
	public static final int FLD_VIEW_MODE = 7;
	public static final int FLD_IS_DEFAULT_VIEW = 8;
	public static final int FLD_IS_SUMMARY_VIEW = 9;
	
	public GuiDetailsDesc(){
	
		super(GuiDetails.class, FocDesc.DB_RESIDENT, "USER_DETAILS_VIEW_DEFINTION", false);
		addReferenceField();
		setGuiBrowsePanelClass(GuiDetailsGuiBrowsePanel.class);
		setGuiDetailsPanelClass(GuiDetailsGuiDetailsPanel.class);
		
		FObjectField objFld = new FObjectField("TABLE_DEFINITION", "table definition", FLD_TABLE_DEFINITION, false, TableDefinitionDesc.getInstance(), "TABLE_DEF_", this, TableDefinitionDesc.FLD_DETAILS_VIEW_LIST);
		objFld.setDisplayField(TableDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		//objFld.setSelectionList(TableDefinitionDesc.getList(FocList.NONE));
		objFld.setWithList(false);
		addField(objFld);
		
		FField fld = new FCharField("DESCRIPTION", "Description", FLD_DESCRIPTION, false, 50);
		addField(fld);
		
		/*fld = new FIntField("VIEW_ID", "View id", FLD_VIEW_ID, false, 2);
		addField(fld);*/
		
		fld = new FBoolField("ADD_SUBJECT_TO_VALID_PANEL", "Validate subject on save", FLD_ADD_SUBJECT_TO_VALIDATION_PANEL, false);
		addField(fld);
		
		fld = new FBoolField("SHOW_VALIDATION_PANEL", "Show validation panel", FLD_SHOW_VALIDATION_PANEL, false);
		addField(fld);
		
		FMultipleChoiceField multiField = new FMultipleChoiceField("VIEW_MODE", "View mode", FLD_VIEW_MODE, false, 1);
		multiField.addChoice(GuiDetails.VIEW_MODE_ID_NORMAL, GuiDetails.VIEW_MODE_LABEL_NORMAL);
		multiField.addChoice(GuiDetails.VIEW_MODE_ID_TABBED_PANEL, GuiDetails.VIEW_MODE_LABEL_TABBED_PANEL);
		addField(multiField);
		
		fld = new FBoolField("IS_DEFAULT_VIEW", "Is default view", FLD_IS_DEFAULT_VIEW, false);
		addField(fld);
		
		fld = new FBoolField("IS_SUMMARY_VIEW", "Is summary view", FLD_IS_SUMMARY_VIEW, false);
		addField(fld);
		
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/GuiDetailsDesc();
    }
    return focDesc;
  }

}

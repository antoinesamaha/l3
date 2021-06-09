package b01.fab.gui.browse;

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

public class GuiBrowseColumnDesc extends FocDesc {
	
	public static final int FLD_BROWSE_VIEW      = 1;
	public static final int FLD_FIELD_DEFINITION = 2;
	public static final int FLD_VIEW_ID = 3;
	
	private static  FPropertyListener browseViewListener = null;
	public static FPropertyListener getBrowseViewListner(){
		if(browseViewListener == null){
			browseViewListener = new FPropertyListener(){

				public void propertyModified(FProperty property) {
					if(property != null){
						GuiBrowseColumn browseColumn = (GuiBrowseColumn)property.getFocObject();
						if(browseColumn != null){
							GuiBrowse browseViewDefinition = browseColumn.getBrowseViewDefinition();
							if(browseViewDefinition != null){
								TableDefinition tableDefinition = browseViewDefinition.getTableDefinition();
								if(tableDefinition != null){
									FocList fieldDefinitionList = tableDefinition.getFieldDefinitionList();
									/*FObjectField fieldDefinitionField = (FObjectField)browseColumn.getThisFocDesc().getFieldByID(FLD_FIELD_DEFINITION);
									fieldDefinitionField.setSelectionList(fieldDefinitionList);*/
									FObject fieldDefinitionProp = (FObject)browseColumn.getFocProperty(FLD_FIELD_DEFINITION);
									fieldDefinitionProp.setLocalSourceList(fieldDefinitionList);
								}
							}
						}
					}
				}
				
				public void dispose() {
				}
			};
		}
		return browseViewListener;
	}
	
	public GuiBrowseColumnDesc(){
		super(GuiBrowseColumn.class, FocDesc.DB_RESIDENT, "BROWSE_COLUMN", false);
		FField fld = addReferenceField();
		
		FObjectField objFld = new FObjectField("BROWSE_VIEW", "Browse view", FLD_BROWSE_VIEW, false, GuiBrowseDesc.getInstance(), "BROWSE_VIEW_", this, GuiBrowseDesc.FLD_BROWSE_COLUMN_LIST);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(GuiBrowseDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiBrowseDesc.FLD_DESCRIPTION);
		objFld.setWithList(false);
		objFld.addListener(getBrowseViewListner());
		addField(objFld);
		
		objFld = new FObjectField("FIELD_DEFINITION", "Field definition", FLD_FIELD_DEFINITION, false, FieldDefinitionDesc.getInstance(), "FIELD_DEF_");
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(FieldDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(FieldDefinitionDesc.FLD_NAME);
		objFld.setWithList(false);
		addField(objFld);
		
		fld = new FIntField("VIEW_ID", "View id", FLD_VIEW_ID, false, 2);
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
      focDesc = new /*XXX*/GuiBrowseColumnDesc();;
    }
    return focDesc;
  }

}

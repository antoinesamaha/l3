package b01.fab.model.filter;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FAttributeLocationField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;

public class FilterFieldDefinitionDesc extends FocDesc {
	public static final int FLD_FILTER_DEFINITION = 1;
	public static final int FLD_CONDITION_PROPERTY_PATH = 2;
	
	public FilterFieldDefinitionDesc(){
		super(FilterFieldDefinition.class, FocDesc.DB_RESIDENT, "FILTER_FIELD_DEFINITION", false);
		setGuiBrowsePanelClass(FilterFieldDefinitionGuiBrowsePanel.class);
		setGuiDetailsPanelClass(FilterFieldDefinitionGuiDetailsPanel.class);
		FField fld = addReferenceField();
		
		FObjectField objFld = new FObjectField("FILTER_DEFINITION", "Filter definition", FLD_FILTER_DEFINITION, false, FilterDefinitionDesc.getInstance(), "FILTER_DEFINITION_", this, FilterDefinitionDesc.FLD_FILTER_FIELD_DEFINITION_LIST);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(FilterDefinitionDesc.FLD_TITLE);
		objFld.setComboBoxCellEditor(FilterDefinitionDesc.FLD_TITLE);
		addField(objFld);
		
		fld = new FAttributeLocationField("CONDITION_PROPERTY_PATH", "Condition property path", FLD_CONDITION_PROPERTY_PATH, false, FFieldPath.newFieldPath(FilterFieldDefinitionDesc.FLD_FILTER_DEFINITION, FilterDefinitionDesc.FLD_BASE_FOC_DESC));
		addField(fld);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	private static FocList list = null;
	public static FocList getList(int mode){
		list = getInstance().getList(list, mode);
		list.setDirectlyEditable(true);
		list.setDirectImpactOnDatabase(false);
		/*if(list.getListOrder() == null){
			FocListOrder order = new FocListOrder(FLD_TITLE);
			list.setListOrder(order);
		}*/
		return list;		
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/FilterFieldDefinitionDesc();
    }
    return focDesc;
  }

}

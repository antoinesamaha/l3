package b01.fab.parameterSheet;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FMultipleChoiceFieldStringBased;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;

public class ParameterSheetSelectorDesc extends FocDesc {
	public static final int FLD_PARAM_SET_ID = 1;
	public static final int FLD_PARAM_SET_NAME = 2;
	public static final int FLD_TABLE_NAME = 3;
	
	public ParameterSheetSelectorDesc(){
		super(ParameterSheetSelector.class, FocDesc.DB_RESIDENT, "PARAMETER_SET_SLECTOR", false);
		
		setGuiBrowsePanelClass(ParameterSheetSelectorGuiBrowsePanel.class);
		
		FField focFld = addReferenceField();
		
		focFld = new FIntField("PARAM_SET_ID", "Param set ID", FLD_PARAM_SET_ID, false, 2);
		addField(focFld);
		
		focFld = new FCharField("PARAM_SET_NAME", "Param set name", FLD_PARAM_SET_NAME, false, 30);
		addField(focFld);
		
		focFld = new FMultipleChoiceFieldStringBased("TABLE_NAME", "Table name", FLD_TABLE_NAME, false, 30);
		addField(focFld);
	}
	
	protected void afterConstruction(){
		FMultipleChoiceFieldStringBased descFld = (FMultipleChoiceFieldStringBased)getFieldByID(ParameterSheetSelectorDesc.FLD_TABLE_NAME);
		if(descFld != null){
			descFld.fillWithAllDeclaredFocDesc();
		}
		//FilterDefinition.fillFDescFieldChoices(descFld);
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
		if(list.getListOrder() == null){
			FocListOrder order = new FocListOrder(FLD_PARAM_SET_ID);
			list.setListOrder(order);
		}
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
      focDesc = new /*XXX*/ParameterSheetSelectorDesc();
    }
    return focDesc;
  } 
}

package b01.fab.model.filter;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FDescFieldStringBased;
import b01.foc.desc.field.FField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;

public class FilterDefinitionDesc extends FocDesc {
	public static final int FLD_TITLE = 1;
	public static final int FLD_BASE_FOC_DESC = 2;
	public static final int FLD_FILTER_FIELD_DEFINITION_LIST = 3;
	
	public FilterDefinitionDesc(){
		super(FilterDefinition.class, FocDesc.DB_RESIDENT, "FILTER_DEFINITION", false);
		setGuiBrowsePanelClass(FilterDefinitionGuiBrowsePanel.class);
		setGuiDetailsPanelClass(FilterDefinitionGuiDetailsPanel.class);
		FField fld = addReferenceField();
		
		fld = new FCharField("TITLE", "Title", FLD_TITLE, false, 50);
		addField(fld);
		
		fld = new FDescFieldStringBased("BASE_FOC_DESC", "Base foc desc", FLD_BASE_FOC_DESC, false, 50);
		fld.setMandatory(true);
		fld.setLockValueAfterCreation(true);
		addField(fld);
	}
	
	protected void afterConstruction(){
		FDescFieldStringBased descFld = (FDescFieldStringBased)getFieldByID(FilterDefinitionDesc.FLD_BASE_FOC_DESC);
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
			FocListOrder order = new FocListOrder(FLD_TITLE);
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
      focDesc = new /*XXX*/FilterDefinitionDesc();
    }
    return focDesc;
  }

}

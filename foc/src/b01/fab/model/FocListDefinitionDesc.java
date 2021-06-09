package b01.fab.model;

import b01.fab.model.table.TableDefinitionDesc;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;

public class FocListDefinitionDesc extends FocDesc {
	public static final int FLD_FOCLIST_ID = 1;
	public static final int FLD_FOCLIST_TITLE = 2;
	public static final int FLD_TABLE_DEFINITION = 3;
	
	/**
	 * 
	 */
	public FocListDefinitionDesc(){
		super(FocListDefinition.class, FocDesc.DB_RESIDENT, "FOCLIST_DEFINITION", false);
		FField fld = addReferenceField();
		
		fld = new FIntField("FOCLIST_ID", "List id", FLD_FOCLIST_ID, false, 2);
		addField(fld);
		
		fld = new FCharField("FOCLIST_TITLE", "List title", FLD_FOCLIST_TITLE, false, 30);
		addField(fld);
		
		FObjectField objFld = new FObjectField("TABLE_DEFINITION", "Table definiton", FLD_TABLE_DEFINITION, false, TableDefinitionDesc.getInstance(), "TABLE_DEF_", this, TableDefinitionDesc.FLD_FOCLIST_LIST);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setDisplayField(TableDefinitionDesc.FLD_NAME);
		objFld.setSelectionList(TableDefinitionDesc.getList(FocList.NONE));
		addField(objFld);
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
			FocListOrder order = new FocListOrder(FLD_FOCLIST_ID);
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
      focDesc = new /*XXX*/FocListDefinitionDesc();
    }
    return focDesc;
  }

}

package b01.fab.model.table;

import b01.fab.FabModule;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;

public class TableDefinitionDesc extends FocDesc {
	public static final int FLD_NAME                  = 1;
	public static final int FLD_WITH_REF              = 2;
	public static final int FLD_DB_RESIDENT           = 3;
	public static final int FLD_KEY_UNIQUE            = 4;
	public static final int FLD_SINGLE_INSTANCE          = 5;
	public static final int FLD_BROWSE_VIEW_LIST      = 6;
	public static final int FLD_DETAILS_VIEW_LIST     = 7;
	public static final int FLD_FOCLIST_LIST          = 8;
	public static final int FLD_FILTER_FIELD_DEF_LIST = 9;
	public static final int FLD_FIELD_DEFINITION_LIST = 10;
	
	public TableDefinitionDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique) {
		super(focObjectClass, dbResident, storageName, isKeyUnique);
		addFieldsToDesc();
	}
	
	public TableDefinitionDesc(){
		this(TableDefinition.class, FocDesc.DB_RESIDENT, FabModule.getTableDefinitionTableStorageName(), false);
	}
	
	private void addFieldsToDesc(){
		setGuiBrowsePanelClass(TableDefinitionGuiBrowsePanel.class);
		setGuiDetailsPanelClass(TableDefinitionGuiDetailsPanel.class);
		
		FField fld = addReferenceField();
		
		FCharField charFld = new FCharField("NAME","Name",FLD_NAME,false,50);
		charFld.setCapital(true);
		addField(charFld);
		
		fld = new FBoolField("DB_RESIDENT","DB resident",FLD_DB_RESIDENT,false);
		addField(fld);
		
		fld = new FBoolField("SINGLE_INSTANCE", "Is single table", FLD_SINGLE_INSTANCE, false);
		addField(fld);
		
		fld = new FBoolField("WITH_REF","With ref",FLD_WITH_REF,false);
		addField(fld);
		
		fld = new FBoolField("KEY_UNIQUE","Key unique",FLD_KEY_UNIQUE,false);
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
		if(list.getListOrder() == null){
			FocListOrder order = new FocListOrder(FLD_NAME);
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
      focDesc = new /*XXX*/TableDefinitionDesc();
    }
    return focDesc;
  }

}

package b01.fab.model.table;

import b01.fab.FabModule;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FDescFieldStringBased;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FNumField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;

public class FieldDefinitionDesc extends FocDesc {
	
	public static final int FLD_TABLE                          = 1;
	public static final int FLD_ID                             = 2;
	public static final int FLD_NAME                           = 3;
	public static final int FLD_DB_RESIDENT                    = 4;
	public static final int FLD_TITLE                          = 5;
	public static final int FLD_SQL_TYPE                       = 6;
	public static final int FLD_IS_KEY                         = 7;
	public static final int FLD_LENGTH                         = 8;
	public static final int FLD_DECIMALS                       = 9;
	public static final int FLD_FORMULA                        = 10;
	//B For FObjectField
	public static final int FLD_FOC_DESC                       = 11;
	public static final int FLD_KEY_PREFIX                     = 12;
	public static final int FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID = 13;
	//public static final int FLD_LIST_FIELD_ID                  = 14;
	public static final int FLD_FILTER_REF                     = 15;
	public static final int FLD_FILTER_LIST                    = 16;
	//E For FObjectField
	
	//B For FListField
	public static final int FLD_SLAVE_DESC                     = 17;
	public static final int FLD_UNIQUE_FOREIGN_KEY             = 18;
	//E For FListField
	
	
	
	/*private static FPropertyListener tableListener = null;
	private static FPropertyListener getTableListener(){
		if(tableListener == null){
			tableListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FieldDefinition fieldDefinition = (FieldDefinition)property.getFocObject();
					if(fieldDefinition != null){
						if(fieldDefinition.getID() == FField.NO_FIELD_ID){
							TableDefinition tableDefinition = fieldDefinition.getTableDefinition();
							int maxFieldId = tableDefinition.getMaxFieldDefinitionId();
							fieldDefinition.setID(maxFieldId + 1);
						}
					}
				}
				
				public void dispose() {
				}
			};
		}
		return tableListener;
	}*/
	
	/*private static FPropertyListener focDescNameListener = null;
	private static FPropertyListener getFocDescNameListener(){
		if(focDescNameListener == null){
			focDescNameListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FieldDefinition fieldDefinition = (FieldDefinition)property.getFocObject();
					if(fieldDefinition != null && fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FOBJECT_FIELD){
						FMultipleChoice cellEditorFieldId = (FMultipleChoice)fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
						boolean cellEditorFieldIdNotNull = cellEditorFieldId != null;
						FocDesc focDesc = fieldDefinition.getFocDesc();
						if(focDesc != null){
							if(cellEditorFieldIdNotNull){
								cellEditorFieldId.resetLocalSourceList();
							}
							FocFieldEnum enumeration = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
							while(enumeration != null && enumeration.hasNext()){
								FField field = enumeration.nextField();
								if(field != null){
									FField fieldFromThisFocDescWithSameId = focDesc.getFieldByID(field.getID());//we make like this cause if we have (FInLineFieldObject or FTypedFieldObject) the enumeration returns the fields of this fields so we have to chek if the returned field are field from this desc or from the desc of the (FInLineFieldObject or FTypedFieldObject) fields before adding them to the multiple choice
									boolean addField = field == fieldFromThisFocDescWithSameId;
									if(addField){
										if(cellEditorFieldIdNotNull){
											cellEditorFieldId.addLocalChoice(field.getID(), field.getName());
										}
									}
								}
							}
						}
					}
				}
				
				public void dispose() {
				}
			};
		}
		return focDescNameListener;
	}*/
	
	public FieldDefinitionDesc(){
		super(FieldDefinition.class,FocDesc.DB_RESIDENT, FabModule.getFieldDefinitionTableStorageName(), false);
		setGuiDetailsPanelClass(FieldDefinitionGuiDetailsPanel.class);
		setGuiBrowsePanelClass(FieldDefinitionGuiBrowsePanel.class);
		FField fld = addReferenceField();
		
		FObjectField objFld = new FObjectField("TABLE","Table",FLD_TABLE,false,TableDefinitionDesc.getInstance(),"TABLE_", this, TableDefinitionDesc.FLD_FIELD_DEFINITION_LIST);
		objFld.setLockValueAfterCreation(true);
		objFld.setMandatory(true);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setSelectionList(TableDefinitionDesc.getList(FocList.NONE));
		//objFld.addListener(getTableListener());
		addField(objFld);
		
		fld = new FNumField("ID","Id",FLD_ID,false,3,0);
		addField(fld);
		
		FCharField charFld = new FCharField("NAME","Name",FLD_NAME,false,50);
		addField(charFld);
		charFld.setMandatory(true);
		charFld.setCapital(true);
		
		fld = new FBoolField("DB_RESIDENT", "DB resident", FLD_DB_RESIDENT, false);
		addField(fld);
		
		fld = new FCharField("TITLE","Title",FLD_TITLE,false,50);
		addField(fld);
		
		FMultipleChoiceField multiChoice = new FMultipleChoiceField("SQL_TYPE","SQL Type",FLD_SQL_TYPE,false,30);
		
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_VARCHAR, FieldDefinition.SQL_TYPE_NAME_VARCHAR);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_INT, FieldDefinition.SQL_TYPE_NAME_INT);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_DOUBLE, FieldDefinition.SQL_TYPE_NAME_DOUBLE);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_LONG, FieldDefinition.SQL_TYPE_NAME_LONG);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_BOOLEAN, FieldDefinition.SQL_TYPE_NAME_BOOLEAN);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_DATE, FieldDefinition.SQL_TYPE_NAME_DATE);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_TIME, FieldDefinition.SQL_TYPE_NAME_TIME);
		multiChoice.addChoice(FieldDefinition.TYPE_ID_FOBJECT_FIELD, FieldDefinition.TYPE_NAME_FOBJECT_FIELD);
		multiChoice.addChoice(FieldDefinition.TYPE_ID_FLIST_FIELD, FieldDefinition.TYPE_NAME_FLIST_FIELD);
		//multiChoice.addListener(getSqlTypeListener());
		multiChoice.setMandatory(true);
		addField(multiChoice);
		
		fld = new FBoolField("IS_KEY","Is key",FLD_IS_KEY,false);
		addField(fld);
		
		fld = new FNumField("LENGTH","Length",FLD_LENGTH,false,3,0);
		addField(fld);
		
		fld = new FNumField("DCIMALS","Decimals",FLD_DECIMALS,false,3,0);
		addField(fld);
		
		fld = new FDescFieldStringBased("FOCDESC_NAME", "FocDesc Name", FLD_FOC_DESC, false, 50);
		//fld.addListener(getFocDescNameListener());
		addField(fld);
		
		FCharField keyPrefixfld = new FCharField("KEY_PREFIX", "Key Prefix", FLD_KEY_PREFIX, false, 20);
		keyPrefixfld.setCapital(true);
		addField(keyPrefixfld);
		
		fld = new FMultipleChoiceField("CELL_EDITOR_FIELD_ID", "Cell editor field", FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID, false, 2);
		addField(fld);
		
		/*fld = new FMultipleChoiceField("LIST_FIELD_ID", "List field in master", FLD_LIST_FIELD_ID, false, 50);
		addField(fld);*/
		
		fld = new FCharField("FORMULA", "Formula", FLD_FORMULA, false, 200);
		//((FCharField)fld).setCapital(true);
		addField(fld);
		
		fld = new FIntField("FILTER_REF", "Filter ref", FLD_FILTER_REF, false, 3);
		addField(fld);
		
		fld = new FDescFieldStringBased("SLAVE_FOC_DESC", "Slave table", FLD_SLAVE_DESC, false, 30);
		addField(fld);
		
		fld = new FMultipleChoiceField("UNIQUE_FOREING_KEY", "Unique foreign key", FLD_UNIQUE_FOREIGN_KEY, false, 30);
		addField(fld);
	}
	
	public void afterConstruction(){
		FDescFieldStringBased descFeild = (FDescFieldStringBased) getFieldByID(FLD_FOC_DESC);
		descFeild.fillWithAllDeclaredFocDesc();
		
		descFeild = (FDescFieldStringBased) getFieldByID(FLD_SLAVE_DESC);
		descFeild.fillWithAllDeclaredFocDesc();
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
      focDesc = new /*XXX*/FieldDefinitionDesc();
    }
    return focDesc;
  }
}

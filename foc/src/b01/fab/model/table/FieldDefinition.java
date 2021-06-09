package b01.fab.model.table;

import b01.fab.model.filter.FilterDefinition;
import b01.fab.model.filter.FocListForFobjectFieldSelectionList;
import b01.fab.model.filter.UserDefinedFilter;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FDateField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FListField;
import b01.foc.desc.field.FLongField;
import b01.foc.desc.field.FNumField;
import b01.foc.desc.field.FObjectField;
import b01.foc.desc.field.FTimeField;
import b01.foc.list.FocLinkForeignKey;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;

public class FieldDefinition extends FocObject {
	
	public static final int SQL_TYPE_ID_VARCHAR = 1;
	public static final int SQL_TYPE_ID_INT = 2;
	public static final int SQL_TYPE_ID_DOUBLE = 3;
	public static final int SQL_TYPE_ID_LONG = 4;
	public static final int SQL_TYPE_ID_DATE = 5;
	public static final int SQL_TYPE_ID_TIME = 6;
	public static final int TYPE_ID_FOBJECT_FIELD = 7;
	public static final int TYPE_ID_FLIST_FIELD = 8;
	public static final int SQL_TYPE_ID_BOOLEAN = 9;
	
	public static final String SQL_TYPE_NAME_VARCHAR = "VARCHAR";
	public static final String SQL_TYPE_NAME_INT = "INT";
	public static final String SQL_TYPE_NAME_DOUBLE = "DOUBLE";
	public static final String SQL_TYPE_NAME_LONG = "LONG";
	public static final String SQL_TYPE_NAME_DATE = "DATE";
	public static final String SQL_TYPE_NAME_TIME = "TIME";
	public static final String TYPE_NAME_FOBJECT_FIELD = "OBJECT FIELD";
	public static final String TYPE_NAME_FLIST_FIELD = "LIST FIELD";
	public static final String SQL_TYPE_NAME_BOOLEAN = "BOOLEAN";
	
	public static final String NO_LIST_FIELD_ID_LABEL = "NONE";
	
	public static final String FIELD_TITLE_NOT_SET_YET = "";
	public static final int FIELD_LENGHT_NOT_SET_YET = 0;
	
	
	public FieldDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
		setID(FField.NO_FIELD_ID);
		setTitle(FieldDefinition.FIELD_TITLE_NOT_SET_YET);
		setLength(FieldDefinition.FIELD_LENGHT_NOT_SET_YET);
		setFieldDBResident(true);
	}

	public TableDefinition getTableDefinition(){
		return (TableDefinition)getPropertyObject(FieldDefinitionDesc.FLD_TABLE);
	}
	
	public void setTable(TableDefinition table){
		setPropertyObject(FieldDefinitionDesc.FLD_TABLE, table);
	}
	
	public int getID(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_ID);
	}
	
	public void setID(int ID){
		setPropertyInteger(FieldDefinitionDesc.FLD_ID, ID);
	}
	
	public String getName(){
		return getPropertyString(FieldDefinitionDesc.FLD_NAME);
	}
	
	public void setName(String name){
		setPropertyString(FieldDefinitionDesc.FLD_NAME,name);
	}
	
	public boolean isFieldDBResident(){
		return getPropertyBoolean(FieldDefinitionDesc.FLD_DB_RESIDENT);
	}
	
	public void setFieldDBResident(boolean dbResident){
		setPropertyBoolean(FieldDefinitionDesc.FLD_DB_RESIDENT, dbResident);
	}
	
	public String getTitle(){
		return getPropertyString(FieldDefinitionDesc.FLD_TITLE);
	}
	
	public void setTitle(String title){
		setPropertyString(FieldDefinitionDesc.FLD_TITLE, title);
	}
	
	public int getSQLType(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_SQL_TYPE);
	}
	
	public void setSQLType(String SQLType){
		setPropertyString(FieldDefinitionDesc.FLD_SQL_TYPE, SQLType);
	}
	
	public boolean isKey(){
		return getPropertyBoolean(FieldDefinitionDesc.FLD_IS_KEY);
	}
	
	public void setKey(boolean key){
		setPropertyBoolean(FieldDefinitionDesc.FLD_IS_KEY, key);
	}
	
	public int getLength(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_LENGTH);
	}
	
	public void setLength(int length){
		setPropertyInteger(FieldDefinitionDesc.FLD_LENGTH, length);
	}
	
	public int getDecimals(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_DECIMALS);
	}
	
	public void setDecimals(int decimals){
		setPropertyInteger(FieldDefinitionDesc.FLD_DECIMALS, decimals);
	}
	
	public String getFocDescName(){
		return getPropertyString(FieldDefinitionDesc.FLD_FOC_DESC);
	}
	
	public String getKeyPrefix(){
		return getPropertyString(FieldDefinitionDesc.FLD_KEY_PREFIX);
	}
	
	public void setKeyPrefix(String prefix){
		setPropertyString(FieldDefinitionDesc.FLD_KEY_PREFIX, prefix);
	}
	
	public int getComboBoxCellEditorFieldId(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
	}
	
	public void setComboBoxCellEditorFieldId(int id){
		setPropertyInteger(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID, id);
	}
	
	public String getFormulaString(){
		return getPropertyString(FieldDefinitionDesc.FLD_FORMULA);
	}
	
	public void setFormula(String formula){
		setPropertyString(FieldDefinitionDesc.FLD_FORMULA, formula);
	}
	
	public boolean isWithFormula(){
		String formula = getFormulaString();
		return formula != null && !formula.equals("");
	}
	
	public void setFilterRef(int filterRef){
		setPropertyInteger(FieldDefinitionDesc.FLD_FILTER_REF, filterRef);
	}
	
	public int getFilterRef(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_FILTER_REF);
	}
	
	public FocDesc getSlaveFocDesc(){
		return getPropertyDesc(FieldDefinitionDesc.FLD_SLAVE_DESC);
	}
	
	public int getUniqueForeignKey(){
		return getPropertyMultiChoice(FieldDefinitionDesc.FLD_UNIQUE_FOREIGN_KEY);
	}
	
	/*public FocDesc getFocDesc(){
		FocDesc focDesc = null;
		String focDescName = getFocDescName();
		if(focDescName != null && focDescName.length() > 0){
			focDesc = Globals.getApp().getFocDescByName(focDescName);
		}
		return focDesc;
	}*/
	
	public FocDesc getFocDesc(){
		return getPropertyDesc(FieldDefinitionDesc.FLD_FOC_DESC);
	}
	
	public void adjustPropertiesEnability(){
		int SQLTYPE = getSQLType();
		boolean lockLength = SQLTYPE == FieldDefinition.SQL_TYPE_ID_BOOLEAN || SQLTYPE == FieldDefinition.SQL_TYPE_ID_DATE || SQLTYPE == FieldDefinition.SQL_TYPE_ID_TIME ||SQLTYPE == FieldDefinition.TYPE_ID_FOBJECT_FIELD ||SQLTYPE == FieldDefinition.TYPE_ID_FLIST_FIELD;
		boolean lockDecimals = SQLTYPE != FieldDefinition.SQL_TYPE_ID_DOUBLE;
		boolean lockFormula = SQLTYPE == FieldDefinition.TYPE_ID_FOBJECT_FIELD || SQLTYPE == FieldDefinition.TYPE_ID_FLIST_FIELD;
		boolean lockPropertiesRelatedToObjectField = SQLTYPE != FieldDefinition.TYPE_ID_FOBJECT_FIELD;
		boolean lockPropertiesRelatedToListField = SQLTYPE != FieldDefinition.TYPE_ID_FLIST_FIELD;
		
		FProperty prop = getFocProperty(FieldDefinitionDesc.FLD_LENGTH);
		prop.setValueLocked(lockLength);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_DECIMALS);
		prop.setValueLocked(lockDecimals);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_KEY_PREFIX);
		prop.setValueLocked(lockPropertiesRelatedToObjectField);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
		prop.setValueLocked(lockPropertiesRelatedToObjectField);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_SLAVE_DESC);
		prop.setValueLocked(lockPropertiesRelatedToListField);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_UNIQUE_FOREIGN_KEY);
		prop.setValueLocked(lockPropertiesRelatedToListField);
		
		prop.getFocProperty(FieldDefinitionDesc.FLD_FORMULA);
		prop.setValueLocked(lockFormula);
		
		/*prop = getFocProperty(FieldDefinitionDesc.FLD_LIST_FIELD_ID);
		prop.setValueLocked(lockPropertiesRelatedToObjectField);*/
	}
	
	public FocDesc getFilterFocDesc(){
		FocDesc filterFocDesc = FilterDefinition.getFilterFocDesc(getFocDescName());;
		return filterFocDesc;
	}
	
	public UserDefinedFilter getUserDefinedFilter(){
		UserDefinedFilter filter = null;
		if(getSQLType() == TYPE_ID_FOBJECT_FIELD){
			FocDesc filterFocDesc = getFilterFocDesc();
			if(filterFocDesc != null){
				FocList filtersList = filterFocDesc.getList(null, FocList.LOAD_IF_NEEDED);
				if(filtersList != null){
					filter = (UserDefinedFilter)filtersList.searchByReference(getFilterRef());
				}
			}
		}
		return filter;
	}
	
	public void addToFocDesc(FocDesc focDesc){
		if(focDesc != null){
			FField field = focDesc.getFieldByID(getID());
			if(field == null){
				int SQLType = getSQLType();
				if(SQLType == SQL_TYPE_ID_VARCHAR){
					field = new FCharField(getName(),getTitle(),getID(),isKey(),getLength());
				}else if(SQLType == SQL_TYPE_ID_INT){
					field = new FIntField(getName(),getTitle(),getID(),isKey(),getLength());
				}else if(SQLType == SQL_TYPE_ID_DOUBLE){
					field = new FNumField(getName(),getTitle(),getID(),isKey(),getLength(),getDecimals());
				}else if(SQLType == SQL_TYPE_ID_LONG){
					field = new FLongField(getName(),getTitle(),getID(),isKey(),getLength());
				}else if(SQLType == SQL_TYPE_ID_BOOLEAN){
					field = new FBoolField(getName(),getTitle(),getID(),isKey());
				}else if(SQLType == SQL_TYPE_ID_DATE){
					field = new FDateField(getName(),getTitle(),getID(),isKey());
				}else if(SQLType == SQL_TYPE_ID_TIME){
					field = new FTimeField(getName(),getTitle(),getID(),isKey());
				}else if(SQLType == TYPE_ID_FOBJECT_FIELD){
					FocDesc masterDesc = getFocDesc();
					field = new FObjectField(getName(), getTitle(), getID(), isKey(), masterDesc, getKeyPrefix());
					((FObjectField)field).setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
					((FObjectField)field).setComboBoxCellEditor(getComboBoxCellEditorFieldId());
					((FObjectField)field).setDisplayField(getComboBoxCellEditorFieldId());
					FocLinkSimple focLink = new FocLinkSimple(masterDesc);
					FocListForFobjectFieldSelectionList focList = new FocListForFobjectFieldSelectionList(getFilterRef(), focLink);
					((FObjectField)field).setSelectionList(focList);
				}else if(SQLType == TYPE_ID_FLIST_FIELD){
					FocLinkForeignKey linkForeignKey = new FocLinkForeignKey(getSlaveFocDesc(), getUniqueForeignKey(),true);
					field = new FListField(getName(), getTitle(), getID(), linkForeignKey);
				}
				if(field != null){
					field.setDBResident(isFieldDBResident());
					focDesc.addField(field);
					String formula = getFormulaString();
					if(formula != null && formula.length() > 0){
						field.setFormulaString(formula);
					}
				}
			}
		}
	}
}

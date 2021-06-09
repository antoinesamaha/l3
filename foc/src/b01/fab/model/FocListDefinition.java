package b01.fab.model;

import b01.fab.model.table.TableDefinition;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class FocListDefinition extends FocObject {
	
	public FocListDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public void setFocListId(int focListId){
		setPropertyInteger(FocListDefinitionDesc.FLD_FOCLIST_ID, focListId);
	}
	
	public int getFocListId(){
		return getPropertyInteger(FocListDefinitionDesc.FLD_FOCLIST_ID);
	}
	
	public void setFocListTitle(String title){
		setPropertyString(FocListDefinitionDesc.FLD_FOCLIST_TITLE, title);
	}
	
	public String getFocListTitle(){
		return getPropertyString(FocListDefinitionDesc.FLD_FOCLIST_TITLE);
	}
	
	public void setTableDefinition(TableDefinition tableDefinition){
		setPropertyObject(FocListDefinitionDesc.FLD_TABLE_DEFINITION, tableDefinition);
	}
	
	public TableDefinition getTableDefinition(){
		return (TableDefinition)getPropertyObject(FocListDefinitionDesc.FLD_TABLE_DEFINITION);
	}

}

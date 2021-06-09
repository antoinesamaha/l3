package b01.fab;

import java.util.Iterator;

import b01.fab.model.table.FieldDefinition;
import b01.fab.model.table.FieldDefinitionDesc;
import b01.fab.model.table.TableDefinition;
import b01.fab.model.table.UserDefinedObject;
import b01.fab.model.table.UserDefinedObjectGuiBrowsePanel;
import b01.fab.model.table.UserDefinedObjectGuiDetailsPanel;
import b01.foc.Globals;
import b01.foc.IFocDescDeclaration;
import b01.foc.desc.FocDesc;
import b01.foc.list.FocLinkForeignKey;
import b01.foc.list.FocList;

public class DBFocDescDeclaration implements IFocDescDeclaration{
	private FocDesc focDesc = null;
	private TableDefinition tableDefinition = null;
	
	private DBFocDescDeclaration(TableDefinition tableDefinition){
		this.tableDefinition = tableDefinition;
	}
	
	public void dispose(){
		this.focDesc = null;
		this.tableDefinition = null;
	}
	
	public int getTableReference(){
		return (tableDefinition != null ? tableDefinition.getReference().getInteger() : 0);
	}
	
	public String getTableName() {
		return (tableDefinition != null ? tableDefinition.getName() : "");
	}

	public FocDesc getFocDesctiption() {
		if(focDesc == null){
			focDesc = new FocDesc(UserDefinedObject.class, tableDefinition.isDBResident(), getTableName(), tableDefinition.isKeyUnique());
			if(tableDefinition.isWithReference()){
				focDesc.addReferenceField();
			}
			focDesc.setGuiBrowsePanelClass(UserDefinedObjectGuiBrowsePanel.class);
			focDesc.setGuiDetailsPanelClass(UserDefinedObjectGuiDetailsPanel.class);
			fillFocDescWithFields();
		}else{
			int debug = 1;
		}
		return focDesc;
	}
	
	@SuppressWarnings("unchecked")
	public void fillFocDescWithFields(){
		//dont use the the fucntion tableDefinition.getFieldList() because when this tableDefinition was created 
		//the function FieldDefinitionDesc.getInstance() was not called yet(that means the FObjectField "tableDefinition" 
		//in FieldDefinitionDesc was not created and it has not create the listField in the masterDesc  : "TableDefinitionDesc"; see Application.prepareDBForLogin and fabModule.declareFocObjects())
		//so this object(tableDefinition) was created with out the property FList for this reson we cant use getPropertyList
		//to get the list of fieldDefinition 
		
		
		
		
		
		//FocList fieldsList = tableDefinition.newFieldDefinitionList(false);
		FocLinkForeignKey link = new FocLinkForeignKey(FieldDefinitionDesc.getInstance(),FieldDefinitionDesc.FLD_TABLE,true);
		FocList fieldsList = new FocList(tableDefinition,link,null);
		fieldsList.loadIfNotLoadedFromDB();
		fieldsList.setFatherSubject(null);
		
		Iterator<FieldDefinition> iter = fieldsList.focObjectIterator();
		while(iter != null && iter.hasNext()){
			FieldDefinition fieldDef = iter.next();
			fieldDef.addToFocDesc(focDesc);
		}
		fieldsList.dispose();
		fieldsList = null;
	}
	
	public static DBFocDescDeclaration getDbFocDescDeclaration(TableDefinition tableDefinition){
		DBFocDescDeclaration declaration = null;
		if(tableDefinition != null){
			String name = tableDefinition.getName();
			declaration = (DBFocDescDeclaration)Globals.getApp().getIFocDescDeclarationByName(name);
			if(declaration == null){
				declaration = new DBFocDescDeclaration(tableDefinition);
				Globals.getApp().putIFocDescDeclaration(name, declaration);
			}
		}
		return declaration;
	}

	public int getPriority() {
		return IFocDescDeclaration.PRIORITY_SECOND;
	}
}

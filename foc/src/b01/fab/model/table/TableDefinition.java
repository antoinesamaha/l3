package b01.fab.model.table;

import java.util.Iterator;

import b01.fab.DBFocDescDeclaration;
import b01.fab.gui.browse.GuiBrowse;
import b01.fab.gui.details.GuiDetails;
import b01.fab.gui.details.GuiDetailsDesc;
import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.list.FocLinkForeignKey;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;

public class TableDefinition extends FocObject{
	private boolean firstCallToGetFieldDefintion = true;

	public TableDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
		setPropertyBoolean(TableDefinitionDesc.FLD_DB_RESIDENT, true );
		setPropertyBoolean(TableDefinitionDesc.FLD_KEY_UNIQUE , false);
		setPropertyBoolean(TableDefinitionDesc.FLD_WITH_REF   , true );
	}
	
	public String getName(){
		return getPropertyString(TableDefinitionDesc.FLD_NAME);
	}
	
	public void setName(String name){
		setPropertyString(TableDefinitionDesc.FLD_NAME, name.toUpperCase());
	}
	
	public boolean isDBResident(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_DB_RESIDENT);
	}
	
	public void setDBResident(boolean dbResident){
		setPropertyBoolean(TableDefinitionDesc.FLD_DB_RESIDENT, dbResident);
	}
	
	public boolean isWithReference(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_WITH_REF);
	}
	
	public void setWithReference(boolean withReference){
		setPropertyBoolean(TableDefinitionDesc.FLD_WITH_REF, withReference);
	}
	
	public boolean isKeyUnique(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_KEY_UNIQUE);
	}
	
	public void setKeyUnique(boolean keyUnique){
		setPropertyBoolean(TableDefinitionDesc.FLD_KEY_UNIQUE, keyUnique);
	}
	
	public void setSingleInstance(boolean singleInstance){
		setPropertyBoolean(TableDefinitionDesc.FLD_SINGLE_INSTANCE, singleInstance);
	}
	
	public boolean isSingleInstance(){
		return getPropertyBoolean(TableDefinitionDesc.FLD_SINGLE_INSTANCE);
	}
	
	@SuppressWarnings("unchecked")
	public void adjustIFocDescDeclaration(){
		DBFocDescDeclaration alreadyDeclaredDBFocDescDeclaration = (DBFocDescDeclaration) Globals.getApp().getIFocDescDeclarationByName(getName());
		if(alreadyDeclaredDBFocDescDeclaration == null){
			DBFocDescDeclaration dbFocDescDeclaration = DBFocDescDeclaration.getDbFocDescDeclaration(this);
			Globals.getApp().declaredObjectList_DeclareDescription(dbFocDescDeclaration);
		}else{
			FocDesc alreadyDeclaredFocDesc = alreadyDeclaredDBFocDescDeclaration.getFocDesctiption();
			if(alreadyDeclaredFocDesc != null){
				FocList fieldDefinitionList = getFieldDefinitionList();
				Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
				while(iter != null && iter.hasNext()){
					FieldDefinition fieldDefintion = iter.next();
					fieldDefintion.addToFocDesc(alreadyDeclaredFocDesc);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public int getMaxFieldDefinitionId(){
		int maxId = 0;
		FocList fieldDefinitionList = getFieldDefinitionList();
		if(fieldDefinitionList != null){
			Iterator<FieldDefinition> iter = fieldDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext()){
				FieldDefinition fieldDefinition = iter.next();
				int id = fieldDefinition.getID();
				if(id > maxId){
					maxId = id;
				}
			}
		}
		return maxId;
	}
	
	/*public FocList getFieldDefinitionList(boolean doNotTryLoading){
		if(fieldsList == null){
			FocLinkForeignKey link = new FocLinkForeignKey(FieldDefinitionDesc.getInstance(),FieldDefinitionDesc.FLD_TABLE,true);
			fieldsList = new FocList(this,link,null);
			fieldsList.setDirectlyEditable(true);
			fieldsList.setDirectImpactOnDatabase(false);
			FocListOrder order = new FocListOrder(FieldDefinitionDesc.FLD_ID);
			fieldsList.setListOrder(order);
			if(!doNotTryLoading){
				fieldsList.loadIfNotLoadedFromDB();
			}
		}
		return fieldsList;
	}*/
	
	public FocList getFieldDefinitionList(){
		FocList fieldDefintionList = getPropertyList(TableDefinitionDesc.FLD_FIELD_DEFINITION_LIST);
		if(firstCallToGetFieldDefintion){
			firstCallToGetFieldDefintion = false;
			FocListOrder order = new FocListOrder(FieldDefinitionDesc.FLD_ID);
			fieldDefintionList.setListOrder(order);
		}
		return fieldDefintionList;
	}

	public FocList newFieldDefinitionList(boolean doNotTryLoading){
		FocList fieldsList = null;
		FocLinkForeignKey link = new FocLinkForeignKey(FieldDefinitionDesc.getInstance(),FieldDefinitionDesc.FLD_TABLE,true);
		fieldsList = new FocList(this,link,null);
		fieldsList.setDirectlyEditable(true);
		fieldsList.setDirectImpactOnDatabase(false);
		FocListOrder order = new FocListOrder(FieldDefinitionDesc.FLD_ID);
		fieldsList.setListOrder(order);
		if(!doNotTryLoading){
			fieldsList.loadIfNotLoadedFromDB();
		}
		return fieldsList;
	}
	
	public FocList getBrowseViewDefinitionList(){
		FocList list = getPropertyList(TableDefinitionDesc.FLD_BROWSE_VIEW_LIST);
		return list;
	}
	
	public FocList getDetailsViewDefinitionList(){
		FocList list = getPropertyList(TableDefinitionDesc.FLD_DETAILS_VIEW_LIST);
		return list;
	}
	
	public FocList getFilterFieldDefinitionList(){
		return getPropertyList(TableDefinitionDesc.FLD_FILTER_FIELD_DEF_LIST);
	}
	
	@SuppressWarnings("unchecked")
	private GuiDetails getDetailsViewDefinitionForViewId(int viewId){
		GuiDetails aDetailsViewDefinition = null;
		boolean found = false;
		if(viewId == FocObject.DEFAULT_VIEW_ID){
			aDetailsViewDefinition = getDefaultVeiwDefinition();
			found = true;
		}else if(viewId == FocObject.SUMMARY_VIEW_ID){
			aDetailsViewDefinition = getSummaryVeiwDefinition();
			found = true;
		}else{
			FocList detailsViewDefinitionList = getDetailsViewDefinitionList();
			if(detailsViewDefinitionList != null){
				aDetailsViewDefinition = (GuiDetails) detailsViewDefinitionList.searchByRealReferenceOnly(viewId);
				found = aDetailsViewDefinition != null;
			}
			/*Iterator<GuiDetails> iter = detailsViewDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext() && !found){
				aDetailsViewDefinition = iter.next();
				if(aDetailsViewDefinition != null && aDetailsViewDefinition.getReference().getInteger() == viewId){
					found = true;
				}
			}*/
		}
		return found ? aDetailsViewDefinition : null;
	}
	
	private GuiDetails getDefaultVeiwDefinition(){
		GuiDetails defaultDetailsViewDefinition = null;
		FocList detailsViewDefinitionList = getDetailsViewDefinitionList();
		if(detailsViewDefinitionList != null){
			defaultDetailsViewDefinition = (GuiDetails)detailsViewDefinitionList.searchByPropertyBooleanValue(GuiDetailsDesc.FLD_IS_DEFAULT_VIEW, true);
		}
		return defaultDetailsViewDefinition;
	}
	
	private GuiDetails getSummaryVeiwDefinition(){
		GuiDetails summaryDetailsViewDefinition = null;
		FocList detailsViewDefinitionList = getDetailsViewDefinitionList();
		if(detailsViewDefinitionList != null){
			summaryDetailsViewDefinition = (GuiDetails)detailsViewDefinitionList.searchByPropertyBooleanValue(GuiDetailsDesc.FLD_IS_SUMMARY_VIEW, true);
		}
		return summaryDetailsViewDefinition;
	}
	
	@SuppressWarnings("unchecked")
	public static TableDefinition getTableDefinitionForFocDesc(FocDesc focDesc){
		FocList tableDefinitionList = TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
		Iterator<TableDefinition> iter = tableDefinitionList.focObjectIterator();
		TableDefinition tableDefinition = null;
		boolean found = false;
		while(iter != null && iter.hasNext() && !found){
			tableDefinition = iter.next();
			if(tableDefinition != null){
				if(tableDefinition.getName().equals(focDesc.getStorageName())){
					found = true;
				}
			}
		}
		return found ? tableDefinition : null;
	}
	
	@SuppressWarnings("unchecked")
	public static GuiBrowse getBrowseViewDefinitionForFocDescAndViewId(FocDesc focDesc, int viewId){
		TableDefinition tableDefinition = getTableDefinitionForFocDesc(focDesc);
		GuiBrowse aBrowseViewDefinition = null;
		//boolean found = false;
		if(tableDefinition != null){
			FocList browseViewDefinitionList = tableDefinition.getBrowseViewDefinitionList();
			if(browseViewDefinitionList != null){
				aBrowseViewDefinition = (GuiBrowse) browseViewDefinitionList.searchByRealReferenceOnly(viewId);
			}
			/*Iterator<GuiBrowse> iter = browseViewDefinitionList.focObjectIterator();
			while(iter != null && iter.hasNext() && !found){
				aBrowseViewDefinition = iter.next();
				if(aBrowseViewDefinition != null && aBrowseViewDefinition.getReference().getInteger() == viewId){
					found = true;
				}
			}*/
		}
		return aBrowseViewDefinition ;
	}
	
	public static GuiDetails getDetailsViewDefinitionForFocDescAndViewId(FocDesc focDesc, int viewId){
		TableDefinition tableDefinition = getTableDefinitionForFocDesc(focDesc);
		GuiDetails detailsViewDefinition = null;
		if(tableDefinition != null){
			detailsViewDefinition = tableDefinition.getDetailsViewDefinitionForViewId(viewId);
		}
		return detailsViewDefinition;
	}
}

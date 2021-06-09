package b01.fab.model.filter;

import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.list.FocList;
import b01.foc.list.filter.FilterDesc;
import b01.foc.list.filter.FocListFilterBindedToList;

public class FilterDefinition extends FocObject {
	
	public static final int VIEW_ID_DEFAULT = 1;
	public static final int VIEW_ID_FOR_NEW_ITEM = 2;
	
	private FilterDesc filterDesc = null;

	public FilterDefinition(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public void dispose(){
		super.dispose();
		this.filterDesc = null;
	}
	
	public void setTitle(String title){
		setPropertyString(FilterDefinitionDesc.FLD_TITLE, title);
	}
	
	public String getTitle(){
		return getPropertyString(FilterDefinitionDesc.FLD_TITLE);
	}
	
	public String getBaseFocDescName(){
		return getPropertyMultipleChoiceStringBased(FilterDefinitionDesc.FLD_BASE_FOC_DESC);
	}
	
	public FocDesc getBaseFocDesc(){
		return getPropertyDesc(FilterDefinitionDesc.FLD_BASE_FOC_DESC);
	}
	
	public String getFilterTableName(){
		String name = getBaseFocDescName();
		if(name != null){
			name = FocListFilterBindedToList.getFilterTableName(name);
		}
		return name;
	}
	
	public FocList getFieldDefinitionList(){
		return getPropertyList(FilterDefinitionDesc.FLD_FILTER_FIELD_DEFINITION_LIST);
	}
	
	@SuppressWarnings("unchecked")
	public FilterDesc getFilterDesc(){
		if(filterDesc == null){
			filterDesc = new FilterDesc(getBaseFocDesc());
			/*FocLinkForeignKey link = new FocLinkForeignKey(FilterFieldDefinitionDesc.getInstance(), FilterFieldDefinitionDesc.FLD_FILTER_DEFINITION, true);
			FocList fieldDefinitionList = new FocList(this, link, null);*/
			FocList fieldDefinitionList = getFieldDefinitionList();
			fieldDefinitionList.loadIfNotLoadedFromDB();
			if(fieldDefinitionList != null){
				Iterator<FilterFieldDefinition> iter = fieldDefinitionList.focObjectIterator();
				while(iter != null && iter.hasNext()){
					FilterFieldDefinition definition = iter.next();
					if(definition != null){
						definition.addConditionToFilterDesc(filterDesc);
					}
				}
			}
		}
		return filterDesc;
	}
	
	/*public static void fillFDescFieldChoices(FDescFieldStringBased descField){
		if(descField != null){
			Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
			while(iter != null && iter.hasNext()){
				IFocDescDeclaration declaration = iter.next();
				if(declaration != null){
					FocDesc focDesc = declaration.getFocDesctiption();
					if(focDesc != null){
						String focDescName = focDesc.getStorageName();
						descField.putChoice(focDescName);
					}
				}
			}
		}
	}*/
	
	public static FocDesc getFilterFocDesc(String baseDescStorageName){
		String filterTableName = FocListFilterBindedToList.getFilterTableName(baseDescStorageName);
		FocDesc filterFocDesc = Globals.getApp().getFocDescByName(filterTableName);
		return filterFocDesc;
	}

}

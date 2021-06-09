package b01.fab;

import java.util.Iterator;
import b01.fab.model.filter.FilterDefinition;
import b01.fab.model.filter.FilterFieldDefinition;
import b01.fab.model.filter.FilterFieldDefinitionDesc;
import b01.fab.model.filter.FocDescForUserDefinedFilter;
import b01.fab.model.filter.UserDefinedFilter;
import b01.foc.Globals;
import b01.foc.IFocDescDeclaration;
import b01.foc.desc.FocDesc;
import b01.foc.list.FocLinkForeignKey;
import b01.foc.list.FocList;
import b01.foc.list.filter.FilterDesc;

public class FilterFocDescDeclaration implements IFocDescDeclaration {
	
	private FilterDefinition filterDefinition = null;
	private FocDesc focDesc = null;
	private FilterDesc filterDesc = null;
	private boolean constructingFocDesc = false;
	
	private FilterFocDescDeclaration(FilterDefinition fitlerDefinition){
		this.filterDefinition = fitlerDefinition;
		constructingFocDesc = false;
	}
	
	public void dispose(){
		this.filterDefinition = null;
		focDesc = null;
		filterDesc = null;
	}
	
	private FilterDefinition getFilterDefinition(){
		return this.filterDefinition;
	}
	
	public String getTableName(){
		String name = null;
		FilterDefinition definition = getFilterDefinition();
		if(definition != null){
			name = definition.getFilterTableName();
		}
		return name;
	}
	
	@SuppressWarnings("unchecked")
	public FilterDesc getFilterDesc_CreateIfNeeded(){
		if(filterDesc == null){
			FilterDefinition filterDefinition = getFilterDefinition();
			if(filterDefinition != null){
				filterDesc = new FilterDesc(filterDefinition.getBaseFocDesc());
				//dont use the the fucntion filterDefinition.getFilterFieldList() because when this filterDefinition was created 
				//the function FilterFieldDefinitionDesc.getInstance() was not called yet(that means the FObjectField "filterDefinition" 
				//in FilterFieldDefinitionDesc was not created and it has not create the listField in the masterDesc "FilterDefinitionDesc"; see Application.prepareDBForLogin() and fabModule.declareFocObjects())
				//so this object(filterDefinition) was created with out the property FList for this reson we cant use getPropertyList
				//to get the list of filterFieldDefinition
				FocLinkForeignKey link = new FocLinkForeignKey(FilterFieldDefinitionDesc.getInstance(), FilterFieldDefinitionDesc.FLD_FILTER_DEFINITION, true);
				FocList fieldDefinitionList = new FocList(filterDefinition, link, null);
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
		}
		return filterDesc;
	}
	 
	public FocDesc getFocDesctiption() {
		if(!constructingFocDesc){
			constructingFocDesc = true;
			if(focDesc == null){
				focDesc = new FocDescForUserDefinedFilter(UserDefinedFilter.class, FocDesc.DB_RESIDENT, getTableName(), false);
				constructingFocDesc = false;
			}
		}
		return focDesc;
	}
	
	public static FilterFocDescDeclaration getFilterFocDescDeclaration(FilterDefinition filterDefinition){
		FilterFocDescDeclaration declaration = null;
		if(filterDefinition != null){
			String name = filterDefinition.getFilterTableName();
			declaration = (FilterFocDescDeclaration)Globals.getApp().getIFocDescDeclarationByName(name);
			if(declaration == null){
				declaration = new FilterFocDescDeclaration(filterDefinition);
				Globals.getApp().putIFocDescDeclaration(name, declaration);
			}
		}
		return declaration;
	}

	public int getPriority() {
		return IFocDescDeclaration.PRIORITY_THIRD;
	}
}

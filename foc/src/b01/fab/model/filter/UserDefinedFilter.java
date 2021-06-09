package b01.fab.model.filter;

import b01.foc.desc.FocConstructor;
import b01.foc.list.filter.FocListFilter;
import b01.foc.list.filter.FocListFilterBindedToList;

public class UserDefinedFilter extends FocListFilterBindedToList {
	public static final int VIEW_FOR_FILTER_CREATION = 1;
	
	public UserDefinedFilter(FocConstructor constr){
		super(constr);
		newFocProperties();
		setFilterLevel(FocListFilter.LEVEL_DATABASE);
	}
}

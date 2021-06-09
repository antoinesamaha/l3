package b01.fab.model.filter;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.list.filter.FilterCondition;
import b01.foc.list.filter.FilterDesc;
import b01.foc.property.FAttributeLocationProperty;

public class FilterFieldDefinition extends FocObject {

	public FilterFieldDefinition(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public void setFilterDefinition(FilterDefinition filterDefintion){
		setPropertyObject(FilterFieldDefinitionDesc.FLD_FILTER_DEFINITION, filterDefintion);
	}
	
	public FilterDefinition getFilterDefinition(){
		return (FilterDefinition)getPropertyObject(FilterFieldDefinitionDesc.FLD_FILTER_DEFINITION);
	}
	
	private FFieldPath getFieldPath(){
		FAttributeLocationProperty attributeLocationProp = (FAttributeLocationProperty)getFocProperty(FilterFieldDefinitionDesc.FLD_CONDITION_PROPERTY_PATH);
		return attributeLocationProp != null ? attributeLocationProp.getFieldPath() : null;
	}
	
	private FField getConditionLastField(){
		FFieldPath fieldPath = getFieldPath();
		FField conditionField = null;
		if(fieldPath != null){
			conditionField = fieldPath.getFieldFromDesc(getFilterDefinition().getBaseFocDesc());
		}
		return conditionField;
	}
	
	public void addConditionToFilterDesc(FilterDesc filterDesc){
		if(filterDesc != null){
			FField conditionField = getConditionLastField();
			if(conditionField != null){
				FocDesc baseFocDesc = getFilterDefinition().getBaseFocDesc();
				FilterCondition filterCondition = conditionField.getFilterCondition(getFieldPath(), baseFocDesc);
				if(filterCondition != null){
					filterDesc.addCondition(filterCondition);
				}
			}else{
				Globals.getDisplayManager().popupMessage("Filter condition not added for " + getFilterDefinition().getBaseFocDescName() + " filter");
			}
		}
	}

}

package b01.fab.model.filter;

import java.awt.Component;

import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FilterFieldDefinitionGuiDetailsPanel extends FPanel {
	
	public FilterFieldDefinitionGuiDetailsPanel(FocObject focObject, int viewID){
		super("Filter field", FPanel.FILL_NONE);
		if(focObject != null){
			Component comp = focObject.getGuiComponent(FilterFieldDefinitionDesc.FLD_CONDITION_PROPERTY_PATH);
			add("Condition path", comp, 0, 0);
			
			FValidationPanel validPanel = showValidationPanel(true);
			validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
		}
	}
}

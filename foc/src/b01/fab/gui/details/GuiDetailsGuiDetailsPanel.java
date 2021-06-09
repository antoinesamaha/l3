package b01.fab.gui.details;

import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;

@SuppressWarnings("serial")
public class GuiDetailsGuiDetailsPanel extends FPanel {
	
	private GuiDetails detailsViewDefinition = null;
	
	public GuiDetailsGuiDetailsPanel(FocObject detailsViewDefinition, int viewId){
		super("Gui details", FPanel.FILL_BOTH);
		this.detailsViewDefinition = (GuiDetails)detailsViewDefinition;
		FPanel detailsFieldDefinitionPanel = new GuiDetailsComponentGuiBrowsePanel(this.detailsViewDefinition.getGuiDetailsFieldList(), viewId);
		add(detailsFieldDefinitionPanel, 0, 0);
	}

}

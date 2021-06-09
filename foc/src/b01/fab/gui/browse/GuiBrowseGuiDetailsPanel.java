package b01.fab.gui.browse;

import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;

@SuppressWarnings("serial")
public class GuiBrowseGuiDetailsPanel extends FPanel {
	
	private GuiBrowse browseViewDefinition = null;
	
	public GuiBrowseGuiDetailsPanel(FocObject browseViewDefinition, int viewId){
		this.browseViewDefinition = (GuiBrowse) browseViewDefinition;
		FPanel browseColumnBrowsePanel = new GuiBrowseColumnGuiBrowsePanel(this.browseViewDefinition.getBrowseColumnList(), viewId);
		add(browseColumnBrowsePanel, 0, 0);
	}

}

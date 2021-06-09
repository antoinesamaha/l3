package b01.fab;

import javax.swing.JSplitPane;

import b01.fab.model.table.TableDefinitionGuiBrowsePanel;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGCurrentItemPanel;
import b01.foc.gui.FGSplitPane;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FocApplicationBuilderGuiDetailsPanel extends FPanel {
	
	public FocApplicationBuilderGuiDetailsPanel(FocObject applicationBuilder, int view){
		FGSplitPane splitPan = new FGSplitPane();
		splitPan.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPan.setOneTouchExpandable(true);
		
		FListPanel tablesBrowsePanel = new TableDefinitionGuiBrowsePanel(null, FocObject.DEFAULT_VIEW_ID);
		splitPan.setLeftComponent(tablesBrowsePanel);
  	
  	FGCurrentItemPanel currPanel = new FGCurrentItemPanel(tablesBrowsePanel, FocApplicationBuilder.VIEW_NO_EDIT);
  	splitPan.setRightComponent(currPanel);
		setFrameTitle("Tables");
  	setMainPanelSising(FPanel.FILL_BOTH);
		add(splitPan,0,0);
		
		FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(tablesBrowsePanel.getFocList());	
    }
	}
}

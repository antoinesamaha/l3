package b01.foc.list.filter;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class FocListFilterGuiBrowsePanel extends FListPanel {
		
	public FocListFilterGuiBrowsePanel(FocList focList, int viewID){
		super("Filters", FPanel.FILL_VERTICAL);
		if(focList != null){
			FocDesc filterFocDesc = focList.getFocDesc();
			if(filterFocDesc != null){
				focList.setDirectlyEditable(true);
				try {
					setFocList(focList);
				} catch (Exception e) {
					Globals.logException(e);
				}
				FTableView tableView = getTableView();
				tableView.setDetailPanelViewID(viewID);
				tableView.addColumn(filterFocDesc, FField.FLD_NAME, true);
				
				construct();
				
				/*FValidationPanel validPanel = showValidationPanel(true);
				if(validPanel != null){
					validPanel.addSubject(focList);
				}*/
				setFrameTitle("User defined filter");
				setMainPanelSising(FPanel.FILL_BOTH);
				showAddButton(true);
				showRemoveButton(true);
				showEditButton(true);
			}
		}
	}
}

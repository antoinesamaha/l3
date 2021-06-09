package b01.l3;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class L3ApplicationGuiBrowsePanel extends FPanel {
	public  L3ApplicationGuiBrowsePanel(FocList list, int viewID){
		FocDesc desc = L3Application.getFocDesc();

    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
      	list = L3Application.getFocDesc().getDefaultFocList(FocList.FORCE_RELOAD);
      }
      if (list != null) {
        list.setDirectImpactOnDatabase(false);
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        FTableColumn col = null;

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3ApplicationDesc.FLD_APPLICATION_MODE), L3ApplicationDesc.FLD_APPLICATION_MODE, "Mode",30, false);
        tableView.addColumn(col);
                
        selectionPanel.construct();

        selectionPanel.setDirectlyEditable(false);
        
      	FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
        if (savePanel != null) {
          list.setFatherSubject(null);
          savePanel.addSubject(list);	
        }

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(true);
        selectionPanel.showAddButton(true);
        selectionPanel.showRemoveButton(true);
        selectionPanel.showDuplicateButton(false);
      }
    }
    setFrameTitle("Application mode");
    selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    add(selectionPanel,0,0);
	} 
}

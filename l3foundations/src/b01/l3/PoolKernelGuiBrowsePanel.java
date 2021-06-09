// PROPERTIES
// INSTANCE
// PANEL
//    STOCK VIEW
//    STATIC LISTENERS
//    PANEL
// LIST
// DESCRIPTION


/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public class PoolKernelGuiBrowsePanel extends FPanel{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 2243921085364838180L;

	public PoolKernelGuiBrowsePanel(FocList list, int viewID){
    FocDesc desc = PoolKernel.getFocDesc();
    if(list == null){
      list = PoolKernel.getList(FocList.LOAD_IF_NEEDED);
    }
    
    //list = new FocList(getPoolUsersLink());
    //list.loadIfNotLoadedFromDB();
    
    //FPanel mainPanel = new FPanel();
    FListPanel selectionPanel = null;

    if (desc != null) {
      if (list != null) {
        list.setDirectImpactOnDatabase(true); 
        
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        tableView.setDetailPanelViewID(viewID);
        
        FTableColumn col = null;

        col = new FTableColumn(desc, FFieldPath.newFieldPath(PoolKernelDesc.FLD_NAME), PoolKernelDesc.FLD_NAME, "Name", true);
        tableView.addColumn(col);
        
        selectionPanel.construct();

        selectionPanel.setDirectlyEditable(false);

        //FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel, 0);
        add(selectionPanel, 0, 0);
        //mainPanel.add(currentItemPanel, 1, 0);

        //FPanel panel = selectionPanel.getTotalsPanel();
        
        
        FValidationPanel savePanel = showValidationPanel(true);
        if (savePanel != null) {
          list.setFatherSubject(null);
          savePanel.addSubject(list);
        }

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(true);
        selectionPanel.showDuplicateButton(false);
        selectionPanel.showAddButton(viewID == L3Globals.VIEW_CONFIG);
        selectionPanel.showRemoveButton(viewID==L3Globals.VIEW_CONFIG);
      }
    }
    setFrameTitle("Pools");
    setMainPanelSising(FPanel.MAIN_PANEL_FILL_VERTICAL);
  }  
}

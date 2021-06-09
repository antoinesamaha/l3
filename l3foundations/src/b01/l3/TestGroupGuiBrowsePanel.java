/*
 * Created on Dec 5, 2006
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
@SuppressWarnings("serial")
public class TestGroupGuiBrowsePanel extends FPanel{ 
	
	public TestGroupGuiBrowsePanel(FocList list, int viewID){
    FocDesc desc = TestGroupDesc.getInstance();

    if(list == null){
      list = TestGroupDesc.getList();
      list.loadIfNotLoadedFromDB();
    }
    
    FListPanel selectionPanel = null;

    if (desc != null) {
      if (list != null) {
        list.setDirectImpactOnDatabase(true); 
        
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        FTableColumn col = null;

        col = new FTableColumn(desc, FFieldPath.newFieldPath(TestGroupDesc.FLD_NAME), TestGroupDesc.FLD_NAME, "Group", true);
        tableView.addColumn(col);
        
        selectionPanel.construct();
        selectionPanel.setDirectlyEditable(true);

        add(selectionPanel, 0, 0);
        
        FValidationPanel savePanel = showValidationPanel(true);
        if (savePanel != null) {
          savePanel.addSubject(list);
        }

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(false);
        selectionPanel.showDuplicateButton(false);
      }
    }
    setFrameTitle("Tests Groups");
    setMainPanelSising(FPanel.MAIN_PANEL_NONE);
  }
}

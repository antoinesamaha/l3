package b01.foc.admin;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;
import b01.foc.gui.FPanel;
import b01.foc.gui.FTreeTablePanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class MenuRightsGuiTreePanel extends FPanel{

  
  public MenuRightsGuiTreePanel(FocList list, int viewID){
    super("Menu Rights", FPanel.FILL_BOTH);
    setFill(FPanel.FILL_HORIZONTAL);
    setMainPanelSising(FPanel.MAIN_PANEL_FILL_HORIZONTAL);
    
    FocDesc desc = MenuRightsDesc.getInstance();
    if (desc != null) {
      if(list == null){
        list = MenuRightsDesc.getList(FocList.FORCE_RELOAD);
      }
      if (list != null) {
        MenuRightsObjectTree menuRightsTree = new MenuRightsObjectTree(list, viewID);
        
        FTreeTablePanel selectionPanel = new FTreeTablePanel(menuRightsTree);
        FTableView tableView = selectionPanel.getTableView();

        FTableColumn col = tableView.getColumnById(FField.TREE_FIELD_ID);
        col.setEditable(false);
        tableView.addColumn(list.getFocDesc(), MenuRightsDesc.FLD_RIGHT, 5, true);
        
        selectionPanel.construct();

        selectionPanel.showEditButton(false);
        selectionPanel.showAddButton(false);
        selectionPanel.showRemoveButton(false);
        
        add(selectionPanel,0,0);
      }
    }
  }
}

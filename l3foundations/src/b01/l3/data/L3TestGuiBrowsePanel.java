/*
 * Created on Jun 5, 2006
 */
package b01.l3.data;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.l3.FocAppGroup;
import b01.l3.L3Globals;

/**
 * @author 01Barmaja
 */
public class L3TestGuiBrowsePanel extends FPanel{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 2243921085364838180L;
  
  FocAppGroup group = (FocAppGroup)Globals.getApp().getAppGroup();
  private int realView = 0;
  private boolean isLocked = false;

	public L3TestGuiBrowsePanel(FocList list, int viewID){
	
		FocDesc desc = L3Test.getFocDesc();
    realView = L3Globals.view_ExtractRealViewId(viewID);
    isLocked = L3Globals.view_IsViewLocked(viewID);
    
    boolean editable = group.allowResultConfirmation() ; //&& ((L3Sample)list.getFatherSubject()).getStatus() != L3SampleDesc.SAMPLE_STATUS_COMMITED_TO_LIS && !isLocked;

    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
      	list = L3Test.getFocDesc().getDefaultFocList(FocList.FORCE_RELOAD);
      }
      if (list != null) {
        FocListOrder order = new FocListOrder(L3TestDesc.FLD_LABEL);
        list.setListOrder(order);
        list.setDirectImpactOnDatabase(false);
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        FTableColumn col = null;

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3TestDesc.FLD_LABEL), L3TestDesc.FLD_LABEL, "Test", 8, editable);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3TestDesc.FLD_VALUE), L3TestDesc.FLD_VALUE, "Result", 10, editable);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3TestDesc.FLD_VALUE_NOTES), L3TestDesc.FLD_VALUE_NOTES, "Result", 30, editable);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3TestDesc.FLD_ALARM), L3TestDesc.FLD_ALARM, "Alarm", 3, editable);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3TestDesc.FLD_UNIT_LABEL), L3TestDesc.FLD_UNIT_LABEL, "Unit", 5, editable);
        tableView.addColumn(col);
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3TestDesc.FLD_RESULT_OK), L3TestDesc.FLD_RESULT_OK, "OK", 4, editable);
        tableView.addColumn(col);
        //tableView.setColumnWidthFactor(0.75);
                
        selectionPanel.construct();

        selectionPanel.setDirectlyEditable(true);
        
      	FValidationPanel savePanel = selectionPanel.showValidationPanel(false);
        if (savePanel != null) {
          list.setFatherSubject(null);
          savePanel.addSubject(list);	
        }
       /* FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel, viewID);
        currentItemPanel.setFill(FPanel.FILL_NONE);
        add(currentItemPanel, 1, 0, GridBagConstraints.NORTHWEST);*/

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(false);
        selectionPanel.showDuplicateButton(false);
        selectionPanel.showAddButton(group.allowSampleModification() && group.allowResultConfirmation() && !isLocked);
        selectionPanel.showRemoveButton(group.allowSampleModification() && group.allowResultConfirmation() && !isLocked);
      }
    }
    selectionPanel.setFrameTitle("Tests");
    setFill(FPanel.FILL_VERTICAL);
    add(selectionPanel,0,0);
	}  
}

/*
 * Created on September 29, 2007
 */
package b01.l3.data;

import java.awt.GridBagConstraints;

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
public class L3SampleMessageGuiBrowsePanel extends FPanel{	

	private FListPanel selectionPanel = null;
	private FocList listToDispose = null;
	private L3SampleMessageJoinTree tree = null;
	private boolean doDelete = false;
	
	public L3SampleMessageGuiBrowsePanel(FocList list, int viewID){
		L3SampleTestJoinDesc desc = (L3SampleTestJoinDesc) L3SampleTestJoinDesc.getInstance();

    if (desc != null) {
    	if(list == null){
	    	list = L3SampleMessageJoinDesc.newList();
	    	listToDispose = list;
    	}
      if (list != null) {
        //tree = new L3SampleMessageJoinTree(list, viewID);
        
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();       
        
        FTableColumn col = null;
        tableView.addLineNumberColumn();
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DESCRIPTION), L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DESCRIPTION, "Test Desc.", false);
        tableView.addColumn(col);
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_FIRST_NAME), L3SampleDesc.FLD_FIRST_NAME, "First name", 15, false);
        tableView.addColumn(col);
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_LAST_NAME), L3SampleDesc.FLD_LAST_NAME, "Last name", 15, false);
        tableView.addColumn(col);
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_LIQUIDE_TYPE), L3SampleDesc.FLD_LIQUIDE_TYPE, "Liq.", 10, false);
        tableView.addColumn(col);
        tableView.addColumn(desc, L3SampleDesc.FLD_AGE, "Age", 5, false);
        tableView.addColumn(desc, L3SampleDesc.FLD_SEXE, "Sex", 5, false);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_ENTRY_DATE), L3SampleDesc.FLD_ENTRY_DATE, "Date", 14, false);
        tableView.addColumn(col);
        
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_SUGGESTED_INSTRUMENT, "Suggested Inst.", 10, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DISPATCH_INSTRUMENT, "Dispatch to Inst.", 10, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_RECEIVE_INSTRUMENT, "Reception Inst.", 10, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_STATUS, "Status", 15, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_LABEL, "Test", 10, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_VALUE, "Value", 8, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_VALUE_NOTES, "Value", 20, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_ALARM, "Alarm", 3, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_RESULT_OK, "Ok", 5, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_BLOCKED, "Blocked", 3, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_MESSAGE, "Message", 15, false);
        
        selectionPanel.construct();

        selectionPanel.showFilterButton(true);
        selectionPanel.setFill(FPanel.FILL_BOTH);
      }
    }
    
    FValidationPanel validPanel = showValidationPanel(true);
   	validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    
    //selectionPanel.setFrameTitle("Samples");
    selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    selectionPanel.showModificationButtons(false);
    selectionPanel.setFill(FPanel.FILL_BOTH);
    add(selectionPanel, 0, 0, 1, 1, 0.9, 0.1, GridBagConstraints.EAST, GridBagConstraints.BOTH);
    selectionPanel.showColomnSelectorButton(true, "SMPL_TST_JOIN");
    //setFrameTitle("Samples");
    setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    setFill(FPanel.FILL_BOTH);    
	}
	
	public void dispose(){
		if(listToDispose != null){
			listToDispose.dispose();
			listToDispose = null;
		}
		
		if(tree != null){
			tree.dispose();
			tree = null;
		}
		
		selectionPanel = null;
		super.dispose();
	}

	public boolean isDoDelete() {
		return doDelete;
	}
}

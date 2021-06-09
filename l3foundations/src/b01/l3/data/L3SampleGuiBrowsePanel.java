/*
 * Created on Jun 5, 2006
 */
package b01.l3.data;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FGButton;
import b01.foc.gui.FGCurrentItemPanel;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;
import b01.foc.list.filter.FocListFilter;
import b01.l3.FocAppGroup;
import b01.l3.L3Globals;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class L3SampleGuiBrowsePanel extends FPanel{
  
  private boolean isLocked = false;
  private int realView = 0;
  
	public L3SampleGuiBrowsePanel(FocList list, int viewID){
    realView = L3Globals.view_ExtractRealViewId(viewID);
    isLocked = L3Globals.view_IsViewLocked(viewID);
		FocDesc desc = L3Sample.getFocDesc();
    FocAppGroup group = (FocAppGroup)Globals.getApp().getAppGroup();
    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
      	list = L3Sample.getFocDesc().getDefaultFocList(FocList.FORCE_RELOAD);
      }
      if (list != null) {
        list.setDirectImpactOnDatabase(false);
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();       
        tableView.setDetailPanelViewID(L3Globals.view_BuildViewId(0, isLocked));
        
        FocConstructor constr = new FocConstructor(L3SampleFilter.getFocDesc(), null);
        L3SampleFilter filter = (L3SampleFilter) constr.newItem();
        tableView.setFilter(filter);
        filter.makeForCurrentDateOnly();
        filter.setFilterLevel(FocListFilter.LEVEL_DATABASE);
        
        tableView.addLineNumberColumn();
        //tableView.addStatusColumn();
        
        FTableColumn col = null;
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_ID), L3SampleDesc.FLD_ID, "Sample ID", group.allowSampleModification()&& !isLocked);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_FIRST_NAME), L3SampleDesc.FLD_FIRST_NAME, "First name", 15, group.allowSampleModification() && !isLocked);
        tableView.addColumn(col);
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_LAST_NAME), L3SampleDesc.FLD_LAST_NAME, "Last name", 15, group.allowSampleModification() && !isLocked);
        tableView.addColumn(col);
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_LIQUIDE_TYPE), L3SampleDesc.FLD_LIQUIDE_TYPE, "Liq.", 10, group.allowSampleModification() && !isLocked);
        tableView.addColumn(col);

        if ((realView == L3Globals.VIEW_SAMPLES_WAIT_FOR_SENDING_ACCEPTATION_ONLY || realView== L3Globals.VIEW_SAMPLES_WAIT_FOR_BOTH) ){
          col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_OK_TO_BE_SENT), L3SampleDesc.FLD_OK_TO_BE_SENT, "send",6, !isLocked);
          tableView.addColumn(col);
        }
        
        if ((realView == L3Globals.VIEW_SAMPLES_WAIT_FOR_RESULT_CONFIRMATION_ONLY || realView == L3Globals.VIEW_SAMPLES_WAIT_FOR_BOTH) ){
          col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_RESULT_CONFIRMED), L3SampleDesc.FLD_RESULT_CONFIRMED, "Valid",7, group.allowResultConfirmation() && !isLocked);
          tableView.addColumn(col);
        }
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_ENTRY_DATE), L3SampleDesc.FLD_ENTRY_DATE, "Date",14, false);
        tableView.addColumn(col);
        //tableView.setColumnWidthFactor(0.60);
        selectionPanel.construct();

        selectionPanel.setDirectlyEditable(true);
        
      	/*FValidationPanel savePanel = showValidationPanel(true);
        if (savePanel != null) {
          //list.setFatherSubject(null);
          list.forceControler(true);
          savePanel.addSubject(list);	
        }*/
        
        FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel, L3Globals.view_BuildViewId(1, isLocked));
        currentItemPanel.setFill(FPanel.FILL_NONE);
        add(currentItemPanel, 1, 0, 1, 1, 0.1, 0.99, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH);

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(false);
        selectionPanel.showDuplicateButton(false);
        selectionPanel.showFilterButton(true);
        selectionPanel.showAddButton(group.allowSampleModification() && ! isLocked);
        selectionPanel.showRemoveButton(group.allowSampleModification() && !isLocked);
        selectionPanel.setFill(FPanel.FILL_BOTH);

        FPanel totalsPanel = selectionPanel.getTotalsPanel();
        if ((realView == L3Globals.VIEW_SAMPLES_WAIT_FOR_RESULT_CONFIRMATION_ONLY || realView == L3Globals.VIEW_SAMPLES_WAIT_FOR_BOTH)){
          FGButton btnPostConfirmedSamples = new FGButton("Post to LIS");
          btnPostConfirmedSamples.setEnabled( !isLocked);
          btnPostConfirmedSamples.setBackground(L3Sample.resultAvailableColor);
          final FocList sampleList = list;
          btnPostConfirmedSamples.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
            	/*HHH
               for (int i = 0; i < sampleList.size(); i++){
                 L3Sample sample = (L3Sample)sampleList.getFocObject(i);
                 if(sample.getStatus()== L3SampleDesc.SAMPLE_STATUS_RESULT_AVAILABLE && sample.isResultConfirmed()){
                   sample.validate(false);
                 }
               }
               */
            }
          });
          totalsPanel.add(btnPostConfirmedSamples,0,0);
        }
        
        if ((realView == L3Globals.VIEW_SAMPLES_WAIT_FOR_SENDING_ACCEPTATION_ONLY || realView == L3Globals.VIEW_SAMPLES_WAIT_FOR_BOTH)){
          FGButton btnSendToInstrument = new FGButton("Send to instrument");
          btnSendToInstrument.setEnabled(!isLocked);
          btnSendToInstrument.setBackground(L3Sample.availableINL3Color);
          btnSendToInstrument.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
            	/*HHH
               for (int i = 0; i < sampleList.size(); i++){
                 L3Sample sample = (L3Sample)sampleList.getFocObject(i);
                 if(sample.getStatus()== L3SampleDesc.SAMPLE_STATUS_AVAILABLE_IN_L3 && sample.isOkToBeSent()){
                   sample.validate(false);
                 }else{
                 }
                 sampleArray.add(sample);
               }
               printDebug();
               */
            }
          });
          totalsPanel.add(btnSendToInstrument,1,0);
        }
        
        FGButton btnDeleteVisibles = new FGButton("Delete all visibles");
        btnDeleteVisibles.setEnabled(!isLocked);
        final FocList sampleList = list;
        btnDeleteVisibles.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent evt){
            boolean ok = false;
            int dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                "Delete all visible samples?",
                "01Barmaja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null);
            
            switch(dialogRet){
            case JOptionPane.YES_OPTION:
              ok = true;
              break;
            case JOptionPane.NO_OPTION:
              ok = false;
              break;
            }
            if (ok){
              
              for (int i = sampleList.size()-1; i >= 0 ; i--){
                L3Sample sample = (L3Sample)sampleList.getFocObject(i);
                //sample.delete();
                sample.setDeleted(true);
              }
              sampleList.forceControler(true);
              sampleList.validate(false);
            }
            sampleList.reloadFromDB();
          }    
        });
        totalsPanel.add(btnDeleteVisibles,2,0);
      }
    }
    //selectionPanel.setFrameTitle("Samples");
    selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    selectionPanel.setFill(FPanel.FILL_BOTH);
    add(selectionPanel, 0, 0, 1, 1, 0.9, 0.1, GridBagConstraints.EAST, GridBagConstraints.BOTH);
    //setFrameTitle("Samples");
    setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    setFill(FPanel.FILL_BOTH);
	}  
}

/*
 * Created on Jun 5, 2008
 */
package b01.l3.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FGButton;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class L3InstrumentGraphGuiBrowsePanel extends FListPanel{
   
	public L3InstrumentGraphGuiBrowsePanel(FocList list, int viewID){
		FocDesc desc = L3InstrumentGraphDesc.getInstance();
    if (desc != null) {
      if(list == null){
      	list = L3InstrumentGraphDesc.getInstance().getDefaultFocList(FocList.FORCE_RELOAD);
      }
      list.setDirectImpactOnDatabase(true);
      try{
        setFocList(list);
      }catch(Exception e){
        Globals.logException(e);
      }
      FTableView tableView = getTableView();       
            
      tableView.addLineNumberColumn();
     
      FTableColumn col = null;
      col = new FTableColumn(desc, FFieldPath.newFieldPath(L3InstrumentGraphDesc.FLD_INSTRUMENT_CODE), L3InstrumentGraphDesc.FLD_INSTRUMENT_CODE, "Instrument", false);
      tableView.addColumn(col);

      col = new FTableColumn(desc, FFieldPath.newFieldPath(L3InstrumentGraphDesc.FLD_SAMPLE_ID), L3InstrumentGraphDesc.FLD_SAMPLE_ID, "Sample ID", false);
      tableView.addColumn(col);
      
      col = new FTableColumn(desc, FFieldPath.newFieldPath(L3InstrumentGraphDesc.FLD_STATUS), L3InstrumentGraphDesc.FLD_STATUS, "Status", false);
      tableView.addColumn(col);
      
      construct();

      setDirectlyEditable(false);
            
      requestFocusOnCurrentItem();
      showEditButton(true);
      showDuplicateButton(false);
      showFilterButton(false);
      showAddButton(false);
      showRemoveButton(true);
      setFill(FPanel.FILL_BOTH);

      FGButton btnDeleteVisibles = new FGButton("Delete all visibles");
      btnDeleteVisibles.setEnabled(true);
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
              L3InstrumentGraph sample = (L3InstrumentGraph) sampleList.getFocObject(i);
              sample.setDeleted(true);
            }
            sampleList.forceControler(true);
            sampleList.validate(false);
          }
          sampleList.reloadFromDB();
        }    
      });
    }
    setFrameTitle("Graphs");
    setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    setFill(FPanel.FILL_BOTH);
    
    showValidationPanel(true);
	}  
}

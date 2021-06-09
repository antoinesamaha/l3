package b01.l3;

import javax.swing.SwingUtilities;

import b01.foc.Globals;
import b01.foc.db.SQLUpdate;
import b01.foc.desc.FocObject;
import b01.foc.event.FValidationListener;
import b01.foc.gui.FGTabbedPane;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.list.FocListElement;
import b01.foc.list.FocListIterator;
import b01.foc.list.FocListOrder;

@SuppressWarnings("serial")
public class DispatchingGuiTabbedPanel extends FPanel {

  private TestLabelMapGuiBrowsePanel paneTests = null;
  private InstrumentGuiBrowsePanel paneInstruments = null;
  private FocList instrumentsList = null;
  
  public DispatchingGuiTabbedPanel(int viewID){  	
    super("Dispatcher", FPanel.FILL_BOTH);
    FGTabbedPane tabbedPane = new FGTabbedPane();
    
    if(viewID == L3Globals.VIEW_CONFIG){
    	paneTests = new TestLabelMapGuiBrowsePanel(null, TestLabelMap.VIEW_BROWSE_ALL);
    }else{
    	paneTests = new TestLabelMapGuiBrowsePanel(null, TestLabelMap.VIEW_BROWSE_ALL_NO_EDIT);
    }
    tabbedPane.add("Test Mapping", paneTests);
    
    instrumentsList = new FocList(new FocLinkSimple(Instrument.getFocDesc()));
    instrumentsList.setCollectionBehaviour(true);
    instrumentsList.setListOrder(new FocListOrder(InstrumentDesc.FLD_CODE));
    FocList list = PoolKernel.getList(FocList.LOAD_IF_NEEDED);
    for (int i=0; i<list.size(); i++){
      PoolKernel pool = (PoolKernel)list.getFocObject(i);
      FocList instrList = pool.getInstrumentList();
      for(int j=0; j<instrList.size(); j++){
      	instrumentsList.add(instrList.getFocObject(j));
      }
    }
    paneInstruments = new InstrumentGuiBrowsePanel(instrumentsList, viewID);
    instrumentsList = paneInstruments.getInstrumentList();
    tabbedPane.add("Instruments", paneInstruments);

    add(tabbedPane, 0, 0);
    FValidationPanel validPanel = showValidationPanel(true);
    validPanel.addSubject(paneTests.getBrowseList());
    validPanel.setValidationListener(new FValidationListener(){
      public void postCancelation(FValidationPanel panel) {
      }
      public void postValidation(FValidationPanel panel) {
      	instrumentsList.iterate(new FocListIterator(){
					@Override
					public boolean treatElement(FocListElement element, FocObject focObj) {
						SQLUpdate update = new SQLUpdate(Instrument.getFocDesc(), focObj);
						update.addQueryField(InstrumentDesc.FLD_ON_HOLD);
						update.execute();
						return false;
					}
      	});
      }

      public boolean proceedCancelation(FValidationPanel panel) {
        return true;
      }

      public boolean proceedValidation(FValidationPanel panel) {
        return !paneTests.checkInformationIntegrity();
      }
    });

    SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				Globals.getDisplayManager().violentRefresh();
		    paneTests.setFilterActive(true);
			}
    });
  }
  
  public void dispose(){
    paneTests = null;
    paneInstruments = null; 
  }
}

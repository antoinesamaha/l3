/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.event.FValidationListener;
import b01.foc.gui.FGButton;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;
import b01.foc.list.FocListElement;
import b01.foc.property.FProperty;
import b01.l3.data.L3Test;
import b01.l3.dispatcher.L3GeneralDispatcher;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class TestLabelMapGuiBrowsePanel extends FPanel{
  
	private FocList list = null;	
	private TestLabelMapFilter filter = null;
  //Bantoine - Permute
  private InstrumentGuiBrowsePanel paneInstrumentsForPermutation = null;
  //Eantoine - Permute
	
	@Override
	public void dispose() {
		list = null;
		filter = null;
    //BAntoine - Permute 
    paneInstrumentsForPermutation = null;
    //EAntoine - Permute    
		super.dispose();
	}
	
  //Bantoine - Permute
  private InstrumentGuiBrowsePanel getPaneInstrumentsForPermutation(){
    return paneInstrumentsForPermutation;
  }
  
  protected FocList getTestLabelMapList(){
    return list;
  }
  //Eantoine - Permute
  
  public FocList getBrowseList(){
    return list;
  }

  public void setFilterActive(boolean active){
  	if(filter != null) filter.setActive(active);
  }

	private class FillButtonListener extends AbstractAction{
		private static final long serialVersionUID = 366215481612776773L;

		public void actionPerformed(ActionEvent e) {
			Instrument instr = (Instrument) list.getMasterObject();
			if(instr != null){
				try{
					IDriver driver = instr.getDriver();
					driver.completeListOfAvailableTests(list);
				}catch(Exception ex){
					b01.foc.Globals.logException(ex);
				}
			}
		}
	}

	private boolean checkPriorityUnicity(int priorityField){
		boolean err = false;
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(int i=0; i<list.size() && !err; i++){
			TestLabelMap testMap = (TestLabelMap) list.getFocObject(i);
						
			if(priorityField == 0){
				//If priority field ==0 we are testing the repetition of the same instrument for the same lisTest
				Instrument instr = (Instrument) testMap.getPropertyObject(TestLabelMapDesc.FLD_INSTRUMENT);
				if(instr == null){
					err = true;
					Globals.getDisplayManager().popupMessage("Unselected instrument at LIS Label : "+testMap.getLisTestLabel());
				}else{
					String key = testMap.getLisTestLabel() + "|" + instr.getCode();
					if(map.get(key) != null){
						err = true;
						Globals.getDisplayManager().popupMessage("Duplicate instrument "+instr.getCode()+" for LIS Label : "+testMap.getLisTestLabel());
					}
					map.put(key, testMap);
				}
			}else{
				int priority = testMap.getPropertyInteger(priorityField);
				if(priority == 0){
					err = true;
					Globals.getDisplayManager().popupMessage("Priority = 0 not allowed for LIS Label : "+testMap.getLisTestLabel());
				}else{
					String key = testMap.getLisTestLabel() + "|" + priority;
					if(map.get(key) != null){
						err = true;
						Globals.getDisplayManager().popupMessage("Duplicate priority for LIS Label : "+testMap.getLisTestLabel());
					}else{
						map.put(key, testMap);
					}
				}
			}
		}
		return err;
	}
	
	private boolean checkInstrumentUnicity(){
		return checkPriorityUnicity(0);
	}
	
  public boolean checkInformationIntegrity(){
    boolean err = checkInstrumentUnicity();
    if(!err) err = checkPriorityUnicity(TestLabelMapDesc.FLD_DAY_PRIORITY);
    if(!err) err = checkPriorityUnicity(TestLabelMapDesc.FLD_NIGHT_PRIORITY);
    if(!err) err = checkPriorityUnicity(TestLabelMapDesc.FLD_HOLIDAY_PRIORITY);
    return err;
  }
  
	public TestLabelMapGuiBrowsePanel(FocList list, int viewID){
		super("Tests mapping", FPanel.FILL_BOTH);
		FocDesc desc = TestLabelMap.getFocDesc();
    FListPanel selectionPanel = null;
    
    boolean isDispatcherView = viewID == TestLabelMap.VIEW_BROWSE_ALL || viewID == TestLabelMap.VIEW_BROWSE_ALL_NO_EDIT;
    boolean isEditable = viewID == TestLabelMap.VIEW_BROWSE_ALL;
    
    if (desc != null) {
      if(list == null){
      	list = ((TestLabelMapDesc)TestLabelMap.getFocDesc()).getList(FocList.FORCE_RELOAD);
      }
      this.list = list;
      if (list != null) {
      	list.sort();
        list.setDirectImpactOnDatabase(false);
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        tableView.setDetailPanelViewID(viewID);

        FocConstructor constr = new FocConstructor(TestLabelMapFilter.getFocDesc(), null);
        filter = (TestLabelMapFilter) constr.newItem();
        tableView.setFilter(filter);
        
        FTableColumn col = null;
        
        tableView.addLineNumberColumn();
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(TestLabelMapDesc.FLD_LIS_TEST_LABEL), TestLabelMapDesc.FLD_LIS_TEST_LABEL, "LIS label", 15, isEditable);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(TestLabelMapDesc.FLD_INSTRUMENT_TEST_CODE), TestLabelMapDesc.FLD_INSTRUMENT_TEST_CODE, "Inst. Test code",15, isEditable);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(TestLabelMapDesc.FLD_TEST_DESCRIPTION), TestLabelMapDesc.FLD_TEST_DESCRIPTION, "Description", 15, isEditable);
        tableView.addColumn(col);

        if(isDispatcherView){
          col = new FTableColumn(desc, FFieldPath.newFieldPath(TestLabelMapDesc.FLD_INSTRUMENT), TestLabelMapDesc.FLD_INSTRUMENT, "Instrument", 15, isEditable);
          tableView.addColumn(col);
	        tableView.addColumn(desc, TestLabelMapDesc.FLD_DAY_PRIORITY, isEditable);
	        tableView.addColumn(desc, TestLabelMapDesc.FLD_NIGHT_PRIORITY, isEditable);
	        tableView.addColumn(desc, TestLabelMapDesc.FLD_HOLIDAY_PRIORITY, isEditable);
        }

        tableView.addColumn(desc, TestLabelMapDesc.FLD_TEST_GROUP, 10, isEditable);

        tableView.addColumn(desc, TestLabelMapDesc.FLD_CALCULATED, 10, isEditable);
        tableView.addColumn(desc, TestLabelMapDesc.FLD_ON_HOLD, 10, isEditable);
        
        selectionPanel.construct();
      	filter.setActive(true);

        selectionPanel.setDirectlyEditable(true);

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(false);
      	selectionPanel.showAddButton(isEditable);
      	selectionPanel.showRemoveButton(isEditable);
     		selectionPanel.showDuplicateButton(isEditable && isDispatcherView);
      	selectionPanel.showColomnSelectorButton(true, "L3TSTMAP");
      	selectionPanel.showFilterButton(true);
      	selectionPanel.showPrintButton(true);
      	filter.setActive(false);
      	filter.setActive(true);
      }
      if(!isDispatcherView && isEditable){
      	FGButton buttonFillCodesFromDriver = new FGButton("Fill Codes from Driver");
      	selectionPanel.getTotalsPanel().add(buttonFillCodesFromDriver, 0, 0);
        buttonFillCodesFromDriver.addActionListener(new FillButtonListener());
      }
      coloredRows();
    }

    selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    selectionPanel.setFill(FPanel.FILL_BOTH);
    
    //FGTabbedPane tabbed = new FGTabbedPane();
    //tabbed.add("Test Mapping",selectionPanel);
    add(selectionPanel,0,0);
    
    /*
    selectionPanel.getTable().setDropable(new FocDefaultDropTargetListener(){
			public void drop(DropTargetDropEvent dtde) {
				try{
					Transferable transferable = dtde.getTransferable();
					FocTransferable focTransferable = (FocTransferable) transferable.getTransferData(FocTransferable.getFocDataFlavor());
					super.drop(dtde);
					FocList list = focTransferable.getSourceFocList();
					TestLabelMap.rearrangePriorityList(list);
				}catch(Exception e){
					Globals.logException(e);
				}
			}
    });
    */
    
    FGButton onHoldToggleButton = new FGButton("Toggle on Hold");
    onHoldToggleButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int nbrOfOnHold = 0;
				for(int i=0; i<getFilter().getListVisibleElementCount(); i++){
					TestLabelMap testLabelMap = (TestLabelMap) getFilter().getListVisibleElementAt(i).getFocObject();
					if(testLabelMap.getPropertyBoolean(TestLabelMapDesc.FLD_ON_HOLD)){
						nbrOfOnHold++;
					}
				}
				boolean newValue = true;
				if(nbrOfOnHold * 2 > getFilter().getListVisibleElementCount()){
					newValue = false;
				}
				for(int i=0; i<getFilter().getListVisibleElementCount(); i++){
					TestLabelMap testLabelMap = (TestLabelMap) getFilter().getListVisibleElementAt(i).getFocObject();
					testLabelMap.setPropertyBoolean(TestLabelMapDesc.FLD_ON_HOLD, newValue);
				}
				Globals.getDisplayManager().refresh();
			}
    });
    selectionPanel.getTotalsPanel().add(onHoldToggleButton, 0, 0);

    //BAntoine - Permute
    //This will popup an instrument selection panel then when 2 instruments are selected
    //We will scan all the TestMapLabel and if an LISTest has 2 lines we permute the 2 lines priorities
    FGButton permuteButton = new FGButton("Permute Instrument priorities");
    permuteButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent arg0) {
        paneInstrumentsForPermutation = new InstrumentGuiBrowsePanel(null, L3Globals.VIEW_PERMUTE);
        FValidationPanel vPanel = paneInstrumentsForPermutation.showValidationPanel(true);
        vPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
        vPanel.setValidationButtonLabel("Permute");
        paneInstrumentsForPermutation.setMainPanelSising(FPanel.MAIN_PANEL_FILL_VERTICAL);
        vPanel.setValidationListener(new FValidationListener(){

          public void postCancelation(FValidationPanel panel) {
          }

          public void postValidation(FValidationPanel panel) {
          }

          public boolean proceedCancelation(FValidationPanel panel) {
            return true;
          }

          public boolean proceedValidation(FValidationPanel panel) {
            boolean proceed       = false;
            int     nbrOfSelected = 0;
            Instrument instrument1 = null;
            Instrument instrument2 = null;            
            InstrumentGuiBrowsePanel instrumentSelectionPanel = getPaneInstrumentsForPermutation();
            FocList instList = instrumentSelectionPanel.getInstrumentList();
            for(int i=0; i<instList.size(); i++){
              FocListElement elem = instList.getFocListElement(i);
              if(elem != null){
                if(elem.isSelected()){
                  if(instrument1 == null){
                    instrument1 = (Instrument) instList.getFocObject(i);
                  }else if(instrument2 == null){
                    instrument2 = (Instrument) instList.getFocObject(i);
                  }
                  nbrOfSelected++;
                }
              }
            }
            if(nbrOfSelected != 2){
              Globals.getDisplayManager().popupMessage("Please select 2 instruments");
              proceed = false;
            }else{
              TestLabelMap labelMap1         = null;
              TestLabelMap labelMap2         = null;
              String       previousTestLabel = null; 
              
              FocList testLabelMapList = getTestLabelMapList();
              for(int i=0; i<testLabelMapList.size(); i++){
                TestLabelMap labelMap = (TestLabelMap) testLabelMapList.getFocObject(i);
                if(labelMap != null){
                  if(previousTestLabel != null && !labelMap.getLisTestLabel().equals(previousTestLabel)){
                    if(labelMap1 != null && labelMap2 != null){
                      int swap = 0;
                      swap = labelMap1.getDayPriority();
                      labelMap1.setDayPriority(labelMap2.getDayPriority());
                      labelMap2.setDayPriority(swap);
                      
                      swap = labelMap1.getNightPriority();
                      labelMap1.setNightPriority(labelMap2.getNightPriority());
                      labelMap2.setNightPriority(swap);

                      swap = labelMap1.getHolidayPriority();
                      labelMap1.setHolidayPriority(labelMap2.getHolidayPriority());
                      labelMap2.setHolidayPriority(swap);
                    }
                    labelMap1 = null;
                    labelMap2 = null;
                  }
                  if(labelMap.getInstrument().getCode().equals(instrument1.getCode())){
                    labelMap1 = labelMap;
                  }else if(labelMap.getInstrument().getCode().equals(instrument2.getCode())){
                    labelMap2 = labelMap;
                  }
                }
                previousTestLabel = labelMap.getLisTestLabel(); 
              }
              proceed = true;
            }
            return proceed;
          }
        });
        Globals.getDisplayManager().popupDialog(paneInstrumentsForPermutation, "Select the 2 Instruments to permute", true);
        Globals.getDisplayManager().refresh();
      }
    });
    selectionPanel.getTotalsPanel().add(permuteButton, 1, 0);
    //EAntoine - Permute    
	}
	
  @SuppressWarnings("unchecked")
	public void coloredRows(){
    try{
      L3GeneralDispatcher dispatcher = new L3GeneralDispatcher();
      Iterator<TestLabelMap> testLabelMapIter = list.focObjectIterator();
      FocConstructor testConstr = new FocConstructor(L3Test.getFocDesc(), null);
      L3Test test = (L3Test)testConstr.newItem();
      while (testLabelMapIter.hasNext()){
        FocObject focObj = testLabelMapIter.next();

        TestLabelMap testLabelMap = (TestLabelMap) focObj;
        testLabelMap.adjustOnHoldColor();
        
        String label = focObj.getPropertyString(TestLabelMapDesc.FLD_LIS_TEST_LABEL);
        test.setLabel(label);
        TestLabelMap testMap = dispatcher.getInstrumentForTest(test);
        Instrument instrument = testMap != null ? testMap.getInstrument() : null;

      	FProperty prop = focObj.getFocProperty(TestLabelMapDesc.FLD_INSTRUMENT);
        if(prop != null){
        	if (instrument != null && instrument.getCode().compareTo(focObj.getPropertyString(TestLabelMapDesc.FLD_INSTRUMENT)) == 0){
        	//getPropertyObject(TestLabelMapDesc.FLD_INSTRUMENT).getPropertyString(InstrumentDesc.FLD_CODE)
            prop.setBackground(Color.GREEN);
          }else{
          	prop.setBackground(null);
          }
        }
      }
      test.dispose();
    }catch(Exception e){
      Globals.logException(e); 
    }
  }
  
  public TestLabelMapFilter getFilter(){
  	return filter;
  }
}

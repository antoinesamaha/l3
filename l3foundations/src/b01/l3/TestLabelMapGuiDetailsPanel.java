// PROPERTIES
// INSTANCE
// PANEL
//    STATIC LISTENERS
//    PANEL
// LIST
// DESCRIPTION


/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.awt.Component;

import b01.foc.gui.FPanel;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class TestLabelMapGuiDetailsPanel extends FPanel{
	//private Instrument instrument = null;

  public TestLabelMapGuiDetailsPanel(int view, TestLabelMap test){
  	Component comp = test.getGuiComponent(TestLabelMapDesc.FLD_LIS_TEST_LABEL);
	  add("LIS label",comp, 0, 0);	
	  comp = test.getGuiComponent(TestLabelMapDesc.FLD_INSTRUMENT_TEST_CODE);
	  add("Instrument test code",comp, 0, 1);
  	
  	
  /*	switch(view){
  	case L3Globals.VIEW_CONFIG:
  		newDetailsPanelConfigView();
  		break;
  	case L3Globals.VIEW_NORMAL:
  		newDetailsPanelNormalView();
  		break;  		
  	case 3:
  		newDetailsPanelConnectionOnly();
  		break;
  	}*/
  }

  public void dispose(){
  	super.dispose();
  	//instrument = null;
  }

	
/*		
	private void newDetailsPanelConfigView() {
		Component comp = instrument.getGuiComponent(InstrumentDesc.FLD_CODE);
	  add("Code",comp, 0, 0);	
	  comp = instrument.getGuiComponent(InstrumentDesc.FLD_NAME);
	  add("Name",comp, 0, 1);
	  comp = instrument.getGuiComponent(InstrumentDesc.FLD_DRIVER_CLASS_NAME);
	  add("Driver",comp, 0, 2);
	  comp = instrument.getGuiComponent(InstrumentDesc.FLD_PROPERTIES_FILE_PATH);
	  add("Prpoerites file",comp, 0, 3);
	  FValidationPanel validPanel = showValidationPanel(true);
	  validPanel.addSubject(instrument);
	}
	
	private void newDetailsPanelNormalView() {//called when edit or add buttons are pressed
		Component comp = instrument.getGuiComponent(InstrumentDesc.FLD_CODE);
	  add("Code",comp, 0, 0);	
	  comp = instrument.getGuiComponent(InstrumentDesc.FLD_NAME);
	  add("Name",comp, 0, 1);
	}
	
	private void newDetailsPanelConnectionOnly() {
		buttonConnectDisconnect = new FGButton("Connect");
		FGButton buttonShowSamples = new FGButton("Show samples");
		add(buttonConnectDisconnect, 0, 0);
		add(buttonShowSamples, 0, 1);
		buttonConnectDisconnect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				try{
					if (!instrument.isConnected()){
						instrument.connect();	
					}else{
						instrument.disconnect();
					}
						
				}
				catch (Exception e){
					Globals.logException(e);
				}
				toggleConnectStatus();
			}
		});
		buttonShowSamples.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
			FPanel panel =L3Sample.newBrowsePanel(instrument.getSampleList(), 0);
			Globals.getDisplayManager().newInternalFrame(panel);
			}
		});
		
 		adjustColorToStatus();
		// panel.showValidationPanel(true);
	}*/
}

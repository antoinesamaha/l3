
/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import b01.foc.Globals;
import b01.foc.gui.FGButton;
import b01.foc.gui.FGCheckBox;
import b01.foc.gui.FGTabbedPane;
import b01.foc.gui.FGTextField;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class InstrumentGuiDetailsPanel extends FPanel {
	private Instrument instrument = null;    
  private int realView = 0;
  private boolean isLocked = false;

  public InstrumentGuiDetailsPanel(int view, Instrument instrument){
  	this.instrument = instrument;
    
    isLocked = L3Globals.view_IsViewLocked(view);
    realView = L3Globals.view_ExtractRealViewId(view);
  	switch(realView){
  	case L3Globals.VIEW_CONFIG:
  		newDetailsPanelConfigView();
  		break;
  	}
  }
  
  public void dispose(){
    super.dispose();
  	instrument = null;
  }

	private void newDetailsPanelConfigView() {
		Component comp = instrument.getGuiComponent(InstrumentDesc.FLD_CODE);
    comp.setEnabled(comp.isEnabled() && !isLocked);
    add("Instrument Code",comp, 0, 0);	
	  comp = instrument.getGuiComponent(InstrumentDesc.FLD_NAME);
    comp.setEnabled(!isLocked);
	  add("Name",comp, 0, 1);
	  comp = instrument.getGuiComponent(InstrumentDesc.FLD_DRIVER_CLASS_NAME);
    comp.setEnabled(!isLocked);
    add("Driver",comp, 0, 2);
	  FGTextField field = (FGTextField) instrument.getGuiComponent(InstrumentDesc.FLD_PROPERTIES_FILE_PATH);
    field.setEnabled(!isLocked);
    field.setColumns(45);
	  add("Properties File",field, 0, 3);
    comp = instrument.getGuiComponent(InstrumentDesc.FLD_MODE);
    comp.setEnabled(!isLocked);
    add("Sending To Instrument",comp, 0, 4);
    comp = instrument.getGuiComponent(InstrumentDesc.FLD_WAIT_FOR_RESULT_CONFIRMATION);
    comp.setEnabled(!isLocked);
    add("",comp, 0, 5);
    
    comp = add(instrument, InstrumentDesc.FLD_SERVICE_PORT, 0, 6);
    comp.setEnabled(!isLocked);
    
    FGTabbedPane tabbed = new FGTabbedPane();
		FPanel panel = TestLabelMap.newBrowsePanel(instrument.getSupportedTestList(), L3Globals.VIEW_CONFIG);
    panel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_VERTICAL);
    tabbed.add("Supported tests", panel);

    //Here add the panel
    FPanel titledPanel = new FPanel();
    titledPanel.setBorder("Time delais");
    {
	    comp = instrument.getGuiComponent(InstrumentDesc.FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE);
	    comp.setEnabled(!isLocked);
	    titledPanel.add("TimeOut",comp, 0, 0);
	    comp = instrument.getGuiComponent(InstrumentDesc.FLD_DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH);
	    comp.setEnabled(!isLocked);
	    titledPanel.add("AutomaticRefresh",comp, 0, 1);
	    comp = instrument.getGuiComponent(InstrumentDesc.FLD_DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND);
	    comp.setEnabled(!isLocked);
	    titledPanel.add("PollingForDBSampleToSend",comp, 0, 2);
	    comp = instrument.getGuiComponent(InstrumentDesc.FLD_DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE);
	    comp.setEnabled(!isLocked);
	    titledPanel.add("TryAgain",comp, 0, 3);
	    comp = instrument.getGuiComponent(InstrumentDesc.FLD_DELAY_TO_TRY_LATER);
	    comp.setEnabled(!isLocked);
	    titledPanel.add("TryLater",comp, 0, 4);
	    FGButton defaultValuesButton = new FGButton("Reset defaults");
	    defaultValuesButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					instrument.resetDefaults();
				}
	    });
	    titledPanel.add(defaultValuesButton, 1, 5);
    }
    
    tabbed.add("Time delais", titledPanel);

    FPanel instrumentPanel = new FPanel();
    //instrumentPanel.add(titledPanel, 0, 0, 2, 1);
    comp = instrumentPanel.add(instrument, InstrumentDesc.FLD_SERIAL_PORT_NAME, 0, 5);
    comp.setEnabled(!isLocked);
    comp = instrumentPanel.add(instrument, InstrumentDesc.FLD_SERIAL_BAUDE_RATE, 0, 6);
    comp.setEnabled(!isLocked);
    comp = instrumentPanel.add(instrument, InstrumentDesc.FLD_SERIAL_DATA_BITS, 0, 7);
    comp.setEnabled(!isLocked);
    comp = instrumentPanel.add(instrument, InstrumentDesc.FLD_SERIAL_PARITY, 0, 8);
    comp.setEnabled(!isLocked);
    comp = instrumentPanel.add(instrument, InstrumentDesc.FLD_SERIAL_STOP_BIT, 0, 9);
    comp.setEnabled(!isLocked);
    
    final FPanel emulatorPanel = new FPanel();
    {
	    final FGCheckBox checkBox = (FGCheckBox) instrument.getGuiComponent(InstrumentDesc.FLD_IS_EMULATOR);
	    checkBox.setEnabled(!isLocked);
	    final FGTextField comboComp = (FGTextField) instrument.getGuiComponent(InstrumentDesc.FLD_RELATED_INSTRUMENT);
	    comboComp.setEnabled(!isLocked && checkBox.isSelected());
	    comboComp.setVisible(checkBox.isSelected());
	    
	    emulatorPanel.add(checkBox, 0, 0);
	    emulatorPanel.add(comboComp, 1, 0);
	    
	    checkBox.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					comboComp.setEnabled(!isLocked && checkBox.isSelected());
					comboComp.setVisible(checkBox.isSelected());
					Globals.getDisplayManager().violentRefresh();
				}
	    });
    }
    instrumentPanel.add(emulatorPanel, 0, 10, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

    tabbed.add("Serial port", instrumentPanel);
    
    add(tabbed, 0, 7, 2, 1);
    
	  FValidationPanel validPanel = showValidationPanel(true);	  
	  validPanel.addSubject(instrument);
	  
	  setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
	  setFrameTitle(instrument.getName() + " (Instrument)");
	}
}

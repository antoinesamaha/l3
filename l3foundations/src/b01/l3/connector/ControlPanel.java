/*
 * Created on 14-May-2005
 */
package b01.l3.connector;

import b01.foc.gui.*;
import b01.l3.*;

import java.awt.event.*;
import java.awt.*;


/**
 * @author 01Barmaja
 */
public class ControlPanel {
  
  private FGButton startStop = null;
  private boolean started = false;
  private Instrument instrument = null;
  private Color backupColor = null;
  
  public ControlPanel(Instrument instrument){
    this.instrument = instrument;
  }
  
  public FPanel getPanel(){
    FPanel pan = new FPanel();    
    pan.setTitle(instrument.getName());
    startStop = new FGButton("Start");
    backupColor = startStop.getBackground();
    
    startStop.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
      	try{
	        FGButton ssb = getStartStopButton();
	        Instrument mach = getInstrument();
	        if(isStarted()){
	          mach.setConnected(false);
	          ssb.setText("Start");
	          setStarted(false);
	          startStop.setBackground(backupColor);
	        }else{
	          mach.setConnected(true);
	          ssb.setText("Stop");
	          setStarted(true);
	          startStop.setBackground(Color.ORANGE);
	        }
      	}catch(Exception exep){
      		instrument.logException(exep);
      	}
      }
    });
    
    pan.add(startStop, 0, 0);
    pan.setPreferredSize(new Dimension(200, 200));
    pan.setMainPanelSising(FPanel.MAIN_PANEL_NONE);
    return pan;
  }
  /**
   * @return Returns the started.
   */
  public boolean isStarted() {
    return started;
  }
  /**
   * @param started The started to set.
   */
  public void setStarted(boolean started) {
    this.started = started;
  }
  /**
   * @return Returns the startStop.
   */
  public FGButton getStartStopButton() {
    return startStop;
  }
  
  /**
   * @return Returns the machine.
   */
  public Instrument getInstrument() {
    return instrument;
  }
}

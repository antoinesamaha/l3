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
package b01.l3.data;

import java.awt.Component;

import b01.foc.gui.FGTextField;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.l3.L3Globals;

/**
 * @author 01Barmaja
 */
public class L3SampleGuiDetailsPanel extends FPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6682938764643549803L;
	
	private L3Sample sample = null;
  private boolean isLocked = false;
  private int realView = 0;
  
  public L3SampleGuiDetailsPanel(int view, L3Sample sample){
  	this.sample = sample;
  	this.realView = L3Globals.view_ExtractRealViewId(view);
    isLocked = L3Globals.view_IsViewLocked(view);
    
  	switch(realView){
  	case 0 :
  	newDetailPanelNormalView();
  		break;
  	case 1 :
  		newDetailViewShowTests();  	
  	}
  }
  
  private void newDetailPanelNormalView(){
    Component comp = sample.getGuiComponent(L3SampleDesc.FLD_ID);
    add("id",comp, 0, 0);
  	comp = sample.getGuiComponent(L3SampleDesc.FLD_LAST_NAME);
	  add("Last name",comp, 0, 1);	
	  comp = sample.getGuiComponent(L3SampleDesc.FLD_FIRST_NAME);
	  add("First name",comp, 0, 2);
	  comp = sample.getGuiComponent(L3SampleDesc.FLD_MIDDLE_INITIAL);
	  add("Middele Initial",comp, 2, 2);
	  comp = sample.getGuiComponent(L3SampleDesc.FLD_LIQUIDE_TYPE);
	  add("Liquide type",comp, 0, 4);
	  
	  // encore la liste des testes (le browser panel de l3test)
	  
	  FValidationPanel validPanel = showValidationPanel(true);
	  validPanel.addSubject(sample);
  }
  
  private void newDetailViewShowTests(){
  	/*FGButton buttonShowTests = new FGButton("Show tests");
  	add(buttonShowTests,0,0);
  	buttonShowTests.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
			FPanel panel =L3Test.newBrowsePanel(sample.getTestList(), 0);
			Globals.getDisplayManager().newInternalFrame(panel);
			}
		});*/
    FPanel panelLabel = new FPanel();
    FGTextField comp = (FGTextField)sample.getGuiComponent(L3SampleDesc.FLD_ID);
    comp.setEnabled(false);
    comp.setColumns(10);
    panelLabel.add("Id", comp, 0, 0);
    
    comp = (FGTextField)sample.getGuiComponent(L3SampleDesc.FLD_PATIENT_ID);
    comp.setEnabled(false);
    comp.setColumns(10);
    panelLabel.add("Patient id", comp,0,1);
    
    comp = (FGTextField)sample.getGuiComponent(L3SampleDesc.FLD_FIRST_NAME);
    comp.setEnabled(false);
    comp.setColumns(10);
    panelLabel.add("First name",comp, 0, 2);
    
    comp = (FGTextField)sample.getGuiComponent(L3SampleDesc.FLD_LAST_NAME);    
    comp.setEnabled(false);
    comp.setColumns(10);
    panelLabel.add("Last name", comp, 0, 3);
    
    panelLabel.add(comp, 0, 5,2,1);
    FPanel panel = L3Test.newBrowsePanel(sample.getTestList(), L3Globals.view_BuildViewId(0, isLocked));
    
    panelLabel.setFill(FPanel.FILL_NONE);
    add(panelLabel, 0, 0);
    add(panel, 0, 1);
    
    setFill(FPanel.FILL_VERTICAL);
  }
  public void dispose(){
  	super.dispose();
  	sample = null;
  }
}

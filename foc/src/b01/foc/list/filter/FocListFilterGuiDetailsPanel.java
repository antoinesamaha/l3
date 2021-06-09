package b01.foc.list.filter;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import b01.fab.model.filter.UserDefinedFilter;
import b01.foc.desc.FocObject;
import b01.foc.event.FValidationListener;
import b01.foc.gui.FGButton;
import b01.foc.gui.FGButtonAction;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FocListFilterGuiDetailsPanel extends FPanel {
	private FocListFilter focListFilter = null;
	private FValidationPanel validationPanel = null;	
  
  private static final String BUTTON_LABEL_APPLY  = "Apply" ;
  private static final String BUTTON_LABEL_CANCEL = "Cancel";
  private static final String BUTTON_LABEL_RESET  = "Reset" ;
  
	public FocListFilterGuiDetailsPanel(FocObject focObject, int viewID){
    if(focObject != null){
    	focListFilter = (FocListFilter)focObject;
			int y = 0; 
	    FilterDesc filterDesc = focListFilter.getThisFilterDesc();
	    for(int i=0; i<filterDesc.getConditionCount(); i++){
	      FilterCondition cond = filterDesc.getConditionAt(i);
	      GuiSpace space = cond.putInPanel(focListFilter, this, 0, y);
	      y += space.getY();
	    }
	    
      if (viewID != FocListFilter.VIEW_SUMMARY){
  	    if(validationPanel != null){
  	      validationPanel.dispose();
  	      validationPanel = null;
  	    }
  	    validationPanel = showValidationPanel(true);
  	    if(viewID == UserDefinedFilter.VIEW_FOR_FILTER_CREATION){
  	    	validationPanel.addSubject(focListFilter);
  	    }else{
  		    validationPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
  		    validationPanel.setAskForConfirmationForExit(false);
  		    
  		    validationPanel.setCancelationButtonLabel(BUTTON_LABEL_CANCEL);
  		    validationPanel.setValidationButtonLabel(BUTTON_LABEL_APPLY);
  		    if(!focListFilter.isAllwaysActive()){
  		      FGButton removeButton = new FGButton(BUTTON_LABEL_RESET);
  		      removeButton.setName("Remove");/*TEMP*/
  		      removeButton.addActionListener(new ActionListener(){
  		        public void actionPerformed(ActionEvent e) {
  		          focListFilter.setActive(false);
  		          validationPanel.cancel();
  		        }
  		      });
  		      validationPanel.addButton(removeButton);
  		      //BElie
  		      if (focListFilter.isRevisionSupportEnabled()){
  		        removeButton.setVisible(false);
  		      }
  		      //EElie
  		    }    
  		    
  		    validationPanel.setValidationListener(new FValidationListener(){
  		      public boolean proceedValidation(FValidationPanel panel) {
  		        //Globals.logString("list visible count in foc filter : " +getListVisibleElementCount());
  		        return true;
  		      }
  		
  		      public boolean proceedCancelation(FValidationPanel panel) {
  		        return true;
  		      }
  		
  		      public void postValidation(FValidationPanel panel) {   
  		        focListFilter.setActive(true); 
  		      }
  		
  		      public void postCancelation(FValidationPanel panel) {
  		        
  		      }
  		    });
  	    }
      }else{
        setFill(FPanel.FILL_HORIZONTAL);
        setMainPanelSising(GridBagConstraints.NORTHEAST);
        FGButton button = new FGButton(BUTTON_LABEL_APPLY);
        FGButtonAction buttonAction = new FGButtonAction(button) {
          public void focActionPerformed(ActionEvent e) {
            focListFilter.setActive(true);
          }
        };
        button.setAction(buttonAction);
        add(button, 3, 0);        
        button.setText(BUTTON_LABEL_APPLY);//A Swing Bug made the title marked above disapear if we do not reconfirm the text
      }
    }
	}
	
	public void dispose(){
		super.dispose();
		focListFilter = null;
		validationPanel = null;
	}

}

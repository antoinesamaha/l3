package b01.foc.gui;

public abstract class FWizardPanel extends FPanel {

	private FWizardFooterPanel footerPanel = null;
  
	public abstract boolean nextAction();
	public abstract boolean previousAction();
  public boolean cancelAction(){ return false; }
	
	public void dispose(){
		super.dispose();
		footerPanel = null;
	}

	public FGButton getNextButton() {
		return footerPanel.getNextButton();
	}

	public FGButton getPreviousButton() {
		return footerPanel.getPreviousButton();
	}
  
  public FGButton getCancelButton() {
    return footerPanel.getCancelButton();
  }
  
	@SuppressWarnings("serial")
	public void addWizardButtons(){
		footerPanel = new FWizardFooterPanel(){
			@Override
			public boolean nextAction() {
				return FWizardPanel.this.nextAction();
			}

			@Override
			public boolean previousAction() {
				return FWizardPanel.this.previousAction();
			}

			public void cancelAction() {
				FWizardPanel.this.cancelAction();
			}
    };
    FGButton previousButton = footerPanel.getPreviousButton();
		if(previousButton != null){
			previousButton.setEnabled(false);
		}
    setFooterPanel(footerPanel);
	}
}

package b01.foc.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import b01.foc.desc.FocObject;

@SuppressWarnings("serial")
public class RevisionPanel extends FPanel {
  
  private FocObject focObj = null;
  private FGButton revisionButton = null;
  
  public RevisionPanel(String frameTitle, int fill){
    super(frameTitle,fill);
  }
  
  public void dispose(){
    if(focObj != null && revisionButton != null){
      focObj.removeRelatedGuiComponent(revisionButton);  
    }
    super.dispose();
  }
  
  public RevisionPanel getParentRevisionPanel(FocObject focObj, int fieldId){
    this.focObj = focObj;
    RevisionPanel panel = new RevisionPanel("",FPanel.FILL_NONE);
    Component comp = panel.addField(this.focObj, fieldId, 0, 0);
    comp.setEnabled(false);
    revisionButton = new FGButton("+");
    revisionButton.addActionListener(new RevisionIncrementButtonListener(this.focObj));
    panel.addField(revisionButton, 1, 0, FPanel.FILL_NONE);
    this.focObj.addRelatedGuiComponent(revisionButton);
    return panel;
  }
  
  private class RevisionIncrementButtonListener implements ActionListener {
    private FocObject focObj = null;
    public RevisionIncrementButtonListener(FocObject focObj) {
      this.focObj = focObj;
    }
    public void actionPerformed(ActionEvent a) {
      focObj.incrementRevision();
    }
  }
}

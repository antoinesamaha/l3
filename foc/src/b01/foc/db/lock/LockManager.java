// LOCKABLE DESC LIST
// LOCKABLE DESCRIPTION

package b01.foc.db.lock;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.gui.FGTabbedPane;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author 01Barmaja
 */
public class LockManager {
  
  ArrayList<LockableDescription> lockableDescriptions = null;
  
  public LockManager(){
    
  }
  
  public void dispose(){
    listOfLockableDescriptions_dispose();
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LOCKABLE DESC LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private void listOfLockableDescriptions_dispose(){
    if(lockableDescriptions != null){
      for(int i=0; i<lockableDescriptions.size(); i++){
        LockableDescription lockDesc = lockableDescriptions.get(i);
        if(lockDesc != null){
          lockDesc.dispose();
        }
      }
      lockableDescriptions.clear();
      lockableDescriptions = null;
    }
  }
  
  private ArrayList listOfLockableDescriptions_getList(){
    if(lockableDescriptions == null){
      lockableDescriptions = new ArrayList<LockableDescription>();
      Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
      while(iter != null && iter.hasNext()){
      	IFocDescDeclaration focDescDecalaration = iter.next();
      	if(focDescDecalaration != null){
      		FocDesc desc = focDescDecalaration.getFocDesctiption();
      		if(desc != null && desc.isConcurrenceLockEnabled()){
      			lockableDescriptions.add(new LockableDescription(desc));
      		}
      	}
      }
    }
    return lockableDescriptions;
  }
  
  public int listOfLockableDescriptions_Size(){
    ArrayList list = listOfLockableDescriptions_getList();
    return list != null ? list.size() : 0;
  }

  public LockableDescription listOfLockableDescriptions_get(int i){
    ArrayList list = listOfLockableDescriptions_getList();
    return list != null ? (LockableDescription) list.get(i) : null;
  }

  public void unlockAllObjectsForAllDescriptions(){
    for(int i=0; i<listOfLockableDescriptions_Size(); i++){
      LockableDescription lockableDesc = listOfLockableDescriptions_get(i);
      if(lockableDesc != null){
        lockableDesc.unlockAll();
      }
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LOCKABLE RECORD LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public FPanel newDetailsPanel(){
    FPanel panel = new FPanel();
    FGTabbedPane tabbed = new FGTabbedPane();
    panel.add(tabbed, 0, 0);
    
    for(int i=0; i<listOfLockableDescriptions_Size(); i++){
      LockableDescription lockDesc = listOfLockableDescriptions_get(i);
      if(lockDesc != null){
        FPanel descPanel = lockDesc.newRecordBrowsePanel();
        tabbed.add(lockDesc.getTitle(), descPanel);
      }
    }
    
    FValidationPanel validPanel = (FValidationPanel) panel.showValidationPanel(true);
    validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    
    return panel;
  }
}

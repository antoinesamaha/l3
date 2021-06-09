package b01.foc.access;

import b01.foc.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public abstract class AccessSubject extends AccessConsole {
  private AccessControl control = null;
  private int dbResident = DB_RESIDENT_INHERITED;

  private final static int DB_RESIDENT_INHERITED = 0;
  private final static int DB_RESIDENT_TRUE = 1; 
  private final static int DB_RESIDENT_FALSE = 2;
  
  private AccessSubject fatherSubject = null;
  private boolean desactivateSubjectNotifications = false;
  private boolean forceSelfControler = false;
  private boolean transactionalWithChildren = false;
  
  public abstract void commitStatusToDatabase();
  public abstract void undoStatus();
  public abstract void childStatusModification(AccessSubject childSubject);
  public abstract void childStatusUndo(AccessSubject childSubject);
  public abstract void childValidated(AccessSubject childSubject);
  public abstract void doBackup();
  protected abstract void statusModification(int statusModified);
  public abstract boolean isContentValid(boolean displayMessage);
  
  private boolean modified = false;
  private boolean deleted = false;
  private boolean created = false;
  private boolean deletionExecuted = false;
  
  public final static int STATUS_CREATED = 1;
  public final static int STATUS_MODIFIED = 2;  
  public final static int STATUS_DELETED = 3;
  
  public AccessSubject(AccessControl control) {
    super();
    this.control = control;
  }

  public AccessSubject() {
    super();
    this.control = Globals.getDefaultAccessControl();
  }
  
  public void dispose(){
    setFatherSubject(null);
    fatherSubject = null;
    
    super.dispose();
    
    if(control == null){
      control = null;
    }
  }  
  
  public int getHashCodeAsPointer(){
    return super.hashCode();
  }
  
  public boolean isModified() {
    return modified;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public boolean isCreated() {
    return created;
  }

  public void setModified(boolean modified) {
    if (modified != isModified()) {      
      if (!isCreated() && !isDeleted()) {
        /*if( modified && this instanceof FocObject && ((FocObject)this).getReference() != null && ((FocObject)this).getReference().getInteger() == 156 ){
          int debug = 0;
        }*/
        
        this.modified = modified;
        statusModificationInternal(STATUS_MODIFIED);
      }
    }
  } 

  public void setDeleted(boolean deleted) {
    if (deleted != isDeleted()) {
      //this.getOrCreateAccessStatus().setDeleted(deleted);
      this.deleted = deleted;
      if(deleted){
        //this.getOrCreateAccessStatus().setCreated(false);
        //this.getOrCreateAccessStatus().setModified(false);
        this.created = false;
        this.modified = false;
      }
      statusModificationInternal(STATUS_DELETED);
    }
  }

  public void setCreated(boolean created) {
    if (created != isCreated()) {
      //this.getOrCreateAccessStatus().setCreated(created);
      this.created = created;
      if(created){
        //this.getOrCreateAccessStatus().setDeleted(false);
        //this.getOrCreateAccessStatus().setModified(false);
        this.deleted = false;
        this.modified = false;
      }
      statusModificationInternal(STATUS_CREATED);
    }
  }

  public void forceControler(boolean force) {
    forceSelfControler = force;
  }

  public boolean isControler() {
    return fatherSubject == null || fatherSubject == this || forceSelfControler;
  }

  public boolean isTransactionalWithChildren(){
    return transactionalWithChildren;
  }
  
  public void setTransactionalWithChildren(boolean transactionalWithChildren){
    this.transactionalWithChildren = transactionalWithChildren;
  }
  
  public boolean validate(boolean checkValidity) {
    boolean valid = !checkValidity || isContentValidWithPropagation();
    if(valid){
      if (!isCreated()) {
        if(!this.isModified()){
          //Globals.logString("! IN VALIDATION WE ARE IN THE OLD METHOD THAT SET MODIFIED !");
          
          //Globals.logString("! IN VALIDATION THE OLD METHOD WOULD HAVE SET MODIFIED !");
        }
        //this.setModified(true);
      }
      if (fatherSubject != null && fatherSubject != this) {
        fatherSubject.childValidated(this);
      }
      if (isControler()){
        commitStatusToDatabaseWithPropagation();
      }
    }
    return valid;
  }

  public void cancel() {
    undoStatusWithPropagation();
  }

  public void resetStatus() {
    setCreated(false);
    setDeleted(false);
    setModified(false);
  }

  public void resetStatusWithPropagation() {
    this.resetStatus();
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null) {
        subject.resetStatusWithPropagation();
      }
    }
  }

  public boolean isContentValidWithPropagation(){
    boolean isContentValid = isContentValid(true);
    
    if(isContentValid){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext() && isContentValid){
        AccessSubject subject = (AccessSubject) iter.next();
        if (subject != null && !subject.isDeleted()) {
          isContentValid = subject.isContentValid(true);
        }
      }
    }
    return isContentValid;
  }
  
  @Override
  public boolean needValidationWithPropagation() {
    boolean need = isCreated() || isModified() || (isDeleted() && !isDeletionExecuted()); 
    return need || super.needValidationWithPropagation();
  }
  
  public void undoStatusWithPropagation() {
    if (fatherSubject != null) {
      fatherSubject.childStatusUndo(this);
    }
    undoStatus();
    resetStatus();    

    /* WE CANNOT DO THIS BECAUSE WE GET A CONCURRENCY EXCEPTION SEE EXPLANATION BELLOW
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next(); 
      if (subject != null) {
        subject.undoStatusWithPropagation();
      }
    }*/

    //In the following we want to scan the subjects and call the undoStatusWithPropagation
    //We cannot do this directly through the subjects iterator because:
    //In the case of a create that we are undoing, the subject will call a remove from father 
    //and modify the iteration procedure, this will generate a concurrency exception.
    //This is why we are putting all subjects in a temporary item, then we are scanning 
    //this temporary item to undoStatus
    
    //Scan the subjects and fill a temporary array
    ArrayList<AccessSubject> arrayOfSubjects = new ArrayList<AccessSubject>();
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next(); 
      if (subject != null) {
        arrayOfSubjects.add(subject);
      }
    }
    
    //Scan the temporary array and call the undo status with propagation    
    for(int i=0; i<arrayOfSubjects.size(); i++){
      AccessSubject subject = (AccessSubject) arrayOfSubjects.get(i); 
      if (subject != null) {
        subject.undoStatusWithPropagation();
      }
    }
    
  }
  
  private void statusModificationInternal(int statusModified){
    statusModification(statusModified);
    childStatusModificationPropagation();
  }
  
  private void childStatusModificationPropagation() {
    if (fatherSubject != null && fatherSubject != this && !isDesactivateSubjectNotifications()) {
      fatherSubject.childStatusModification(this);
      //fatherSubject.childStatusModificationPropagation();
    }
  }

  public void commitStatusToDatabaseWithPropagation() {
    if(isTransactionalWithChildren()){
      Globals.getDBManager().beginTransaction();
    }
    
    commitStatusToDatabase();

    //B
    
    //E
    
    
    //ATTENTION ATTENTION
    //ATTENTION ATTENTION
    //ATTENTION ATTENTION
    //ATTENTION ATTENTION    
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null) {
        subject.commitStatusToDatabaseWithPropagation();
        if(subject.isDeletionExecuted()){
          iter.remove();
          subject.setFatherSubject(null);
        }
      }
    }
    
    if(isTransactionalWithChildren()){
      Globals.getDBManager().commitTransaction();
    }
    resetStatus();
  }

  public void doBackupWithPropagation(){
    doBackup();

    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null) {
        subject.doBackup();
      }
    }
  }
  
  /**
   * @param fatherSubject
   *          The fatherSubject to set.
   */
  public void setFatherSubject(AccessSubject fatherSubject) {
    if (this.fatherSubject != fatherSubject) {
      if (this.fatherSubject != null) {
        this.fatherSubject.removeSubject(this);
        this.fatherSubject = null;
      }
      if (fatherSubject != null) {
        this.fatherSubject = fatherSubject;
        this.fatherSubject.addSubject(this);
      }
    }
  }

  /**
   * @return Returns the desactivateNotifications.
   */
  public boolean isDesactivateSubjectNotifications() {
    return desactivateSubjectNotifications;
  }

  /**
   * @param desactivateNotifications
   *          The desactivateNotifications to set.
   */
  public void setDesactivateSubjectNotifications(boolean desactivateNotifications) {
    this.desactivateSubjectNotifications = desactivateNotifications;
  }
  
  /**
   * @return Returns the fatherSubject.
   */
  public AccessSubject getFatherSubject() {
    return fatherSubject;
  }
  
	public boolean isDbResident() {
		boolean retValue = true;
		
		if(dbResident == DB_RESIDENT_INHERITED){
			if(getFatherSubject() != null && getFatherSubject() != this){
				retValue = getFatherSubject().isDbResident();
			}
		}else if(dbResident == DB_RESIDENT_TRUE){
			retValue = true;
		}else if(dbResident == DB_RESIDENT_FALSE){
			retValue = false;
		}
		return retValue;
	}
	
	public void setDbResident(boolean dbResident) {
		this.dbResident = dbResident ? DB_RESIDENT_TRUE : DB_RESIDENT_FALSE;
	}
  public boolean isDeletionExecuted() {
    return deletionExecuted;
  }
  public void setDeletionExecuted(boolean deletionExecuted) {
    this.deletionExecuted = deletionExecuted;
  }
}

package b01.foc.access;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class AccessConsole {
  private HashMap childrenSubjects = null;
  private ArrayList<AccessConsole> disposeDependentObjectsArray = null;
 
  public AccessConsole() {
    super();
  }

  public void dispose(){
    if(childrenSubjects != null){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext()){
        AccessSubject subject = (AccessSubject) iter.next();
        if (subject != null) {
          iter.remove();
        }
      }
      childrenSubjects = null;
    }
    disposeDisposeDependentObjects();
  }
  
  private void disposeDisposeDependentObjects(){
  	if(disposeDependentObjectsArray != null){
  		Iterator<AccessConsole> iter = disposeDependentObjectsArray.iterator();
  		while(iter != null && iter.hasNext()){
  			AccessConsole console = iter.next();
  			if(console != null){
  				console.dispose();
  			}
  		}
  		disposeDependentObjectsArray.clear();
  		disposeDependentObjectsArray = null;
  	}
  }
  
  private ArrayList<AccessConsole> getDisposeDepenedentObjectsArray(){
  	if(disposeDependentObjectsArray == null){
  		disposeDependentObjectsArray = new ArrayList<AccessConsole>();
  	}
  	return disposeDependentObjectsArray;
  }
  
  public void addDisposeDependentObject(AccessConsole disposeDependentObject){
  	if(disposeDependentObject != null){
  		getDisposeDepenedentObjectsArray().add(disposeDependentObject);
  	}
  }
  
  /*  
  public int subjectNumber() {
    return childrenSubjects != null ? childrenSubjects.size() : 0;
  }

  public AccessSubject subjectAt(int i) {
    return (childrenSubjects != null) ? (AccessSubject) childrenSubjects.get(i) : null;
  }
  */
  
  public Iterator newSubjectIterator(){
    return childrenSubjects != null ? childrenSubjects.values().iterator() : null;
  }
  
  public void addSubject(AccessSubject subject) {
    if (childrenSubjects == null) {
      childrenSubjects = new HashMap();
    }
    //childrenSubjects.put(subject, subject);
    childrenSubjects.put(Integer.valueOf(subject.getHashCodeAsPointer()), subject);
  }

  public void removeSubject(AccessSubject subject) {
    if (childrenSubjects != null) {
      //childrenSubjects.remove(subject);
      childrenSubjects.remove(Integer.valueOf(subject.getHashCodeAsPointer()));
    }
  }

  public boolean validate(boolean checkValidity) {
    boolean validate = true;
    
    //BGuiLock
    if(checkValidity){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext() && validate && checkValidity){
        AccessSubject subject = (AccessSubject) iter.next();
        if (subject != null) {
          validate = subject.isContentValidWithPropagation();
        }
      }
    }
    //EGuiLock
    
    if(validate){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext()){
        AccessSubject subject = (AccessSubject) iter.next();
        if (subject != null) {
          subject.validate(true);
        }
      }
    }
    
    return validate;
  }
  
  //BELIAS
  public boolean needValidationWithPropagation(){
    boolean res = false;
    
    if(childrenSubjects != null && childrenSubjects.size()!=0){
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext() && !res){
        AccessSubject subject = (AccessSubject) iter.next();
        res = subject.needValidationWithPropagation();
      }
    }
    return res;
  }
  //EELIAS

  public void cancel() {
    //Globals.logString("Cancel :"+toString());
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null) {
        subject.cancel();
      }
    }
  }
}

/*
 * Created on Aug 23, 2005
 */
package b01.foc.desc;

import b01.foc.*;

/**
 * @author 01Barmaja
 */
public class FocRef implements Cloneable, Comparable{
  int reference = 0;
  
  public FocRef(){
    reference = 0;
  }

  public FocRef(int ref){
    reference = ref;
  }
  
  public void dispose(){
    
  }
  
  public int getInt() {
    return reference;
  }

  public void setInt(int reference) {
    this.reference = reference;
  }
  
  public void copy(FocRef focRef){
    reference = focRef.reference;
  }
  
  public Object clone(){
    FocRef cRef = null;
    try{
      cRef = (FocRef) super.clone();
    }catch (Exception e){
      Globals.logException(e);
    }
    return cRef;
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object arg0) {
    return reference - ((arg0 != null) ? ((FocRef)arg0).reference : 0);
  }
}

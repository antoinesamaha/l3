package b01.foc.list;

import b01.foc.desc.FocObject;
import b01.foc.util.FocIterator;

public abstract class FocListIterator extends FocIterator{
  
  public abstract boolean treatElement(FocListElement element, FocObject focObj);

  public FocListIterator() {
    super();
  }

  public FocListIterator(int nbrOfUserObjects) {
    super(nbrOfUserObjects);
  }
  
  public FocListIterator(Object userObj1, Object userObj2, Object userObj3, Object userObj4) {
    super(userObj1, userObj2, userObj3, userObj4);
  }

  public FocListIterator(Object userObj1, Object userObj2, Object userObj3) {
    super(userObj1, userObj2, userObj3);
  }

  public FocListIterator(Object userObj1, Object userObj2) {
    super(userObj1, userObj2);
  }

  public FocListIterator(Object userObj1) {
    super(userObj1);
  }

  public boolean treatElement(Object object){
    FocListElement element = (FocListElement)object;
    FocObject focObject = (FocObject)element.getFocObject();
    return treatElement(element, focObject);
  }
}

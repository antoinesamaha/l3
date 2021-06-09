package b01.foc.util;

public abstract class FocIterator implements IFocIterator{
  public final static int DEFAULT_NBR_USER_OBJECTS = 3;
  
  private Object userObjects[] = null;

  public FocIterator(){
  	super();
  }
  
  public FocIterator(int nbrOfUserObjects){ 
    super();
    userObjects = new Object[nbrOfUserObjects];
  }

  public FocIterator(Object userObj1){
    super();
    userObjects = new Object[1];
    setObject(0, userObj1);  
  }

  public FocIterator(Object userObj1, Object userObj2){
    super();
    userObjects = new Object[2];
    setObject(0, userObj1);
    setObject(1, userObj2);
  }

  public FocIterator(Object userObj1, Object userObj2, Object userObj3){
    super();
    userObjects = new Object[3];
    setObject(0, userObj1);
    setObject(1, userObj2);
    setObject(2, userObj3);
  }
  
  public FocIterator(Object userObj1, Object userObj2, Object userObj3, Object userObj4){
    super();
    userObjects = new Object[4];
    setObject(0, userObj1);
    setObject(1, userObj2);
    setObject(2, userObj3);
    setObject(3, userObj4);
  }
  
  public Object getObject(int i){
    return (userObjects != null) ? userObjects[i] : null;
  }

  public void setObject(int i, Object object){
    userObjects[i] = object;
  }
}

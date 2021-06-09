package b01.foc.desc;

/**
 * @author 01Barmaja
 */
public abstract class FocAbstractUniqueModule implements FocUniqueModule{
  private boolean declared = false;
  
  public abstract void declareFocObjectsOnce();
  
  public void declareFocObjects(){
    if(!declared){
      declared = true;
      declareFocObjectsOnce();
    }
  }
}

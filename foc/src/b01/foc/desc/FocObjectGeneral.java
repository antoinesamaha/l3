// MAIN
// COMPARE
// CONCURRENT ACCESS
// ACCESS
// LISTENERS
// REFERENCE
// DATABASE
// LIST
// XML

/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc;

import b01.foc.desc.field.*;
import b01.foc.gui.*;

/**
 * @author 01Barmaja
 */
public class FocObjectGeneral extends FocObject{

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public FocObjectGeneral(FocConstructor constr) {
    super(constr);
    
    focDesc = constr.getFocDesc();
    FocFieldEnum enumer = new FocFieldEnum(focDesc, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(enumer != null && enumer.hasNext()){
      FField field = (FField) enumer.next();
      if(field != null){
        field.newProperty(this, null);
      }
    }
  }
     
  public void dispose(){
    super.dispose();
  }

  public FPanel newDetailsPanel(int viewID){
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static FocDesc getFocDesc() {
    return focDesc;
  }
  
  public static void setFocDesc(FocDesc focDesc2) {
    focDesc = focDesc2;
  }

}

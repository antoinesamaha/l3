/*
 * Created on Jan 9, 2006
 */
package b01.foc.join;

import java.util.Iterator;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocFieldEnum;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.gui.FPanel;

/**
 * @author 01Barmaja
 */
public abstract class FocRequestLine extends FocObject{
  public abstract FPanel newDetailsPanel(int viewID);

  public FocRequestLine(FocConstructor constr, FocRequestDesc requestDesc){
    super(constr);
    
    FocDesc focDesc = requestDesc.getFocDesc();
    
    FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(enumer != null && enumer.hasNext()){
      FField field = (FField) enumer.next();
      field.newProperty(this, null);
    }
  }
}

/*
 * Created on 15 fvr. 2004
 */
package b01.foc.property;

import b01.foc.desc.field.*;
import b01.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class FDescProperty extends FMultipleChoice implements IFDescProperty {

  public FDescProperty(FocObject focObj, int fieldID, int iVal) {
    super(focObj, fieldID, iVal);
  }

  public FocDesc getSelectedFocDesc(){
    FDescField descField = (FDescField) getFocField();
    return descField.getChoiceFocDesc(getInteger());
  }
}

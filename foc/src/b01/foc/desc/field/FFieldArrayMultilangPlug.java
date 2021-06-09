/*
 * Created on Jun 27, 2005
 */
package b01.foc.desc.field;

import b01.foc.*;

/**
 * @author 01Barmaja
 */
public class FFieldArrayMultilangPlug implements FFieldArrayPlug{
  public int getCurrentIndex(){
    return MultiLanguage.getCurrentLanguage().getId();    
  }
}

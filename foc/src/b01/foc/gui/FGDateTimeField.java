/*
 * Created on 03 May. 2008
 */
package b01.foc.gui;

import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGDateTimeField extends FGDateField {
	
  public FGDateTimeField() {
    super(FDateTime.getDateTimeFormat());
    setToolTipText("Format : dd/mm/yyyy HH:MM");
    setColumns(16);    
  }
  
}

/*
 * Created on Oct 14, 2004
 */
package b01.foc.list;

import b01.foc.Globals;
import b01.foc.gui.*;
import b01.foc.property.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class FocLinkTableObject extends FocObject {

  private FocLink focLink = null;

  private FProperty createProperty(FField field) {
    FProperty property = null;
    if (field.getSqlType() == FCharField.SqlType()) {
      property = new FString(this, field.getID(), "");
    } else if (field.getSqlType() == FIntField.SqlType()) {
      property = new FInt(this, field.getID(), 0);
    }
    return property;
  }
  
  public FocLinkTableObject(FocConstructor constr) {
    super(Globals.getDefaultAccessControl());
    focLink = constr.getFocDesc().getN2nLink();
    super.constructFocObject(constr);

    FField masterFld = focLink.getLinkTableDesc().getFieldByID(FField.MASTER_REF_FIELD_ID);
    FField slaveFld = focLink.getLinkTableDesc().getFieldByID(FField.SLAVE_REF_FIELD_ID);

    createProperty(masterFld);
    createProperty(slaveFld);
  }

  public static FocLinkTableObject newObject(FocLink link){
    FocConstructor constr = new FocConstructor(link.getLinkTableDesc(), null, null);
    return (FocLinkTableObject) constr.newItem();
  }
  
  /**
   * @return
   */
  public FocDesc getThisFocDesc() {
    return focLink != null ? focLink.getLinkTableDesc() : null; 
  }

  public FPanel newDetailsPanel(int viewID) {
    return null;
  }
}

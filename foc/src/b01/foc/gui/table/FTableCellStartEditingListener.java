package b01.foc.gui.table;

import b01.foc.desc.FocObject;
import b01.foc.property.FProperty;

public interface FTableCellStartEditingListener {
  public void dispose();
  public boolean requestToEditCell(FocObject focObject, FProperty property);
}

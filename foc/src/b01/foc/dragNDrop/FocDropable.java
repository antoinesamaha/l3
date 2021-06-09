package b01.foc.dragNDrop;

import java.awt.dnd.DropTargetDropEvent;

public interface FocDropable {
  public abstract boolean drop(FocTransferable focTransferable, DropTargetDropEvent dtde);
  public abstract void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde);
  public abstract boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde);
}

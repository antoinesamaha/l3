package b01.foc.dragNDrop;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

public abstract class FocDropTargetListener implements DropTargetListener {

  public void dragEnter(DropTargetDragEvent dtde) {
  }

  public void dragExit(DropTargetEvent dte) {
  }

  public void dragOver(DropTargetDragEvent dtde) {
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {
  }
  
  public abstract void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde);
  public abstract boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde);

}

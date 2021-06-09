package b01.foc.dragNDrop;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import b01.foc.Globals;


public class FocDragSourceListener implements DragSourceListener {

  public void dragDropEnd(DragSourceDropEvent dsde) {
    Transferable transferable = dsde.getDragSourceContext().getTransferable();
    try{
      FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());
      focTransferable.dispose();
    }catch(Exception e){
      Globals.logException(e);
    }
  }

  public void dragEnter(DragSourceDragEvent dsde) {
    boolean allowDrop = true;
    DragSourceContext context =  dsde.getDragSourceContext();
    Transferable transferable = context.getTransferable();
    try{
      FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());
      allowDrop = focTransferable.isAllowDropInCurrentComponent();
    }catch(Exception e){
      Globals.logException(e);
    }
    if(allowDrop){
      context.setCursor(DragSource.DefaultCopyDrop);
    }else{
      context.setCursor(DragSource.DefaultCopyNoDrop);
    }
  }

  public void dragExit(DragSourceEvent dse) {
  }

  public void dragOver(DragSourceDragEvent dsde) {
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
  }

}

package b01.foc.dragNDrop;

import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.util.Iterator;

import b01.foc.Globals;

public class FocDragGestureListener implements DragGestureListener {
  private DragSourceListener dragSourceListener         = null;
  
  public FocDragGestureListener(DragSourceListener dragSourceListener){
    this.dragSourceListener = dragSourceListener;
  }
 

/*  public void dragGestureRecognized(DragGestureEvent dge) {
    if(dge.getDragAction() == 0){
      return;
    }
    FocTransferable transferable = new FocTransferable();
    FocDragable dragableSource = (FocDragable)dge.getComponent();
    transferable.setSourceComponent(dragableSource);
    dragableSource.fillTransferableObject(transferable);
    try {
      dge.startDrag(DragSource.DefaultCopyNoDrop, transferable, this.dragSourceListener);
    }catch( InvalidDnDOperationException e ) {
      Globals.logException(e);
    }
  }*/
  
 /**
  * It's pretty nasty with table cell editing.  At least on 
  * Windows, double-clicking on a cell frequently is recognized 
  * as a drag gesture.
  * My workaround is to count the number of drag gesture 
  * events.  If there aren't at least 3 (with the 1st being the 
  * initial mouse click), then I reject the drag.  If the user 
  * drags a little further, then the number of events will be 
  * greater than 2 and I'll accept the drag.  Works so far 
  * without any annoying side effects.
  */
  
  final int eventThreshold = 2;

  public void dragGestureRecognized(DragGestureEvent dge)  {
    int count = 0;
    for (Iterator i = dge.iterator(); count < eventThreshold && i.hasNext(); i.next()){
      count++;
    }

    if (count >= eventThreshold){
      FocTransferable transferable = new FocTransferable();
      FocDragable dragableSource = (FocDragable)dge.getComponent();
      transferable.setSourceComponent(dragableSource);
      Component comp = (Component)dragableSource;
      Point mousePosition = comp.getMousePosition();
      if(mousePosition != null){
	      transferable.setSourceX(mousePosition.x);
	      transferable.setSourceY(mousePosition.y);
      }
      dragableSource.fillTransferableObject(transferable);
      try {
        dge.startDrag(DragSource.DefaultCopyNoDrop, transferable, this.dragSourceListener);
      }catch( InvalidDnDOperationException e ) {
        Globals.logException(e);
      }
    }
  }
  
  private static FocDragGestureListener focDragGestureListener = null;
  public static FocDragGestureListener newFocdragGestureListener(){
    if(focDragGestureListener == null){
      FocDragSourceListener dragSourceListener = new FocDragSourceListener();
      focDragGestureListener = new FocDragGestureListener(dragSourceListener);
    }
    return focDragGestureListener;
    
  }

}

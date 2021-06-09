package b01.foc.dragNDrop;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDropEvent;

import b01.foc.Globals;

public class FocDefaultDropTargetListener extends FocDropTargetListener {
  
  //old methode
	/*public void drop(DropTargetDropEvent dtde) {
    boolean accepted = false;
    FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
    Component component = (Component)targetComponent;
    Point point = component.getMousePosition();
    try{
	    Transferable transferable = dtde.getTransferable();
	    FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());
	    fillDropInfo(focTransferable, dtde);
	    if(shouldExecuteDrop(focTransferable, dtde)){
	    	accepted = targetComponent.drop(focTransferable, dtde);
	    }
	    if(!accepted){
	      dtde.rejectDrop();
	    }
    }catch(Exception e){
    	Globals.logException(e);
    }
  }*/
	
	public void drop(DropTargetDropEvent dtde) {
    try{
	    Transferable transferable = dtde.getTransferable();
	    FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());
	    drop(focTransferable, dtde);
    }catch(Exception e){
    	Globals.logException(e);
    }
  }
  
  public void drop(FocTransferable focTransferable, DropTargetDropEvent dtde){
    boolean accepted = false;
    FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
    /*Component component = (Component)targetComponent;
    Point point = component.getMousePosition();*/
    try{
	    fillDropInfo(focTransferable, dtde);
	    if(shouldExecuteDrop(focTransferable, dtde)){
	    	accepted = targetComponent.drop(focTransferable, dtde);
	    }
	    if(!accepted){
	      dtde.rejectDrop();
	    }
    }catch(Exception e){
    	Globals.logException(e);
    }
  }
  
	@Override
	public void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde){
		FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
		targetComponent.fillDropInfo(focTransferable, dtde);
		if(focTransferable != null){
	    Component component = (Component)targetComponent;
	    Point point = component.getMousePosition();
	    if(point != null){
		    focTransferable.setTargetX(point.x);
		    focTransferable.setTargetY(point.y);
	    }
		}
	}
	
	@Override
	public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
		FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
		return targetComponent.shouldExecuteDrop(focTransferable, dtde);
	}
  
	public boolean executeDrop(FocTransferable focTransferable, DropTargetDropEvent dtde){
		FocDropable targetComponent = (FocDropable)dtde.getDropTargetContext().getComponent();
		return targetComponent.drop(focTransferable, dtde);
	}
	
  private static FocDefaultDropTargetListener focDefaultDropTargetListener = null;
  public static FocDefaultDropTargetListener getInstance(){
    if(focDefaultDropTargetListener == null){
      focDefaultDropTargetListener = new FocDefaultDropTargetListener();
    }
    return focDefaultDropTargetListener;
  }
}

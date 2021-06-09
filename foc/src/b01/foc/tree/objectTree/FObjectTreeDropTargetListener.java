package b01.foc.tree.objectTree;

import java.awt.dnd.DropTargetDropEvent;
import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.dragNDrop.FocDefaultDropTargetListener;
import b01.foc.dragNDrop.FocTransferable;
import b01.foc.gui.FTreeTablePanel;
import b01.foc.gui.treeTable.FTreeTableModel;

public class FObjectTreeDropTargetListener extends FocDefaultDropTargetListener {
  
  //private FocList list = null;
  private FTreeTablePanel selectionPanel = null;
  //private int displayID = 0;
  private int fatherNodeID = 0;
  FObjectTree tree = null;
  
  public FObjectTreeDropTargetListener( FTreeTablePanel selectionPanel, FObjectTree tree, int fatherNodeID) {
    //this.list = list;
    this.selectionPanel = selectionPanel;
    //this.displayID = displayID;
    this.fatherNodeID = fatherNodeID;
    this.tree = tree;
  }
  
  public void drop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
    try {
      fillDropInfo(focTransferable, dtde);
      
      if( shouldExecuteDrop(focTransferable, dtde) ){
        executeDrop(focTransferable, dtde);
      }
      
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  public boolean executeDrop(FocTransferable focTransferable, DropTargetDropEvent dtde){
    FTreeTablePanel treeTablePanel = selectionPanel;
    FTreeTableModel treeTableModel = (FTreeTableModel)treeTablePanel.getTableModel();
    FObjectNode targetNode = (FObjectNode) treeTableModel.getNodeForRow(focTransferable.getTableTargetRow());
    FObjectNode sourceNode = (FObjectNode) treeTableModel.getNodeForRow(focTransferable.getTableSourceRow());
    FocObject targetObj = (FocObject)targetNode.getObject();
    FocObject sourceObj = (FocObject)sourceNode.getObject();
    
    boolean execute = shouldExecuteDrop(focTransferable, dtde);
    
    if( execute ){
      sourceObj.setPropertyObject(fatherNodeID, targetObj);
      /*sourceNode.getFatherNode().removeChild(sourceNode);
      sourceNode.setFatherNode(targetNode);
      targetNode.addChild(sourceNode);*/
      sourceNode.moveTo(targetNode);
      treeTableModel.refreshTree(tree, true);
    }
    
    return execute;
  }
  
  public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde){
    FTreeTablePanel treeTablePanel = selectionPanel;
    FTreeTableModel treeTableModel = (FTreeTableModel)treeTablePanel.getTableModel();
    FObjectNode targetNode = (FObjectNode) treeTableModel.getNodeForRow(focTransferable.getTableTargetRow());
    FObjectNode sourceNode = (FObjectNode) treeTableModel.getNodeForRow(focTransferable.getTableSourceRow());
    FocObject targetObj = (FocObject)targetNode.getObject();
    FocObject sourceObj = (FocObject)sourceNode.getObject();
    
    boolean shouldExecute = (sourceObj != null && sourceObj != targetObj) && (!sourceNode.isAncestorOf(targetNode));
    
    return shouldExecute;
  }
  
}

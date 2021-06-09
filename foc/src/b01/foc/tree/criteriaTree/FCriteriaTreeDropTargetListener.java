package b01.foc.tree.criteriaTree;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDropEvent;
import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.dragNDrop.FocDefaultDropTargetListener;
import b01.foc.dragNDrop.FocDropTargetListener;
import b01.foc.dragNDrop.FocTransferable;
import b01.foc.gui.FTreeTablePanel;
import b01.foc.gui.treeTable.FTreeTableModel;
import b01.foc.property.FProperty;
import b01.foc.tree.FRootNode;
import b01.foc.tree.FTree;

public class FCriteriaTreeDropTargetListener extends FocDefaultDropTargetListener {

  private FTreeTablePanel selectionPanel = null;
  
  public FCriteriaTreeDropTargetListener(FTreeTablePanel selectionPanel ){
    this.selectionPanel = selectionPanel;
  }
  
  public void drop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
    try {
      /*Transferable transferable = dtde.getTransferable();
      FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());*/
      FocObject sourceObj = focTransferable.getSourceFocObject();
      
      Point targetPoint = selectionPanel.getTable().getCellCoordinatesForMouseCurrentPosition();
      FTreeTablePanel treeTablePanel = selectionPanel;
      FTreeTableModel treeTableModel = (FTreeTableModel)treeTablePanel.getTableModel();
      FCriteriaNode targetNode = (FCriteriaNode) treeTableModel.getNodeForRow(targetPoint.y);
      if( !targetNode.isDisplayLeaf() && !(targetNode instanceof FRootNode) ){
        int row = focTransferable.getTableSourceRow();
        FCriteriaNode sourceNode = (FCriteriaNode)treeTableModel.getNodeForRow(row);
        
        
        sourceNode.getFatherNode().removeChild(sourceNode);
        sourceNode.setFatherNode(targetNode);
        targetNode.addChild(sourceNode);
        
        
        FCriteriaNode criteriaNode = (FCriteriaNode)sourceNode.getFatherNode();
        while( !(criteriaNode instanceof FRootNode) ){
          FFieldPath fieldPath = criteriaNode.getNodeLevel().getPath();
          FProperty property = fieldPath.getPropertyFromObject((FocObject)criteriaNode.getObject(), 0);
          sourceObj.getFocProperty(property.getFocField().getID()).setObject(property.getObject());
          criteriaNode = (FCriteriaNode)criteriaNode.getFatherNode();
        }
        
        FTree tree = ((FTreeTableModel)selectionPanel.getTableModel()).getTree();
        treeTableModel.refreshTree(tree, true);  
      }
      
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
}

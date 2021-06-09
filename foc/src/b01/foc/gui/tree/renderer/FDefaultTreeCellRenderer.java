package b01.foc.gui.tree.renderer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import b01.foc.gui.tree.FTreeModel;
import b01.foc.tree.FNode;
import b01.foc.tree.FTree;

@SuppressWarnings("serial")
public class FDefaultTreeCellRenderer extends DefaultTreeCellRenderer {
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    
    FTree fTree = ((FTreeModel)tree.getModel()).getTree();
    
    
    
    //tree.setBackground(Color.ORANGE);
    FNode node = (FNode) value;
    
    Color color = fTree.getColorForNode(node, row, null);
    if( color != null ){
      setBackgroundSelectionColor(Color.LIGHT_GRAY);
      setBackgroundNonSelectionColor(color);  
    }else {
      setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
      setBackgroundNonSelectionColor(Color.WHITE);
    }
    
    
    Icon icon = fTree.getIconForNode(node);
    
    if (icon != null ){
      setIcon(icon);
    }
    
    return this;
  }
}

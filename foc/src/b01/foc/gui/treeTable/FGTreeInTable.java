package b01.foc.gui.treeTable;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import b01.foc.gui.tree.FGTree;
import b01.foc.gui.tree.FTreeModel;
import b01.foc.tree.FNode;
import b01.foc.tree.FTree;

@SuppressWarnings("serial")
public class FGTreeInTable extends FGTree {
	private int wBackup = 0;
	/** Last table/tree row asked to renderer. */
	private int visibleRow;
	public FGTreeInTable(TreeModel treeModel){
		super(treeModel);
  }
  
  public void dispose(){
    
  }
  
  public void setVisibleRow(int visibleRow){
    this.visibleRow = visibleRow;
  }
	
	/**
	 * updateUI is overridden to set the colors of the Tree's renderer to match
	 * that of the table.
	 */
	public void updateUI() {
    //System.out.println("XX Update UI");
		super.updateUI();
		// Make the tree's cell renderer use the table's cell selection
		// colors.
		TreeCellRenderer tcr = getCellRenderer();
		if (tcr instanceof DefaultTreeCellRenderer) {
			DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
			// For 1.1 uncomment this, 1.2 has a bug that will cause an
			// exception to be thrown if the border selection color is
			// null.
			// dtcr.setBorderSelectionColor(null);
			dtcr.setTextSelectionColor(UIManager
					.getColor("Table.selectionForeground"));
			dtcr.setBackgroundSelectionColor(UIManager
					.getColor("Table.selectionBackground"));
		}
	}

	/**
	 * Sets the row height of the tree, and forwards the row height to the
	 * table.
	 */
	public void setRowHeight(int rowHeight) {
		if (rowHeight > 0) {
			super.setRowHeight(rowHeight);
			if (FGTreeInTable.this != null
					&& FGTreeInTable.this.getRowHeight() != rowHeight) {
				FGTreeInTable.this.setRowHeight(getRowHeight());
			}
		}
	}

	/**
	 * This is overridden to set the height to match that of the JTable.
	 */
	public void setBounds(int x, int y, int w, int h) {
	  //System.out.println("XX SetBounds");
    wBackup = w;
		super.setBounds(x, 0, w, this.getRowHeight());
		//super.setBounds(x, 0, w, 18);
	}

	/**
	 * Sublcassed to translate the graphics such that the last visible row will
	 * be drawn at 0,0.
	 */
  
	public void paint(Graphics g) {

    TreePath treePath = getPathForRow(visibleRow);
    FNode node = treePath != null ? (FNode) treePath.getLastPathComponent() : null;
    
    FTree fTree = ((FTreeModel)this.getModel()).getTree();
    Color color = fTree.getColorForNode(node, visibleRow, this);

    if( color != null ){
      g.setColor(color);
      g.fillRect(0, 0, wBackup, this.getRowHeight());  
    }

    int fullDepth = node.getNodeDepth();
    int level = 0;
    while ( node != null /*&& !(node instanceof FRootNode)*/ ){
      //Color marginColor = fTree.getColorForLevel(level);
      Color marginColor = fTree.getColorForNode(node, 0, null);
      
      if( marginColor != null){
        g.setColor(marginColor);
        g.fillRect((fullDepth-level) * 19, 0, 19, this.getRowHeight());    
      }
      
      level++;
      node = node.getFatherNode();
    }
    
    g.translate(0, -visibleRow * getRowHeight());
		super.paint(g);
	}

	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
  /*
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		//FDefaultCellRenderer.setCellColorStatic(this, table, value, isSelected, hasFocus, row, column);
		if (isSelected)
			setBackground(table.getSelectionBackground());
		else
			setBackground(table.getBackground());
		
		visibleRow = row;
		return this;
	}*/
}

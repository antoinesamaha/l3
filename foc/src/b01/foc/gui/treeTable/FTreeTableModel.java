package b01.foc.gui.treeTable;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.dragNDrop.FocTransferable;
import b01.foc.event.FocEvent;
import b01.foc.event.FocListener;
import b01.foc.gui.table.FAbstractTableModel;
import b01.foc.gui.table.FTable;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.cellControler.TreeCellControler;
import b01.foc.gui.tree.FTreeModel;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;
import b01.foc.tree.FNode;
import b01.foc.tree.FTree;

@SuppressWarnings({ "serial", "unchecked" })
public class FTreeTableModel extends FAbstractTableModel implements FocListener{

	protected FGTreeInTable gTree = null;
	protected EventListenerList listenerList = null;
	//private   ArrayList<Color>  levelsColors = null;
  private   FTable  table   = null;
  private   FTree tree = null;
  
  public FTreeTableModel(FTree tree) {
    refreshTree(tree);
    this.tree = tree;
    FocList list = getFocList();
    if(list != null && tree.isAutomaticlyListenToListEvents()){
    	list.addFocListener(this);
    }
  }
  
  public void dispose(){
    super.dispose();
    disposeTree();
    listenerList = null;
    table = null;
    FocList list = getFocList();
    if(list != null){
    	list.removeFocListener(this);
    }
    tree = null;
  }
  
  public void disposeTree(){
    if(gTree != null){
      //removeListeners();
      gTree.dispose();
      gTree = null;
    }
  }
  
  
  //BElias : reWritng this fucntion
  /*@Override
  public Color getCellColor(int row, int col) {
    Color color = tree.getColorForNode(getNodeForRow(row), row, getGuiTree());
    
    if(color != null ){
      return color;
    }else{
      return Color.WHITE;
    }
    
    //return super.getCellColor(row, col);
  }*/
  
  @Override
  public Color getCellColor(int row, int col) {
  	
  	Color color = super.getCellColor(row, col);
  	if(color == null){
  		color = tree.getColorForNode(getNodeForRow(row), row, getGuiTree());
  	}
    return color != null ? color : Color.WHITE;
    
    /*if(color != null ){
      return color;
    }else{
      return Color.WHITE;
    }*/
    
  }
  //EElias

  public void refreshTree(FTree tree) {
    refreshTree(tree, false);
  }
  
  public void refreshTree(FTree tree, boolean copyExpansion) {

    FGTreeInTable gNewTree = new FGTreeInTable(new FTreeModel(tree));
    if(copyExpansion){
      gNewTree.copyExpansion(gTree);
    }
    disposeTree();
    gTree = gNewTree;
    
    
    //levelsColors = new ArrayList<Color>();
    //tree.initLevelsColors();
    
    fireTableDataChanged();

    plugTreeListeners();
    
  
  }
  
  public void refreshTreeFromList(){
		if(this.tree != null){
			ArrayList<ArrayList<String>> expandedPathesNodesTitle = this.gTree.getExpandedPathesNodesTitles();
			this.tree.growTreeFromFocList(getFocList());
			refreshTree(this.tree);
			this.gTree.expandPathes(expandedPathesNodesTitle);
			/*Iterator<ArrayList<String>> iter = expandedPathesNodesTitle.iterator();
		  while(iter != null && iter.hasNext()){
		  	ArrayList<String> nodesPathTitles = iter.next();
		  	FNode[] objectPath = new FNode[nodesPathTitles.size()];
		  	boolean targetPathContainsNullVaLues = false;
		  	for(int i = 0; i < nodesPathTitles.size() && !targetPathContainsNullVaLues; i++){
		  		String nodeTitle = nodesPathTitles.get(i);
		  		FNode node = this.gTree.getNodeByTitle(nodeTitle);
		  		if(node == null){
		  			targetPathContainsNullVaLues = true;
		  		}
		  		objectPath[i] = node;
		  	}
		  	if(!targetPathContainsNullVaLues){
		  		TreePath targetPath = new TreePath(objectPath);
		  		this.gTree.expandPath(targetPath);
		  	}
		  }*/
		}
  }
    
  /*  
  public void removeListeners(){
    removeTreeExpansionListeners();
    removeTreeSelectionListeners();
    removeTreeWillExpandListeners();
    //removeListSelectionListeners();
  }

  private void removeTreeExpansionListeners(){
    if(gTree != null){
      TreeExpansionListener[] expansionLiteners =  gTree.getTreeExpansionListeners();
      for(int i = 0; i < expansionLiteners.length; i++){
        TreeExpansionListener expansionListener = expansionLiteners[i];
        gTree.removeTreeExpansionListener(expansionListener);
      }
    }
  }

  private void removeTreeSelectionListeners(){
    if(gTree != null){
      TreeSelectionListener[] selectionLiteners =  gTree.getTreeSelectionListeners();
      for(int i = 0; i < selectionLiteners.length; i++){
        TreeSelectionListener expansionListener = selectionLiteners[i];
        gTree.removeTreeSelectionListener(expansionListener);
      }
    }
  }
  
  private void removeTreeWillExpandListeners(){
    if(gTree != null){
      TreeWillExpandListener[] willExpandLiteners =  gTree.getTreeWillExpandListeners();
      for(int i = 0; i < willExpandLiteners.length; i++){
        TreeWillExpandListener expansionListener = willExpandLiteners[i];
        gTree.removeTreeWillExpandListener(expansionListener);
      }
    }
  }
  
  private void removeListSelectionListeners(){
    if(gTree != null){
      TreeSelectionModel listSelectionModelWraper = gTree.getSelectionModel();
      if(listSelectionModelWraper != null){
        ListSelectionModel defaultListSelectionModel = ((ListToTreeSelectionModelWrapper)listSelectionModelWraper).getListSelectionModel();
        if(defaultListSelectionModel != null){
          ListSelectionListener[] listSelectionListeners = ((DefaultListSelectionModel)defaultListSelectionModel).getListSelectionListeners();
          if(listSelectionListeners != null){
            for(int i = 0; i < listSelectionListeners.length; i++){
              ListSelectionListener listener = listSelectionListeners[i];
              defaultListSelectionModel.removeListSelectionListener(listener);
            }
          }
        }
      }
    }
  }
  */
	
	public FGTreeInTable getGuiTree(){
		return gTree;
	}

	public FTree getTree(){
		return ((FTreeModel)getGuiTree().getModel()).getTree();
	}
  
  public FNode getNodeForRow(int row){
    TreePath treePath = getGuiTree().getPathForRow(row);
    FNode node = treePath != null ? (FNode) treePath.getLastPathComponent() : null;
    return node;
  }
  
  public int getRowForNode( FNode node ){
    int nodeDepth = node.getNodeDepth()+1;
    Object[] objPath = new Object[nodeDepth];
    int index = objPath.length -1;
    objPath[index--] = node;
    while( node.getFatherNode() != null ){
      node = node.getFatherNode();
      objPath[index--] = node;
    }
    TreePath path = new TreePath(objPath);
    
    //TEMP
    int row = -1;
    try {
      row = getGuiTree().getRowForPath(path);  
    }catch(Exception e){
      row = -1;
    }
    //TEMP
    return row;
  }
	
	@Override
	public FocObject getRowFocObject(int row) {
		FNode node = getNodeForRow(row);		
		return node != null ? (FocObject) node.getObject() : null;
	}

	public int getRowCount() {
    if ( getGuiTree() == null ){
      Globals.logString("Gui Tree Null");
    }
    int rowCount = getGuiTree().getRowCount();
    
    return rowCount;
	}
//BElie
	@Override
	public FProperty getSpecialFProperty(FTableColumn tc, FocObject rowObject, int row, int col) {
  	FProperty objectProperty = super.getSpecialFProperty(tc, rowObject, row, col);
		
  	if(objectProperty == null){
	    if(tc.getID() == FField.TREE_FIELD_ID){
	      objectProperty = getTree().getTreeSpecialProperty(this, row);  
	    }
  	}
    return objectProperty;
	}
  
  public void setValueAt(Object aValue, int row, int column) {
    super.setValueAt(aValue, row, column);

    //This additional part to change the Node title in memory when it is edited
    FProperty prop = getFProperty(row, column);
    FTableColumn tc = (FTableColumn) tableView.getColumnAt(column);

    if (prop != null && tc != null) {
      if(tc.getID() == FField.TREE_FIELD_ID){
        FNode node = getNodeForRow(row);
        if(node != null){
          node.setTitle(prop.getString());
        }
      }
    }
  }
  
	//EElie
  public Object getValueAt(int row, int col){
    Object obj = super.getValueAt(row, col);
    if(obj == null){
    	FTableColumn tc = (FTableColumn) tableView.getColumnAt(col);
    	if(tc != null && tc.getID() == FField.TREE_FIELD_ID){
    		obj = gTree;
    	}
    }
    return obj;
  }
  
  public Color getDefaultBackgroundColor(Color bg, Component comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		TreePath treePath = getGuiTree().getPathForRow(row);
		FNode node = (FNode) treePath.getLastPathComponent();
    
		//System.out.println("Color list "+node.getLevelDepth());
    return node.isLeaf() ? Color.WHITE : tree.getColorForLevel(node.getLevelDepth()) /*levelsColors.get(node.getLevelDepth()*/;		
  }
  //BElie
  public boolean isCellEditable(int row, int col) {
    if (tableView != null) {
      FTableColumn tableColumn = tableView.getColumnAt(col);
      if (tableColumn != null) {
      	if(tableColumn.getID() == FField.TREE_FIELD_ID){
          return true;
        }
      }
    }
  	return super.isCellEditable(row, col);
  }
  //EElie

	public void setRowHeight(int rowHeight) {
		if(gTree.getRowHeight() != rowHeight){
			gTree.setRowHeight(rowHeight);
		}
	}

  public void plugTreeListeners(){
    if(table != null){
      gTree.addTreeExpansionListener(new TreeExpansionListener() {
        // Don't use fireTableRowsInserted() here; the selection model
        // would get updated twice.
        public void treeExpanded(TreeExpansionEvent event) {
          fireTableDataChanged();
        }
    
        public void treeCollapsed(TreeExpansionEvent event) {
          fireTableDataChanged();
        }
      });
      
      // Force the JTable and JTree to share their row selection models.
      ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
      gTree.setSelectionModel(selectionWrapper);
      table.setSelectionModel(selectionWrapper.getListSelectionModel());
    }
  }
  
  //BELie
  
  public FocList getFocList(){
  	FocList list = null;
  	if(this.tree != null){
  		list = tree.getFocList();
  	}
  	return list;
  }
  
  @Override
	public void afterTableConstruction(final FTable table) {
    
    if(getFocList().isKeepNewLineFocusUntilValidation()){
      table.addFocusListener(table);
    }
    
    //table.addFocusListener(table);
    /*table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e) {
        table.reactToFocusChange(false);
      }
    });
     table.requestFocus();
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    table.setInputVerifier(FTable.getTableLockInputVerifier());*/

    
		// No grid.
		// table.setShowGrid(false);

		// No intercell spacing
		// table.setIntercellSpacing(new Dimension(0, 0));

		// And update the height of the trees row to match that of
		// the table.
		if (gTree.getRowHeight() < 1) {
			// Metal looks better like this.
			table.setRowHeight(20);
		}
		
		FTableColumn fCol = getTableView().getColumnById(FField.TREE_FIELD_ID);
		if(fCol != null){
			TreeCellControler controler = (TreeCellControler)fCol.getCellEditor();
			if(controler != null){
        controler.setTable(table);
			}
		}
    
    this.table = table;
    plugTreeListeners();
    if(getFocList() != null ){
      plugListListenerToCellPropertiesMoifications();  
    }
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		// Install a TreeModelListener that can update the table when
		// tree changes. We use delayedFireTableDataChanged as we can
		// not be guaranteed the tree will have finished processing
		// the event before us.
		
		  /*gTree.getModel().addTreeModelListener(new TreeModelListener() { 
        
       public void treeNodesChanged(TreeModelEvent e) { 
         delayedFireTableDataChanged(); 
       }
  		 
  		 public void treeNodesInserted(TreeModelEvent e) {
  		   delayedFireTableDataChanged(); 
       }
  		 
  		 public void treeNodesRemoved(TreeModelEvent e) {
  		   delayedFireTableDataChanged(); 
       }
  		 
  		 public void treeStructureChanged(TreeModelEvent e) {
  		   delayedFireTableDataChanged(); 
         } 
       });*/
		 
	}
  //EElie
	
	/**
	 * Invokes fireTableDataChanged after all the pending events have been
	 * processed. SwingUtilities.invokeLater is used to handle this.
	 */
	protected void delayedFireTableDataChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fireTableDataChanged();
			}
		});
	}
	
  // --------------------------
  // Drag implementation
  // --------------------------
  public void fillSpecificDragInfo(FocTransferable focTransferable){
  	if(focTransferable != null){
	  	super.fillSpecificDragInfo(focTransferable);
	  	int selectedRow = focTransferable.getTableSourceRow();
	  	focTransferable.setSourceNode(getNodeForRow(selectedRow));
  	}
  }
	
	//BElias
	//--------------------------
  // FocListener implementation
  // --------------------------
	public void focActionPerformed(FocEvent evt){
		if(this.tree != null){
			if (evt.getID() == FocEvent.ID_ITEM_ADD || evt.getID() == FocEvent.ID_ITEM_REMOVE || evt.getID() == FocEvent.ID_ITEM_MODIFY || evt.getID() == FocEvent.ID_WAIK_UP_LIST_LISTENERS) {
				refreshTreeFromList();
			}
		}
	}
	//EElias
	
	/**
	 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel to listen
	 * for changes in the ListSelectionModel it maintains. Once a change in the
	 * ListSelectionModel happens, the paths are updated in the
	 * DefaultTreeSelectionModel.
	 */
	@SuppressWarnings("serial")
	class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
		/** Set to true when we are updating the ListSelectionModel. */
		protected boolean updatingListSelectionModel;

		public ListToTreeSelectionModelWrapper() {
			super();
			getListSelectionModel().addListSelectionListener(createListSelectionListener());
		}

		/**
		 * Returns the list selection model. ListToTreeSelectionModelWrapper listens
		 * for changes to this model and updates the selected paths accordingly.
		 */
		ListSelectionModel getListSelectionModel() {
			return listSelectionModel;
		}

		/**
		 * This is overridden to set <code>updatingListSelectionModel</code> and
		 * message super. This is the only place DefaultTreeSelectionModel alters
		 * the ListSelectionModel.
		 */
		public void resetRowSelection() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					super.resetRowSelection();
				} finally {
					updatingListSelectionModel = false;
				}
			}
			// Notice how we don't message super if
			// updatingListSelectionModel is true. If
			// updatingListSelectionModel is true, it implies the
			// ListSelectionModel has already been updated and the
			// paths are the only thing that needs to be updated.
		}

		/**
		 * Creates and returns an instance of ListSelectionHandler.
		 */
		protected ListSelectionListener createListSelectionListener() {
			return new ListSelectionHandler();
		}

		/**
		 * If <code>updatingListSelectionModel</code> is false, this will reset
		 * the selected paths from the selected rows in the list selection model.
		 */
		protected void updateSelectedPathsFromSelectedRows() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					// This is way expensive, ListSelectionModel needs an
					// enumerator for iterating.
					int min = listSelectionModel.getMinSelectionIndex();
					int max = listSelectionModel.getMaxSelectionIndex();

					clearSelection();
					if (min != -1 && max != -1) {
						for (int counter = min; counter <= max; counter++) {
							if (listSelectionModel.isSelectedIndex(counter)) {
								TreePath selPath = gTree.getPathForRow(counter);

								if (selPath != null) {
									addSelectionPath(selPath);
								}
							}
						}
					}
				} finally {
					updatingListSelectionModel = false;
				}
			}
		}
    
		/**
		 * Class responsible for calling updateSelectedPathsFromSelectedRows when
		 * the selection of the list changse.
		 */
		class ListSelectionHandler implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent e) {
				updateSelectedPathsFromSelectedRows();
        //01Barmaja
        //This line was added to update the FGCurrentItemPanel.
        //It's real place would be in the after table construction
        //But the listener wrappers prevent the call of the selectionChanged...
        //We do not know why????
        table.reactToFocusChange(false);
			}
		}
    
	}
  
  /*private FocListListener tableCellModificationListener = null;
  public void plugListListenerToCellPropertiesMoifications(){
    FTableView view = getTableView();
    if (view != null) {
      FocListener focListener = new FocListener(){
        public void focActionPerformed(FocEvent evt) {
          fireTableRowsUpdated(0, getRowCount());
        }

        public void dispose() {
          // TODO Auto-generated method stub
          
        }
      };
      
      //disposeTableCellModificationListener();
      
      
      tableCellModificationListener = new FocListListener(focList);
      tableCellModificationListener.addListener(focListener);
      
      for (int i = 0; i < view.getColumnCount(); i++) {
        FTableColumn fCol = view.getColumnAt(i);
        if (fCol != null) {
          tableCellModificationListener.addProperty(fCol.getFieldPath());
        }
      }
      
      tableCellModificationListener.startListening();
    }
  }*/
  
}

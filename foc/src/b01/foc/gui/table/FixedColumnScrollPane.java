package b01.foc.gui.table;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;

import b01.foc.dragNDrop.FocDropTargetListener;
import b01.foc.dragNDrop.FocDropable;
import b01.foc.dragNDrop.FocTransferable;

@SuppressWarnings("serial")
public class FixedColumnScrollPane extends JScrollPane implements FocDropable {
  
  private FTable scrollTable = null;
  private FTable fixedTable = null;
  
  public FixedColumnScrollPane(FTable scrollTable ) {
    super();
    this.scrollTable = scrollTable;
    
    FColumnGroupHeaderConstructor headerConstr = scrollTable.getTableView().getColumnGroupHeaderConstructor();
    if( headerConstr.getColumnGroupFatherArraySize() > 0 ){
      headerConstr.constructHeader(scrollTable);
    }
    
    FAbstractTableModel scrollModel = (FAbstractTableModel)scrollTable.getModel();
    FTableView tableview = scrollModel.getTableView();
    
    if( tableview.getColumnsToFreeze() > 0 ){
      splitTableAccordingToColmnsFreezed(scrollModel);
    }else{
      putTableInScroll();
    }
  }
  
  public void dispose(){
    scrollTable        = null;
    fixedTable         = null; 
  }
  
  private void initDrop(DropTargetListener dropTargetListener){
  	new DropTarget(this, DnDConstants.ACTION_COPY, dropTargetListener, true);
  	if(scrollTable != null){
  		scrollTable.setDropable(dropTargetListener);
  	}
  	if(fixedTable != null){
  		fixedTable.setDropable(dropTargetListener);
  	}
  }

  private void putTableInScroll() {
    setViewportView(this.scrollTable);
    
    /*setLayout(new ScrollPaneLayout.UIResource());
    setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
    setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setViewport(createViewport());
    setVerticalScrollBar(createVerticalScrollBar());
    setHorizontalScrollBar(createHorizontalScrollBar());
    setViewportView(this.scrollTable);
    setOpaque(true);
    updateUI();

    if (!this.getComponentOrientation().isLeftToRight()) {
      viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
    }*/
  }

  private void splitTableAccordingToColmnsFreezed(FAbstractTableModel scrollModel){
    
    FTableView tableview = scrollModel.getTableView();
    int numFrozenColumns = tableview.getColumnsToFreeze();
    
    
    FAbstractTableModel fixedModel = null;
    try {
      fixedModel = (FAbstractTableModel)scrollModel.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    
    this.fixedTable = new FTable(fixedModel, true, true);
    this.fixedTable.setScrollpane(this);
    
    
    FColumnGroupHeaderConstructor headerConstr = fixedTable.getTableView().getColumnGroupHeaderConstructor();
    if( headerConstr.getColumnGroupFatherArraySize() > 0 ){
      headerConstr.constructHeader(fixedTable.getScrollPane().getFixedTable());
    }
    
    fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    scrollTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setViewportView(scrollTable);
    
    JViewport viewport = new JViewport();
    viewport.setView(fixedTable);
    setRowHeader(viewport);
    
    JTableHeader lockedHeader = fixedTable.getTableHeader();
    fixedTable.setFocusable(false);
    lockedHeader.setReorderingAllowed(false);
    lockedHeader.setResizingAllowed(false);
    setCorner(JScrollPane.UPPER_LEFT_CORNER, lockedHeader);
    
    fixedTable.setSelectionModel(scrollTable.getSelectionModel());
    
    fixedTable.getTableHeader().setReorderingAllowed(false);
    fixedTable.getTableHeader().setResizingAllowed(false);
    fixedTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    scrollTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    
    
    FAbstractTableModel  scrollTableModel = (FAbstractTableModel)scrollTable.getModel();
    FTableView scrollTableView = scrollTableModel.getTableView();
    if( scrollTableView != null ){
      for( int i = 0; i < numFrozenColumns; i++){
        int col = scrollTableView.getVisibleColumnIndex(i);
        FTableColumn fTableColumn = scrollTableView.getColumnAt(col);
        fTableColumn.setShow(false);
      }
      scrollTableView.adjustColumnVisibility();
    }
    
    FAbstractTableModel fixedTableModel = (FAbstractTableModel)fixedTable.getModel();
    FTableView fixedTableView = fixedTableModel.getTableView();
    if( fixedTableView != null ){
      for( int i = fixedTableView.getColumnCount()-1; i >= numFrozenColumns; i--){
        int col = fixedTableView.getVisibleColumnIndex(i);
        FTableColumn fTableColumn = fixedTableView.getColumnAt(col);
        fTableColumn.setShow(false);
      }
      fixedTableView.adjustColumnVisibility();
    }
    
    fixedTable.setPreferredScrollableViewportSize(fixedTable.getPreferredSize());
  }

  public FTable getScrollTable() {
    return scrollTable;
  }

  public FTable getFixedTable() {
    return fixedTable;
  }
  
  public void setDropable(DropTargetListener dropTargetListener){
  	initDrop(dropTargetListener);
  }

	public boolean drop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
		return true;
	}

	public void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde) {
	}

	public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
		return true;
	}
  
}

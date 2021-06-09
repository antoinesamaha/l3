package b01.foc.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.CellRendererPane;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class FColumnGroupTableHeader extends JTableHeader {

  private ArrayList<FColumnGroup> columnGroupList = null;

  private CellRendererPane rendererPane = null;

  public FColumnGroupTableHeader(TableColumnModel columnModel) {
    super(columnModel);
    rendererPane = new CellRendererPane();
    add(rendererPane);
    setReorderingAllowed(false);
    //setResizingAllowed(false);

    /*
    ListSelectionModel lsm = columnModel.getSelectionModel();
    if (lsm != null) {
      lsm.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          //repaint();
        }
      });
    }
    */
  }

  public Rectangle getHeaderRect(int column) {
    Rectangle rect = super.getHeaderRect(column);
    rect.height += getHeaderHeight();
    return rect;
  }

  public ArrayList<FColumnGroup> getColumnGroupList() {
    if (columnGroupList == null) {
      columnGroupList = new ArrayList<FColumnGroup>();
    }
    return columnGroupList;
  }

  public CellRendererPane getRendererPane() {
    return rendererPane;
  }

  public void addColumnGroup(FColumnGroup columnGroup) {
    ArrayList<FColumnGroup> groupList = getColumnGroupList();
    groupList.add(columnGroup);
  }

  public ArrayList<FColumnGroup> getGroupsForColumn(FTableColumn fTableColumn) {
    ArrayList<FColumnGroup> groupList = new ArrayList<FColumnGroup>();
    ArrayList<FColumnGroup> groupColumnList = getColumnGroupList();
    Iterator<FColumnGroup> iter = groupColumnList.iterator();
    while (iter != null && iter.hasNext()) {
      FColumnGroup columnGroup = iter.next();
    	columnGroup.getGroupsForColumn(fTableColumn, groupList);
    }
    return groupList;
  }
  
  public FColumnGroup getFatherGroupForColumn(FTableColumn fTableColumn){
  	ArrayList< FColumnGroup> groupList = getColumnGroupList();
  	Iterator< FColumnGroup> iter = groupList.iterator();
  	FColumnGroup result = null;
  	while(iter.hasNext() && result == null){
  		FColumnGroup group = iter.next();
  		result = group.getFatherGroupForColumn(fTableColumn);
  	}
  	return result;
  }

  public void setColumnMargin() {
    ArrayList<FColumnGroup> columnGroupList = getColumnGroupList();
    int columnMargin = getColumnModel().getColumnMargin();
    Iterator<FColumnGroup> iter = columnGroupList.iterator();
    while (iter != null && iter.hasNext()) {
      FColumnGroup columnGroup = iter.next();
      columnGroup.setMargin(columnMargin);
    }
  }

  public FTable getFTable() {
    return (FTable) getTable();
  }

  public void paint(Graphics graphics) {
    TableColumnModel columnModel = getColumnModel();
    if (columnModel != null) {
      Rectangle clipBounds = graphics.getClipBounds();
      Dimension headerSize = getSize();
      Rectangle cellRect = new Rectangle(0, 0, headerSize.width, headerSize.height);
      HashMap<FColumnGroup, Rectangle> paintedGroups = new HashMap<FColumnGroup, Rectangle>();

      setColumnMargin();
      FTable fTable = getFTable();
      
      for (int columnIndex = 0; columnIndex < columnModel.getColumnCount(); columnIndex++) {
        TableColumn column = columnModel.getColumn(columnIndex);
        int groupHeight = 0;
        cellRect.height = headerSize.height;
        cellRect.y = 0;
        ArrayList<FColumnGroup> columnGroupList = getGroupsForColumn(fTable.getTableView().getFTableColumn(column));
        for (int i = 0; i < columnGroupList.size(); i++) {
          FColumnGroup columnGroup = columnGroupList.get(i);
          Rectangle groupRect = (Rectangle) paintedGroups.get(columnGroup);
          if (groupRect == null) {
            groupRect = new Rectangle(cellRect);

            Dimension d = columnGroup.getSize(getTable());
            groupRect.width = d.width;
            groupRect.height = d.height;
            paintedGroups.put(columnGroup, groupRect);
          }
          paintCell(graphics, groupRect, columnGroup);
          groupHeight += groupRect.height;
          cellRect.height = headerSize.height - groupHeight;
          cellRect.y = groupHeight;
        }

        cellRect.width = column.getWidth();
        if (cellRect.intersects(clipBounds)) {
          paintCell(graphics, cellRect, columnIndex);
        }
        cellRect.x += cellRect.width;
      }
    }
  }

  private void paintCell(Graphics graphics, Rectangle cellRect, int columnIndex) {
    TableColumn column = getColumnModel().getColumn(columnIndex);
    Component component = column.getHeaderRenderer().getTableCellRendererComponent(getTable(), column.getHeaderValue(), false, false, -1, columnIndex);
    rendererPane.add(component);
    rendererPane.paintComponent(graphics, component, this, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
  }

  private void paintCell(Graphics graphics, Rectangle cellRect, FColumnGroup columnGroup) {
    Component component = columnGroup.getHeaderRenderer().getTableCellRendererComponent(getTable(), columnGroup.getHeaderValue(), false, false, -1, -1);
    Color backColor = columnGroup.getColor();
    if(backColor != null){
      component.setBackground(backColor);
    }
    rendererPane.add(component);
    rendererPane.paintComponent(graphics, component, this, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
  }

  private int getHeaderHeight() {
    int maxHeight = 0;
    TableColumnModel columnModel = getColumnModel();
    TableCellRenderer renderer = getDefaultRenderer();
    for (int columnIndex = 0; columnIndex < columnModel.getColumnCount(); columnIndex++) {
      TableColumn column = columnModel.getColumn(columnIndex);
      Component component = renderer.getTableCellRendererComponent(getTable(), column.getHeaderValue(), false, false, -1, columnIndex);
      int height = component.getPreferredSize().height;
      ArrayList<FColumnGroup> columnGroupList = getGroupsForColumn(getFTable().getTableView().getFTableColumn(column));
      Iterator<FColumnGroup> iter = columnGroupList.iterator();
      while (iter != null && iter.hasNext()) {
        FColumnGroup columnGroup = iter.next();
        height += columnGroup.getSize(getTable()).height;
      }

      maxHeight = Math.max(maxHeight, height);
    }
    return maxHeight;
  }

  public Dimension getPreferredSize() {
    Dimension size = new Dimension(0, getHeaderHeight());
    TableColumn column = null;
    TableColumnModel columnModel = getColumnModel();

    for (int i = 0; i < columnModel.getColumnCount(); i++) {
      column = columnModel.getColumn(i);
      size.width += column.getPreferredWidth();
    }
    size.width += columnModel.getColumnMargin() * columnModel.getColumnCount();
    return size;
  }

  public class FColumnGroupHeaderMouseInputHandler extends MyMouseInputHandler {

    public FColumnGroupHeaderMouseInputHandler() {
      super(FColumnGroupTableHeader.this);
    }

    public void mouseMoved(MouseEvent e) {
      /*
      // if(canResize(getResizingColumn(e.getPoint())) != (header.getCursor() ==
      // getResizingColumn(e.getPoint())))
      // swapCursor();
      Point p = e.getPoint();
      TableColumnModel columnModel = getColumnModel();
      int index = columnAtPoint(p);
      FColumnGroup currentGroup = null;
      javax.swing.table.TableColumn column = columnModel.getColumn(index);
      int groupHeight = 0;
      columnGroupSelected = null;
      columnSelected = -1;
      FTableView fTableView = (FTableView) getFTable().getTableView();
      FTableColumn tableColumn = fTableView.getFTableColumn(column);
      java.util.List groupList = ((FColumnGroupTableHeader) FColumnGroupTableHeader.this).getGroupsForColumn(tableColumn);
      Iterator iter = groupList.iterator();
      do {
        if (!iter.hasNext()) break;
        FColumnGroup group = (FColumnGroup) iter.next();
        Dimension d = group.getSize(getTable());
        groupHeight += d.height;
        if (p.y > groupHeight) continue;
        columnGroupSelected = group;
        break;
      } while (true);
      if (columnGroupSelected == null) {
        columnSelected = index;
        repaint();
      } else if (columnGroupSelected != null)
        repaint();
        */
    }

    public void mouseEntered(MouseEvent mouseevent) {
    }

    public void mouseExited(MouseEvent e) {
      /*
      columnGroupSelected = null;
      columnSelected = -1;
      repaint();
      */
    }
  }
}

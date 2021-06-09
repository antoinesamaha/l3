package b01.foc.gui.table;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;

public class AutofitTableColumns {
  
  private static final int DEFAULT_COLUMN_PADDING = 5;
  
  /*
   * @param JTable aTable, the JTable to autoresize the columns on @param
   * boolean includeColumnHeaderWidth, use the Column Header width as a minimum
   * width @returns The table width, just in case the caller wants it...
   */
  
  public static Dimension autoResizeTable(JTable aTable, boolean includeColumnHeaderWidth, boolean applyResizeToColumns) {
    return (autoResizeTable(aTable, includeColumnHeaderWidth, DEFAULT_COLUMN_PADDING, applyResizeToColumns));
  }
  
  /*
   * @param JTable aTable, the JTable to autoresize the columns on @param
   * boolean includeColumnHeaderWidth, use the Column Header width as a minimum
   * width @param int columnPadding, how many extra pixels do you want on the
   * end of each column @returns The table width, just in case the caller wants
   * it...
   */
  public static Dimension autoResizeTable(JTable aTable, boolean includeColumnHeaderWidth, int columnPadding, boolean applySizeToColumns) {
    int columnCount = aTable.getColumnCount();
    Dimension dim = null; 
    
    if (columnCount > 0) // must have columns !
    {
      // STEP ONE : Work out the column widths
      
      int columnWidth[] = new int[columnCount];
      
      dim = getTableDimesion(aTable, columnWidth, columnPadding);
      
      // STEP TWO : Dynamically resize each column
      
      /* DANGER DANGER DANGER
       * We have noticed very bad paint behaviour when these are added

      // try changing the size of the column names area
      JTableHeader tableHeader = aTable.getTableHeader();
      Dimension dim = tableHeader.getPreferredSize();
      dim.width = tableWidth;
      tableHeader.setPreferredSize(dim);
      
      dim = aTable.getPreferredSize();
      dim.width = tableWidth;
      aTable.setPreferredSize(dim);
      aTable.setw*/
      
      if(applySizeToColumns){
	      FAbstractTableModel tableModel = (FAbstractTableModel)aTable.getModel();
	      FTableView tableView = tableModel.getTableView();
	      if( tableView != null ){
	        for( int i = 0; i < columnCount; i++){
	          //FTableColumn ftableCol = tableView.getColumnAt(i);
	          
            int col = tableView.getVisibleColumnIndex(i);
            FTableColumn ftableCol = tableView.getColumnAt(col);
            
	          int width = columnWidth[i];
	          if(!ftableCol.isAllowAutoResizing()){
	            width = ftableCol.getPreferredWidth();
	          }
	            
	          //ftableCol.setSize(columnWidth[i]);
	          ftableCol.setPreferredWidth(width);
	          /*TableColumn tableColumn = ftableCol.getTableColumn();
	          tableColumn.setPreferredWidth(width);  */
	          
	        }
	      }
      
	      /*TableColumnModel tableColumnModel = aTable.getColumnModel();
	      TableColumn tableColumn = null;
	      
	      for (int i = 0; i < columnCount; i++) {
	        tableColumn = tableColumnModel.getColumn(i);
	        tableColumn.setPreferredWidth(columnWidth[i]);
	      }*/
	      
	      aTable.doLayout();
      }     
    }
    
    return dim;
  }
  
  private static Dimension getTableDimesion(JTable aTable, int [] columnWidth, int columnPadding){
    int tableWidth = 0;
    int columnCount = aTable.getColumnCount();
    Dimension cellSpacing = aTable.getIntercellSpacing();
    
    int rowHeight = 0;
    
    for (int i = 0; i < columnCount; i++) {
    	Dimension dim = getMaxColumnWidth(aTable, i, true, columnPadding);
      columnWidth[i] = dim.width;
      if(rowHeight < dim.height) rowHeight = dim.height;
      	
      tableWidth += columnWidth[i];
    }
    
    // account for cell spacing too
    tableWidth += ((columnCount - 1) * cellSpacing.width);
    return new Dimension(tableWidth, rowHeight);
  }
  
  private static Dimension getMaxDimensionForString(Component defaultLabel, String text){
    int maxWidth  = 0;
    int maxHeight = 0;
    if(text != null){
      Font font = defaultLabel.getFont();
      FontMetrics fontMetrics = defaultLabel.getFontMetrics(font);
      
	    maxWidth  = SwingUtilities.computeStringWidth(fontMetrics, text);
	    maxHeight = fontMetrics.getHeight();
	    //Globals.logString(" Ascent = " + fontMetrics.getAscent() + " Descent = " + fontMetrics.getDescent() + " Leading = " + fontMetrics.getLeading() + " Leading = " + fontMetrics.getHeight());
    }
    return new Dimension(maxWidth, maxHeight);
  }

  private static Dimension getMaxDimensionForString(String text){
  	return getMaxDimensionForString(new JLabel(text), text);
  }

  private static Dimension getMaxDimensionForString(JLabel defaultLabel){
  	return getMaxDimensionForString(defaultLabel, defaultLabel.getText());
  }
  
  private static Dimension getMaxDimensionForComponent(Component comp, boolean headerCalculation){
  	Dimension dim = null;
    if (comp instanceof JLabel) {
      JLabel jtextComp = (JLabel) comp;
      
      dim = getMaxDimensionForString(jtextComp);
    } else if (comp instanceof JComboBox) {
    	JComboBox jtextComp = (JComboBox) comp;
      
      dim = getMaxDimensionForString(jtextComp, (String)jtextComp.getSelectedItem());
      dim.width  += 25;
      dim.height += 2;

    }else if(comp instanceof JTree || comp instanceof JCheckBox ){
      Dimension preffDim = comp.getPreferredSize();
      int w = preffDim.width;
      int h = 0;
      if(headerCalculation){
      	h = new JLabel("AnyThing").getPreferredSize().height;
      }
      dim = new Dimension(w, h);
    }else {
    	Dimension preffDim = comp.getPreferredSize();
    	dim = new Dimension(preffDim);
    }
    return dim;
  }
  
  /*
   * @param JTable aTable, the JTable to autoresize the columns on @param int
   * columnNo, the column number, starting at zero, to calculate the maximum
   * width on @param boolean includeColumnHeaderWidth, use the Column Header
   * width as a minimum width @param int columnPadding, how many extra pixels do
   * you want on the end of each column @returns The table width, just in case
   * the caller wants it...
   */

  private static Dimension getMaxColumnWidth(JTable aTable, int columnNo, boolean includeColumnHeaderWidth, int columnPadding) {
    TableColumn column = aTable.getColumnModel().getColumn(columnNo);
    Component comp = null;
    Dimension maxDimension = null;
    
    if (includeColumnHeaderWidth) {
      TableCellRenderer headerRenderer = column.getHeaderRenderer();
      if (headerRenderer != null) {
        comp = headerRenderer.getTableCellRendererComponent(aTable, column.getHeaderValue(), false, false, 0, columnNo);
        
        maxDimension = comp != null ? getMaxDimensionForComponent(comp, true) : new Dimension(0, 0);
        
      } else {
        try {
          String headerText = (String) column.getHeaderValue();
          
          maxDimension = getMaxDimensionForString(headerText);
        } catch (ClassCastException ce) {
          // Can't work out the header column width..
        	maxDimension = new Dimension(0, 0);
        }
      }
    }
    
    TableCellRenderer tableCellRenderer = null;
    
    for (int i = 0; i < aTable.getRowCount(); i++) {
      tableCellRenderer = aTable.getCellRenderer(i, columnNo);
      
      comp = tableCellRenderer.getTableCellRendererComponent(aTable, aTable.getValueAt(i, columnNo), false, false, i, columnNo);
      
      Dimension currDim = comp != null ? getMaxDimensionForComponent(comp, false) : new Dimension(0, 0);
      maxDimension.width  = Math.max(maxDimension.width , currDim.width );
      maxDimension.height = Math.max(maxDimension.height, currDim.height);
    }
    
    maxDimension.width += columnPadding;
    
    return maxDimension;
  }
}
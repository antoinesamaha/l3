package b01.foc.wrapper;

import b01.foc.desc.FocDesc;
import b01.foc.gui.FPanel;
import b01.foc.gui.FTreeTablePanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

/*public class WrapperGuiTreePanel extends FPanel{

  private FTreeTablePanel selectionPanel    = null;
  private FocList wrapperBasicResourceList  = null;
  private WrapperTree tree     = null;
  
  public WrapperGuiTreePanel(FocList list, int viewID){
    super("", FPanel.FILL_BOTH);
    
    wrapperBasicResourceList = list;
    FocDesc desc = list.getFocDesc();
    
    if (desc != null && list != null) {
      wrapperBasicResourceList.setDirectImpactOnDatabase(false);
      
      tree = new WrapperTree(wrapperBasicResourceList, viewID);
      
      selectionPanel = new FTreeTablePanel(tree);
      
      addColumns();
      //tableView.addColumn(desc, WrapperDesc.FATHER_NODE_ID, true);
      
      selectionPanel.construct();
      selectionPanel.showRemoveButton(false);
      selectionPanel.showAddButton(false);
      selectionPanel.showEditButton(false);
      showValidationPanel(true);
    }
    add(selectionPanel,0,0);
  }
  
  public void dispose(){
  	super.dispose();
  }
  
  public WrapperTree getTree() {
    return tree;
  }
  
  protected void addColumns(){
    
  }
  
  public FTreeTablePanel getSelectionPanel(){
    return selectionPanel;
  }
}*/

@SuppressWarnings("serial")
public class WrapperGuiTreePanel extends FPanel{
	
	private WrapperTree wrapperTree = null;

  public WrapperGuiTreePanel(FocList list, int viewID){
    super("", FPanel.FILL_BOTH);
    FocDesc desc = list.getFocDesc();
    if (desc != null && list != null) {
      list.setDirectImpactOnDatabase(false);
      this.wrapperTree = newWrapperTree(list, viewID);
      FTreeTablePanel selectionPanel = new FTreeTablePanel(wrapperTree);

      addColumns(selectionPanel.getTableView(), desc);
      
      selectionPanel.construct();
      selectionPanel.showRemoveButton(false);
      selectionPanel.showAddButton(false);
      selectionPanel.showEditButton(false);
      showValidationPanel(true);
      add(selectionPanel,0,0);
    }
  }
  
  public void dispose(){
  	super.dispose();
  	this.wrapperTree = null;
  }
  
  public WrapperTree getWrapperTree(){
  	return this.wrapperTree;
  }
  
  protected WrapperTree newWrapperTree(FocList focList, int viewID){
  	return new WrapperTree(focList, viewID);
  }
  
  protected void addColumns(FTableView tableView, FocDesc focDesc){
  }
}

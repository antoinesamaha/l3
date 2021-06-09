package b01.foc.tree;

public interface FINode {
  public void dispose();
  public String getTitle();
  public void setTitle(String title);
  
  public FNode getFatherNode();
  public void setFatherNode(FNode fatherNode);
  
  public int getNodeDepth();
  public boolean isLeaf();  
  public boolean isRoot();
  public FRootNode getRootNode();
  
  public FINode addChild(String childTitle);
  
  public Object getObject();
  public void setObject(Object object);
  public void scan(TreeScanner scanner);
}

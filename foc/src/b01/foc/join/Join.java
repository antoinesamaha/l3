/*
 * Created on Jan 9, 2006
 */
package b01.foc.join;

/**
 * @author 01Barmaja
 */
public abstract class Join {
  private TableAlias sourceTableAlias = null;
  private TableAlias targetTableAlias = null;
  private int firstJoinFieldID = 0;
  
  public abstract String getLinkCondition();
  public abstract String getUpdateCondition();
  protected abstract int fillRequestDescWithJoinFields_Internal(FocRequestDesc desc, int firstJoinFieldID);
  
  public int fillRequestDescWithJoinFields(FocRequestDesc reqDesc, int firstJoinFieldID) {
    setFirstJoinFieldID(firstJoinFieldID);
    return fillRequestDescWithJoinFields_Internal(reqDesc, firstJoinFieldID);
  }
  
  public Join(TableAlias sourceTableAlias){
    this.sourceTableAlias = sourceTableAlias; 
  }
  
  public TableAlias getSourceAlias(){
    return sourceTableAlias;
  }
  
  public TableAlias getTargetAlias(){
    return targetTableAlias;
  } 
  
  public void setTargetAlias(TableAlias targetTableAlias) {
    this.targetTableAlias = targetTableAlias;
  }
  
  public int getFirstJoinFieldID() {
    return firstJoinFieldID;
  }
  
  public void setFirstJoinFieldID(int firstJoinFieldID) {
    this.firstJoinFieldID = firstJoinFieldID;
  }
}


package b01.foc.tree;

import b01.foc.desc.FocObject;

public class TreeFormulas {
  
  public double getSingleFieldChildAverage(FNode node, int fieldID){
    return getSingleFieldChildSum(node, fieldID, true) / (double)node.getChildCount();
  }
  
  public double getSingleFieldChildSum(FNode node, int fieldID){
    return getSingleFieldChildSum(node, fieldID, true);
  }
  
  public double getSingleFieldChildSum(FNode node, int fieldID, boolean condition){
    
    double sum = 0;
    for( int i = 0; i < node.getChildCount(); i++){
      FNode childNode = node.getChildAt(i);
      
      if( condition ){
        if( childNode != null && childNode.getObject() != null ){
          FocObject focObj = (FocObject)childNode.getObject();
          sum += focObj.getPropertyDouble(fieldID);
        }  
      }
    }
    
    return sum;
  }
  
  public double[] getManyFieldChildSum( FNode node, int [] fieldIDs ){
    
    double [] sum = new double[fieldIDs.length];

    for( int i = 0; i < node.getChildCount(); i++){
      FNode childNode = node.getChildAt(i);
      if( childNode != null && childNode.getObject() != null ){
        FocObject focObj = (FocObject)childNode.getObject();
        for( int j = 0; j < sum.length; j++){
          sum[j] += focObj.getPropertyDouble(fieldIDs[j]);
        }
      }
    }
    return sum;
  }

  public void sumFields(FNode node, int [] fieldIDs){
  	double[] results = getManyFieldChildSum(node, fieldIDs);
  	FocObject focObj = (FocObject) node.getObject();
    for( int j = 0; j < fieldIDs.length; j++){
      focObj.setPropertyDouble(fieldIDs[j], results[j]);
    }
  }
  
  private static TreeFormulas treeFormulas = null;
  public static TreeFormulas getInstance(){
    if( treeFormulas == null ){
      treeFormulas = new TreeFormulas();
    }
    return treeFormulas;
  }
  
}

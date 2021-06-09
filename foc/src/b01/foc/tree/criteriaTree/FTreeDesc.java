package b01.foc.tree.criteriaTree;

import java.util.ArrayList;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FFieldPath;

public class FTreeDesc {
	private ArrayList<FNodeLevel> levelsList = null;
  private int         depthVisibilityLimit = -1;
	
	public FTreeDesc(FocDesc leavesFocDesc){
    levelsList = new ArrayList<FNodeLevel>();
    addNodeLevel(new FNodeLevel(null));
	}
  
  public int getDepthVisibilityLimit() {
    return depthVisibilityLimit < 0 ? getNodeLevelsCount()-1 :depthVisibilityLimit;
  }

  public void setDepthVisibilityLimit(int depthVisibilityLimit) {
    this.depthVisibilityLimit = depthVisibilityLimit;
  }

	public void dispose(){
		if(levelsList != null){
      for(int i=0; i<getNodeLevelsCount(); i++){
        FNodeLevel level = getNodeLevelAt(i);
        level.dispose();
      }
      levelsList.clear();
      levelsList = null;
		}
	}

  public int getNodeLevelsCount(){
    return levelsList.size();
  }
  
  public FNodeLevel getNodeLevelAt(int i){
    return levelsList != null ? levelsList.get(i) : null;
  }
  
	public void addNodeLevel(FNodeLevel level) {
    level.setLevelDepth(levelsList.size());
    levelsList.add(level);
	}
  
  public int getFieldPathLevel(FFieldPath fieldPath){
    int level = -1;
    for (int i = 0; i < getNodeLevelsCount() && level<0; i++){
      FNodeLevel nodeLevel = getNodeLevelAt(i);
      FFieldPath path = nodeLevel.getPath();
      if(fieldPath.isEqualTo(path)){
        level = i;
      }
    }
    return level;
  }

  public boolean containsFieldPath(FFieldPath fieldPath){
    return getFieldPathLevel(fieldPath)>=0;
  }
  
  public FNodeLevel getNodeInfoForLevel(int level){
    FNodeLevel nodeLevel = null;
    if(levelsList != null && level >= 0 && levelsList.size() > level){
      nodeLevel = levelsList.get(level);
    }
    return nodeLevel;
  }
  
}

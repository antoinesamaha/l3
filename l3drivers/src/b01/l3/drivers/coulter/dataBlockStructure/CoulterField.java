package b01.l3.drivers.coulter.dataBlockStructure;

import b01.l3.drivers.coulter.CoulterFrame;

public abstract class CoulterField implements IFieldReadWrite{
  private int groupIndex = 0;
  private String tag = null;

  public CoulterField(int groupIndex, String tag){
    this.groupIndex = groupIndex;
    this.tag = tag;
  }

  public void dispose(){
    this.tag = null;
  }

  public String getTag() {
    return tag;
  }

  public int getGroupIndex() {
    return groupIndex;
  }
  
  public void addTag(StringBuffer str){
    str.append(getTag()+CoulterFrame.FIELD_SEPERATOR);
  }
}

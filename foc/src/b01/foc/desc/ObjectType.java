/*
 * Created on 20-Apr-2005
 */
package b01.foc.desc;

import b01.foc.IFocDescDeclaration;
import b01.foc.list.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class ObjectType implements FMultipleChoiceItemInterface {
  private int type = 0;
  private String title = null;
  //private FocDesc focDesc = null;
  private IFocDescDeclaration iFocDescDeclaration = null;
  private FocList selectionList = null;

  public ObjectType(int type, String title, IFocDescDeclaration iFocDescDeclaration){
    this.type = type;
    this.title = title;
    this.iFocDescDeclaration = iFocDescDeclaration;
    this.selectionList = null;
  }
  
  public ObjectType(int type, String title, FocList selectionList){
    this.type = type;
    this.title = title;
    this.iFocDescDeclaration = null;
    this.selectionList = selectionList;
  }
  
  public void dispose(){
    iFocDescDeclaration = null;
    selectionList = null;
  }
  
  /*public ObjectType(ObjectType sourceType){
    this.type = sourceType.type;
    this.title = sourceType.title;
    this.focDesc = sourceType.focDesc;
    this.selectionList = sourceType.selectionList;
  }*/
  
  public int getType(){
    return type;
  }

  //This method is an implementation of the MultipleChoiceInterface
  public int getId(){
    return getType();
  }
  
  public String getTitle(){
    return title;
  }
  
  public FocList getSelectionList(){
    return selectionList;
  }
  
  public void setSelectionList(FocList list){
    selectionList = list;
  }
  
  public FocDesc getFocDesc(){
    FocDesc ret = null;
    if(selectionList != null){
      ret = selectionList.getFocDesc();
    }
    if(ret == null){
      ret = this.iFocDescDeclaration.getFocDesctiption();
    }
    return ret ; 
  }
}

/*
 * Created on Jul 8, 2005
 */
package b01.foc.list;

import b01.foc.event.*;
import b01.foc.property.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class ListSumComputer implements FocListener{
  private FocList list = null;
  private ArrayList sumColumns = null;
    
  public ListSumComputer(FocList list, FFieldPath fieldPath, FProperty sumProp){
    this.list = list;
    sumColumns = new ArrayList();
    SumColumn col = new SumColumn(fieldPath, sumProp);
    sumColumns.add(col);
  }    

  public void dispose(){
    for(int i=0; i<sumColumns.size(); i++){
      SumColumn sumColumn = (SumColumn) sumColumns.get(i);
      sumColumn.dispose();
    }
    sumColumns.clear();
    sumColumns = null;
    
    list = null;
  }
    
  public void addSumComputation(FFieldPath fieldPath, FProperty sumProp){
    SumColumn col = new SumColumn(fieldPath, sumProp);
    sumColumns.add(col);
  }
  
  public ArrayList getSumColumns() {
    return sumColumns;
  }
  
  public void compute(){
    for(int i=0; i<sumColumns.size(); i++){
      SumColumn sumCol = (SumColumn) sumColumns.get(i);
      sumCol.reset();
    }    
    
    Iterator iter = list.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject obj = (FocObject) iter.next();
      if(obj != null){
        for(int i=0; i<sumColumns.size(); i++){
          SumColumn sumCol = (SumColumn) sumColumns.get(i);
          sumCol.treatObject(obj);
        }    
      }
    }

    for(int i=0; i<sumColumns.size(); i++){
      SumColumn sumCol = (SumColumn) sumColumns.get(i);
      sumCol.sendResult();
    }    
  }
  
  public void focActionPerformed(FocEvent evt) {
    compute();
  }
  
  public class SumColumn{
    public FFieldPath fieldPath = null; 
    public FProperty sumProp = null;
    public double sum = 0;
    
    public SumColumn(FFieldPath fieldPath, FProperty sumProp){
      this.fieldPath = fieldPath; 
      this.sumProp = sumProp;
    }
    
    public void dispose(){
      if(fieldPath != null){
        fieldPath.dispose();
        fieldPath = null;       
      }
      sumProp = null;      
    }
    
    public void reset(){
      sum = 0;  
    }
    
    public void treatObject(FocObject obj){
      FProperty prop = fieldPath.getPropertyFromObject(obj);
      if(prop != null){
        sum += prop.getDouble();
      }
    }
    
    public void sendResult(){
      sumProp.setDouble(sum);
    }
  }
  
  public FocList getList() {
    return list;
  }
}

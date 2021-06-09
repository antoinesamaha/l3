/*
 * Created on Feb 23, 2006
 */
package b01.foc.jasper;

import java.util.*;

import net.sf.jasperreports.engine.*;

/**
 * @author 01Barmaja
 */
public class FocJRDataSource implements JRDataSource{

  private ArrayList arrayList = null;
  private int currentLine = -1;
  
  public FocJRDataSource(){
    arrayList = new ArrayList();
    currentLine = -1;
  }
  
  public int getSize(){
    return arrayList.size();
  }
  
  public FocJRDataRow newDataRow(){
    FocJRDataRow row = new FocJRDataRow();
    arrayList.add(row);
    return row;
  }

  public void sort(Comparator comp){    
    Collections.sort(arrayList, comp);
  }
  
  public void reset(){
  	currentLine = -1;
  }
  
  /*private static class FocJRDataSourceComparator implements Comparator{
  	ArrayList FieldList = null;
  	public FocJRDataSourceComparator(ArrayList fieldList){
  		this.FieldList = fieldList;
  	}

		public int compare(Object o1, Object o2) {
			FocJRDataRow row1 = (FocJRDataRow)o1;
			FocJRDataRow row2 = (FocJRDataRow)o2;
			Iterator<String> iter = FieldList.iterator();
			String key = iter.next();
			Object o = row1.getFieldData(key);
			o.
			return 0;
		}
  	
  }*/
  
  /* (non-Javadoc)
   * @see net.sf.jasperreports.engine.JRDataSource#next()
   */
  public boolean next() throws JRException {
    currentLine++;
    return currentLine < arrayList.size();
  }

  /* (non-Javadoc)
   * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
   */
  public Object getFieldValue(JRField arg0) throws JRException {
    FocJRDataRow row = (FocJRDataRow) arrayList.get(currentLine);
    Object fieldData = row.getFieldData(arg0.getName());
    if(fieldData == null){
      b01.foc.Globals.logString("Could not get field value for " + arg0);
    }else{
      //b01.foc.Globals.logString(arg0 + " = " + fieldData);
    }
    return fieldData;
  }
}

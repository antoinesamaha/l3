package b01.foc.jasper;

import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.list.FocList;
import b01.foc.property.FAttributeLocationProperty;
import b01.foc.property.FProperty;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class JRFocListDataSource implements JRDataSource{

  private FocList list = null;
  private int     i    = 0   ;
  
  public JRFocListDataSource(FocList list){
    this.list = list;
    i = 0;
  }
  
  public Object getFieldValue(JRField jrField) throws JRException {
    //rr Begin
    /*FocObject obj = list.getFocObject(i-1);
    FocDesc focDesc = list.getFocDesc();
    
    FField field = focDesc.getFieldByName(jrField.getName());
    Object retObj = "";
    if(field != null){
      FProperty prop = obj.getFocProperty(field.getID());
      retObj = prop.getObject();
    }
    return retObj;*/
    
    FocObject obj = list.getFocObject(i-1);
    FocDesc focDesc = list.getFocDesc();
    
    FFieldPath fieldPath = FAttributeLocationProperty.newFieldPath(false, jrField.getName(), focDesc);
    FProperty prop = fieldPath.getPropertyFromObject(obj);
    Object retObj = prop.getObject();
   
    return retObj;
    //rr End
  }

  public boolean next() throws JRException {
    boolean hasNext = i < list.size();
    i++;
    return hasNext;
  }
}

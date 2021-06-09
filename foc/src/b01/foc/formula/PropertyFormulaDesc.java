package b01.foc.formula;

import java.util.ArrayList;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

public class PropertyFormulaDesc extends FocDesc {
  public static final int FLD_EXPRESSION                = 1;
  public static final int FLD_FIELD_NAME                = 2;
  
  public static final int FLD_FIRST_OBJECT  = 1000;
  private int fieldCount = 0;
  
  private static ArrayList<IPropertyFormulaDesc> descInterface = null;
  
   public PropertyFormulaDesc(){
     super(PropertyFormula.class, FocDesc.DB_RESIDENT, "FORMULA_BY_PROP", false);
     
     FField focFld = addReferenceField();
     
     focFld = new FCharField("FIELDNAME", "Field Name", FLD_FIELD_NAME, false, 30);    
     addField(focFld);
     
     focFld = new FCharField("FORMULA", "Formula", FLD_EXPRESSION, false, 150);
     focFld.addListener(new FPropertyListener(){

      public void dispose() {
      }

      public void propertyModified(FProperty property) {
        PropertyFormula propertyFormula = (PropertyFormula) (property != null ? property.getFocObject() : null);
        if(propertyFormula != null){
          propertyFormula.updateFatherFormulaProperties();
        }
        
      }
       
     });
     addField(focFld);
     
     
     if( descInterface != null ){
       for(int i = 0; i < descInterface.size(); i++){
         FocDesc desc = descInterface.get(i).getForeignDesc();
         newObjectField(desc);
       }  
     }
     
   }
   
   private int newObjectField(FocDesc desc){
     int currentFieldID = FLD_FIRST_OBJECT + fieldCount;
     
     FObjectField  objField = new FObjectField(desc.getStorageName(), desc.getStorageName(), currentFieldID , false, desc, desc.getStorageName()+"_", this, FField.FLD_PROPERTY_FORMULA_LIST );
     objField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
     objField.setWithList(false);
     addField(objField);
     
     fieldCount++;
     return currentFieldID; 
   }
   
   public int getForeignObjectFieldCount() {
     return fieldCount;
   }
   
   public static void addForeignObject(IPropertyFormulaDesc fieldFormulasInterface){
     if (descInterface == null){
       descInterface = new ArrayList<IPropertyFormulaDesc>();
     }
     descInterface.add(fieldFormulasInterface);
   }

   //ooooooooooooooooooooooooooooooooooo
   // oooooooooooooooooooooooooooooooooo
   // SINGLE LIST
   // oooooooooooooooooooooooooooooooooo
   // oooooooooooooooooooooooooooooooooo
   
   private static FocList list = null;
   public static FocList getList(int mode){
     list = getInstance().getList(list, mode);
     list.setDirectlyEditable(true);
     list.setDirectImpactOnDatabase(false);
     if(list.getListOrder() == null){
       FocListOrder order = new FocListOrder(FField.REF_FIELD_ID);
       list.setListOrder(order);
     }
     return list;    
   }
   
   //ooooooooooooooooooooooooooooooooooo
   // oooooooooooooooooooooooooooooooooo
   // SINGLE INSTANCE
   // oooooooooooooooooooooooooooooooooo
   // oooooooooooooooooooooooooooooooooo

   private static FocDesc focDesc = null;
   
   public static FocDesc getInstance() {
     if (focDesc==null){
       focDesc = new /*XXX*/PropertyFormulaDesc();
     }
     return focDesc;
   }
}

package b01.l3.data;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocLink;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;

public class L3InstrumentMessageDesc extends FocDesc{

  public static final int FLD_INSTRUMENT = 1;
  public static final int FLD_L3_SAMPLE  = 2;
  public static final int FLD_MESSAGE    = 3;
  public static final int FLD_STATUS     = 4;
  
  public static final String FNAME_SAMPLE_PREFIX     = "L3_SAMPLE_" ;
  public static final String FNAME_INSTRUMENT_PREFIX = "INSTRUMENT_";
  public static final String FNAME_STATUS            = "STATUS"     ;
  
  public L3InstrumentMessageDesc() {
    super(L3InstrumentMessage.class, FocDesc.DB_RESIDENT, "L3MESSAGE", false);
    //setGuiBrowsePanelClass(focObjectBrowsePanelClass);
    
    FField fField = addReferenceField();
    
    FObjectField objectField = new FObjectField("INSTRUMENT", "Instrument", FLD_INSTRUMENT, false, Instrument.getFocDesc(), FNAME_INSTRUMENT_PREFIX);
    objectField.setComboBoxCellEditor(InstrumentDesc.FLD_NAME);
    objectField.setDisplayField(InstrumentDesc.FLD_NAME);
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objectField.setSelectionList(Instrument.getList(FocList.NONE));
    addField(objectField);
    
    objectField = new FObjectField("L3_SAMPLE", "L3 Sample", FLD_L3_SAMPLE, false, L3Sample.getFocDesc(), FNAME_SAMPLE_PREFIX, this, L3SampleDesc.FLD_INSTRUMENT_MESSAGE_LIST);
    objectField.setComboBoxCellEditor(L3SampleDesc.FLD_ID);
    objectField.setDisplayField(L3SampleDesc.FLD_ID);
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objectField.setWithList(false);
    addField(objectField);
    
    fField = new FCharField("MESSAGE", "Message", FLD_MESSAGE, false, 250);
    addField(fField);
    
    FMultipleChoiceField statusFld = new FMultipleChoiceField(FNAME_STATUS, "Status", FLD_STATUS, false, 2);
    L3TestDesc.fillMultipleChoiceStatusField(statusFld);
    addField(statusFld);
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static FocLink link = null;

  private static FocLink getLink(){
  	if(link == null){
  		link = new FocLinkSimple(L3InstrumentMessageDesc.getInstance());
  	}
  	return link;
  }
  
  public static FocList newList(){
  	FocList list = new FocList(getLink());
    list = getInstance().getList(list, FocList.NONE);
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
      focDesc = new /*XXX*/L3InstrumentMessageDesc();
    }
    return focDesc;
  }
}

package b01.l3.data;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBlobStringField;
import b01.foc.desc.field.FField;
import b01.l3.InstrumentDesc;
import b01.l3.connector.dbConnector.lisConnectorTables.LisTestDesc;

public class L3InstrumentGraphDesc extends FocDesc{

  public static final int FLD_INSTRUMENT_CODE = 1;
  public static final int FLD_SAMPLE_ID       = 2;
  public static final int FLD_STATUS          = 3;
  public static final int FLD_GRAPH           = 4;
  
  public L3InstrumentGraphDesc() {
    super(L3InstrumentGraph.class, FocDesc.DB_RESIDENT, "L3GRAPH", true);
    setGuiBrowsePanelClass(L3InstrumentGraphGuiBrowsePanel.class);
    setGuiDetailsPanelClass(L3InstrumentGraphGuiDetailsPanel.class);
    
    FField fField = addReferenceField();

    fField = L3SampleDesc.newSampleIDFIeld(FLD_SAMPLE_ID, "SAMPLE_ID");
    fField.setKey(true);
    addField(fField);

    fField = InstrumentDesc.newCodeField(FLD_INSTRUMENT_CODE, "INSTRUMENT_CODE");
    fField.setKey(true);
    addField(fField);

    fField = LisTestDesc.newStatusField(FLD_STATUS);
    addField(fField);

    fField = new FBlobStringField("GRAPH", "Graph", FLD_GRAPH, false, 10, 80);
    addField(fField);    
  }
      
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc == null){
      focDesc = new L3InstrumentGraphDesc();
    }
    return focDesc;
  }
}

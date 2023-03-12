package b01.l3.drivers.astm;

import b01.l3.data.L3Test;

public class OrderLineReader extends FrameReader{

	private String sampleId = null;
	private String testLabel = null; 
	
	private static final int POS_ORDER_SEQUENCE  		= 1;
	private static final int POS_ORDER_SPECIMEN_ID      = 2;
	private static final int POS_ORDER_SPECIMEN 		= 3;
	private static final int POS_ORDER_TEST_ID   		= 4;

	private static final int COMP_SPECIMEN    = 0;
	private static final int COMP_TEST_ID     = 3;

	private int pos_Field_OrderSpecimenID     = -1;
	private int pos_Component_OrderSpecimenID = -1;
	
	public OrderLineReader(int pos_Field_OrderSpecimenID, int pos_Component_OrderSpecimenID){
		super('|', '^');
		this.pos_Component_OrderSpecimenID = pos_Component_OrderSpecimenID;
		this.pos_Field_OrderSpecimenID     = pos_Field_OrderSpecimenID;
	}

	public OrderLineReader(){
		this(-1, -1);
	}

	public void setTest(L3Test test){
	}
	
	public void readToken(String token, int fieldPos, int compPos) {
		b01.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);
		
		if(fieldPos == POS_ORDER_TEST_ID && compPos == COMP_TEST_ID){
			testLabel = token;
		}else{
			if(pos_Field_OrderSpecimenID>=0 && pos_Component_OrderSpecimenID>=0){
				if(fieldPos == pos_Field_OrderSpecimenID && compPos == pos_Component_OrderSpecimenID){
					sampleId = token;
					sampleId = sampleId.trim();				
				}
			}else{
				if(fieldPos == POS_ORDER_SPECIMEN_ID && compPos == 0){
					sampleId = token;
					sampleId = sampleId.trim();
				}else if(fieldPos == POS_ORDER_SPECIMEN && compPos == COMP_SPECIMEN){
					if(sampleId == null){
						sampleId = token;
						sampleId = sampleId.trim();
					}
				}
			}	
		}
	}

	public String getSampleId() {
		return sampleId;
	}

	public String getTestLabel() {
		return testLabel;
	}

	@Override
	public void scanTokens(StringBuffer buff) {
		sampleId = null;
		testLabel = null;
		super.scanTokens(buff);
	}
}

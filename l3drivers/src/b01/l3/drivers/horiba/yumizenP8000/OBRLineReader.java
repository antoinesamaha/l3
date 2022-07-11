package b01.l3.drivers.horiba.yumizenP8000;

import b01.l3.drivers.astm.FrameReader;

//Patient line reader
public class OBRLineReader extends FrameReader{
	
	private String sampleId = null;
	
	public OBRLineReader(){
		super('|', '^');
	}

	/*
		SPM|1|201604163002||EDTA||||MAIN LAB
	 */
	public void readToken(String token, int fieldPos, int compPos) {
		b01.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);
		
		if(fieldPos == 2 && compPos==0) sampleId = new String(token);
	}

	public String getSampleID() {
		return sampleId != null ? sampleId : "";
	}
}

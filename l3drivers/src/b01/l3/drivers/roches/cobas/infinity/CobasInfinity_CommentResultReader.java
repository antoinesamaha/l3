package b01.l3.drivers.roches.cobas.infinity;

import b01.l3.drivers.astm.CommentResultReader;

public class CobasInfinity_CommentResultReader extends CommentResultReader {
	
	private static final int FLD_COMMENT_TEXT  = 3;
	
	private String comment = null;
	
	public void readToken(String token, int fieldPos, int compPos) {
		b01.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);
		
		if(fieldPos == FLD_COMMENT_TEXT){
			try{
				comment = token.trim();
			}catch(Exception e){
				b01.foc.Globals.logException(e); 
			}
		}
	}

	public String getComment() {
		return comment;
	}

}

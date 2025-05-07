package b01.l3.drivers.roches.cobas.infinity;

import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.astm.AstmReceiver;

public class CobasInfinity_AstmReceiver extends AstmReceiver {

	public CobasInfinity_AstmReceiver(AstmDriver driver) {
		super(driver);
	}

	protected void parseCommentAfterResultFrame(StringBuffer data) {
		if (test != null && driver.getAstmParams().isReadResultComment()) {
			CobasInfinity_CommentResultReader commentResultReader = (CobasInfinity_CommentResultReader) getCommentResultReader();
			
			if (commentResultReader != null) {
				commentResultReader.scanTokens(data);
				
				String comment = commentResultReader.getComment();
				if(comment != null && comment.length() > 0) {
					if (test.getNotificationMessage() != null && test.getNotificationMessage().length() > 0) {
						comment = test.getNotificationMessage() + ", " + comment;
					}
					test.setNotificationMessage(comment);
				}
			}
		}
	}
	
}

package b01.sbs;

public interface BServerListener {
	public String replyToReceivedRequest(String request);
	public void   postReply();
}

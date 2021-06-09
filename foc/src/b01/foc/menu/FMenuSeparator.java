package b01.foc.menu;

public class FMenuSeparator extends FMenuItem{

	public FMenuSeparator(){
		super("", ' ', null);
	}
	
	@Override
	public boolean isSeparator() {
		return true;
	}
}

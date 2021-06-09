package b01.foc;

import b01.foc.desc.FocDesc;

public interface IFocDescDeclaration {
	public static final int PRIORITY_FIRST = 1;
	public static final int PRIORITY_SECOND = 2;
	public static final int PRIORITY_THIRD = 3;
	
	public FocDesc getFocDesctiption();
	public int getPriority();
}

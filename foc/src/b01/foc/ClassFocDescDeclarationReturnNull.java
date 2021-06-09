package b01.foc;

import b01.foc.desc.FocDesc;

public class ClassFocDescDeclarationReturnNull extends ClassFocDescDeclaration{

	public ClassFocDescDeclarationReturnNull(Class cls) {
		super(cls);
	}

	@Override
	public FocDesc getFocDesctiption() {
		super.getFocDesctiption();
		return null;//Because we do not want to see the same FocDesc STorage name in the combo box, 
		//yet we want to call the getInstance to do the necessary modifications on the original FocDesc 
	}
	

}

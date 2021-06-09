package b01.fab;

import b01.foc.Application;


public class FabPlug {
	
	public void declareModules(Application app) {
		app.declareModule(FabModule.getInstance());
	}

}

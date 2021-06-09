package b01.foc.formula;

import java.util.ArrayList;
import java.util.Iterator;
import b01.foc.desc.FocObject;
import b01.foc.formula.function.Function;
import b01.foc.tree.TreeScanner;

public class Formula {
	private String                string             = null;
	private Function              mainFunction       = null; 
  private FocObject             formulaFocObject   = null;
	private ArrayList<Function>   usedFunctionsArray = null; 
	private FFormulaTree          formulaTree        = null;
	
	public Formula(String string){
		usedFunctionsArray = new ArrayList<Function>();
		setString(string);
		compile();
	}
	
	public void dispose(){
		string           = null;
		mainFunction     = null; 
    formulaFocObject = null;
    if(formulaTree != null){
    	formulaTree.dispose();
    	formulaTree      = null;
    }
		disposeUsedFunctionsArray();
	}
	
	private void disposeUsedFunctionsArray(){
		if(usedFunctionsArray != null){
			Iterator<Function> iter = usedFunctionsArray.iterator();
			while(iter != null && iter.hasNext()){
				Function function = iter.next();
				if(function != null){
					function.dispose();
				}
			}
		}
		usedFunctionsArray.clear();
		usedFunctionsArray = null;
	}
	
	public void setString(String string){
		this.string = string;
	}
	
	public String getString(){
		return this.string;
	}
	
	public Function getMainFunction() {
		return mainFunction;
	}
	
	private void setFormulaTree(FFormulaTree formulaTree){
		this.formulaTree = formulaTree;
	}
	
	public FFormulaTree getFormulaTree(){
		return this.formulaTree;
	}
	
	public void setMainFunction(Function mainFunction) {
		if(mainFunction != null){
			this.mainFunction = mainFunction;
			this.mainFunction.setFormula(this);
		}
		
	}
	
	private void compile(){
		FFormulaTree formulaTree = new FFormulaTree(getString());
		formulaTree.growTree();
		setFormulaTree(formulaTree);
	}
	
	public boolean computeUponConstruction(){
		return mayNotComputeAutomaticly();
	}
	
	private boolean mayNotComputeAutomaticly(){
		boolean mayNotComputeAutomaticly = false;
		FFormulaTree formulaTree = getFormulaTree();
		if(formulaTree != null){
			formulaTreeScaner scanner = new formulaTreeScaner();
			formulaTree.scan(scanner);
			mayNotComputeAutomaticly = scanner.mayTreeNotComputeAutomaticly();
		}
		return mayNotComputeAutomaticly;
	}
	
	/*public Object compute(IFormulaContext context){
		Object resultObject = null;
		Function mainFunction = getMainFunction();
		if(mainFunction != null){
			resultObject = mainFunction.compute();
			String resultStr = String.valueOf(resultObject);
			Globals.logDebug("Result : " + resultStr);
		}
		return resultObject; 
	}*/
	
	public Object compute(IFormulaContext context){
		FFormulaTree formulaTree = getFormulaTree();
		Object resultObject = null;
		if(formulaTree != null){
			formulaTree.scan(new formulaTreeScaner(context));
			FFormulaRootNode rootNode = (FFormulaRootNode) formulaTree.getRoot();
			FFormulaNode childLevel0 = (FFormulaNode) rootNode.getChildAt(0);
			if(childLevel0 != null){
				resultObject = childLevel0.getCalculatedValue();
			}
		}
		return resultObject;
	}

  public FocObject getFormulaFocObject() {
    return formulaFocObject;
  }

  public void setFormulaFocObject(FocObject formulaFocObject) {
    this.formulaFocObject = formulaFocObject;
  }
  
  private static class formulaTreeScaner implements TreeScanner<FFormulaNode>{
  	private IFormulaContext context = null;
  	private boolean forComputation = false;
  	private boolean mayNotComputeAutomaticly = false;
  	
  	public formulaTreeScaner(IFormulaContext context){
  		this.context = context; 
  		this.forComputation = true;
  	}
  	
  	public boolean mayTreeNotComputeAutomaticly(){
  		return this.mayNotComputeAutomaticly;
  	}
  	
  	public formulaTreeScaner(){
  		this.forComputation = false;
  		mayNotComputeAutomaticly = false;
  	}
  	
		public void afterChildren(FFormulaNode node) {
			if(forComputation){
				afterChildren_Compute(node);
			}else{
				afterChildren_TestIFComputeAutomaticly(node);
			}
		}
		
		public void afterChildren_Compute(FFormulaNode node) {
			if(node != null && !node.isRoot()){
				node.computeCalculatedValue(this.context);
			}			
		}
		
		public void afterChildren_TestIFComputeAutomaticly(FFormulaNode node) {
			if(node != null && !node.isRoot() && !node.isLeaf()){
				String functionName = node.getFunctionName();
				Function function = FunctionFactory.getInstance().getFunction(functionName);
				if(function != null && function.needsManualNotificationToCompute()){
					mayNotComputeAutomaticly = true;
				}
			}
		}

		public boolean beforChildren(FFormulaNode node) {
			return !mayNotComputeAutomaticly;
		}
  }
}



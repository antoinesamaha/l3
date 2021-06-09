package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;
import b01.foc.formula.FunctionFactory;

public class FunctionSum extends Function {
	private static final String FUNCTION_NAME = "SUM";
	private static final String OPERATOR_SYMBOL = "+";

	public Object compute(FFormulaNode formulaNode){
		double result = 0;
		if(formulaNode != null){
			for(int i = 0; i < formulaNode.getChildCount(); i++){
				FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(i);
				if(childNode != null){
					double childNodeCalculatedValue = childNode.getCalculatedValue_double();
					result += childNodeCalculatedValue;
				}
			}
		}
		return result;
	}
	
	public String getName(){
		return FUNCTION_NAME;
	}
	
	public String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public int getOperatorPriority(){
		return FunctionFactory.PRIORITY_ADDITIVE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

}

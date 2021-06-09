package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;
import b01.foc.formula.FunctionFactory;

public class FunctionDivide extends Function {
	private static final String FUNCTION_NAME = "DIV";
	private static final String OPERATOR_SYMBOL = "/";

	/*public Object compute() {
		double value = 0;
		IOperand operand0 = getOperandAt(0);
		String value0Str = String.valueOf(operand0.compute());
		value = Double.valueOf(value0Str);
		
		int operandCount = getOperandCount();
		for(int i = 1; i < operandCount; i++){
			IOperand operand = getOperandAt(i);
			String operandValueStr = String.valueOf(operand.compute());
			double operandValue = Double.valueOf(operandValueStr);
			if(operandValue != 0){
				value /= operandValue;
			}
		}
		return value;
	}*/
	
	public Object compute(FFormulaNode formulaNode){
		double result = 0;
		if(formulaNode != null){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNode != null){
				double childNodeCalculatedValue = childNode.getCalculatedValue_double();
				result = childNodeCalculatedValue;
			}
			for(int i = 1; i < formulaNode.getChildCount(); i++){
				childNode = (FFormulaNode) formulaNode.getChildAt(i);
				if(childNode != null){
					double childNodeCalculatedValue = childNode.getCalculatedValue_double();
					if(childNodeCalculatedValue == 0){
						childNodeCalculatedValue = 1;
					}
					result = result / childNodeCalculatedValue;
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
		return FunctionFactory.PRIORITY_MULTIPLICATIVE;
	}

	@Override
	public boolean needsManualNotificationToCompute() {
		return false;
	}

}

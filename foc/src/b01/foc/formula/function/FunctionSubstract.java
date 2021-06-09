package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;
import b01.foc.formula.FunctionFactory;

public class FunctionSubstract extends Function {
	private static final String FUNCTION_NAME = "SUBSTR";
	private static final String OPERATOR_SYMBOL = "-";

	/*public Object compute() {
		double value = 0;
		IOperand operand0 = getOperandAt(0);
		//String value0Str = (String) operand0.compute();
		String value0Str = String.valueOf(operand0.compute());
		value = Double.valueOf(value0Str);
		
		int operandCount = getOperandCount();
		for(int i = 1; i < operandCount; i++){
			double operandValue = 0; 
			IOperand operand = getOperandAt(i);
			Object operandValueObj = operand.compute();
			if(operandValueObj != null){
				String operandValueStr = String.valueOf(operand.compute());
				operandValue = Double.valueOf(operandValueStr);
			}
			value -= operandValue;
		}
		return value;
	}*/
	
	private double compute_NormalSubstract(FFormulaNode formulaNode){
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
					result -= childNodeCalculatedValue;
				}
			}
		}
		return result;
	}
	
	private double compute_UnaryMinus(FFormulaNode formulaNode){
		double result = 0;
		if(formulaNode != null){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNode != null){
				double childNodeCalculatedValue = childNode.getCalculatedValue_double();
				result = -childNodeCalculatedValue;
			}
		}
		return result;
	}
	
	public Object compute(FFormulaNode formulaNode){
		double result = 0;
		if(formulaNode != null){
			if(formulaNode.getChildCount() == 1){
				result = compute_UnaryMinus(formulaNode);
			}else{
				result = compute_NormalSubstract(formulaNode);
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

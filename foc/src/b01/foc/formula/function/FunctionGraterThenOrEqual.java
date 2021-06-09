package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;
import b01.foc.formula.FunctionFactory;

public class FunctionGraterThenOrEqual extends BooleanFunction {

	private static final String FUNCTION_NAME = "GE";
	private static final String OPERATOR_SYMBOL = ">=";

	/*public Object compute() {
		boolean res = false;
		IOperand operand1 = getOperandAt(0);
		IOperand operand2 = getOperandAt(1);
		if(operand1 != null && operand2 != null){
			String operandStr = String.valueOf(operand1.compute());
			Double operand1Double = Double.valueOf(operandStr);
			operandStr = String.valueOf(operand2.compute());
			Double operand2Double = Double.valueOf(operandStr);
			res = operand1Double.doubleValue() >= operand2Double.doubleValue();
		}
		return res;
	}*/
	
	public Object compute(FFormulaNode formulaNode){
		boolean result = false;
		if(formulaNode != null){
			FFormulaNode childNode1 = (FFormulaNode) formulaNode.getChildAt(0);
			FFormulaNode childNode2 = (FFormulaNode) formulaNode.getChildAt(1);
			if(childNode1 != null && childNode2 != null){
				double double1 = childNode1.getCalculatedValue_double();
				double double2 = childNode2.getCalculatedValue_double();
				result = double1 >= double2;
			}
		}
		return result;
	}
	
	public boolean needsManualNotificationToCompute() {
		return false;
	}
	
	public String getName(){
		return FUNCTION_NAME;
	}
	
	public String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public int getOperatorPriority(){
		return FunctionFactory.PRIORITY_RELATIONAL;
	}
}

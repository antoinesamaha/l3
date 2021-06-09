package b01.foc.formula.function;

import b01.foc.formula.FFormulaNode;

public interface IOperand {
	public Object compute(FFormulaNode formulaNode);
	public void dispose();
}

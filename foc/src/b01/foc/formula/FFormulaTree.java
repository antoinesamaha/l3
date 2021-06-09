package b01.foc.formula;

import javax.swing.Icon;

import b01.foc.gui.treeTable.FTreeTableModel;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;
import b01.foc.tree.FNode;
import b01.foc.tree.FRootNode;
import b01.foc.tree.FTree;
import b01.foc.tree.TreeScanner;

public class FFormulaTree extends FTree {
	
	private String formulaLevel1Format = null; 
	
	@SuppressWarnings("unchecked")
	public FFormulaTree(String completeFormula){
		FFormulaRootNode rootNode = new FFormulaRootNode();
		this.root = rootNode;
		this.formulaLevel1Format = completeFormula;
	}
	
	public void dispose(){
		super.dispose();
		this.formulaLevel1Format = null;
	}
	
	@Override
	public int getDepthVisibilityLimit() {
		return 0;
	}

	@Override
	public FocList getFocList() {
		return null;
	}

	@Override
	public Icon getIconForNode(FNode node) {
		return null;
	}

	@Override
	public FProperty getTreeSpecialProperty(FTreeTableModel treeTableModel, int row) {
		return null;
	}

	@Override
	public void growTreeFromFocList(FocList focList) {
	}

	@Override
	public boolean isNodeLocked(FNode node) {
		return false;
	}

	@Override
	public FNode newEmptyNode(FTreeTableModel treeTableModel, FNode node) {
		return null;
	}
	
	private String getFormulaLevel1Format(){
		return this.formulaLevel1Format;
	}
	
	public void growTree(){
		FRootNode root = getRoot();
		if(root != null){
			FFormulaNode childNode = (FFormulaNode) root.addChild("Main node");
			childNode.setExpression(getFormulaLevel1Format());
			childNode.growNode();
		}
	}
	
	public String getFormulaLevel0Format(){
		growTree();
		FFormulaTreeScaner scaner = new FFormulaTreeScaner();
		scan(scaner);
		return scaner.getFormulaLevel0Format();
	}
	
	private static class FFormulaTreeScaner implements TreeScanner<FFormulaNode>{
		
		private StringBuffer formulaLevel0Format = new StringBuffer();

		public void afterChildren(FFormulaNode node) {
			if(!node.isRoot()){
				String functionName = node.getFunctionName();
				if(functionName != null && functionName.length() > 0){
					String lastChar = formulaLevel0Format.substring(formulaLevel0Format.length() - 1);
					if(lastChar != null && lastChar.equals(FunctionFactory.ARGUMENT_SEPARATOR)){
						formulaLevel0Format.deleteCharAt(formulaLevel0Format.length() -1);
					}
					formulaLevel0Format.append(FunctionFactory.CLOSE_PARENTHESIS);
				}
				if(!node.getFatherNode().isRoot()){
					formulaLevel0Format.append(FunctionFactory.ARGUMENT_SEPARATOR);
				}
			}
		}

		public boolean beforChildren(FFormulaNode node) {
			if(!node.isRoot()){
				String functionName = node.getFunctionName();
				if(functionName != null && functionName.length() > 0){
					formulaLevel0Format.append(functionName);
					formulaLevel0Format.append(FunctionFactory.OPEN_PARENTHESIS);
				}else{
					String expression = node.getExpression();
					if(expression != null)
					formulaLevel0Format.append(expression);
				}
			}
			return true;
		}
		
		public String getFormulaLevel0Format(){
			return String.valueOf(formulaLevel0Format);
		}
	}
}

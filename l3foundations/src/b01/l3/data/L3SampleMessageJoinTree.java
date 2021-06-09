package b01.l3.data;

import b01.foc.desc.field.FFieldPath;
import b01.foc.list.FocList;
import b01.foc.tree.criteriaTree.FCriteriaTree;
import b01.foc.tree.criteriaTree.FNodeLevel;
import b01.foc.tree.criteriaTree.FTreeDesc;
import b01.l3.InstrumentDesc;

public class L3SampleMessageJoinTree extends FCriteriaTree{
	
	private L3SampleMessageJoinTree(FTreeDesc treeDesc){
		super(treeDesc);
	}
	
	public L3SampleMessageJoinTree(FocList list, int viewID){
		this(null);
		FTreeDesc treeDesc = new FTreeDesc(L3SampleMessageJoinDesc.getInstance());
		 
		FNodeLevel nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleDesc.FLD_ID));
		treeDesc.addNodeLevel(nodeLevel);
		
		nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleMessageJoinDesc.FLD_MESSAGE_FIELDS_START + L3InstrumentMessageDesc.FLD_INSTRUMENT, InstrumentDesc.FLD_NAME));
		treeDesc.addNodeLevel(nodeLevel);

		setTreeDesc(treeDesc);
		growTreeFromFocList(list);
	}
}

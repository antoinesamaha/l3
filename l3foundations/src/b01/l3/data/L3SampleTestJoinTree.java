package b01.l3.data;

import b01.foc.desc.field.FFieldPath;
import b01.foc.list.FocList;
import b01.foc.tree.TreeScanner;
import b01.foc.tree.criteriaTree.FCriteriaNode;
import b01.foc.tree.criteriaTree.FCriteriaTree;
import b01.foc.tree.criteriaTree.FNodeLevel;
import b01.foc.tree.criteriaTree.FTreeDesc;

public class L3SampleTestJoinTree extends FCriteriaTree{
	
	public static final int DEFAULT_VIEW                  = 1;
  public static final int VIEW_BY_DISPATCHED_INSTRUMENT = 2;
  public static final int VIEW_BY_RECEIVED_INSTRUMENT   = 3;
  public static final int VIEW_PURGE                    = 4;  
	
	private L3SampleTestJoinTree(FTreeDesc treeDesc){
		super(treeDesc);
	}
	
	public L3SampleTestJoinTree(FocList list, int viewID){
		this(null);
    if (viewID == VIEW_BY_DISPATCHED_INSTRUMENT){
      setTreeDesc(getTreeDesc_ViewByDispatchedInstrument());
    }else if (viewID == VIEW_BY_RECEIVED_INSTRUMENT){
    	setTreeDesc(getTreeDesc_ViewByReceivedInstrument());
    }else{
      setTreeDesc(getTreeDesc_ViewBySample());
    }
		growTreeFromFocList(list);
	}
	
	public void growTreeFromFocList(FocList focList){
		super.growTreeFromFocList(focList);
	 
    scan(new TreeScanner<FCriteriaNode>(){
    	
			public void afterChildren(FCriteriaNode node) {
				
				if(node.getLevelDepth() < 3){
					L3SampleTestJoin fatherSampleTest = (L3SampleTestJoin)node.getObject();
					if(fatherSampleTest != null){
						int fatherStatus = L3TestDesc.TEST_STATUS_NOT_ASSIGNED;

						for(int i=0; i<node.getChildCount(); i++){
							FCriteriaNode child = (FCriteriaNode) node.getChildAt(i);
							L3SampleTestJoin sampleTestJoin = (L3SampleTestJoin) child.getObject();

							int status = sampleTestJoin.getPropertyMultiChoice(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_STATUS);
							if(status < fatherStatus) fatherStatus = status;
							
							if(i == 0){
								fatherSampleTest.setPropertyString(L3SampleDesc.FLD_FIRST_NAME, sampleTestJoin.getPropertyString(L3SampleDesc.FLD_FIRST_NAME));
								fatherSampleTest.setPropertyString(L3SampleDesc.FLD_LAST_NAME, sampleTestJoin.getPropertyString(L3SampleDesc.FLD_LAST_NAME));
  							fatherSampleTest.setPropertyString(L3SampleDesc.FLD_MIDDLE_INITIAL, sampleTestJoin.getPropertyString(L3SampleDesc.FLD_MIDDLE_INITIAL));
  							fatherSampleTest.setPropertyInteger(L3SampleDesc.FLD_AGE, sampleTestJoin.getPropertyInteger(L3SampleDesc.FLD_AGE));
  							fatherSampleTest.setPropertyDate(L3SampleDesc.FLD_DATE_AND_TIME, sampleTestJoin.getPropertyDate(L3SampleDesc.FLD_DATE_AND_TIME));
  							fatherSampleTest.setPropertyDate(L3SampleDesc.FLD_ENTRY_DATE, sampleTestJoin.getPropertyDate(L3SampleDesc.FLD_ENTRY_DATE));
  							fatherSampleTest.setPropertyMultiChoice(L3SampleDesc.FLD_AGE, sampleTestJoin.getPropertyMultiChoice(L3SampleDesc.FLD_LIQUIDE_TYPE));
  							fatherSampleTest.setPropertyString(L3SampleDesc.FLD_PATIENT_ID, sampleTestJoin.getPropertyString(L3SampleDesc.FLD_PATIENT_ID));
  							fatherSampleTest.setPropertyString(L3SampleDesc.FLD_SEXE, sampleTestJoin.getPropertyString(L3SampleDesc.FLD_SEXE));
							}										
						}
					
						fatherSampleTest.setPropertyMultiChoice(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_STATUS, fatherStatus);
					}
				}
			}

			public boolean beforChildren(FCriteriaNode node) {
				return true;
			}
    });
	}
	    
	public FTreeDesc getTreeDesc_ViewBySample(){
		FTreeDesc treeDesc = new FTreeDesc(L3SampleTestJoinDesc.getInstance());
		 
		FNodeLevel nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleDesc.FLD_ID));
		treeDesc.addNodeLevel(nodeLevel);
		
		nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DISPATCH_INSTRUMENT));
		treeDesc.addNodeLevel(nodeLevel);
		
		nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_LABEL));
		treeDesc.addNodeLevel(nodeLevel);
		
		return treeDesc;

	}
	 
	public FTreeDesc getTreeDesc_ViewByDispatchedInstrument(){
		FTreeDesc treeDesc = new FTreeDesc(L3SampleTestJoinDesc.getInstance());
   
		FNodeLevel nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DISPATCH_INSTRUMENT));
		treeDesc.addNodeLevel(nodeLevel);

		nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleDesc.FLD_ID));
		treeDesc.addNodeLevel(nodeLevel);

		nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_LABEL));
		treeDesc.addNodeLevel(nodeLevel);

		return treeDesc;
	}
	
	public FTreeDesc getTreeDesc_ViewByReceivedInstrument(){
		FTreeDesc treeDesc = new FTreeDesc(L3SampleTestJoinDesc.getInstance());
	   
	  FNodeLevel nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_RECEIVE_INSTRUMENT));
	  treeDesc.addNodeLevel(nodeLevel);
	
	  nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleDesc.FLD_ID));
	  treeDesc.addNodeLevel(nodeLevel);
	
	  nodeLevel = new FNodeLevel(FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_LABEL));
	  treeDesc.addNodeLevel(nodeLevel);
	
	  return treeDesc;
	}
}

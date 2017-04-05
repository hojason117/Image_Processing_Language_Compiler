package cop5556sp17;

import java.util.Stack;
import java.util.HashMap;
import java.util.ArrayList;
import cop5556sp17.AST.Dec;

public class SymbolTable {
	//TODO  add fields
	final int pervasiveID;
	int current_scope;
	int next_scope;
	int instance_count;
	Stack<Integer> scope_stack;
	HashMap<String, ArrayList<Dec>> hashTable;
	
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		pervasiveID = 0;
		current_scope = 0;
		next_scope = 1;
		instance_count = 0;
		scope_stack = new Stack<Integer>();
		hashTable = new HashMap<String, ArrayList<Dec>>();
		enterScope();
	}
	
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		current_scope = next_scope++;
		scope_stack.push(current_scope);
	}
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		scope_stack.pop();
		current_scope = scope_stack.peek();
	}
	
	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		ArrayList<Dec> chain = hashTable.get(ident);
		
		if(chain == null) {
			dec.setScopeID(current_scope);
			ArrayList<Dec> list = new ArrayList<Dec>();
			list.add(dec);
			hashTable.put(ident, list);
			instance_count++;
		}
		else {
			for(Dec tempDec : chain) {
				if(tempDec.getIdent().getText().equals(ident)) {
					if(tempDec.getScopeID() == current_scope)
						return false;
				}
			}
			
			dec.setScopeID(current_scope);
			chain.add(dec);
			instance_count++;
		}
		
		return true;
	}
	
	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		Dec pervasive = null;
		Dec best = null;
		
		ArrayList<Dec> chain = hashTable.get(ident);
		if(chain == null)
			return null;
		
		for(Dec tempDec : chain) {
			if(tempDec.getIdent().getText().equals(ident)) {
				if(tempDec.getScopeID() == pervasiveID)
					pervasive = tempDec;
				else {
					for(int i = scope_stack.size()-1; i >= 0; i--) {
						if(scope_stack.get(i) == tempDec.getScopeID()) {
							best = tempDec;
							break;
						}
						else if(best != null && scope_stack.get(i) == best.getScopeID())
							break;
					}
				}
			}
		}
		
		if(best != null)
			return best;
		else if(pervasive != null)
			return pervasive;
		else
			return null;
	}

	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "Current scope: " + current_scope + " Instances count: " + instance_count;
	}
}
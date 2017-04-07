package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

public abstract class Chain extends Statement {
	
	Type.TypeName type;
	
	public Chain(Token firstToken) {
		super(firstToken);
		
		type = Type.TypeName.NONE;
	}
	
	public Type.TypeName getTypeName() {
		return type;
	}
	
	public void setTypeName(Type.TypeName typeName) {
		type = typeName;
	}
}
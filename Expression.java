package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

public abstract class Expression extends ASTNode {
	
	Type.TypeName type;
	
	protected Expression(Token firstToken) {
		super(firstToken);
		
		type = Type.TypeName.NONE;
	}

	public Type.TypeName getTypeName() {
		return type;
	}
	
	public void setTypeName(Type.TypeName typeName) {
		type = typeName;
	}
	
	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;
}
package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

public class IdentLValue extends ASTNode {
	
	Dec dec;
	Type.TypeName type;
	
	public IdentLValue(Token firstToken) {
		super(firstToken);
	}
	
	public Dec getDec() {
		return dec;
	}
	
	public void setDec(Dec d) {
		dec = d;
	}
	
	public Type.TypeName getTypeName() {
		return type;
	}
	
	public void setTypeName(Type.TypeName typeName) {
		type = typeName;
	}
	
	@Override
	public String toString() {
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}
}
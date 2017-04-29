package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import org.objectweb.asm.Label;

public class Dec extends ASTNode {
	
	final Token ident;
	int scopeID;
	int slotID;
	Label start;
	Label end;

	public Dec(Token firstToken, Token ident) {
		super(firstToken);

		this.ident = ident;
		scopeID = -1;
		slotID = -2;
	}

	public Token getType() {
		return firstToken;
	}

	public Token getIdent() {
		return ident;
	}
	
	public int getScopeID() {
		return scopeID;
	}
	
	public void setScopeID(int id) {
		scopeID = id;
	}
	
	public int getSlotID() {
		return slotID;
	}
	
	public void setSlotID(int id) {
		slotID = id;
	}
	
	public Label getStart() {
		return start;
	}
	
	public void setStart(Label sl) {
		start = sl;
	}
	
	public Label getEnd() {
		return end;
	}
	
	public void setEnd(Label el) {
		end = el;
	}

	@Override
	public String toString() {
		return "Dec [ident=" + ident + ", firstToken=" + firstToken + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Dec)) {
			return false;
		}
		Dec other = (Dec) obj;
		if (ident == null) {
			if (other.ident != null) {
				return false;
			}
		} else if (!ident.equals(other.ident)) {
			return false;
		}
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDec(this,arg);
	}
}
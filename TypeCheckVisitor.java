package cop5556sp17;

import java.util.ArrayList;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;
//import cop5556sp17.AST.ASTNode;
//import cop5556sp17.AST.ASTVisitor;
//import cop5556sp17.AST.Tuple;
//import cop5556sp17.AST.AssignmentStatement;
//import cop5556sp17.AST.BinaryChain;
//import cop5556sp17.AST.BinaryExpression;
//import cop5556sp17.AST.Block;
//import cop5556sp17.AST.BooleanLitExpression;
//import cop5556sp17.AST.Chain;
//import cop5556sp17.AST.ChainElem;
//import cop5556sp17.AST.ConstantExpression;
//import cop5556sp17.AST.Dec;
//import cop5556sp17.AST.Expression;
//import cop5556sp17.AST.FilterOpChain;
//import cop5556sp17.AST.FrameOpChain;
//import cop5556sp17.AST.IdentChain;
//import cop5556sp17.AST.IdentExpression;
//import cop5556sp17.AST.IdentLValue;
//import cop5556sp17.AST.IfStatement;
//import cop5556sp17.AST.ImageOpChain;
//import cop5556sp17.AST.IntLitExpression;
//import cop5556sp17.AST.ParamDec;
//import cop5556sp17.AST.Program;
//import cop5556sp17.AST.SleepStatement;
//import cop5556sp17.AST.Statement;
//import cop5556sp17.AST.Type.TypeName;
//import cop5556sp17.AST.WhileStatement;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.*;
//import static cop5556sp17.Scanner.Kind.ARROW;
//import static cop5556sp17.Scanner.Kind.KW_HIDE;
//import static cop5556sp17.Scanner.Kind.KW_MOVE;
//import static cop5556sp17.Scanner.Kind.KW_SHOW;
//import static cop5556sp17.Scanner.Kind.KW_XLOC;
//import static cop5556sp17.Scanner.Kind.KW_YLOC;
//import static cop5556sp17.Scanner.Kind.OP_BLUR;
//import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
//import static cop5556sp17.Scanner.Kind.OP_GRAY;
//import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
//import static cop5556sp17.Scanner.Kind.OP_WIDTH;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		binaryChain.getE0().visit(this, null);
		binaryChain.getE1().visit(this, null);
		
		if(binaryChain.getE0().getTypeName() == URL && binaryChain.getE1().getTypeName() == IMAGE &&
				binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(IMAGE);
		else if(binaryChain.getE0().getTypeName() == FILE && binaryChain.getE1().getTypeName() == IMAGE &&
				binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(IMAGE);
		else if(binaryChain.getE0().getTypeName() == FRAME && binaryChain.getE1() instanceof FrameOpChain &&
				binaryChain.getE1().getFirstToken().isKind(KW_XLOC, KW_YLOC) && binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(INTEGER);
		else if(binaryChain.getE0().getTypeName() == FRAME && binaryChain.getE1() instanceof FrameOpChain &&
				binaryChain.getE1().getFirstToken().isKind(KW_SHOW, KW_HIDE, KW_MOVE) && binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(FRAME);
		else if(binaryChain.getE0().getTypeName() == IMAGE && binaryChain.getE1() instanceof ImageOpChain &&
				binaryChain.getE1().getFirstToken().isKind(OP_WIDTH, OP_HEIGHT) && binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(INTEGER);
		else if(binaryChain.getE0().getTypeName() == IMAGE && binaryChain.getE1().getTypeName() == FRAME &&
				binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(FRAME);
		else if(binaryChain.getE0().getTypeName() == IMAGE && binaryChain.getE1().getTypeName() == FILE &&
				binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(NONE);
		else if(binaryChain.getE0().getTypeName() == IMAGE && binaryChain.getE1() instanceof FilterOpChain &&
				binaryChain.getE1().getFirstToken().isKind(OP_GRAY, OP_BLUR, OP_CONVOLVE) && binaryChain.getArrow().isKind(ARROW, BARARROW))
			binaryChain.setTypeName(IMAGE);
		else if(binaryChain.getE0().getTypeName() == IMAGE && binaryChain.getE1() instanceof ImageOpChain &&
				binaryChain.getE1().getFirstToken().isKind(KW_SCALE) && binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(IMAGE);
		else if(binaryChain.getE0().getTypeName() == IMAGE && binaryChain.getE1() instanceof IdentChain &&
				binaryChain.getArrow().isKind(ARROW))
			binaryChain.setTypeName(IMAGE);
		else
			throw new TypeCheckException("Type error at " + binaryChain.getFirstToken().getLinePos().toString() + ", illegal binary chain.");
		
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		binaryExpression.getE0().visit(this, null);
		binaryExpression.getE1().visit(this, null);
		
		if(binaryExpression.getE0().getTypeName() == INTEGER && binaryExpression.getE1().getTypeName() == INTEGER &&
				binaryExpression.getOp().isKind(PLUS, MINUS, TIMES, DIV))
			binaryExpression.setTypeName(INTEGER);
		else if(binaryExpression.getE0().getTypeName() == IMAGE && binaryExpression.getE1().getTypeName() == IMAGE &&
				binaryExpression.getOp().isKind(PLUS, MINUS))
			binaryExpression.setTypeName(IMAGE);
		else if(binaryExpression.getE0().getTypeName() == INTEGER && binaryExpression.getE1().getTypeName() == IMAGE &&
				binaryExpression.getOp().isKind(TIMES))
			binaryExpression.setTypeName(IMAGE);
		else if(binaryExpression.getE0().getTypeName() == IMAGE && binaryExpression.getE1().getTypeName() == INTEGER &&
				binaryExpression.getOp().isKind(TIMES))
			binaryExpression.setTypeName(IMAGE);
		else if(binaryExpression.getE0().getTypeName() == INTEGER && binaryExpression.getE1().getTypeName() == INTEGER &&
				binaryExpression.getOp().isKind(LT, GT, LE, GE))
			binaryExpression.setTypeName(BOOLEAN);
		else if(binaryExpression.getE0().getTypeName() == BOOLEAN && binaryExpression.getE1().getTypeName() == BOOLEAN &&
				binaryExpression.getOp().isKind(LT, GT, LE, GE))
			binaryExpression.setTypeName(BOOLEAN);
		else if(binaryExpression.getOp().isKind(EQUAL, NOTEQUAL)) {
			if(binaryExpression.getE0().getTypeName() != binaryExpression.getE1().getTypeName())
				throw new TypeCheckException("Type error at " + binaryExpression.getFirstToken().getLinePos().toString() + 
	 					 ", left expression has type " + binaryExpression.getE0().getTypeName() +
	 					 ", but right expression has type " + binaryExpression.getE1().getTypeName());
			
			binaryExpression.setTypeName(BOOLEAN);
		}
		else
			throw new TypeCheckException("Type error at " + binaryExpression.getFirstToken().getLinePos().toString() + ", illegal binary expression.");
		
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symtab.enterScope();
		
		for(Dec d : block.getDecs())
			d.visit(this, null);
		
		for(Statement s : block.getStatements())
			s.visit(this, null);
		
		symtab.leaveScope();
		
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.setTypeName(BOOLEAN);
		
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(filterOpChain.getArg().getExprList().size() != 0)
			throw new TypeCheckException("Type error at " + filterOpChain.getArg().getFirstToken().getLinePos().toString() + 
										 ", expected 0 args but see " + filterOpChain.getArg().getExprList().size());
		
		filterOpChain.setTypeName(IMAGE);
		filterOpChain.getArg().visit(this, null);
		
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(frameOpChain.getFirstToken().isKind(KW_SHOW, KW_HIDE)) {
			if(frameOpChain.getArg().getExprList().size() != 0)
				throw new TypeCheckException("Type error at " + frameOpChain.getArg().getFirstToken().getLinePos().toString() + 
											 ", expected 0 args but see " + frameOpChain.getArg().getExprList().size());
			
			frameOpChain.setTypeName(NONE);
		}
		else if(frameOpChain.getFirstToken().isKind(KW_XLOC, KW_YLOC)) {
			if(frameOpChain.getArg().getExprList().size() != 0)
				throw new TypeCheckException("Type error at " + frameOpChain.getArg().getFirstToken().getLinePos().toString() + 
											 ", expected 0 args but see " + frameOpChain.getArg().getExprList().size());
			
			frameOpChain.setTypeName(INTEGER);
		}
		else if(frameOpChain.getFirstToken().isKind(KW_MOVE)) {
			if(frameOpChain.getArg().getExprList().size() != 2)
				throw new TypeCheckException("Type error at " + frameOpChain.getArg().getFirstToken().getLinePos().toString() + 
											 ", expected 2 args but see " + frameOpChain.getArg().getExprList().size());
			
			frameOpChain.setTypeName(NONE);
		}
		else
			assert false;
		
		frameOpChain.getArg().visit(this, null);
		
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec variable = symtab.lookup(identChain.getFirstToken().getText());
		if(variable == null)
			throw new TypeCheckException("Type error at " + identChain.getFirstToken().getLinePos().toString() + ", variable undeclared or not visible.");
		
		identChain.setDec(variable);
		identChain.setTypeName(Type.getTypeName(variable.getType()));
		
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec variable = symtab.lookup(identExpression.getFirstToken().getText());
		if(variable == null)
			throw new TypeCheckException("Type error at " + identExpression.getFirstToken().getLinePos().toString() + ", variable undeclared or not visible.");
		
		identExpression.setDec(variable);
		identExpression.setTypeName(Type.getTypeName(variable.getType()));
		
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		ifStatement.getE().visit(this, null);
		
		if(ifStatement.getE().getTypeName() != BOOLEAN)
			throw new TypeCheckException("Type error at " + ifStatement.getE().getFirstToken().getLinePos().toString() + 
					 					 ", expression has type " + ifStatement.getE().getTypeName() + ", but expecting " + BOOLEAN);
		
		ifStatement.getB().visit(this, null);
		
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.setTypeName(INTEGER);
		
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		sleepStatement.getE().visit(this, null);
		if(sleepStatement.getE().getTypeName() != INTEGER)
			throw new TypeCheckException("Type error at " + sleepStatement.getE().getFirstToken().getLinePos().toString() + 
										 ", expression has type " + sleepStatement.getE().getTypeName() + ", but expecting " + INTEGER);
		
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		whileStatement.getE().visit(this, null);
		
		if(whileStatement.getE().getTypeName() != BOOLEAN)
			throw new TypeCheckException("Type error at " + whileStatement.getE().getFirstToken().getLinePos().toString() + 
					 					 ", expression has type " + whileStatement.getE().getTypeName() + ", but expecting " + BOOLEAN);
		
		whileStatement.getB().visit(this, null);
		
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = symtab.insert(declaration.getIdent().getText(), declaration);
		if(!flag)
			throw new TypeCheckException("Type error at " + declaration.getIdent().getLinePos().toString() + ", variable already exists in current scope.");
		
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(ParamDec p : program.getParams())
			p.visit(this, null);
		
		program.getB().visit(this, null);
		
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		assignStatement.getVar().visit(this, null);
		assignStatement.getE().visit(this, null);
		
		if(assignStatement.getVar().getTypeName() != assignStatement.getE().getTypeName())
			throw new TypeCheckException("Type error at " + assignStatement.getFirstToken().getLinePos().toString() + 
										 ", variable has type " + assignStatement.getVar().getTypeName() + 
										 ", but expression has type " + assignStatement.getE().getTypeName());
		
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec variable = symtab.lookup(identX.getFirstToken().getText());
		if(variable == null)
			throw new TypeCheckException("Type error at " + identX.getFirstToken().getLinePos().toString() + ", variable undeclared or not visible.");
		
		identX.setDec(variable);
		identX.setTypeName(Type.getTypeName(variable.getType()));
		
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = symtab.insert(paramDec.getIdent().getText(), paramDec);
		if(!flag)
			throw new TypeCheckException("Type error at " + paramDec.getIdent().getLinePos().toString() + ", variable already exists in current scope.");
		
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.setTypeName(INTEGER);
		
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(imageOpChain.getFirstToken().isKind(OP_WIDTH, OP_HEIGHT)) {
			if(imageOpChain.getArg().getExprList().size() != 0)
				throw new TypeCheckException("Type error at " + imageOpChain.getArg().getFirstToken().getLinePos().toString() + 
											 ", expected 0 args but see " + imageOpChain.getArg().getExprList().size());
			
			imageOpChain.setTypeName(INTEGER);
		}
		else if(imageOpChain.getFirstToken().isKind(KW_SCALE)) {
			if(imageOpChain.getArg().getExprList().size() != 1)
				throw new TypeCheckException("Type error at " + imageOpChain.getArg().getFirstToken().getLinePos().toString() + 
											 ", expected 1 args but see " + imageOpChain.getArg().getExprList().size());
			
			imageOpChain.setTypeName(IMAGE);
		}
		else
			assert false;
		
		imageOpChain.getArg().visit(this, null);
		
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(Expression e : tuple.getExprList()) {
			e.visit(this, null);
			
			if(e.getTypeName() != INTEGER)
				throw new TypeCheckException("Type error at " + e.getFirstToken().getLinePos().toString() + 
	 					 ", expression has type " + e.getTypeName() + ", but expecting " + INTEGER);
		}
		
		return null;
	}
}
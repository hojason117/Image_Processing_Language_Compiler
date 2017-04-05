package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;
import java.util.ArrayList;

public class Parser {

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}
	
	Program program() throws SyntaxException {
		//TODO
		//if(t.isKind(EOF))
			//return;
		
		Token firstToken = t;
		
		Program p = null;
		ArrayList<ParamDec> pl = new ArrayList<ParamDec>();
		Block b = null;;
		match(IDENT);
		if(t.isKind(LBRACE))
			b = block();
		else {
			pl.add(paramDec());
			while(t.isKind(COMMA)) {
				consume();
				pl.add(paramDec());
			}
			b = block();
		}
		p = new Program(firstToken, pl, b);
		
		return p;
	}

	ParamDec paramDec() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		ParamDec p = null;
		match(KW_URL, KW_FILE, KW_INTEGER, KW_BOOLEAN);
		p = new ParamDec(firstToken, t);
		match(IDENT);
		
		return p;
	}
	
	Block block() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		Block b = null;
		ArrayList<Dec> dl = new ArrayList<Dec>();
		ArrayList<Statement> sl = new ArrayList<Statement>();
		match(LBRACE);
		while(t.isKind(KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_FRAME, OP_SLEEP, KW_WHILE, KW_IF, IDENT, OP_BLUR, OP_GRAY, OP_CONVOLVE,
					   KW_SHOW, KW_HIDE, KW_MOVE, KW_XLOC, KW_YLOC, OP_WIDTH, OP_HEIGHT, KW_SCALE)) {
			if(t.isKind(KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_FRAME))
				dl.add(dec());
			else
				sl.add(statement());
		}
		match(RBRACE);
		b = new Block(firstToken, dl, sl);
		
		return b;
	}

	Dec dec() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		Dec p = null;
		match(KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_FRAME);
		p = new Dec(firstToken, t);
		match(IDENT);
		
		return p;
	}

	Statement statement() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		Statement s = null;
		switch(t.kind) {
			case OP_SLEEP:
				consume();
				Expression e = expression();
				match(SEMI);
				s = new SleepStatement(firstToken, e);
				break;
			case KW_WHILE:
				s = whileStatement();
				break;
			case KW_IF:
				s = ifStatement();
				break;
			case IDENT: case OP_BLUR: case OP_GRAY: case OP_CONVOLVE: case KW_SHOW: case KW_HIDE:
			case KW_MOVE: case KW_XLOC: case KW_YLOC: case OP_WIDTH: case OP_HEIGHT: case KW_SCALE:
				if(scanner.peek().isKind(ASSIGN))
					s = assign();
				else
					s = chain();
				match(SEMI);
				break;
			default:
				
				break;
		}
		
		return s;
	}
	
	AssignmentStatement assign() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		AssignmentStatement a = null;
		IdentLValue i = new IdentLValue(t);
		match(IDENT);
		match(ASSIGN);
		Expression e = expression();
		a = new AssignmentStatement(firstToken, i, e);
		
		return a;
	}

	Chain chain() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		BinaryChain bc = null;
		ChainElem c0 = null;
		ChainElem c1 = null;
		Token op = null;
		
		c0 = chainElem();
		op = t;
		arrowOp();
		c1 = chainElem();
		bc = new BinaryChain(firstToken, c0, op, c1);
		while(t.isKind(ARROW, BARARROW)) {
			op = t;
			arrowOp();
			c1 = chainElem();
			bc = new BinaryChain(firstToken, bc, op, c1);
		}
		
		return bc;
	}
	
	WhileStatement whileStatement() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		match(KW_WHILE);
		match(LPAREN);
		Expression e = expression();
		match(RPAREN);
		Block b = block();
		
		return new WhileStatement(firstToken, e, b);
	}
	
	IfStatement ifStatement() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		match(KW_IF);
		match(LPAREN);
		Expression e = expression();
		match(RPAREN);
		Block b = block();
		
		return new IfStatement(firstToken, e, b);
	}
	
	void arrowOp() throws SyntaxException {
		//TODO
		match(ARROW, BARARROW);
	}

	ChainElem chainElem() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		ChainElem c = null;
		Tuple tp = null;
		switch(t.kind) {
			case IDENT:
				consume();
				c = new IdentChain(firstToken);
				break;
			case OP_BLUR: case OP_GRAY: case OP_CONVOLVE:
				filterOp();
				tp = arg();
				c = new FilterOpChain(firstToken, tp);
				break;
			case KW_SHOW: case KW_HIDE: case KW_MOVE: case KW_XLOC: case KW_YLOC:
				frameOp();
				tp = arg();
				c = new FrameOpChain(firstToken, tp);
				break;
			case OP_WIDTH: case OP_HEIGHT: case KW_SCALE:
				imageOp();
				tp = arg();
				c = new ImageOpChain(firstToken, tp);
				break;
				
				default:
					//you will want to provide a more useful error message
					throw new SyntaxException("illegal chainElem " + t.kind + " at " + t.getLinePos().toString());
		}
		
		return c;
	}
	
	void filterOp() throws SyntaxException {
		//TODO
		match(OP_BLUR, OP_GRAY, OP_CONVOLVE);
	}
	
	void frameOp() throws SyntaxException {
		//TODO
		match(KW_SHOW, KW_HIDE, KW_MOVE, KW_XLOC, KW_YLOC);
	}
	
	void imageOp() throws SyntaxException {
		//TODO
		match(OP_WIDTH, OP_HEIGHT, KW_SCALE);
	}

	Tuple arg() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		ArrayList<Expression> el = new ArrayList<Expression>();
		if(t.isKind(LPAREN)) {
			consume();
			el.add(expression());
			while(t.isKind(COMMA)) {
				consume();
				el.add(expression());
			}
			match(RPAREN);
		}
		
		return new Tuple(firstToken, el);
	}
	
	Expression expression() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		Expression e0 = null;
		Expression e1 = null;
		e0 = term();
		while(t.isKind(LT, LE, GT, GE, EQUAL, NOTEQUAL)) {
			Token op = t;
			relOp();
			e1 = term();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		
		return e0;
	}

	Expression term() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		Expression e0 = null;
		Expression e1 = null;
		e0 = elem();
		while(t.isKind(PLUS, MINUS, OR)) {
			Token op = t;
			weakOp();
			e1 = elem();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		
		return e0;
	}

	Expression elem() throws SyntaxException {
		//TODO
		Token firstToken = t;
		
		Expression e0 = null;
		Expression e1 = null;
		e0 = factor();
		while(t.isKind(TIMES, DIV, AND, MOD)) {
			Token op = t;
			strongOp();
			e1 = factor();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		
		return e0;
	}

	Expression factor() throws SyntaxException {
		Expression e = null;
		switch (t.kind) {
			case IDENT:
				e = new IdentExpression(t);
				consume();
				break;
			case INT_LIT:
				e = new IntLitExpression(t);
				consume();
				break;
			case KW_TRUE: case KW_FALSE:
				e = new BooleanLitExpression(t);
				consume();
				break;
			case KW_SCREENWIDTH: case KW_SCREENHEIGHT:
				e = new ConstantExpression(t);
				consume();
				break;
			case LPAREN:
				consume();
				e = expression();
				match(RPAREN);
				break;
				
			default:
				//you will want to provide a more useful error message
				throw new SyntaxException("illegal factor " + t.kind + " at " + t.getLinePos().toString());
		}
		
		return e;
	}

	void relOp() throws SyntaxException {
		//TODO
		match(LT, LE, GT, GE, EQUAL, NOTEQUAL);
	}
	
	void weakOp() throws SyntaxException {
		//TODO
		match(PLUS, MINUS, OR);
	}
	
	void strongOp() throws SyntaxException {
		//TODO
		match(TIMES, DIV, AND, MOD);
	}
	
	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF))
			return t;
		
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind))
			return consume();
		
		throw new SyntaxException("saw " + t.kind + " at " + t.getLinePos().toString() + " but expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		for(int i = 0; i < kinds.length; i++) {
			if (t.isKind(kinds[i]))
				return consume();
		}
		
		String errorMessage = "saw " + t.kind + " at " + t.getLinePos().toString() + " but expected {";
		for(int i = 0; i < kinds.length - 1; i++)
			errorMessage = errorMessage + kinds[i] + " | ";
		errorMessage = errorMessage + kinds[kinds.length - 1] + '}';
		
		throw new SyntaxException(errorMessage);
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}
}
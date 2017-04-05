package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//Parser parser = new Parser(scanner);
		//ASTNode ast = parser.expression();
		//assertEquals(IdentExpression.class, ast.getClass());
		assertEquals(new Parser(scanner).expression(), new IdentExpression(scanner.new Token(IDENT, 0, 3)));
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//Parser parser = new Parser(scanner);
		//ASTNode ast = parser.expression();
		//assertEquals(IntLitExpression.class, ast.getClass());
		assertEquals(new Parser(scanner).expression(), new IntLitExpression(scanner.new Token(INT_LIT, 0, 3)));
	}

	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//Parser parser = new Parser(scanner);
		//ASTNode ast = parser.expression();
		//assertEquals(BinaryExpression.class, ast.getClass());
		//BinaryExpression be = (BinaryExpression) ast;
		//assertEquals(IntLitExpression.class, be.getE0().getClass());
		//assertEquals(IdentExpression.class, be.getE1().getClass());
		//assertEquals(PLUS, be.getOp().kind);
		Expression e0 = new IntLitExpression(scanner.new Token(INT_LIT, 0, 1));
		Expression e1 = new IdentExpression(scanner.new Token(IDENT, 2, 3));
		BinaryExpression be = new BinaryExpression(scanner.new Token(INT_LIT, 0, 1), e0, scanner.new Token(PLUS, 1, 1), e1);
		assertEquals(new Parser(scanner).expression(), be);
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        //TupleASTVerify(parser.arg());
        ArrayList<Expression> el = new ArrayList<Expression>();
        el.add(new IntLitExpression(scanner.new Token(INT_LIT, 3, 1)));
        el.add(new IntLitExpression(scanner.new Token(INT_LIT, 5, 1)));
        assertEquals(parser.arg(), new Tuple(scanner.new Token(LPAREN, 2, 1), el));
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}
	
	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog0 {}";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ProgramASTVerify(parser.parse());
		Block b = new Block(scanner.new Token(LBRACE, 6, 1), new ArrayList<Dec>(), new ArrayList<Statement>());
		assertEquals(parser.parse(), new Program(scanner.new Token(IDENT,0, 5), new ArrayList<ParamDec>(), b));
	}
	
	@Test
	public void test_0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		thrown.expect(Parser.SyntaxException.class);
		thrown.expectMessage("saw EOF at LinePos [line=0, posInLine=0] but expected IDENT");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void test_1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "!==";
		thrown.expect(IllegalCharException.class);
		thrown.expectMessage("Illegal char = at pos 2");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void test_2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "/* != 456 abc if while ";
		thrown.expect(IllegalCharException.class);
		thrown.expectMessage("Unclosed comment at pos 0");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void test_3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "\n43486743864130406846846438643640124135468749489000";
		thrown.expect(IllegalNumberException.class);
		thrown.expectMessage("Overflow integer 43486743864130406846846438643640124135468749489000 at pos 1");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	// program ::= IDENT block
	public void test_4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc{image xyz sleep screenwidth;}";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ProgramASTVerify(parser.program());
		ArrayList<Dec> dl = new ArrayList<Dec>();
		dl.add(new Dec(scanner.new Token(KW_IMAGE, 4, 5), scanner.new Token(IDENT, 10, 3)));
		ConstantExpression ce = new ConstantExpression(scanner.new Token(KW_SCREENWIDTH, 20, 11));
		SleepStatement ss = new SleepStatement(scanner.new Token(OP_SLEEP, 14, 5), ce);
		ArrayList<Statement> sl = new ArrayList<Statement>();
		sl.add(ss);
		Block b = new Block(scanner.new Token(LBRACE, 3, 1), dl, sl);
		assertEquals(parser.program(), new Program(scanner.new Token(IDENT,0, 3), new ArrayList<ParamDec>(), b));
	}
	
	@Test
	// program ::= IDENT block
	public void test_5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc {xyz |-> t;";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw EOF at LinePos [line=0, posInLine=15] but expected RBRACE");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	// program ::= IDENT param_dec ( , param_dec )* block
	public void test_6() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc file xyz, integer i{}";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ProgramASTVerify(parser.program());
		ArrayList<ParamDec> pl = new ArrayList<ParamDec>();
		ParamDec p0 = new ParamDec(scanner.new Token(KW_FILE, 4, 4), scanner.new Token(IDENT, 9, 3));
		pl.add(p0);
		ParamDec p1 = new ParamDec(scanner.new Token(KW_INTEGER, 14, 7), scanner.new Token(IDENT, 22, 1));
		pl.add(p1);
		Block b = new Block(scanner.new Token(LBRACE, 23, 1), new ArrayList<Dec>(), new ArrayList<Statement>());
		assertEquals(parser.program(), new Program(scanner.new Token(IDENT,0, 3), pl, b));
	}
	
	@Test
	// program ::= IDENT param_dec ( , param_dec )* block
	public void test_7() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc boolean j url y{}";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw KW_URL at LinePos [line=0, posInLine=14] but expected LBRACE");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	// param_dec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN ) IDENT
	public void test_8() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url www";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ParamDecASTVerify(parser.paramDec());
		ParamDec p = new ParamDec(scanner.new Token(KW_URL, 0, 3), scanner.new Token(IDENT, 4, 3));
		assertEquals(parser.paramDec(), p);
	}
	
	@Test
	// param_dec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN ) IDENT
	public void test_9() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file integer";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw KW_INTEGER at LinePos [line=0, posInLine=5] but expected IDENT");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
	}
	
	@Test
	// block ::= { ( dec | statement) * }
	public void test_10() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{\nif(screenheight){}\nframe a image b\n}";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//BlockASTVerify(parser.block());
		ConstantExpression ce = new ConstantExpression(scanner.new Token(KW_SCREENHEIGHT, 5, 12));
		Block bb = new Block(scanner.new Token(LBRACE, 18, 1), new ArrayList<Dec>(), new ArrayList<Statement>());
		IfStatement is = new IfStatement(scanner.new Token(KW_IF, 2, 2), ce, bb);
		ArrayList<Statement> sl = new ArrayList<Statement>();
		sl.add(is);
		ArrayList<Dec> dl = new ArrayList<Dec>();
		dl.add(new Dec(scanner.new Token(KW_FRAME, 21, 5), scanner.new Token(IDENT, 27, 1)));
		dl.add(new Dec(scanner.new Token(KW_IMAGE, 29, 5), scanner.new Token(IDENT, 35, 1)));
		Block b = new Block(scanner.new Token(LBRACE, 0, 1), dl, sl);
		assertEquals(parser.block(), b);
	}
	
	@Test
	// block ::= { ( dec | statement) * }
	public void test_11() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "\nif(screenheight){}\nframe a image b\n}";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw KW_IF at LinePos [line=1, posInLine=0] but expected LBRACE");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.block();
	}
	
	@Test
	// dec ::= ( KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME ) IDENT
	public void test_12() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "boolean b";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//DecASTVerify(parser.dec());
		Dec d = new Dec(scanner.new Token(KW_BOOLEAN, 0, 7), scanner.new Token(IDENT, 8, 1));
		assertEquals(parser.dec(), d);
	}
	
	@Test
	// dec ::= ( KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME ) IDENT
	public void test_13() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "integer /";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw DIV at LinePos [line=0, posInLine=8] but expected IDENT");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}
	
	@Test
	// statement ::= OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	public void test_14() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep (false);";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//StatementASTVerify(parser.statement());
		BooleanLitExpression be = new BooleanLitExpression(scanner.new Token(KW_FALSE, 7, 5));
		SleepStatement ss = new SleepStatement(scanner.new Token(OP_SLEEP, 0, 5), be);
		assertEquals(parser.statement(), ss);
	}
	
	@Test
	// statement ::= OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	public void test_15() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc <- true";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw EOF at LinePos [line=0, posInLine=11] but expected SEMI");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
	}
	
	@Test
	// assign ::= IDENT ASSIGN expression
	public void test_16() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "jason <- (true + false)";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//AssignmentStatementASTVerify(parser.assign());
		BooleanLitExpression be0 = new BooleanLitExpression(scanner.new Token(KW_TRUE, 10, 4));
		Token op = scanner.new Token(PLUS, 15, 1);
		BooleanLitExpression be1 = new BooleanLitExpression(scanner.new Token(KW_FALSE, 17, 5));
		BinaryExpression b = new BinaryExpression(scanner.new Token(KW_TRUE, 10, 4), be0, op, be1);
		IdentLValue iv = new IdentLValue(scanner.new Token(IDENT, 0, 5));
		AssignmentStatement as = new AssignmentStatement(scanner.new Token(IDENT, 0, 5), iv, b);
		assertEquals(parser.assign(), as);
	}
	
	@Test
	// assign ::= IDENT ASSIGN expression
	public void test_17() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "tom -> 123";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw ARROW at LinePos [line=0, posInLine=4] but expected ASSIGN");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.assign();
	}
	
	@Test
	// chain ::= chainElem arrowOp chainElem ( arrowOp chainElem)*
	public void test_18() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur -> Tim";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ChainASTVerify(parser.chain());
		Tuple t = new Tuple(scanner.new Token(ARROW, 5, 2), new ArrayList<Expression>());
		FilterOpChain fc = new FilterOpChain(scanner.new Token(OP_BLUR, 0, 4), t);
		Token op = scanner.new Token(ARROW, 5, 2);
		IdentChain ic = new IdentChain(scanner.new Token(IDENT, 8, 3));
		BinaryChain bc = new BinaryChain(scanner.new Token(OP_BLUR, 0, 4), fc, op, ic);
		assertEquals(parser.chain(), bc);
	}
	
	@Test
	// chain ::= chainElem arrowOp chainElem ( arrowOp chainElem)*
	public void test_19() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "hide -> abc -> xyz ->";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("illegal chainElem EOF at LinePos [line=0, posInLine=21]");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}
	
	@Test
	// whileStatement ::= KW_WHILE ( expression ) block
	public void test_20() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(false) {\nframe x\n}";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//WhileStatementASTVerify(parser.whileStatement());
		BooleanLitExpression be = new BooleanLitExpression(scanner.new Token(KW_FALSE, 6, 5));
		ArrayList<Dec> dl = new ArrayList<Dec>();
		Dec d = new Dec(scanner.new Token(KW_FRAME, 15, 5), scanner.new Token(IDENT, 21, 1));
		dl.add(d);
		Block b = new Block(scanner.new Token(LBRACE, 13, 1), dl, new ArrayList<Statement>());
		WhileStatement ws = new WhileStatement(scanner.new Token(KW_WHILE, 0, 5), be, b);
		assertEquals(parser.whileStatement(), ws);
	}
	
	@Test
	// whileStatement ::= KW_WHILE ( expression ) block
	public void test_21() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "\nwhile (false {\nframe x\n}";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw LBRACE at LinePos [line=1, posInLine=13] but expected RPAREN");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.whileStatement();
	}
	
	@Test
	// ifStatement ::= KW_IF ( expression ) block
	public void test_22() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(999){}";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//IfStatementASTVerify(parser.ifStatement());
		IntLitExpression ie = new IntLitExpression(scanner.new Token(INT_LIT, 3, 3));
		Block b = new Block(scanner.new Token(LBRACE, 7, 1), new ArrayList<Dec>(), new ArrayList<Statement>());
		IfStatement is = new IfStatement(scanner.new Token(KW_IF, 0, 2), ie, b);
		assertEquals(parser.ifStatement(), is);
	}
	
	@Test
	// ifStatement ::= KW_IF ( expression ) block
	public void test_23() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true){}";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw KW_WHILE at LinePos [line=0, posInLine=0] but expected KW_IF");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.ifStatement();
	}
	
	@Test
	// arrowOp ::= ARROW | BARARROW
	public void test_24() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "|->";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arrowOp();
	}
	
	@Test
	// arrowOp ::= ARROW | BARARROW
	public void test_25() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "-";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw MINUS at LinePos [line=0, posInLine=0] but expected {ARROW | BARARROW}");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arrowOp();
	}
	
	@Test
	// chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	public void test_26() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray(123)";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ChainElemASTVerify(parser.chainElem());
		IntLitExpression ie = new IntLitExpression(scanner.new Token(INT_LIT, 5, 3));
		ArrayList<Expression> el = new ArrayList<Expression>();
		el.add(ie);
		Tuple t = new Tuple(scanner.new Token(LPAREN, 4, 1), el);
		FilterOpChain fc = new FilterOpChain(scanner.new Token(OP_GRAY, 0, 4), t);
		assertEquals(parser.chainElem(), fc);
	}
	
	@Test
	// chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	public void test_27() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "+ (abc)";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("illegal chainElem PLUS at LinePos [line=0, posInLine=0]");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
	}
	
	@Test
	// filterOp ::= OP_BLUR | OP_GRAY | OP_CONVOLVE
	public void test_28() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " \tconvolve ";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.filterOp();
	}
	
	@Test
	// filterOp ::= OP_BLUR | OP_GRAY | OP_CONVOLVE
	public void test_29() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " \n\tmove";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw KW_MOVE at LinePos [line=1, posInLine=1] but expected {OP_BLUR | OP_GRAY | OP_CONVOLVE}");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.filterOp();
	}
	
	@Test
	// frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC | KW_YLOC
	public void test_30() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "xloc";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.frameOp();
	}
	
	@Test
	// frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC | KW_YLOC
	public void test_31() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " ";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw EOF at LinePos [line=0, posInLine=1] but expected {KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC | KW_YLOC}");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.frameOp();
	}
	
	@Test
	// imageOp ::= OP_WIDTH | OP_HEIGHT | KW_SCALE
	public void test_32() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "height";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.imageOp();
	}
	
	@Test
	// imageOp ::= OP_WIDTH | OP_HEIGHT | KW_SCALE
	public void test_33() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "wid";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw IDENT at LinePos [line=0, posInLine=0] but expected {OP_WIDTH | OP_HEIGHT | KW_SCALE}");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.imageOp();
	}
	
	@Test
	// arg ::=  | ( expression ( , expression)* )
	public void test_34() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "\n\t\r";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//TupleASTVerify(parser.arg());
		Tuple t = new Tuple(scanner.new Token(EOF, 3, 0), new ArrayList<Expression>());
		assertEquals(parser.arg(), t);
	}
	
	@Test
	// arg ::=  | ( expression ( , expression)* )
	public void test_35() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(9487940, bool * (abc))";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//TupleASTVerify(parser.arg());
		ArrayList<Expression> el = new ArrayList<Expression>();
		IntLitExpression ie = new IntLitExpression(scanner.new Token(INT_LIT, 1, 7));
		el.add(ie);
		IdentExpression ide0 = new IdentExpression(scanner.new Token(IDENT, 10, 4));
		Token op = scanner.new Token(TIMES, 15, 1);
		IdentExpression ide1 = new IdentExpression(scanner.new Token(IDENT, 18, 3));
		BinaryExpression bie = new BinaryExpression(scanner.new Token(IDENT, 10, 4), ide0, op, ide1);
		el.add(bie);
		Tuple t = new Tuple(scanner.new Token(LPAREN, 0, 1), el);
		assertEquals(parser.arg(), t);
	}
	
	@Test
	// arg ::=  | ( expression ( , expression)* )
	public void test_36() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "((123 % 3) abc)";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw IDENT at LinePos [line=0, posInLine=11] but expected RPAREN");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}
	
	@Test
	// expression ::= term ( relOp term)*
	public void test_37() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "a + b * 1 != 3";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ExpressionASTVerify(parser.expression());
		IdentExpression ide0 = new IdentExpression(scanner.new Token(IDENT, 4, 1));
		Token op0 = scanner.new Token(TIMES, 6, 1);
		IntLitExpression ile0 = new IntLitExpression(scanner.new Token(INT_LIT, 8, 1));
		BinaryExpression bie0 = new BinaryExpression(scanner.new Token(IDENT, 4, 1), ide0, op0, ile0);
		IdentExpression ide1 = new IdentExpression(scanner.new Token(IDENT, 0, 1));
		Token op1 = scanner.new Token(PLUS, 2, 1);
		BinaryExpression bie1 = new BinaryExpression(scanner.new Token(IDENT, 0, 1), ide1, op1, bie0);
		Token op2 = scanner.new Token(NOTEQUAL, 10, 2);
		IntLitExpression ile1 = new IntLitExpression(scanner.new Token(INT_LIT, 13, 1));
		BinaryExpression bie2 = new BinaryExpression(scanner.new Token(IDENT, 0, 1), bie1, op2, ile1);
		assertEquals(parser.expression(), bie2);
	}
	
	@Test
	// expression ::= term ( relOp term)*
	public void test_38() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1 - 2 >= ";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("illegal factor EOF at LinePos [line=0, posInLine=9]");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.expression();
	}
	
	@Test
	// term ::= elem ( weakOp elem)*
	public void test_39() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "a + b * 1 - 3 / 8 % 32";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ExpressionASTVerify(parser.term());
		IdentExpression ide0 = new IdentExpression(scanner.new Token(IDENT, 4, 1));
		Token op0 = scanner.new Token(TIMES, 6, 1);
		IntLitExpression ile0 = new IntLitExpression(scanner.new Token(INT_LIT, 8, 1));
		BinaryExpression bie0 = new BinaryExpression(scanner.new Token(IDENT, 4, 1), ide0, op0, ile0);
		IntLitExpression ile1 = new IntLitExpression(scanner.new Token(INT_LIT, 12, 1));
		Token op1 = scanner.new Token(DIV, 14, 1);
		IntLitExpression ile2 = new IntLitExpression(scanner.new Token(INT_LIT, 16, 1));
		BinaryExpression bie1 = new BinaryExpression(scanner.new Token(INT_LIT, 12, 1), ile1, op1, ile2);
		Token op2 = scanner.new Token(MOD, 18, 1);
		IntLitExpression ile3 = new IntLitExpression(scanner.new Token(INT_LIT, 20, 2));
		BinaryExpression bie2 = new BinaryExpression(scanner.new Token(INT_LIT, 12, 1), bie1, op2, ile3);
		IdentExpression ide1 = new IdentExpression(scanner.new Token(IDENT, 0, 1));
		Token op3 = scanner.new Token(PLUS, 2, 1);
		BinaryExpression bie3 = new BinaryExpression(scanner.new Token(IDENT, 0, 1), ide1, op3, bie0);
		Token op4 = scanner.new Token(MINUS, 10, 1);
		BinaryExpression bie4 = new BinaryExpression(scanner.new Token(IDENT, 0, 1), bie3, op4, bie2);
		assertEquals(parser.term(), bie4);
	}
	
	@Test
	// term ::= elem ( weakOp elem)*
	public void test_40() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "a + b * 1 - < 8 % 0";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("illegal factor LT at LinePos [line=0, posInLine=12]");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.term();
	}
	
	@Test
	// elem ::= factor ( strongOp factor)*
	public void test_41() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "a * (b -3) / 8 % (0 * 5) & screenheight";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ExpressionASTVerify(parser.elem());
		IdentExpression ide0 = new IdentExpression(scanner.new Token(IDENT, 5, 1));
		Token op0 = scanner.new Token(MINUS, 7, 1);
		IntLitExpression ile0 = new IntLitExpression(scanner.new Token(INT_LIT, 8, 1));
		BinaryExpression bie0 = new BinaryExpression(scanner.new Token(IDENT, 5, 1), ide0, op0, ile0);
		IdentExpression ide1 = new IdentExpression(scanner.new Token(IDENT, 0, 1));
		Token op1 = scanner.new Token(TIMES, 2, 1);
		BinaryExpression bie1 = new BinaryExpression(scanner.new Token(IDENT, 0, 1), ide1, op1, bie0);
		Token op2 = scanner.new Token(DIV, 11, 1);
		IntLitExpression ile1 = new IntLitExpression(scanner.new Token(INT_LIT, 13, 1));
		BinaryExpression bie2 = new BinaryExpression(scanner.new Token(IDENT, 0, 1), bie1, op2, ile1);
		IntLitExpression ile2 = new IntLitExpression(scanner.new Token(INT_LIT, 18, 1));
		Token op3 = scanner.new Token(TIMES, 20, 1);
		IntLitExpression ile3 = new IntLitExpression(scanner.new Token(INT_LIT, 22, 1));
		BinaryExpression bie3 = new BinaryExpression(scanner.new Token(INT_LIT, 18, 1), ile2, op3, ile3);
		Token op4 = scanner.new Token(MOD, 15, 1);
		BinaryExpression bie4 = new BinaryExpression(scanner.new Token(IDENT, 0, 1), bie2, op4, bie3);
		Token op5 = scanner.new Token(AND, 25, 1);
		ConstantExpression ce = new ConstantExpression(scanner.new Token(KW_SCREENHEIGHT, 27, 12));
		BinaryExpression bie5 = new BinaryExpression(scanner.new Token(IDENT, 0, 1), bie4, op5, ce);
		assertEquals(parser.elem(), bie5);
	}
	
	@Test
	// elem ::= factor ( strongOp factor)*
	public void test_42() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "|";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("illegal factor OR at LinePos [line=0, posInLine=0]");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.elem();
	}
	
	@Test
	// factor ::= IDENT | INT_LIT | KW_TRUE | KW_FALSE | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
	public void test_43() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(true == false)";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ExpressionASTVerify(parser.factor());
		BooleanLitExpression be0 = new BooleanLitExpression(scanner.new Token(KW_TRUE, 1, 4));
		Token op = scanner.new Token(EQUAL, 6, 2);
		BooleanLitExpression be1 = new BooleanLitExpression(scanner.new Token(KW_FALSE, 9, 5));
		BinaryExpression bie = new BinaryExpression(scanner.new Token(KW_TRUE, 1, 4), be0, op, be1);
		assertEquals(parser.factor(), bie);
	}
	
	@Test
	// factor ::= IDENT | INT_LIT | KW_TRUE | KW_FALSE | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
	public void test_44() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "==";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("illegal factor EQUAL at LinePos [line=0, posInLine=0]");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
	}
	
	@Test
	// relOp ::= LT | LE | GT | GE | EQUAL | NOTEQUAL
	public void test_45() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = ">";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.relOp();
	}
	
	@Test
	// relOp ::= LT | LE | GT | GE | EQUAL | NOTEQUAL
	public void test_46() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "+";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw PLUS at LinePos [line=0, posInLine=0] but expected {LT | LE | GT | GE | EQUAL | NOTEQUAL}");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.relOp();
	}
	
	@Test
	// weakOp ::= PLUS | MINUS | OR
	public void test_47() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "|";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.weakOp();
	}
	
	@Test
	// weakOp ::= PLUS | MINUS | OR
	public void test_48() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "&";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw AND at LinePos [line=0, posInLine=0] but expected {PLUS | MINUS | OR}");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.weakOp();
	}
	
	@Test
	// strongOp ::= TIMES | DIV | AND | MOD
	public void test_49() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "%";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.strongOp();
	}
	
	@Test
	// strongOp ::= TIMES | DIV | AND | MOD
	public void test_50() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = ">=";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw GE at LinePos [line=0, posInLine=0] but expected {TIMES | DIV | AND | MOD}");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.strongOp();
	}
	
	@Test
	public void test_51() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc file xyz, integer i {\nif(screenheight) {\nhide -> abc -> move (1+2);\nxyz <- 777;\n\n}\nframe a image b\n}";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ProgramASTVerify(parser.parse());
		
		ArrayList<ParamDec> pl = new ArrayList<ParamDec>();
		ParamDec p0 = new ParamDec(scanner.new Token(KW_FILE, 4, 4), scanner.new Token(IDENT, 9, 3));
		pl.add(p0);
		ParamDec p1 = new ParamDec(scanner.new Token(KW_INTEGER, 14, 7), scanner.new Token(IDENT, 22, 1));
		pl.add(p1);
		
		ArrayList<Statement> sl1 = new ArrayList<Statement>();
		
		ArrayList<Statement> sl0 = new ArrayList<Statement>();
		
		Tuple t0 = new Tuple(scanner.new Token(ARROW, 50, 2), new ArrayList<Expression>());
		FrameOpChain fc0 = new FrameOpChain(scanner.new Token(KW_HIDE, 45, 4), t0);
		Token op0 = scanner.new Token(ARROW, 50, 2);
		IdentChain ic = new IdentChain(scanner.new Token(IDENT, 53, 3));
		BinaryChain bc0 = new BinaryChain(scanner.new Token(KW_HIDE, 45, 4), fc0, op0, ic);
		
		IntLitExpression ile0 = new IntLitExpression(scanner.new Token(INT_LIT, 66, 1));
		Token op1 = scanner.new Token(PLUS, 67, 1);
		IntLitExpression ile1 = new IntLitExpression(scanner.new Token(INT_LIT, 68, 1));
		BinaryExpression be0 = new BinaryExpression(scanner.new Token(INT_LIT, 66, 1), ile0, op1, ile1);
		ArrayList<Expression> el = new ArrayList<Expression>();
		el.add(be0);
		Tuple t1 = new Tuple(scanner.new Token(LPAREN, 65, 1), el);
		FrameOpChain fc1 = new FrameOpChain(scanner.new Token(KW_MOVE, 60, 4), t1);
		Token op2 = scanner.new Token(ARROW, 57, 2);
		BinaryChain bc1 = new BinaryChain(scanner.new Token(KW_HIDE, 45, 4), bc0, op2, fc1);
		sl0.add(bc1);
		
		IdentLValue iv = new IdentLValue(scanner.new Token(IDENT, 72, 3));
		IntLitExpression ile2 = new IntLitExpression(scanner.new Token(INT_LIT, 79, 3));
		AssignmentStatement as = new AssignmentStatement(scanner.new Token(IDENT, 72, 3), iv, ile2);
		sl0.add(as);
		
		Block b0 = new Block(scanner.new Token(LBRACE, 43, 1), new ArrayList<Dec>(), sl0);
		
		ConstantExpression ce = new ConstantExpression(scanner.new Token(KW_SCREENHEIGHT, 29, 12));
		IfStatement is = new IfStatement(scanner.new Token(KW_IF, 26, 2), ce, b0);
		
		sl1.add(is);
		
		ArrayList<Dec> dl0 = new ArrayList<Dec>();
		Dec d0 = new Dec(scanner.new Token(KW_FRAME, 87, 5), scanner.new Token(IDENT, 93, 1));
		dl0.add(d0);
		Dec d1 = new Dec(scanner.new Token(KW_IMAGE, 95, 5), scanner.new Token(IDENT, 101, 1));
		dl0.add(d1);
		
		Block b1 = new Block(scanner.new Token(LBRACE, 24, 1), dl0, sl1);
		
		Program p = new Program(scanner.new Token(IDENT, 0, 3), pl, b1);
		
		assertEquals(parser.parse(), p);
	}
	
	@Test
	public void test_52() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog1 {while(screenwidth == true) {\nif(screenheight == 10 != 20){\n/*This is a test."
				+ "\nHahaha*/\nimage a\nb <- (123 & abc) * screenwidth / 3 | screenheight;\n}}}";
		
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ProgramASTVerify(parser.parse());
		
		ArrayList<Statement> sl2 = new ArrayList<Statement>();
		
		ConstantExpression ce3 = new ConstantExpression(scanner.new Token(KW_SCREENWIDTH, 13, 11));
		Token op6 = scanner.new Token(EQUAL, 25, 2);
		BooleanLitExpression ble = new BooleanLitExpression(scanner.new Token(KW_TRUE, 28, 4));
		BinaryExpression be6 = new BinaryExpression(scanner.new Token(KW_SCREENWIDTH, 13, 11), ce3, op6, ble);
		
		ArrayList<Statement> sl1 = new ArrayList<Statement>();
		
		ConstantExpression ce0 = new ConstantExpression(scanner.new Token(KW_SCREENHEIGHT, 39, 12));
		Token op0 = scanner.new Token(EQUAL, 52, 2);
		IntLitExpression ile0 = new IntLitExpression(scanner.new Token(INT_LIT, 55, 2));
		BinaryExpression be0 = new BinaryExpression(scanner.new Token(KW_SCREENHEIGHT, 39, 12), ce0, op0, ile0);
		
		Token op1 = scanner.new Token(NOTEQUAL, 58, 2);
		IntLitExpression ile1 = new IntLitExpression(scanner.new Token(INT_LIT, 61, 2));
		BinaryExpression be1 = new BinaryExpression(scanner.new Token(KW_SCREENHEIGHT, 39, 12), be0, op1, ile1);
		
		ArrayList<Dec> dl = new ArrayList<Dec>();
		Dec d0 = new Dec(scanner.new Token(KW_IMAGE, 93, 5), scanner.new Token(IDENT, 99, 1));
		dl.add(d0);
		
		ArrayList<Statement> sl0 = new ArrayList<Statement>();
		
		IntLitExpression ile2 = new IntLitExpression(scanner.new Token(INT_LIT, 107, 3));
		Token op2 = scanner.new Token(AND, 111, 1);
		IdentExpression ide = new IdentExpression(scanner.new Token(IDENT, 113, 3));
		BinaryExpression be2 = new BinaryExpression(scanner.new Token(INT_LIT, 107, 3), ile2, op2, ide);
		
		Token op3 = scanner.new Token(TIMES, 118, 1);
		ConstantExpression ce1 = new ConstantExpression(scanner.new Token(KW_SCREENWIDTH, 120, 11));
		//BinaryExpression be3 = new BinaryExpression(scanner.new Token(INT_LIT, 107, 3), be2, op3, ce1);
		BinaryExpression be3 = new BinaryExpression(scanner.new Token(LPAREN, 106, 1), be2, op3, ce1);
		
		Token op4 = scanner.new Token(DIV, 132, 1);
		IntLitExpression ile3 = new IntLitExpression(scanner.new Token(INT_LIT, 134, 1));
		//BinaryExpression be4 = new BinaryExpression(scanner.new Token(INT_LIT, 107, 3), be3, op4, ile3);
		BinaryExpression be4 = new BinaryExpression(scanner.new Token(LPAREN, 106, 1), be3, op4, ile3);
		
		Token op5 = scanner.new Token(OR, 136, 1);
		ConstantExpression ce2 = new ConstantExpression(scanner.new Token(KW_SCREENHEIGHT, 138, 12));
		//BinaryExpression be5 = new BinaryExpression(scanner.new Token(INT_LIT, 107, 3), be4, op5, ce2);
		BinaryExpression be5 = new BinaryExpression(scanner.new Token(LPAREN, 106, 1), be4, op5, ce2);
		
		IdentLValue idv = new IdentLValue(scanner.new Token(IDENT, 101, 1));
		AssignmentStatement as = new AssignmentStatement(scanner.new Token(IDENT, 101, 1), idv, be5);
		
		sl0.add(as);
		Block b0 = new Block(scanner.new Token(LBRACE, 64, 1), dl, sl0);
		
		IfStatement is = new IfStatement(scanner.new Token(KW_IF, 36, 2), be1, b0);
		sl1.add(is);
		Block b1 = new Block(scanner.new Token(LBRACE, 34, 1), new ArrayList<Dec>(), sl1);
		
		WhileStatement ws = new WhileStatement(scanner.new Token(KW_WHILE, 7, 5), be6, b1);
		sl2.add(ws);
		Block b2 = new Block(scanner.new Token(LBRACE, 6, 1), new ArrayList<Dec>(), sl2);
		
		Program p = new Program(scanner.new Token(IDENT, 0, 5), new ArrayList<ParamDec>(), b2);
		
		assertEquals(parser.parse(), p);
	}
	
	@Test
	public void test_52_1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(123) | screenheight - xyz";
		
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		
		IntLitExpression ile = new IntLitExpression(scanner.new Token(INT_LIT, 1, 3));
		Token op0 = scanner.new Token(OR, 6, 1);
		ConstantExpression ce = new ConstantExpression(scanner.new Token(KW_SCREENHEIGHT, 8, 12));
		//BinaryExpression be0 = new BinaryExpression(scanner.new Token(INT_LIT, 1, 3), ile, op0, ce);
		BinaryExpression be0 = new BinaryExpression(scanner.new Token(LPAREN, 0, 1), ile, op0, ce);
		Token op1 = scanner.new Token(MINUS, 21, 1);
		IdentExpression ide = new IdentExpression(scanner.new Token(IDENT, 23, 3));
		//BinaryExpression be1 = new BinaryExpression(scanner.new Token(INT_LIT, 1, 3), be0, op1, ide);
		BinaryExpression be1 = new BinaryExpression(scanner.new Token(LPAREN, 0, 1), be0, op1, ide);
		
		assertEquals(parser.expression(), be1);
	}
	
	@Test
	public void test_52_2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(123) + xyz";
		
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		
		IntLitExpression ile = new IntLitExpression(scanner.new Token(INT_LIT, 1, 3));
		Token op = scanner.new Token(PLUS, 6, 1);
		IdentExpression ide = new IdentExpression(scanner.new Token(IDENT, 8, 3));
		//BinaryExpression be = new BinaryExpression(scanner.new Token(INT_LIT, 1, 3), ile, op, ide);
		BinaryExpression be = new BinaryExpression(scanner.new Token(LPAREN, 0, 1), ile, op, ide);
		
		assertEquals(parser.expression(), be);
	}
	
	@Test
	public void test_52_3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(1 + 2) * 3";
		
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		
		IntLitExpression ile0 = new IntLitExpression(scanner.new Token(INT_LIT, 1, 1));
		Token op0 = scanner.new Token(PLUS, 3, 1);
		IntLitExpression ile1 = new IntLitExpression(scanner.new Token(INT_LIT, 5, 1));
		BinaryExpression be0 = new BinaryExpression(scanner.new Token(INT_LIT, 1, 1), ile0, op0, ile1);
		Token op1 = scanner.new Token(TIMES, 8, 1);
		IntLitExpression ile2 = new IntLitExpression(scanner.new Token(INT_LIT, 10, 1));
		//BinaryExpression be1 = new BinaryExpression(scanner.new Token(INT_LIT, 1, 1), be0, op1, ile2);
		BinaryExpression be1 = new BinaryExpression(scanner.new Token(LPAREN, 0, 1), be0, op1, ile2);
		
		assertEquals(parser.expression(), be1);
	}
	
	@Test
	public void test_53() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = ";;;";
		thrown.expect(SyntaxException.class);
		thrown.expectMessage("saw SEMI at LinePos [line=0, posInLine=0] but expected IDENT");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void test_54() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "/**/";
		thrown.expect(Parser.SyntaxException.class);
		thrown.expectMessage("saw EOF at LinePos [line=0, posInLine=4] but expected IDENT");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void test_55() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "/* != 456 abc if while */";
		thrown.expect(Parser.SyntaxException.class);
		thrown.expectMessage("saw EOF at LinePos [line=0, posInLine=25] but expected IDENT");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void test_56() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc{sleep 2147483647;}";
		Scanner scanner = new Scanner(input);
		Parser parser = new Parser(scanner.scan());
		//ProgramASTVerify(parser.parse());
		IntLitExpression ie = new IntLitExpression(scanner.new Token(INT_LIT, 10, 10));
		SleepStatement ss = new SleepStatement(scanner.new Token(OP_SLEEP, 4, 5), ie);
		ArrayList<Statement> sl = new ArrayList<Statement>();
		sl.add(ss);
		Block b = new Block(scanner.new Token(LBRACE, 3, 1), new ArrayList<Dec>(), sl);
		assertEquals(parser.parse(), new Program(scanner.new Token(IDENT,0, 3), new ArrayList<ParamDec>(), b));
	}
	
	@Test
	public void test_57() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc{sleep 2147483648;}";
		thrown.expect(IllegalNumberException.class);
		thrown.expectMessage("Overflow integer 2147483648 at pos 10");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	
	
	
	
	

	/*void ProgramASTVerify(ASTNode ast) {
		assertEquals(Program.class, ast.getClass());
		ArrayList<ParamDec> pl = ((Program)ast).getParams();
		for(int i = 0; i < pl.size(); i++)
			ParamDecASTVerify(pl.get(i));
		BlockASTVerify(((Program)ast).getB());
	}
	
	void ParamDecASTVerify(ASTNode ast) {
		assertEquals(ParamDec.class, ast.getClass());
	}
	
	void BlockASTVerify(ASTNode ast) {
		assertEquals(Block.class, ast.getClass());
		ArrayList<Dec> dl = ((Block)ast).getDecs();
		for(int i = 0; i < dl.size(); i++)
			DecASTVerify(dl.get(i));
		ArrayList<Statement> sl = ((Block)ast).getStatements();
		for(int i = 0; i < sl.size(); i++)
			StatementASTVerify(sl.get(i));
	}
	
	void DecASTVerify(ASTNode ast) {
		assertEquals(Dec.class, ast.getClass());
	}
	
	void StatementASTVerify(ASTNode ast) {
		if(!(ast instanceof Statement))
			assert false;
		switch(ast.getFirstToken().kind) {
			case OP_SLEEP:
				SleepStatementASTVerify(ast);
				break;
			case KW_WHILE:
				WhileStatementASTVerify(ast);
				break;
			case KW_IF:
				IfStatementASTVerify(ast);
				break;
			
			default:
				if(ast instanceof Chain)
					ChainASTVerify(ast);
				else if(ast.getClass() == AssignmentStatement.class)
					AssignmentStatementASTVerify(ast);
				else
					assert false;
				break;
		}
	}
	
	void SleepStatementASTVerify(ASTNode ast) {
		assertEquals(SleepStatement.class, ast.getClass());
		ExpressionASTVerify(((SleepStatement)ast).getE());
	}
	
	void AssignmentStatementASTVerify(ASTNode ast) {
		assertEquals(AssignmentStatement.class, ast.getClass());
		IdentLValueASTVerify(((AssignmentStatement)ast).getVar());
		ExpressionASTVerify(((AssignmentStatement)ast).getE());
	}
	
	void ChainASTVerify(ASTNode ast) {
		if(!(ast instanceof Chain))
			assert false;
		if(ast instanceof ChainElem)
			ChainElemASTVerify(ast);
		else if(ast.getClass() == BinaryChain.class)
			BinaryChainASTVerify(ast);
		else
			assert false;;
	}
	
	void ChainElemASTVerify(ASTNode ast) {
		if(!(ast instanceof ChainElem))
			assert false;
		switch(ast.getFirstToken().kind) {
			case IDENT:
				IdentChainASTVerify(ast);
				break;
			case OP_BLUR: case OP_GRAY: case OP_CONVOLVE:
				FilterOpChainASTVerify(ast);
				break;
			case KW_SHOW: case KW_HIDE: case KW_MOVE: case KW_XLOC: case KW_YLOC:
				FrameOpChainASTVerify(ast);
				break;
			case OP_WIDTH: case OP_HEIGHT: case KW_SCALE:
				ImageOpChainASTVerify(ast);
				break;
				
			default:
				assert false;
				break;
		}
	}
	
	void IdentChainASTVerify(ASTNode ast) {
		assertEquals(IdentChain.class, ast.getClass());
	}
	
	void FilterOpChainASTVerify(ASTNode ast) {
		assertEquals(FilterOpChain.class, ast.getClass());
		TupleASTVerify(((FilterOpChain)ast).getArg());
	}
	
	void FrameOpChainASTVerify(ASTNode ast) {
		assertEquals(FrameOpChain.class, ast.getClass());
		TupleASTVerify(((FrameOpChain)ast).getArg());
	}
	
	void ImageOpChainASTVerify(ASTNode ast) {
		assertEquals(ImageOpChain.class, ast.getClass());
		TupleASTVerify(((ImageOpChain)ast).getArg());
	}
	
	void BinaryChainASTVerify(ASTNode ast) {
		assertEquals(BinaryChain.class, ast.getClass());
		ChainASTVerify(((BinaryChain)ast).getE0());
		ChainElemASTVerify(((BinaryChain)ast).getE1());
	}
	
	void WhileStatementASTVerify(ASTNode ast) {
		assertEquals(WhileStatement.class, ast.getClass());
		ExpressionASTVerify(((WhileStatement)ast).getE());
		BlockASTVerify(((WhileStatement)ast).getB());
	}
	
	void IfStatementASTVerify(ASTNode ast) {
		assertEquals(IfStatement.class, ast.getClass());
		ExpressionASTVerify(((IfStatement)ast).getE());
		BlockASTVerify(((IfStatement)ast).getB());
	}
	
	void ExpressionASTVerify(ASTNode ast) {
		if(!(ast instanceof Expression))
			assert false;
		if(ast.getClass() == BinaryExpression.class)
			BinaryExpressionASTVerify(ast);
		else {
			switch(ast.getFirstToken().kind) {
				case IDENT:
					IdentExpressionASTVerify(ast);
					break;
				case INT_LIT:
					IntLitExpressionASTVerify(ast);
					break;
				case KW_TRUE: case KW_FALSE:
					BooleanLitExpressionASTVerify(ast);
					break;
				case KW_SCREENHEIGHT: case KW_SCREENWIDTH:
					ConstantExpressionASTVerify(ast);
					break;
					
				default:
					assert false;
					break;
			}
		}
	}
	
	void IdentExpressionASTVerify(ASTNode ast) {
		assertEquals(IdentExpression.class, ast.getClass());
	}
	
	void IdentLValueASTVerify(ASTNode ast) {
		assertEquals(IdentLValue.class, ast.getClass());
	}
	
	void IntLitExpressionASTVerify(ASTNode ast) {
		assertEquals(IntLitExpression.class, ast.getClass());
	}
	
	void BooleanLitExpressionASTVerify(ASTNode ast) {
		assertEquals(BooleanLitExpression.class, ast.getClass());
	}
	
	void ConstantExpressionASTVerify(ASTNode ast) {
		assertEquals(ConstantExpression.class, ast.getClass());
	}
	
	void BinaryExpressionASTVerify(ASTNode ast) {
		assertEquals(BinaryExpression.class, ast.getClass());
		ExpressionASTVerify(((BinaryExpression)ast).getE0());
		ExpressionASTVerify(((BinaryExpression)ast).getE1());
	}
	
	void TupleASTVerify(ASTNode ast) {
		assertEquals(Tuple.class, ast.getClass());
		List<Expression> el = ((Tuple)ast).getExprList();
		for(int i = 0; i < el.size(); i++)
			ExpressionASTVerify(el.get(i));
	}*/
	
	/*int ASTPreorderTraversal(ASTNode root, Object[] correctNodes, int rootIndex) {
		if(correctNodes[rootIndex].getClass() == Token.class) {
			System.out.println("Type mismatch at node " + rootIndex + " during preorder traversal.");
			System.out.println("See: " + root.getClass() + " Expected: token(\"" + ((Token)correctNodes[rootIndex]).getText() + "\")");
			assert false;
		}
		else if(root.getClass() != correctNodes[rootIndex]) {
			System.out.println("Type mismatch at node " + rootIndex + " during preorder traversal.");
			System.out.println("See: " + root.getClass() + " Expected: " + correctNodes[rootIndex]);
			assert false;
		}
		
		int nodesInSubtree = 1;
		
		if(root.getClass() == Program.class) {
			ArrayList<ParamDec> pl = ((Program)root).getParams();
			for(int i = 0; i < pl.size(); i++)
				nodesInSubtree += ASTPreorderTraversal(pl.get(i), correctNodes, rootIndex + nodesInSubtree);
			nodesInSubtree += ASTPreorderTraversal(((Program)root).getB(), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == ParamDec.class) {
			assertEquals(((ParamDec)root).getType(), correctNodes[rootIndex + (nodesInSubtree++)]);
			assertEquals(((ParamDec)root).getIdent(), correctNodes[rootIndex + (nodesInSubtree++)]);
		}
		else if(root.getClass() == Block.class) {
			ArrayList<Dec> dl = ((Block)root).getDecs();
			for(int i = 0; i < dl.size(); i++)
				nodesInSubtree += ASTPreorderTraversal(dl.get(i), correctNodes, rootIndex + nodesInSubtree);
			ArrayList<Statement> sl = ((Block)root).getStatements();
			for(int i = 0; i < sl.size(); i++)
				nodesInSubtree += ASTPreorderTraversal(sl.get(i), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == Dec.class) {
			assertEquals(((Dec)root).getType(), correctNodes[rootIndex + (nodesInSubtree++)]);
			assertEquals(((Dec)root).getIdent(), correctNodes[rootIndex + (nodesInSubtree++)]);
		}
		else if(root.getClass() == Statement.class) {
	
		}
		else if(root.getClass() == SleepStatement.class) {
			nodesInSubtree += ASTPreorderTraversal(((SleepStatement)root).getE(), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == AssignmentStatement.class) {
			nodesInSubtree += ASTPreorderTraversal(((AssignmentStatement)root).getVar(), correctNodes, rootIndex + nodesInSubtree);
			nodesInSubtree += ASTPreorderTraversal(((AssignmentStatement)root).getE(), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == Chain.class) {
			
		}
		else if(root.getClass() == ChainElem.class) {
			
		}
		else if(root.getClass() == IdentChain.class) {
			assertEquals(((IdentChain)root).getFirstToken(), correctNodes[rootIndex + (nodesInSubtree++)]);
		}
		else if(root.getClass() == FilterOpChain.class) {
			assertEquals(((FilterOpChain)root).getFirstToken(), correctNodes[rootIndex + (nodesInSubtree++)]);
			nodesInSubtree += ASTPreorderTraversal(((FilterOpChain)root).getArg(), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == FrameOpChain.class) {
			assertEquals(((FrameOpChain)root).getFirstToken(), correctNodes[rootIndex + (nodesInSubtree++)]);
			nodesInSubtree += ASTPreorderTraversal(((FrameOpChain)root).getArg(), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == ImageOpChain.class) {
			assertEquals(((ImageOpChain)root).getFirstToken(), correctNodes[rootIndex + (nodesInSubtree++)]);
			nodesInSubtree += ASTPreorderTraversal(((ImageOpChain)root).getArg(), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == BinaryChain.class) {
			
		}
		else if(root.getClass() == WhileStatement.class) {
			nodesInSubtree += ASTPreorderTraversal(((WhileStatement)root).getE(), correctNodes, rootIndex + nodesInSubtree);
			nodesInSubtree += ASTPreorderTraversal(((WhileStatement)root).getB(), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == IfStatement.class) {
			nodesInSubtree += ASTPreorderTraversal(((IfStatement)root).getE(), correctNodes, rootIndex + nodesInSubtree);
			nodesInSubtree += ASTPreorderTraversal(((IfStatement)root).getB(), correctNodes, rootIndex + nodesInSubtree);
		}
		else if(root.getClass() == Expression.class) {
			
		}
		else if(root.getClass() == IdentExpression.class) {
			assertEquals(((IdentExpression)root).getFirstToken(), correctNodes[rootIndex + (nodesInSubtree++)]);
		}
		else if(root.getClass() == IdentLValue.class) {
			assertEquals(((IdentLValue)root).getFirstToken(), correctNodes[rootIndex + (nodesInSubtree++)]);
		}
		else if(root.getClass() == IntLitExpression.class) {
			
		}
		else if(root.getClass() == BooleanLitExpression.class) {
			
		}
		else if(root.getClass() == ConstantExpression.class) {
			
		}
		else if(root.getClass() == BinaryExpression.class) {
			
		}
		else if(root.getClass() == Tuple.class) {
			
		}
		
		return nodesInSubtree;
	}*/
}
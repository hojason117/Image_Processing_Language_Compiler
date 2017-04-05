/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static cop5556sp17.AST.Type.TypeName.INTEGER;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.*;
//import cop5556sp17.AST.ASTNode;
//import cop5556sp17.AST.Dec;
//import cop5556sp17.AST.IdentExpression;
//import cop5556sp17.AST.Program;
//import cop5556sp17.AST.Statement;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

public class TypeCheckVisitorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	public Parser initTest(String input) throws Exception {
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		
		return parser;
	}
	
	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Parser parser =  initTest(input);
		//Scanner scanner = new Scanner(input);
		//scanner.scan();
		//Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testAssignmentBoolLitError0() throws Exception{
		String input = "p {\nboolean y \ny <- 3;}";
		Parser parser =  initTest(input);
		//Scanner scanner = new Scanner(input);
		//scanner.scan();
		//Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=2, posInLine=0], variable has type BOOLEAN, but expression has type INTEGER");
		program.visit(v, null);
	}

	@Test
	// ParamDec := type ident
	public void test_1() throws Exception{
		String input = "p boolean y, boolean y {}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=21], variable already exists in current scope.");
		program.visit(v, null);
	}
	
	@Test
	// Dec := type ident
	public void test_2() throws Exception{
		String input = "p {integer x integer x}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=21], variable already exists in current scope.");
		program.visit(v, null);
	}
	
	@Test
	// Dec := type ident
	public void test_3() throws Exception{
		String input = "p boolean y{integer x if(true){integer x}}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// Dec := type ident
	public void test_4() throws Exception{
		String input = "p boolean y{integer x if(true){integer x image a image a}}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=55], variable already exists in current scope.");
		program.visit(v, null);
	}
	
	@Test
	// SleepStatement := Expression
	public void test_5() throws Exception{
		String input = "p {sleep 1;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// SleepStatement := Expression
	public void test_6() throws Exception{
		String input = "p {sleep true;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=9], expression has type BOOLEAN, but expecting INTEGER");
		program.visit(v, null);
	}
	
	@Test
	// AssignmentStatement := IdentLValue Expression
	public void test_7() throws Exception{
		String input = "p {integer x x <- 3;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// AssignmentStatement := IdentLValue Expression
	public void test_8() throws Exception{
		String input = "p {integer x x <- false;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=13], variable has type INTEGER, but expression has type BOOLEAN");
		program.visit(v, null);
	}
	
	@Test
	// WhileStatement := Expression Block
	public void test_9() throws Exception{
		String input = "p {while(true){}}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// WhileStatement := Expression Block
	public void test_10() throws Exception{
		String input = "p {while(1){}}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=9], expression has type INTEGER, but expecting BOOLEAN");
		program.visit(v, null);
	}
	
	@Test
	// IfStatement := Expression Block
	public void test_11() throws Exception{
		String input = "p {if(false){}}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// IfStatement := Expression Block
	public void test_12() throws Exception{
		String input = "p {if(5){}}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=6], expression has type INTEGER, but expecting BOOLEAN");
		program.visit(v, null);
	}
	
	@Test
	// IdentChain := ident
	public void test_13() throws Exception{
		String input = "p url x{image y x -> y;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// IdentChain := ident
	public void test_14() throws Exception{
		String input = "p {image y x -> y;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=11], variable undeclared or not visible.");
		program.visit(v, null);
	}
	
	@Test
	// FilterOpChain := filterOp Tuple
	public void test_15() throws Exception{
		String input = "p url x{x -> gray;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// FilterOpChain := filterOp Tuple
	public void test_16() throws Exception{
		String input = "p url x{x -> gray(0);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=17], expected 0 args but see 1");
		program.visit(v, null);
	}
	
	@Test
	// FrameOpChain := frameOp Tuple
	public void test_17() throws Exception{
		String input = "p {frame x x -> move(1, 2);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// FrameOpChain := frameOp Tuple
	public void test_18() throws Exception{
		String input = "p {frame x x -> hide(0);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=20], expected 0 args but see 1");
		program.visit(v, null);		
	}
	
	@Test
	// FrameOpChain := frameOp Tuple
	public void test_19() throws Exception{
		String input = "p {frame x x -> xloc(0);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=20], expected 0 args but see 1");
		program.visit(v, null);		
	}
	
	@Test
	// FrameOpChain := frameOp Tuple
	public void test_20() throws Exception{
		String input = "p {frame x x -> move(0);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=20], expected 2 args but see 1");
		program.visit(v, null);		
	}
	
	@Test
	// ImageOpChain := imageOp Tuple
	public void test_21() throws Exception{
		String input = "p {image x x -> width;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// ImageOpChain := imageOp Tuple
	public void test_22() throws Exception{
		String input = "p {image x x -> width(5);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=21], expected 0 args but see 1");
		program.visit(v, null);		
	}
	
	@Test
	// ImageOpChain := imageOp Tuple
	public void test_23() throws Exception{
		String input = "p {image x x -> scale(1, 2);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=21], expected 1 args but see 2");
		program.visit(v, null);		
	}
	
	@Test
	// IdentExpression := ident
	public void test_24() throws Exception{
		String input = "p integer x{sleep x;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// IdentExpression := ident
	public void test_25() throws Exception{
		String input = "p {sleep x;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=9], variable undeclared or not visible.");
		program.visit(v, null);		
	}
	
	@Test
	// IdentLValue := ident
	public void test_26() throws Exception{
		String input = "p integer x{x <- 1;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// IdentLValue := ident
	public void test_27() throws Exception{
		String input = "p {x <- 1;}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=3], variable undeclared or not visible.");
		program.visit(v, null);		
	}
	
	@Test
	// BinaryExpression := Expression op Expression
	public void test_28() throws Exception{
		String input = "p {integer x integer y if(x != y){}}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// BinaryExpression := Expression op Expression
	public void test_29() throws Exception{
		String input = "p {integer x image y if(x != y){}}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=24], left expression has type INTEGER, but right expression has type IMAGE");
		program.visit(v, null);		
	}
	
	@Test
	// Tuple := List<Expression>
	public void test_31() throws Exception{
		String input = "p {image x x -> scale(5);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	
	@Test
	// Tuple := List<Expression>
	public void test_32() throws Exception{
		String input = "p {image x x -> scale(true);}";
		Parser parser =  initTest(input);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		thrown.expectMessage("Type error at LinePos [line=0, posInLine=22], expression has type BOOLEAN, but expecting INTEGER");
		program.visit(v, null);
	}
}
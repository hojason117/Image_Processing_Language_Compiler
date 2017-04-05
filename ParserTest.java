package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.util.concurrent.Callable;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ParserTest {
	
	/*public static enum NonTerminal {
		PROGRAM("program"), PARAM_DEC("param_dec"), BLOCK("block"), DEC("dec"),
		STATEMENT("statement"), ASSIGN("assign"), CHAIN("chain"), WHILESTATEMENT("whileStatement"),
		IFSTATEMENT("ifStatement"), ARROWOP("arrowOp"), CHAINELEM("chainElem"), FILTEROP("filterOp"),
		FRAMEOP("frameOp"), IMAGEOP("imageOp"), ARG("arg"), EXPRESSION("expression"), TERM("term"),
		ELEM("elem"), FACTOR("factor"), RELOP("relOp"), WEAKOP("weakOp"), STRONGOP("strongOp");
		
		final String text;
		
		NonTerminal(String text) {
			this.text = text;
		}

		String getText() {
			return text;
		}
	}*/

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.block();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.statement();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.assign();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.whileStatement();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.ifStatement();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chainElem();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}
	
	@Test
	// arg ::=  | ( expression ( , expression)* )
	public void test_35() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(9487940, bool * (abc))";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.expression();
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
		String input = "a + b * 1 - 3 / 8 % 0";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.term();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.elem();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.factor();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void test_52() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog1 {while(screenwidth == true) {\nif(screenheight == 10 != 20){\n/*This is a test."
				+ "\nHahaha*/\nimage a\nb <- (123 & abc) * screenwidth / 3 | screenheight;\n}}}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
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
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void test_57() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc{sleep 2147483648;}";
		thrown.expect(IllegalNumberException.class);
		thrown.expectMessage("Overflow integer 2147483648 at pos 10");
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	
	
	
	
	/*@Test
	public void test_() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String text = "";
		String errorMessage = "";
		testCase test_ = new testCase(text, null, null);
		test_.run(null);
	}*/
	
	/*class testCase {
		private String input;
		private String exceptionMessage;
		private Class<?> exceptionType;
		
		public testCase(String text, String message, Class<?> type) {
			this.setInput(text);
			this.setExceptionMessage(message);
			this.setExceptionType(type);
		}
		
		public String getInput() {
			return input;
		}

		public String  getExceptionMessage() {
			return exceptionMessage;
		}
		
		public Class<?> getExceptionType() {
			return exceptionType;
		}
		
		public void setInput(String text) {
			input = text;
		}
		
		public void setExceptionMessage(String message) {
			exceptionMessage = message;
		}
		
		public void setExceptionType(Class<?> type) {
			exceptionType = type;
		}
		
		public void run(NonTerminal NT) throws IllegalCharException, IllegalNumberException, SyntaxException {
			Scanner scanner = new Scanner(input);
			scanner.scan();
			Parser parser = new Parser(scanner);
	        
	        if(exceptionType != null) {
	        	if(exceptionType == Parser.SyntaxException.class)
	        		thrown.expect(Parser.SyntaxException.class);
	        	else if(exceptionType == Scanner.IllegalCharException.class)
	        		thrown.expect(Scanner.IllegalCharException.class);
	        	if(exceptionType == Scanner.IllegalNumberException.class)
	        		thrown.expect(Scanner.IllegalNumberException.class);
	        	else
	        		assert false;
	        }
	        
	        if(exceptionMessage != null)
	        	thrown.expectMessage(exceptionMessage);
	        
	        if(NT == null)
	        	parser.parse();
	        else {
		        switch(NT) {
		        	case PROGRAM:
		        		parser.program();
		        		break;
		        	case PARAM_DEC:
		        		parser.paramDec();
		        		break;
		        	case BLOCK:
		        		parser.block();
		        		break;
		        	case DEC:
		        		parser.dec();
		        		break;
		        	case STATEMENT:
		        		parser.statement();
		        		break;
		        	case ASSIGN:
		        		parser.assign();
		        		break;
		        	case CHAIN:
		        		parser.chain();
		        		break;
		        	case WHILESTATEMENT:
		        		parser.whileStatement();
		        		break;
		        	case IFSTATEMENT:
		        		parser.ifStatement();
		        		break;
		        	case ARROWOP:
		        		parser.arrowOp();
		        		break;
		        	case CHAINELEM:
		        		parser.chainElem();
		        		break;
		        	case FILTEROP:
		        		parser.filterOp();
		        		break;
		        	case FRAMEOP:
		        		parser.frameOp();
		        		break;
		        	case IMAGEOP:
		        		parser.imageOp();
		        		break;
		        	case ARG:
		        		parser.arg();
		        		break;
		        	case EXPRESSION:
		        		parser.expression();
		        		break;
		        	case TERM:
		        		parser.term();
		        		break;
		        	case ELEM:
		        		parser.elem();
		        		break;
		        	case FACTOR:
		        		parser.factor();
		        		break;
		        	case RELOP:
		        		parser.relOp();
		        		break;
		        	case WEAKOP:
		        		parser.weakOp();
		        		break;
		        	case STRONGOP:
		        		parser.strongOp();
		        		break;
		        		
		        	default:
		        		assert false;
		        }
	        }
		}
	}*/
}
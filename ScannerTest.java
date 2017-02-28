package cop5556sp17;

import static cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import static cop5556sp17.Scanner.LinePos;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

import java.util.ArrayList;

public class ScannerTest {
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	/*@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}*/
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	
	/*@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		thrown.expectMessage("Overflow integer 99999999999999999 at pos 0");
		scanner.scan();
	}*/

//TODO  more tests
	ArrayList<testCase> tests;
	
	static class testCase {
		private String input;
		private ArrayList<Object> tokens;
		private ArrayList<Kind> kinds;
		private int[] pos;
		private int[] length;
		private LinePos[] lineposes;
		private String exceptionMessage;
		private Class<?> exceptionType;
		private int tokenCount;
		
		public testCase(String text, Object[] ts, Kind[] ks, int[] p, int[] l, LinePos[] poses, int tCount) {
			this.setInput(text);
			tokens = new ArrayList<Object>();
			this.setTokens(ts);
			kinds = new ArrayList<Kind>();
			this.setKinds(ks);
			this.setPos(p);
			this.setLength(l);
			this.setLinePoses(poses);
			this.setTokenCount(tCount);
		}
		
		public String getInput() {
			return input;
		}

		public ArrayList<Object> getTokens() {
			return tokens;
		}
		
		public ArrayList<Kind> getKinds() {
			return kinds;
		}
		
		public int[] getPos() {
			return pos;
		}
		
		public int[] getLength() {
			return length;
		}
		
		public LinePos[] getLinePoses() {
			return lineposes;
		}
		
		public String  getExceptionMessage() {
			return exceptionMessage;
		}
		
		public Class<?> getExceptionType() {
			return exceptionType;
		}
		
		public int getTokenCount() {
			return tokenCount;
		}
		
		public void setInput(String text) {
			input = text;
		}
		
		public void setTokens(Object[] ts) {
			if(ts == null)
				tokens = null;
			else {
				for(int i = 0; i < ts.length; i++)
					tokens.add(ts[i]);
			}
		}
		
		public void setKinds(Kind[] ks) {
			if(ks == null)
				kinds = null;
			else {
				for(int i = 0; i < ks.length; i++)
					kinds.add(ks[i]);
			}
		}
		
		public void setPos(int[] p) {
			pos = p;
		}
		
		public void setLength(int[] l) {
			length = l;
		}
		
		public void setLinePoses(LinePos[] poses) {
			lineposes = poses;
		}
		
		public void setExceptionMessage(String message) {
			exceptionMessage = message;
		}
		
		public void setExceptionType(Class<?> type) {
			exceptionType = type;
		}
		
		public void setTokenCount(int count) {
			tokenCount = count;
		}
	}
	
	private void initTestCases() {
		tests = new ArrayList<testCase>();
		
		String test_0_text = "";
		Object[] test_0_tokens = {};
		Kind[] test_0_kinds = {EOF};
		int[] test_0_pos = {0};
		int[] test_0_length = {0};
		testCase test_0 = new testCase(test_0_text, test_0_tokens, test_0_kinds, test_0_pos, test_0_length, null, 1);
		tests.add(test_0);
		
		String test_1_text = "123\nabc";
		Object[] test_1_tokens = {123, "abc"};
		Kind[] test_1_kinds = {INT_LIT, IDENT, EOF};
		int[] test_1_pos = {0, 4, 7};
		int[] test_1_length = {3, 3, 0};
		testCase test_1 = new testCase(test_1_text, test_1_tokens, test_1_kinds, test_1_pos, test_1_length, null, 3);
		tests.add(test_1);
		
		String test_2_text = "123456789";
		Object[] test_2_tokens = {123456789};
		Kind[] test_2_kinds = {INT_LIT, EOF};
		int[] test_2_pos = {0, 9};
		int[] test_2_length = {9, 0};
		testCase test_2 = new testCase(test_2_text, test_2_tokens, test_2_kinds, test_2_pos, test_2_length, null, 2);
		tests.add(test_2);
		
		String test_3_text = "integer";
		Object[] test_3_tokens = {"integer"};
		Kind[] test_3_kinds = {KW_INTEGER, EOF};
		int[] test_3_pos = {0, 7};
		int[] test_3_length = {7, 0};
		testCase test_3 = new testCase(test_3_text, test_3_tokens, test_3_kinds, test_3_pos, test_3_length, null, 2);
		tests.add(test_3);
		
		String test_4_text = "integer boolean image url file frame";
		Object[] test_4_tokens = {"integer", "boolean", "image", "url", "file", "frame"};
		Kind[] test_4_kinds = {KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_URL, KW_FILE, KW_FRAME, EOF};
		int[] test_4_pos = {0, 8, 16, 22, 26, 31, 36};
		int[] test_4_length = {7, 7, 5, 3, 4, 5, 0};
		testCase test_4 = new testCase(test_4_text, test_4_tokens, test_4_kinds, test_4_pos, test_4_length, null, 7);
		tests.add(test_4);
		
		String test_5_text = "while if true false";
		Object[] test_5_tokens = {"while", "if", "true", "false"};
		Kind[] test_5_kinds = {KW_WHILE, KW_IF, KW_TRUE, KW_FALSE, EOF};
		int[] test_5_pos = {0, 6, 9, 14, 19};
		int[] test_5_length = {5, 2, 4, 5, 0};
		testCase test_5 = new testCase(test_5_text, test_5_tokens, test_5_kinds, test_5_pos, test_5_length, null, 5);
		tests.add(test_5);
		
		String test_6_text = "; , ( ) { }";
		Object[] test_6_tokens = {';', ',', '(', ')', '{', '}'};
		Kind[] test_6_kinds = {SEMI, COMMA, LPAREN, RPAREN, LBRACE, RBRACE, EOF};
		int[] test_6_pos = {0, 2, 4, 6, 8, 10, 11};
		int[] test_6_length = {1, 1, 1, 1, 1, 1, 0};
		testCase test_6 = new testCase(test_6_text, test_6_tokens, test_6_kinds, test_6_pos, test_6_length, null, 7);
		tests.add(test_6);
		
		String test_7_text = "-> |-> | & == !=";
		Object[] test_7_tokens = {"->", "|->", "|", "&", "==", "!="};
		Kind[] test_7_kinds = {ARROW, BARARROW, OR, AND, EQUAL, NOTEQUAL, EOF};
		int[] test_7_pos = {0, 3, 7, 9, 11, 14, 16};
		int[] test_7_length = {2, 3, 1, 1, 2, 2, 0};
		testCase test_7 = new testCase(test_7_text, test_7_tokens, test_7_kinds, test_7_pos, test_7_length, null, 7);
		tests.add(test_7);
		
		String test_8_text = "< > <= >= + - * / % ! <-";
		Object[] test_8_tokens = {"<", ">", "<=", ">=", "+", "-", "*", "/", "%", "!", "<-"};
		Kind[] test_8_kinds = {LT, GT, LE, GE, PLUS, MINUS, TIMES, DIV, MOD, NOT, ASSIGN, EOF};
		int[] test_8_pos = {0, 2, 4, 7, 10, 12, 14, 16, 18, 20, 22, 24};
		int[] test_8_length = {1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 2, 0};
		testCase test_8 = new testCase(test_8_text, test_8_tokens, test_8_kinds, test_8_pos, test_8_length, null, 12);
		tests.add(test_8);
		
		String test_9_text = "blur gray convolve screenheight screenwidth width height";
		Object[] test_9_tokens = {"blur", "gray", "convolve", "screenheight", "screenwidth", "width", "height"};
		Kind[] test_9_kinds = {OP_BLUR, OP_GRAY, OP_CONVOLVE, KW_SCREENHEIGHT, KW_SCREENWIDTH, OP_WIDTH, OP_HEIGHT, EOF};
		int[] test_9_pos = {0, 5, 10, 19, 32, 44, 50, 56};
		int[] test_9_length = {4, 4, 8, 12, 11, 5, 6, 0};
		testCase test_9 = new testCase(test_9_text, test_9_tokens, test_9_kinds, test_9_pos, test_9_length, null, 8);
		tests.add(test_9);
		
		String test_10_text = "xloc yloc hide show move sleep scale";
		Object[] test_10_tokens = {"xloc", "yloc", "hide", "show", "move", "sleep", "scale"};
		Kind[] test_10_kinds = {KW_XLOC, KW_YLOC, KW_HIDE, KW_SHOW, KW_MOVE, OP_SLEEP, KW_SCALE, EOF};
		int[] test_10_pos = {0, 5, 10, 15, 20, 25, 31, 36};
		int[] test_10_length = {4, 4, 4, 4, 4, 5, 5, 0};
		testCase test_10 = new testCase(test_10_text, test_10_tokens, test_10_kinds, test_10_pos, test_10_length, null, 8);
		tests.add(test_10);
		
		String test_11_text = ";;;";
		Object[] test_11_tokens = {";", ";", ";"};
		Kind[] test_11_kinds = {SEMI, SEMI, SEMI, EOF};
		int[] test_11_pos = {0, 1, 2, 3};
		int[] test_11_length = {1, 1, 1, 0};
		testCase test_11 = new testCase(test_11_text, test_11_tokens, test_11_kinds, test_11_pos, test_11_length, null, 4);
		tests.add(test_11);
		
		String test_12_text = "99999999999999999";
		testCase test_12 = new testCase(test_12_text, null, null, null, null, null, 0);
		test_12.setExceptionType(IllegalNumberException.class);
		test_12.setExceptionMessage("Overflow integer 99999999999999999 at pos 0");
		tests.add(test_12);
		
		String test_13_text = "!==";
		testCase test_13 = new testCase(test_13_text, null, null, null, null, null, 0);
		test_13.setExceptionType(IllegalCharException.class);
		test_13.setExceptionMessage("Illegal char = at pos 2");
		tests.add(test_13);
		
		String test_14_text = "/**/";
		Object[] test_14_tokens = {};
		Kind[] test_14_kinds = {EOF};
		int[] test_14_pos = {4};
		int[] test_14_length = {0};
		testCase test_14 = new testCase(test_14_text, test_14_tokens, test_14_kinds, test_14_pos, test_14_length, null, 1);
		tests.add(test_14);
		
		String test_15_text = "/* != 456 abc if while */";
		Object[] test_15_tokens = {};
		Kind[] test_15_kinds = {EOF};
		int[] test_15_pos = {25};
		int[] test_15_length = {0};
		testCase test_15 = new testCase(test_15_text, test_15_tokens, test_15_kinds, test_15_pos, test_15_length, null, 1);
		tests.add(test_15);
		
		String test_16_text = "/* != 456 abc if while ";
		testCase test_16 = new testCase(test_16_text, null, null, null, null, null, 0);
		test_16.setExceptionType(IllegalCharException.class);
		test_16.setExceptionMessage("Unclosed comment at pos 0");
		tests.add(test_16);
		
		String test_17_text = " != <-456abc if |-> ";
		Object[] test_17_tokens = {"!=", "<-", 456, "abc", "if", "|->"};
		Kind[] test_17_kinds = {NOTEQUAL, ASSIGN, INT_LIT, IDENT, KW_IF, BARARROW, EOF};
		int[] test_17_pos = {1, 4, 6, 9, 13, 16, 20};
		int[] test_17_length = {2, 2, 3, 3, 2, 3, 0};
		testCase test_17 = new testCase(test_17_text, test_17_tokens, test_17_kinds, test_17_pos, test_17_length, null, 7);
		tests.add(test_17);
		
		String test_18_text = " != <-456abc if |-> */";
		Object[] test_18_tokens = {"!=", "<-", 456, "abc", "if", "|->", '*', '/'};
		Kind[] test_18_kinds = {NOTEQUAL, ASSIGN, INT_LIT, IDENT, KW_IF, BARARROW, TIMES, DIV, EOF};
		int[] test_18_pos = {1, 4, 6, 9, 13, 16, 20, 21, 22};
		int[] test_18_length = {2, 2, 3, 3, 2, 3, 1, 1, 0};
		testCase test_18 = new testCase(test_18_text, test_18_tokens, test_18_kinds, test_18_pos, test_18_length, null, 9);
		tests.add(test_18);
		
		String test_19_text = "||-|||->";
		Object[] test_19_tokens = {'|', '|', '-', '|', '|', "|->"};
		Kind[] test_19_kinds = {OR, OR, MINUS, OR, OR, BARARROW, EOF};
		int[] test_19_pos = {0, 1, 2, 3, 4, 5, 8};
		int[] test_19_length = {1, 1, 1, 1, 1, 3, 0};
		testCase test_19 = new testCase(test_19_text, test_19_tokens, test_19_kinds, test_19_pos, test_19_length, null, 7);
		tests.add(test_19);
		
		String test_20_text = "123/*abc*/456";
		Object[] test_20_tokens = {123, 456};
		Kind[] test_20_kinds = {INT_LIT, INT_LIT, EOF};
		testCase test_20 = new testCase(test_20_text, test_20_tokens, test_20_kinds, null, null, null, 0);
		tests.add(test_20);
		
		String test_21_text = "abc\nwidth \n frame\n";
		Object[] test_21_tokens = {"abc", "width", "frame"};
		Kind[] test_21_kinds = {IDENT, OP_WIDTH, KW_FRAME, EOF};
		int[] test_21_pos = {0, 4, 12, 18};
		int[] test_21_length = {3, 5, 5, 0};
		LinePos test_21_linePos[] = {new LinePos(0, 0), new LinePos(1, 0), new LinePos(2, 1)};
		testCase test_21 = new testCase(test_21_text, test_21_tokens, test_21_kinds, test_21_pos, test_21_length, test_21_linePos, 4);
		tests.add(test_21);
		
		String test_22_text = "while\n  \n\nboolean\t \n sleepframe screenwidth\n";
		Kind[] test_22_kinds = {KW_WHILE, KW_BOOLEAN, IDENT, KW_SCREENWIDTH, EOF};
		LinePos test_22_linePos[] = {new LinePos(0, 0), new LinePos(3, 0), new LinePos(4, 1), new LinePos(4, 12)};
		testCase test_22 = new testCase(test_22_text, null, test_22_kinds, null, null, test_22_linePos, 5);
		tests.add(test_22);
		
		String test_23_text = "while\n/*\n\nboolean\nsleepframe*/screenwidth";
		Kind[] test_23_kinds = {KW_WHILE, KW_SCREENWIDTH, EOF};
		LinePos test_23_linePos[] = {new LinePos(0, 0), new LinePos(4, 12)};
		testCase test_23 = new testCase(test_23_text, null, test_23_kinds, null, null, test_23_linePos, 3);
		tests.add(test_23);		
		
		String test_24_text = "while(file == true) {\nif(screenheight == 10 & yloc != 20)\n/*This is a test.\nHahaha*/\nurl <- xloc * width / 3 % height;\n}";
		Object[] test_24_tokens = {"while", '(', "file", "==", "true", ')', '{', "if", '(', "screenheight",  "==", 10, '&', "yloc", "!=", 20, ')',
								   "url", "<-", "xloc", '*', "width", '/', 3, '%', "height", ';', '}'};
		Kind[] test_24_kinds = {KW_WHILE, LPAREN, KW_FILE, EQUAL, KW_TRUE, RPAREN, LBRACE, KW_IF, LPAREN, KW_SCREENHEIGHT, EQUAL, INT_LIT, AND,	KW_YLOC,
								NOTEQUAL, INT_LIT, RPAREN, KW_URL, ASSIGN, KW_XLOC, TIMES, OP_WIDTH, DIV, INT_LIT, MOD, OP_HEIGHT, SEMI, RBRACE, EOF};
		int[] test_24_pos = {0, 5, 6, 11, 14, 18, 20, 22, 24, 25, 38, 41, 44, 46, 51, 54, 56, 85, 89, 92, 97, 99, 105, 107, 109, 111, 117, 119, 120};
		int[] test_24_length = {5, 1, 4, 2, 4, 1, 1, 2, 1, 12, 2, 2, 1, 4, 2, 2, 1, 3, 2, 4, 1, 5, 1, 1, 1, 6, 1, 1, 0};
		LinePos test_24_linePos[] = {new LinePos(0, 0), new LinePos(0, 5), new LinePos(0, 6), new LinePos(0, 11), new LinePos(0, 14), new LinePos(0, 18),
									 new LinePos(0, 20), new LinePos(1, 0), new LinePos(1, 2), new LinePos(1, 3), new LinePos(1, 16), new LinePos(1, 19),
									 new LinePos(1, 22), new LinePos(1, 24), new LinePos(1, 29), new LinePos(1, 32), new LinePos(1, 34), new LinePos(4, 0),
									 new LinePos(4, 4), new LinePos(4, 7), new LinePos(4, 12), new LinePos(4, 14), new LinePos(4, 20), new LinePos(4, 22),
									 new LinePos(4, 24), new LinePos(4, 26), new LinePos(4, 32), new LinePos(5, 0)};
		testCase test_24 = new testCase(test_24_text, test_24_tokens, test_24_kinds, test_24_pos, test_24_length, test_24_linePos, 29);
		tests.add(test_24);
		
		String test_25_text = "000123";
		Object[] test_25_tokens = {0, 0, 0, 123};
		Kind[] test_25_kinds = {INT_LIT, INT_LIT, INT_LIT, INT_LIT, EOF};
		int[] test_25_pos = {0, 1, 2, 3, 6};
		int[] test_25_length = {1, 1, 1, 3, 0};
		testCase test_25 = new testCase(test_25_text, test_25_tokens, test_25_kinds, test_25_pos, test_25_length, null, 5);
		tests.add(test_25);
		
		String test_26_text = "foobar^";
		testCase test_26 = new testCase(test_26_text, null, null, null, null, null, 0);
		test_26.setExceptionType(IllegalCharException.class);
		test_26.setExceptionMessage("Illegal char ^ at pos 6");
		tests.add(test_26);
		
		String test_27_text = "Hello \\ World.";
		testCase test_27 = new testCase(test_27_text, null, null, null, null, null, 0);
		test_27.setExceptionType(IllegalCharException.class);
		test_27.setExceptionMessage("Illegal char \\ at pos 6");
		tests.add(test_27);
		
		String test_28_text = "\n43486743864130406846846438643640124135468749489000";
		testCase test_28 = new testCase(test_28_text, null, null, null, null, null, 0);
		test_28.setExceptionType(IllegalNumberException.class);
		test_28.setExceptionMessage("Overflow integer 43486743864130406846846438643640124135468749489000 at pos 1");
		tests.add(test_28);
		
		String test_29_text = "===";
		testCase test_29 = new testCase(test_29_text, null, null, null, null, null, 0);
		test_29.setExceptionType(IllegalCharException.class);
		test_29.setExceptionMessage("Illegal char = at pos 2");
		tests.add(test_29);
		
		String test_30_text = "abc\r\neof\t123";
		Object[] test_30_tokens = {"abc", "eof", 123};
		Kind[] test_30_kinds = {IDENT, IDENT, INT_LIT, EOF};
		int[] test_30_pos = {0, 5, 9, 12};
		int[] test_30_length = {3, 3, 3, 0};
		LinePos test_30_linePos[] = {new LinePos(0, 0), new LinePos(1, 0)};
		testCase test_30 = new testCase(test_30_text, test_30_tokens, test_30_kinds, test_30_pos, test_30_length, test_30_linePos, 3);
		tests.add(test_30);
		
		String test_31_text = "-2147483647";
		Object[] test_31_tokens = {'-', 2147483647, "eof"};
		Kind[] test_31_kinds = {MINUS, INT_LIT, EOF};
		testCase test_31 = new testCase(test_31_text, test_31_tokens, test_31_kinds, null, null, null, 0);
		tests.add(test_31);
		
		String test_32_text = "2147483648";
		testCase test_32 = new testCase(test_32_text, null, null, null, null, null, 0);
		test_32.setExceptionType(IllegalNumberException.class);
		test_32.setExceptionMessage("Overflow integer 2147483648 at pos 0");
		tests.add(test_32);
		
		
		
		/*String test__text = "";
		Object[] test__tokens = {};
		Kind[] test__kinds = {};
		int[] test__pos = {};
		int[] test__length = {};
		LinePos test__linePos[] = {new LinePos(, )};
		testCase test_ = new testCase(test__text, test__tokens, test__kinds, test__pos, test__length, test__linePos, );
		test_.setExceptionType();
		test_.setExceptionMessage("");
		tests.add(test_);*/
	}
	
	@Test
	public void automaticTest() throws IllegalCharException, IllegalNumberException{
		initTestCases();
		
		for(int i = 0; i < tests.size(); i++) {
			String text = tests.get(i).getInput();
			ArrayList<Object> ts = tests.get(i).getTokens();
			ArrayList<Kind> ks = tests.get(i).getKinds();
			int[] p = tests.get(i).getPos();
			int[] l = tests.get(i).getLength();
			LinePos[] poses = tests.get(i).getLinePoses();
			Scanner scanner = new Scanner(text);
			
			System.out.println("Test " + i + ": " + text);
			
			try {
				scanner.scan();
				
				if(tests.get(i).getExceptionType() == IllegalCharException.class) {
					thrown.expect(IllegalCharException.class);
					assert false;
				}
				else if(tests.get(i).getExceptionType() == IllegalNumberException.class) {
					thrown.expect(IllegalNumberException.class);
					assert false;
				}
			}
			catch(IllegalCharException charException) {
				if(tests.get(i).getExceptionType() != null) {
					assertEquals(IllegalCharException.class, tests.get(i).getExceptionType());
					assertEquals(charException.getMessage(), tests.get(i).getExceptionMessage());
					System.out.print("Exception as expected. ");
				}
				else {
					System.out.println(charException.getMessage());
					assert false;
				}
			}
			catch(IllegalNumberException numException) {
				if(tests.get(i).getExceptionType() != null) {
					assertEquals(IllegalNumberException.class, tests.get(i).getExceptionType());
					assertEquals(numException.getMessage(), tests.get(i).getExceptionMessage());
					System.out.print("Exception as expected. ");
				}
				else {
					System.out.println(numException.getMessage());
					assert false;
				}
			}
			
			for(int j = 0; j < tests.get(i).getTokenCount(); j++) {
				Scanner.Token token = scanner.nextToken();
				
				if(ks != null) {
					assertEquals(ks.get(j), token.kind);
					if(ts != null && j < tests.get(i).getTokenCount() - 1) {
						if(ks.get(j) == INT_LIT)
							assertEquals(ts.get(j), token.intVal());
						else if(ks.get(j) == IDENT)
							assertEquals(ts.get(j), token.getText());
						else
							assertEquals(ks.get(j).getText(), token.getText());
					}
				}
				
				if(p != null && j < tests.get(i).getTokenCount())
					assertEquals(p[j], token.pos);
				
				if(l != null && j < tests.get(i).getTokenCount())
					assertEquals(l[j], token.length);
				
				if(poses != null && j < tests.get(i).getTokenCount() - 1) {
					assertEquals(poses[j].toString(), token.getLinePos().toString());
					assertEquals(poses[j].toString(), scanner.getLinePos(token).toString());
				}
			}
			
			System.out.println("Pass");
		}	
	}
}
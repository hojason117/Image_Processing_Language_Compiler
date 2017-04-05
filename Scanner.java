package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;

public class Scanner {
	
	// Kind enum
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		final String text;
		
		Kind(String text) {
			this.text = text;
		}

		String getText() {
			return text;
		}
	}
	
	// State enum
	public static enum State {
		START, DIGITS, IDENT, GOT_EQUAL, GOT_EXCLAMATION, GOT_BAR, GOT_LESSTHAN,
		GOT_GREATERTHAN, GOT_MINUS, GOT_DIVIDE, GOT_BAR_MINUS, COMMENT;
	}
	
	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;
	HashMap reserved;
	
	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		reserved = new HashMap();
		
		reserved.put("integer", Kind.KW_INTEGER);
		reserved.put("boolean", Kind.KW_BOOLEAN);
		reserved.put("image", Kind.KW_IMAGE);
		reserved.put("url", Kind.KW_URL);
		reserved.put("file", Kind.KW_FILE);
		reserved.put("frame", Kind.KW_FRAME);
		reserved.put("while", Kind.KW_WHILE);
		reserved.put("if", Kind.KW_IF);
		reserved.put("sleep", Kind.OP_SLEEP);
		reserved.put("screenheight", Kind.KW_SCREENHEIGHT);
		reserved.put("screenwidth", Kind.KW_SCREENWIDTH);
		reserved.put("gray", Kind.OP_GRAY);
		reserved.put("convolve", Kind.OP_CONVOLVE);
		reserved.put("blur", Kind.OP_BLUR);
		reserved.put("scale", Kind.KW_SCALE);
		reserved.put("width", Kind.OP_WIDTH);
		reserved.put("height", Kind.OP_HEIGHT);
		reserved.put("xloc", Kind.KW_XLOC);
		reserved.put("yloc", Kind.KW_YLOC);
		reserved.put("hide", Kind.KW_HIDE);
		reserved.put("show", Kind.KW_SHOW);
		reserved.put("move", Kind.KW_MOVE);
		reserved.put("true", Kind.KW_TRUE);
		reserved.put("false", Kind.KW_FALSE);
	}
	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0;
		
		//TODO IMPLEMENT THIS!!!!
		State state = State.START;
		int startPos = 0;
		int ch;
		
		while(pos <= chars.length()) {
			ch = (pos < chars.length()) ? chars.charAt(pos) : -1;
			
			switch(state) {
				case START: {
					pos = skipWhiteSpace(pos);
					ch = (pos < chars.length()) ? chars.charAt(pos) : -1;
					startPos = pos;
					switch(ch) {
						case -1:
							tokens.add(new Token(Kind.EOF, pos, 0)); break;
						case ';':
							tokens.add(new Token(Kind.SEMI, pos, 1)); break;
						case ',':
							tokens.add(new Token(Kind.COMMA, pos, 1)); break;
						case '(':
							tokens.add(new Token(Kind.LPAREN, pos, 1)); break;
						case ')':
							tokens.add(new Token(Kind.RPAREN, pos, 1)); break;
						case '{':
							tokens.add(new Token(Kind.LBRACE, pos, 1)); break;
						case '}':
							tokens.add(new Token(Kind.RBRACE, pos, 1)); break;
						case '&':
							tokens.add(new Token(Kind.AND, pos, 1)); break;
						case '+':
							tokens.add(new Token(Kind.PLUS, pos, 1)); break;
						case '*':
							tokens.add(new Token(Kind.TIMES, pos, 1)); break;
						case '%':
							tokens.add(new Token(Kind.MOD, pos, 1)); break;
						case '0':
							tokens.add(new Token(Kind.INT_LIT, pos, 1)); break;
						case '=':
							state = State.GOT_EQUAL; break;
						case '|':
							state = State.GOT_BAR; break;
						case '!':
							state = State.GOT_EXCLAMATION; break;
						case '<':
							state = State.GOT_LESSTHAN; break;
						case '>':
							state = State.GOT_GREATERTHAN; break;
						case '-':
							state = State.GOT_MINUS; break;
						case '/':
							state = State.GOT_DIVIDE; break;
							
						default:
							if(Character.isDigit(ch))
								state = State.DIGITS;
							else if(Character.isJavaIdentifierStart(ch))
								state = State.IDENT;
							else
								throw new IllegalCharException("Illegal char " + (char)ch + " at pos " + pos);
							break;
					}
					pos++;
				} break;
				case DIGITS:
					if(Character.isDigit(ch))
						pos++;
					else {
						try {
							Integer.parseInt(chars.substring(startPos, pos));
						}
						catch(NumberFormatException numException) {
							throw new IllegalNumberException("Overflow integer " + chars.substring(startPos, pos) + " at pos " + startPos);
						}
						
						tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
						
						state = State.START;
					}
					break;
				case IDENT:
					if(Character.isJavaIdentifierPart(ch))
						pos++;
					else {
						Kind temp = (Kind)reserved.get(chars.substring(startPos, pos));
						if(temp != null)
						tokens.add(new Token(temp, startPos, pos - startPos));
						else
							tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
						
						state = State.START;
					}
					break;
				case GOT_EQUAL:
					if(ch == '=') {
						tokens.add(new Token(Kind.EQUAL, startPos, 2));
						pos++;
					}
					else
						throw new IllegalCharException("Illegal char " + chars.charAt(startPos) + " at pos " + startPos);
					
					state = State.START;
					break;
				case GOT_EXCLAMATION:
					if(ch == '=') {
						tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
						pos++;
					}
					else
						tokens.add(new Token(Kind.NOT, startPos, 1));
					
					state = State.START;
					break;
				case GOT_BAR:
					if(ch == '-') {
						state = State.GOT_BAR_MINUS;
						pos++;
					}
					else {
						tokens.add(new Token(Kind.OR, startPos, 1));
						state = State.START;
					}
					break;
				case GOT_LESSTHAN:
					if(ch == '=') {
						tokens.add(new Token(Kind.LE, startPos, 2));
						pos++;
					}
					else if(ch == '-') {
						tokens.add(new Token(Kind.ASSIGN, startPos, 2));
						pos++;
					}
					else
						tokens.add(new Token(Kind.LT, startPos, 1));
					
					state = State.START;
					break;
				case GOT_GREATERTHAN:
					if(ch == '=') {
						tokens.add(new Token(Kind.GE, startPos, 2));
						pos++;
					}
					else
						tokens.add(new Token(Kind.GT, startPos, 1));
					
					state = State.START;
					break;
				case GOT_MINUS:
					if(ch == '>') {
						tokens.add(new Token(Kind.ARROW, startPos, 2));
						pos++;
					}
					else
						tokens.add(new Token(Kind.MINUS, startPos, 1));
					
					state = State.START;
					break;
				case GOT_DIVIDE:
					if(ch == '*') {
						state = State.COMMENT;
						pos++;
					}
					else {
						tokens.add(new Token(Kind.DIV, startPos, 1));
						state = State.START;
					}
					break;
				case GOT_BAR_MINUS:
					if(ch == '>') {
						tokens.add(new Token(Kind.BARARROW, startPos, 3));
						pos++;
					}
					else {
						tokens.add(new Token(Kind.OR, startPos, 1));
						tokens.add(new Token(Kind.MINUS, startPos+1, 1));
					}
					
					state = State.START;
					break;
				case COMMENT:
					if(ch == '*') {
						if(pos+1 < chars.length()) {
							if(chars.charAt(pos + 1) == '/') {
								pos += 2;
								state = State.START;
							}
							else
								pos++;
						}
						else
							throw new IllegalCharException("Unclosed comment at pos " + startPos);
					}
					else if(ch == -1)
						throw new IllegalCharException("Unclosed comment at pos " + startPos);
					else
						pos++;
					
					break;
					
				default:
					assert false;
					break;
			}
		}
		
		return this;  
	}
	
	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  
		
		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		// returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			return chars.substring(pos, pos + length);
		}
		
		// returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			int newlineCount = 0;
			int last_newline = -1;
			
			for(int i = 0; i < pos; i++) {
				if(chars.charAt(i) == '\n') {
					newlineCount++;
					last_newline = i;
				}
			}
			
			LinePos linePos = new LinePos(newlineCount, pos - last_newline - 1);
			return linePos;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Token)) {
				return false;
			}
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (kind != other.kind) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			return true;
		}
 
		Scanner getOuterType() {
			return Scanner.this;
		}

		/**
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			return Integer.parseInt(chars.substring(pos, pos + length));
		}
		
		public boolean isKind(Kind k) {
			return (this.kind == k) ? true : false;
		}
		
		public boolean isKind(Kind... ks) {
			for(int i = 0; i < ks.length; i++) {
				if (this.kind == ks[i])
					return true;
			}
			
			return false;
		}
	}
	
	/**
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/**
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}
	
	// Holds the line and position in the line of a token.
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		int newlineCount = 0;
		int last_newline = -1;
		
		for(int i = 0; i < t.pos; i++) {
			if(chars.charAt(i) == '\n') {
				newlineCount++;
				last_newline = i;
			}
		}
		
		LinePos linePos = new LinePos(newlineCount, t.pos - last_newline - 1);
		return linePos;
	}
	
	// Thrown by Scanner when an illegal character is encountered
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	 // Thrown by Scanner when an int literal is not a value that can be represented by an int.
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
		public IllegalNumberException(String message){
			super(message);
		}
	}
	
	private int skipWhiteSpace(int pos) {
		while(pos < chars.length() && Character.isWhitespace(chars.charAt(pos)))
			pos++;
		
		return pos;
	}
}

package ADT;

/*
The following code is provided by the instructor for the completion of PHASE 2 
of the compiler project for CS4100/5100.

FALL 2022 version

STUDENTS ARE TO PROVIDE THE FOLLOWING CODE FOR THE COMPLETION OF THE ASSIGNMENT:

1) Initialize the 2 reserve tables, which are fields in the Lexical class,
   named reserveWords and mnemonics.  Create the following functions.
   These calls are in the lexical constructor:
       initReserveWords(reserveWords);
       initMnemonics(mnemonics);
   One-line examples are provided below

2) getIdentifier, getNumber, getString, and getOtherToken. getOtherToken recognizes
  one- and two-character tokesn in the language. 



PROVIDED UTILITY FUNCTIONS THAT STUDENT MAY NEED TO CALL-
1) YOU MUST NOT USE MAGIC NUMBERS, that is, numeric constants anywhere in the code,
  like "if tokencode == 50".  Instead, use the following:
//To get an integer for a given mnemonic, use
public int codeFor(String mnemonic) {
       return mnemonics.LookupName(mnemonic);
   }
//To get the full reserve word for a given mnemonic, use:
   public String reserveFor(String mnemonic) {
       return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
   }

Other methods:
   private void consoleShowError(String message)
   private boolean isLetter(char ch)
   private boolean isDigit(char ch)
   private boolean isStringStart(char ch)
   private boolean isWhitespace(char ch)
   public char GetNextChar()
To check numeric formats of strings to see if they are valid, use:

   public boolean doubleOK(String stin) 
   public boolean integerOK(String stin)


CALLING OTHER FUNCTIONS LIKE getNextLine COULD BREAK THE EXISTING CODE!

 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author abrouill, Mark Fish
 */
import java.io.*;

public class Lexical {

	//Student generated constant identifiers for code quality
	private static final int IDENT_ID = 50;
	private static final int INTGR_ID = 51;
	private static final int FLOAT_ID = 52;
	private static final int STRNG_ID = 53;
	private static final int MAXIDENT = 20;
	private static final int MAXINT = 6;
	private static final int MAXFLT = 12;
	private static final String UNDEFINED = "UNDEFINED";

	private File file;                        //File to be read for input
	private FileReader filereader;            //Reader, Java reqd
	private BufferedReader bufferedreader;    //Buffered, Java reqd
	private String line;                      //Current line of input from file   
	private int linePos;                      //Current character position
	//  in the current line
	private SymbolTable saveSymbols;          //SymbolTable used in Lexical
	//  sent as parameter to construct
	private boolean EOF;                      //End Of File indicator
	private boolean echo;                     //true means echo each input line
	private boolean printToken;               //true to print found tokens here
	private int lineCount;                    //line #in file, for echo-ing
	private boolean needLine;                 //track when to read a new line

	//Tables to hold the reserve words and the mnemonics for token codes
	private final int sizeReserveTable = 50;
	private ReserveTable reserveWords = new ReserveTable(sizeReserveTable); //a few more than # reserves
	private ReserveTable mnemonics = new ReserveTable(sizeReserveTable); //a few more than # reserves

	//constructor
	public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
		saveSymbols = symbols;  //map the initialized parameter to the local ST
		echo = echoOn;          //store echo status
		lineCount = 0;          //start the line number count
		line = "";              //line starts empty
		needLine = true;        //need to read a line
		printToken = false;     //default OFF, do not print tokesn here
		//  within GetNextToken; call setPrintToken to
		//  change it publicly.
		linePos = -1;           //no chars read yet
		//call initializations of tables
		initReserveWords(reserveWords);
		initMnemonics(mnemonics);

		//set up the file access, get first character, line retrieved 1st time
		try {
			file = new File(filename);    //creates a new file instance  
			filereader = new FileReader(file);   //reads the file  
			bufferedreader = new BufferedReader(filereader);  //creates a buffering character input stream  
			EOF = false;
			currCh = GetNextChar();
		} catch (IOException e) {
			EOF = true;
			e.printStackTrace();
		}
	}

	// inner class "token" is declared here, no accessors needed
	public class token {

		public String lexeme;
		public int code;
		public String mnemonic;

		token() {
			lexeme = "";
			code = 0;
			mnemonic = "";
		}
	}

//	//This is the DISCARDABLE dummy method for getting and returning single characters
//	//STUDENT TURN-IN SHOULD NOT USE THIS!    
//	private token dummyGet() {
//		token result = new token();
//		result.lexeme = "" + currCh; //have the first char
//		currCh = GetNextChar();
//		result.code = 0;
//		result.mnemonic = "DUMY";
//		return result;
//
//	}

	//******************* PUBLIC USEFUL METHODS
	//These are nice for syntax to call later 
	//given a mnemonic, find its token code value
	public int codeFor(String mnemonic) {
		return mnemonics.LookupName(mnemonic);
	}
	
	//given a mnemonic, return its reserve word
	public String reserveFor(String mnemonic) {
		return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
	}

	// Public access to the current End Of File status
	public boolean EOF() {
		return EOF;
	}
	
	//DEBUG enabler, turns on/OFF token printing inside of GetNextToken
	public void setPrintToken(boolean on) {
		printToken = on;
	}

	/**
	 * Initializer for reserved words in language
	 * Generates all reserved words in reserve table given language specifications
	 * @param reserveWords structure created in constructor
	 */
	private void initReserveWords(ReserveTable reserveWords) {
		//Reserved name identifiers
		reserveWords.Add("GOTO", 0);
		reserveWords.Add("INTEGER", 1);
		reserveWords.Add("TO", 2);
		reserveWords.Add("DO", 3);
		reserveWords.Add("IF", 4);
		reserveWords.Add("THEN", 5);
		reserveWords.Add("ELSE", 6);
		reserveWords.Add("FOR", 7);
		reserveWords.Add("OF", 8);
		reserveWords.Add("WRITELN", 9);
		reserveWords.Add("READLN", 10);
		reserveWords.Add("BEGIN", 11);
		reserveWords.Add("END", 12);
		reserveWords.Add("VAR", 13);
		reserveWords.Add("DOWHILE", 14);
		reserveWords.Add("UNIT", 15);
		reserveWords.Add("LABEL", 16);
		reserveWords.Add("REPEAT", 17);
		reserveWords.Add("UNTIL", 18);
		reserveWords.Add("PROCEDURE", 19);
		reserveWords.Add("DOWNTO", 20);
		reserveWords.Add("FUNCTION", 21);
		reserveWords.Add("RETURN", 22);
		reserveWords.Add("FLOAT", 23);
		reserveWords.Add("STRING", 24);
		reserveWords.Add("ARRAY", 25);

		//Reserved operation characters
		reserveWords.Add("/", 30);
		reserveWords.Add("*", 31);
		reserveWords.Add("+", 32);
		reserveWords.Add("-", 33);
		reserveWords.Add("(", 34);
		reserveWords.Add(")", 35);
		reserveWords.Add(";", 36);
		reserveWords.Add(":=", 37);
		reserveWords.Add(">", 38);
		reserveWords.Add("<", 39);
		reserveWords.Add(">=", 40);
		reserveWords.Add("<=", 41);
		reserveWords.Add("=", 42);
		reserveWords.Add("<>", 43);
		reserveWords.Add(",", 44);
		reserveWords.Add("[", 45);
		reserveWords.Add("]", 46);
		reserveWords.Add(":", 47);
		reserveWords.Add(".", 48);

		//reserve undefined character(s)
		reserveWords.Add(UNDEFINED, 99);

	}

	/**
	 * Initializer for mnemonic codes
	 * Generates reserve table structure for each mnemonic
	 * Mnemonic names generated by student
	 * @param mnemonics reserve table created in constructor
	 */
	private void initMnemonics(ReserveTable mnemonics) {

		//mnemonics for reserved name identifiers
		mnemonics.Add("GOTO_", 0);
		mnemonics.Add("INTGR", 1);
		mnemonics.Add("TO___", 2);
		mnemonics.Add("DO___", 3);
		mnemonics.Add("IF___", 4);
		mnemonics.Add("THEN_", 5);
		mnemonics.Add("ELSE_", 6);
		mnemonics.Add("FOR__", 7);
		mnemonics.Add("OF___", 8);
		mnemonics.Add("WRTLN", 9);
		mnemonics.Add("RDLN_", 10);
		mnemonics.Add("BEGIN", 11);
		mnemonics.Add("END__", 12);
		mnemonics.Add("VAR__", 13);
		mnemonics.Add("DWHLE", 14);
		mnemonics.Add("UNIT_", 15);
		mnemonics.Add("LABEL", 16);
		mnemonics.Add("RPEAT", 17);
		mnemonics.Add("UNTIL", 18);
		mnemonics.Add("PRCDR", 19);
		mnemonics.Add("DWNTO", 20);
		mnemonics.Add("FNCTN", 21);
		mnemonics.Add("RETRN", 22);
		mnemonics.Add("FLOAT", 23);
		mnemonics.Add("STRNG", 24);
		mnemonics.Add("ARRAY", 25);

		//mnemonics for operations
		mnemonics.Add("DIVID", 30);
		mnemonics.Add("MULTI", 31);
		mnemonics.Add("ADD__", 32);
		mnemonics.Add("SUBTR", 33);
		mnemonics.Add("LPRNT", 34);
		mnemonics.Add("RPRNT", 35);
		mnemonics.Add("SEMIC", 36);
		mnemonics.Add("ASSGN", 37);
		mnemonics.Add("GRTHN", 38);
		mnemonics.Add("LSTHN", 39);
		mnemonics.Add("GRTEQ", 40);
		mnemonics.Add("LSTEQ", 41);
		mnemonics.Add("EQUAL", 42);
		mnemonics.Add("NTEQL", 43);
		mnemonics.Add("COMMA", 44);
		mnemonics.Add("LFBRC", 45);
		mnemonics.Add("RTBRC", 46);
		mnemonics.Add("COLON", 47);
		mnemonics.Add("PRD__", 48);

		//mnemonics for token types
		//Identifier, integer, float, string constants
		mnemonics.Add("IDENT", IDENT_ID);
		mnemonics.Add("ICNST", INTGR_ID);
		mnemonics.Add("FCNST", FLOAT_ID);
		mnemonics.Add("SCNST", STRNG_ID);

		//mnemonic for undefined
		mnemonics.Add("UNDEF", 99);
	}


	//********************** UTILITY FUNCTIONS
	private void consoleShowError(String message) {
		System.out.println("**** ERROR FOUND: " + message);
	}

	// Character category for alphabetic chars
	private boolean isLetter(char ch) {
		return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
	}

	// Character category for 0..9 
	private boolean isDigit(char ch) {
		return ((ch >= '0') && (ch <= '9'));
	}

	// Category for any whitespace to be skipped over
	private boolean isWhitespace(char ch) {
		// SPACE, TAB, NEWLINE are white space
		return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
	}

	//Recognize if input is underscore for identifiers
	private boolean isUnderscore(char ch) {
		return ch == '_';
	}

	// Returns the VALUE of the next character without removing it from the
	//    input line.  Useful for checking 2-character tokens that start with
	//    a 1-character token.
	private char PeekNextChar() {
		char result = ' ';
		if ((needLine) || (EOF)) {
			result = ' '; //at end of line, so nothing
		} else // 
		{
			if ((linePos + 1) < line.length()) { //have a char to peek
				result = line.charAt(linePos + 1);
			}
		}
		return result;
	}

	// Called by GetNextChar when the characters in the current line are used up.
	// STUDENT CODE SHOULD NOT EVER CALL THIS!
	private void GetNextLine() {
		try {
			line = bufferedreader.readLine();
			if ((line != null) && (echo)) {
				lineCount++;
				System.out.println(String.format("%04d", lineCount) + " " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (line == null) {    // The readLine returns null at EOF, set flag
			EOF = true;
		}
		linePos = -1;      // reset vars for new line if we have one
		needLine = false;  // we have one, no need
		//the line is ready for the next call to get a character
	}

	// Called to get the next character from file, automatically gets a new
	//      line when needed. CALL THIS TO GET CHARACTERS FOR GETIDENT etc.
	public char GetNextChar() {
		char result;
		if (needLine) //ran out last time we got a char, so get a new line
		{
			GetNextLine();
		}
		//try to get char from line buff
		if (EOF) {
			result = '\n';
			needLine = false;
		} else {
			if ((linePos < line.length() - 1)) { //have a character available
				linePos++;
				result = line.charAt(linePos);
			} else { //need a new line, but want to return eoln on this call first
				result = '\n';
				needLine = true; //will read a new line on next GetNextChar call
			}
		}
		return result;
	}

	//The constants below allow flexible comment start/end characters    
	final char commentStart_1 = '{';
	final char commentEnd_1 = '}';
	final char commentStart_2 = '(';
	final char commentPairChar = '*';
	final char commentEnd_2 = ')';

	//Skips past single and multi-line comments, and outputs UNTERMINATED 
	// COMMENT when end of line is reached before terminating
	String unterminatedComment = "Comment not terminated before End Of File";

	//Provided method to recognize and ignore commented lines
	public char skipComment(char curr) {
		if (curr == commentStart_1) {
			curr = GetNextChar();
			while ((curr != commentEnd_1) && (!EOF)) {
				curr = GetNextChar();
			}
			if (EOF) {
				consoleShowError(unterminatedComment);
			} else {
				curr = GetNextChar();
			}
		} else {
			if ((curr == commentStart_2) && (PeekNextChar() == commentPairChar)) {
				curr = GetNextChar(); // get the second
				curr = GetNextChar(); // into comment or end of comment
				//           while ((curr != commentPairChar) && (PeekNextChar() != commentEnd_2) &&(!EOF)) {
				while ((!((curr == commentPairChar) && (PeekNextChar() == commentEnd_2))) && (!EOF)) {
					//               if (lineCount >=4) {
					//              System.out.println("In Comment, curr, peek: "+curr+", "+PeekNextChar());}
					curr = GetNextChar();
				}
				if (EOF) {
					consoleShowError(unterminatedComment);
				} else {
					curr = GetNextChar();          //must move past close
					curr = GetNextChar();          //must get following
				}
			}

		}
		return (curr);
	}

	// Reads past all whitespace as defined by isWhiteSpace
	// NOTE THAT COMMENTS ARE SKIPPED AS WHITESPACE AS WELL!
	public char skipWhiteSpace() {

		do {
			while ((isWhitespace(currCh)) && (!EOF)) {
				currCh = GetNextChar();
			}
			currCh = skipComment(currCh);
		} while (isWhitespace(currCh) && (!EOF));
		return currCh;
	}
	
	//Recognizes if character could start double character operand
	private boolean isPrefix(char ch) {
		return ((ch == ':') || (ch == '<') || (ch == '>'));
	}

	//Recognizes string start
	private boolean isStringStart(char ch) {
		return ch == '"';
	}
	//Recognizes string end
	private boolean isStringEnd(char ch) {
		return ch == '"';
	}

	//global char value for current input character
	char currCh;

	/**
	 * Method to get identifier token
	 * Accepts a token starting with letters and consisting of any number of further alphanumeric chars or underscores until termination
	 * according to language token specifications
	 * Length limited to 20 chars in symbol table
	 * @return token of recognized identifier
	 */
	private token getIdentifier() {

		token result = new token();
		result.lexeme = "" + currCh; //have the first char
		currCh = GetNextChar();
		
		//Digest all valid characters and add to lexeme until end of token
		while (isLetter(currCh)||(isDigit(currCh) || isUnderscore(currCh))) {
			result.lexeme = result.lexeme + currCh; //extend lexeme
			currCh = GetNextChar();
		}
		
		//end of token, lookup for reserved word identifier if applicable
		result.code = reserveWords.LookupName(result.lexeme);
		if (result.code == ReserveTable.NOT_FOUND) {
			result.code = IDENT_ID;
		}

		//assign mnemonic based on token code
		result.mnemonic = mnemonics.LookupCode(result.code);

		//Identifiers need to be added to the symbol table after truncation
		//as needed
		if(result.code == IDENT_ID) {
			String symbolName = result.lexeme;

			if (symbolName.length() > MAXIDENT) {
				System.out.print("Identifier length > " + MAXIDENT + ", truncated " + symbolName);
				symbolName = result.lexeme.substring(0, MAXIDENT);
				System.out.print(" to " + symbolName + "\n");
			}
			saveSymbols.AddSymbol(symbolName, 'v', 0 );
		}
		return result;		
	}

	/**
	 * Method to get numeric constant token
	 * Accepts a integer or float number according to project language specifications
	 * regex:   <digit>+[.<digit>*[E<digit>+]]
	 * @return Token of recognized token
	 */
	private token getNumber() {

		token result = new token();
		result.lexeme = "" + currCh; //have the first char
		
		//support value if number requires truncation
		//needed due to value in symbol table being zeroed after truncation 
		boolean truncatedFlag = false;
		
		//Get all leading numbers
		while (isDigit(PeekNextChar())) {
			currCh = GetNextChar();
			result.lexeme = result.lexeme + currCh; //extend lexeme
		}
		result.code = INTGR_ID;
		currCh = GetNextChar();

		//truncation check for integer
		String symbolValue = result.lexeme;
		if (result.lexeme.length() > MAXINT) {
			System.out.print("Integer length > " + MAXINT + ", truncated " + result.lexeme);
			symbolValue = result.lexeme.substring(0, MAXINT);
			System.out.print(" to " + symbolValue + "\n");
			truncatedFlag = true;
		}

		//If float, get decimal numbers
		if (currCh == '.') {
			result.lexeme = result.lexeme + currCh;
			result.code = FLOAT_ID;
			
			//trailing decimals
			while (isDigit(PeekNextChar())){
				currCh = GetNextChar();
				result.lexeme = result.lexeme + currCh; //extend lexeme
			}

			//further logic for scientific notation for float
			currCh = GetNextChar();
			if (currCh == 'E') {
				result.lexeme = result.lexeme + currCh;
				currCh = GetNextChar();
				if (isDigit(currCh)) {
					while (isDigit(currCh)){

						result.lexeme = result.lexeme + currCh; //extend lexeme
						currCh = GetNextChar();
					}
				//reject invalid float with scientific notation
				} else {
					result.code = reserveWords.LookupName(UNDEFINED);
					result.mnemonic = mnemonics.LookupCode(result.code);
				}
			}
			
			//Truncation check for float
			symbolValue = result.lexeme;
			if (result.lexeme.length() > MAXFLT) {
				System.out.print("Float length > " + MAXFLT + ", truncated " + result.lexeme);
				symbolValue = result.lexeme.substring(0, MAXFLT);
				System.out.print(" to " + symbolValue + "\n");
				truncatedFlag = true;
			}
		}

		//assign mnemonic based on token code
		result.mnemonic = mnemonics.LookupCode(result.code);

		//Generate correct symbol table entry
		if (integerOK(symbolValue)){
			int value = !truncatedFlag ? Integer.parseInt(symbolValue) : 0;
			saveSymbols.AddSymbol(symbolValue, 'c', value);
		} else if (doubleOK(symbolValue)) {
			double value = !truncatedFlag ? Double.parseDouble(symbolValue) : 0.0;
			saveSymbols.AddSymbol(symbolValue, 'c', value);
		}

		return result;
	}

	/**
	 * Method to get string constant token
	 * Recognizes any string delimited by double quotes according to project langauge specifications
	 * Can contain and sequence of characters except line termination
	 * @return token of string constant
	 */
	private token getString() {

		token result = new token();
		currCh = GetNextChar();

		//Digest all characters until end of line or delimiter termination
		while ((!isStringEnd(currCh)) && (currCh != '\n')) {
			result.lexeme = result.lexeme + currCh; //extend lexeme
			currCh = GetNextChar();
		}

		//Complete token and add symbol entry if valid
		//otherwise throw warning, unterminated string
		if (currCh != '\n') {
			result.code = STRNG_ID;
			result.mnemonic = mnemonics.LookupCode(STRNG_ID);

			saveSymbols.AddSymbol(result.lexeme, 'c' , result.lexeme);
			currCh = GetNextChar();
			return result;
		} else {
			System.out.println("Unterminated String");
			result.code = reserveWords.LookupName(UNDEFINED);
			result.mnemonic = mnemonics.LookupCode(result.code);

			currCh = GetNextChar();
			return result;
		}
	}

	/**
	 * Method to get other tokens
	 * Recognizes single or double character token operands according to language specification
	 * @return Token for given operand
	 */
	private token getOtherToken() {

		token result = new token();
		result.lexeme = "" + currCh;

		//Logic for double character tokens
		if (isPrefix(currCh)) {
			if(PeekNextChar() == '=' || PeekNextChar() == '>') {
				currCh = GetNextChar();
				result.lexeme = result.lexeme + currCh; //extend lexeme
				switch (result.lexeme) {
				case ":=":
					assignFields(result);
					break;
				case ">=":
					assignFields(result);
					break;
				case "<=":
					assignFields(result);
					break;
				case "<>":
					assignFields(result);
					break;
				}
				return result;
			}
		}
		
		//Logic for single character tokens
			switch (currCh) {
			case '/':
				assignFields(result);
				break;
			case '*':
				assignFields(result);
				break;
			case '+':
				assignFields(result);
				break;
			case '-':
				assignFields(result);
				break;
			case '(':
				assignFields(result);
				break;
			case ')':
				assignFields(result);
				break;
			case ';':
				assignFields(result);
				break;
			case '>':
				assignFields(result);
				break;
			case '<':
				assignFields(result);
				break;
			case '=':
				assignFields(result);
				break;
			case ',':
				assignFields(result);
				break;
			case '[':
				assignFields(result);
				break;
			case ']':
				assignFields(result);
				break;
			case ':':
				assignFields(result);
				break;
			case '.':
				assignFields(result);
				break;
				
				//Default case declares unrecognized characters
			default:
				result.code = reserveWords.LookupName(UNDEFINED);
				result.mnemonic = mnemonics.LookupCode(result.code);
				currCh = GetNextChar();				
		}
		
		return result;
	}

	//Support method to clean code, assigns code/mnemonic based on lexeme results
	public void assignFields(token currToken) {
		currToken.code = reserveWords.LookupName(currToken.lexeme);
		currToken.mnemonic = mnemonics.LookupCode(currToken.code);	
		currCh = GetNextChar();		
	}

	// Checks to see if a string contains a valid DOUBLE 
	public boolean doubleOK(String stin) {
		boolean result;
		Double x;
		try {
			x = Double.parseDouble(stin);
			result = true;
		} catch (NumberFormatException ex) {
			result = false;
		}
		return result;
	}

	// Checks the input string for a valid INTEGER
	public boolean integerOK(String stin) {
		boolean result;
		int x;
		try {
			x = Integer.parseInt(stin);
			result = true;
		} catch (NumberFormatException ex) {
			result = false;
		}
		return result;
	}

	//Primary method to get next token in input sequence
	public token GetNextToken() {
		token result = new token();

		currCh = skipWhiteSpace();
		if (isLetter(currCh)) { //is identifier
			result = getIdentifier();
		} else if (isDigit(currCh)) { //is numeric
			result = getNumber();
		} else if (isStringStart(currCh)) { //string literal
			result = getString();
		} else //default char checks
		{
			result = getOtherToken();
		}

		if ((result.lexeme.equals("")) || (EOF)) {
			result = null;
		}
		//set the mnemonic
		if (result != null) {
			//THIS LINE REMOVED-- PUT BACK IN TO USE LOOKUP            
			//           result.mnemonic = mnemonics.LookupCode(result.code);
			if (printToken) {
				System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
			}
		}
		return result;
	}
}


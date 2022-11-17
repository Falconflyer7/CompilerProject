package ADT;

/**
 * Syntax Analyzer class
 * Uses previously developed ADTs to accept an input program and runs a recursive parse tree on program tokens
 * INCOMPLETE, Code for assignment PART A
 * @author abrouill, Mark Fish
 */
public class Syntactic {

	private String filein;              //The full file path to input file
	private SymbolTable symbolList;     //Symbol table storing ident/const
	private Lexical lex;                //Lexical analyzer 
	private Lexical.token token;        //Next Token retrieved 
	private boolean traceon;            //Controls tracing mode 
	private int level = 0;              //Controls indent for trace mode
	private boolean anyErrors;          //Set TRUE if an error happens 

	//symbol size variable
	private final int symbolSize = 250;

	/**
	 * Class constructor
	 * @param filename to read
	 * @param traceOn boolean for console trace
	 */
	public Syntactic(String filename, boolean traceOn) {
		filein = filename;
		traceon = traceOn;
		symbolList = new SymbolTable(symbolSize);
		lex = new Lexical(filein, symbolList, true);
		lex.setPrintToken(traceOn);
		anyErrors = false;
	}

	//The interface to the syntax analyzer, initiates parsing
	// Uses variable RECUR to get return values throughout the non-terminal methods    
	public void parse() {
		int recur = 0;
		// prime the pump to get the first token to process
		token = lex.GetNextToken();
		// call PROGRAM
		recur = Program();
	}

	//Non Terminal PROGIDENTIFIER is fully implemented here, leave it as-is.
	//<prog-identifier> -> <identifier>
	private int ProgIdentifier() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}

		// This non-term is used to uniquely mark the program identifier
		if (token.code == lex.codeFor("IDENT")) {
			// Because this is the progIdentifier, it will get a 'P' type to prevent re-use as a var
			symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'P', 0);
			//move on
			token = lex.GetNextToken();
		}
		return recur;
	}

	//Non Terminal PROGRAM is fully implemented here.
	//Descends in CFG from PROGRAM to block section
	//<program> -> $UNIT  <prog-identifier>  $SEMICOLON  <bloc> $PERIOD
	private int Program() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("Program", true);
		if (token.code == lex.codeFor("UNIT_")) {
			token = lex.GetNextToken();
			recur = ProgIdentifier();
			if (token.code == lex.codeFor("SEMIC")) {
				token = lex.GetNextToken();
				recur = Block();
				if (token.code == lex.codeFor("PRD__")) {
					if (!anyErrors) {
						System.out.println("Success.");
					} else {
						System.out.println("Compilation failed.");
					}
				} else {
					error(lex.reserveFor("PRD__"), token.lexeme);
				}
			} else {
				error(lex.reserveFor("SEMIC"), token.lexeme);
			}
		} else {
			error(lex.reserveFor("UNIT_"), token.lexeme);
		}
		trace("Program", false);
		return recur;
	}


	private int Block(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Block", true);


		while (token.code == lex.codeFor("VAR__")) {
			token = lex.GetNextToken();
			recur = VariableDecSec();
		}

		recur = BlockBody();

		trace("Block", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;

	}

	private int VariableDecSec(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("VariableDecSec", true);


		if (token.code == lex.codeFor("VAR__")) {
			token = lex.GetNextToken();
		}

		recur = VariableDeclaration();


		trace("VariableDecSec", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;

	}

	private int VariableDeclaration(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("VariableDeclaration", true);


		while (token.code == lex.codeFor("IDENT")) {
			token = lex.GetNextToken();

			recur = Identifier();

			while  (token.code == lex.codeFor("COMMA")) {
				token = lex.GetNextToken();
				recur = Identifier();
			}

			if (token.code == lex.codeFor("COLON")) {
				token = lex.GetNextToken();
			}else {
				//TODO error checking, should except expecting colon here
			}

			recur = SimpleType();

			if (token.code == lex.codeFor("SEMIC")) {
				token = lex.GetNextToken();
			}else {
				//TODO error checking, should except expecting semicolon here
			}
		}

		trace("VariableDeclaration", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;

	}

	//Non Terminal BLOCK is fully implemented here.
	//<block> -> $BEGIN <statement>  {$SEMICOLON  <statement>}* $END
	private int BlockBody() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("Block-body", true);

		if (token.code == lex.codeFor("BEGIN")) {
			token = lex.GetNextToken();
			recur = Statement();
			while ((token.code == lex.codeFor("SEMIC")) && (!lex.EOF()) && (!anyErrors)) {
				token = lex.GetNextToken();
				recur = Statement();
			}
			if (token.code == lex.codeFor("END__")) {
				token = lex.GetNextToken();
			} else {
				error(lex.reserveFor("END__"), token.lexeme);
			}

		} else {
			error(lex.reserveFor("BEGIN"), token.lexeme);
		}

		trace("Block-body", false);
		return recur;
	}

	//Not a NT, but used to shorten Statement code body for readability.   
	//<variable> $COLON-EQUALS <simple expression>
	private int handleAssignment() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("handleAssignment", true);
		//have ident already in order to get to here, handle as Variable
		recur = Variable();  //Variable moves ahead, next token ready

		if (token.code == lex.codeFor("ASSGN")) {
			token = lex.GetNextToken();
			recur = SimpleExpression();
		} else {
			error(lex.reserveFor("ASSGN"), token.lexeme);
		}

		trace("handleAssignment", false);
		return recur;
	}
	private int handleDoWhile(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("handleDoWhile", true);

		//Call for token after dowhile
		token = lex.GetNextToken();

		recur = RelExpression();
		recur = Statement();
		//        if (token.code == lex.codeFor("ASSGN")) {
		//			recur = SimpleExpression();
		//		} else {
		//			error(lex.reserveFor("ASSGN"), token.lexeme);
		//		}
		//        
		//        if (token.code == lex.codeFor("ASSGN")) {
		//			token = lex.GetNextToken();
		//			recur = SimpleExpression();
		//		} else {
		//			error(lex.reserveFor("ASSGN"), token.lexeme);
		//		}

		trace("handleDoWhile", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	} 
	
	private int handleRepeat(){
        int recur = 0;   //Return value used later
        if (anyErrors) { // Error check for fast exit, error status -1
            return -1;
        }

        trace("handleRepeat", true);

        recur = Statement();
        
        if (token.code == lex.codeFor("UNTIL")) {
        	token = lex.GetNextToken();
        } else {
        	error("Until", token.lexeme);
        }
        
        recur = RelExpression();

		trace("handleRepeat", false);
// Final result of assigning to "recur" in the body is returned
        return recur;

} 
	//Simple Expression nonterminal
	//<simple expression> -> [<sign>]  <term>  {<addop>  <term>}*
	private int SimpleExpression() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("SimpleExpression", true);

		//Optional sign nonterminal
		if (token.code == lex.codeFor("ADD__") || token.code == lex.codeFor("SUBTR")) {
			recur = Sign();
		}

		//Term nonterminal call
		//CFG rule must call into term at least once for a simple expression
		recur = Term();

		//Optional additional addop and term nonterminal calls
		if (token.code == lex.codeFor("ADD__") || token.code == lex.codeFor("SUBTR")){
			recur = Addop();
			recur = Term();
		}

		trace("SimpleExpression", false);
		return recur;
	}

	// Eventually this will handle all possible statement starts in 
	//    a nested if/else structure. Only ASSIGNMENT is implemented now.
	//<statement>-> <variable>  $COLON-EQUALS  <simple expression>
	private int Statement() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}

		trace("Statement", true);

		if (token.code == lex.codeFor("IDENT")) {  //must be an ASSIGNMENT
			recur = handleAssignment();
		} else if (token.code == lex.codeFor("IF___")) {  //must be an IF

		} else if (token.code == lex.codeFor("DWHLE")){
			recur = handleDoWhile();
		} else if (token.code == lex.codeFor("RPEAT")){
			recur = handleRepeat();
		} else if (token.code == lex.codeFor("FOR__")){

		} else if (token.code == lex.codeFor("WRTLN")){

		} else if (token.code == lex.codeFor("RDLN_")){

		} else {
			error("Statement start", token.lexeme);
		}
		//TODO Block body
		//		else if (token.code == lex.codeFor("DWHLE")){
		//
		//		}

		trace("Statement", false);
		return recur;
	}

	//Non-terminal VARIABLE just looks for an IDENTIFIER.  Later, a
	//  type-check can verify compatible math ops, or if casting is required.
	//<variable> -> <identifier> 
	private int Variable(){
		int recur = 0;
		if (anyErrors) {
			return -1;
		}

		trace("Variable", true);
		if ((token.code == lex.codeFor("IDENT"))) {
			// bookkeeping and move on
			token = lex.GetNextToken();
		} else {
			error("Variable", token.lexeme);
		}

		trace("Variable", false);
		return recur;
	}  

	//Term nonterminal
	//<term> -> <factor> {<mulop>  <factor> }*
	private int Term(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Term", true);

		//Factor nonterminal call
		//CFG rule must call into factor at least once
		recur = Factor();

		//Optional additional multiplication and factor nonterminal calls
		while ((token.code == lex.codeFor("MULTI") && !anyErrors) ||  (token.code == lex.codeFor("DIVID") && !anyErrors)){
			recur = Mulop();
			recur = Factor();
		}

		trace("Term", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}  

	//Sign terminal
	//Arrives at add or sub terminal, iterates token
	//<sign> -> $PLUS | $MINUS
	private int Sign(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Sign", true);

		//Retrieve token if sign found
		if (token.code == lex.codeFor("ADD__") || token.code == lex.codeFor("SUBTR")) {
			token = lex.GetNextToken();
		} else {
			error("Sign", token.lexeme);
		}

		trace("Sign", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}

	//Factor nonterminal
	//Recognizes a constant, variable, or parenthetical expression
	//<factor> -> <unsigned constant> | <variable> | $LPAR    <simple expression>    $RPAR
	private int Factor(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Factor", true);

		//Call unsigned constant NT if token constant is found,
		//or variable NT if token identifier is found
		//or call for simple expression if parenthetical group is found
		if (token.code == lex.codeFor("FCNST") || token.code == lex.codeFor("ICNST")) {
			recur = UnsignedConstant();
		} else if (token.code == lex.codeFor("IDENT")) {
			recur = Variable();
		} else if (token.code == lex.codeFor("LPRNT")) {
			token = lex.GetNextToken();
			recur = SimpleExpression();

			if (token.code == lex.codeFor("RPRNT")) {
				token = lex.GetNextToken();
			} else {
				error("')'", token.lexeme);
			}
		} else {
			error("Number, Variable or '('", token.lexeme);
		}

		trace("Factor", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	} 

	private int RelExpression(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("RelExpression", true);

		recur = SimpleExpression();
		recur = RelOp();
		recur = SimpleExpression();

		trace("RelExpression", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;

	} 

	private int RelOp(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("RelOp", true);

		if (token.code == lex.codeFor("EQUAL")){
			token = lex.GetNextToken();
		} else if (token.code == lex.codeFor("LSTHN")) {
			token = lex.GetNextToken();
		} else if (token.code == lex.codeFor("GRTHN")) {
			token = lex.GetNextToken();
		} else if (token.code == lex.codeFor("NTEQL")) {
			token = lex.GetNextToken();
		} else if (token.code == lex.codeFor("LSTEQ")) {
			token = lex.GetNextToken();
		} else if (token.code == lex.codeFor("GRTEQ")) {
			token = lex.GetNextToken();
		} else {
			error("RelationalOperator", token.lexeme);
		}

		trace("RelOp", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;

	} 

	//Unsigned Constant nonterminal
	//<unsigned constant>-> <unsigned number>
	private int UnsignedConstant(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("UnsignedConstant", true);

		//if float or integer constant token, call Unsigned Number
		if (token.code == lex.codeFor("FCNST") || token.code == lex.codeFor("ICNST")) {
			recur = UnsignedNumber();
		} else {
			error("Unsigned Constant", token.lexeme);
		}

		trace("UnsignedConstant", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}  

	//Unsigned Number terminal
	//<unsigned number>-> $FLOAT | $INTEGER
	private int UnsignedNumber(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("UnsignedNumber", true);

		//Iterate token if int or float found
		if (token.code == lex.codeFor("FCNST") || token.code == lex.codeFor("ICNST")) {
			token = lex.GetNextToken();
		} else {
			error("Unsigned Number", token.lexeme);
		}

		trace("UnsignedNumber", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}  

	private int Identifier(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Identifier", true);

		if (token.code == lex.codeFor("IDENT")) {
			token = lex.GetNextToken();
		}

		trace("Identifier", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;

	}

	private int SimpleType(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("SimpleType", true);


		if (token.code == lex.codeFor("INTGR")) {
			token = lex.GetNextToken();
		}
		if (token.code == lex.codeFor("FLOAT")) {
			token = lex.GetNextToken();
		}
		if (token.code == lex.codeFor("SRTNG")) {
			token = lex.GetNextToken();
		}

		//TODO bounding and case checking, must be one of these three

		trace("SimpleType", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;

	}

	//Multiplication operation terminal
	//<mulop> -> $MULTIPLY | $DIVIDE
	private int Mulop(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Mulop", true);

		//Iterate token if mulop found
		if (token.code == lex.codeFor("DIVID") || token.code == lex.codeFor("MULTI")) {
			token = lex.GetNextToken();
		} else {
			error("Mulop", token.lexeme);
		}

		trace("Mulop", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}

	//Addition operation terminal
	//<addop> -> $PLUS | $MINUS
	private int Addop(){
		int recur = 0;   //Return value used later
		if (anyErrors) { // Error check for fast exit, error status -1
			return -1;
		}

		trace("Addop", true);

		//Iterate token if arithmetic token found
		if (token.code == lex.codeFor("ADD__") || token.code == lex.codeFor("SUBTR")) {
			token = lex.GetNextToken();
		} else {
			error("Addop", token.lexeme);
		}

		trace("Addop", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}  

	/**
	 * *************************************************
	 */
	/*     UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
	// error provides a simple way to print an error statement to standard output
	//     and avoid reduncancy
	private void error(String wanted, String got) {
		anyErrors = true;
		System.out.println("ERROR: Expected " + wanted + " but found " + got);
	}

	// trace simply RETURNs if traceon is false; otherwise, it prints an
	// ENTERING or EXITING message using the proc string
	private void trace(String proc, boolean enter) {
		String tabs = "";

		if (!traceon) {
			return;
		}

		if (enter) {
			tabs = repeatChar(" ", level);
			System.out.print(tabs);
			System.out.println("--> Entering " + proc);
			level++;
		} else {
			if (level > 0) {
				level--;
			}
			tabs = repeatChar(" ", level);
			System.out.print(tabs);
			System.out.println("<-- Exiting " + proc);
		}
	}

	// repeatChar returns a string containing x repetitions of string s; 
	//    nice for making a varying indent format
	private String repeatChar(String s, int x) {
		int i;
		String result = "";
		for (i = 1; i <= x; i++) {
			result = result + s;
		}
		return result;
	}
	//END OF PROVIDED UTILITY FUNCTIONS

	/*  Template for all the non-terminal method bodies
   // ALL OF THEM SHOULD LOOK LIKE THE FOLLOWING AT THE ENTRY/EXIT POINTS  
private int exampleNonTerminal(){
        int recur = 0;   //Return value used later
        if (anyErrors) { // Error check for fast exit, error status -1
            return -1;
        }

        trace("NameOfThisMethod", true);

// The unique non-terminal stuff goes here, assigning to "recur" based
//     on recursive calls that were made

		trace("NameOfThisMethod", false);
// Final result of assigning to "recur" in the body is returned
        return recur;

}  

	 */    
}

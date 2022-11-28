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
	//Uses variable RECUR to get return values throughout the non-terminal methods    
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

		//This non-term is used to uniquely mark the program identifier
		if (token.code == lex.codeFor("IDENT")) {
			//Because this is the progIdentifier, it will get a 'P' type to prevent re-use as a var
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


	//Nonterminal Block
	//<block> -> {<variable-dec-sec>}*<block-body>
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

		//Variable declaration complete, will now complain when you use undeclared variables
		symbolList.DeclarationComplete();

		recur = BlockBody();

		trace("Block", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;
	}

	//Nonterminal for variable declaration section of program
	//<variable-dec-sec> -> $VAR <variable-declaration>
	private int VariableDecSec(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("VariableDecSec", true);

		if (token.code == lex.codeFor("VAR__")) {
			token = lex.GetNextToken();
		}

		recur = VariableDeclaration();

		trace("VariableDecSec", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;

	}

	//Nonterminal for all declaration of variables in program
	//<variable-declaration> -> {<identifier> {$COMMA <identifier>}* $COLON <simple type> $SEMICOLON}+ 
	private int VariableDeclaration(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("VariableDeclaration", true);

		while (token.code == lex.codeFor("IDENT") && !anyErrors) {
			token = lex.GetNextToken();

			recur = Identifier();

			while  (token.code == lex.codeFor("COMMA") && !anyErrors) {
				token = lex.GetNextToken();
				recur = Identifier();
			}

			if (token.code == lex.codeFor("COLON")) {
				token = lex.GetNextToken();
			}

			recur = SimpleType();

			if (token.code == lex.codeFor("SEMIC")) {
				token = lex.GetNextToken();
			}
		}

		trace("VariableDeclaration", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;

	}

	//Non Terminal block body is fully implemented here.
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
			while ((token.code == lex.codeFor("SEMIC")) && (!lex.EOF()) && (!anyErrors) && (token.code != lex.codeFor("END__"))) {
				token = lex.GetNextToken();
				recur = Statement();
			}
		} else if (token.code == lex.codeFor("END__")) {
			token = lex.GetNextToken();
		} else {
			error(lex.reserveFor("BEGIN"), token.lexeme);
		}

		trace("Block-body", false);
		return recur;
	}

	//Not a NT, but used to shorten Statement code body for readability.   
	//<variable> $ASSIGN (<simple expression> | <string literal>)
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

	//Not a NT, but used to shorten Statement code body for readability.
	//$IF <relexpression> $THEN <statement>[$ELSE <statement>]
	private int handleIf(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("handleIf", true);

		token = lex.GetNextToken();

		recur = RelExpression();

		if (token.code == lex.codeFor("THEN_")) {
			token = lex.GetNextToken();
		} else if (!anyErrors){
			error("THEN_", token.lexeme);
		}

		recur = Statement();

		if (token.code == lex.codeFor("ELSE_")) {
			token = lex.GetNextToken();
			recur = Statement();
		} 
		else if (!anyErrors){
			error("ELSE_", token.lexeme);
		}


		trace("handleIf", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;

	} 

	//Not a NT, but used to shorten Statement code body for readability.
	//$DOWHILE <relexpression> <statement>
	private int handleDoWhile(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("handleDoWhile", true);

		//Call for token after dowhile
		token = lex.GetNextToken();

		recur = RelExpression();
		recur = Statement();

		trace("handleDoWhile", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;
	} 

	//Not a NT, but used to shorten Statement code body for readability.
	//$REPEAT <statement> $UNTIL <relexpression>
	private int handleRepeat(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("handleRepeat", true);

		token = lex.GetNextToken();

		recur = Statement();

		if (token.code == lex.codeFor("UNTIL")) {
			token = lex.GetNextToken();
		} else if (!anyErrors){
			error("Until", token.lexeme);
		}

		recur = RelExpression();

		trace("handleRepeat", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;

	} 

	//Not a NT, but used to shorten Statement code body for readability.
	//$FOR <variable> $ASSIGN <simple expression> $TO <simple expression> $DO <statement>
	private int handleFor(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("handleFor", true);

		token = lex.GetNextToken();

		recur = Variable();

		if (token.code == lex.codeFor("ASSGN")) {
			token = lex.GetNextToken();
		} else if (!anyErrors){
			error("Assign", token.lexeme);
		}

		recur = SimpleExpression();

		if (token.code == lex.codeFor("TO___")) {
			token = lex.GetNextToken();
		} else if (!anyErrors){
			error("To", token.lexeme);
		}

		recur = SimpleExpression();

		if (token.code == lex.codeFor("DO___")) {
			token = lex.GetNextToken();
		} else if (!anyErrors){
			error("Do", token.lexeme);
		}

		recur = Statement();

		trace("handleFor", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;

	} 

	//Not a NT, but used to shorten Statement code body for readability.
	//$WRITELN $LPAR (<simple expression> | <identifier> |<stringconst> ) $RPAR
	private int handleWriteline(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("handleWriteline", true);

		token = lex.GetNextToken();

		if (token.code == lex.codeFor("LPRNT")) {
			token = lex.GetNextToken();
		} else {
			error("(", token.lexeme);
		}

		if (token.code == lex.codeFor("IDENT")) {
			recur = Identifier();
		} else if (token.code == lex.codeFor("SCNST")){
			recur = StringConstant();
		} else if (token.code == lex.codeFor("ADD__") || token.code == lex.codeFor("SUBTR") || token.code == lex.codeFor("LPRNT") 
				|| token.code == lex.codeFor("ICNST") || token.code == lex.codeFor("FCNST") || token.code == lex.codeFor("IDENT")) {
			recur = SimpleExpression();
		} else if (!anyErrors){
			error("", token.lexeme);
		}

		if (token.code == lex.codeFor("RPRNT")) {
			token = lex.GetNextToken();
		} else if (!anyErrors){
			error(")", token.lexeme);
		}

		trace("handleWriteline", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;

	} 

	//Not a NT, but used to shorten Statement code body for readability.
	//$READLN $LPAR <identifier> $RPAR
	private int handleReadline(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("handleReadline", true);

		token = lex.GetNextToken();

		if (token.code == lex.codeFor("LPRNT")) {
			token = lex.GetNextToken();
		} else if (!anyErrors){
			error("LPRNT", token.lexeme);
		}

		recur = Identifier();

		if (token.code == lex.codeFor("RPRNT")) {
			token = lex.GetNextToken();
		} else if (!anyErrors){
			error("RPRNT", token.lexeme);
		}
		trace("handleReadline", false);
		//Final result of assigning to "recur" in the body is returned
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

	// Handles all possible statement starts in a nested if/else structure.
	//<statement>-> { 
	//	[
	//	 <variable> $ASSIGN 
	//	(<simple expression> | <string literal>) |
	//	 <block-body> |
	//	 $IF <relexpression> $THEN <statement>
	//	[$ELSE <statement>] |
	//	 $DOWHILE <relexpression> <statement> |
	//	 $REPEAT <statement> $UNTIL <relexpression> |
	//	 $FOR <variable> $ASSIGN <simple expression>
	//	 $TO <simple expression> $DO <statement> |
	//	 $WRITELN $LPAR (<simple expression> | <identifier> |
	//	<stringconst> ) $RPAR
	//	 $READLN $LPAR <identifier> $RPAR
	//	]+
	private int Statement() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}

		trace("Statement", true);

		if (token.code == lex.codeFor("IDENT")) {
			recur = handleAssignment();
		} else if (token.code == lex.codeFor("IF___")) {
			recur = handleIf();
		} else if (token.code == lex.codeFor("DWHLE")){
			recur = handleDoWhile();
		} else if (token.code == lex.codeFor("RPEAT")){
			recur = handleRepeat();
		} else if (token.code == lex.codeFor("FOR__")){
			recur = handleFor();
		} else if (token.code == lex.codeFor("WRTLN")){
			recur = handleWriteline();
		} else if (token.code == lex.codeFor("RDLN_")){
			recur = handleReadline();
		} else if (token.code == lex.codeFor("BEGIN") || token.code == lex.codeFor("END__")){
			recur = BlockBody();
		} else {
			error("Statement start", token.lexeme);
		}

		//Scan through errored line for next statement
		if (anyErrors) {
			anyErrors = false;
			while (token.code != lex.codeFor("SEMIC") && token.code != lex.codeFor("END__")) {
				token = lex.GetNextToken();
			}
		}

		trace("Statement", false);
		return recur;
	}

	//Non-terminal VARIABLE just looks for an IDENTIFIER.  Later, a
	//type-check can verify compatible math ops, or if casting is required.
	//<variable> -> <identifier> 
	private int Variable(){
		int recur = 0;
		if (anyErrors) {
			return -1;
		}

		trace("Variable", true);
		if ((token.code == lex.codeFor("IDENT"))) {
			// bookkeeping and move on
			recur = Identifier();
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
		//Final result of assigning to "recur" in the body is returned
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
		//Final result of assigning to "recur" in the body is returned
		return recur;
	}

	//Factor nonterminal
	//Recognizes a constant, variable, or parenthetical expression
	//<factor> -> <unsigned constant> | <variable> | $LPAR    <simple expression>    $RPAR
	private int Factor(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
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
		//Final result of assigning to "recur" in the body is returned
		return recur;
	} 

	//Relational expression nonterminal
	//<relexpression> -> <simple expression> <relop> <simple expression>
	private int RelExpression(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("RelExpression", true);

		recur = SimpleExpression();
		recur = RelOp();
		recur = SimpleExpression();

		trace("RelExpression", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;

	} 

	//relational operation terminal
	//<relop> -> $EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
	private int RelOp(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
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
		//Final result of assigning to "recur" in the body is returned
		return recur;

	} 

	//Unsigned Constant nonterminal
	//<unsigned constant>-> <unsigned number>
	private int UnsignedConstant(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
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
		//Final result of assigning to "recur" in the body is returned
		return recur;
	}  

	//Unsigned Number terminal
	//<unsigned number>-> $FLOAT | $INTEGER
	private int UnsignedNumber(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
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
		//Final result of assigning to "recur" in the body is returned
		return recur;
	}  

	//Identifier terminal
	//<identifier> -> $IDENTIFIER
	private int Identifier(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("Identifier", true);

		if (token.code == lex.codeFor("IDENT")) {
			token = lex.GetNextToken();
		}

		trace("Identifier", false);
		//Final result of assigning to "recur" in the body is returned
		return recur;

	}

	//Simple type terminal
	//<simple type> -> $INTEGER | $FLOAT | $STRING
	private int SimpleType(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
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

		trace("SimpleType", false);
		// Final result of assigning to "recur" in the body is returned
		return recur;
	}

	//Multiplication operation terminal
	//<mulop> -> $MULTIPLY | $DIVIDE
	private int Mulop(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
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
		//Final result of assigning to "recur" in the body is returned
		return recur;
	}

	//Addition operation terminal
	//<addop> -> $PLUS | $MINUS
	private int Addop(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
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
		//Final result of assigning to "recur" in the body is returned
		return recur;
	}  

	//String constant terminal
	//<stringconst> -> $STRINGTYPE
	private int StringConstant(){
		int recur = 0;   //Return value used later
		if (anyErrors) { //Error check for fast exit, error status -1
			return -1;
		}

		trace("StringConstant", true);

		if (token.code == lex.codeFor("SCNST")) {
			token = lex.GetNextToken();
		} else {
			error("StringConstant", token.lexeme);
		}

		trace("StringConstant", false);
		//Final result of assigning to "recur" in the body is returned
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

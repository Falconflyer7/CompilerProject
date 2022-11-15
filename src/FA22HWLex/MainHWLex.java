package FA22HWLex;

import ADT.Lexical;
//import ADT.LexicalReserve;
import ADT.SymbolTable;
//import ADT.Lexical;
/**
 *
 * @author abrouill FALL 2022
 */
public class MainHWLex {

    public static void main(String[] args) {
        String inFileAndPath = args[0];
        String outFileAndPath = args[1];
        System.out.println("Lexical for " + inFileAndPath);
        boolean traceOn = true;
        // Create a symbol table to store appropriate3 symbols found
        SymbolTable symbolList;
        symbolList = new SymbolTable(150);
//        Lexical myLexer = new Lexical(inFileAndPath, symbolList, traceOn);
//        Lexical.token currToken;
        Lexical myLexer = new Lexical(inFileAndPath, symbolList, traceOn);
        Lexical.token currToken;
        currToken = myLexer.GetNextToken();
        while (currToken != null) {
            System.out.println("\t" + currToken.mnemonic + " | \t" + String.format("%04d", currToken.code)
                    + " | \t" + currToken.lexeme);
            currToken = myLexer.GetNextToken();
        }
        symbolList.PrintSymbolTable(outFileAndPath);
        System.out.println("Done.");
    }

}

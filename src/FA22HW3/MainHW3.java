package FA22HW3;

import ADT.*;

/**
 *
 * @author abrouill
 */
public class MainHW3 {

    public static void main(String[] args) {
    	
    	//Required student name header
        System.out.println("Mark Fish CS4100 Homework 3, Fall 2022");
        
//        String filePath = "d:\\"; 
        Interpreter interp = new Interpreter();
        SymbolTable st;
        QuadTable qt;
        
        // interpretation FACTORIAL
        st = new SymbolTable(20);     //Create an empty SymbolTable
        qt = new QuadTable(20);       //Create an empty QuadTable
        System.out.println("This program expects command-line parameters for filenames in this order:");
        System.out.println("traceFactorial SymbolFactorial QuadFactorial traceSum SymbolSum QuadSum");
        interp.initializeFactorialTest(st,qt);  //Set up for FACTORIAL
//        interp.InterpretQuads(qt, st, true, filePath+"traceFact.txt");
//        st.PrintSymbolTable(filePath+"symbolTableFact.txt");
//        qt.PrintQuadTable(filePath+"quadTableFact.txt");
        interp.InterpretQuads(qt, st, true, args[0]);
        st.PrintSymbolTable(args[1]);
        qt.PrintQuadTable(args[2]);

        // interpretation SUMMATION
        st = new SymbolTable(20);     //Create an empty SymbolTable
        qt = new QuadTable(20);       //Create an empty QuadTable
        interp.initializeSummationTest(st,qt);  //Set up for SUMMATION
        interp.InterpretQuads(qt, st, true, args[3]);
        st.PrintSymbolTable(args[4]);
        qt.PrintQuadTable(args[5]);
        }
    
}

package ADT;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Interpreter Class
 * Hard coded initialization for building quad and symbol table objects required
 * Creates interpreter class and allows for running of inputed quad and symbol table structure
 * @author Mark Fish
 * CS 4100 FA22 
 *
 */
public class Interpreter {

	/**
	 * Class private fields
	 * Includes several helpful constants for opcode clarity
	 */
	private final ReserveTable reserveTable;
	private static final int MAX_RESERVE_TABLE_SIZE = 16;
	private static final int STOP = 0;
	private static final int DIV = 1;
	private static final int MUL = 2;
	private static final int SUB = 3;
	private static final int ADD = 4;
	private static final int MOV = 5;
	private static final int PRINT = 6;
	private static final int READ = 7;
	private static final int JMP = 8;
	private static final int JZ = 9;
	private static final int JP = 10;
	private static final int JN = 11;
	private static final int JNZ = 12;
	private static final int JNP  = 13;
	private static final int JNN = 14;
	private static final int JINDR = 15;

	/**
	 *  initializes the Interpreter’s ReserveTable, adding its opcode mappings and any other initialization needed.
	 */
	public Interpreter() {

		this.reserveTable = new ReserveTable(MAX_RESERVE_TABLE_SIZE);
		initReserve(reserveTable);

	}

	/**
	 * Hard coded method to initialize a summation symbol and quad table
	 * @param st symbol table to build
	 * @param qt quad table to build
	 */
	public void initializeSummationTest(SymbolTable st, QuadTable qt) {

		// populate symbol table for summation algorithm, constitutes program data
		st.AddSymbol("n", 'v', 10);
		st.AddSymbol("i", 'v', 0);
		st.AddSymbol("sum", 'v', 0);
		st.AddSymbol("1", 'c', 1);
		st.AddSymbol("$temp", 'v', 0);
		st.AddSymbol("0", 'c', 0);

		// populate quad table for summation algorithm, constitutes program instructions
		qt.AddQuad(5, 5, 0, 2);
		qt.AddQuad(5, 3, 0, 1);
		qt.AddQuad(3, 1, 0, 4);
		qt.AddQuad(10, 4, 0, 7);
		qt.AddQuad(4, 2, 1, 2);
		qt.AddQuad(4, 1, 3, 1);
		qt.AddQuad(8, 0, 0, 2);
		qt.AddQuad(6, 2, 0, 0);

	}

	/**
	 * Hard coded method to initialize a factorial algorithm symbol and quad table
	 * @param st symbol table to build
	 * @param qt quad table to build
	 */
	public void initializeFactorialTest(SymbolTable st, QuadTable qt) {

		// populate symbol table for factorial algorithm, constitutes program data
		st.AddSymbol("n", 'v', 10);
		st.AddSymbol("i", 'v', 0);
		st.AddSymbol("product", 'v', 0);
		st.AddSymbol("1", 'c', 1);
		st.AddSymbol("$temp", 'v', 0);

		// populate quad table for factorial algorithm, constitutes program instructions
		qt.AddQuad(5, 3, 0, 2);
		qt.AddQuad(5, 3, 0, 1);
		qt.AddQuad(3, 1, 0, 4);
		qt.AddQuad(10, 4, 0, 7);
		qt.AddQuad(2, 2, 1, 2);
		qt.AddQuad(4, 1, 3, 1);
		qt.AddQuad(8, 0, 0, 2);
		qt.AddQuad(6, 2, 0, 0);
	}

	/**
	 * Interpreter of the class. Accepts a quad table and symbol table and executes their
	 * instructions on their data appropriately based on opcodes
	 * @param quadTable containing instructions to run
	 * @param symbolTable containing data fields to the related quad table
	 * @param traceOn boolean to print interpreter operation trace
	 * @param fileName output file for the trace
	 */
	public void InterpretQuads(QuadTable quadTable, SymbolTable symbolTable, boolean traceOn, String fileName) {

		//initialize program counter and quad codes
		int pc = 0;
		int opcode;
		int op1;
		int op2;
		int op3;

		//initialize file writer for trace
		BufferedWriter bufferedWriter = initWriter(fileName);

		//Loop over quads under PC until program stop
		while (pc < quadTable.GetMaxQuadTableSize()) {

			//get next quad table values at program counter
			opcode = quadTable.GetQuad(pc)[0];
			op1 = quadTable.GetQuad(pc)[1];
			op2 = quadTable.GetQuad(pc)[2];
			op3 = quadTable.GetQuad(pc)[3];
			int temp;

			if (opcodeValidation(opcode)) {

				//Run trace
				if (traceOn) {
					String trace = makeTraceString(pc, opcode, op1, op2, op3);	
					System.out.println(trace);
					traceIO(trace, bufferedWriter);

				}

				//Case structure for each opcode, runs corresponding behaviour
				switch (opcode) {

				//STOP code
				case STOP:
					pc = quadTable.GetMaxQuadTableSize();
					if (traceOn) {
						System.out.println("Execution terminated by program STOP.");
						traceIO("Execution terminated by program STOP.", bufferedWriter);
					}
					break;

					//DIV code
				case DIV:
					temp = symbolTable.GetInteger(op1) / symbolTable.GetInteger(op2);
					symbolTable.UpdateSymbol(op3,'v', temp);
					//Increment program counter after code execution
					pc++;
					break;

					//MUL code
				case MUL:
					temp = symbolTable.GetInteger(op1) * symbolTable.GetInteger(op2);
					symbolTable.UpdateSymbol(op3,'v', temp);
					//Increment program counter after code execution
					pc++;
					break;

					//SUB code
				case SUB:
					temp = symbolTable.GetInteger(op1) - symbolTable.GetInteger(op2);
					symbolTable.UpdateSymbol(op3,'v', temp);
					//Increment program counter after code execution
					pc++;
					break;

					//ADD code
				case ADD:
					temp = symbolTable.GetInteger(op1) + symbolTable.GetInteger(op2);
					symbolTable.UpdateSymbol(op3,'v', temp);
					//Increment program counter after code execution
					pc++;
					break;

					//MOV code
				case MOV:
					symbolTable.UpdateSymbol(op3, 'v', symbolTable.GetInteger(op1));
					//Increment program counter after code execution
					pc++;
					break;

					//PRINT code
				case PRINT:
					System.out.println(symbolTable.GetSymbol(op3) + " = " + symbolTable.GetInteger(op3));
					//Increment program counter after code execution
					pc++;
					break;

					//READ code
					//				case READ:
					//					Scanner scanner = new Scanner(System.in);
					//					temp = Integer.valueOf(scanner.nextLine());
					//					symbolTable.UpdateSymbol(op3, 'v', temp);
					//					//Increment program counter after code execution
					//					pc++;
					//					break;

				case READ:
					// Assume parameter/operand must be an integer value
				{
					// Make a scanner to read from CONSOLE
					Scanner sc = new Scanner(System.in);
					// Put out a prompt to the user
					System.out.print('>');
					// Read one integer only
					int readval = sc.nextInt();
					// Op3 has the SymbolTable index we need, update its value
					symbolTable.UpdateSymbol(op3,'i',readval);
					// Deallocate the scanner
					sc.close();
					sc = null;
					pc++;
					break;
				}

				//JMP code
				case JMP:
					pc = op3;
					break;

					//JZ code
				case JZ:
					if (symbolTable.GetInteger(op1) == 0) {
						pc = op3;
					} else {
						pc++;
					}
					break;

					//JP code
				case JP:
					if (symbolTable.GetInteger(op1) > 0) {
						pc = op3;
					} else {
						pc++;
					}
					break;

					//JN code
				case JN:
					if (symbolTable.GetInteger(op1) < 0) {
						pc = op3;
					} else {
						pc++;
					}
					break;

					//JNZ code
				case JNZ:
					if (symbolTable.GetInteger(op1) != 0) {
						pc = op3;
					} else {
						pc++;
					}
					break;

					//JNP code
				case JNP:
					if (symbolTable.GetInteger(op1) <= 0) {
						pc = op3;
					} else {
						pc++;
					}
					break;

					//JNN code
				case JNN:
					if (symbolTable.GetInteger(op1) >= 0) {
						pc = op3;
					} else {
						pc++;
					}
					break;

					//JINDR code
				case JINDR:
					pc = symbolTable.GetInteger(op3);
					break;

				default:
					//should never get here but this would be invalid opcode
					break;

				}
			}
		}

		//Close writer for trace after program execution
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	/**
	 * Hard coded method to initialize a reserve table with our required operations and codes
	 * @param optable to initialize
	 */
	private void initReserve(ReserveTable optable){
		optable.Add("STOP", 0);
		optable.Add("DIV", 1);
		optable.Add("MUL", 2);
		optable.Add("SUB", 3);
		optable.Add("ADD", 4);
		optable.Add("MOV", 5);
		optable.Add("PRINT", 6);
		optable.Add("READ", 7);
		optable.Add("JMP", 8);
		optable.Add("JZ", 9);
		optable.Add("JP", 10);
		optable.Add("JN", 11);
		optable.Add("JNZ", 12);
		optable.Add("JNP", 13);
		optable.Add("JNN", 14);
		optable.Add("JINDR", 15);
	}

	/**
	 * support method to create a string item for trace output
	 * @param pc Current program counter
	 * @param opcode Current opcode
	 * @param op1 Current op1
	 * @param op2 Current op2
	 * @param op3 Current op3
	 * @return formatted string of current program trace
	 */
	private String makeTraceString(int pc, int opcode,int op1,int op2,int op3 ){
		String result = "";
		result = "PC = "+String.format("%04d", pc)+": "+(reserveTable.LookupCode(opcode)+"     ").substring(0,6)+String.format("%02d",op1)+
				", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
		return result;
	}

	/**
	 * Support method to help abstract trace writing
	 * @param traceString written to trace
	 * @param writer Buffered Writer for trace file output
	 */
	private void traceIO(String traceString, BufferedWriter writer) {
		try {
			writer.write(traceString);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}    
	}

	/**
	 * Support method to abstract validation of given opcode in the space of accepted codes
	 * @param opcode to validate
	 * @return boolean for valid opcode
	 */
	private boolean opcodeValidation(int opcode) {
		if ((opcode < MAX_RESERVE_TABLE_SIZE) && (opcode >= 0)){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Support method to help abstract file IO for trace
	 * @param fileName to output to
	 * @return a buffered writer directed provided file
	 */
	private BufferedWriter initWriter(String fileName) {
		BufferedWriter bufferedWriter = null;
		try {
			FileOutputStream outputStream = new FileOutputStream(fileName);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			return bufferedWriter;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int opcodeFor(String string) {
		return this.reserveTable.LookupName(string);
	}

}

package ADT;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * CS 4100 FA22
 * @author Mark Fish
 * Class for Quad Table abstract data type
 * Constructs a 2d array indexed linearly containing an opcode and 3 operands
 *
 */
public class QuadTable {

	//class private variables
	private int[][] quadTable;
	private int nextAvailable;

	/**
	 * Quad table constructor
	 * Initializes to max size provided
	 * next available parameter set to zero, keeps track of next open space in table
	 * @param maxSize parameter to specify table size
	 */
	public QuadTable(int maxSize) {
		this.quadTable = new int[maxSize][4];
		this.nextAvailable = 0;
	}

	/**
	 * Method to add a quad into table
	 * @param opcode operation code for given quad
	 * @param op1 operand 1 for given quad
	 * @param op2 operand 2 for given quad
	 * @param op3 operand 3 for given quad
	 */
	public void AddQuad(int opcode, int op1, int op2, int op3) {
		int quadLocation = NextQuad();
		quadTable[quadLocation][0] = opcode;
		quadTable[quadLocation][1] = op1;
		quadTable[quadLocation][2] = op2;
		quadTable[quadLocation][3] = op3;

		nextAvailable++;
	}

	/**
	 * Acquire next open space in table
	 * @return given available space
	 */
	public int NextQuad() {
		return nextAvailable;
	}

	/**
	 * Acquires indexed quad from table
	 * @param index for requested quad
	 * @return integer array of requested quad at index
	 */
	public int[] GetQuad(int index) {
		if (index < quadTable.length) {
			return quadTable[index];
		}
		return null;
	}
	
	/**
	 * Update target address of indexed quad
	 * @param index of target quad
	 * @param op3 address location to be updated
	 */
	public void UpdateJump(int index, int op3) {
		if (index < quadTable.length) {
			quadTable[index][3] = op3;
		}
	}

	/**
	 * Retrieve table size for interpreter
	 * @return integer of quad table size
	 */
	public int GetMaxQuadTableSize() {
		
		return quadTable.length;
		
	}
	
	/**
	 * Prints contents of currently constructed quad table to file provided
	 * @param filename to write to
	 */
	public void PrintQuadTable(String filename) {
		try {
			FileOutputStream outputStream = new FileOutputStream(filename);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
			
			// file header
			String paddedIndexHeadString = pad(pad("Index",3,true),6,false);
			String paddedOpcodeHeadString = pad(pad("Opcode",3,true),8,false);
			String paddedOp1HeadString = pad(pad("Op1",3,true),5,false);
			String paddedOp2HeadString = pad(pad("Op2",3,true),5,false);
			String paddedOp3HeadString = pad(pad("Op3",3,true),5,false);

			bufferedWriter.write(paddedIndexHeadString+ paddedOpcodeHeadString + paddedOp1HeadString + paddedOp2HeadString + paddedOp3HeadString);
			bufferedWriter.newLine();

			//file contents
			for (int i = 0; i < nextAvailable; i++) {
				int[] entry = this.GetQuad(i);
				String paddedIndexString = pad(pad(Integer.toString(i),3,true),5,false);
				String paddedOpcodeString = pad(pad(Integer.toString(entry[0]),3,true),6,false);
				String paddedOp1String = pad(pad(Integer.toString(entry[1]),3,true),4,false);
				String paddedOp2String = pad(pad(Integer.toString(entry[2]),3,true),4,false);
				String paddedOp3String = pad(pad(Integer.toString(entry[3]),3,true),4,false);
				bufferedWriter.write(paddedIndexString + "|" + paddedOpcodeString + "|" + paddedOp1String + "|" + paddedOp2String + "|" + paddedOp3String + "|");
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Text padding algorithm
	private String pad(String input, int len, boolean left) {
		while (input.length() < len){
			if (left)
				input = " " +input ;
			else
				input = input + " ";
		}
		return input;
	}
}

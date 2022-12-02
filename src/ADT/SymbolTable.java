package ADT;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * CS 4100 FA22
 * @author Mark Fish
 * Class for Symbol Table abstract data type
 * constructs a table linearly indexed of symbols with given name, use type (label, variable, constant)
 * data type (integer, float, string)
 * and particular value of said type
 *
 */
public class SymbolTable {

	//class private variables
	private int maxSize;
	private int index = 0;
	private ArrayList<SymbolTableData> data;
	private boolean declarationComplete = false;

	/**
	 * Class constructor
	 * initializes table to specific max size
	 * @param maxSize of symbol table
	 */
	public SymbolTable(int maxSize) {
		this.maxSize = maxSize;
		this.data = new ArrayList<>(maxSize);
	}
	
	/**
	 * Method to add a symbol entry into table
	 * Overloaded method, integer variant
	 * @param name of symbol item
	 * @param usage type of symbol item
	 * @param value of symbol item
	 * @return index where symbol was written in table
	 */
	public int AddSymbol(String name, char usage, int value) {

		//Size limit checking
		if (index == maxSize) {
			return -1;
		}
		
		// searches table, if given symbol is already present, returns that index instead
		int existsInTable = LookupSymbol(name);
		if (existsInTable >= 0) {
			return existsInTable;
		}

		int currentIndex = index;

		SymbolTableData tableItem = new SymbolTableData(currentIndex, name, usage, 'I', value);
		data.add(currentIndex, tableItem);

		index++;
		
		if (Character.toLowerCase(usage) ==  'v' && declarationComplete) {
			undeclaredError(name);
		}
		return currentIndex;

	}
	
	/**
	 * Method to add a symbol entry into table
	 * Overloaded method, double variant
	 * @param name of symbol item
	 * @param usage type of symbol item
	 * @param value of symbol item
	 * @return index where symbol was written in table
	 */
	public int AddSymbol(String name, char usage, double value) {

		//Size limit checking
		if (index == maxSize) {
			return -1;
		}

		int existsInTable = LookupSymbol(name);
		if (existsInTable >= 0) {
			return existsInTable;
		}
		int currentIndex = index;

		SymbolTableData tableItem = new SymbolTableData(currentIndex, name, usage, 'F', value);
		data.add(currentIndex, tableItem);

		index++;
		
		if (Character.toLowerCase(usage) ==  'v' && declarationComplete) {
			undeclaredError(name);
		}
		return currentIndex;


	}

	/**
	 * Method to add a symbol entry into table
	 * Overloaded method, string variant
	 * @param name of symbol item
	 * @param usage type of symbol item
	 * @param value of symbol item
	 * @return index where symbol was written in table
	 */
	public int AddSymbol(String name, char usage, String value) {

		//Size limit checking
		if (index == maxSize) {
			return -1;
		}

		int existsInTable = LookupSymbol(name);
		if (existsInTable >= 0) {
			return existsInTable;
		}
		int currentIndex = index;

		SymbolTableData tableItem = new SymbolTableData(currentIndex, name, usage, 'S', value);
		data.add(currentIndex, tableItem);

		index++;
		if (Character.toLowerCase(usage) ==  'v' && declarationComplete) {
			undeclaredError(name);
		}
		return currentIndex;

	}


	/**
	 * Method to look up symbol in table by given name
	 * @param symbolName to search
	 * @return index of symbol, otherwise return -1, item not found
	 */
	public int LookupSymbol(String symbolName) {
		//Search index for given name lookup
		for (SymbolTableData entry : data) {
			if (entry.symbolName.compareToIgnoreCase(symbolName) == 0) {
				return entry.index;
			} 
		}
		//Return Item Not In Table
		return -1;
	}

	/**
	 * Method to search symbol item at index
	 * @param index to read from
	 * @return symbol at index
	 */
	public String GetSymbol(int index) {

		if (index >= maxSize) {
			return null;
		}

		SymbolTableData indexedItem = data.get(index);
		return indexedItem.symbolName;
	}
	
	/**
	 * Method to search usage item at index
	 * @param index to read from
	 * @return usage at index
	 */
	public char GetUsage(int index) {
		if (index >= maxSize) {
			return ' ';
		}

		SymbolTableData indexedItem = data.get(index);
		return indexedItem.usage;
	}
	
	/**
	 * Method to search data type item at index
	 * @param index to read from
	 * @return data type at index
	 */
	public char GetDataType(int index) {
		if (index >= maxSize) {
			return ' ';
		}

		SymbolTableData indexedItem = data.get(index);
		return indexedItem.dataType;
	}
	
	/**
	 * Method to search string item at index
	 * @param index to read from
	 * @return string at index
	 */
	public String GetString(int index) {
		if (index >= maxSize) {
			return null;
		}

		SymbolTableData indexedItem = data.get(index);
		return indexedItem.stringValue;
	}
	
	/**
	 * Method to search integer item at index
	 * @param index to read from
	 * @return integer at index
	 */
	public int GetInteger(int index) {
		if (index >= maxSize) {
			return -1;
		}

		SymbolTableData indexedItem = data.get(index);
		return indexedItem.integerValue;
	}
	
	/**
	 * Method to search float item at index
	 * @param index to read from
	 * @return float at index
	 */
	public double GetFloat(int index) {
		if (index >= maxSize) {
			return -1;
		}

		SymbolTableData indexedItem = data.get(index);
		return indexedItem.floatValue;
	}

	/**
	 * Method to update usage and value of item in table at index
	 * Integer variant of overloaded method
	 * @param index to update
	 * @param usage of updated symbol
	 * @param value of updated symbol
	 */
	public void UpdateSymbol(int index, char usage, int value){
		if (index >= maxSize) {
			return;
		}

		SymbolTableData indexedItem = data.get(index);
		if (indexedItem.dataType == 'I') {
			indexedItem.usage = usage;
			indexedItem.integerValue = value;	
		}
	}
	
	/**
	 * Method to update usage and value of item in table at index
	 * double variant of overloaded method
	 * @param index to update
	 * @param usage of updated symbol
	 * @param value of updated symbol
	 */
	public void UpdateSymbol(int index, char usage, double value){
		if (index >= maxSize) {
			return;
		}

		SymbolTableData indexedItem = data.get(index);
		if (indexedItem.dataType == 'F') {
			indexedItem.usage = usage;
			indexedItem.floatValue = value;
		}
	}
	
	/**
	 * Method to update usage and value of item in table at index
	 * string variant of overloaded method
	 * @param index to update
	 * @param usage of updated symbol
	 * @param value of updated symbol
	 */
	public void UpdateSymbol(int index, char usage, String value){
		if (index >= maxSize) {
			return;
		}

		SymbolTableData indexedItem = data.get(index);
		if (indexedItem.dataType == 'S') {
			indexedItem.usage = usage;
			indexedItem.stringValue = value;
		}
	}

	/**
	 * Method to print constructed symbol table to specified file
	 * @param filename to write to
	 */
	public void PrintSymbolTable(String filename) {

		try {
			FileOutputStream outputStream = new FileOutputStream(filename);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

			//symbol table header
			int stringPad = findMaxStringLength();
			String paddedIndexHeadString = pad(pad("Index",3,true),6,false);
			String paddedNameHeadString = pad(pad("Name",3,true),stringPad+3,false);
			String paddedUseHeadString = pad(pad("Use",3,true),4,false);
			String paddedTypHeadString = pad(pad("Typ",3,true),4,false);
			String paddedValueHeadString = pad(pad("Value",3,true),5,false);

			bufferedWriter.write(paddedIndexHeadString+ paddedNameHeadString + paddedUseHeadString + paddedTypHeadString + paddedValueHeadString);
			bufferedWriter.newLine();

			//symbol table contents
			for (SymbolTableData entry : data) {
				String paddedIndexString = pad(pad(Integer.toString(entry.index),3,true),5,false);
				String paddedNameString = pad(pad(entry.symbolName,3,true),stringPad+2,false);
				String paddedUseString = pad(pad(Character.toString(entry.usage),1,true),3,false);
				String paddedTypString = pad(pad(Character.toString(entry.dataType),1,true),3,false);
				String paddedValueString = pad(pad(entry.GetValue(),1,true),0,false);
				bufferedWriter.write(paddedIndexString + "|" + paddedNameString + "|" + paddedUseString + "|" + paddedTypString + "|" + paddedValueString);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}    

	}

	//support method
	//Find longest name in table
	private int findMaxStringLength() {

		int maxLength = 0;

		for (SymbolTableData entry : data) {
			if (entry.symbolName.length() > maxLength) {
				maxLength = entry.symbolName.length();
			}
		}
		return maxLength;
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


	public void DeclarationComplete() {
		declarationComplete = true;
	}
	
	private void undeclaredError(String name) {
		System.out.println("Undeclared Identifier: " + name);
	}
	
	/**
	 * 
	 * @author Mark Fish
	 * internal private class to provide data structure for symbol table data items
	 *
	 */
	private static class SymbolTableData{
		
		//Private data variables
		private final int index;
		private String symbolName;
		private char usage;
		private char dataType;
		private int integerValue;
		private double floatValue;
		private String stringValue;

		/**
		 * Constructor for symbol table data entry
		 * Integer variant of overloaded constructor
		 * @param index of table item
		 * @param symbolName of table item
		 * @param usage of table item
		 * @param dataType of table item
		 * @param integerValue of table item
		 */
		public SymbolTableData(int index, String symbolName, char usage, char dataType, int integerValue) {
			super();
			this.index = index;
			this.symbolName = symbolName;
			this.usage = usage;
			this.dataType = dataType;
			this.integerValue = integerValue;
			this.floatValue = 0;
			this.stringValue = null;
		}
		
		/**
		 * Constructor for symbol table data entry
		 * double variant of overloaded constructor
		 * @param index of table item
		 * @param symbolName of table item
		 * @param usage of table item
		 * @param dataType of table item
		 * @param integerValue of table item
		 */
		public SymbolTableData(int index, String symbolName, char usage, char dataType, double floatValue) {
			super();
			this.index = index;
			this.symbolName = symbolName;
			this.usage = usage;
			this.dataType = dataType;
			this.integerValue = 0;
			this.floatValue = floatValue;
			this.stringValue = null;
		}
		
		/**
		 * Constructor for symbol table data entry
		 * string variant of overloaded constructor
		 * @param index of table item
		 * @param symbolName of table item
		 * @param usage of table item
		 * @param dataType of table item
		 * @param integerValue of table item
		 */
		public SymbolTableData(int index, String symbolName, char usage, char dataType, String stringValue) {
			super();
			this.index = index;
			this.symbolName = symbolName;
			this.usage = usage;
			this.dataType = dataType;
			this.integerValue = 0;
			this.floatValue = 0;
			this.stringValue = stringValue;
		}

		/**
		 * Support method to return value of table item as a string regardless of type
		 * used in file printing
		 * @return value of given item as string
		 */
		public String GetValue() {
			if (dataType == 'S') {
				return stringValue;
			}

			if (dataType == 'I') {
				return Integer.toString(integerValue);
			}

			if (dataType == 'F') {
				return Double.toString(floatValue);
			}
			return null;
		}
	}
	
}

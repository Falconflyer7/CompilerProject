package ADT;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

//Class for a reserve table that accepts a name and associated code, as well as tracks item index
//Mark Fish CS 4100 FA22
public class ReserveTable {

	public static final int NOT_FOUND = -1;
	
	int maxSize;
	int index = 0;
	ArrayList<InternalData> data;
	
	//Constructor Reserve Table
	public ReserveTable(int maxSize){

		this.maxSize = maxSize;
		this.data = new ArrayList<>(maxSize);

	}
	
	//Add entry to table
	public int Add(String name, int code) {

		//Size limit checking
		if (index == maxSize) {
			return -1;
		}

		int currentIndex = index;

		InternalData tableItem = new InternalData(currentIndex, name, code);
		data.add(currentIndex, tableItem);

		index++;
		return currentIndex;
	}

	//Look up code by name
	public int LookupName(String name) {
		//Search code for given name lookup
		for (InternalData entry : data) {
			if (entry.name.compareToIgnoreCase(name) == 0) {
				return entry.code;
			} 
		}
		//Return Item Not In Table
		return NOT_FOUND;
	}

	//Look up name by code
	public String LookupCode(int code) {

		//Search name for given code lookup
		for (InternalData entry : data) {
			if (entry.code == code) {
				return entry.name;
			} 
		}

		//Return Not Found Empty String
		return "";
	}

	//Print table contents to specified file
	public void PrintReserveTable(String filename){

		try {
			FileOutputStream outputStream = new FileOutputStream(filename);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
			
			int stringPad = findMaxStringLength();
			String paddedIndexHeadString = pad(pad("Index",3,true),6,false);
			String paddedNameHeadString = pad(pad("Name",3,true),stringPad+3,false);
			String paddedCodeHeadString = pad(pad("Code",3,true),8,false);
			
			bufferedWriter.write(paddedIndexHeadString+ paddedNameHeadString + paddedCodeHeadString);
			bufferedWriter.newLine();
			
			for (InternalData entry : data) {
				String paddedIndexString = pad(pad(Integer.toString(entry.index),3,true),9,false);
				String paddedNameString = pad(pad(entry.name.toUpperCase(),3,true),stringPad+2,false);
				String paddedCodeString = pad(pad(Integer.toString(entry.code),3,true),8,false);

				bufferedWriter.write(paddedIndexString + paddedNameString + paddedCodeString);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}    
	}

	//Find longest name in table
	private int findMaxStringLength() {
		
		int maxLength = 0;
		
		for (InternalData entry : data) {
			if (entry.name.length() > maxLength) {
				maxLength = entry.name.length();
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

	//Data type for table items
	private static class InternalData{
		private final int index;
		private final String name;
		private final int code;

		public InternalData(int index, String name, int code) {
			super();
			this.index = index;
			this.name = name;
			this.code = code;
		}
	}
}


package algRead;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.solartis.exception.DatabaseException;
import com.solartis.util.common.DatabaseOperation;

public class Trial {
	
	public static DatabaseOperation db = new DatabaseOperation();
	
	public static void main(String args[]) throws DatabaseException {
		DatabaseOperation.ConnectionSetup("com.mysql.jdbc.Driver",
				"jdbc:mysql://192.168.84.225:3700/CommercialAuto_Development_ADMIN?useSSL=false", "root", "redhat");
		StringBuffer temp1 = new StringBuffer();
		LinkedHashMap<Integer, LinkedHashMap<String, String>> input =db.GetDataObjects("Select * from Output_FormSelection_Conditions");
		for (Entry<Integer, LinkedHashMap<String, String>> entry1 : input.entrySet())	
		{
			LinkedHashMap<String, String> inputrow = entry1.getValue();
			String s = inputrow.get("Condition");
			s=s.replaceAll("\\\".*?\\\"|\\'.*?\\'|`.*`", "");
			String[] words = s.split("\\W+");
			//System.out.println(words);
			for (int j=0; j<words .length; j++) {
				  System.out.println(words [j]);
				}
			//System.out.println(getWords(inputrow.get("Condition")));
		}
		DatabaseOperation.CloseConn();
	}
	
	public static List<String> getWords(String text) {
	    List<String> words = new ArrayList<String>();
	    BreakIterator breakIterator = BreakIterator.getWordInstance();
	    breakIterator.setText(text);
	    int lastIndex = breakIterator.first();
	    while (BreakIterator.DONE != lastIndex) {
	        int firstIndex = lastIndex;
	        lastIndex = breakIterator.next();
	        if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
	            words.add(text.substring(firstIndex, lastIndex));
	        }
	    }

	    return words;
	}
}

package algRead;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class trial2 {
	
	public static void main(String args[]) {
		extractbetweenbrackets();
		//extract();
	}

	public static void extractbetweenbrackets() {
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("[^()]+");
		Matcher regexMatcher = regex.matcher("Hello This is (Java)|(jawass)) Not (.NET)");

		while (regexMatcher.find()) {//Finds Matching Pattern in String
		   matchList.add(regexMatcher.group());//Fetching Group from String
		}

		for(String str:matchList) {
		   System.out.println(str);
		}
	}
	
	public static void extract() {
		String sample = "Hello This is (Java(jawass)) Not (.NET)";
		int endindex=sample.indexOf(')');
		String formula = sample.substring(0, endindex);
		int firstindex =formula.lastIndexOf('(');
		
		String extracted = sample.substring(firstindex, endindex).replace('(', '[');
		
		System.out.println(extracted+']');
	}
	
	public static List<String> extractbetweenbrackets(String text) {
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("(?<=\\()(?!\\s*\\()[^()]+");
		Matcher regexMatcher = regex.matcher(text);

		while (regexMatcher.find()) {//Finds Matching Pattern in String
		   matchList.add(regexMatcher.group());//Fetching Group from String
		}
		
		return matchList;

		/*for(String str:matchList) {
		   System.out.println(str);
		}*/
	}
}

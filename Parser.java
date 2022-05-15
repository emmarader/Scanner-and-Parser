package ConceptsProject2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser{
	
	static boolean ERROR = false;
	
	static final char DOT = '.';
	
	static ArrayList<String> contents = new ArrayList<String>();
	
	
	// SCAN FUNCTION
	static ArrayList<String> scan(){
		
		// get file
		Scanner scann = new Scanner(System.in);
		System.out.print("Enter Filename: ");
		String filename = scann.nextLine();
		File file = new File(filename);
		String str = "";
		Scanner sc;
		
		// read file and store data in string str
		try{
			sc = new Scanner(file);
			while(sc.hasNextLine()){
			   str = str + "\n" + sc.nextLine();
		   }
		   
		} catch(FileNotFoundException e){
		    	e.printStackTrace();
			} 
		
		char chars[];
		chars = str.toCharArray();
			
		// initialize stuff
		ArrayList<String> tokens = new ArrayList<String>();
		String id = "";
		String number = "";
		String colon = "";
    	String assign = ""; 	
    	String special = "";
    	String comment = "";
    	String endComment = "";
    	HashMap<String, String> specialChars = new HashMap<String, String>();
    	specialChars.put("(","lparen");
    	specialChars.put(")", "rparen");
    	specialChars.put("*", "times");
    	specialChars.put("+", "plus");
    	specialChars.put("-", "minus");
    	boolean inComment = false;
    	boolean inLineComment = false;
    	boolean error = false;
    	int dotCount = 0;
		
		//Loop to iterate through every char of str
		for(int i = 0; i < str.length(); i++){ 
			char ch = str.charAt(i);
		
		// check for comments
			// line comment
			if(ch == '/') {
				if(comment != ""){
					inLineComment = true;
				}
			}
			
			// end line comment
			if(inLineComment == true){
				if(chars[i] == '\n') {
					inLineComment = false;
				}
				comment = "";
			}
			
			// start block comment
			if(ch == '/'){
    			comment = comment + Character.toString(ch);
    		}
			
			// block comment
			if(ch == '*'){
				if(comment != ""){
					inComment = true;
				}
				comment = "";
			}

			// if division
			if (ch != '*' && ch != '/'){
				if(comment != "") {
					tokens.add("div");
					contents.add("div");
				}
				comment = "";
			}	

			// end block comment
    		if (inComment == true){
	    		if (ch == '*') {
	    			endComment = endComment + Character.toString(ch);
	    		}
	    		if (ch == '/') {
	    			if(endComment != ""){
	    				endComment = "";
	    				comment = "";
	    				inComment = false;
	    			}
	    		}
    		}	
    		
    	// if not in comment, find tokens and add to array 
    		if(inComment == false && inLineComment == false){
			
				// id/keywords to tokens
				if(Character.isLetter(ch)){
					id = id + Character.toString(ch);
					
				}
				if(Character.isDigit(ch)){
					if(id != ""){
						id = id + Character.toString(ch);
					}
				}
				if(Character.isLetter(ch) == false && Character.isDigit(ch) == false){
					if(id != "") {
						if(id.equals("read") ){
			    			tokens.add("read");
			    			contents.add("read");
			    			id = "";
			    		}
					}
					if(id != ""){	
			    		if(id.equals("write")) {
			    			tokens.add("write");
			    			contents.add("write");
			    			id = "";
			    		}
			    		else{
			    		tokens.add("id");
			    		
			    		id = id + Character.toString(ch);
			    		contents.add(id);
			    		}
					}
					id = "";	
				}
				
				// numbers to tokens
				if (id == ""){ 
					if (Character.isDigit(ch) || ch == DOT){
						number = number + Character.toString(ch);
						if(ch == DOT){
							dotCount += 1;
						}
						
					}
					if(Character.isDigit(ch) == false && ch != DOT){
						if(number != "") { 
							if (dotCount <= 1){
							tokens.add("number");
							contents.add(number);
							dotCount = 0;
							}
							else{
								error = true;
							}
						}
						number = "";	
					}
				}
				
				// special char to tokens
				if(ch == '(' || ch == ')'  |ch == '*' || ch == '+' || ch == '-'){
					special = Character.toString(ch);
					tokens.add(specialChars.get(special));
					contents.add(specialChars.get(special));
				}
				
				// assignment operator to token
				if(ch == ':'){
					colon = colon + ch;
				}
				if(ch == '='){
					if(colon != ""){
						assign = colon + ch;
						tokens.add("assign");
						contents.add("assign");
					}
					else{
						error = true;
					}
					assign = "";
					colon = "";
				}
				if(ch != '=' && ch != ':'){
					if(colon != "") {
						error = true;
					}
				}
				
				// invalid tokens
				if(ch == '!' || ch == '@' || ch == '#' || ch == '$' || ch == '%' || ch == '^' || ch == '&' 
						|| ch == '_' || ch == '~' || ch == '`' || ch == '?' || ch == '<' || ch == '>'
						|| ch == ';' || ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == '|' ){
					error = true;
				}
	
    		}
    		
		}
		
		tokens.add("$$"); 
		
		// if there are any invalid tokens, display error
		if(error == false){
			System.out.println(tokens);
			//System.out.println("contents: " + contents);
		}
		// otherwise, print the tokens
		else{
			System.out.println("error");
			ERROR = true;
		}

		return tokens;
	}
	
	
	// PARSE FUNCTION
	static void parse (ArrayList<String> parser) {
		if(parser.isEmpty() == false) {
			program(parser);
		}
	}
	
	//program 
	static void program(ArrayList<String> tok) { 
		//<program> : <stmt list> $$
		int len;
		len = tok.size();
		if(tok.isEmpty() == false) {
			if(tok.get(len-1) == "$$") {
				System.out.println("<Program>");
				stmt_list(tok);
				System.out.println("</Program>");
				System.exit(0);
			}
		}
	}
	
	//stmt_list 
	static void stmt_list(ArrayList<String> tok) { 
		// <stmt list> :  <stmt> <stmt list> 
		System.out.println("\t<stmt_list>");
		stmt(tok);
		System.out.println("\t</stmt_list>");
	}
	
	//stmt 
	static void stmt(ArrayList<String> tok) { 
		//<stmt> : id assign <expr> | read id | write <expr>
		System.out.println("\t\t<stmt>");
		
		int i;
		String partid = "", halfread = "", halfwrite = "";
		
		for(i = 0; i < tok.size(); i++) {
			String item = "";
		    item = tok.get(i);

			if(halfread != "") {
				String item2 = "";
			    item2 = tok.get(i);
			    if(item2.equals("id")) {
				    System.out.println("\t\t\t<read>");
				    System.out.println("\t\t\t	read");
				    System.out.println("\t\t\t</read>"); 
				    System.out.println("\t\t\t<id>");
				    System.out.println("\t\t\t	" + contents.get(i));
				    System.out.println("\t\t\t</id>");
			    }
			    halfread = "";
			}
			
			if(partid != "") {
				String item2 = "";
			    item2 = tok.get(i);
			    if(item2.equals("assign")) {
			        System.out.println("\t\t\t<assign>");
			        System.out.println("\t\t\t\t :=");
			        System.out.println("\t\t\t</assign>");
			        expr(tok);
			    }
			    partid = "";
			}
			
			if (halfwrite != "") {
				System.out.println("\t\t\t<write>");
				System.out.println("\t\t\t	write");
		    	System.out.println("\t\t\t</write>");
		    	halfwrite = "";
			}
					
			if(item.equals("id")) {
				partid = "id";
				System.out.println("\t\t\t<id>");
		        System.out.println("\t\t\t	" + contents.get(i));
		        System.out.println("\t\t\t</id>");
		    }
			
		    if(item.equals("read")) {
		    	halfread = "read";
		    }
		    
		    if(item.equals("write")) {
		        halfwrite = "write";
		    } 
		}
		System.out.println("\t\t</stmt>");			
	}
	
	//expr function 
	static boolean expr(ArrayList<String> tok) {
		//<expr> : <term> <term tail>
		System.out.println("\t\t\t<expr>");
		term(tok);
		term_tail(tok);
		System.out.println("\t\t\t\t</term_tail>");
		System.out.println("\t\t\t</expr>");
		return true;
	}
	
	//term_tail function 
	static boolean term_tail(ArrayList<String> tok) {
		//<term tail> : <add op> <term> <term tail> 
		System.out.println("\t\t\t\t<term_tail>");
		add_op(tok);
		return true;
	}
	
	//term function
	static boolean term(ArrayList<String> tok) { 
		// <term> : <factor> <fact tail>
		System.out.println("\t\t\t\t<term>");	
		factor(tok);
		fact_tail(tok);
		System.out.println("\t\t\t\t\t</fact_tail>");
		System.out.println("\t\t\t\t</term>");
		return true;
	}
	
	//fact_tail function 
	static boolean fact_tail(ArrayList<String> tok) { 
		//<fact tail> : <mult op> <factor> <fact tail>
		System.out.println("\t\t\t\t\t<fact_tail>");
		mult_op(tok);
		return true;	
	}
	
	//factor 
	static boolean factor(ArrayList<String> tok) { //calls expr
		//<factor> : lparen <expr> rparen | id | number
		int i;
        int breakpoint = 0;
        boolean istrue = false;
        String paren = "";
        System.out.println("\t\t\t\t\t<factor>");
        
        //lparen expr rparen
        for(i = 0; i < tok.size(); i++) {
            String item = "";
            item = tok.get(i);
            if(item.equals("lparen")) {
                System.out.println("\t\t\t\t\t\t<lparen>");
                System.out.println("\t\t\t\t\t\t (");
                System.out.println("\t\t\t\t\t\t</lparen>");
                paren = "(";
                breakpoint = i;
            }
        } 
       
        if(paren != "") {
            for(i = breakpoint; i < tok.size(); i++) {
                String item2 = "";
                item2 = tok.get(i);
                if(item2.equals("rparen")) {
                	 System.out.println("\t\t\t\t\t\t<rparen>");
                     System.out.println("\t\t\t\t\t\t )");
                     System.out.println("\t\t\t\t\t\t</rparen>");
                        istrue = true;
                }
            }
        }
            
        //id
        for(i = 0; i < tok.size(); i++) {
        	String id = "";
            id = tok.get(i);
            if(id.equals("id")) {
            	System.out.println("\t\t\t\t\t\t<id>");
        		System.out.println("\t\t\t\t\t\t	" + contents.get(i));
        		System.out.println("\t\t\t\t\t\t</id>");
        		istrue = true;
            }
        } 
              
       //number
       for(i = 0; i < tok.size(); i++) {
           String item = "";
           item = tok.get(i);
           if(item.equals("number")) {
        	   System.out.println("\t\t\t\t\t\t<number>");
               System.out.println("\t\t\t\t\t\t	" + contents.get(i));
               System.out.println("\t\t\t\t\t\t</number>");
               istrue = true;       
           }
       }
                 
       System.out.println("\t\t\t\t\t</factor>");
       if(istrue == true) {
    	   return true;
       }
       return false;
	}
	
	//add_op
	static boolean add_op(ArrayList<String> tok) {
		for(int i = 0; i < tok.size(); i ++) {
			String item = "";
			item = tok.get(i);
			if(item.equals("plus")) {
				System.out.println("\t\t\t\t\t\t<plus>");
				System.out.println("\t\t\t\t\t\t +");
				System.out.println("\t\t\t\t\t\t</plus>");
				return true;
				
			}
			if(item.equals("minus")) {
				System.out.println("\t\t\t\t\t\t<minus>");
				System.out.println("\t\t\t\t\t\t -");
				System.out.println("\t\t\t\t\t\t</minus>");
				return true;
			}
			
		}
		return false;
	}

	//mult_op
	static boolean mult_op(ArrayList<String> tok) {
		for(int i = 0; i < tok.size(); i ++) {
			String item = "";
			item = tok.get(i);
			if(item.equals("times")) {
				System.out.println("\t\t\t\t\t\t<times>");
				System.out.println("\t\t\t\t\t\t *");
				System.out.println("\t\t\t\t\t\t</times>");
				return true;
				
			}
			if(item.equals("div")) {
				System.out.println("\t\t\t\t\t\t<div>");
				System.out.println("\t\t\t\t\t\t /");
				System.out.println("\t\t\t\t\t\t</div>");
				return true;
			}
		}
		return false;
	}
	
	
	// MAIN FUNCTION
	public static void main(String[] args){
		
		ArrayList<String> scanResults = new ArrayList<String>();
		scanResults = scan();
		if(ERROR == false) {
			parse(scanResults);
		}
		
	}
}

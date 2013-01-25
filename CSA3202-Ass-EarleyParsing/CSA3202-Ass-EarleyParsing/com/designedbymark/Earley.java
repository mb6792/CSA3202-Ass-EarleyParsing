package com.designedbymark;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTree;

class Earley{
	public Hashtable grammar;			//datastructure to store the grammer
	public Chart charts[];				//datastructure to store the charts
	public Chart lexicon[];				//the lexicon
	public String sentenceArr[];		//sentence to be parsed
	Node parseTree;
	
	String sentence;
	
	private int c;
	private int c1;
	boolean found;
	
	public Earley(String grammarS, String sentenceS) throws IOException{
		sentenceArr = sentenceS.split(" ");
		int length = sentenceArr.length;
		
		this.grammar = new Hashtable();
		this.lexicon = new Chart[length];
		charts = new Chart[length+1];
		
		for (int i = 0; i < length; i++) {
			lexicon[i] = new Chart();
		}
		for (int i = 0; i <= length; i++) {
			charts[i] = new Chart();
		}
		
		sentence = sentenceS;
		c = -1;
		c1 = 0;
		found = false;
		
		EarleyParse(grammarS, sentenceArr);
	}
	
	public boolean getFound(){
		return found;
	}
	
	public Chart[] getCharts(){
		return charts;
	}
	
	public void EarleyParse(String grammarS, String sentenceSplit[]) throws IOException{
		FileReader fr1 = new FileReader(grammarS);
		FileReader fr2 = new FileReader(grammarS);
		BufferedReader br1 = new BufferedReader(fr1);
		BufferedReader br2 = new BufferedReader(fr2);
		
		String grammar = null;
		String tgrammar = null;
		
		Vector terminals = new Vector();
		Vector nonterminals = new Vector();
		Vector pos = new Vector();
		
		int length = sentenceArr.length;
		
		while((tgrammar = br1.readLine()) != null){
			grammar  = tgrammar.replace('|', '&');
			
			String tmpGrammar[] = grammar.split(" ");
			if(!nonterminals.contains(tmpGrammar[0])){
				nonterminals.addElement(tmpGrammar[0]);
			}
			
			String tmpGrammar1[] = grammar.split(" -> ");
			Vector grammarRule = new Vector();
			if(tmpGrammar1[1].indexOf('&') != -1){
				String tmpGrammar2[] = tmpGrammar1[1].split(" & ");
				for (int i = 0; i < tmpGrammar2.length; i++) {
					grammarRule.addElement(tmpGrammar2[i]);
				}
				this.grammar.put(tmpGrammar1[0], grammarRule);
			}else{
				grammarRule.addElement(tmpGrammar1[1]);
				this.grammar.put(tmpGrammar1[0], grammarRule);
			}
		}
		
		while((tgrammar = br2.readLine()) != null){
			grammar = tgrammar.replace('|', '&');
			String tempGrammar[] = grammar.split(" ");
			for (int i = 1; i < tempGrammar.length; i++) {
				if(!nonterminals.contains(tempGrammar[i])){
					if((!tempGrammar[i].equals("->"))&&(!tempGrammar[i].equals("&"))){
						terminals.addElement(tempGrammar[i]);
					}
				}
			}
		}
		
		//CONSTRUCTION OF THE LEXICON
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < nonterminals.size(); j++) {
				Vector v = (Vector) this.grammar.get(nonterminals.elementAt(j));
				if(v.indexOf(sentenceArr[i]) != -1){
					lexicon[i].table.addElement(nonterminals.elementAt(j));
				}
			}
		}
		
		//CONSTRUCTION OF PART OF SPEECH
		for (int i = 0; i < nonterminals.size(); i++) {
			boolean flag = false;
			Vector v = (Vector)this.grammar.get(nonterminals.elementAt(i));
			for (int j = 0; j < v.size(); j++) {
				String temp[] = ((String) v.elementAt(j)).split(" ");
				if(temp.length != 1){
					flag = true;
				}
				for (int k = 0; k < temp.length; k++) {
					if((!temp[k].equals("->"))&&(!temp[k].equals("&"))){
						if(!terminals.contains(temp[k])){
							flag = true;
						}
					}
				}
			}
			if(flag == false){
				pos.addElement(nonterminals.elementAt(i));
			}
		}
		 
		for (int i = 0; i < pos.size(); i++) {
			nonterminals.removeElement(pos.elementAt(i));
			this.grammar.remove(pos.elementAt(i));
		}
		
	//	Procedures p = new Procedures(this.grammar, lexicon, charts, sentenceArr, c);
		enqueue(new Rules(1,0,0,"@ S",'D'), 0, 0);
		for (int i = 0; i < (length + 1); i++) {
			int tc = charts[i].table.size();
			for (int j = 0; j < charts[i].table.size(); j++) {
				Rules tempRule = (Rules)charts[i].table.elementAt(j);
				if(tempRule.dot != tempRule.rule.size()){ 
					if((nonterminals.indexOf((String)tempRule.rule.elementAt(tempRule.dot))) != -1){
						predictor(tempRule);
					}else{
						if(i != length){
							scanner(tempRule);
						}
					}
				}else{
					if(charts[i].table.size() == 1){
						c++;
						tempRule.ruleNum = c;
					}
					completor(tempRule);
				}
			}
		}
		
		int ruleNum = -1;
		for (int i = 0; i < charts[charts.length-1].table.size(); i++) {
			Rules tempRules = (Rules)(charts[charts.length-1].table.elementAt(i));
			String s = (String) tempRules.rule.elementAt(0);
			if((s.equals("S")) & (tempRules.dot == tempRules.rule.size())){
				ruleNum = tempRules.ruleNum;
				i = charts[charts.length-1].table.size();
			}
		}
		if(ruleNum != -1){
			found = true;
			parseTree = tree(ruleNum);
		}
	}
	
	public void predictor(Rules rule){
		Vector v = (Vector) grammar.get(rule.rule.elementAt(rule.dot));
		for(int i=0; i<(v.size()); i++){
			String sentence = ((String) rule.rule.elementAt(rule.dot)) + " " + ((String)v.elementAt(i));
			Rules tempRule = new Rules(1, rule.end, rule.end, sentence, 'P');
			enqueue(tempRule,rule.end,0);
		}
	}
	
	public void scanner(Rules rule){
		for(int i=0; i<lexicon[rule.end].table.size(); i++){
			String tempRule = (String) rule.rule.elementAt(rule.dot);
			String tempLexi = (String) lexicon[rule.end].table.elementAt(i);
			if(tempRule.equals(tempLexi)){
				String inRule = ((String)lexicon[rule.end].table.elementAt(i)) + " " + sentenceArr[rule.end];
				Rules r = new Rules(2, rule.end, rule.end+1, inRule, 'S');
				enqueue(r, rule.end+1, 1);
			}
		}
	}
	
	public void completor(Rules rule){
		for(int i=0; i<charts[rule.start].table.size(); i++){
			Rules tempRule = (Rules) charts[rule.start].table.elementAt(i);
			if(tempRule.rule.size() != tempRule.dot){
				String ruleS = (String) rule.rule.elementAt(0);
				String tempRuleS = (String) tempRule.rule.elementAt(tempRule.dot);
				if(ruleS.equals(tempRuleS)){
					Vector v = (Vector) tempRule.pointer.clone();
					v.addElement(new Integer(rule.ruleNum));
					Rules r = new Rules(tempRule.dot+1, tempRule.start, rule.end, tempRule.rule, v, 'C');
					enqueue(r, rule.end, 2);
				}
			}
		}
	}

	public void enqueue(Rules r, int i, int check){
		String ts1 = null;
		for(int c=0; c<r.rule.size(); c++){
			if(c == 0){
				ts1 = (String) r.rule.elementAt(c);
			}else{
				ts1 += " " + ((String) r.rule.elementAt(c));
			}
		}
		
		boolean flag = false;
		String ts3 = null;
		int temp = charts[i].table.size();
		for (int j = 0; j < charts[i].table.size(); j++) {
			Rules tempRules = (Rules) charts[i].table.elementAt(j);
			String ts2 = null;
			
			for (int k = 0; k < tempRules.rule.size(); k++) {
				if(k == 0){
					ts2 = (String)tempRules.rule.elementAt(k);
					ts3 = (String)tempRules.rule.elementAt(k);
				}else{
					ts2 += " " + ((String) tempRules.rule.elementAt(k));
				}
				
				if((r.dot == tempRules.dot) && (ts1.equals(ts2))){
					flag = true;
					if(check == 2){
						if(tempRules.pointer.size() == r.pointer.size()){
							boolean f1 = false;
							for (int l = 0; l < tempRules.pointer.size(); l++) {
								if(((Integer)tempRules.pointer.elementAt(l)).intValue() != ((Integer)r.pointer.elementAt(l)).intValue()){
									f1 = true;
								}
							}
							if(f1 == true){
								flag = false;
							}
						}
					}
					
					k = charts[i].table.size();
				}
			}
		}
		if(flag == false){
			if((check == 0) || (check == 2)){
				c += 1;
				r.ruleNum = c;
			}
			charts[i].table.addElement(r);
		}
	}
	
	public Node tree(int ruleNum){
		for (int i = 0; i < charts.length; i++) {
			for (int j = 0; j < charts[i].table.size(); j++) {
				Rules tempRule = (Rules)charts[i].table.elementAt(j);
				if (tempRule.ruleNum == ruleNum) {
					Node node = new Node((String) tempRule.rule.elementAt(0));
					boolean flag = false;
					for (int k = tempRule.pointer.size()-1; k >= 0; k--) {
						flag = true;
						Node tempNodeAdd = tree(((Integer)tempRule.pointer.elementAt(k)).intValue());
						node.pointers.addElement(tempNodeAdd);
					}
					if (flag == false) {
						Node tempNodeAdd = new Node((String)tempRule.rule.elementAt(1)); 
						node.pointers.addElement(tempNodeAdd);
					}
					return node;
				}
			}
		}
		return new Node();
	}
	
	public Node getParseTree(){
		return parseTree;
	}
}

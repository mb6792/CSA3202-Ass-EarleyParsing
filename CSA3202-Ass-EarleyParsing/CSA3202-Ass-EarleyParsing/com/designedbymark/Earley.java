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
	public Hashtable grammar;		//datastructure to store the grammer
	public chart charts[];				//datastructure to store the charts
	public chart lexicon[];			//the lexicon
	public String sentenceArr[];		//sentence to be parsed
	
	String sentence;
	
	private int c;
	private int c1;
	boolean found;
	
	node ptree;							//parsetree
	
	public Earley(String grammarS, String sentenceS) throws IOException{
		String sentenceSplit[] = sentenceS.split(" ");
		int length = sentenceSplit.length;
		
		this.grammar = new Hashtable();
		this.lexicon = new chart[length];
		charts = new chart[length+1];
		
		for (int i = 0; i < length; i++) {
			lexicon[i] = new chart();
			charts[i] = new chart();
		}
		
		sentenceArr = new String[length];
		sentence = sentenceS;
		c = -1;
		c1 = 0;
		found = false;
		
		EarleyParse(grammarS, sentenceSplit);
		
		if(found == true){
			
		}else{
			
		}
	}
	
	public void EarleyParse(String grammarS, String sentenceSplit[]) throws IOException{
		FileReader fr = new FileReader(grammarS);
		BufferedReader br1 = new BufferedReader(fr);
		BufferedReader br2 = new BufferedReader(fr);
		
		String grammar = null;
		String tgrammar = null;
		
		Vector terminals = new Vector();
		Vector nonterminals = new Vector();
		Vector pos = new Vector();
		
		int length = sentenceSplit.length;
		
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
					terminals.addElement(tempGrammar[i]);
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
					if((!temp[k].equals("->"))|(!temp[k].equals("&"))){
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
		
		Procedures p = new Procedures(this.grammar, lexicon, charts, sentenceArr, c);
		p.enqueue(new rules(1,0,0,"@ S",'D'), 0, 0);
		
		for (int i = 0; i < (length + 1); i++) {
			for (int j = 0; j < charts[i].table.size(); j++) {
				rules tempRule = (rules)charts[i].table.elementAt(j);
				if(tempRule.dot != tempRule.rule.size()){
					if((nonterminals.indexOf((String) tempRule.rule.elementAt(tempRule.dot))) != -1){
						p.predictor(tempRule);
					}else if(i != length){
						p.scanner(tempRule);
					}
				}else{
					if(charts[i].table.size() == 1){
						c++;
						tempRule.ruleNum = c;
					}
					p.completor(tempRule);
				}
			}
		}
		
		int ruleNum = -1;
		for (int i = 0; i < charts[charts.length-1].table.size(); i++) {
			rules tempRules = (rules)(charts[charts.length-1].table.elementAt(i));
			String s = (String) tempRules.rule.elementAt(0);
			if((s.equals("S")) & (tempRules.dot == tempRules.rule.size())){
				ruleNum = tempRules.ruleNum;
				i = charts[charts.length-1].table.size();
			}
		}
		if(ruleNum != -1){
			found = true;
		}
	}
}

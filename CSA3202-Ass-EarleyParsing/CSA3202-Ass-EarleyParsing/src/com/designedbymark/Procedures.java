package com.designedbymark;

import java.util.Hashtable;
import java.util.Vector;

public class Procedures {
	Hashtable grammar;
	chart lexicon[];
	chart charts[];
	String sentence[];
	private static int counter;
	
	public Procedures(Hashtable grammar, chart lexicon[], chart charts[], String sentence[], int counter){
		this.grammar = grammar;
		this.lexicon = lexicon;
		this.charts = charts;
		this.sentence = sentence;
		this.counter = counter;
	}
	
	public void predictor(rules rule){
		Vector v = (Vector) grammar.get(rule.rule.elementAt(rule.dot));
		for(int i=0; i<(v.size()); i++){
			String sentence = ((String) rule.rule.elementAt(rule.dot)) + " " + ((String)v.elementAt(i));
			rules tempRule = new rules(1, rule.end, rule.end, sentence, 'P');
			enqueue(tempRule,rule.end,0);
		}
	}
	
	public void scanner(rules rule){
		for(int i=0; i<lexicon[rule.end].table.size(); i++){
			String tempRule = (String) rule.rule.elementAt(rule.dot);
			String tempLexi = (String) lexicon[rule.end].table.elementAt(i);
			if(tempRule.equals(tempLexi)){
				String inRule = ((String)lexicon[rule.end].table.elementAt(i)) + " " + sentence[rule.end];
				rules r = new rules(2, rule.end, rule.end+1, inRule, 'S');
				enqueue(r, rule.end+1, 1);
			}
		}
	}
	
	public void completor(rules rule){
		for(int i=0; i<charts[rule.start].table.size(); i++){
			rules tempRule = (rules) charts[rule.start].table.elementAt(i);
			if(tempRule.rule.size() != tempRule.dot){
				String ruleS = (String) rule.rule.elementAt(0);
				String tempRuleS = (String) tempRule.rule.elementAt(tempRule.dot);
				if(ruleS.equals(tempRuleS)){
					Vector v = (Vector) tempRule.pointer.clone();
					v.addElement(new Integer(rule.ruleNum));
					rules r = new rules(tempRule.dot+1, tempRule.start, tempRule.end, tempRule.rule, v, 'C');
					enqueue(r, rule.end, 2);
				}
			}
		}
	}

	public void enqueue(rules r, int i, int check){
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
		for (int j = 0; j < charts[i].table.size(); j++) {
			rules tempRules = (rules) charts[i].table.elementAt(j);
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
			
			if(flag == false){
				if((check == 0) | (check == 2)){
					counter += 1;
					r.ruleNum = counter;
				}
				charts[i].table.addElement(r);
			}
		}
	}
}

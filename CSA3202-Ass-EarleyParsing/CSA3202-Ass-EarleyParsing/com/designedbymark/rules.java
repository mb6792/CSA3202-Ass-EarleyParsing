package com.designedbymark;

import java.util.Vector;

public class rules {
	int dot, start, end, ruleNum;
	Vector rule, pointer;
	char ch;
	
	public rules(){
		rule = new Vector();
		pointer = new Vector();
	}
	
	public rules(int dot, int start, int end, String sent, char ch){
		String s[] = sent.split(" ");
		this.dot = dot;
		this.start = start;
		this.end = end;
		this.ch = ch;
		
		rule = new Vector();
		pointer = new Vector();
		for(int i = 0; i<s.length; i++){
			rule.addElement(s[i]);
		}
	}
	
	public rules(int dot, int start, int end, Vector ruleVect, Vector pointerVect, char ch){
		this.dot = dot;
		this.start = start;
		this.end = end;
		this.rule = ruleVect;
		this.pointer = pointerVect;
		this.ch = ch;
	}
}

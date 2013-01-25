package com.designedbymark;

import java.util.Vector;

public class Node {
	String name;
	Vector pointers;
	
	public Node(){
		pointers = new Vector();
	}
	
	public Node(String s){
		name = s;
		pointers = new Vector();
	}
}
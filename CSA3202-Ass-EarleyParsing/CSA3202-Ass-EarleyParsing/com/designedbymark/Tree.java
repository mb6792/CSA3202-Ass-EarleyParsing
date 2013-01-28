package com.designedbymark;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTree;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class Tree extends JFrame {

	private JPanel contentPane;

	Node parseTree;
	Vector<Node> parseTrees = new Vector<Node>();
	
	public Tree(Vector<Node> parseTrees) {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 360, 537);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		this.parseTrees = parseTrees;
		
		//this.parseTree = parseTree;
		
		drawTree();
	}
	
	public void drawTree(){
		DefaultMutableTreeNode top = new DefaultMutableTreeNode();
		
		for (int i = 0; i < parseTrees.size(); i++){
			parseTree = parseTrees.get(i);
			
			if(parseTrees.size() == 0){
				top = getNodes(top, parseTree);
			}else{
				DefaultMutableTreeNode top2 = new DefaultMutableTreeNode(parseTrees.size());
				top = getNodes(top2, parseTree);
			}
		}
		
		contentPane.setLayout(new BorderLayout(0, 0));
		
		final JTree tree = new JTree(top);
		contentPane.add(tree);
	}
	
	public DefaultMutableTreeNode getNodes(DefaultMutableTreeNode top, Node n){
		top = new DefaultMutableTreeNode(n.name);
		if(n.pointers.size() > 0){
			for (int i = 0; i < n.pointers.size(); i++) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode();
				top.add(getNodes(node, (Node) n.pointers.elementAt(i)));
			}
		}
		return top;
	}
}
//the flower is nice Mary in Richard

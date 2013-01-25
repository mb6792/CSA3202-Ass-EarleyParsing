package com.designedbymark;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTree;

public class Tree extends JFrame {

	private JPanel contentPane;

	Node parseTree;
	
	public Tree(Node parseTree) {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 763, 537);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		this.parseTree = parseTree;
		
		drawTree();
	}
	
	public void drawTree(){
		DefaultMutableTreeNode top = new DefaultMutableTreeNode();
		top = getNodes(top, parseTree);
		
		JTree tree = new JTree(top);
		contentPane.add(tree, BorderLayout.CENTER);
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

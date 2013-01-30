package com.designedbymark;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.Font;
import javax.swing.ListSelectionModel;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTable chartsTable;
	private JTextField importPathTF;
	private JTextField txtInputString;
	
	private Earley e = null;
	
	private int lastRuleNo;

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		setTitle("Human Language Technologies Assignment 2012/13 - Mark Bonnici\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 552, 868);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel centrePanel = new JPanel();
		contentPane.add(centrePanel, BorderLayout.CENTER);
		centrePanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		centrePanel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblGrammarFile = new JLabel("Grammar File:");
		panel.add(lblGrammarFile, BorderLayout.WEST);
		
		importPathTF = new JTextField();
		panel.add(importPathTF, BorderLayout.CENTER);
		importPathTF.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.showOpenDialog(MainFrame.this);
				File selImpFile = fc.getSelectedFile();
				importPathTF.setText(selImpFile.toString());
			}
		});
		panel.add(btnBrowse, BorderLayout.EAST);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		txtInputString  = new JTextField();
		txtInputString.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_2.add(txtInputString, BorderLayout.CENTER);
		txtInputString.setText("Input String");
		txtInputString.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		panel_2.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton btnRecognize = new JButton("Recognize");
		btnRecognize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				recGrammar(true);
			}
		});
		panel_1.add(btnRecognize);
		
		JButton btnParse = new JButton("Parse");
		btnParse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean found = recGrammar(false);
				
				if(found){
					Tree t = new Tree(e.getParseTrees());
					t.setVisible(true);
				}else{
					JOptionPane.showMessageDialog(null, "Sentence not in L(G)", "Not Found", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panel_1.add(btnParse);
		
		JPanel bottomPanel = new JPanel();
		centrePanel.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		centrePanel.add(scrollPane, BorderLayout.CENTER);
		
		chartsTable = new JTable();
		scrollPane.setViewportView(chartsTable);
	}
	
	public boolean recGrammar(boolean displayMessage){
		String grammarFile = importPathTF.getText();
		String sentence = txtInputString.getText();
		
		long startTime = System.nanoTime();
		try {
			e = new Earley(grammarFile, sentence);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error in Earley Parsing", "Error", JOptionPane.ERROR_MESSAGE);
		}
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		double timeTaken  = (double)duration / 1000000000;
		
		if(displayMessage){
			if(e.getFound()){
				JOptionPane.showMessageDialog(null, "This String is in L(G)");
			}else{
				JOptionPane.showMessageDialog(null, "Not A Member");
			}
		}
		
		fillChartsTable(e.getCharts());
		
		System.out.println("Performance Statistics for '" + sentence + "':");
		System.out.println("Dotted Rules Instances Created: " + lastRuleNo);
		System.out.println("Useless Rules: ");
		System.out.println("Duration: " + timeTaken + " seconds");
		System.out.println();
		
		return e.getFound();
	}

	public void fillChartsTable(Chart[] charts){
		DefaultTableModel tableModel = new DefaultTableModel();

		String[] columnNames = { "State No.", "Production", "Origin", "[S]", "Comment" };
		tableModel.setColumnIdentifiers(columnNames);

		for (int i = 0; i < charts.length; i++) {
			String[] chartNoRow =  {"CHART: " + i, "","","",""};
			tableModel.addRow(chartNoRow);
			for (int j = 0; j < charts[i].table.size(); j++) {
				int stateNo;
				String prod, origin, comment, s;
				
				Rules tempRule = (Rules)charts[i].table.elementAt(j);
				
				String ruleNum = "S" + tempRule.ruleNum;
				lastRuleNo = tempRule.ruleNum;
				
				prod = tempRule.rule.elementAt(0) + " -> ";
				for (int k = 1; k < tempRule.dot; k++) {
					prod += tempRule.rule.elementAt(k);
				}
				prod += ".";
				for (int k = tempRule.dot; k < tempRule.rule.size(); k++) {
					prod += " " + tempRule.rule.elementAt(k);
				}
				
				origin = "[" + tempRule.start + ", " + tempRule.end + "]";
				
				s = "[";
				if(tempRule.pointer.size() > 0){
					for (int k = 0; k < tempRule.pointer.size(); k++) {
						s += "S"+tempRule.pointer.elementAt(k);
						if (k != (tempRule.pointer.size()-1)) {
							s += ", ";
						}
					}
				}
				s += "]";
				
				switch(tempRule.ch){
					case 'C' : comment = "Completor"; break;
					case 'D' : comment = "Dummy Start State"; break;
					case 'P' : comment = "Predictor"; break;
					case 'S' : comment = "Scanner"; break;
					default : comment = "INVALID!"; break;
				}
				
				String[] rowData = {ruleNum, prod, origin, s, comment};
				tableModel.addRow(rowData);
			}
		}
		
		chartsTable.setModel(tableModel);
	}
}

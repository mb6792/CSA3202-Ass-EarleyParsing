package com.designedbymark;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Font;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTable grammarTable;
	private JTextField importPathTF;
	private JTextField txtInputString;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Human Language Technologies Assignment 2012/13 - Mark Bonnici\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
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
				String grammarFile = importPathTF.getText();
				String sentence = txtInputString.getText();
				Earley e = null;
				try {
					e = new Earley(grammarFile, sentence);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Error in Earley Parsing", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
				if(e.getFound()){
					JOptionPane.showMessageDialog(null, "This String is in L(G)");
				}else{
					JOptionPane.showMessageDialog(null, "Not A Member");
				}
			}
		});
		panel_1.add(btnRecognize);
		
		JButton btnParse = new JButton("Parse");
		btnParse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		panel_1.add(btnParse);
		
		JPanel bottomPanel = new JPanel();
		centrePanel.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		centrePanel.add(scrollPane, BorderLayout.CENTER);
		
		grammarTable = new JTable();
		grammarTable.setAutoCreateRowSorter(true);
		scrollPane.setViewportView(grammarTable);
	}

}

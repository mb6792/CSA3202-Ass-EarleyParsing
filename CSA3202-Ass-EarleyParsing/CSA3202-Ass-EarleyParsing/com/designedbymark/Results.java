package com.designedbymark;

public class Results {

	public void getChartsString(Chart[] charts){
		String outputString = "";
		for (int i = 0; i < charts.length; i++) {
			outputString += "CHART: " + i + "\n";
			for (int j = 0; j < charts[i].table.size(); j++) {
				Rules tempRule = (Rules)charts[i].table.elementAt(j);
				
			}
		}
		
	}

}

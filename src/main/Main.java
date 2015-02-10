package main;

import java.io.File;
import java.util.ArrayList;

import pdfHelper.ExtractionInformation;
import bddHelper.Distance;
import bddHelper.FormatageCitation;

public class Main {

	public static void main(String args[]){



		ArrayList<String> texte=new ArrayList<String>();

		//main pour 3 cas
		//texte.add("9012.pdf");
		//texte.add("94164.pdf");
		//texte.add("98321.pdf");

		//main pour tout (en supposant le repertoire d'article dans le projet)
		/*
		String directoryName="lib";
		File directory = new File(directoryName);
		String[] allFileName = directory.list();
		for (int i = 0; i < allFileName.length; i++) {
			System.out.println(allFileName[i].substring(allFileName[i].lastIndexOf(".")));
			if(!allFileName[i].substring(allFileName[i].lastIndexOf(".")).equals(".txt")){
		texte.add(directoryName + "/" + allFileName[i]);
			}
		}
*/

	}

}

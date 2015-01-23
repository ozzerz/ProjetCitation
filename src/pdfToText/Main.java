package pdfToText;

import java.io.File;
import java.util.ArrayList;

import bddHelper.FormatageCitation;

public class Main {

	public static void main(String args[]){



		ArrayList<String> texte=new ArrayList<String>();

		//main pour 3 cas
		//texte.add("9012.pdf");
		//texte.add("94164.pdf");
		//texte.add("98321.pdf");

		//main pour tout
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
		EnregistrementTexte en=new EnregistrementTexte(texte);
		FormatageCitation fc=new FormatageCitation("bdd.xml");
		//fc.getAuteurCitation("1. Je suis un aigle.","unique_name_raw_persee.csv");
		fc.mettreEnForme("Basil", "B.");
	}

}

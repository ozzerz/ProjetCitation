package pdfToText;

import java.util.ArrayList;

public class Main {

	public static void main(String args[]){


		ArrayList<String> texte=new ArrayList<String>();
		texte.add("9012.pdf");
		texte.add("94164.pdf");
		texte.add("98321.pdf");
		EnregistrementTexte en=new EnregistrementTexte(texte);

	}

}

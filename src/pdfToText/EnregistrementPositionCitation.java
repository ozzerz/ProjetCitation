package pdfToText;

import java.util.ArrayList;

public class EnregistrementPositionCitation {
	/**
	 * La liste contenant tout le texte a l'éxception des citations
	 */
	ArrayList<String> resteDuTexte;

	public EnregistrementPositionCitation(ArrayList<String> resteDuTexte){
		this.resteDuTexte=resteDuTexte;
	}

	//Dans cette classe on utilisera l'ensemble des paragraphe restant pour trouvé OU sont cité les citations
}

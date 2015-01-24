package bddHelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cette classe a pour but de permettre de trouvé les erreurs possible dans
 * l'ensemble des citations de la DB
 *
 * @author Ozzerz
 *
 */
public class GestionErreur {

	/**
	 * On stockera l'ensemble des numero
	 */
	public ArrayList<String> numero;

	public GestionErreur() {
		numero = new ArrayList<String>();

	}

	/**
	 * On considére qu'une citation est complete si elle commence par X. et
	 * termine par un .
	 *
	 * @param citation
	 * @return true si complete , false sinon
	 */
	public boolean citationComplete(String citation) {


		if (citation.indexOf(".") >= 3||citation.indexOf(".") == -1) {
			return false;
		}
		// on doit verifier qu'avant le point il s'agit d'un numero
		else {
			String numero = citation.substring(0, citation.indexOf(".")).trim();
			Pattern p = Pattern.compile("^[a-z0-9]*");
			Matcher m = p.matcher(numero);
			if (!m.matches()) {
				return false;// la citation ne commence pas par un "numéro"
			}
		}

		citation = citation.trim();
		return ((citation.charAt(citation.length() - 1)) == ('.'));// il reste a
																	// verifier
																	// si la
																	// citation
																	// termine
																	// par un
																	// point

	}

	/**
	 * Ici on stockera l'ensemble des "numeros"
	 *
	 * @param citations
	 */
	public void stockNumero(ArrayList<String> citations) {

		for (int i = 0; i < citations.size(); i++) {
			if (citations.get(i).indexOf(".") < 3) { // alors on est bien dans
														// une citation bien
														// formé (même si elle n'est pas complete)
				numero.add(citations.get(i)
						.substring(0, citations.get(i).indexOf(".")).trim());
			}
		}

	}

	/**
	 * Dans cette fonction on renverra le numero des citations qu'il semble ne
	 * peut pas être présente Exemple si l'on a <1. blabla, 3.blabla> on
	 * renverra <2>
	 *
	 * @return le String permettant de savoir
	 */
	public ArrayList<Integer> numeroManquant() {
		ArrayList<Integer> numeroManquant = new ArrayList<Integer>();// contiendra
																		// la
																		// liste
																		// des
																		// numero
																		// manquant
		int numeroRecherche = 1;// a la base on cherche la premiere citation
		String num;// le numero sous forme de String
		int numI;// le numero sous forme d'entier si c'est un entier
		Matcher m;
		Pattern p = Pattern.compile("[0-9]+");// contient au moins un nombre

		for (int i = 0; i < numero.size(); i++) {
			num = numero.get(i);
			m = p.matcher(num);
			if (m.matches()) {
				numI = Integer.parseInt(num);
				if (numI == numeroRecherche) {
					numeroRecherche++;// on cherchera donc le prochain numero
				} else// dans le cas ou on a pas le numéro voulu
				{
					for (int j = numeroRecherche; j < numI; j++) {
						numeroManquant.add(j);

					}
					numeroRecherche = numI + 1;// on doit donc chercher le
												// numero suivant
				}

			} else {
				numeroRecherche++;// on considére que si l'on a pas le numéro
									// c'est une erreur d'OCR
			}

		}

		return numeroManquant;
	}

	/**
	 * On renvoie la liste des numero des citations qui apparaissent plusieurs
	 * fois Exemple si l'on a <1. blabla, 1.eaeea> on Renverra <1>
	 *
	 * @return
	 */
	public ArrayList<String> numeroDuplique() {
		ArrayList<String> numeroDuplique = new ArrayList<String>();// contiendra
																	// la liste
																	// des
																	// numero
																	// dupliqué
		ArrayList<String> copie = new ArrayList<String>(numero);
		String numEnCour;
		while (copie.size() > 1) {
			numEnCour = copie.get(0);// on prend le premier element
			copie.remove(0);// on le supprime de la liste
			if (copie.contains(numEnCour)) {
				numeroDuplique.add(numEnCour);
				// tant que la liste contiens le numero duplique on le supprime
				while (copie.contains(numEnCour) && !copie.isEmpty()) {
					copie.remove(copie.indexOf(numEnCour));// on le supprime de
															// la liste
				}

			}
			// si elle ne contiens pas le numero il n'y a rien a faire
		}

		return numeroDuplique;
	}

}

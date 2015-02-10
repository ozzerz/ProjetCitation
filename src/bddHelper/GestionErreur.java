package bddHelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cette classe a pour but de permettre de trouver les erreurs possibles dans
 * l'ensemble des citations de la BDD
 *
 * @author Ozzerz
 *
 */
public class GestionErreur {

	/**
	 * On stockera l'ensemble des numéros
	 */
	public ArrayList<String> numero;

	public GestionErreur() {
		numero = new ArrayList<String>();

	}

	/**
	 * On considére qu'une citation est complète si elle commence par X. et
	 * termine par un .
	 *
	 * @param citation
	 * @return true si complète , false sinon
	 */
	public boolean citationComplete(String citation) {


		if (citation.indexOf(".") >= 3||citation.indexOf(".") == -1) {
			return false;
		}
		// on doit vérifier qu'avant le point il s'agit d'un numéro
		else {
			String numero = citation.substring(0, citation.indexOf(".")).trim();
			Pattern p = Pattern.compile("^[a-z0-9]*");
			Matcher m = p.matcher(numero);
			if (!m.matches()) {
				return false;// la citation ne commence pas par un "numéro"
			}
		}

		citation = citation.trim();
		return ((citation.charAt(citation.length() - 1)) == ('.'));// il reste à
																	// vérifier
																	// si la
																	// citation
																	// termine
																	// par un
																	// point

	}

	/**
	 * Ici on stockera l'ensemble des "numéros"
	 *
	 * @param citations
	 */
	public void stockNumero(ArrayList<String> citations) {

		for (int i = 0; i < citations.size(); i++) {
			if (citations.get(i).indexOf(".") < 3) { // alors on est bien dans
														// une citation bien
														// formée (même si elle n'est pas complète)
				numero.add(citations.get(i)
						.substring(0, citations.get(i).indexOf(".")).trim());
			}
		}

	}

	/**
	 * Dans cette fonction on renverra le numéro des citations qui semble ne
	 * pas être présente Exemple si l'on a <1. blabla, 3.blabla> on
	 * renverra <2>
	 *
	 * @return une liste d'entier des numéros manquants
	 */
	public ArrayList<Integer> numeroManquant() {
		ArrayList<Integer> numeroManquant = new ArrayList<Integer>();// contiendra
																		// la
																		// liste
																		// des
																		// numéros
																		// manquants
		int numeroRecherche = 1;// a la base on cherche la première citation
		String num;// le numéro sous forme de String
		int numI;// le numéro sous forme d'entier si c'est un entier
		Matcher m;
		Pattern p = Pattern.compile("[0-9]+");// contient au moins un nombre

		for (int i = 0; i < numero.size(); i++) {
			num = numero.get(i);
			m = p.matcher(num);
			if (m.matches()) {
				numI = Integer.parseInt(num);
				if (numI == numeroRecherche) {
					numeroRecherche++;// on cherchera donc le prochain numéro
				} else// dans le cas ou on a pas le numéro voulu
				{
					for (int j = numeroRecherche; j < numI; j++) {
						numeroManquant.add(j);

					}
					numeroRecherche = numI + 1;// on doit donc chercher le
												// numéro suivant
				}

			} else {
				numeroRecherche++;// on considére que si l'on n'a pas le numéro
									// c'est une erreur d'OCR
			}

		}

		return numeroManquant;
	}

	/**
	 * On renvoie la liste des numéros des citations qui apparaissent plusieurs
	 * fois Exemple si l'on a <1. blabla, 1.eaeea> on renverra <1>
	 *
	 * @return
	 */
	public ArrayList<String> numeroDuplique() {
		ArrayList<String> numeroDuplique = new ArrayList<String>();// contiendra
																	// la liste
																	// des
																	// numéros
																	// dupliqués
		ArrayList<String> copie = new ArrayList<String>(numero);
		String numEnCour;
		while (copie.size() > 1) {
			numEnCour = copie.get(0);// on prend le premier élèment
			copie.remove(0);// on le supprime de la liste
			if (copie.contains(numEnCour)) {
				numeroDuplique.add(numEnCour);
				// tant que la liste contient le numéro dupliqué on le supprime
				while (copie.contains(numEnCour) && !copie.isEmpty()) {
					copie.remove(copie.indexOf(numEnCour));// on le supprime de
															// la liste
				}

			}
			// si elle ne contient pas le numéro il n'y a rien à faire
		}

		return numeroDuplique;
	}

}

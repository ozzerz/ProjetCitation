package bddHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

/**
 * Dans cette classe on changera les citations pour atteindre un format plus
 * intéressant
 *
 * @author Ozzerz
 *
 */
public class FormatageCitation {

	/**
	 * le document de la BDD
	 */
	private Document doc;

	public FormatageCitation(String nomDb) {
		File monFichier = new File(nomDb);

		// creation de fichier de la BDD si il n'existe pas
		try {
			if (monFichier.exists()) {
				SAXBuilder sxb = new SAXBuilder();
				doc = sxb.build(new File("bdd.xml"));
			}
		} catch (Exception e) {

		}

	}

	/**
	 * On formatera la citation
	 *
	 * @param citation
	 */
	public void formatCitation(String citation) {

	}

	/**
	 * Permettra d'apeller formatCitation sur toute les citations
	 */
	public void formatAllCitations() {
		// pour chaque citation
		// formatCitation(citation)

	}

	/**
	 * Cette methode a pour but de trouver l'auteur d'une citation si il existe
	 * pour cette methode il faudra utiliser La distance d'édition
	 *
	 * @see Distance
	 * @param citation
	 *            la citation
	 * @param fichierNom
	 *            le nom du fichier contenant l'ensembles des noms d'auteurs
	 * @return l'auteur si il existe , null sinon
	 */
	public String getAuteurCitation(String citation, String fichierNom) {
		String auteur = null;
		String nom;// ce que l'on pense pouvoir etre le nom de l'auteur
		String prenom;// ce que l'on pense pouvoir etre le prenom de l'auteur
		String ligne;// la ligne que l'on va lire
		String nomAuteur;// le nom de l'auteur dans le fichier
		String prenomAuteur;// le prenom de l'auteur dans le fichier
		String[] tempo;// serivra a repartir le nom et le prenom de l'auteur
		Distance di = new Distance();// permettra de lancer le calcul d'édition
		File lesAuteurs = new File(fichierNom);// le fichier avec tout les
												// auteurs
		InputStream ips;
		// une citation termine tout par un . on va le supprimer
		citation = citation.substring(0, citation.length() - 1);
		boolean trouve = false;
		try {
			ips = new FileInputStream(lesAuteurs);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			if (citation.indexOf(".") != -1) {
				citation = citation.substring(citation.indexOf(".") + 1).trim();
				String[] lesMots = citation.split(" ");
				// pour chaque mot
				for (int i = 0; i < lesMots.length - 1; i++) {
					nom = lesMots[i];
					prenom = lesMots[i + 1];
					while ((ligne = br.readLine()) != null && !trouve) {
						tempo = ligne.split(" ");
						// on recupére les nom et on remet en forme
						nomAuteur = tempo[0];
						prenomAuteur = tempo[1];
						nom = mettreEnForme(nom, nomAuteur);

						prenom = mettreEnForme(prenom, prenomAuteur);
						// il est possible que le . soit dans la citation est
						// pas dans la DB
						// dans ce cas il faut modifier

						nomAuteur = mettreEnForme(nomAuteur, nom);
						prenomAuteur = mettreEnForme(prenomAuteur, prenom);

						// si le prenom ou le nom est de taille 2 et que l'on a
						// pas le même alors ce n'est pas l'auteur
						if (prenom.length() <= 2 || nom.length() <= 2) {
							if (prenom.length() <= 2) {
								if (prenom.equals(prenomAuteur)) {
									if (di.LevenshteinDistance(nom, nomAuteur) <= fonctionTaille(nom)) { // on
																											// a
																											// l'auteur
										trouve = true;
										auteur = nomAuteur + " " + prenomAuteur;
									}

								}
								// on sais que ce n'est pas l'auteur

							} else {
								if (nom.equals(nomAuteur)) {
									if (di.LevenshteinDistance(prenom,
											prenomAuteur) <= fonctionTaille(prenom)) { // on
																						// a
																						// l'auteur
										trouve = true;
										auteur = nomAuteur + " " + prenomAuteur;
									}

								}
								// on sais que ce n'est pas l'auteur

								// c'est le nom qui est de taille 2
							}
						} else
						// taille supérieur a 2
						{ // si le nom et le prenom remplisse les conditions on
							// a le nom

							if (di.LevenshteinDistance(prenom, prenomAuteur) <= fonctionTaille(prenom)
									&& di.LevenshteinDistance(nom, nomAuteur) <= fonctionTaille(nom)) {
								// on a l'auteur
								trouve = true;
								auteur = nomAuteur + " " + prenomAuteur;
							}
							// on inverse le nom et le prenom au cas ou ca ne
							// serait pas dans le même sens
							String t = nomAuteur;
							nomAuteur = prenomAuteur;
							prenomAuteur = t;
							if (di.LevenshteinDistance(prenom, prenomAuteur) <= fonctionTaille(prenom)
									&& di.LevenshteinDistance(nom, nomAuteur) <= fonctionTaille(nom)) {
								// on a l'auteur
								trouve = true;
								auteur = prenomAuteur + " " + nomAuteur;
							}

						}

					}
					br.close();
					ips = new FileInputStream(lesAuteurs);
					ipsr = new InputStreamReader(ips);
					br = new BufferedReader(ipsr);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return auteur;

	}

	/**
	 * Permet de changer nom et prenom pour qu'il soit de la même forme que ceux
	 * du fichier exemple: aChanger = Albert , forme = A. aChanger deviendra A.
	 *
	 * @param aChanger
	 * @param forme
	 * @return
	 */
	public String mettreEnForme(String aChanger, String forme) {
		String retour = aChanger;
		if (forme.contains(".")) {
			int pos = forme.indexOf(".");
			retour = aChanger.substring(0, pos) + ".".trim();
		}

		return retour;
	}

	/**
	 * Renvoie le maximum de caractére mal Placé en fonction de la taille du nom
	 * pour l'apelle a DistanceLeven
	 *
	 * @param mot
	 *            le mot
	 * @return
	 */
	public int fonctionTaille(String mot) {
		if (mot.length() <= 2) {
			return 0;
		} else {
			if (mot.length() > 2 && mot.length() <= 5) {
				return 1;
			} else {
				return 2;
			}
		}
	}

}

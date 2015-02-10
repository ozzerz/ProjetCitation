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
				doc = sxb.build(monFichier);
			}
		} catch (Exception e) {

		}

	}

	/**
	 * On formatera la citation dans le format voulu
	 *
	 * @param citation
	 */
	public void formatCitation(String citation) {

	}

	/**
	 * Permettra d'appeler formatCitation sur toutes les citations
	 */
	public void formatAllCitations() {
		// pour chaque citation
		// formatCitation(citation)

	}

	/**
	 * Cette méthode a pour but de trouver l'auteur d'une citation s'il existe
	 * Cependant celle ci s'arréte s'il on en trouve un avec un certain % d'erreur
	 *
	 * @see Distance
	 * @param citation
	 *            la citation
	 * @param fichierNom
	 *            le nom du fichier contenant l'ensembles des noms d'auteurs
	 * @return l'auteur s"il existe , null sinon
	 */
	public String getAuteurCitationRapide(String citation, String fichierNom) {
		String auteur = null;
		String nom;// ce que l'on pense pouvoir être le nom de l'auteur
		String prenom;// ce que l'on pense pouvoir être le prénom de l'auteur
		String ligne;// la ligne que l'on va lire
		String nomAuteur;// le nom de l'auteur dans le fichier
		String prenomAuteur;// le prénom de l'auteur dans le fichier
		String[] tempo;// servira à répartir le nom et le prénom de l'auteur
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
				// pour chaques mots
				for (int i = 0; i < lesMots.length - 1; i++) {
					nom = lesMots[i];
					prenom = lesMots[i + 1];
					while ((ligne = br.readLine()) != null && !trouve) {
						tempo = ligne.split(" ");
						// on récupére les noms et on remet en forme
						nomAuteur = tempo[0];
						prenomAuteur = tempo[1];
						nom = mettreEnForme(nom, nomAuteur);

						prenom = mettreEnForme(prenom, prenomAuteur);
						// il est possible que le . soit dans la citation est
						// pas dans la DB
						// dans ce cas il faut modifier

						nomAuteur = mettreEnForme(nomAuteur, nom);
						prenomAuteur = mettreEnForme(prenomAuteur, prenom);

						// si le prénom ou le nom est de taille 2 et que l'on n'a
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
								// on sait que ce n'est pas l'auteur

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
								// on sait que ce n'est pas l'auteur

								// c'est le nom qui est de taille 2
							}
						} else
						// taille supérieure à 2
						{ // si le nom et le prénom remplissent les conditions on
							// a le nom

							if (di.LevenshteinDistance(prenom, prenomAuteur) <= fonctionTaille(prenom)
									&& di.LevenshteinDistance(nom, nomAuteur) <= fonctionTaille(nom)) {
								// on a l'auteur
								trouve = true;
								auteur = nomAuteur + " " + prenomAuteur;
							}
							// on inverse le nom et le prénom au cas où ça ne
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
	 * Cette version prend plus de temps que la précédente mais renvoit la réponse la plus précise possible
	 * @param citation
	 * @param fichierNom
	 * @return l'auteur s'il existe sinon null
	 */
	public String getAuteurCitationLente(String citation, String fichierNom) {
		int erreurActuel=300;//on stockera les distances d'éditions actuelles pour améliorer le résultat
		int erreurTempo;//pour comparer avec l'erreur actuelle
		String auteur = null;
		String nom;// ce que l'on pense pouvoir être le nom de l'auteur
		String prenom;// ce que l'on pense pouvoir être le prénom de l'auteur
		String ligne;// la ligne que l'on va lire
		String nomAuteur;// le nom de l'auteur dans le fichier
		String prenomAuteur;// le prénom de l'auteur dans le fichier
		String[] tempo;// servira à repartir le nom et le prénom de l'auteur
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
				// pour chaques mots
				for (int i = 0; i < lesMots.length - 1; i++) {
					nom = lesMots[i];
					prenom = lesMots[i + 1];
					while ((ligne = br.readLine()) != null && !trouve) {
						tempo = ligne.split(" ");
						// on recupére les noms et on remet en forme
						nomAuteur = tempo[0];
						prenomAuteur = tempo[1];
						nom = mettreEnForme(nom, nomAuteur);

						prenom = mettreEnForme(prenom, prenomAuteur);
						// il est possible que le . soit dans la citation et
						// pas dans la DB
						// dans ce cas il faut modifier

						nomAuteur = mettreEnForme(nomAuteur, nom);
						prenomAuteur = mettreEnForme(prenomAuteur, prenom);

						// si le prénom ou le nom est de taille 2 et que l'on n'a
						// pas le même alors ce n'est pas l'auteur
						if (prenom.length() <= 2 || nom.length() <= 2) {
							if (prenom.length() <= 2) {
								if (prenom.equals(prenomAuteur)) {
									if (di.LevenshteinDistance(nom, nomAuteur) <= fonctionTaille(nom)) { // on
																										// a
																											// l'auteur
										erreurTempo=di.LevenshteinDistance(nom, nomAuteur)+di.LevenshteinDistance(prenom, prenomAuteur);
										if(erreurTempo<erreurActuel)
										{
											auteur = nomAuteur + " " + prenomAuteur;
											erreurActuel=erreurTempo;
											if(erreurActuel==0)
											{
												trouve = true;//aucune erreur donc le bon auteur
											}
										}




									}

								}
								// on sait que ce n'est pas l'auteur

							} else {
								if (nom.equals(nomAuteur)) {
									if (di.LevenshteinDistance(prenom,
											prenomAuteur) <= fonctionTaille(prenom)) { // on
																						// a
																						// l'auteur

										erreurTempo=di.LevenshteinDistance(nom, nomAuteur)+di.LevenshteinDistance(prenom, prenomAuteur);
										if(erreurTempo<erreurActuel)
										{
											auteur = nomAuteur + " " + prenomAuteur;
											erreurActuel=erreurTempo;
											if(erreurActuel==0)
											{
												trouve = true;//aucune erreur donc le bon auteur
											}
										}
									}

								}
								// on sait que ce n'est pas l'auteur

								// c'est le nom qui est de taille 2
							}
						} else
						// taille supérieur a 2
						{ // si le nom et le prénom remplissent les conditions on
							// a le nom

							if (di.LevenshteinDistance(prenom, prenomAuteur) <= fonctionTaille(prenom)
									&& di.LevenshteinDistance(nom, nomAuteur) <= fonctionTaille(nom)) {
								erreurTempo=di.LevenshteinDistance(nom, nomAuteur)+di.LevenshteinDistance(prenom, prenomAuteur);
								if(erreurTempo<erreurActuel)
								{
									auteur = nomAuteur + " " + prenomAuteur;
									erreurActuel=erreurTempo;
									if(erreurActuel==0)
									{
										trouve = true;//aucune erreur donc le bon auteur
									}
								}
							}
							// on inverse le nom et le prénom au cas où ça ne
							// serait pas dans le même sens
							String t = nomAuteur;
							nomAuteur = prenomAuteur;
							prenomAuteur = t;
							if (di.LevenshteinDistance(prenom, prenomAuteur) <= fonctionTaille(prenom)
									&& di.LevenshteinDistance(nom, nomAuteur) <= fonctionTaille(nom)) {
								erreurTempo=di.LevenshteinDistance(nom, nomAuteur)+di.LevenshteinDistance(prenom, prenomAuteur);
								if(erreurTempo<erreurActuel)
								{
									auteur = prenomAuteur + " " + nomAuteur;
									erreurActuel=erreurTempo;
									if(erreurActuel==0)
									{
										trouve = true;//aucune erreur donc le bon auteur
									}
								}
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
	 * Permet de changer nom et prénom pour qu'il soit de la même forme que ceux
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
	 * Renvoie le maximum de caractère mal placé (autrement dit le % d'erreur) en fonction de la taille du nom
	 * pour l'appele à LevenshteinDistance
	 * @see Distance
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

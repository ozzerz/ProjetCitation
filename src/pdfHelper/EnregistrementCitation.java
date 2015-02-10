package pdfHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
/**
 * Enregistre les citations dans la base de données
 * @author Ozzerz
 *
 */
public class EnregistrementCitation {

	/**
	 *  le titre de l'oeuvre en cours
	 */
	String titre;
	/**
	 * contient le document de la BDD
	 */
	private Document doc;
	/**
	 * le BufferedReader pour ne pas recommencer à lire depuis le début
	 */
	BufferedReader br;

	/**
	 * Contiendra les parties du texte où il n'y a pas de citation
	 */
	ArrayList<String> resteDuTexte;

	/**
	 * constructeur
	 * @param titre le titre du PDF
	 * @param doc la base de données
	 * @param br le bufferedReader
	 */
	public EnregistrementCitation(String titre, Document doc, BufferedReader br) {
		this.titre = titre;
		this.doc = doc;
		this.br = br;
		this.resteDuTexte=new ArrayList<String>();

	}

	/**
	 * Enregistre les citations si celles ci ne sont pas déja dans la base de données
	 *
	 * @param racine
	 *            la racine du document
	 * @param result
	 *            le titre
	 */
	private void testAjoutCitation(Element racine, ArrayList<String> citations) {
		boolean find = false;
		Element oeuvreEnCour = null;
		boolean citationIn = true;//sera utilisé pour savoir si les citations sont déja dans la BDD
		Element cits = new Element("citations");//on crée l'élement citations pour la BDD
		//tant que toutes les citations ne sont pas dans la BDD on crée leur élement
		for (int c = 0; c < citations.size(); c++) {
			Element citation = new Element("citation");
			citation.addContent(citations.get(c));
			cits.addContent(citation);
		}
		List<Element> oeuvres = racine.getChildren();
		Iterator i = oeuvres.iterator();
		// ici on détermine si les citations sont déja dans la base de données
		while (i.hasNext()) {

			Element courant = (Element) i.next();
			if (courant.getAttributeValue("nom").equals(titre.trim()) || !find) {


				oeuvreEnCour = courant;
				if (courant.getChild("citations") == null) {
					citationIn = false;//ne sont pas dans la BDD
				}
				find = true;

			}
		}
		//si elle ne sont pas dedans alors on les enregistre
		if (!citationIn) {

			oeuvreEnCour.addContent(cits);
		}
		enregistreXML();// on sauvegarde les changements

	}

	/**
	 * Permet d'obtenir les citations d'un texte
	 *
	 * @param nomFichier
	 *            le texte que l'on étudie
	 * @param br
	 *            le bufferReder de ce texte (pour éviter de recommencer au
	 *            début)
	 * @return
	 */
	BufferedReader getCitation(String nomFichier, BufferedReader br) {
		ArrayList<String> citations = new ArrayList<String>();
		Element racine = doc.getRootElement();
		try {
			String ligne;
			String result = "";
			// tant qu'on a pas trouvé les auteurs
			while ((ligne = br.readLine()) != null) {

				if (ligne.equals("Debut de paragraphe")) {
					result = "";
					ligne = br.readLine();
					while (!ligne.equals("Fin de paragraphe")) {

						result = result + " " + ligne;
						ligne = br.readLine();// on a le titre

					}
					result = result.trim();

					// ici on a récupéré l'ensemble du texte
					if (isCitations(result)) {



						citations = splitCitation(result, citations);


					}

					// si ce n'est pas une citation les ajouter dans une liste
					// contenant le reste du texte
					resteDuTexte.add(result);
				}

			}

		} catch (Exception e) {

		}

		citations = organiseListe(citations);
		testAjoutCitation(racine, citations);

		return br;
	}

	/**
	 * permet de séparer les citations contenues dans le même paragraphe
	 *
	 * @param ligne
	 *            le paragraphe en question
	 * @param citations
	 *            la liste de citation
	 * @return la liste de citations modifier
	 */
	public ArrayList<String> splitCitation(String ligne,
			ArrayList<String> citations) {
		Pattern p = Pattern.compile("^[a-z0-9]*");
		Pattern p2 = Pattern.compile("[0-9]+");// contient au moins un nombre
		int position = 0;
		int nextCitationStart = 0;
		int point = ligne.indexOf(".");//on trouve l'endroit du premier point
		int pointavant = 0;
		int debutCitation = 0;
		String ligneT;
		boolean passeWhile = false;
		String ajout = null;
		boolean debutcitationOk = false;

		// on cherche le debut de citation actuel
		ligneT = ligne.substring(pointavant, point);//on récupére ce qu'il y a avant le premier point
		Matcher m = p.matcher(ligneT);
		Matcher m3 = p2.matcher(ligneT);
		//si ce qu'il y a avant le point est de taille avec au moins un chiffre alors on sait que c'est le début d'une citation
		//les lettres permettent d'être résistant aux problémes d'OCR
		if (ligneT.length() <= 3 && m.matches() && m3.matches()) {
			debutCitation = point;
		}
		//sinon on doit chercher le début de la citation
		else {
			//tant que l'on a pas trouvé le point
			while (point != -1 && !debutcitationOk) {
				//on prend la position du point suivant
				point = ligne.indexOf(".", point + 1);

				if (point != -1) {

					//si il n'y a aucun espace dans la sous chaine
					if (ligne.substring(0, point).lastIndexOf(" ") != -1) {
						ligneT = ligne.substring(ligne.substring(0, point)
								.lastIndexOf(" "), point);

						Matcher m2 = p.matcher(ligneT);
						Matcher m4 = p2.matcher(ligneT);
						//on teste si l'on a trouvé le début de citation
						if (m2.matches() && m4.matches()) {
							// on a trouvé la citation
							debutCitation = point;
							debutcitationOk = true;
						}

					}
				}
			}

		}

		position = debutCitation + 1;
		//tant qu'il y a d'autres citations
		while (nextCitations(ligne, position) != -1) {
			nextCitationStart = nextCitations(ligne, position);//on enregistre l'endroit où débute la prochaine citation
																//c'est à dire l'endroit où termine celle ci

			if (position - (ligneT.length() + 1) > -1) {
				ajout = ligne.substring(position - (ligneT.length() + 1),
						nextCitationStart).trim();

				citations.add(ajout);

				position = nextCitationStart + 1;

				passeWhile = true;
			} else {
				position = nextCitationStart + 1;
				passeWhile = true;
			}

		}
		//si il n'y a plus de citation aprés
		if (!passeWhile) {

			if (position - (ligneT.length() + 1) > -1) {
				ajout = ligne.substring(position - (ligneT.length() + 1),
						ligne.length()).trim();//on enregistre la citation

			} else {
				ajout = ligne.substring(position, ligne.length()).trim();//on enregistre la citation

			}
			citations.add(ajout);//on ajoute les citations dans la liste de citation

		} else {
			ajout = ligne.substring(position - 1, ligne.length()).trim();
			citations.add(ajout);//ajout des citations

		}

		return citations;
	}

	/**
	 * Permet de savoir si le paragraphe que l'on traite actuellement contient
	 * des citations
	 *
	 * @param ligne
	 * @return true si c'est une citation , false sinon
	 * @throws InterruptedException
	 */
	public boolean isCitations(String ligne) throws InterruptedException {
		// chaque citation commence par la forme XX.
		int point = ligne.indexOf(".");
		int pointavant = 0;
		String ligneT;

		Pattern p = Pattern.compile("^[a-z0-9]*");
		Pattern p2 = Pattern.compile("[0-9]+");// contient au moins un nombre

		if (point == -1) {
			// si il n'y a pas de "." alors ca ne peut pas être une citation
			// on considére qu'il n'y aura jamais + de 1000 citations par texte
			return false;
		}
		while (point != -1) {

			// on récupére ce qu'il y a avant le point
			// on commence par faire une sous chaine du début jusqu'au point
			// on récupére ce qu'il y a avant le point
			if (point == pointavant) {
				// dans ce cas le point est a la position 0
				ligneT = "plusde4commecaOnChercheLeProchain";
			} else {
				if (pointavant == 0) {
					ligneT = ligne.substring(pointavant, point);
					if (ligneT.lastIndexOf(" ") != -1) {
						ligneT = ligneT.substring(ligneT.lastIndexOf(" "))
								.trim();
					}
				} else {
					ligneT = ligne.substring(pointavant, point);
				}

			}

			Matcher m3 = p.matcher(ligneT);
			Matcher m4 = p2.matcher(ligneT);

			if (ligneT.length() <= 3 && m3.matches() && m4.matches()) {

				return true;// dans ce cas on sait qu'il y a des citations ,
							// sinon on continue de chercher
			} else {
				pointavant = point;
				point = ligne.indexOf(".", point + 1);

				// tant que l'on a pas fini de chercher
				while (point != -1) {

					ligneT = ligne.substring(pointavant, point);// on récupére
																// l'ensemble
																// jusqu'au
																// prochain
																// point

					if (ligneT.lastIndexOf(' ') == -1) {

						return false;
					}
					ligneT = ligneT.substring(ligneT.lastIndexOf(' '),
							ligneT.length());// on récupére juste le point

					// on doit également vérifier que ca ne contient pas d'espace
					Matcher m = p.matcher(ligneT);
					Matcher m2 = p2.matcher(ligneT);

					boolean casFauxPositif = ligneT.equals(" p")
							|| ligneT.equals("(éd.");// ici on va gérer les
														// "faux positifs"

					if (ligneT.length() <= 3 && m.matches() && m2.matches()
							&& ligneT.length() > 0 && !casFauxPositif) {

						return true;// dans ce cas on sait qu'il y a des
									// citations , sinon on continue de chercher
					} else {
						pointavant = point;
						point = ligne.indexOf(".", point + 1);

					}

				}

			}

			return false;

		}
		return false;

	}

	/**
	 * renvoie la position du début de la prochaine citation dans le paragraphe
	 *
	 * @param position
	 *            la position de la derniere citation trouvée
	 * @param ligne
	 *            le paragraphe où l'on cherche
	 * @return -1 si il n'y a plus de citation, sinon renvoie la position de la
	 *         prochaine citation
	 */
	public int nextCitations(String ligne, int position) {

		boolean verif = true;
		int i = 0;
		String numero;
		int pointAvant = 0;
		int point = 0;
		int testE;
		boolean fauxPositif = true;
		int taille = 0;
		String recherche = ligne.substring(position);

		Pattern p = Pattern.compile("[0-9]*");
		Pattern p2 = Pattern.compile("[0-9]+");// contient au moins un nombre
		while (recherche.indexOf(".", i) != -1 && verif) {

			if (recherche.indexOf(".", i + 1) == -1) {
				// il faut arréter la recherche maintenant ,

				return -1;
			} else {
				pointAvant = point;
				if (point == -1) {
					return -1;
				}
				point = recherche.indexOf(".", i);

				testE = recherche.substring(pointAvant, point).lastIndexOf(" ");

				// on récupére le "numéro" avant le prochain point
				if (testE == -1) {
					numero = recherche.substring(pointAvant, point);

				} else {
					numero = recherche.substring(testE + pointAvant, point);
					numero = numero.trim();
					taille = numero.length();

				}
				// on verifie si on a le résultat

				Matcher m = p.matcher(numero);
				Matcher m2 = p2.matcher(numero);
				fauxPositif = !numero.equals("p") && !numero.equals("vol")
						&& !numero.equals("op") && !numero.equals("cit")
						&& !numero.equals("u") && !numero.equals("cf")
						&& !numero.equals("etc") && !numero.equals("Cf");

				if (m2.matches() && m.matches() && fauxPositif
						&& numero.length() <= 3) {

					return point - taille + position;

				} else {
					// on continue de chercher


					// dans le cas où on a trouver une page il faut ignorer ce
					// qu'il y a avant le prochain point car il s'agira du
					// numéro de page.

					if (numero.equals("p") || numero.equals("chap")) {
						i = point + 1;
						point = recherche.indexOf(".", i);

					}
					pointAvant = point + 1;
					i = point + 1;

				}

			}

		}

		return -1;

	}
	/**
	 * Enregistre les changements dans notre document XML
	 */
	private void enregistreXML() {
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		try {
			sortie.output(doc, new FileOutputStream("bdd.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Permet de modifier la liste de citations pour la rendre plus lisible
	 * ex: <citation> 1: Je suis un aigle voir George p. </citation>
	 *     <citations> 32. Livre de la vie. </citation>
	 *  Devient
	 *	<citation> 1: Je suis un aigle voir George p32. Livre de la vie. </citation>
	 * @param citations
	 *            la liste des citations
	 * @return la liste des citations réorganiser
	 */
	public ArrayList<String> organiseListe(ArrayList<String> citations) {
		boolean citationNonComplete = true;// permet de continuer à modifier une
											// citation
		Pattern p = Pattern.compile("[0-9]*");
		if (citations.size() > 0) {
			citations.remove(0);// dans ce cas on supprime la première ligne
								// contenant le DOI

			// pour chaque élèment de la liste on va effectuer différents
			// traitements
			for (int i = 1; i < citations.size(); i++) {
				citationNonComplete = true;
				// si la citation commence par un ./p. ou si elle n'en a pas
				// alors c'est la suite de celle du dessus de même si la taille
				// est inférieure à 3
				if (citations.get(i).startsWith(".")
						|| citations.get(i).startsWith("p.")
						|| citations.get(i).indexOf(".") == -1
						|| citations.get(i).length() <= 3) {

					citations.set(i - 1,
							citations.get(i - 1) + citations.get(i));
					citations.remove(i);
					i--;

				} else {

					// si le point n'est pas au début alors il y a des choses à
					// supprimer
					while (citations.get(i).indexOf(".") >= 3
							&& citationNonComplete) {
						String partieAvantPoint = citations.get(i).substring(
								citations.get(i).indexOf("."));
						int partieAGarde = partieAvantPoint.lastIndexOf(" ");
						if (partieAGarde == -1) {
							partieAGarde = 0;// en cas d'erreur
							citationNonComplete = false;
						}
						String newcitation = citations.get(i).substring(
								partieAGarde);
						citations.set(i, newcitation);

					}
					// si le premier est à la fin de la citation elle ne devrait
					// pas y etre
					if (citations.get(i).indexOf(".") == citations.get(i)
							.length() - 1) {
						citations.remove(i);
						i--;
					}

				}

			}
		}
		return citations;
	}
}

package db;
/**
 * Cette classe permet de mod�lis� un Auteur
 * @author Maxime Chaste
 *
 */



// A FAIRE DISPARAITRE , aucune raison de s�par� nom et prenom

public class Auteur {

	private String nom;
	private String prenom;

	public Auteur(String prenom, String nom) {
		this.nom = nom;
		this.prenom = prenom;
	}


	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}






}

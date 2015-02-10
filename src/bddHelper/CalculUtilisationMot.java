package bddHelper;

import java.io.File;
import java.util.ArrayList;

import org.jdom2.Document;
/**
 * Cette classe à pour but de calculer l'utilisation de chaque mot
 * On peut imaginer que si un mot est peu utilisé alors il est important pour une citation
 * Cette classe sera donc utilisé dans FormatageCitation
 * Pour améliorer cette classe on peut imaginer enregistrer les resultats dans un fichier CSV
 * ainsi que la creation d'une methode pour ne pas toujours le relancer sur la totalité de la BDD
 * @author Ozzerz
 *
 */
public class CalculUtilisationMot {


	public ArrayList<String> mots;//contiendra la liste des mots

	public ArrayList<Integer> nbUtilisation;//contiendra a l'indice i le nombre de fois ou le mot à l'indice i (dans mots) apparait

	private Document doc;//contiendra la BDD

	//en cas d'amélioration (sera surement nécessaire)
	/**
	 * Contiendra le fichier avec les résultats
	 */
	private File stockage;

	public CalculUtilisationMot(Document doc)
	{
		this.mots=new ArrayList<String>();
		this.nbUtilisation=new ArrayList<Integer>();
		this.doc=doc;

	}
	//en cas d'amélioration
	public CalculUtilisationMot(Document doc,String nomStockage)
	{
		this.mots=new ArrayList<String>();
		this.nbUtilisation=new ArrayList<Integer>();
		this.doc=doc;
		this.stockage=new File(nomStockage);

	}

	/**
	 * Permettra de lancer le calcul sur l'ensemble de la base de données
	 */
	public void premiereUtilisation()
	{
		/*
		Pour chaque citation
			 compteMotUneCitation( citation)
			    	  */
	}
	/**
	 * Cette fonction renverra une liste contenant les mots utilisé n ou moins de fois
	 * @param n le nombre d'occurance
	 * @return la liste des mots
	 */
	public ArrayList<String> motUtiliseNouMoinsDefois(int n)
	{
		ArrayList<String> motsPeuUtiliser=new ArrayList<String>();//contiendra la liste des mots utiliser n ou moins de fois
		for(int i=0;i<mots.size();i++)
		{
			if(nbUtilisation.get(i)<=n)//si le mot est utilisé moins/ou n fois on l'ajoute
			{
				motsPeuUtiliser.add(mots.get(i));
			}

		}

		return motsPeuUtiliser;
	}
	/**
	 * Calcul de l'utilisation de chaque mot dans une citation
	 * @param citation : la citation
	 */
	public void compteMotUneCitation(String citation)
	{
		if(citation.indexOf(".")!=-1){
		String aCompter=citation.substring(citation.indexOf(".")+1).trim();//On doit commencer apres le point
		String[] lesMots = aCompter.split(" ");
		for (int i=0;i<lesMots.length-1;i++)
		{
			addMot(lesMots[i]);
		}
		//pour le dernier mot il faut enlevé le point
		String dernierMot=lesMots[lesMots.length-1];
			if(dernierMot.indexOf(".")==-1)//si il n'y a pas de point alors traitement standard
			{
				addMot(dernierMot);
			}
			else
			{
				dernierMot=dernierMot.substring(0, dernierMot.indexOf("."));
				addMot(dernierMot);
			}

		}

	}

	/**
	 * Si le mot est dans la liste on ajoute 1 a son nombre (dans nbUtilisation) sinon on l'ajoute dans mots et on initalise nbUtilisation à 1
	 * @param mot le mot a ajouter
	 */
	public void addMot(String motAjout)
	{
		if(mots.contains(motAjout.trim())){//si le mot est déja la
			int pos=mots.indexOf(motAjout);
			nbUtilisation.set(pos, nbUtilisation.get(pos)+1);//on ajoute un au nombre d'utilisation
		}
		else//sinon on ajoute le mot
		{
			mots.add(motAjout.trim());
			nbUtilisation.add(1);
		}
	}


}

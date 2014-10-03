package pdfToText;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import db.Auteur;
/**
 * Permet d'enregistrer les .txt contenants les information qui nous intéresse
 * @author Ozzerz
 *
 */
public class EnregistrementTexte {

	private ArrayList<String> lesTextes;//contiendra la liste des titres de texte dont on souhaite obtenir le contenu
	private ExtractionTexte et;//la transformation en texte
	private ArrayList<String> auteur;//contiendra le/les auteur du texte actuel (réinitialiser avant chaque nouveau fichier)


	public EnregistrementTexte(ArrayList<String> texte)
	{
		lesTextes=texte;
		String nomFichier;


		//on va transformer chaque pdf en fichierTexte pour ensuite les manipuler
		for(int i=0;i<lesTextes.size();i++)
		{
			//auteur=new ArrayList<String>();
			nomFichier=(lesTextes.get(i).substring(0, lesTextes.get(i).length()-3))+"txt";
			et=new ExtractionTexte(lesTextes.get(i));
			et.creationFichier(et.pdftoText(),nomFichier);
			System.out.println(i);
			getAllInfo(nomFichier);

		}



	}

	/**
	 * récupérera toute les information nécessaire
	 * @param nomFichier le nom du fichier
	 */
	private void getAllInfo(String nomFichier)
	{
		InputStream ips;
		try {
			ips = new FileInputStream(nomFichier);
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			br=getAuteur(nomFichier, br);
			br=getTitre(nomFichier, br);


			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}



	}


/**
 * récupére les auteurs
 * @param nomFichier
 * @return
 */
	private BufferedReader getAuteur(String nomFichier ,BufferedReader br)
	{

		boolean fin=true;
		File file;
		auteur=new ArrayList<String>();//permet de nettoyer la liste entre chaque fichier
		//on va lire dans le fichier

		try{
			String ligne;
			//tant qu'on a pas trouvé les auteurs
			while ((ligne=br.readLine())!=null&&fin){
				if(ligne.equals("Debut de paragraphe"))
				{
					ligne =br.readLine();
					while(!ligne.equals("Fin de paragraphe"))
					{


						ligne=formaterNom(ligne);
						file=new File(ligne+".txt");//on crée un fichier au nom de l'auteur
						auteur.add(ligne);//permet d'avoir le nom de l'auteur


						//creation de fichier
						try
						{
						//si le fichier n'existe pas je le crée
						if(!file.exists())
						{
						file.createNewFile();
						}
						else
						{
							System.out.println("cette auteur existe déja");
						}
						}
						catch (IOException e)
						{
						System.out.println("Impossible de créer le fichier");
						}
						ligne =br.readLine();//on a nom+prenom d'un auteur


					}
					fin=false;//on a trouvé tout les auteurs
				}




			}

		}
		catch (Exception e){
			System.out.println(e.toString());
		}




		return br;
	}


	/**
	 * Permet de récupéré de titre du PDF , s'utilise toujours aprés GetAuteur
	 * @param nomFichier , le nom du fichier
	 * @param br , le bufferedReader
	 * @return le bufferReader situé aprés le titre
	 */
	private BufferedReader getTitre(String nomFichier ,BufferedReader br)
	{
		boolean fin=true;
		File file;
		try{
			String ligne;
			String result="";
			//tant qu'on a pas trouvé les auteurs
			while ((ligne=br.readLine())!=null&&fin){
				if(ligne.equals("Debut de paragraphe"))
				{
					ligne =br.readLine();
					//result=result+ligne;
					System.out.println("j'ajout H"+ligne);
					while(!ligne.equals("Fin de paragraphe"))
					{


						//ligne=formaterNom(ligne);
						file=new File(auteur.get(0)+".txt");//on crée un fichier au nom de l'auteur
						//creation de fichier
						try
						{
						//si le fichier n'existe pas je le crée
						if(file.exists())
						{
						System.out.println(ligne);
						}
						else
						{
							System.out.println("cette auteur existe déja");
						}
						}
						catch (Exception e)
						{
						System.out.println("Impossible de créer le fichier");
						}
						result=result+" "+ligne;
						ligne =br.readLine();//on a nom+prenom d'un auteur

						System.out.println("j'ajoute B"+ligne);

					}
					fin=false;//on a trouvé tout les auteurs
					result=result+"\n";
				}




			}

			for(int j=0;j<auteur.size();j++){
				//ici on ajoute le titre dans le fichier de chaque auteur
				//ligne=formaterNom(ligne);
				FileWriter writer = new FileWriter(auteur.get(j)+".txt", true);
				BufferedWriter output = new BufferedWriter(writer);
				//ajout dans le fichier
				try
				{
					 System.out.println("dans le try "+result+" auteur contient "+auteur.get(j));
					//ICI insertion du formatage + vérification de la non présence
				     output.write(result,0,result.length());
				     output.close();

				}
				catch (Exception e)
				{
				System.out.println("Impossible de créer le fichier");
				}

			}

		}
		catch (Exception e){
			System.out.println(e.toString());
		}

		return br;
	}


	/**
	 * On formate le nom
	 * @param ligne contient nom + prenom
	 * @return nom + prenom au format voulu
	 */
	private String formaterNom(String ligne)
	{
		//on va supprimer tout les "Monsieur Madame mlle...
		//partie Monsieur
		ligne=ligne.replaceAll("monsieur","");
		ligne=ligne.replaceAll("Monsieur","");
		ligne=ligne.replaceAll("MONSIEUR","");
		ligne=ligne.replaceAll("Mr","");
		ligne=ligne.replaceAll("MR","");
		//partie madame
		ligne=ligne.replaceAll("madame","");
		ligne=ligne.replaceAll("Madame","");
		ligne=ligne.replaceAll("MADAME","");
		ligne=ligne.replaceAll("Mme","");
		ligne=ligne.replaceAll("MME","");
		//partie mademoiselle
		ligne=ligne.replaceAll("mademoiselle","");
		ligne=ligne.replaceAll("Mademoiselle","");
		ligne=ligne.replaceAll("MADEMOISELLE","");
		ligne=ligne.replaceAll("Mlle","");
		ligne=ligne.replaceAll("MLLE","");

		ligne.trim();//on supprime tout les espaces de debut et de fin

		return ligne;
	}



}

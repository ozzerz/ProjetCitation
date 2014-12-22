package pdfToText;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Permet d'enregistrer les .txt contenants les information qui nous intéresse
 * @author Ozzerz
 *
 */
public class EnregistrementTexte {

    private ArrayList<String> lesTextes;//contiendra la liste des titres de texte dont on souhaite obtenir le contenu
    private ExtractionTexte et;//la transformation en texte
    private ArrayList<String> auteur;//contiendra le/les auteur du texte actuel (réinitialiser avant chaque nouveau fichier)
    private Document doc;//contiendra le document de la BDD
    private int numeroCitation;//permettra de connaitre le numero de la prochaine citation que l'on cherche
    private ArrayList<String> citations;//contiendra toute les citations d'un texte
    private ArrayList<String> numeros;//contiendra toute les citations d'un texte
    private ArrayList<String> citationsPossible;//on stocke l'endroit ou il y a possiblement des citations
    private ArrayList<String> nonCitations;//on stocke le reste
    private String titre;


    public EnregistrementTexte(ArrayList<String> texte)
    {
        lesTextes=texte;
        String nomFichier;
        File monFichier=new File("bdd.xml");

        //creation de fichier
        try
        {
        if(!monFichier.exists())
        {
            creationXML();
        }
        SAXBuilder sxb = new SAXBuilder();
        doc = sxb.build(new File("bdd.xml"));


        //on va transformer chaque pdf en fichierTexte pour ensuite les manipuler
        for(int i=0;i<lesTextes.size();i++)
        {
            //auteur=new ArrayList<String>();
            nomFichier=(lesTextes.get(i).substring(0, lesTextes.get(i).length()-3))+"txt";
            et=new ExtractionTexte(lesTextes.get(i));
            et.creationFichier(et.pdftoText(),nomFichier);
            getAllInfo(nomFichier);
            //calculLigne(nomFichier);

        }
        }
        catch (Exception e)
        {
        System.out.println("Erreur non naturel");
        }



    }

/**
 * INUTILE POUR L'INSTANT
 * utilisé pour compté le nombre de mot
 * @param ligne
 * @return
 */
    public int compteMot(String ligne)
    {

    	int retour =0;
    	ligne=ligne.trim();
    	int depuis =0;
    	while (ligne.indexOf(' ',depuis)!=-1)
    	{
    		depuis =ligne.indexOf(' ',depuis)+1;
    		retour++;

    	}


    	return retour+1;

    }
/**INUTILE
 * Compte le nombre de mot par paragraphe
 * @param nomFichier
 */
    public void calculLigne(String nomFichier)
    {
    	int nbMot=0;
    	int retourTotal=0;

    	try{
    		FileWriter fw = new FileWriter (nomFichier+"ligne");
			BufferedWriter bw = new BufferedWriter (fw);
			PrintWriter fichierSortie = new PrintWriter (bw);
			InputStream ips=new FileInputStream(nomFichier);
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null){
				retourTotal=0;
				if(ligne.equals("Debut de paragraphe"))
				{
					ligne=br.readLine();

					while (!ligne.contains("Fin de paragraphe"))
					{
						nbMot=compteMot(ligne);
						retourTotal=retourTotal+nbMot;
						fw.write (ligne +" "+nbMot+"\n");
						ligne=br.readLine();

					}

					fw.write (retourTotal+"\n");

				}


			}
			br.close();
		}
		catch (Exception e){
			System.out.println(e.toString());
		}

    }


    /**
     * récupérra toute les information nécessaire
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
          EnregistrementCitation en=  new EnregistrementCitation(titre, doc, br);
          br=en.getCitation(nomFichier, br);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


/**
 * recupere les auteurs
 * @param nomFichier
 * @return
 */
    private BufferedReader getAuteur(String nomFichier ,BufferedReader br)
    {

         boolean fin=true;
         Element racine = doc.getRootElement();
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
                    	 if(!ligne.isEmpty()){
                         auteur.add(ligne);//permet d'avoir le nom de l'auteur pour ajouter son oeuvre
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
        boolean auteurT=true;//permet de savoir quand on a trouvé l'element auteur lors de notre recherche pour ajouter son oeuvre
        File file;
        Element racine = doc.getRootElement();
        //Element auteurElement = new Element("auteur");
        try{
            String ligne;
            String result="";
            //tant qu'on a pas trouvé les auteurs
            while ((ligne=br.readLine())!=null&&fin){
                if(ligne.equals("Debut de paragraphe"))
                {
                    ligne =br.readLine();
                    while(!ligne.equals("Fin de paragraphe"))
                    {

                        result=result+" "+ligne;
                        ligne =br.readLine();//on a le titre

                    }
                    fin=false;//on a récupéré entiérement le titre
                   // result=result;

                }

                //result=result.trim();
                titre=result;
                testAjoutTitre(racine);

            }



        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        enregistreXML();
        return br;
    }

    /**
     * enregistre le titre si necessaire
     * @param racine la racine du document
     * @param result le titre
     */
    private void testAjoutTitre(Element racine)
    {
    	 for(int j=0;j<auteur.size();j++){
    		 boolean exist=false;
             java.util.List<Element> lesoeuvre=racine.getChildren();
             if(lesoeuvre.size()!=0){
             //il faut chercher dans cette liste verifier si l'oeuvre existe déja
              Iterator i = lesoeuvre.iterator();
              while(i.hasNext())
              {
             	 Element courant = (Element)i.next();
                 if(courant.getAttributeValue("nom").trim().equals(titre.trim())||exist)
                 {
                    //alors l'oeuvre a déja etait ajouté
                    exist=true;
                 }
              }
              //si l'oeuvre n'existe pas on l'ajoute
              	if(!exist)
              	{

              		Element oeuvre = new Element("oeuvre");
              		titre=titre.trim();
                    oeuvre.setAttribute("nom",titre);
                    //ici on ajoute comme fils les auteurs
                    for(int k=0;k<auteur.size();k++)
                    {
                    	 Element auteurElement = new Element("auteur");
                         auteurElement.setAttribute("nom",auteur.get(k));
                         oeuvre.addContent(auteurElement);
                    }
                    //on l'ajoute au document XML
                    racine.addContent(oeuvre);
                    enregistreXML();//on sauvegarde les changement



              	}

             }
             else
             {
            	 Element oeuvre = new Element("oeuvre");

           		titre=titre.trim();

                 oeuvre.setAttribute("nom",titre);
                 //ici on ajoute comme fils les auteurs
                 for(int k=0;k<auteur.size();k++)
                 {
                 	 Element auteurElement = new Element("auteur");
                      auteurElement.setAttribute("nom",auteur.get(k));
                      oeuvre.addContent(auteurElement);
                 }
                 //on l'ajoute au document XML
                 racine.addContent(oeuvre);
                 enregistreXML();//on sauvegarde les changement
            	 //le doc est vide on peut donc ajouter
             }
         }

    }


/* pour l'instant inutile
    private ArrayList<String> getCitations(String ligne)
    {
    	ArrayList<String> citations= new ArrayList<String>();
    	int pointEnCours=0;//l'index du point en cours
    	int numeroEnCours=0;//le numéro en cours
    	int departDernier=0;//permettra de récupéré la derniere citation
    	String stockNombre="nop";//on s'en servira pour stocker le nombre avant le point


    		//pour la premiere citation
    		int premierP=ligne.indexOf(".");
    		String numero=ligne.substring(0,premierP);
    		int numProchaineCitation =(Integer.parseInt(numero))+1;
    		int stop= nextCitations(ligne,0,String.valueOf(numProchaineCitation));
    		if(stop!=-1)
    		{
    		String citation=ligne.substring(0,stop);//<==Bloque a cette ligne
    		citations.add(citation);
    		//pour toutes les autres avant la derniere;

    		int depart=stop;

    		while(depart!=-1)
    		{	//System.out.println("depart="+depart);
    			pointEnCours=ligne.indexOf(".",depart);
    			numero=ligne.substring(pointEnCours-numero.length(),pointEnCours);
    			numProchaineCitation =(Integer.parseInt(numero)+1);
    			//System.out.println("numProchainecita= "+numProchaineCitation);
    			//System.out.println("ointEnCours-numero.length(): "+(pointEnCours-numero.length())+" String.valueOf(numProchaineCitation) "+String.valueOf(numProchaineCitation));
    			//System.out.println("le prochain stop va valloir "+nextCitations(ligne,pointEnCours-numero.length(),String.valueOf(numProchaineCitation))+" on recherche"+numProchaineCitation);


    			stop= nextCitations(ligne,pointEnCours-numero.length(),String.valueOf(numProchaineCitation));
    			if(stop!=-1){
    			citation=ligne.substring(pointEnCours-numero.length(),stop);
    			//System.out.println("on a ajouté "+citation);
        		citations.add(citation);
        		depart=stop;
        		//System.out.println("prochaine citation= "+String.valueOf(numProchaineCitation));
        		//System.out.println("depart= "+depart);

        		//System.out.println("on a récup "+citations);
    			}
    			else
    			{
    				//dans ce cas stop vaut -1 il faut donc passé a l'étape suivante
    				//System.out.println("dafuq");
    				departDernier=depart;
    				depart=stop;
    			}

    		}
    		//pour la derniere
    		//System.out.println("on arrive la");
    		pointEnCours=ligne.indexOf(".",departDernier);
    		citation=ligne.substring(pointEnCours-numero.length(),ligne.length());
    		citations.add(citation);}
    		else
    		{
    			//il n'y a qu'une citation
    			String citation=ligne.substring(0,ligne.length());//<==Bloque a cette ligne
        		citations.add(citation);
    		}


    		//System.out.println("on renvoie une liste de taille "+citations.size());
    	return citations;
    }
*/


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
        ligne=ligne.trim();//on supprime tout les espaces de debut et de fin
        return ligne;
    }

    /**
     * on va crée le fichier XML pour stocker les données
     */
    private void creationXML()
    {
         Element racine = new Element("oeuvres");
         Document document = new Document(racine);
         try
           {
              XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
              sortie.output(document, new FileOutputStream("bdd.xml"));
           }
           catch (java.io.IOException e){}


    }
    /**
     * permettra d'enregistrer dans le fichier XML
     */
    private void enregistreXML()
    {
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
     * Permet de modifier la liste de citation pour supprimer les doublons , mettre les numero de citations au bon endroit si necessaire
     * @param citations la liste des citations
     * @return la liste des citations réorganiser
     */
    public ArrayList<String> organiseListe(ArrayList<String>citations ){

    	String numero;
    	Pattern p = Pattern.compile("[0-9]*");

    	for(int i=0;i<citations.size();i++)
    	{
    		if(citations.get(i).length()<=3)
    		{
    			//on verifie si l'element d'apres de la liste a son numero
    			if(citations.get(i+1).indexOf(".")!=-1){
    			numero=citations.get(i+1).substring(0,citations.get(i+1).indexOf("."));
    			Matcher m=p.matcher(numero);

    			if(numero.length()<=3&&m.matches())
    			{
    				citations.remove(i);
    			}
    			else
    			{citations.set(i,citations.get(i)+". "+citations.get(i+1));
				citations.remove(i+1);

    			}
    			}

    		}

    	}

    	//dans ce deuxieme tour on va réunir ce qu'il reste , (des erreur du au "p.")
    	for(int i=0;i<citations.size();i++)
    	{
    		//si la citations commence par un.
    		if(citations.get(i).startsWith("."))
    		{
    			//alors on l'attache a celle d'avant
    			citations.set(i-1, citations.get(i-1)+citations.get(i).substring(1));
    			citations.remove(i);
    			i--;

    		}
    		//si elle commence par un p
    		if(citations.get(i).startsWith("p"))
    		{
    			//alors on l'attache a celle d'avant
    			citations.set(i-1, citations.get(i-1)+citations.get(i));
    			citations.remove(i);
    			i--;

    		}
    		if(citations.get(i).length()<=3)
    		{
    			citations.remove(i);
    		}

    			//si il n'y a pas de point ou si ce qu'il y a avant le point n'est pas matché
    			int point =citations.get(i).indexOf(".");
    		if(point!=-1){
    			numero=citations.get(i).substring(0,point);
    			Matcher m=p.matcher(numero);

    			if(!m.matches()){
    			citations.set(i-1, citations.get(i-1)+citations.get(i));
    			citations.remove(i);
    			i--;
    			}
    		}
    		else
    		{
    			citations.set(i-1, citations.get(i-1)+citations.get(i));
    			citations.remove(i);
    			i--;
    		}




    	}

    	return citations;
    }


}
package pdfToText;


import java.awt.List;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

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
    private Document doc;//contiendra le document de la BDD

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
            System.out.println(i);
            getAllInfo(nomFichier);

        }
        }
        catch (Exception e)
        {
        System.out.println("Erreur non naturel");
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
            br=getCitation(nomFichier, br);

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

                    	ligne=testAjoutAuteur(br , ligne ,racine);

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
     * enregistre l'auteur si necessaire
     * @param br le lecteur de texte
     * @param ligne la ligne actuel du texte
     * @param racine la racine de notre base de donnée
     * @return la prochaine ligne
     * @throws IOException
     */
    private String testAjoutAuteur(BufferedReader br , String ligne ,Element racine) throws IOException
    {

    	 //System.out.println("ligne contient" +ligne);

        ligne=formaterNom(ligne);
        auteur.add(ligne);//permet d'avoir le nom de l'auteur pour ajouter son oeuvre
        //si l'auteur n'est pas déja dans la liste on l'ajoute
        if(auteurDontExist(ligne)){
        Element auteurElement = new Element("auteur");
        auteurElement.addContent(new Element("nom").setText(ligne));
        racine.addContent(auteurElement);
        //auteur.add(ligne);//permet d'avoir le nom de l'auteur
        enregistreXML();
        System.out.println("ajout de "+ligne);
        ligne =br.readLine();//on a nom+prenom d'un auteur
        }
        else
        {
        	 ligne =br.readLine();//on a nom+prenom d'un auteur

        }

    	return ligne;
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


                testAjoutTitre(racine,result);

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
    private void testAjoutTitre(Element racine,String titre)
    {
    	 for(int j=0;j<auteur.size();j++){

             java.util.List<Element> lesauteurs=racine.getChildren();
             //il faut chercher dans cette liste l'auteur dont le nom est auteur.get(j)
              Iterator i = lesauteurs.iterator();
              while(i.hasNext())
              {

             	 Element courant = (Element)i.next();
                 if(courant.getChild("nom").getText().equals(auteur.get(j)))
                 {
                    //alors on ajoute l"oeuvre
                    if(oeuvreDontExist(auteur.get(j),titre)){
                 	Element oeuvre = new Element("oeuvre");
                     oeuvre.setAttribute("nom",titre);
                     courant.addContent(oeuvre);
                    }

                 }
              }


         }

    }


    private BufferedReader getCitation(String nomFichier , BufferedReader br)
    {
    	ArrayList<String> citations= new ArrayList<String>();
    	 Element racine = doc.getRootElement();
    	 try{
             String ligne;
             String result="";
             ArrayList<String> citationsParagrapheEnCours= new ArrayList<String>();
             //tant qu'on a pas trouvé les auteurs
             while ((ligne=br.readLine())!=null){
                 if(ligne.equals("Debut de paragraphe"))
                 {	 result="";
                     ligne =br.readLine();
                     while(!ligne.equals("Fin de paragraphe"))
                     {

                         result=result+" "+ligne;
                         ligne =br.readLine();//on a le titre

                     }
                     result=result.trim();
                     //System.out.println("pour result "+result+ "   on a "+isCitations(result));
                 //ici on a récupéré l'ensemble du texte
                     if(isCitations(result)){
                    	  //System.out.println("ici?");
                    	 //ici il faut ajouter les citations à la liste pas la remplacer a chaque fois

                    	 citationsParagrapheEnCours=getCitations(result);


                    	//on ajoute chaque citation dans la liste contenant toutes les citations
                    	 for(int k =0;k<citationsParagrapheEnCours.size();k++)
                    	 {
                    		 citations.add(citationsParagrapheEnCours.get(k));

                    	 }



                     }
                     //si ce n'est pas une citation on a besoin de rien faire
                 }
             }
    	 }
catch (Exception e)
{

}


    	 //partie pour voir ce que l'on obtiens
    	 System.out.println("-----------TEST-------------");
    	 for(int i=0;i<citations.size();i++)
    	 {
    		 System.out.println(citations.get(i));
    	 }

    	 System.out.println("-----------TEST-------------");



    	return br;
    }

    /**
     * Permet de savoir si le paragraphe que l'on traite actuellement contient des citations
     * @param ligne
     * @return true si c'est une citation , false sinon
     * @throws InterruptedException
     */
    private boolean isCitations(String ligne) throws InterruptedException{
    	//chaque citation commence par la forme XX.
    	int point = ligne.indexOf(".");

    	if(point==-1){
    		//si il n'y a pas de "." alors ca ne peut pas être une citation
    		//on considére qu'il n'y aura jamais + de 1000 citation par texte
    		return false;
    	}
    	String nombre=ligne.substring(0, point);//on récupuére ce qu'il y a avant le point
    	nombre=nombre.trim();
    	//on préare la regex
    	Pattern p = Pattern.compile("[0-9]*");
    	Matcher m = p.matcher(nombre);
    	//System.out.println("ligne"+ligne+" on a "+  m.matches()+"on donne en sous chaine "+nombre);
    	//Thread.sleep(4000);
    	//System.out.println("pour la ligne "+ligne+" on a"+m.matches());
    	//System.out.println("on récupére "+m.matches());

    	return m.matches();
    }

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
    		//System.out.println("ligne= "+ligne+" de taille "+ligne.length()+"stop renvoie "+stop);
    		if(stop!=-1)
    		{
    		String citation=ligne.substring(0,stop);//<==Bloque a cette ligne
    		citations.add(citation);
    		//System.out.println("on a ajouter la citation "+citation);
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

    /**
     * renvoie la position du debut de la prochaine citation
     * @param position la position de la derniere citation trouvé
     * @param nombreRechercher le numero de la citation voulue
     * @param ligne le paragraphe ou l'on chercher
     * @return -1 si il n'y a plus de citation sinon renvoie la position de la prochaine citation
     */
    private int nextCitations(String ligne,int position,String nombreRechercher)
    {
    	int retour=-1;
    	boolean verif=true;
    	int i=0;
    	String recherche=ligne.substring(position);
    	while(recherche.indexOf(".",i)!=-1&&verif){
    		//System.out.println("on plante sur test en fait");
    		if(recherche.indexOf(".",i+1)==-1)
    		{
    			//il faut arrété la rechercher maintenant ,
    			System.out.println("on arrive la");
    			i++;
    		}
    		else{
    		String test=recherche.substring(recherche.indexOf(".",i)-nombreRechercher.length(),recherche.indexOf(".",i));//on récupére le numéro de la citation
    		//System.out.println("pour nb recherche= "+nombreRechercher+" test "+test);
    		//System.out.println("test= "+test);
    		//System.out.println("pour le nombre "+nombreRechercher+"recherche "+recherche);
    		if(test.equals(nombreRechercher))
    		{
    			retour=recherche.indexOf(".",i)-nombreRechercher.length();//la position de la prochaine citation
    			//System.out.println("recherche.indexOf " +recherche.indexOf(".",i));
    			//System.out.println("retour "+retour);
    			verif=false;//on arrete de chercher

    		}
    		else
    		{
    			i++;//on continue a chercher
    		}
    	}

    	}
    	if(verif)
    	{
    		return -1;
    	}
    	else{
    	return retour+position;
    	}
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
        //System.out.println("avant trim "+ligne);
        ligne=ligne.trim();//on supprime tout les espaces de debut et de fin
        //System.out.println("apres trim "+ligne);
        return ligne;
    }

    /**
     * on va crée le fichier XML pour stocker les données
     */
    private void creationXML()
    {
         Element racine = new Element("auteurs");
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
     * permet de determiné si l'auteur existe déja dans la BDD
     * @param nom le nom de l'auteur
     * @return
     */
    public boolean auteurDontExist(String nom)
    {
    	 Element racine = doc.getRootElement();
    	 java.util.List<Element> lesauteurs=racine.getChildren();
          Iterator i = lesauteurs.iterator();
          while(i.hasNext())
          {
        	  Element courant = (Element)i.next();
        	  String nomT=courant.getChild("nom").getText().trim();
         	// System.out.println("noeud "+courant.getChildText("nom"));
             if(nomT.equals(nom.trim()))
             {
            	 //System.out.println("l'auteur existe déja");
            	 return false;
             }
             else
             {
             }

          }

    	return true;
    }


    /**
     *  permet de vérifier qu'une oeuvre n'existe pas déja dans la db
     * @param nom
     * @return
     */
    public boolean oeuvreDontExist(String nomAuteur,String nomOeuvre)
    {

   	 Element racine = doc.getRootElement();
   	 java.util.List<Element> lesauteurs=racine.getChildren();
         Iterator i = lesauteurs.iterator();
        //System.out.println("avant premier while");
         while(i.hasNext())
         {
       	  Element courant = (Element)i.next();
       	  String nomT=courant.getChild("nom").getText().trim();
            if(nomT.equals(nomAuteur.trim()))
            {
           	//ici on a trouvé le bon auteur , on récupére donc toute ses oeuvre
            	java.util.List<Element> lesOeuvres=courant.getChildren("oeuvre");
            	//System.out.println("lesoeuvree"+lesOeuvres.size());
            	Iterator j = lesOeuvres.iterator();
                //System.out.println("avant deuxiéme while");
                while (j.hasNext())
                {

                	Element oeuvreCourante =(Element)j.next();
                	//System.out.println("nom oeuvre"+oeuvreCourante.getContentSize());
                	if(oeuvreCourante.getAttributeValue("nom").equals(nomOeuvre))
                	{
                		System.out.println("OEuvre existe déja");
                		return false;
                	}


                }


            }


         }

    	return true;
    }




}
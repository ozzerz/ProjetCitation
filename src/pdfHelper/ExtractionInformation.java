package pdfHelper;


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
 * Permet d'enregistrer les .txt contenants les pdfs
 * @author Ozzerz
 *
 */
public class ExtractionInformation {
	/**
	 * la liste des titres de textes dont on souhaite obtenir le contenu
	 */
    private ArrayList<String> lesTextes;
    /**
     * la transformation en texte
     */
    private TransformationPDFtoText et;
    /**
     * contiendra le/les auteur(s) du texte actuel (r�initialiser avant chaque nouveau fichier)
     */
    private ArrayList<String> auteur;
    /**
     * le document de la BDD
     */
    private Document doc;
    /**
     * le titre du PDF
     */
    private String titre;

    /**
     * contructeur
     * @param texte la liste des PDF
     */
    public ExtractionInformation(ArrayList<String> texte)
    {
        lesTextes=texte;
        String nomFichier;
        File monFichier=new File("bdd.xml");

        //cr�ation du fichier de la BDD s'il n'existe pas
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

            nomFichier=(lesTextes.get(i).substring(0, lesTextes.get(i).length()-3))+"txt";
            et=new TransformationPDFtoText(lesTextes.get(i));
            et.creationFichier(et.pdftoText(),nomFichier);
            getAllInfo(nomFichier);


        }
        }
        catch (Exception e)
        {
        System.out.println("Erreur non naturel");// ne devrait jamais arriver
        }



    }




    /**
     * r�cup�re toute les informations n�cessaires
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
 * R�cupere les auteurs
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
            //tant qu'on a pas trouver les auteurs
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
                    fin=false;//on a trouv� tout les auteurs
                }




            }

        }
        catch (Exception e){
            System.out.println(e.toString());
        }




        return br;
    }




    /**
     * Permet de r�cup�rer de titre du PDF , s'utilise toujours apr�s GetAuteur
     * @param nomFichier , le nom du fichier
     * @param br , le bufferedReader
     * @return le bufferReader situ� apr�s le titre
     */
    private BufferedReader getTitre(String nomFichier ,BufferedReader br)
    {
        boolean fin=true;
        boolean auteurT=true;//permet de savoir quand on a trouv� l'�l�ment auteur lors de notre recherche pour ajouter son oeuvre
        File file;
        Element racine = doc.getRootElement();

        try{
            String ligne;
            String result="";
            //tant qu'on n'a pas trouv� les auteurs
            while ((ligne=br.readLine())!=null&&fin){
                if(ligne.equals("Debut de paragraphe"))
                {
                    ligne =br.readLine();
                    while(!ligne.equals("Fin de paragraphe"))
                    {

                        result=result+" "+ligne;
                        ligne =br.readLine();//on a le titre

                    }
                    fin=false;//on a r�cup�r� enti�rement le titre


                }


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
     * enregistre le titre si n�cessaire
     * @param racine la racine du document
     * @param result le titre
     */
    private void testAjoutTitre(Element racine)
    {
    	 for(int j=0;j<auteur.size();j++){
    		 boolean exist=false;
             java.util.List<Element> lesoeuvre=racine.getChildren();
             if(lesoeuvre.size()!=0){
             //il faut chercher dans cette liste verifier si l'oeuvre existe d�ja
              Iterator i = lesoeuvre.iterator();
              while(i.hasNext())
              {
             	 Element courant = (Element)i.next();
                 if(courant.getAttributeValue("nom").trim().equals(titre.trim())||exist)
                 {
                    //alors l'oeuvre a d�ja etait ajout�
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
                    enregistreXML();//on sauvegarde les changements



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
                 enregistreXML();//on sauvegarde les changements

             }
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
        ligne=ligne.trim();//on supprime tout les espaces du d�but et de la fin
        return ligne;
    }

    /**
     * on va cr�e le fichier XML pour stocker les donn�es
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
     * Permet de modifier la liste de citation pour supprimer les doublons , mettre les num�ros de citations au bon endroit si n�cessaire
     * @param citations la liste des citations
     * @return la liste des citations r�organiser
     */
    public ArrayList<String> organiseListe(ArrayList<String>citations ){

    	String numero;
    	Pattern p = Pattern.compile("[0-9]*");

    	for(int i=0;i<citations.size();i++)
    	{
    		if(citations.get(i).length()<=3)
    		{
    			//on v�rifie si l'�l�ment d'apr�s de la liste a son num�ro
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

    	//dans ce deuxi�me tour on va r�unir ce qu'il reste , (des erreur du au "p.")
    	for(int i=0;i<citations.size();i++)
    	{
    		//si la citation commence par un.
    		if(citations.get(i).startsWith("."))
    		{
    			//alors on l'attache � celle d'avant
    			citations.set(i-1, citations.get(i-1)+citations.get(i).substring(1));
    			citations.remove(i);
    			i--;

    		}
    		//si elle commence par un p
    		if(citations.get(i).startsWith("p"))
    		{
    			//alors on l'attache � celle d'avant
    			citations.set(i-1, citations.get(i-1)+citations.get(i));
    			citations.remove(i);
    			i--;

    		}
    		if(citations.get(i).length()<=3)
    		{
    			citations.remove(i);
    		}

    			//s'il n'y a pas de point ou si ce qu'il y a avant le point n'est pas match�
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
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
import java.lang.Character.Subset;
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
            System.out.println("nom = "+nomFichier);
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


    public int compteMot(String ligne)
    {

    	int retour =0;
    	ligne=ligne.trim();
    	int depuis =0;
    	while (ligne.indexOf(' ',depuis)!=-1)
    	{
    		depuis =ligne.indexOf(' ',depuis)+1;
    		retour++;
    		//System.out.println("on incrémente avec l'index "+ligne.indexOf(' ',depuis));
    	}

    	//System.out.println("pour la ligne "+ligne+" on renvoie "+retour+ "la ligne est de taille "+ligne.length());
    	return retour+1;

    }

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
					System.out.println("on sort du while");
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

                    	 ligne=formaterNom(ligne);
                         auteur.add(ligne);//permet d'avoir le nom de l'auteur pour ajouter son oeuvre
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
    		 boolean exist=false;
             java.util.List<Element> lesoeuvre=racine.getChildren();
             //il faut chercher dans cette liste verifier si l'oeuvre existe déja
              Iterator i = lesoeuvre.iterator();
              while(i.hasNext())
              {

             	 Element courant = (Element)i.next();
                 if(courant.getAttributeValue("nom").equals(titre)||exist)
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
            	// System.out.println("on a pas encore nul");

                 if(ligne.equals("Debut de paragraphe"))
                 {
                	 result="";
                     ligne =br.readLine();
                     while(!ligne.equals("Fin de paragraphe"))
                     {

                         result=result+" "+ligne;
                         ligne =br.readLine();//on a le titre

                     }
                     result=result.trim();
                    // System.out.println("result "+result);
                 //ici on a récupéré l'ensemble du texte
                     if(isCitations(result)){
                    	  //System.out.println("ici?");
                    	 //ici il faut ajouter les citations à la liste pas la remplacer a chaque fois

                    	//System.out.println("on a récupéré "+result);
                    	citations=splitCitation(result,citations);


                     }
                    // System.out.println("on sort du test de citation");
                     //si ce n'est pas une citation  les ajouter dans une liste conteant le reste du texte
                 }

             }

    	 }
catch (Exception e)
{

}


    	 //partie pour voir ce que l'on obtiens

    	 System.out.println("-----------TEST AVANT-------------");
    	 for(int i=0;i<citations.size();i++)
    	 {

    		 System.out.println(citations.get(i));}


    	 System.out.println("-----------TEST AVANT-------------");
    	 System.out.println("-----------TEST APRES-------------");
    	 citations=organiseListe(citations);
    	 for(int i=0;i<citations.size();i++)
    	 {

    		 System.out.println(citations.get(i));}


    	 System.out.println("-----------TEST APRES-------------");


    	return br;
    }

    private ArrayList<String> splitCitation(String ligne,
			ArrayList<String> citations) {
    	//System.out.println("taille ligne == "+ligne.length());
    	Pattern p = Pattern.compile("^[a-z0-9]*");
    	int position=0;
    	int nextCitationStart=0;
    	int point = ligne.indexOf(".");
    	int pointavant=0;
    	int debutCitation=0;
    	String ligneT;
    	String recherche;
    	boolean passeWhile=false;
    	String ajout=null;
    	boolean debutcitationOk=false;


    		//on cherche le debut de citation actuel
    		ligneT=ligne.substring(pointavant,point);
    		Matcher m = p.matcher(ligneT);
    		if(ligneT.length()<=3&&m.matches())
    		{
    			debutCitation=point;
    		}
    		else
    		{
    			//dans ce cas on doit chercher le début de la citation
				while(point!=-1&&!debutcitationOk)
				{
					point = ligne.indexOf(".",point+1);
					//on a besoin du dernier espace avant le point trouvé : ligne.substring(point).lastIndexOf(" ")
					ligneT=ligne.substring(ligne.substring(point).lastIndexOf(" "),point);
					Matcher m2 = p.matcher(ligneT);
					if(m2.matches())
					{
						//si on la trouvé
						debutCitation=point;
						debutcitationOk=true;
					}


				}

    		}

    	//System.out.println("debutCitation= "+debutCitation);
    	position=debutCitation+1;
    	while(nextCitations(ligne, position)!=-1)
    	{	//System.out.println("next citation renvoie "+nextCitations(ligne, position));
    		nextCitationStart=nextCitations(ligne, position);
    		ajout=ligne.substring(position-(ligneT.length()+1),nextCitationStart).trim();
    		citations.add(ajout);
    		System.out.println("dans le while on ajoute "+(ajout));
    		position=nextCitationStart+1;

    		//ligne=ligne.substring(position);
    		passeWhile=true;

    	}
    	//System.out.println("on sort du while "+position +" "+ligne.length());
    	//System.out.println("on trouve le string "+ligne.substring(position,ligne.length()));
    	if(!passeWhile){
    		ajout=ligne.substring(position-(ligneT.length()+1),ligne.length()).trim();
    	citations.add(ajout);
    	System.out.println("a la fin on ajoute "+(ajout));
    	}
    	else
    	{
    		ajout=ligne.substring(position,ligne.length()).trim();
    		citations.add(ajout);
        	System.out.println("a la fin on ajoute "+(ajout));
    	}




		return citations;
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
    	int pointavant=0;
    	String ligneT;
    	//System.out.println("on cherche avec la ligne "+ligne);
    	Pattern p = Pattern.compile("^[a-z0-9]*");
    	//System.out.println("ligne ="+ligne);
    	if(point==-1){
    		//si il n'y a pas de "." alors ca ne peut pas être une citation
    		//on considére qu'il n'y aura jamais + de 1000 citation par texte
    		return false;
    	}
    	while(point!=-1)
    	{	//System.out.println("la1");

    		//on récupére ce qu'il y a avant le point
    		//on commence par faire une sous chaine du debut jusqu'au point
    			//on récupére ce qu'il y a avant le point
    			if(point==pointavant)
    			{
    				//dans ce cas le point est a la position 0
    				ligneT="plusde4commecaOnChercheLeProchain";
    			}
    			else
    				{ligneT=ligne.substring(pointavant, point);
    			//System.out.println("la2");
    			}
    			//System.out.println("premier while + ligneT "+ligneT);
    			//System.out.println("point avant "+pointavant+" pointapres "+point);
    			//System.out.println(ligneT);
    			if(ligneT.length()<=3)
    			{

    				return true;//dans ce cas on sais qu'il y a des citations , sinon on continue de chercher
    			}
    			else
    			{
    				pointavant= point;
    				point = ligne.indexOf(".",point+1);
    				//System.out.println("la3");

    					//tant que l'on a pas fini de chercher
    					while (point!=-1)
    					{

    						ligneT=ligne.substring(pointavant, point);//on récupére l'ensemble jusqu'au prochain point
    						//System.out.println("deuxieme while + ligneT "+ligneT);
    						if(ligneT.lastIndexOf(' ')==-1)
    						{
    							//System.out.println("renvoie true");
    							return false;
    						}
    						ligneT=ligneT.substring(ligneT.lastIndexOf(' '), ligneT.length());//on récupére juste le point
    						//System.out.println("deuxieme passage deuxieme while + ligneT "+ligneT);
    						//on doit également vérifié que ca ne contiens pas d'espace
    						Matcher m = p.matcher(ligneT);
    						//System.out.println(ligneT);
    						boolean casFauxPositif = ligneT.equals(" p")||ligneT.equals("(éd.");//ici on va géré les "faux positif"
    						//System.out.println(casFauxPositif);
    						if(ligneT.length()<=3&&m.matches()&&ligneT.length()>0&&!casFauxPositif)
    		    			{

    		    				return true;//dans ce cas on sais qu'il y a des citations , sinon on continue de chercher
    		    			}
    						else
    						{
    							pointavant= point;
    		    				point = ligne.indexOf(".",point+1);

    						}

    					}



    			}

    	return false;


    	}
		return false;

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
     * renvoie la position du debut de la prochaine citation
     * @param position la position de la derniere citation trouvé
     * @param ligne le paragraphe ou l'on chercher
     * @return -1 si il n'y a plus de citation sinon renvoie la position de la prochaine citation
     */
    private int nextCitations(String ligne,int position)
    {

    	boolean verif=true;
    	int i=0;
    	String numero;
    	int pointAvant=0;
    	int point=0;
    	int testE;
    	boolean fauxPositif=true;
    	int taille = 0;
    	String recherche=ligne.substring(position);
    	System.out.println("recherche ="+recherche);
    	Pattern p = Pattern.compile("[0-9]*");
    	while(recherche.indexOf(".",i)!=-1&&verif){
    		//System.out.println("on plante sur test en fait");
    		if(recherche.indexOf(".",i+1)==-1)
    		{
    			//il faut arrété la rechercher maintenant ,
    			//System.out.println("arrétons la recherche");
    			return -1;
    		}
    		else{	pointAvant=point;
    				point = recherche.indexOf(".",i);
    				testE = recherche.substring(pointAvant, point).lastIndexOf(" ");
    				System.out.println("point "+point);
    				System.out.println("testE "+testE+ " la chaine= "+recherche.substring(pointAvant, point));
    				//System.out.println("pointavant "+pointAvant);
    				//on récupére le "numero" avant le prochain point
    				if(testE==-1){
    					numero = recherche.substring(pointAvant, point);
    					System.out.println("IF  numero ="+numero);
    				}
    				else
    				{
    					numero = recherche.substring(testE+pointAvant, point);
    					numero = numero.trim();
    					taille=numero.length();
    					System.out.println("ELSE  numero ="+numero);
    				}
    				//on verifie si on a le resultat
    				System.out.println("numero= "+numero);
    				Matcher m = p.matcher(numero);
    				fauxPositif=!numero.equals("p")&&!numero.equals("vol")&&!numero.equals("op")&&!numero.equals("cit")&&!numero.equals("u")&&!numero.equals("cf")&&!numero.equals("etc")&&!numero.equals("Cf");
    				System.out.println("le match "+m.matches()+ " num = p "+!numero.equals("p")+" la taille "+numero.length());
    				if(m.matches()&&fauxPositif&&numero.length()<=3)
    				{
    					System.out.println("on renvoie le point "+point);

    					return point-taille+position;

    				}
    				else
    				{
    					//on continue a chercher
    					System.out.println("le else");
    					//pointAvant=point;
    					//dans le cas ou on  a trouve une page il faut ignorer ce qu'il y a avant le prochain point car il s'agira du numero de page
    					//System.out.println("on met precedent p a"+numero.equals("p"));
    					if(numero.equals("p")||numero.equals("chap"))
    					{	System.out.println("on est rentré dans le egale p point ="+point);
    						i = point +1;
    						point =recherche.indexOf(".",i);
    						System.out.println("point = "+point);
    					}
    					pointAvant=point+1;
    					i = point +1;
    					//System.out.println("i devient "+i);
    					//pointAvant=point+1;
    					//System.out.println("point avant devient "+pointAvant);
    				}



    	}

    	}

    		//System.out.println("on renvoie "+retour);
    	return -1;

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


    public void creationListeNumero()
    {
    	//on ajout de 1 a 8
    	for (int i=1;i<9;i++)
    	{
    		numeros.add(Integer.toString(i));
    	}
    	numeros.add("4");
    	numeros.add("1?");
    	for (int i=11;i<17;i++)
    	//de 11 a 17
    	{
    		numeros.add(Integer.toString(i));
    	}
    	numeros.add("IX");
    	numeros.add("14");
    	for (int i=20;i<70;i++)
        	//de 60 a 68
        	{
        		numeros.add(Integer.toString(i));
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
package pdfToText;

/**
 * Cette classe permet l'extraction des données d'un fichier PDF
 * @author Maxime Chaste
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import db.Auteur;

public class ExtractionTexte {



	File file;


		public ExtractionTexte(String fileName)
		{
			file = new File(fileName);
		}
	/**
	 *  Permet d'extraire le texte d'un fichier
	 * @param fileName le fichier dont on souhaite extraire le texte
	 * @return l'ensemble du texte du fichier pdf
	 */

		public String pdftoText() {
			PDFParser parser;
			String parsedText = null;;
			PDFTextStripper pdfStripper = null;
			PDDocument pdDoc = null;
			COSDocument cosDoc = null;
			String tripleSautDeLigne=System.getProperty("line.separator")+System.getProperty("line.separator")+System.getProperty("line.separator");

			//vérification que le fichier existe
			if (!file.isFile()) {
				System.err.println("le fichier " + file.getName() + " n'existe pas.");
				return null;
			}

			try {
				parser = new PDFParser(new FileInputStream(file));
			}
			catch (IOException e) {
				System.err.println("impossible d'ouvrir. " + e.getMessage());
				return null;
			}

			try {
				parser.parse();
				cosDoc = parser.getDocument();
				pdfStripper = new PDFTextStripper();

				pdDoc = new PDDocument(cosDoc);
				pdfStripper.setStartPage(1);
				//System.out.println(pdfStripper.getStartPage());

				//ajout d'un début de page
				pdfStripper.setPageStart(System.getProperty("line.separator")+ " Debut de page "+tripleSautDeLigne);

				//ajout d'un "fin de page"
				pdfStripper.setPageEnd(System.getProperty("line.separator")+ " fin de page "+tripleSautDeLigne);

				//ajout d'un"debut de paragraphe"
				pdfStripper.setParagraphStart(System.getProperty("line.separator")+"Debut de paragraphe"+System.getProperty("line.separator"));

				//ajout d'un"fin de paragraphe"
				pdfStripper.setParagraphEnd(System.getProperty("line.separator")+"Fin de paragraphe"+System.getProperty("line.separator"));
				//pdfStripper.setEndPage(2);

				parsedText = pdfStripper.getText(pdDoc);//récupération du texte
			} catch (Exception e) {
				System.err
						.println("An exception occured in parsing the PDF Document."
								+ e.getMessage());
			}
			finally {
				try {
					if (cosDoc != null)
						cosDoc.close();//fermeture du document
					if (pdDoc != null)
						pdDoc.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return parsedText;
		}


		/**
		 * (Surement temporaire) Permet de crée un fichier contenant le texte placé en parametre
		 *  @param texte le texte que l'on ecrira dans le fichier
		 */
		void creationFichier(String texte,String nomFichier)
		{
			File monFichier=new File(nomFichier);

			//creation de fichier
			try
			{


			//si le fichier n'existe pas je le crée
			if(!monFichier.exists())
			{
			monFichier.createNewFile();
			}
			else
			{
				monFichier.delete();
				monFichier.createNewFile();
			}
			}
			catch (IOException e)
			{
			System.out.println("Impossible de créer le fichier");
			}


			//ecriture du texte
			FileWriter writer = null;
			try{
			     writer = new FileWriter(monFichier, true);
			     writer.write(texte,0,texte.length());
			}catch(IOException ex){
			    ex.printStackTrace();
			}finally{
			  if(writer != null){
			     try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			  }
			}




		}








}

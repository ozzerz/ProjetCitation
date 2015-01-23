package testUnitaire;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.TestCase;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import bddHelper.FormatageCitation;
import pdfToText.EnregistrementCitation;

public class FormatageCitationTestCase extends TestCase {

	FormatageCitation fc;
	String minidb="miniNom.txt";
	@Override
    protected void setUp() throws Exception
    {
        super.setUp();
        this.fc = new FormatageCitation("bdd.xml");
    }

	@Test
	public void test_mettreEnForme() {
		assertEquals("B.",fc.mettreEnForme("Basil", "B."));
		assertEquals("B.",fc.mettreEnForme("Basil", "C."));
		assertEquals("K.",fc.mettreEnForme("Koala", "B."));
		assertEquals("Basil",fc.mettreEnForme("Basil", "UnmotLong"));
		assertEquals("Bas.",fc.mettreEnForme("Basil", "aer."));

	}

	@Test
	public void test_getAuteurCitation() {
		String citationA="1. Il fut un temps ou Albert Camus.";
		assertEquals("Albert Camus",fc.getAuteurCitation(citationA, minidb));
		//test nom prenom inversé
		citationA="1. Il fut un temps ou Camus Albert.";
		assertEquals("Albert Camus",fc.getAuteurCitation(citationA, minidb));
		//test avec une erreur dans le nom
		citationA="1. Il fut un temps ou Camis Albert.";
		assertEquals("Albert Camus",fc.getAuteurCitation(citationA, minidb));
		//test sur une autre position
		citationA="1. et Camus Albert est un être vivant.";
		assertEquals("Albert Camus",fc.getAuteurCitation(citationA, minidb));
		//test un ; dans la BDD
		citationA="1. Il fut un temps ou Basil bleu.";
		assertEquals("B. bleu",fc.getAuteurCitation(citationA, minidb));
		//test ou l'on ne trouve rien
		citationA="1. Je suis un aigle";
		assertNull(fc.getAuteurCitation(citationA, minidb));

	}

}

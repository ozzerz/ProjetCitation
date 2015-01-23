package testUnitaire;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import pdfToText.EnregistrementCitation;
import bddHelper.CalculUtilisationMot;

public class EnregistrementCitationTestCase extends TestCase {

	EnregistrementCitation er;
	@Override
    protected void setUp() throws Exception
    {
        super.setUp();
        SAXBuilder sxb = new SAXBuilder();
        Document doc = sxb.build(new File("bdd.xml"));
        this.er = new EnregistrementCitation("monTitre",doc,null);
    }

	@Test
	public void test_splitCitation() {
		String paragraphe="1. une citation genial. 2. une deuxieme citation.";
		ArrayList<String> retourAttendu = new ArrayList<String>();
		retourAttendu.add("1. une citation genial.");
		retourAttendu.add("2. une deuxieme citation.");
		assertEquals(retourAttendu, er.splitCitation(paragraphe,new ArrayList<String>()));


		paragraphe="un texte totalement inutile suivi de la citation. 1. une citation genial.";
		retourAttendu.remove(1);
		assertEquals(retourAttendu, er.splitCitation(paragraphe,new ArrayList<String>()));


	}

	@Test
	public void test_isCitations() throws InterruptedException {
		assertTrue(er.isCitations("1. une citation genial. 2. une deuxieme citation."));
		assertFalse(er.isCitations("Ce bout de texte ne contiendra pas de citation. Même pas dans cette deuxieme partie"));
	}

	@Test
	public void test_nextCitations(){
	assertEquals(1, er.nextCitations("1. une citation genial. 2. une deuxieme citation.", 0));
	assertEquals(24, er.nextCitations("1. une citation genial. 2. une deuxieme citation.", 1));


	}
}

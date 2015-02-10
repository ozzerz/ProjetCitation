package testUnitaire;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.junit.Test;

import bddHelper.CalculUtilisationMot;
/**
 * Contient les tests pour la classe CalculUtilisationMot
 * @author Ozzerz
 *
 */
public class CalculUtilisationMotTestCase extends TestCase {

	CalculUtilisationMot calc;
	@Override
    protected void setUp() throws Exception
    {
        super.setUp();
        SAXBuilder sxb = new SAXBuilder();
        Document doc = sxb.build(new File("bdd.xml"));
        this.calc = new CalculUtilisationMot(doc);
    }

	public void tearDown(  ) {
        this.calc=null;
    }

	@Test
	public void test_motUtiliseNouMoinsDefois() {

			//on va ajouter des mots dans la liste
			calc.mots.add("mots4Fois");
			calc.nbUtilisation.add(4);
			calc.mots.add("mots3Fois");
			calc.nbUtilisation.add(3);
			calc.mots.add("mots3FoisAussi");
			calc.nbUtilisation.add(3);
			calc.mots.add("mots2Fois");
			calc.nbUtilisation.add(2);
			calc.mots.add("mots2FoisAussi");
			calc.nbUtilisation.add(2);
			//on va préparer les retours attendus
			ArrayList<String>retour3MotAttendu=new ArrayList<String>();
			retour3MotAttendu.add("mots3Fois");
			retour3MotAttendu.add("mots3FoisAussi");
			retour3MotAttendu.add("mots2Fois");
			retour3MotAttendu.add("mots2FoisAussi");
			ArrayList<String>retour2MotAttendu=new ArrayList<String>();
			retour2MotAttendu.add("mots2Fois");
			retour2MotAttendu.add("mots2FoisAussi");

			ArrayList<String>retour3Mot=calc.motUtiliseNouMoinsDefois(3);
			ArrayList<String>retour2Mot=calc.motUtiliseNouMoinsDefois(2);

			//cas pour 3 mots
			assertEquals(retour3MotAttendu,retour3Mot);
			assertTrue(retour3Mot.contains("mots3Fois"));
			assertTrue(retour3Mot.contains("mots3FoisAussi"));
			assertTrue(retour3Mot.contains("mots2Fois"));
			assertTrue(retour3Mot.contains("mots2FoisAussi"));
			assertFalse(retour3Mot.contains("mots4Fois"));

			//cas pour 2 mots
			assertEquals(retour2MotAttendu,retour2Mot);
			assertTrue(retour2Mot.contains("mots2Fois"));
			assertTrue(retour2Mot.contains("mots2FoisAussi"));
			assertFalse(retour2Mot.contains("mots3Fois"));
			assertFalse(retour2Mot.contains("mots3FoisAussi"));
			assertFalse(retour2Mot.contains("mots4Fois"));




	}
	@Test
	public void test_addMot() {
		//preparation des elements
		ArrayList<String>retourMotAttendu=new ArrayList<String>();
		ArrayList<Integer> retourNbMotAttendu=new ArrayList<Integer>();
		calc.addMot("mot");
		retourMotAttendu.add("mot");
		retourNbMotAttendu.add(1);
		assertEquals(retourMotAttendu,calc.mots);
		assertEquals(retourNbMotAttendu,calc.nbUtilisation);
		calc.addMot("mot");
		retourNbMotAttendu.set(0, 2);
		assertEquals(retourMotAttendu,calc.mots);
		assertEquals(retourNbMotAttendu,calc.nbUtilisation);
		calc.addMot("bleu");
		retourMotAttendu.add("bleu");
		retourNbMotAttendu.add(1);
		assertEquals(retourMotAttendu,calc.mots);
		assertEquals(retourNbMotAttendu,calc.nbUtilisation);


	}

	@Test
	public void test_compteMotUneCitation() {
			String citation ="1. Je suis un aigle aigle aigle.";
			calc.compteMotUneCitation(citation);
			//preparation des résultats attendus
			ArrayList<String>retourMotAttendu=new ArrayList<String>();
			ArrayList<Integer> retourNbMotAttendu=new ArrayList<Integer>();
			retourMotAttendu.add("Je");
			retourMotAttendu.add("suis");
			retourMotAttendu.add("un");
			retourMotAttendu.add("aigle");
			retourNbMotAttendu.add(1);
			retourNbMotAttendu.add(1);
			retourNbMotAttendu.add(1);
			retourNbMotAttendu.add(3);
			assertEquals(retourMotAttendu,calc.mots);
			assertEquals(retourNbMotAttendu,calc.nbUtilisation);



	}




}

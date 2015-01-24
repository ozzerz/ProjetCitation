package testUnitaire;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import bddHelper.GestionErreur;


public class GestionErreurTestCase extends TestCase {
	GestionErreur ge;
	@Override
    protected void setUp() throws Exception
    {
        super.setUp();

        this.ge = new GestionErreur();
    }

	@Test
	public void test_citationComplete() {
		//le cas ou le point n'est pas bien placé
		assertFalse(ge.citationComplete("Agrougroum."));
		//cas ou il n'y a pas de point
		assertFalse(ge.citationComplete("Bonjour"));
		//le cas ou il n'y a pas de numero
		assertFalse(ge.citationComplete("/."));
		//si la citation ne termine pas par un point
		assertFalse(ge.citationComplete("1. une citation"));
		//un cas ou la citation est correct
		assertTrue(ge.citationComplete("1. une citation."));

	}

	@Test
	public void test_stockNumero() {
		//on prepare les citations
		ArrayList<String> citations = new ArrayList<String>();
		citations.add("1. une citation.");
		citations.add("2. une citation.");
		citations.add("3. une citation mal formé");
		ArrayList<String> resultatAttendu = new ArrayList<String>();
		resultatAttendu.add("1");
		resultatAttendu.add("2");
		resultatAttendu.add("3");
		ge.stockNumero(citations);
		assertEquals(resultatAttendu, ge.numero);

	}

	@Test
	public void test_numeroManquant() {

		ArrayList<Integer> resultatAttendu = new ArrayList<Integer>();
		resultatAttendu.add(3);
		ge.numero.add("1");
		ge.numero.add("2");
		ge.numero.add("4");
		assertEquals(resultatAttendu, ge.numeroManquant());
		//cas ou il manque 2 numero d'affile
		ge.numero.remove(2);
		ge.numero.add("5");
		resultatAttendu.add(4);
		assertEquals(resultatAttendu, ge.numeroManquant());
		//cas ou il ne manque rien
		ge.numero.clear();
		ge.numero.add("1");
		ge.numero.add("2");
		ge.numero.add("3");
		resultatAttendu.clear();
		assertEquals(resultatAttendu, ge.numeroManquant());
	}

	@Test
	public void test_numeroDuplique() {
		ArrayList<String> resultatAttendu = new ArrayList<String>();
		resultatAttendu.add("3");
		ge.numero.add("1");
		ge.numero.add("3");
		ge.numero.add("3");
		assertEquals(resultatAttendu, ge.numeroDuplique());
		//cas ou il y 'a beaucoup de répetiton
		ge.numero.add("1");
		ge.numero.add("3");
		ge.numero.add("3");
		resultatAttendu.clear();
		resultatAttendu.add("1");
		resultatAttendu.add("3");
		assertEquals(resultatAttendu, ge.numeroDuplique());
		//cas ou il n'y a aucune répétition
		ge.numero.clear();
		resultatAttendu.clear();
		ge.numero.add("1");
		ge.numero.add("2");
		ge.numero.add("3");
		assertEquals(resultatAttendu, ge.numeroDuplique());
	}



}

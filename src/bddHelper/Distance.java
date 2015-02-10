package bddHelper;
/**
 * Cette classe a pour but d'�tre utiliser pour modifier la citation � l'aide du document contenant tout les noms pour am�liorer la citation
 *On pourra �galement l"utiliser pour r�cup�rer le nom d'un auteur cit� dans une citation et de ce fait changer la base de donn�e pour changer
 *
 * <citation>2. Pour un expos� plus complet de ce point de vue, cf. Leonard  Krieger, "The Horizons of History"  American Historical Review,  vol. 63, 1957-1958, p. 62-74.</citation>
 * en
 * <citation>
 * <lacitation>2. Pour un expos� plus complet de  ce point de vue, cf. Leonard  Krieger, "The Horizons of History"  American Historical Review,  vol. 63, 1957-1958, p. 62-74.
 * </lacitation>
 * <auteursCite>
 * 	<auteurCite>
 * 	L. Krieger
 * 	</auteurCite>
 * </auteursCite>
 *</citation>
 *
 * @author Ozzerz
 *
 */
public class Distance {


	public Distance ()
	{
	}

	/**
	 * Renvoie la distance de Levenshtein entre 2 mots
	 * @param s0 le premier string
	 * @param s1 le deuxieme String
	 * @return la distance d'�dition entre les 2 Strings
	 */
	public int LevenshteinDistance (String s0, String s1) {
	    int len0 = s0.length() + 1;
	    int len1 = s1.length() + 1;

	    // the array of distances
	    int[] cost = new int[len0];
	    int[] newcost = new int[len0];

	    // initial cost of skipping prefix in String s0
	    for (int i = 0; i < len0; i++) cost[i] = i;

	    // dynamicaly computing the array of distances

	    // transformation cost for each letter in s1
	    for (int j = 1; j < len1; j++) {
	        // initial cost of skipping prefix in String s1
	        newcost[0] = j;

	        // transformation cost for each letter in s0
	        for(int i = 1; i < len0; i++) {
	            // matching current letters in both strings
	            int match = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;

	            // computing cost for each transformation
	            int cost_replace = cost[i - 1] + match;
	            int cost_insert  = cost[i] + 1;
	            int cost_delete  = newcost[i - 1] + 1;

	            // keep minimum cost
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	        }

	        // swap cost/newcost arrays
	        int[] swap = cost; cost = newcost; newcost = swap;
	    }

	    // the distance is the cost for transforming all letters in both strings
	    return cost[len0 - 1];
	}

}

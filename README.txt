--------------------------------Séance du 17 Octobre-------------------------------------------------
Ajout de fonction  testAjoutAuteur permettant d'ajouter un auteur si nécessaire
Ajout de fonction  testAjoutTitre permettant d'ajouter un auteur si nécessaire






--------------------------------Hors Séance du 14 Octobre-------------------------------------------------

- les fonctions de récupération de donnée semble correct -getCitations est donc débugué (aucun ajout en DB pour l'instant)
Cependant lors de mes test sur le document Violence et médiation. Théorie de la segmentarité ou pratiques juridiques en Kabylie
J'ai remarqué qu'il me manqué des citations j'ai donc regardé le .txt généré avec PDFBOX pour découvrir ceci
La citation 9. est devenu 4. (donc j'en déduit que le chiffre 9 n'est pas bien "récupéré"
10 deviens 1?
VU que je cherche paragraphe par paragraphe , je ne trouve pas les citation 11 et 12 
16 devient lft.
Pour les meme raison je ne trouve donc pas la 17
18 devient IX
19 devient 14 (on peux donc vraiment en déduire que PDFBOX ne gére pas les "9"

Je dois donc réfléchir à la maniére de passé outre
Il me faut aussi réfléchir a comment avoir entiérement les citation lorsque celle ci sont sur plusieurs paragraphe

Ma fonction est donc loin d'etre complete




--------------------------------Séance du 9 Octobre-------------------------------------------------

-Ajout de la fonction oeuvreDontExist(String nomAuteur,String nomOeuvre) qui me permet de savoir si une oeuvre n'est pas déja dans notre Base de données
-Ajout de la fonction auteurDontExist(String nom) qui me permet de savoir si un auteur n'est pas déja dans notre Base de données

Ces 2 fonctions me permette de m'assurer de l'unicité des oeuvres et des auteurs en Base de donnée , pour de futur évolution (notament avec l'interface graphique) elles seront trés utiles si jamais une personne met 2 fois le même PDF

-Ecriture de la fonction nextCitations(String ligne,int position,String nombreRechercher)qui a partir d'un paragraphe (pouvant contenir plusieurs citation) me permet de récupéré la position dans ce paragraphe ,Si il n'y en a plus on renvoie -1
-Ecriture de la fonction getCitations(String ligne) ; qui en utilisant la fonction précendente me permet de récupéré l'ensemble des citations d'un paragraphe <== Pour l'instant celle ci n'est pas compléte , dans le cas ou il n'y a qu'une seule citation cela marche bien , mais pour l'instant un soucis demeure dans le cas ou il y ' en a plusieur (donc dans 99% des cas)
-Ecriture de la fonction isCitations(String ligne) ; qui a partir d'un paragraphe me permet de determiné si celui ci contiens des citations <== Cette fonction est utilisé pour filtré l'ensemble du texte et ne gardé que les "paragraphes" contenant des citations
-Ecriture de la fonction getCitation(String nomFichier , BufferedReader br) ; qui me permet de récupéré une liste contenant TOUTES les citations du PDF en cours de traitement <== Celle ci n'est pas encore testé car elle dépend de la fonction getCitations qui n'est pas encore efficace
 
Résumé : donc pour l'instant j'arrive a récupéré chaque paragraphe contenant des citations mais je ne l'ai traite pas encore correctement

Pour l'instant le stockage des données ressemble a ça
<auteur>
    <nom>Hans Medick</nom>
    <oeuvre nom=" « Missionnaires en canot ». Les modes de connaissance ethnologiques, un défi à l'histoire sociale ? In: Genèses, 1, 1990. pp. 24-46." />
  </auteur>

J'aimerais donc avoir la prochaine séance quelque chose comme ca 

<auteur>
    <nom>Hans Medick</nom>
    <oeuvre nom=" « Missionnaires en canot ». Les modes de connaissance ethnologiques, un défi à l'histoire sociale ? In: Genèses, 1, 1990. pp. 24-46." />
    <citations>
        <citations id= "le numéro de la citation dans le texte">La citation</citation>
    </citations>
  </auteur>


Travail fais
    -transformation du PDF en fichier texte
    -ajout dans ce fichier texte de repére pour pouvoir selectionner les infos
    POUR CHAQUE ARTICLE (actuellement je test avec 3)
        -on récupére les auteurs du textes
        -on verifie que ces auteurs n'existe pas déja dans la base de donnée  si il existe on ne fais rien , sinon on les ajoutes
        -on récupére le titre
        -on verifie que le titre n'existe pas déja dans la base de donnée pour les auteurs si il existe on ne fais rien , sinon on ajoute cette oeuvre pour chaque auteurs
        -on parcours l'ensemble du texte restant et on garde chaque paragraphes contenant des citations
        POUR CHAQUE PARAGRAPHE
        <==dans la prochaine on ajoutera les citations dans la base ,+ possible vérification de leur existance 
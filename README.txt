--------------------------------S�ance du 17 Octobre-------------------------------------------------
Ajout de fonction  testAjoutAuteur permettant d'ajouter un auteur si n�cessaire
Ajout de fonction  testAjoutTitre permettant d'ajouter un auteur si n�cessaire






--------------------------------Hors S�ance du 14 Octobre-------------------------------------------------

- les fonctions de r�cup�ration de donn�e semble correct -getCitations est donc d�bugu� (aucun ajout en DB pour l'instant)
Cependant lors de mes test sur le document Violence et m�diation. Th�orie de la segmentarit� ou pratiques juridiques en Kabylie
J'ai remarqu� qu'il me manqu� des citations j'ai donc regard� le .txt g�n�r� avec PDFBOX pour d�couvrir ceci
La citation 9. est devenu 4. (donc j'en d�duit que le chiffre 9 n'est pas bien "r�cup�r�"
10 deviens 1?
VU que je cherche paragraphe par paragraphe , je ne trouve pas les citation 11 et 12 
16 devient lft.
Pour les meme raison je ne trouve donc pas la 17
18 devient IX
19 devient 14 (on peux donc vraiment en d�duire que PDFBOX ne g�re pas les "9"

Je dois donc r�fl�chir � la mani�re de pass� outre
Il me faut aussi r�fl�chir a comment avoir enti�rement les citation lorsque celle ci sont sur plusieurs paragraphe

Ma fonction est donc loin d'etre complete




--------------------------------S�ance du 9 Octobre-------------------------------------------------

-Ajout de la fonction oeuvreDontExist(String nomAuteur,String nomOeuvre) qui me permet de savoir si une oeuvre n'est pas d�ja dans notre Base de donn�es
-Ajout de la fonction auteurDontExist(String nom) qui me permet de savoir si un auteur n'est pas d�ja dans notre Base de donn�es

Ces 2 fonctions me permette de m'assurer de l'unicit� des oeuvres et des auteurs en Base de donn�e , pour de futur �volution (notament avec l'interface graphique) elles seront tr�s utiles si jamais une personne met 2 fois le m�me PDF

-Ecriture de la fonction nextCitations(String ligne,int position,String nombreRechercher)qui a partir d'un paragraphe (pouvant contenir plusieurs citation) me permet de r�cup�r� la position dans ce paragraphe ,Si il n'y en a plus on renvoie -1
-Ecriture de la fonction getCitations(String ligne) ; qui en utilisant la fonction pr�cendente me permet de r�cup�r� l'ensemble des citations d'un paragraphe <== Pour l'instant celle ci n'est pas compl�te , dans le cas ou il n'y a qu'une seule citation cela marche bien , mais pour l'instant un soucis demeure dans le cas ou il y ' en a plusieur (donc dans 99% des cas)
-Ecriture de la fonction isCitations(String ligne) ; qui a partir d'un paragraphe me permet de determin� si celui ci contiens des citations <== Cette fonction est utilis� pour filtr� l'ensemble du texte et ne gard� que les "paragraphes" contenant des citations
-Ecriture de la fonction getCitation(String nomFichier , BufferedReader br) ; qui me permet de r�cup�r� une liste contenant TOUTES les citations du PDF en cours de traitement <== Celle ci n'est pas encore test� car elle d�pend de la fonction getCitations qui n'est pas encore efficace
 
R�sum� : donc pour l'instant j'arrive a r�cup�r� chaque paragraphe contenant des citations mais je ne l'ai traite pas encore correctement

Pour l'instant le stockage des donn�es ressemble a �a
<auteur>
    <nom>Hans Medick</nom>
    <oeuvre nom=" � Missionnaires en canot �. Les modes de connaissance ethnologiques, un d�fi � l'histoire sociale ? In: Gen�ses, 1, 1990. pp. 24-46." />
  </auteur>

J'aimerais donc avoir la prochaine s�ance quelque chose comme ca 

<auteur>
    <nom>Hans Medick</nom>
    <oeuvre nom=" � Missionnaires en canot �. Les modes de connaissance ethnologiques, un d�fi � l'histoire sociale ? In: Gen�ses, 1, 1990. pp. 24-46." />
    <citations>
        <citations id= "le num�ro de la citation dans le texte">La citation</citation>
    </citations>
  </auteur>


Travail fais
    -transformation du PDF en fichier texte
    -ajout dans ce fichier texte de rep�re pour pouvoir selectionner les infos
    POUR CHAQUE ARTICLE (actuellement je test avec 3)
        -on r�cup�re les auteurs du textes
        -on verifie que ces auteurs n'existe pas d�ja dans la base de donn�e  si il existe on ne fais rien , sinon on les ajoutes
        -on r�cup�re le titre
        -on verifie que le titre n'existe pas d�ja dans la base de donn�e pour les auteurs si il existe on ne fais rien , sinon on ajoute cette oeuvre pour chaque auteurs
        -on parcours l'ensemble du texte restant et on garde chaque paragraphes contenant des citations
        POUR CHAQUE PARAGRAPHE
        <==dans la prochaine on ajoutera les citations dans la base ,+ possible v�rification de leur existance 
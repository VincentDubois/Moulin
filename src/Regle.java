public class Regle {

	private static final String[] statut = {
		"Au joueur noir de placer un pion",
		"Au joueur blanc de placer un pion"
	};

	public static final int VIDE = 0;
	public static final int NOIR = 1;
	public static final int BLANC = 2;
	
	public static final int POSE = 0;
	
	public static class Position {
		int x,y; // coordonnées		
		int contenu; //contenu pour chaque position : 0=vide, 1=noir, 2=blanc 
	}

	public static class Jeu {
		Position[] position; // emplacements possibles
		Position[][] ligne; 
	
		int joueur; // 1 : noir / 2 : blanc
		int statut; // état du jeu :
		// 0 : pose		
	}


	public static Jeu createJeu() {
		Jeu resultat = new Jeu();
 		resultat.position = new Position[9];

		for(int i=0; i<9;++i){
			resultat.position[i] =  new Position();
			resultat.position[i].x = (i%3)*2;
			resultat.position[i].y = (i/3)*2;
			resultat.position[i].contenu = VIDE;
		}

		resultat.ligne = new Position[8][3];
		for(int i=0; i<3 ; ++i){
			for(int j = 0; j<3 ; ++j){
				resultat.ligne[i][j] = resultat.position[i*3+j];
				resultat.ligne[i+3][j] = resultat.position[j*3+i];
			}
			resultat.ligne[6][i] = resultat.position[i*4];
			resultat.ligne[7][i] = resultat.position[2+i*2];			
		}

		resultat.statut = POSE;
		resultat.joueur = NOIR;


		return resultat;
	}
	
	public static int get(Jeu jeu, int ndx){
		return jeu.position[ndx].contenu;
	}	

	public static boolean canPlay(Jeu jeu, int ndx) {
		return true;
	}

	public static void play(Jeu jeu, int ndx) {
		jeu.position[ndx].contenu = jeu.joueur;
	}


	/***
	 * Fonction appelée quand un joueur clique sur une case.
	 * 
	 * @param jeu
	 * @param ndx
	 * @return true si le jeu a été mofifié et qu'il faut rafraîchir le dessin.
	 */
	public static boolean onClick(Jeu jeu, int ndx) {
		if (canPlay(jeu,ndx)){
			play(jeu,ndx);
			return true;
		} 
		return false;
	}

	
	
	public static boolean canMove(Jeu jeu, int fromNdx, int toNdx) {
		return false;
	}
	
	/***
	 * Fonction appelée quand un joueur fait un drag and drop.
	 * Si le coup est valide, il faut déplacer le pion
	 * 
	 * @param jeu
	 * @param fromNdx
	 * @param toNdx
	 * @return
	 */
	public static boolean onDragAndDrop(Jeu jeu, int fromNdx, int toNdx) {
		return false;
	}


	/***
	 * Génère le texte à afficher pour le statut
	 * @param jeu
	 * @return
	 */
	public static String getStatus(Jeu jeu){
		return statut[jeu.joueur-1];		
	}
	


	public static boolean estDansLigneComplete(Jeu jeu, int ndx) {
		return false;
	}
	
/////////////////////////////////////////////////////////
////////////// Gestion de l'historique //////////////////
/////////////////////////////////////////////////////////

	public static Jeu getStart(Jeu jeu) {
		return null;
	}

	public static Jeu getPrevious(Jeu jeu) {
		return null;
	}

	public static Jeu getNext(Jeu jeu) {
		return null;
	}

	public static Jeu getLast(Jeu jeu) {
		return null;
	}

////////////////////// Main ////////////////////////////

	public static void main(String[] arg){
		Plateau.createMarelle(true);		
	}
	
}

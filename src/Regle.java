public class Regle {

	private static final String[] statut = {
		"Au joueur noir de placer un pion",
		"Au joueur blanc de placer un pion",
		"Au joueur noir de déplacer un pion",
		"Au joueur blanc de déplacer un pion",
		"Au joueur noir de retirer un pion",
		"Au joueur blanc de retirer un pion",
		"Les noirs ont gagné !",
		"les blancs ont gagné !"
	};

	public static final int VIDE = 0;
	public static final int NOIR = 1;
	public static final int BLANC = 2;
	
	public static final int POSE = 0;
	public static final int MOUVEMENT = 1;
	public static final int RETRAIT = 2;
	public static final int GAGNE = 3;

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
		// 1 : mouvement		
		// 2 : retrait		
		// 3 : fin
		int coup;
	}


	public static Jeu createMarelle() {
		Jeu resultat = new Jeu();
		
		resultat.position = new Position[24];
		
		for(int i=0; i<4;++i){
			for(int j = 0; j<3; ++j){
				resultat.position[i*3+j] =  new Position();
				int c = (3-i); if (c<1) c = 1;
				resultat.position[i*3+j].x = (i%3+c*j);
				resultat.position[i*3+j].y = i;
				
				resultat.position[23-(i*3+j)] = new Position();
				resultat.position[23-(i*3+j)].x = 6-resultat.position[i*3+j].x;
				resultat.position[23-(i*3+j)].y = 6-resultat.position[i*3+j].y;

			}
		}
		
		resultat.ligne = new Position[16][3];
		for(int i=0; i<24 ; ++i){
			resultat.ligne[i/3][i%3] = resultat.position[i];
		}
		
		for(int i=0; i<3 ; ++i){
			resultat.ligne[8][i] = resultat.position[1+3*i];
			resultat.ligne[9][i] = resultat.position[16+3*i];
			
			resultat.ligne[10+i][0] = resultat.position[i*3];
			resultat.ligne[10+i][1] = resultat.position[9+i];
			resultat.ligne[10+i][2] = resultat.position[21-i*3];
			
			resultat.ligne[13+i][0] = resultat.position[23-i*3];
			resultat.ligne[13+i][1] = resultat.position[23-(9+i)];
			resultat.ligne[13+i][2] = resultat.position[2+i*3];

		}
		
		resultat.coup = 1;
		resultat.statut = POSE;
		resultat.joueur = NOIR;
		
/*		
 * plateau morpion
 * 
 
 		resultat.position = new Position[9];

		for(int i=0; i<9;++i){
			resultat.position[i] =  new Position();
			resultat.position[i].x = (i%3)*2;
			resultat.position[i].y = (i/3)*2;
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

		resultat.coup = 1;
		resultat.statut = 0;
		resultat.joueur = 1;
*/

		return resultat;
	}

	public static boolean hasLink(Jeu jeu,int ndx1, int ndx2){
		for (int i = 0; i < jeu.ligne.length; ++i){
			for(int j = 1; j < jeu.ligne[i].length;++j){
				if ((jeu.ligne[i][j-1] == jeu.position[ndx1] &&
						jeu.ligne[i][j] == jeu.position[ndx2]) || 
						(jeu.ligne[i][j-1] == jeu.position[ndx2] &&
						jeu.ligne[i][j] == jeu.position[ndx1])){
					return true;
				} 
			}
		}
		return false;
	}
	
	public static int get(Jeu jeu, int ndx){
		return jeu.position[ndx].contenu;
	}
	
	public static boolean estDansMoulin(Jeu jeu, int ndx){
		int couleur = jeu.position[ndx].contenu;
		if (couleur == VIDE) return false;

		
		for (int i = 0; i < jeu.ligne.length; ++i){
			boolean contientNdx = false;
			boolean estMoulin = true;
			for(int j = 0; j < jeu.ligne[i].length;++j){
				if (jeu.ligne[i][j] == jeu.position[ndx]) contientNdx = true;
				if (jeu.ligne[i][j].contenu != couleur) estMoulin = false;
				}
			if (contientNdx && estMoulin) return true;
			}
		return false;				
	}


	private static boolean poseFinie(Jeu jeu) {
		return jeu.coup>= 2*9;
	}

	
	public static boolean canPlay(Jeu jeu, int ndx) {
		if (jeu.statut != POSE) return false;		
		return jeu.position[ndx].contenu==VIDE;
	}

	public static void play(Jeu jeu, int ndx) {
		jeu.position[ndx].contenu = jeu.joueur;

		testMoulin(jeu, ndx);
	}

	private static void testMoulin(Jeu jeu, int ndx) {
		if (estDansMoulin(jeu,ndx)){ // Est ce que l'on vient de compléter un moulin ?
			jeu.statut = RETRAIT; // On retire
			if (!canRemove(jeu)){ // Si ce n'est pas possible, 
				finCoup(jeu);     // alors fin du tour.
			}
		} else {
			finCoup(jeu); // fin du tour
		}
	}

	private static void finCoup(Jeu jeu) {
		jeu.joueur = (NOIR+BLANC) - jeu.joueur; // joueur suivant
		
		if (poseFinie(jeu)) { // est-ce que tous les pions sont posés ?
			jeu.statut = MOUVEMENT;
			if (compte(jeu,jeu.joueur)<3 || !canMove(jeu)){ // le joueur a perdu
				jeu.statut = GAGNE;
				jeu.joueur = (NOIR+BLANC) - jeu.joueur;
				return;
			}
		} else jeu.statut = POSE;
		
		++jeu.coup;
	}
	
	public static int compte(Jeu jeu, int couleur){
		int result = 0;
		for(int i = 0; i < jeu.position.length; ++i){
			if (jeu.position[i].contenu == couleur) ++ result;
		}
		return result;
	}

	public static boolean canMove(Jeu jeu){
		for(int i=0; i < jeu.position.length;++i )
			for(int j=0; j < jeu.position.length; ++j){
				if (canMove(jeu,i,j)) return true;
			}
		return false;
	}
	
	public static boolean canRemove(Jeu jeu){
		for(int i=0; i < jeu.position.length;++i )
			if (canRemove(jeu,i)) return true;
		return false;
	}

	public static boolean canMove(Jeu jeu, int fromNdx, int toNdx) {
		if (jeu.statut != MOUVEMENT) return false;		
		return jeu.position[fromNdx].contenu == jeu.joueur &&
			   jeu.position[toNdx].contenu == VIDE  && hasLink(jeu, fromNdx,toNdx);
	}

	public static void move(Jeu jeu, int fromNdx, int toNdx){
		jeu.position[toNdx].contenu = jeu.position[fromNdx].contenu;
		jeu.position[fromNdx].contenu = VIDE;

		testMoulin(jeu,toNdx);
	}


	public static boolean canRemove(Jeu jeu, int ndx) {
		if (jeu.statut != RETRAIT) return false;
		if (estDansMoulin(jeu,ndx)) return false;
		return jeu.position[ndx].contenu + jeu.joueur == NOIR+BLANC;
	}

	public static void remove(Jeu jeu, int ndx) {
		jeu.position[ndx].contenu = VIDE;
		
		finCoup(jeu);		
	}



	/***
	 * Fonction appelée quand un joueur clique sur une case.
	 * En fonction de l'état du jeu, il faut soit poser un pion, soit en retirer un.
	 * 
	 * @param jeu
	 * @param ndx
	 * @return true si le jeu a été mofifié et qu'il faut rafraîchir le dessin.
	 */
	public static boolean onClick(Jeu jeu, int ndx) {
		if (Regle.canPlay(jeu,ndx)){
			Regle.play(jeu,ndx);
			return true;
		} else if (Regle.canRemove(jeu,ndx)){
			Regle.remove(jeu,ndx);
			return true;
		}
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
		if (Regle.canMove(jeu,fromNdx,toNdx)){
			Regle.move(jeu,fromNdx,toNdx);
			return true;
		}	
		return false;
	}


	/***
	 * Génère le texte à afficher pour le statut
	 * @param jeu
	 * @return
	 */
	public static String getStatus(Jeu jeu){
		return "coup n°"+jeu.coup+ " : " + statut[jeu.statut*2+(jeu.joueur-1)];		
	}


	public static void main(String[] arg){
		Plateau.createMarelle();		
	}



}

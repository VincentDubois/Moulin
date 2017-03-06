import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;



public class Plateau extends JComponent implements MouseListener, MouseMotionListener{

	private static final int SIZE = 80;	
	private static final int DIAMETER = (SIZE*4)/5;	

	private Regle.Jeu jeu;
	private Dimension d;		
	private Regle.Position min,max;

	private static JLabel label;

	private int dragColor, dragStartNdx;
	private int dragX,dragY;
	private boolean debug;
	private static Plateau marelle;

	/***
	 * Création du composant graphique représentant le jeu.
	 * @param jeu
	 * @param debug 
	 * @throws Exception
	 */
	public Plateau(Regle.Jeu jeu, boolean debug) throws Exception{
		this.debug = debug;
		// Quelques vérifications
		if (jeu == null) throw new Exception("jeu should not be null");
		if (jeu.position == null) throw new Exception("field position should not be null");
		if (jeu.ligne == null) throw new Exception("field ligne should not be null");

		this.jeu  = jeu;

		// Calcul des dimensions du plateau
		min = new Regle.Position();
		max = new Regle.Position();
		min.x = jeu.position[0].x;
		min.y = jeu.position[0].y;
		max.x = min.x;
		max.y = min.y;

		for(int i = 1; i< jeu.position.length; ++i){
			if (jeu.position[i].x < min.x) min.x = jeu.position[i].x;
			else if (jeu.position[i].x > max.x) max.x = jeu.position[i].x;
			if (jeu.position[i].y < min.y) min.y = jeu.position[i].y;
			else if (jeu.position[i].y > max.y) max.y = jeu.position[i].y;
		}		

		d = new Dimension((max.x-min.x+3)*SIZE,(max.y-min.y+3)*SIZE);

		setSize(d);
		setPreferredSize(d);

		dragStartNdx = -1;

		addMouseListener(this);
		addMouseMotionListener(this);

	}


	/***
	 * Création de la fenêtre de jeu.
	 */
	public static void createMarelle(boolean debug){
		JFrame frame = new JFrame("Marelle");
		try {
			marelle = new Plateau(Regle.createJeu(),debug);
			
			if (debug){
				Debug.create();
				Debug.target(marelle.jeu);
			}

			frame.add(marelle,BorderLayout.CENTER);
			label = new JLabel("",JLabel.CENTER);
			frame.add(label,BorderLayout.SOUTH);
			Box hbox = Box.createHorizontalBox();
			JButton buttonStart = new JButton("Début");
			buttonStart.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {				
					marelle.setJeu(Regle.getStart(marelle.jeu));
				}				
			});
			JButton buttonPrevious = new JButton("Précédent");
			buttonPrevious.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					marelle.setJeu(Regle.getPrevious(marelle.jeu));
				}				
			});
			JButton buttonNext = new JButton("Suivant");
			buttonNext.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					marelle.setJeu(Regle.getNext(marelle.jeu));
				}				
			});
			JButton buttonLast = new JButton("Dernier");
			buttonLast.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					marelle.setJeu(Regle.getLast(marelle.jeu));
				}				
			});
			hbox.add(buttonStart);
			hbox.add(buttonPrevious);
			hbox.add(buttonNext);
			hbox.add(buttonLast);
			frame.add(hbox,BorderLayout.NORTH);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			marelle.showStatut();



		} catch (Exception e) {
			e.printStackTrace();
		}		
	}


	protected void setJeu(Regle.Jeu jeu) {
		if (jeu == null) return;
		this.jeu = jeu;
		repaint();
		showStatut();
		
	}


	// transformation entre les coordonnées de l'écran et celles du jeu
	private int getCoordX(int x){
		return (x-min.x+1)*SIZE+SIZE/2;
	}

	private int getPosX(int x){
		return x/SIZE+min.x-1;
	}

	private int getCoordY(int y){
		return (y-min.y+1)*SIZE+SIZE/2;
	}

	private int getPosY(int y){
		return y/SIZE+min.y-1;
	}


	private int getNdx(int x, int y){
		for(int i = 0; i< jeu.position.length; ++ i){
			if (jeu.position[i].x == x &&
					jeu.position[i].y == y) return i;
		}
		return -1;
	}

	/***
	 * Affichele plateau de jeu proprement dit
	 */
	public void paint(Graphics g2){
		Graphics2D g = (Graphics2D)g2;
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		g.setColor(Color.WHITE);

		g.fillRect(0,0,d.width,d.height);

		if (jeu == null) return; // Si le jeu n'est pas encore défini, on ne fait rien de plus.

		g.setColor(Color.BLACK);

		g.setStroke(new BasicStroke(5.0f,
				BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));


		// Boucle sur les lignes du jeu, pour tracer le plateau
		for(Regle.Position[] l : jeu.ligne){
			g.drawLine(getCoordX(l[0].x), getCoordY(l[0].y),
				getCoordX(l[l.length-1].x), getCoordY(l[l.length-1].y));
		}


		// Boucle sur les positions du jeu, pour dessiner les pions
		for(int i = 0; i<jeu.position.length; ++i){
			int type = Regle.get(jeu,i);
			if (type !=0){
				g.setColor(getTypeColor(type));
				int x = getCoordX(jeu.position[i].x);
				int y = getCoordY(jeu.position[i].y);
				g.fillOval(x-DIAMETER/2, y-DIAMETER/2, DIAMETER ,DIAMETER);
				g.setColor(Color.BLACK);
				g.drawOval(x-DIAMETER/2, y-DIAMETER/2, DIAMETER ,DIAMETER);
			}
		}

		// En cas de drag and drop, on la position actuelle et les mouvements possibles 
		if (dragStartNdx != -1){			
			// Boucle sur les destinations possibles
			for(int i = 0; i < jeu.position.length; ++i){
				if (Regle.canMove(jeu, dragStartNdx, i)){
					int x = getCoordX(jeu.position[i].x);
					int y = getCoordY(jeu.position[i].y);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(x-DIAMETER/4, y-DIAMETER/4, DIAMETER/2 ,DIAMETER/2);
				}
			}

			// Affiche le pion en cours de déplacement
			g.setColor(getTypeColor(dragColor));
			g.fillOval(dragX-DIAMETER/2, dragY-DIAMETER/2, DIAMETER ,DIAMETER);
			g.setColor(Color.BLACK);
			g.drawOval(dragX-DIAMETER/2, dragY-DIAMETER/2, DIAMETER ,DIAMETER);
		}
		
		if (jeu.statut == 2){
			for(int i = 0; i < jeu.position.length; ++i){
				if (Regle.estDansLigneComplete(jeu, i)){
					int x = getCoordX(jeu.position[i].x);
					int y = getCoordY(jeu.position[i].y);
					g.setColor(Color.LIGHT_GRAY);
					g.drawOval(x-DIAMETER/4, y-DIAMETER/4, DIAMETER/2 ,DIAMETER/2);
				}
			}
		}

	}


	private Color getTypeColor(int type) {
		return type == Regle.BLANC ? Color.WHITE : Color.BLACK;
	}


	/***
	 * Acualise la barre de statut
	 */
	private void showStatut() {		
		label.setText(Regle.getStatus(jeu));
		//Debug.target(marelle.jeu);
		if (debug) Debug.update();
	}


	/***
	 * Tente de jouer à un endroit donné.
	 * Cette fonction est appelée quand on clique sur une case
	 * @param ndx
	 */
	private void onClick(int ndx){
		if (Regle.onClick(jeu,ndx)){
			showStatut();
		}
			

	}

	/***
	 * Tente de déplacer un pion.
	 * Cette fonction est appelée quand on fait un glisser/déposer
	 * @param fromNdx
	 * @param toNdx
	 */
	private void onDragNDrop(int fromNdx, int toNdx) {
		if (fromNdx == -1 || toNdx == -1) return;
		if (Regle.onDragAndDrop(jeu, fromNdx,toNdx))
			showStatut();
	}

	////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// EVENEMENTS SOURIS /////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int x = getPosX(arg0.getX());
		int y = getPosY(arg0.getY());

		int ndx = getNdx(x,y);
		if (ndx==-1) return;

		onClick(ndx);		
		repaint();
	}



	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		dragStartNdx = -1;

		int x = getPosX(arg0.getX());
		int y = getPosY(arg0.getY());

		int ndx = getNdx(x,y);
		if (ndx != -1 && !Regle.canPlay(jeu,ndx)){
			dragColor = Regle.get(jeu, ndx);
			dragStartNdx = ndx;
			dragX = arg0.getX();
			dragY = arg0.getY();
			repaint();
		}		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (dragStartNdx ==-1) return;
		int x = getPosX(arg0.getX());
		int y = getPosY(arg0.getY());

		int ndx = getNdx(x,y);
		onDragNDrop(dragStartNdx,ndx);

		dragStartNdx = -1;
		repaint();
	}



	@Override
	public void mouseDragged(MouseEvent e) {		
		if (dragStartNdx != -1){
			dragX = e.getX();
			dragY = e.getY();
			repaint();
		}	
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

}

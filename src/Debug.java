import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ConcurrentModificationException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;


import canvas.Canvas;




public class Debug {

	private static Explorer exp;
	private static ListListener nameList;
	public static Object jeu;

	public static void create() {
		JFrame mainFrame = new JFrame("Debug");		
		Container contentPane = mainFrame.getContentPane();

		//		contentPane.setLayout(new FlowLayout());
		//		Box vbox = Box.createVerticalBox();

		final Canvas canvas = new Canvas(1024, 768);
		//canvas.setScale(0.4);
		exp = new Explorer(canvas);

		JScrollPane scrollPane = new JScrollPane(   canvas);
		scrollPane.setSize(1024, 800);

		Box hbox = Box.createHorizontalBox();
		JButton button = new JButton("Zoom +");
		button.addActionListener(new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				canvas.multScale(1.2);				
			}
		}
				);
		hbox.add(button);
		button = new JButton("Normal");
		button.addActionListener(new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				canvas.setScale(1);				
			}
		}
				);
		hbox.add(button);

		button = new JButton("Zoom -");
		button.addActionListener(new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				canvas.multScale(0.8);				
			}
		}
				);
		hbox.add(button);

		hbox.add(Box.createGlue());

		hbox.add(new JLabel("Sélection "));


		nameList = new ListListener(exp);
		//		canvas.set
		canvas.addSelectionListener(nameList);
		JScrollPane scrollPaneList = new JScrollPane();
		scrollPaneList.setViewportView(nameList);
		hbox.add(scrollPaneList);

		//vbox.add(hbox);
		contentPane.add(hbox,BorderLayout.PAGE_START);

		/*		scrollPane.setBackground(Color.lightGray);
		canvas.setBackground(Color.lightGray);
		 */

		scrollPane.setBorder(BorderFactory.createTitledBorder("Mémoire"));
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		//		vbox.add(scrollPane);
		contentPane.add(scrollPane,BorderLayout.CENTER);

		//		mainFrame.add(vbox);



		//mainFrame.setMinimumSize(new Dimension(800,800));
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}	


	public static void target(Object jeu) {
		try {
			Debug.jeu = jeu;
			exp.read(jeu,"jeu");
			exp.refresh();
			nameList.update();
		} catch (ConcurrentModificationException c){

		}
	}

	public static void update() {
		try {
			exp.read(jeu,"jeu");
			exp.refresh();
			nameList.update();
		} catch (ConcurrentModificationException c){

		}
	}

}




import javax.swing.DefaultListModel;
import javax.swing.JList;

import canvas.Enregistrement;
import canvas.Grob;
import canvas.Listener;

import java.awt.Dimension;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ListListener extends JList implements Listener {

	Explorer e;
	DefaultListModel data;
	private Grob grob;
	
	
	
	public ListListener(Explorer exp, DefaultListModel model) {
		super(model);
		this.data = (DefaultListModel) this.getModel();
		e = exp;
		grob =  null;
		this.setFixedCellWidth(-1);
//		this.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.setMinimumSize(new Dimension(100,40));
		this.setPreferredSize(new Dimension(200,40));
		this.setMaximumSize(new Dimension(200,40));
	}
	
	public ListListener(Explorer exp) {
		this(exp,new DefaultListModel());
	}

	public void hasMoved(Grob grob) {
		this.grob = grob;
		update();
	}

	void update() {
		data.clear();
		for( Map<Enregistrement, Set<String> > it : e.name.values()){
			Set<String> set = it.get(this.grob);
			if (set != null){
				for(String s :set)
				data.addElement(s);
			}
		}
		if (data.isEmpty()){
			data.addElement(null);
		}
	}
}

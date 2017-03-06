

import java.awt.Color;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import canvas.Arrow;
import canvas.ArrowValue;
import canvas.Canvas;
import canvas.Enregistrement;
import canvas.Grob;
import canvas.MovedArrowAction;
import canvas.TextBox;



public class Explorer {
	
	public static class Pair<K,V>{
		public final K key;
		public final V value;
		
		public Pair(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}		
	}
	
	
	public String getName(Grob newDest) {
		String des ="null";
		if (newDest != null){
			des = "( ";
			boolean first = true;
			for( Map<Enregistrement, Set<String> > it : name.values()){
				Set<String> set =it.get(newDest);
				if (set != null){
					for(String s : set){
						if (!first){
							des = des+" , ";
						}
						first  = false;
						des = des +s;
					}
				}
			}
			des = des+" )";
			if (first) {
				des = "null";
			}
		}
		return des;
	}

	public String getSourceName(Arrow arrow){
		Enregistrement.Field field =((ArrowValue.Center)  arrow.from).getField(); 
		String result = getName(field.getEnregistrement());
		if (result.equals("null") ){
			result = "";
		}
		assembleName(result,((TextBox) field.label).text);
		return result;
	}

	private final class MovedAction implements MovedArrowAction {

		public void moved(Arrow arrow, Grob newDest) {


			System.out.println(getSourceName(arrow)+" <- "+
					getName(newDest)+" [ anciennement " +getName(arrow.to)+" ] ");
		}

	}

	Map<Object,Enregistrement> map; 
	Map<String,Variable> variable;
	Map<String,Map<Enregistrement,Set<String> > > name;
	Canvas canvas;
	public int y;

	public Explorer(Canvas canvas){
		this.canvas = canvas;
		map = new WeakHashMap<Object, Enregistrement>();
		variable = new HashMap<String,Variable>();
		name = new HashMap<String,Map<Enregistrement,Set<String> > >();
		y = 20;
	}
	
	public static class Variable {
		String name;
		Object value;
		public Variable(String name, Object value) {
			super();
			this.name = name;
			this.value = value;
		}
	}
	
	public static class Link {
		Object from;
		String name;
		Object to;
		Class type;
		
		public Link(Object from, String name, Object to, Class type) {
			super();
			this.from = from;
			this.name = name;
			this.to = to;
			this.type = type;
		}
				
		public String toString(){
			return from +" ( "+ name +" ) "+ " -> " + to + " ( "+type + " ) ";
		}
		
		public boolean isLink(){
			if (type == null) return false;
			if (String.class.isAssignableFrom(type)) return false;
			if (type.isPrimitive()) return false;
			if (type.getPackage() == null) return true;
			if (type.getPackage().getName().startsWith("java")) return false;
			return true;
		}
		
	}

	public static Vector<Link> getLink(Object o){
		//System.out.println("getLink " + o);
		Vector<Link> link = new Vector();		
		if (o.getClass() == Variable.class){
			//TODO : handle null var
			link.add(new Link(o,((Variable)o).name,
					((Variable) o).value,((Variable) o).value.getClass()));
		} else if (o.getClass().isArray()){
			int length = Array.getLength(o);

			for(int i = 0; i < length; ++i){
					Object to = Array.get(o,i);
					link.add(new Link(o,"["+i+"]",to,o.getClass().getComponentType()));						
			}
		} else {  
			Field[] tab = o.getClass().getDeclaredFields();

			for(Field f : tab){
				{
					try {
							Object to =f.get(o);
							link.add(new Link(o,f.getName(),to,f.getType()));
					} catch (IllegalArgumentException e1) {
						//e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						//e1.printStackTrace();
					}
				}
			}
		}
		
/*		for(  Link entry : link){
			System.out.println(link);
		}*/
		
		
		return link;
	}

	public void refresh(){

		// On retire les objets inaccessibles
		System.gc();
		canvas.element.retainAll(map.values());

		//Parcours des objets restants
		for ( Map.Entry<Object,Enregistrement> e :map.entrySet()){
			Object o = e.getKey();
			Enregistrement r = e.getValue();

			r.clear();
			int x = r.getBoundingBox().x+200;
			int y = r.getBoundingBox().y;

			for(  Link entry : getLink(o)){

				Object link = entry.to;

				if (entry.isLink()){
					if (map.containsKey(link)){
						x = map.get(link).getBoundingBox().x;
						y = map.get(link).getBoundingBox().y+60;
					} else {
						createInitialView(link,x,y);
						y = y + 30;
					}
				}
			}

			update();
		}
	}


	public void update(){
		for ( Map.Entry<Object,Enregistrement> e : map.entrySet()){
			Object o = e.getKey(); 
			Enregistrement r = e.getValue();

			if (r.field.isEmpty() ){
				for(  Link entry : getLink(o)){

					Object value = entry.to;
					if (entry.type ==  String.class || !entry.isLink()){
						r.addStringRecord(entry.name, value == null ? null : value.toString() );
					} else {					
						r.addFieldRecord(entry.name,map.get(value));
					}
				}			
				r.moved();
			}
		}
	}

	public void createInitialView(Object o, int x, int y){
		if (o == null || (o.getClass() == String.class) ) return;
		if (!map.keySet().contains(o)){
			Enregistrement e = new Enregistrement(x,y);
		
			e.arrowMovedAction = new MovedAction();


			map.put(o, e);
			canvas.add(e);
			for(  Link entry : getLink(o)){
				if (entry.isLink()){
					createInitialView(entry.to,x+200,y);
					y = y+100;
				}
			}			
		}
	}

	public void read(Object o,String label){

		if (o!= null) {
			createInitialView(o,150,y);
		}

		Enregistrement r = null;
		if (o != null){
			r = map.get(o);
		}
		
		Variable v = variable.get(label);
		
		if (v == null){
			v = new Variable(label,o);
			variable.put(label, v);
		}		

		Enregistrement e = map.get(v);
		if (e == null){
			e = new Enregistrement(20,y);
			e.arrowMovedAction = new MovedAction();
			e.addFieldRecord(label, r);
			canvas.add(e);
			y = y +e.getBoundingBox().height+20;
			map.put(v,e);
		} else {
			e.clear();
			e.addFieldRecord(label, r);
		}
		e.field.firstElement().label.setBackground(new Color(0xFAFFE0));

		if (o != null) makeNames(label,o);
		
		canvas.repaint();


	}
	
	private static boolean subsume(Set<String> s, String n){
		if (s == null) return false;
		for(String e : s){
			if (e.startsWith(n)) return true;
		}
		return false;
	}
	
	private static void addName(Map<Enregistrement, Set<String> > nameMap, Enregistrement e, String name){
		Set<String> set = nameMap.get(e);
		if (set == null){
			set = new HashSet<String>();
			nameMap.put(e, set);
		}
		set.add(name);
	}

	private void makeNames(String label,Object o) {
		Map<Enregistrement, Set<String> > nameMap =name.get(o);
		if (nameMap == null){
			nameMap = new HashMap<Enregistrement,Set<String> >();
			name.put(label, nameMap);
		} else {
			nameMap.clear();
		}
		Set< Pair <Object, String> > toDo = new HashSet<>();
		toDo.add(new Pair<Object, String>(o,label));
		Set< Pair <Object, String> > next = new HashSet<>();

		while (!toDo.isEmpty()){
			for( Pair<Object,String> e : toDo){
				addName(nameMap,map.get(e.key), e.value);

				for(  Link entry : getLink(e.key)){

					Object newObject;
					newObject = entry.to;
					if (entry.isLink() &&   newObject !=null  
							&& map.containsKey(newObject)){
						Enregistrement enregistrement = map.get(newObject);
						String newName = assembleName(e.value,entry.name);
						
						if ( !subsume(nameMap.get(enregistrement),newName)){
								next.add(new Pair<Object,String>(newObject,newName));
						}
					}

				}
			}
			toDo = next;
			next = new HashSet<>();
		}
	}

	private static String assembleName(String name, String field) {
		if (field.startsWith("[")) return name+field;
		return name+"."+field;
	}
}

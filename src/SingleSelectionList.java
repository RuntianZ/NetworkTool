import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class SingleSelectionList<E> extends JList<E>{
	public SingleSelectionList() {
		super();
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public SingleSelectionList(Vector<E> v) {
		super(v);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public SingleSelectionList(E[] v) {
		super(v);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
}

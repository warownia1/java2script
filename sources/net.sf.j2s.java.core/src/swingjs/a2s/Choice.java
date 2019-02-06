package swingjs.a2s;

import java.awt.Font;

import javax.swing.JComboBox;

public class Choice extends JComboBox {

	public void select(int index) {
		setSelectedIndex(index);
	}

	public void select(String key) {
		setSelectedItem(key);
	}

	public void add(String label) {
		addItem(label);
	}

	public String getItem(int n) {
		return (String)getItemAt(n);
	}
	
	@Override
	public void removeAll() {
		removeAllItems();
	}
	
    @Override
    public Font getFont() {
    	return getFontAWT();
    }


}

package swingjs.a2s;

import java.awt.Font;

import javax.swing.JCheckBoxMenuItem;

public class CheckboxMenuItem extends JCheckBoxMenuItem {

	public CheckboxMenuItem(String string) {
		super(string);
	}

	public CheckboxMenuItem() {
	}

	public CheckboxMenuItem(String string, boolean b) {
		super(string, b);
	}

	@Override
	public boolean getState() {
		return isSelected();
	}
	
	
	@Override
	public void setState(boolean tf) {
		setSelected(tf);
	}

    @Override
    public Font getFont() {
    	return getFontAWT();
    }


}

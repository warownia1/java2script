package swingjs.plaf;

import java.awt.Dimension;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;

import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import javax.swing.text.WrappedPlainView;

import swingjs.JSToolkit;
import swingjs.api.js.DOMNode;

/**
 * Note that java.awt.TextArea is a JScrollPane, NOT a JTextArea.
 * 
 * @author Bob Hanson
 *
 */
public class JSTextAreaUI extends JSTextViewUI {

	@Override
	public DOMNode updateDOMNode() {
		
//		/**
//		 * @j2sNative
//		 * 
//		 * System.out.println("updateDOM textarea xxt");xxt = this;
//		 */
		
		if (domNode == null) {
			valueNode = domNode = newDOMObject("textarea", id, "spellcheck", FALSE,  "autocomplete", "off");
			setupViewNode();
		}
		if (((JTextArea) jc).getLineWrap())
			domNode.removeAttribute("wrap");
		else
			DOMNode.setStyles(domNode, "white-space", "pre");
		textListener.checkDocument();
		setCssFont(
				DOMNode.setAttr(domNode, "value", setCurrentText()),
				c.getFont());
		updateJSCursor("rewrite");
		return super.updateDOMNode();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();
//		Object newValue = e.getNewValue();
//        Object oldValue = e.getOldValue();
		switch(prop) {
		case "ancestor":
			setJ2sMouseHandler();
			break;
		} 
		super.propertyChange(e);
	}

	protected void updateRootView() {
		useRootView = true;
        rootView.setView(create(editor.getDocument().getDefaultRootElement()));// does not take into account nested documents like HTML (I guess)		
	}
	/**
	 * Get the real height and width of the text in a JavaScript textarea
	 * Used by JSScrollPaneUI
	 * 
	 * @return
	 */
	void getTextAreaTextSize(Dimension d) {
		int sh = 0;
		int sw = 0;
		/**
		 * @j2sNative 
		 * 
		 * var h = this.domNode.style.height; 
		 * this.domNode.style.height = null;
		 * sh = this.domNode.scrollHeight; 
		 * this.domNode.style.height = h;
		 * 		 
		 * var w = this.domNode.style.width; 
		 * this.domNode.style.width = null;
		 * sw = this.domNode.scrollWidth; 
		 * this.domNode.style.width = w;

		 * 
		 */
			{
			}
			
			d.width = sw;
			d.height = sh;
	}

	private Insets myInsets = new Insets(0, 0, 0, 0); 
	@Override
	public Insets getInsets() {
		return myInsets;
	}
	
//	@Override
//	protected Dimension getCSSAdjustment(boolean addingCSS) {
//		return (
//			//	addingCSS ? new Dimension(-5, -12) : 
//			new Dimension(0, 0)); 
//		// total hack -12 is to see full vertical scrollbar (Boltzmann)
//	}

	@Override
	protected String getPropertyPrefix() {
		return "TextArea";
	}

	@Override
	protected DOMNode setHTMLElement() {
		// handled by JScrollPane
		return DOMNode.setStyles(setHTMLElementCUI(), 
				"overflow", "hidden",
				"position", "absolute");
	}

    /**
	 * Creates the view for an element. Returns a WrappedPlainView or PlainView.
	 *
	 * @param elem the element
	 * @return the view
	 */
	@Override
	public View create(Element elem) {
		JTextArea area = (JTextArea) c;
		View v;
		if (area.getLineWrap()) {
			v = new WrappedPlainView(elem, area.getWrapStyleWord());
		} else {
			v = new PlainView(elem);
		}
		return v;
	}

}

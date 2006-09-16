/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.widgets;

 
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.RunnableCompatibility;
import org.eclipse.swt.internal.browser.OS;
import org.eclipse.swt.internal.xhtml.CSSStyle;
import org.eclipse.swt.internal.xhtml.Element;
import org.eclipse.swt.internal.xhtml.document;

/**
 * Instances of this class represent a selectable user interface object
 * that issues notification when pressed and released. 
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>CHECK, CASCADE, PUSH, RADIO, SEPARATOR</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Arm, Help, Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles CHECK, CASCADE, PUSH, RADIO and SEPARATOR
 * may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */

public class MenuItem extends Item {
	Menu parent, menu;
	int id, accelerator;

	Element statusEl, imageEl, textEl, arrowEl;
	
/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>Menu</code>) and a style value
 * describing its behavior and appearance. The item is added
 * to the end of the items maintained by its parent.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together 
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a menu control which will be the parent of the new instance (cannot be null)
 * @param style the style of control to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#CHECK
 * @see SWT#CASCADE
 * @see SWT#PUSH
 * @see SWT#RADIO
 * @see SWT#SEPARATOR
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public MenuItem (Menu parent, int style) {
	super (parent, checkStyle (style));
	this.parent = parent;
	parent.createItem (this, parent.getItemCount ());
	configMenuItem();
}

/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>Menu</code>), a style value
 * describing its behavior and appearance, and the index
 * at which to place it in the items maintained by its parent.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together 
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a menu control which will be the parent of the new instance (cannot be null)
 * @param style the style of control to construct
 * @param index the index to store the receiver in its parent
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#CHECK
 * @see SWT#CASCADE
 * @see SWT#PUSH
 * @see SWT#RADIO
 * @see SWT#SEPARATOR
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public MenuItem (Menu parent, int style, int index) {
	super (parent, checkStyle (style));
	this.parent = parent;
	parent.createItem (this, index);
	configMenuItem();
}

MenuItem (Menu parent, Menu menu, int style, int index) {
	super (parent, checkStyle (style));
	this.parent = parent;
	this.menu = menu;	
	if (menu != null) menu.cascade = this;
	display.addMenuItem (this);
	configMenuItem();
}

void configMenuItem() {
	if ((style & SWT.SEPARATOR) != 0) return;
	handle.onmouseover = new RunnableCompatibility() {
		public void run() {
			String className = handle.className;
			if (className == null) {
				handle.className = "menu-item-hover";
			} else {
				String[] clazz = className.split ("\\s");
				boolean existed = false;
				boolean disabled = false;
				boolean existedDisabled = false;
				for (int i = 0; i < clazz.length; i++) {
					if (clazz[i] == "menu-item-hover") {
						existed = true;
					} else if (clazz[i] == "menu-item-disabled") {
						disabled = true;
					} else if (clazz[i] == "menu-item-disabled-hover") {
						existedDisabled = true;
					}
				}
				if (!existed) {
					clazz[clazz.length] = "menu-item-hover";
				}
				if (disabled && !existedDisabled) {
					clazz[clazz.length] = "menu-item-disabled-hover";
				}
				if (!existed || (disabled && !existedDisabled)) {
					String s = "";
					/**
					 * @j2sNative
					 * s = clazz.join (" ");
					 */ {}
					handle.className = s;
				}
				if (!disabled) {
					Element[] divs = handle.getElementsByTagName ("DIV");
					boolean existedArrow = false;
					for (int k = 0; k < divs.length; k++) {
						Element div = divs[k];
						if (div.className == "menu-item-arrow-right") {
							existedArrow = true;
							break;
						}
					}
					if (existedArrow) {
						// popup submenu with about 0.25s delay!
					}
				}
			}
			Menu p = parent;
			int indexOf = p.indexOf(MenuItem.this);
			if (p.currentIndex != -1 && p.currentIndex != indexOf) {
				Element el = p.items[p.currentIndex].handle;
				/**
				 * @j2sNative
				 * el.onmouseout();
				 */ {}
			}
			p.currentIndex = indexOf;
		}
	};
	handle.onmouseout = new RunnableCompatibility() {
		public void run() {
			String className = handle.className;
			if (className == null) {
				return;
			} else {
				String[] clazz = className.split ("\\s");
				boolean existed = false;
				for (int i = 0; i < clazz.length; i++) {
					if (clazz[i] == "menu-item-hover" 
							|| clazz[i] == "menu-item-disabled-hover") {
						existed = true;
						int k = 0;
						for (int j = i; j < clazz.length - 1; j++) {
							if (clazz[j] != "menu-item-hover" 
									&& clazz[j] != "menu-item-disabled-hover") {
								clazz[i + k] = clazz[j + 1];
								k++;
							}
						}
						/**
						 * @j2sNative
						 * clazz.length -= clazz.length - (i + k);
						 */ {}
					}
				}
				if (existed) {
					String s = "";
					/**
					 * @j2sNative
					 * s = clazz.join (" ");
					 */ {}
					handle.className = s;
				}
			}
			parent.currentIndex = -1;
		}
	};
	checkHookType(SWT.Selection);
	/**
	 * @j2sNative
	this.handle.onselectstart = function () {
		return false;
	};
	this.handle.oncontextmenu = function () {
		return false;
	};
	 */ {}
}

/* (non-Javadoc)
 * @see org.eclipse.swt.widgets.Widget#hookSelection()
 */
void hookSelection() {
	handle.onclick = new RunnableCompatibility() {
		public void run() {
			Element btn = parent.btnFocus;
			if (!isEnabled()) {
				display.timerExec(40, new Runnable() {
					public void run() {
						parent.btnFocus.focus();
					}
				});
				return;
			}
			
			if ((style & SWT.CHECK) != 0) {
				setSelection (!getSelection ());
			} else {
				if ((style & SWT.RADIO) != 0) {
					if ((parent.getStyle () & SWT.NO_RADIO_GROUP) != 0) {
						setSelection (!getSelection ());
					} else {
						selectRadio ();
					}
				}
			}
			Event event = new Event ();
			//setInputState (event, SWT.Selection);
			postEvent (SWT.Selection, event);
			parent.lastFocusdTime = -1;
			/**
			 * @j2sNative btn.onblur();
			 */ {}
		}
	};
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the arm events are generated for the control, by sending
 * it one of the messages defined in the <code>ArmListener</code>
 * interface.
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ArmListener
 * @see #removeArmListener
 */
public void addArmListener (ArmListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Arm, typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the help events are generated for the control, by sending
 * it one of the messages defined in the <code>HelpListener</code>
 * interface.
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see HelpListener
 * @see #removeHelpListener
 */
public void addHelpListener (HelpListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Help, typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the menu item is selected, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * When <code>widgetSelected</code> is called, the stateMask field of the event object is valid.
 * <code>widgetDefaultSelected</code> is not called.
 * </p>
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #removeSelectionListener
 * @see SelectionEvent
 */
public void addSelectionListener (SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener(listener);
	addListener (SWT.Selection,typedListener);
	addListener (SWT.DefaultSelection,typedListener);
}

protected void checkSubclass () {
	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}

static int checkStyle (int style) {
	return checkBits (style, SWT.PUSH, SWT.CHECK, SWT.RADIO, SWT.SEPARATOR, SWT.CASCADE, 0);
}

/*
void fillAccel (ACCEL accel) {
	accel.fVirt = 0;
	accel.cmd = accel.key = 0;
	if (accelerator == 0 || !getEnabled ()) return;
	int fVirt = OS.FVIRTKEY;
	int key = accelerator & SWT.KEY_MASK;
	int vKey = Display.untranslateKey (key);
	if (vKey != 0) {
		key = vKey;	
	} else {
		switch (key) {
			/*
			* Bug in Windows.  For some reason, VkKeyScan
			* fails to map ESC to VK_ESCAPE and DEL to
			* VK_DELETE.  The fix is to map these keys
			* as a special case.
			*-/
			case 27: key = OS.VK_ESCAPE; break;
			case 127: key = OS.VK_DELETE; break;
			default: {
				key = Display.wcsToMbcs ((char) key);
				if (key == 0) return;
				if (OS.IsWinCE) {
					key = OS.CharUpper ((short) key);
				} else {
					vKey = OS.VkKeyScan ((short) key) & 0xFF;
					if (vKey == -1) {
						fVirt = 0;
					} else {
						key = vKey;
					}
				}
			}
		}
	}
	accel.key = (short) key;
	accel.cmd = (short) id;
	accel.fVirt = (byte) fVirt;
	if ((accelerator & SWT.ALT) != 0) accel.fVirt |= OS.FALT;
	if ((accelerator & SWT.SHIFT) != 0) accel.fVirt |= OS.FSHIFT;
	if ((accelerator & SWT.CONTROL) != 0) accel.fVirt |= OS.FCONTROL;
}
*/

void fixMenus (Decorations newParent) {
	if (menu != null) menu.fixMenus (newParent);
}

/**
 * Returns the widget accelerator.  An accelerator is the bit-wise
 * OR of zero or more modifier masks and a key. Examples:
 * <code>SWT.CONTROL | SWT.SHIFT | 'T', SWT.ALT | SWT.F2</code>.
 * The default value is zero, indicating that the menu item does
 * not have an accelerator.
 *
 * @return the accelerator or 0
 *
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getAccelerator () {
	checkWidget ();
	return accelerator;
}

/**
 * Returns a rectangle describing the receiver's size and location
 * relative to its parent (or its display if its parent is null).
 *
 * @return the receiver's bounding rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.1
 */
/*public*/ Rectangle getBounds () {
	checkWidget ();
	//if (OS.IsWinCE) return new Rectangle (0, 0, 0, 0);
	int index = parent.indexOf (this);
	if (index == -1) return new Rectangle (0, 0, 0, 0);
	if ((parent.style & SWT.BAR) != 0) {
		Decorations shell = parent.parent;
		if (shell.menuBar != parent) {
			return new Rectangle (0, 0, 0, 0);
		}
		/*
		int hwndShell = shell.handle;
		MENUBARINFO info1 = new MENUBARINFO ();
		info1.cbSize = MENUBARINFO.sizeof;
		if (!OS.GetMenuBarInfo (hwndShell, OS.OBJID_MENU, 1, info1)) {
			return new Rectangle (0, 0, 0, 0);
		}
		MENUBARINFO info2 = new MENUBARINFO ();
		info2.cbSize = MENUBARINFO.sizeof;
		if (!OS.GetMenuBarInfo (hwndShell, OS.OBJID_MENU, index + 1, info2)) {
			return new Rectangle (0, 0, 0, 0);
		}
		int x = info2.left - info1.left;
		int y = info2.top - info1.top;
		int width = info2.right - info2.left;
		int height = info2.bottom - info2.top;
		return new Rectangle (x, y, width, height);
		*/
		return new Rectangle (0, 0, 0, 0);
	} else {
		/*
		int hMenu = parent.handle;
		RECT rect1 = new RECT ();
		if (!OS.GetMenuItemRect (0, hMenu, 0, rect1)) {
			return new Rectangle (0, 0, 0, 0);
		}
		RECT rect2 = new RECT ();
		if (!OS.GetMenuItemRect (0, hMenu, index, rect2)) {
			return new Rectangle (0, 0, 0, 0);
		}
		int x = rect2.left - rect1.left + 2;
		int y = rect2.top - rect1.top + 2;
		int width = rect2.right - rect2.left;
		int height = rect2.bottom - rect2.top;
		return new Rectangle (x, y, width, height);
		*/
		Point pt2 = OS.calcuateRelativePosition(handle, parent.handle);
		int width = OS.getContainerWidth(handle);
		int height = OS.getContainerHeight(handle);
		return new Rectangle (pt2.x + 2, pt2.y + 2, width, height);
	}
}

/**
 * Returns <code>true</code> if the receiver is enabled, and
 * <code>false</code> otherwise. A disabled menu item is typically
 * not selectable from the user interface and draws with an
 * inactive or "grayed" look.
 *
 * @return the receiver's enabled state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @see #isEnabled
 */
public boolean getEnabled () {
	checkWidget ();
	/*
	if ((OS.IsPPC || OS.IsSP) && parent.hwndCB != 0) {
		int hwndCB = parent.hwndCB;
		TBBUTTONINFO info = new TBBUTTONINFO ();
		info.cbSize = TBBUTTONINFO.sizeof;
		info.dwMask = OS.TBIF_STATE;
		OS.SendMessage (hwndCB, OS.TB_GETBUTTONINFO, id, info);
		return (info.fsState & OS.TBSTATE_ENABLED) != 0;
	}
	int hMenu = parent.handle;
	MENUITEMINFO info = new MENUITEMINFO ();
	info.cbSize = MENUITEMINFO.sizeof;
	info.fMask = OS.MIIM_STATE;
	boolean success;
	if (OS.IsWinCE) {
		int index = parent.indexOf (this);
		if (index == -1) error (SWT.ERROR_CANNOT_GET_ENABLED);
		success = OS.GetMenuItemInfo (hMenu, index, true, info);
	} else {
		success = OS.GetMenuItemInfo (hMenu, id, false, info);
	}
	if (!success) error (SWT.ERROR_CANNOT_GET_ENABLED);
	return (info.fState & (OS.MFS_DISABLED | OS.MFS_GRAYED)) == 0;
	*/
	return handle.className == null || handle.className.indexOf("menu-item-disabled") == -1;
}

/**
 * Returns the receiver's cascade menu if it has one or null
 * if it does not. Only <code>CASCADE</code> menu items can have
 * a pull down menu. The sequence of key strokes, button presses 
 * and/or button releases that are used to request a pull down
 * menu is platform specific.
 *
 * @return the receiver's menu
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Menu getMenu () {
	checkWidget ();
	return menu;
}

String getNameText () {
	if ((style & SWT.SEPARATOR) != 0) return "|";
	return super.getNameText ();
}

/**
 * Returns the receiver's parent, which must be a <code>Menu</code>.
 *
 * @return the receiver's parent
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Menu getParent () {
	checkWidget ();
	return parent;
}

/**
 * Returns <code>true</code> if the receiver is selected,
 * and false otherwise.
 * <p>
 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
 * it is selected when it is checked.
 *
 * @return the selection state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getSelection () {
	checkWidget ();
	if ((style & (SWT.CHECK | SWT.RADIO)) == 0) return false;
	/*
	if ((OS.IsPPC || OS.IsSP) && parent.hwndCB != 0) return false;
	int hMenu = parent.handle;
	MENUITEMINFO info = new MENUITEMINFO ();
	info.cbSize = MENUITEMINFO.sizeof;
	info.fMask = OS.MIIM_STATE;
	boolean success = OS.GetMenuItemInfo (hMenu, id, false, info);
	if (!success) error (SWT.ERROR_CANNOT_GET_SELECTION);
	return (info.fState & OS.MFS_CHECKED) !=0;
	*/
	if (statusEl.className != null) {
		if ((style & SWT.CHECK) != 0 && statusEl.className.indexOf("menu-item-checked") != -1) {
			return true;
		} else if ((style & SWT.RADIO) != 0 && statusEl.className.indexOf("menu-item-selected") != -1) {
			return true;
		}  
	}
	return false;
}

/**
 * Returns <code>true</code> if the receiver is enabled and all
 * of the receiver's ancestors are enabled, and <code>false</code>
 * otherwise. A disabled menu item is typically not selectable from the
 * user interface and draws with an inactive or "grayed" look.
 *
 * @return the receiver's enabled state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @see #getEnabled
 */
public boolean isEnabled () {
	return getEnabled () && parent.isEnabled ();
}

protected void releaseChild () {
	super.releaseChild ();
	if (menu != null) menu.dispose ();
	menu = null;
	parent.destroyItem (this);
}

/* (non-Javadoc)
 * @see org.eclipse.swt.widgets.Widget#releaseHandle()
 */
protected void releaseHandle() {
	if (arrowEl != null) {
		OS.destroyHandle(arrowEl);
		arrowEl = null;
	}
	if (textEl != null) {
		OS.destroyHandle(textEl);
		textEl = null;
	}
	if (imageEl != null) {
		OS.destroyHandle(imageEl);
		imageEl = null;
	}
	if (statusEl != null) {
		OS.destroyHandle(statusEl);
		statusEl = null;
	}
	if (handle != null) {
		OS.destroyHandle(handle);
		handle = null;
	}
	super.releaseHandle();
}
void releaseMenu () {
//	if (!OS.IsSP) setMenu (null);
	menu = null;
}

protected void releaseWidget () {
	if (menu != null) menu.releaseResources ();
	menu = null;
	super.releaseWidget ();
	if (accelerator != 0) {
		parent.destroyAccelerators ();
	}
	accelerator = 0;
	display.removeMenuItem (this);
	parent = null;
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the arm events are generated for the control.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ArmListener
 * @see #addArmListener
 */
public void removeArmListener (ArmListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Arm, listener);
}
/**
 * Removes the listener from the collection of listeners who will
 * be notified when the help events are generated for the control.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see HelpListener
 * @see #addHelpListener
 */
public void removeHelpListener (HelpListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Help, listener);
}
/**
 * Removes the listener from the collection of listeners who will
 * be notified when the control is selected.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #addSelectionListener
 */
public void removeSelectionListener (SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Selection, listener);
	eventTable.unhook (SWT.DefaultSelection,listener);	
}

void selectRadio () {
	int index = 0;
	MenuItem [] items = parent.getItems ();
	while (index < items.length && items [index] != this) index++;
	int i = index - 1;
	while (i >= 0 && items [i].setRadioSelection (false)) --i;
	int j = index + 1;
	while (j < items.length && items [j].setRadioSelection (false)) j++;
	setSelection (true);
}

/**
 * Sets the widget accelerator.  An accelerator is the bit-wise
 * OR of zero or more modifier masks and a key. Examples:
 * <code>SWT.MOD1 | SWT.MOD2 | 'T', SWT.MOD3 | SWT.F2</code>.
 * <code>SWT.CONTROL | SWT.SHIFT | 'T', SWT.ALT | SWT.F2</code>.
 * The default value is zero, indicating that the menu item does
 * not have an accelerator.
 *
 * @param accelerator an integer that is the bit-wise OR of masks and a key
 *
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setAccelerator (int accelerator) {
	checkWidget ();
	if (this.accelerator == accelerator) return;
	this.accelerator = accelerator;
	parent.destroyAccelerators ();
}

/**
 * Enables the receiver if the argument is <code>true</code>,
 * and disables it otherwise. A disabled menu item is typically
 * not selectable from the user interface and draws with an
 * inactive or "grayed" look.
 *
 * @param enabled the new enabled state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setEnabled (boolean enabled) {
	checkWidget ();
	String cssName = handle.className;
	if (cssName == null) cssName = "";
	String key = "menu-item-disabled";
	int idx = cssName.indexOf(key);
	if (!enabled) {
		if (idx == -1) {
			handle.className += " " + key; 
		}
	} else {
		if (idx != -1) {
			handle.className = cssName.substring(0, idx) + cssName.substring(idx + key.length()); 
		}
	}
	/*
	if ((OS.IsPPC || OS.IsSP) && parent.hwndCB != 0) {
		int hwndCB = parent.hwndCB;
		TBBUTTONINFO info = new TBBUTTONINFO ();
		info.cbSize = TBBUTTONINFO.sizeof;
		info.dwMask = OS.TBIF_STATE;
		OS.SendMessage (hwndCB, OS.TB_GETBUTTONINFO, id, info);
		info.fsState &= ~OS.TBSTATE_ENABLED;
		if (enabled) info.fsState |= OS.TBSTATE_ENABLED;
		OS.SendMessage (hwndCB, OS.TB_SETBUTTONINFO, id, info);		
	} else {
		int hMenu = parent.handle;
		if (OS.IsWinCE) {
			int index = parent.indexOf (this);
			if (index == -1) return;
			int uEnable = OS.MF_BYPOSITION | (enabled ? OS.MF_ENABLED : OS.MF_GRAYED);
			OS.EnableMenuItem (hMenu, index, uEnable);
		} else {
			MENUITEMINFO info = new MENUITEMINFO ();
			info.cbSize = MENUITEMINFO.sizeof;
			info.fMask = OS.MIIM_STATE;
			boolean success = OS.GetMenuItemInfo (hMenu, id, false, info);
			if (!success) error (SWT.ERROR_CANNOT_SET_ENABLED);
			int bits = OS.MFS_DISABLED | OS.MFS_GRAYED;
			if (enabled) {
				if ((info.fState & bits) == 0) return;
				info.fState &= ~bits;
			} else {
				if ((info.fState & bits) == bits) return;
				info.fState |= bits;
			}
			success = OS.SetMenuItemInfo (hMenu, id, false, info);
			if (!success) error (SWT.ERROR_CANNOT_SET_ENABLED);
		}
	}
	*/
	parent.destroyAccelerators ();
	parent.redraw ();
}

/**
 * Sets the image the receiver will display to the argument.
 * <p>
 * Note: This operation is a hint and is not supported on
 * platforms that do not have this concept (for example, Windows NT).
 * </p>
 *
 * @param image the image to display
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setImage (Image image) {
	checkWidget ();
	if ((style & SWT.SEPARATOR) != 0) return;
	super.setImage (image);
	if (image != null) {
		String key = "menu-enable-image";
		String cssName = parent.handle.className;
		if (cssName == null) cssName = "";
		int idx = cssName.indexOf(key);
		if (idx == -1) {
			parent.handle.className += " " + key; 
		}
		
		if (this.image.handle == null && this.image.url != null && this.image.url.length() != 0) {
			OS.clearChildren(imageEl);
			CSSStyle handleStyle = imageEl.style;
			if (image.url.toLowerCase().endsWith(".png") && handleStyle.filter != null) {
//					Element imgBackground = document.createElement("DIV");
//					imgBackground.style.position = "absolute";
//					imgBackground.style.width = "100%";
//					imgBackground.style.height = "100%";
//					imgBackground.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\"" + this.image.url + "\", sizingMethod=\"image\")";
//					handle.appendChild(imgBackground);
				handleStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\"" + this.image.url + "\", sizingMethod=\"image\")";
			} else {
				handleStyle.backgroundImage = "url(\"" + this.image.url + "\")";
			}
		}
	}
	/*
	if (OS.IsWinCE) {
		if ((OS.IsPPC || OS.IsSP) && parent.hwndCB != 0) {
			int hwndCB = parent.hwndCB;
			TBBUTTONINFO info = new TBBUTTONINFO ();
			info.cbSize = TBBUTTONINFO.sizeof;
			info.dwMask = OS.TBIF_IMAGE;
			info.iImage = parent.imageIndex (image);
			OS.SendMessage (hwndCB, OS.TB_SETBUTTONINFO, id, info);
		}
		return;
	}
	if (OS.WIN32_VERSION < OS.VERSION (4, 10)) {
		return;
	}
	MENUITEMINFO info = new MENUITEMINFO ();
	info.cbSize = MENUITEMINFO.sizeof;
	info.fMask = OS.MIIM_BITMAP;
	if (image != null) info.hbmpItem = OS.HBMMENU_CALLBACK;
	int hMenu = parent.handle;
	OS.SetMenuItemInfo (hMenu, id, false, info);
	*/
	parent.redraw ();
}

/**
 * Sets the receiver's pull down menu to the argument.
 * Only <code>CASCADE</code> menu items can have a
 * pull down menu. The sequence of key strokes, button presses
 * and/or button releases that are used to request a pull down
 * menu is platform specific.
 *
 * @param menu the new pull down menu
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_MENU_NOT_DROP_DOWN - if the menu is not a drop down menu</li>
 *    <li>ERROR_MENUITEM_NOT_CASCADE - if the menu item is not a <code>CASCADE</code></li>
 *    <li>ERROR_INVALID_ARGUMENT - if the menu has been disposed</li>
 *    <li>ERROR_INVALID_PARENT - if the menu is not in the same widget tree</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setMenu (Menu menu) {
	checkWidget ();

	/* Check to make sure the new menu is valid */
	if ((style & SWT.CASCADE) == 0) {
		error (SWT.ERROR_MENUITEM_NOT_CASCADE);
	}
	if (menu != null) {
		if (menu.isDisposed()) error(SWT.ERROR_INVALID_ARGUMENT);
		if ((menu.style & SWT.DROP_DOWN) == 0) {
			error (SWT.ERROR_MENU_NOT_DROP_DOWN);
		}
		if (menu.parent != parent.parent) {
			error (SWT.ERROR_INVALID_PARENT);
		}
	}

	/* Assign the new menu */
	Menu oldMenu = this.menu;
	if (oldMenu == menu) return;
	if (oldMenu != null) oldMenu.cascade = null;
	this.menu = menu;

	/*
	/* Assign the new menu in the OS *-/		
	if ((OS.IsPPC || OS.IsSP) && parent.hwndCB != 0) {
		if (OS.IsPPC) {
			int hwndCB = parent.hwndCB;
			int hMenu = menu == null ? 0 : menu.handle;
			OS.SendMessage (hwndCB, OS.SHCMBM_SETSUBMENU, id, hMenu);
		}
		if (OS.IsSP) error (SWT.ERROR_CANNOT_SET_MENU);
	} else {
		/*
		* Feature in Windows.  When SetMenuItemInfo () is used to
		* set a submenu and the menu item already has a submenu,
		* Windows destroys the previous menu.  This is undocumented
		* and unexpected but not necessarily wrong.  The fix is to
		* remove the item with RemoveMenu () which does not destroy
		* the submenu and then insert the item with InsertMenuItem ().
		*-/
		int hMenu = parent.handle;
		MENUITEMINFO info = new MENUITEMINFO ();
		info.cbSize = MENUITEMINFO.sizeof;
		info.fMask = OS.MIIM_DATA;
		int index = 0;
		while (OS.GetMenuItemInfo (hMenu, index, true, info)) {
			if (info.dwItemData == id) break;
			index++;
		}
		if (info.dwItemData != id) return;
		boolean hasBitmap = false, success = false;
		
		/*
		* Bug in Windows.  When GetMenuItemInfo() is used to get the text,
		* for an item that has a bitmap set using MIIM_BITMAP, the text is
		* not returned.  This means that when SetMenuItemInfo() is used to
		* set the submenu and the current menu state, the text is lost.
		* The fix is to temporarily remove the bitmap and restore it after
		* the text and submenu have been set.
		*-/
		if (!OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION (4, 10)) {
			info.fMask = OS.MIIM_BITMAP;
			OS.GetMenuItemInfo (hMenu, index, true, info);
			hasBitmap = info.hbmpItem != 0;
			if (hasBitmap) {
				info.hbmpItem = 0;
				success = OS.SetMenuItemInfo (hMenu, id, false, info);
			}
		}
		
		int cch = 128;
		int hHeap = OS.GetProcessHeap ();
		int byteCount = cch * TCHAR.sizeof;
		int pszText = OS.HeapAlloc (hHeap, OS.HEAP_ZERO_MEMORY, byteCount);
		info.fMask = OS.MIIM_STATE | OS.MIIM_ID | OS.MIIM_TYPE | OS.MIIM_DATA;
		info.dwTypeData = pszText;
		info.cch = cch;
		success = OS.GetMenuItemInfo (hMenu, index, true, info);
		if (menu != null) {
			menu.cascade = this; 
			info.fMask |= OS.MIIM_SUBMENU;
			info.hSubMenu = menu.handle;
		}
		OS.RemoveMenu (hMenu, index, OS.MF_BYPOSITION);
		if (OS.IsWinCE) {
			/*
			* On WinCE, InsertMenuItem() is not available.  The fix is to
			* use SetMenuItemInfo() but this call does not set the menu item
			* state and submenu.  The fix is to use InsertMenu() to insert
			* the item, SetMenuItemInfo() to set the string and EnableMenuItem()
			* and CheckMenuItem() to set the state.
			*-/
			int uIDNewItem = id;
			int uFlags = OS.MF_BYPOSITION;
			if (menu != null) {
				uFlags |= OS.MF_POPUP;
				uIDNewItem = menu.handle;
			}
			TCHAR lpNewItem = new TCHAR (0, " ", true);
			success = OS.InsertMenu (hMenu, index, uFlags, uIDNewItem, lpNewItem);
			if (success) {
				info.fMask = OS.MIIM_DATA | OS.MIIM_TYPE;
				success = OS.SetMenuItemInfo (hMenu, index, true, info);
				if ((info.fState & (OS.MFS_DISABLED | OS.MFS_GRAYED)) != 0) {
					OS.EnableMenuItem (hMenu, index, OS.MF_BYPOSITION | OS.MF_GRAYED);
				}
				if ((info.fState & OS.MFS_CHECKED) != 0) {
					OS.CheckMenuItem (hMenu, index, OS.MF_BYPOSITION | OS.MF_CHECKED);
				}
			}
		} else {
			success = OS.InsertMenuItem (hMenu, index, true, info);
			/*
			* Restore the bitmap that was removed to work around a problem
			* in GetMenuItemInfo() and menu items that have bitmaps set with
			* MIIM_BITMAP.
			*-/
			if (OS.WIN32_VERSION >= OS.VERSION (4, 10)) {
				if (hasBitmap) {
					info.fMask = OS.MIIM_BITMAP;
					info.hbmpItem = OS.HBMMENU_CALLBACK;
					success = OS.SetMenuItemInfo (hMenu, id, false, info);
				}
			}
			
		}
		if (pszText != 0) OS.HeapFree (hHeap, 0, pszText);
		if (!success) error (SWT.ERROR_CANNOT_SET_MENU);
	}
	*/
	parent.destroyAccelerators ();
}

boolean setRadioSelection (boolean value) {
	if ((style & SWT.RADIO) == 0) return false;
	if (getSelection () != value) {
		setSelection (value);
		postEvent (SWT.Selection);
	}
	return true;
}

/**
 * Sets the selection state of the receiver.
 * <p>
 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
 * it is selected when it is checked.
 *
 * @param selected the new selection state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (boolean selected) {
	checkWidget ();
	if ((style & (SWT.CHECK | SWT.RADIO)) == 0) return;
	
	String key = ((style & SWT.CHECK) != 0) ? "menu-item-checked" : "menu-item-selected";
	
	String cssName = statusEl.className;
	if (cssName == null) cssName = "";
	int idx = cssName.indexOf(key);
	if (selected) {
		if (idx == -1) {
			statusEl.className += " " + key; 
		}
	} else {
		if (idx != -1) {
			statusEl.className = cssName.substring(0, idx) + cssName.substring(idx + key.length());
		}
	}
	
	/*
	if ((OS.IsPPC || OS.IsSP) && parent.hwndCB != 0) return;
	int hMenu = parent.handle;
	if (OS.IsWinCE) {
		int index = parent.indexOf (this);
		if (index == -1) return;
		int uCheck = OS.MF_BYPOSITION | (selected ? OS.MF_CHECKED : OS.MF_UNCHECKED);
		OS.CheckMenuItem (hMenu, index, uCheck);
	} else {
		MENUITEMINFO info = new MENUITEMINFO ();
		info.cbSize = MENUITEMINFO.sizeof;
		info.fMask = OS.MIIM_STATE;
		boolean success = OS.GetMenuItemInfo (hMenu, id, false, info);
		if (!success) error (SWT.ERROR_CANNOT_SET_SELECTION);
		info.fState &= ~OS.MFS_CHECKED;
		if (selected) info.fState |= OS.MFS_CHECKED;
		success = OS.SetMenuItemInfo (hMenu, id, false, info);
		if (!success) error (SWT.ERROR_CANNOT_SET_SELECTION);
	}
	*/
	parent.redraw ();
}
/**
 * Sets the receiver's text. The string may include
 * the mnemonic character and accelerator text.
 * <p>
 * Mnemonics are indicated by an '&amp' that causes the next
 * character to be the mnemonic.  When the user presses a
 * key sequence that matches the mnemonic, a selection
 * event occurs. On most platforms, the mnemonic appears
 * underlined but may be emphasised in a platform specific
 * manner.  The mnemonic indicator character '&amp' can be
 * escaped by doubling it in the string, causing a single
 *'&amp' to be displayed.
 * </p>
 * <p>
 * Accelerator text is indicated by the '\t' character.
 * On platforms that support accelerator text, the text
 * that follows the '\t' character is displayed to the user,
 * typically indicating the key stroke that will cause
 * the item to become selected.  On most platforms, the
 * accelerator text appears right aligned in the menu.
 * Setting the accelerator text does not install the
 * accelerator key sequence. The accelerator key sequence
 * is installed using #setAccelerator.
 * </p>
 * 
 * @param string the new text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @see #setAccelerator
 */
public void setText (String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	if ((style & SWT.SEPARATOR) != 0) return;
	if (text.equals (string)) return;
	super.setText (string);
	if (textEl.childNodes != null && textEl.childNodes.length != 0) {
		OS.clearChildren(textEl);
	}
	int idx = text.indexOf('\t');
	String normalText = text;
	String accelText = null;
	if (idx != -1) {
		normalText = text.substring(0, idx);
		accelText = text.substring(idx + 1);
	}
	if (accelText != null && accelText.length() != 0) {
		Element accel = document.createElement("DIV");
		accel.className = "menu-item-accel";
		textEl.appendChild(accel);
		accel.appendChild(document.createTextNode(accelText));
	}
	OS.insertText(textEl, normalText);
	textEl.style.display = "block";
	
	/*
	int hHeap = OS.GetProcessHeap ();
	int pszText = 0;
	boolean success = false;
	if ((OS.IsPPC || OS.IsSP) && parent.hwndCB != 0) {
		/*
		* Bug in WinCE PPC.  Tool items on the menubar don't resize
		* correctly when the character '&' is used (even when it
		* is a sequence '&&').  The fix is to remove all '&' from
		* the string. 
		*-/
		if (string.indexOf ('&') != -1) {
			int length = string.length ();
			char[] text = new char [length];
			string.getChars( 0, length, text, 0);
			int i = 0, j = 0;
			for (i=0; i<length; i++) {
				if (text[i] != '&') text [j++] = text [i];
			}
			if (j < i) string = new String (text, 0, j);
		}
		/* Use the character encoding for the default locale *-/
		TCHAR buffer = new TCHAR (0, string, true);
		int byteCount = buffer.length () * TCHAR.sizeof;
		pszText = OS.HeapAlloc (hHeap, OS.HEAP_ZERO_MEMORY, byteCount);
		OS.MoveMemory (pszText, buffer, byteCount);	
		int hwndCB = parent.hwndCB;
		TBBUTTONINFO info2 = new TBBUTTONINFO ();
		info2.cbSize = TBBUTTONINFO.sizeof;
		info2.dwMask = OS.TBIF_TEXT;
		info2.pszText = pszText;
		success = OS.SendMessage (hwndCB, OS.TB_SETBUTTONINFO, id, info2) != 0;
	} else {
		MENUITEMINFO info = new MENUITEMINFO ();
		info.cbSize = MENUITEMINFO.sizeof;
		int hMenu = parent.handle;
		
		/*
		* Bug in Windows 2000.  For some reason, when MIIM_TYPE is set
		* on a menu item that also has MIIM_BITMAP, the MIIM_TYPE clears
		* the MIIM_BITMAP style.  The fix is to reset both MIIM_BITMAP.
		* Note, this does not happen on Windows 98.
		*-/
		boolean hasBitmap = false;
		if (!OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION (4, 10)) {
			info.fMask = OS.MIIM_BITMAP;
			OS.GetMenuItemInfo (hMenu, id, false, info);
			hasBitmap = info.hbmpItem != 0;
		}
		
		/* Use the character encoding for the default locale *-/
		TCHAR buffer = new TCHAR (0, string, true);
		int byteCount = buffer.length () * TCHAR.sizeof;
		pszText = OS.HeapAlloc (hHeap, OS.HEAP_ZERO_MEMORY, byteCount);
		OS.MoveMemory (pszText, buffer, byteCount);	
		info.fMask = OS.MIIM_TYPE;
		info.fType = widgetStyle ();
		info.dwTypeData = pszText;
		success = OS.SetMenuItemInfo (hMenu, id, false, info);

		/*
		* Restore the bitmap that was removed to work around a problem
		* in GetMenuItemInfo() and menu items that have bitmaps set with
		* MIIM_BITMAP.
		*-/
		if (!OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION (4, 10)) {
			if (hasBitmap) {
				info.fMask = OS.MIIM_BITMAP;
				info.hbmpItem = OS.HBMMENU_CALLBACK;
				success = OS.SetMenuItemInfo (hMenu, id, false, info);
			}
		}
	}
	if (pszText != 0) OS.HeapFree (hHeap, 0, pszText);
	if (!success) error (SWT.ERROR_CANNOT_SET_TEXT);
	*/
	parent.redraw ();
}

/*
int widgetStyle () {
	int bits = 0;
	Decorations shell = parent.parent;
	if ((shell.style & SWT.MIRRORED) != 0) {
		if ((parent.style & SWT.LEFT_TO_RIGHT) != 0) {
			bits |= OS.MFT_RIGHTJUSTIFY | OS.MFT_RIGHTORDER;
		}
	} else {
		if ((parent.style & SWT.RIGHT_TO_LEFT) != 0) {
			bits |= OS.MFT_RIGHTJUSTIFY | OS.MFT_RIGHTORDER;
		}
	}
	if ((style & SWT.SEPARATOR) != 0) return bits | OS.MFT_SEPARATOR;
	if ((style & SWT.RADIO) != 0) return bits | OS.MFT_RADIOCHECK;
	return bits | OS.MFT_STRING;
}

LRESULT wmCommandChild (int wParam, int lParam) {
	if ((style & SWT.CHECK) != 0) {
		setSelection (!getSelection ());
	} else {
		if ((style & SWT.RADIO) != 0) {
			if ((parent.getStyle () & SWT.NO_RADIO_GROUP) != 0) {
				setSelection (!getSelection ());
			} else {
				selectRadio ();
			}
		}
	}
	Event event = new Event ();
	setInputState (event, SWT.Selection);
	postEvent (SWT.Selection, event);
	return null;
}

LRESULT wmDrawChild (int wParam, int lParam) {
	DRAWITEMSTRUCT struct = new DRAWITEMSTRUCT ();
	OS.MoveMemory (struct, lParam, DRAWITEMSTRUCT.sizeof);
	if (image != null) {
		GCData data = new GCData();
		data.device = display;
		GC gc = GC.win32_new (struct.hDC, data);
		/*
		* Bug in Windows.  When a bitmap is included in the
		* menu bar, the HDC seems to already include the left
		* coordinate.  The fix is to ignore this value when
		* the item is in a menu bar.
		*-/
		int x = (parent.style & SWT.BAR) != 0 ? (OS.IsWin95 ? 4 : 2) : struct.left;
		gc.drawImage (image, x, struct.top + 2);
		gc.dispose ();
	}
	return null;
}

LRESULT wmMeasureChild (int wParam, int lParam) {
	MEASUREITEMSTRUCT struct = new MEASUREITEMSTRUCT ();
	OS.MoveMemory (struct, lParam, MEASUREITEMSTRUCT.sizeof);
	int width = 0, height = 0;
	if (image != null) {
		Rectangle rect = image.getBounds ();
		width = rect.width;
		height = rect.height;
	} else {
		/*
		* Bug in Windows.  If a menu contains items that have
		* images and can be checked, Windows does not include
		* the width of the image and the width of the check when
		* computing the width of the menu.  When the longest item
		* does not have an image, the label and the accelerator
		* text can overlap.  The fix is to use SetMenuItemInfo()
		* to indicate that all items have a bitmap and then include
		* the width of the widest bitmap in WM_MEASURECHILD.
		*-/
		MENUINFO lpcmi = new MENUINFO ();
		lpcmi.cbSize = MENUINFO.sizeof;
		lpcmi.fMask = OS.MIM_STYLE;
		int hMenu = parent.handle;
		OS.GetMenuInfo (hMenu, lpcmi);
		if ((lpcmi.dwStyle & OS.MNS_CHECKORBMP) == 0) {
			MenuItem [] items = parent.getItems ();
			for (int i=0; i<items.length; i++) {
				MenuItem item = items [i];
				if (item.image != null) {
					Rectangle rect = item.image.getBounds ();
					width = Math.max (width, rect.width); 
				}
			}
		}
	}
	if (width != 0 || height != 0) {
		/*
		* Feature in Windows.  On Windows 98, it is necessary
		* to add 4 pixels to the width of the image or the image
		* and text are too close.  On other Windows platforms,
		* this causes the text of the longest item to touch the
		* accelerator text.  The fix is to add only 2 pixels in
		* this case.
		*-/
		struct.itemWidth = width + (OS.IsWin95 ? 4 : 2);
		struct.itemHeight = height + 4;
		OS.MoveMemory (lParam, struct, MEASUREITEMSTRUCT.sizeof);
	}
	return null;
}
*/

}

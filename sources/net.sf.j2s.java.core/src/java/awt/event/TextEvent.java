/*
 * Some portions of this file have been modified by Robert Hanson hansonr.at.stolaf.edu 2012-2017
 * for use in SwingJS via transpilation into JavaScript using Java2Script.
 *
 * Copyright (c) 1996, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.awt.event;

import java.awt.AWTEvent;

/**
 * A semantic event which indicates that an object's text changed.
 * This high-level event is generated by an object (such as a TextComponent)
 * when its text changes. The event is passed to
 * every <code>TextListener</code> object which registered to receive such
 * events using the component's <code>addTextListener</code> method.
 * <P>
 * The object that implements the <code>TextListener</code> interface gets
 * this <code>TextEvent</code> when the event occurs. The listener is
 * spared the details of processing individual mouse movements and key strokes
 * Instead, it can process a "meaningful" (semantic) event like "text changed".
 *
 * @author Georges Saab
 *
 * @see java.awt.TextComponent
 * @see TextListener
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/textlistener.html">Tutorial: Writing a Text Listener</a>
 *
 * @since 1.1
 */

public class TextEvent extends AWTEvent {

    /**
     * The first number in the range of ids used for text events.
     */
    public static final int TEXT_FIRST  = 900;

    /**
     * The last number in the range of ids used for text events.
     */
    public static final int TEXT_LAST   = 900;

    /**
     * This event id indicates that object's text changed.
     */
    public static final int TEXT_VALUE_CHANGED  = TEXT_FIRST;

    /*
     * JDK 1.1 serialVersionUID
     */
    //private static final long serialVersionUID = 6269902291250941179L;

    /**
     * Constructs a <code>TextEvent</code> object.
     * <p>Note that passing in an invalid <code>id</code> results in
     * unspecified behavior. This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     *
     * @param source the (<code>TextComponent</code>) object that
     *               originated the event
     * @param id     an integer that identifies the event type
     * @throws IllegalArgumentException if <code>source</code> is null
     */
    public TextEvent(Object source, int id) {
        super(source, id);
    }


    /**
     * Returns a parameter string identifying this text event.
     * This method is useful for event-logging and for debugging.
     *
     * @return a string identifying the event and its attributes
     */
    @Override
		public String paramString() {
        String typeStr;
        switch(id) {
          case TEXT_VALUE_CHANGED:
              typeStr = "TEXT_VALUE_CHANGED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr;
    }
}
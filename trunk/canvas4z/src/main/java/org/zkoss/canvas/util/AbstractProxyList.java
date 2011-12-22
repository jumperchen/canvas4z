/* AbstractProxyList.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Dec 22, 2011 11:39:25 AM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author simonpai
 */
public abstract class AbstractProxyList<T> implements List<T> {
	
	protected final List<T> _list;
	
	public AbstractProxyList(List<T> list) {
		_list = list;
	}
	
	// read //
	@Override
	public boolean contains(Object o) {
		return _list.contains(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return _list.containsAll(c);
	}
	@Override
	public T get(int index) {
		return _list.get(index);
	}
	@Override
	public int indexOf(Object o) {
		return _list.indexOf(o);
	}
	@Override
	public boolean isEmpty() {
		return _list.isEmpty();
	}
	@Override
	public Iterator<T> iterator() {
		return _list.iterator();
	}
	@Override
	public int lastIndexOf(Object o) {
		return _list.lastIndexOf(o);
	}
	@Override
	public ListIterator<T> listIterator() {
		return _list.listIterator();
	}
	@Override
	public ListIterator<T> listIterator(int index) {
		return _list.listIterator(index);
	}
	@Override
	public int size() {
		return _list.size();
	}
	@Override
	public Object[] toArray() {
		return _list.toArray();
	}
	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return _list.toArray(a);
	}
	
	// write //
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c)
			changed |= remove(o);
		return changed;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		Iterator<T> iter = iterator();
		while (iter.hasNext())
			if (!c.contains(iter.next())) {
				iter.remove();
				changed = true;
			}
		return changed;
	}
	
}

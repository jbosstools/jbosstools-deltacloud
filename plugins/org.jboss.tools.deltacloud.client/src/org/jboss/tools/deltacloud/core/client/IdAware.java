/*******************************************************************************
 * Copyright (c) 2010 Red Hat Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Incorporated - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.deltacloud.core.client;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Martyn Taylor
 * @author André Dietisheim
 */
public abstract class IdAware {

	protected String id;

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "IdAware [id=" + id + "]";
	}

}

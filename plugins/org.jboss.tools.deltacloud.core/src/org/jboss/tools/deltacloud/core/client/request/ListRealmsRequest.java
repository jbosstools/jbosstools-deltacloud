/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.deltacloud.core.client.request;

import java.net.URL;


/**
 * Lists realms on the deltacloud server. 
 */
public class ListRealmsRequest extends AbstractListObjectsRequest {
	
	public ListRealmsRequest(URL baseUrl) {
		super(baseUrl, "realms");
	}
}
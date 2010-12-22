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
package org.jboss.tools.deltacloud.integration.wizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.deltacloud.core.DeltaCloudInstance;
import org.jboss.tools.deltacloud.core.job.AbstractInstanceJob;
import org.jboss.tools.deltacloud.integration.Messages;
import org.jboss.tools.deltacloud.integration.rse.util.RSEUtils;
import org.jboss.tools.deltacloud.ui.Activator;
import org.jboss.tools.deltacloud.ui.ErrorUtils;
import org.jboss.tools.deltacloud.ui.IDeltaCloudPreferenceConstants;
import org.osgi.service.prefs.Preferences;

public class CreateRSEFromInstanceJob extends AbstractInstanceJob {
	private Job nextJob2 = null;
	public CreateRSEFromInstanceJob(DeltaCloudInstance instance, String family) {
		super("Create RSE Host from DeltaCloud Instance", instance, family);
	}
	public IStatus doRun(IProgressMonitor monitor) {
		return runRSEJob(getInstance(), monitor);
	}
	
	public void setNextJob(Job job) {
		super.setNextJob(job);
		this.nextJob2 = job;
	}
	
	private IStatus runRSEJob(DeltaCloudInstance instance, IProgressMonitor monitor) {
		String hostname = RSEUtils.createHostName(instance);
		if (hostname != null && hostname.length() > 0 && isAutoconnect()) {
			try {
				String connectionName = RSEUtils.createConnectionName(instance);
				IHost host = RSEUtils.createHost(connectionName,
						RSEUtils.createHostName(instance),
						RSEUtils.getSSHOnlySystemType(),
						RSEUtils.getSystemRegistry());
				if( nextJob2 != null && nextJob2 instanceof CreateServerFromRSEJob) {
					((CreateServerFromRSEJob)nextJob2).setHost(host);
				}
				RSEUtils.connect(connectionName, RSEUtils.getConnectorService(host));
			} catch (Exception e) {
				return ErrorUtils.handleError(Messages.ERROR,
						NLS.bind(Messages.COULD_NOT_LAUNCH_RSE_EXPLORER2, instance.getName()),
						e, Display.getDefault().getActiveShell());
			}
		}
		return Status.OK_STATUS;
	}
	private boolean isAutoconnect() {
		Preferences prefs = new InstanceScope().getNode(Activator.PLUGIN_ID);
		boolean autoConnect = prefs.getBoolean(IDeltaCloudPreferenceConstants.AUTO_CONNECT_INSTANCE, true);
		return autoConnect;
	}
}
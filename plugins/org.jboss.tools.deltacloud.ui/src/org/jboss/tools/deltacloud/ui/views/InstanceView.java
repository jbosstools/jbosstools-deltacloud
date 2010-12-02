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
package org.jboss.tools.deltacloud.ui.views;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;
import org.jboss.tools.deltacloud.core.DeltaCloud;
import org.jboss.tools.deltacloud.core.DeltaCloudInstance;
import org.jboss.tools.deltacloud.core.IInstanceListListener;
import org.jboss.tools.deltacloud.ui.IDeltaCloudPreferenceConstants;

/**
 * @author Jeff Johnston
 * @author Andre Dietisheim
 */
public class InstanceView extends AbstractCloudChildrenTableView<DeltaCloudInstance> implements IInstanceListListener {

	protected String getSelectedCloudPrefsKey() {
		return IDeltaCloudPreferenceConstants.LAST_CLOUD_INSTANCE_VIEW;
	}

	@Override
	protected String getViewID() {
		return "org.jboss.tools.deltacloud.ui.views.InstanceView";
	}

	@Override
	protected ITableContentAndLabelProvider getContentAndLabelProvider() {
		return new InstanceViewLabelAndContentProvider();
	}
	
	@Override
	protected void refreshToolbarCommandStates() {
		IEvaluationService evaluationService = (IEvaluationService) PlatformUI.getWorkbench().getService(
				IEvaluationService.class);
		evaluationService.requestEvaluation("org.jboss.tools.deltacloud.ui.commands.canStart");
		evaluationService.requestEvaluation("org.jboss.tools.deltacloud.ui.commands.canStop");
		evaluationService.requestEvaluation("org.jboss.tools.deltacloud.ui.commands.canReboot");
		evaluationService.requestEvaluation("org.jboss.tools.deltacloud.ui.commands.canDestroy");
	}


	protected void addListener(DeltaCloud currentCloud) {
		if (currentCloud != null) {
			currentCloud.removeInstanceListListener(this);
			currentCloud.addInstanceListListener(this);
		}
	}

	protected void removeListener(DeltaCloud currentCloud) {
		if (currentCloud != null) {
			currentCloud.removeInstanceListListener(this);
			currentCloud.addInstanceListListener(this);
		}
	}
}

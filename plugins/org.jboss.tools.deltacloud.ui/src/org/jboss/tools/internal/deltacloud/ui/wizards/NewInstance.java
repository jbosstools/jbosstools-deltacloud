package org.jboss.tools.internal.deltacloud.ui.wizards;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.jboss.tools.deltacloud.core.DeltaCloud;
import org.jboss.tools.deltacloud.core.DeltaCloudException;
import org.jboss.tools.deltacloud.core.DeltaCloudImage;
import org.jboss.tools.deltacloud.core.DeltaCloudInstance;
import org.jboss.tools.deltacloud.ui.Activator;
import org.jboss.tools.deltacloud.ui.IDeltaCloudPreferenceConstants;
import org.osgi.service.prefs.Preferences;

public class NewInstance extends Wizard {

	private final static String CREATE_INSTANCE_FAILURE_TITLE = "CreateInstanceError.title"; //$NON-NLS-1$
	private final static String CREATE_INSTANCE_FAILURE_MSG = "CreateInstanceError.msg"; //$NON-NLS-1$
	private final static String DEFAULT_REASON = "CreateInstanceErrorReason.msg"; //$NON-NLS-1$
	private final static String CONFIRM_CREATE_TITLE = "ConfirmCreate.title"; //$NON-NLS-1$
	private final static String CONFIRM_CREATE_MSG = "ConfirmCreate.msg"; //$NON-NLS-1$
	private final static String DONT_SHOW_THIS_AGAIN_MSG = "DontShowThisAgain.msg"; //$NON-NLS-1$
	private final static String STARTING_INSTANCE_MSG = "StartingInstance.msg"; //$NON-NLS-1$
	
	private NewInstancePage mainPage;
	
	private DeltaCloud cloud;
	private DeltaCloudImage image;
	private DeltaCloudInstance instance;

	public NewInstance(DeltaCloud cloud, DeltaCloudImage image) {
		this.cloud = cloud;
		this.image = image;
	}
	
	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		mainPage = new NewInstancePage(cloud, image);
		addPage(mainPage);
	}
	
	@Override
	public boolean canFinish() {
		return mainPage.isPageComplete();
	}
	
	@Override
	public boolean performFinish() {
		String imageId = image.getId();
		String profileId = mainPage.getHardwareProfile();
		String realmId = mainPage.getRealmId();
		String name = null;
		try {
			name = URLEncoder.encode(mainPage.getInstanceName(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //$NON-NLS-1$
		
		boolean result = false;
		String errorMessage = WizardMessages.getString(DEFAULT_REASON);
		try {
			Preferences prefs = new InstanceScope().getNode(Activator.PLUGIN_ID);
			boolean dontShowDialog = prefs.getBoolean(IDeltaCloudPreferenceConstants.DONT_CONFIRM_CREATE_INSTANCE, false);
			if (!dontShowDialog) {
				MessageDialogWithToggle dialog =
					MessageDialogWithToggle.openOkCancelConfirm(getShell(), WizardMessages.getString(CONFIRM_CREATE_TITLE), 
							WizardMessages.getString(CONFIRM_CREATE_MSG), 
							WizardMessages.getString(DONT_SHOW_THIS_AGAIN_MSG), 
							false, null, null);
				int retCode = dialog.getReturnCode();
				boolean toggleState = dialog.getToggleState();
				if (retCode == Dialog.CANCEL)
					return true;
				// If warning turned off by user, set the preference for future usage
				if (toggleState) {
					prefs.putBoolean(IDeltaCloudPreferenceConstants.DONT_CONFIRM_CREATE_INSTANCE, true);
				}
			}
			instance = cloud.createInstance(name, imageId, realmId, profileId);
			if (instance != null)
				result = true;
			final String instanceId = instance.getId();
			final String instanceName = name;
			if (instance != null) {
				IWorkbench wb = PlatformUI.getWorkbench();
				IProgressService ps = wb.getProgressService();
				try {
					ps.busyCursorWhile(new IRunnableWithProgress() {
						public void run(IProgressMonitor pm) {
							pm.beginTask(WizardMessages.getFormattedString(STARTING_INSTANCE_MSG, new String[] {instanceName}), IProgressMonitor.UNKNOWN);
							pm.worked(1);
							try {
								for (int i = 0; i < 2; ++i) {
									instance = cloud.refreshInstance(instanceId);
									if (!instance.getState().equals(DeltaCloudInstance.PENDING))
										break;
								}
							} finally {
								pm.done();
							}
						}
					});
				} catch(Exception ex) {
					errorMessage = ex.getLocalizedMessage();
				}
			}
		} catch (DeltaCloudException e) {
			errorMessage = e.getLocalizedMessage();
		}
		if (!result) {
			ErrorDialog.openError(this.getShell(),
					WizardMessages.getString(CREATE_INSTANCE_FAILURE_TITLE),
					WizardMessages.getFormattedString(CREATE_INSTANCE_FAILURE_MSG, new String[] {name, imageId, realmId, profileId}),
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, errorMessage));
		}
		return result;
	}

}

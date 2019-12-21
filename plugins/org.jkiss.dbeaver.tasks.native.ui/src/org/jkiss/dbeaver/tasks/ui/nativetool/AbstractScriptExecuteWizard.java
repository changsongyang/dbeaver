/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2019 Serge Rider (serge@jkiss.org)
 * Copyright (C) 2011-2012 Eugene Fradkin (eugene.fradkin@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.tasks.ui.nativetool;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.task.DBTTask;
import org.jkiss.dbeaver.tasks.nativetool.AbstractScriptExecuteSettings;
import org.jkiss.dbeaver.tasks.ui.nativetool.internal.TaskNativeUIMessages;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.utils.GeneralUtils;

import java.io.File;
import java.util.Collection;

public abstract class AbstractScriptExecuteWizard<SETTINGS extends AbstractScriptExecuteSettings<BASE_OBJECT>, BASE_OBJECT extends DBSObject, PROCESS_ARG>
        extends AbstractToolWizard<SETTINGS, BASE_OBJECT, PROCESS_ARG> implements IImportWizard
{
    protected AbstractScriptExecuteWizard(Collection<BASE_OBJECT> dbObject, String task) {
        super(dbObject, task);
	}

    protected AbstractScriptExecuteWizard(DBTTask task) {
        super(task);
    }

    @Override
    protected abstract SETTINGS createSettings();

    @Override
    protected boolean isSingleTimeWizard() {
        return false;
    }

    public File getInputFile()
    {
        return new File(getSettings().getInputFile());
    }

    public void setInputFile(File inputFile)
    {
        getSettings().setInputFile(inputFile == null ? null : inputFile.getAbsolutePath());
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(taskTitle);
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        //super.addPages(); // Do not add base wizard pages. They can be added explicitly thru addTaskConfigPages
        addPage(logPage);
    }

	@Override
	public void onSuccess(long workTime) {
        UIUtils.showMessageBox(getShell(),
            taskTitle,
                NLS.bind(TaskNativeUIMessages.tools_script_execute_wizard_task_completed, taskTitle, getObjectsName()) , //$NON-NLS-1$
                        SWT.ICON_INFORMATION);
	}

    @Override
    protected void startProcessHandler(DBRProgressMonitor monitor, PROCESS_ARG arg, ProcessBuilder processBuilder, Process process)
    {
        logPage.startLogReader(
            processBuilder,
            process.getInputStream());
        new TextFileTransformerJob(monitor, getInputFile(), process.getOutputStream(), getInputCharset(), getOutputCharset()).start();
    }

    @Override
    protected boolean isMergeProcessStreams()
    {
        return true;
    }

    protected String getInputCharset() {
        return GeneralUtils.UTF8_ENCODING;
    }

    protected String getOutputCharset() {
        return GeneralUtils.UTF8_ENCODING;
    }

}

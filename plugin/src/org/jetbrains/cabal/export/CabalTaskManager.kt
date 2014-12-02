package org.jetbrains.cabal.export

import com.intellij.openapi.externalSystem.task.ExternalSystemTaskManager

import com.intellij.openapi.externalSystem.model.ExternalSystemException
import com.intellij.openapi.externalSystem.model.settings.ExternalSystemExecutionSettings
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener

import java.io.File

public class CabalTaskManager() : ExternalSystemTaskManager<ExternalSystemExecutionSettings> {


    throws(javaClass<ExternalSystemException>())
    override fun cancelTask(id: ExternalSystemTaskId, listener: ExternalSystemTaskNotificationListener): Boolean {
        return false
    }

    throws(javaClass<ExternalSystemException>())
    public override fun executeTasks(
            id: ExternalSystemTaskId,
            taskNames: MutableList<String>,
            projectPath: String,
            settings: ExternalSystemExecutionSettings?,
            vmOptions: MutableList<String>,
            arg: MutableList<String>,
            scriptParameters: String?,
            listener: ExternalSystemTaskNotificationListener): Unit {
        return
    }
}


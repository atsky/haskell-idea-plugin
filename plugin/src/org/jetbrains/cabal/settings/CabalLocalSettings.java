package org.jetbrains.cabal.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.externalSystem.service.project.PlatformFacade;
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemLocalSettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.externalSystem.model.ProjectSystemId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.cabal.util.UtilPackage;

@State(name = "CabalLocalSettings", storages = {@Storage(file = StoragePathMacros.WORKSPACE_FILE)} )
public class CabalLocalSettings extends AbstractExternalSystemLocalSettings
        implements PersistentStateComponent<AbstractExternalSystemLocalSettings.State>
{

    public CabalLocalSettings(@NotNull Project project, @NotNull PlatformFacade facade) {
        super(UtilPackage.getSYSTEM_ID(), project, facade);
    }

    @NotNull
    public static CabalLocalSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, CabalLocalSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        State state = new State();
        fillState(state);
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        super.loadState(state);
    }
}
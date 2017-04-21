package org.jetbrains.cabal.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemLocalSettings;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.cabal.util.CabalConstantsKt;

@State(name = "CabalLocalSettings", storages = {@Storage(file = StoragePathMacros.WORKSPACE_FILE)})
public class CabalLocalSettings extends AbstractExternalSystemLocalSettings
        implements PersistentStateComponent<AbstractExternalSystemLocalSettings.State> {

    public CabalLocalSettings(@NotNull Project project) {
        super(CabalConstantsKt.getSYSTEM_ID(), project);
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
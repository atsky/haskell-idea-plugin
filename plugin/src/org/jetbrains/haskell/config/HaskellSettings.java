package org.jetbrains.haskell.config;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.util.OsUtil;
import org.jetbrains.haskell.util.UtilPackage;

import java.io.File;

@State(
        name = "HaskellConfiguration",
        storages = {
                @Storage(id = "default", file = StoragePathMacros.APP_CONFIG + "/haskell.xml")
        }
)
public class HaskellSettings implements PersistentStateComponent<HaskellSettings.State> {

    @NotNull
    public static HaskellSettings getInstance() {
        HaskellSettings persisted = ServiceManager.getService(HaskellSettings.class);
        if (persisted == null) {
            persisted = new HaskellSettings();
        }
        if (persisted.getState().ghcModPath == null) {
            OsUtil os = UtilPackage.getOS();
            persisted.getState().ghcModPath = os.getCabalBin() + File.separator + "ghc-mod" + os.getExe();
        }
        return persisted;
    }

    public static class State {
        public String ghcModPath;
    }

    State myState = new State();

    public State getState() {
        return myState;
    }

    public void loadState(State state) {
        myState = state;
    }


}

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

        if (persisted.getState().cabalPath == null) {
            persisted.getState().cabalPath = "cabal";
        }

        if (persisted.getState().cabalDataPath == null) {
            OsUtil os = UtilPackage.getOS();
            persisted.getState().cabalDataPath = os.getCabalData();
        }

        if (persisted.getState().ghcModPath == null) {
            OsUtil os = UtilPackage.getOS();
            persisted.getState().ghcModPath = os.getDefaultCabalBin() + File.separator + "ghc-mod" + os.getExe();
        }

        if (persisted.getState().ghcModiPath == null) {
            OsUtil os = UtilPackage.getOS();
            persisted.getState().ghcModiPath = os.getDefaultCabalBin() + File.separator + "ghc-modi" + os.getExe();
        }

        if (persisted.getState().buildWrapperPath == null) {
            OsUtil os = UtilPackage.getOS();
            persisted.getState().buildWrapperPath = os.getDefaultCabalBin() + File.separator + "buildwrapper" + os.getExe();
        }

        if (persisted.getState().scionBrowserPath == null) {
            OsUtil os = UtilPackage.getOS();
            persisted.getState().scionBrowserPath = os.getDefaultCabalBin() + File.separator + "scion-browser" + os.getExe();
        }


        return persisted;
    }

    public static class State {
        public String ghcModPath;
        public String ghcModiPath;
        public String buildWrapperPath;
        public String scionBrowserPath;
        public String cabalPath;
        public String cabalDataPath;
    }

    State myState = new State();

    @NotNull
    public State getState() {
        return myState;
    }

    public void loadState(State state) {
        myState = state;
    }


}

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

    public HaskellSettings() {
        update();
    }

    @NotNull
    public static HaskellSettings getInstance() {
        HaskellSettings persisted = ServiceManager.getService(HaskellSettings.class);
        if (persisted == null) {
            return new HaskellSettings();
        }
        return persisted;
    }

    public static class State {
        public String ghcModPath;
        public String ghcModiPath;
        public String cabalPath;
        public String cabalDataPath;
        public Boolean useGhcMod;
        public Boolean usePty;
    }

    State myState = new State();

    void update() {
        if (myState.cabalPath == null) {
            myState.cabalPath = "cabal";
        }

        OsUtil os = UtilPackage.getOS();

        if (myState.cabalDataPath == null) {
            myState.cabalDataPath = os.getCabalData();
        }

        if (myState.ghcModPath == null) {
            myState.ghcModPath = os.getDefaultCabalBin() + File.separator + "ghc-mod" + os.getExe();
        }

        if (myState.ghcModiPath == null) {
            myState.ghcModiPath = os.getDefaultCabalBin() + File.separator + "ghc-modi" + os.getExe();
        }

        if (myState.useGhcMod == null) {
            myState.useGhcMod = true;
        }
        if (myState.usePty == null) {
            myState.usePty = true;
        }
    }

    @NotNull
    public State getState() {
        return myState;
    }

    public void loadState(State state) {
        myState = state;
        update();
    }

}

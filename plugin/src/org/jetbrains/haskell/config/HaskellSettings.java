package org.jetbrains.haskell.config;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.util.OSUtil;

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
        public Boolean usePtyProcess;
    }

    State myState = new State();

    void update() {
        if (myState.cabalPath == null) {
            myState.cabalPath = "cabal";
        }


        if (myState.cabalDataPath == null) {
            myState.cabalDataPath = OSUtil.getCabalData();
        }

        if (myState.ghcModPath == null) {
            myState.ghcModPath = OSUtil.getDefaultCabalBin() + File.separator + "ghc-mod" + OSUtil.getExe();
        }

        if (myState.ghcModiPath == null) {
            myState.ghcModiPath = OSUtil.getDefaultCabalBin() + File.separator + "ghc-modi" + OSUtil.getExe();
        }

        if (myState.useGhcMod == null) {
            myState.useGhcMod = true;
        }
        if (myState.usePtyProcess == null) {
            myState.usePtyProcess = false;
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

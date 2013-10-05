package org.jetbrains.haskell.module;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@State(
  name = "CabalPackages",
  storages = {
    @Storage(file = StoragePathMacros.PROJECT_FILE),
    @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/compiler.xml", scheme = StorageScheme.DIRECTORY_BASED)
  }
)
public class CabalPackagesContainer implements PersistentStateComponent<CabalPackagesContainer.PackageConfig> {

  private PackageConfig myPackages = new PackageConfig();

  @Nullable
  @Override
  public PackageConfig getState() {
    return myPackages;
  }

  @Override
  public void loadState(PackageConfig state) {
    myPackages = state;
  }

  @NotNull
  public static CabalPackagesContainer getInstance(@NotNull Project project) {
    final CabalPackagesContainer persisted = ServiceManager.getService(project, CabalPackagesContainer.class);
    return persisted != null ? persisted : new CabalPackagesContainer();
  }

  public static class PackageConfig {
      public List<String> myPackages = new ArrayList<String>();
    }
}

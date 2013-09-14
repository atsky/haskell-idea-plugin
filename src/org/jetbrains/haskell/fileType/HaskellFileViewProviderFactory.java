package org.jetbrains.haskell.fileType;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.PsiManager;
import org.jetbrains.haskell.HaskellViewProvider;

public class HaskellFileViewProviderFactory implements FileViewProviderFactory {

  public FileViewProvider createFileViewProvider(VirtualFile file, Language language, PsiManager manager, boolean physical) {
    return new HaskellViewProvider(manager, file, physical, language);
  }
}

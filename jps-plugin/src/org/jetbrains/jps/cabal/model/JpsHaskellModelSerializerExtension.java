package org.jetbrains.jps.cabal.model;

import com.intellij.openapi.util.JDOMUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.cabal.CabalBuilder;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.serialization.JpsGlobalExtensionSerializer;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JpsHaskellModelSerializerExtension extends JpsModelSerializerExtension {
    @NotNull
    @Override
    public List<? extends JpsGlobalExtensionSerializer> getGlobalExtensionSerializers() {
        return Collections.singletonList(new JpsGlobalConfigurationSerializer());
    }

    private static class JpsGlobalConfigurationSerializer extends JpsGlobalExtensionSerializer {
        protected JpsGlobalConfigurationSerializer() {
            super("haskell.xml", "HaskellConfiguration");
        }

        @Override
        public void loadExtension(@NotNull JpsGlobal global, @NotNull Element componentTag) {
            for (Element option : JDOMUtil.getChildren(componentTag, "option")) {
                if ("cabalPath".equals(option.getAttributeValue("name"))) {
                    CabalBuilder.cabalPath = option.getAttributeValue("value");
                }
            }
        }
        @Override
        public void saveExtension(@NotNull JpsGlobal global, @NotNull Element componentTag) {
        }

    }

    @NotNull
    @Override
    public List<? extends JpsModulePropertiesSerializer<?>> getModulePropertiesSerializers() {
        return Collections.singletonList(new JpsHaskellModulePropertiesSerializer());
    }

    @NotNull
    @Override
    public List<? extends JpsSdkPropertiesSerializer<?>> getSdkPropertiesSerializers() {
        return Collections.singletonList(new JpsHaskellSdkPropertiesSerializer());
    }

    private static class JpsHaskellSdkPropertiesSerializer extends JpsSdkPropertiesSerializer<JpsSimpleElement<JpsHaskellSdkProperties>> {
        private static final String HOME_PATH = "homePath";

        public JpsHaskellSdkPropertiesSerializer() {
            super("GHC", HaskellSdkType.INSTANCE);
        }

        @NotNull
        @Override
        public JpsSimpleElement<JpsHaskellSdkProperties> loadProperties(@Nullable Element propertiesElement) {
            String ghcPath;

            if (propertiesElement != null) {
                Element parent = (Element) propertiesElement.getParent();
                ghcPath = parent.getChild(HOME_PATH).getAttributeValue("value");
            } else {
                ghcPath = null;
            }
            return JpsElementFactory.getInstance().createSimpleElement(new JpsHaskellSdkProperties(ghcPath));
        }

        @Override
        public void saveProperties(@NotNull JpsSimpleElement<JpsHaskellSdkProperties> properties, @NotNull Element element) {
        }
    }
}
package org.jetbrains.jps.cabal;

import com.intellij.openapi.util.JDOMUtil;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.serialization.JpsGlobalExtensionSerializer;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;

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
}
package org.jetbrains.jps.cabal.model;

import org.jdom.Element;
import org.jetbrains.jps.cabal.JpsHaskellModuleType;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.impl.JpsSimpleElementImpl;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;

/**
 * @author atsky
 * @since 14/05/15.
 */
public class JpsHaskellModulePropertiesSerializer extends JpsModulePropertiesSerializer<JpsSimpleElement<?>> {

    public JpsHaskellModulePropertiesSerializer() {
        super(JpsHaskellModuleType.INSTANCE, "HASKELL_MODULE", "Haskell.ModuleBuildProperties");
    }

    @Override
    public JpsSimpleElement<?> loadProperties(Element componentElement) {
        return new JpsSimpleElementImpl<Object>(null);
    }

    @Override
    public void saveProperties(JpsSimpleElement<?> properties, Element componentElement) {

    }
}

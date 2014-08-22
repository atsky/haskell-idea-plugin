package org.jetbrains.cabal.parser;

import com.intellij.testFramework.ParsingTestCase;
import org.junit.Test;

public class CabalParsingTest extends ParsingTestCase {
    static {
        System.setProperty("idea.platform.prefix", "Idea");
    }

    public CabalParsingTest() {
        super("cabalParserTests", "cabal", new CaballParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "data";
    }

    @Test
    public void testBool() throws Exception { doTest(true); }

    @Test
    public void testFreeForm() throws Exception { doTest(true); }

    @Test
    public void testFreeLine() throws Exception { doTest(true); }

    @Test
    public void testIdentifier() throws Exception { doTest(true); }

    @Test
    public void testToken() throws Exception { doTest(true); }

    @Test
    public void testSimpleVersion() throws Exception { doTest(true); }

    @Test
    public void testVersionConstraint() throws Exception { doTest(true); }

    @Test
    public void testComplexVersionConstraint() throws Exception { doTest(true); }

    @Test
    public void testFullVersionConstraint() throws Exception { doTest(true); }

    @Test
    public void testSimpleTopLevel() throws Exception { doTest(true); }

    @Test
    public void testSimpleBuildInfo() throws Exception { doTest(true); }

    @Test
    public void testSimpleCondition() throws Exception { doTest(true); }

    @Test
    public void testComplexCondition() throws Exception { doTest(true); }

    @Test
    public void testIfElseSection() throws Exception { doTest(true); }

    @Test
    public void testSimpleOptionalCommaList() throws Exception { doTest(true); }
}

package org.jetbrains.yesod.hamlet.parser;

import com.intellij.psi.tree.IElementType;

public interface HamletTokenTypes {

    public static IElementType DOT = new HamletToken(".");
    public static IElementType OCURLY = new HamletToken("{");
    public static IElementType CCURLY = new HamletToken("}");
    public static IElementType AT = new HamletToken("@");
    public static IElementType EQUAL = new HamletToken("=");
    public static IElementType SHARP = new HamletToken(".");
    public static IElementType OANGLE = new HamletToken("<");
    public static IElementType CANGLE = new HamletToken(">");
    public static IElementType IDENTIFIER = new HamletToken("identifier");

}

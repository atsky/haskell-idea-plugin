package org.jetbrains.cabal.parser

// https://github.com/ghc/packages-Cabal/blob/master/Cabal/Distribution/PackageDescription/Parse.hs

public val BUILD_INFO: List<String> = listOf(
                            "buildable",
                            "build-tools",
                            "cpp-options",
                            "cc-options",
                            "ld-options",
                            "pkgconfig-depends",
                            "frameworks",
                            "c-sources",
                            "default-language",
                            "other-languages",
                            "default-extensions",
                            "other-extensions",
                            "extensions",
                            "extra-libraries",
                            "extra-lib-dirs",
                            "includes",
                            "install-includes",
                            "include-dirs",
                            "hs-source-dirs",
                            "other-modules",
                            "ghc-prof-options",
                            "ghc-shared-options",
                            "ghc-options",
                            "hugs-options",
                            "nhc98-options",
                            "jhc-options")



public val PKG_DESCR_FIELD_DESCRS: List<String> = listOf(
                            "name",
                            "version",
                            "disp",
                            "cabal-version",
                            "build-type",
                            "license",
                            "license-file",
                            "copyright",
                            "maintainer",
                            "build-depends",
                            "stability",
                            "homepage",
                            "package-url",
                            "bug-reports",
                            "synopsis",
                            "description",
                            "category",
                            "author",
                            "tested-with",
                            "data-files",
                            "data-dir",
                            "extra-source-files",
                            "extra-tmp-files")


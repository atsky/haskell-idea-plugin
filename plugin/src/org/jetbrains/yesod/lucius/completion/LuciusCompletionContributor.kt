package org.jetbrains.yesod.lucius.completion

/**
 * @author Leyla H
 */

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder

import java.util.Arrays

public class LuciusCompletionContributor : CompletionContributor() {

        override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {

                if (parameters.getCompletionType() === CompletionType.BASIC) {
                        for (tagName in AT_RULES) {
                                result.addElement(LookupElementBuilder.create(tagName))
                        }
                        for (tagName in DATA_TYPES) {
                                result.addElement(LookupElementBuilder.create(tagName))
                        }
                        for (tagName in PROPERTIES) {
                                result.addElement(LookupElementBuilder.create(tagName))
                        }
                        for (tagName in PSEUDO_CLASSES) {
                                result.addElement(LookupElementBuilder.create(tagName))
                        }
                        for (tagName in PSEUDO_ELEMENTS) {
                                result.addElement(LookupElementBuilder.create(tagName))
                        }
                        for (tagName in VALUES) {
                                result.addElement(LookupElementBuilder.create(tagName))
                        }
                        for (tagName in FUNCTIONS) {
                                result.addElement(LookupElementBuilder.create(tagName))
                        }
                }
        }

        companion object {

                public var AT_RULES: List<String> = Arrays.asList(

                        "@charset",               "@counter-style",             "@document",            "@font-face",
                        "@font-feature-values",   "@import",                    "@keyframes",           "@media",
                        "@media.any-hover",       "@media.any-pointer",         "@media.aspect-ratio",  "@media.color",
                        "@media.color-index",     "@media.device-aspect-ratio", "@media.device-height", "@media.device-width",
                        "@media.grid",            "@media.height",              "@media.hover",         "@media.inverted-colors",
                        "@media.light-level",     "@media.monochrome",          "@media.orientation",   "@media.overflow-block",
                        "@media.overflow-inline", "@media.pointer",             "@media.resolution",    "@media.scan",
                        "@media.scripting",       "@media.update-frequency",    "@media.width",         "@namespace",
                        "@page", "@supports",     "@viewport",                  "@viewport.max-zoom",   "@viewport.min-zoom",
                        "@viewport.orientation",  "@viewport.user-zoom",        "@viewport.zoom"
                )

                public var DATA_TYPES: List<String> = Arrays.asList(

                        "angle",      "basic-shape", "color",  "frequency",  "gradient",        "image",
                        "integer",    "length",      "number", "percentage", "position",        "ratio",
                        "resolution", "shape",       "string", "time",       "timing-function", "uri",
                        "user-ident"

                )

                public var FUNCTIONS: List<String> = Arrays.asList(

                        "attr",                       "calc",        "cubic-bezier",
                        "element",                    "hsl",         "hsla",
                        "linear-gradient",            "matrix",      "matrix3d",
                        "radial-gradient",            "rect",        "repeating-linear-gradient",
                        "repeating-radial-gradient",  "rgb",         "rgba",
                        "rotate",                     "rotate3d",    "rotateX",
                        "rotateY",                    "rotateZ",     "scale",
                        "scale3d",                    "scaleX",      "scaleY",
                        "scaleZ",                     "skew",        "skewX",
                        "skewY",                      "steps",       "translate",
                        "translate3d",                "translateX",  "translateY",
                        "translateZ",                 "url"
                )

                public var PROPERTIES: List<String> = Arrays.asList(

                        "align-content",             "align-items",               "align-self",                "all",
                        "animation",                 "animation-delay",           "animation-direction",       "animation-duration",
                        "animation-fill-mode",       "animation-iteration-count", "animation-name",            "animation-play-state",
                        "animation-timing-function", "backface-visibility",       "background",                "background-attachment",
                        "background-blend-mode",     "background-clip",           "background-color",          "background-image",
                        "background-origin",         "background-position",       "background-repeat",         "background-size",
                        "bleed",                     "blend-mode",                "block-size",                "border",
                        "border-block-end",          "border-block-end-color",    "border-block-end-style",    "border-block-end-width",
                        "border-block-start",        "border-block-start-color",  "border-block-start-style",  "border-block-start-width",
                        "border-bottom",             "border-bottom-color",       "border-bottom-left-radius", "border-bottom-right-radius",
                        "border-bottom-style",       "border-bottom-width",       "border-collapse",           "border-color",
                        "border-image",              "border-image-outset",       "border-image-repeat",       "border-image-slice",
                        "border-image-source",       "border-image-width",        "border-inline-end",         "border-inline-end-color",
                        "border-inline-end-style",   "border-inline-end-width",   "border-inline-start",       "border-inline-start-color",
                        "border-inline-start-style", "border-inline-start-width", "border-left",               "border-left-color",
                        "border-left-style",         "border-left-width",         "border-radius",             "border-right",
                        "border-right-color",        "border-right-style",        "border-right-width",        "border-spacing",
                        "border-style",              "border-top",                "border-top-color",          "border-top-left-radius",
                        "border-top-right-radius",   "border-top-style",          "border-top-width",          "border-width",
                        "bottom",                    "box-decoration-break",      "box-shadow",                "box-sizing",
                        "break-after",               "break-before",              "break-inside",              "caption-side",
                        "clear",                     "clip",                      "clip-path",                 "color",
                        "column-count",              "column-fill",               "column-gap",                "column-rule",
                        "column-rule-color",         "column-rule-style",         "column-rule-width",         "column-span",
                        "column-width",              "columns",                   "content",                   "counter-increment",
                        "counter-reset",             "cursor",                    "custom-ident",              "direction",
                        "display",                   "empty-cells",               "fallback",                  "filter",
                        "flex",                      "flex-basis",                "flex-direction",            "flex-flow",
                        "flex-grow",                 "flex-shrink",               "flex-wrap",                 "float",
                        "font",                      "font-family",               "font-feature-settings",     "font-kerning",
                        "font-language-override",    "font-size",                 "font-size-adjust",          "font-stretch",
                        "font-style",                "font-synthesis",            "font-variant",              "font-variant-alternates",
                        "font-variant-caps",         "font-variant-east-asian",   "font-variant-ligatures",    "font-variant-numeric",
                        "font-variant-position",     "font-weight",               "height",                    "hyphens",
                        "image-orientation",         "image-rendering",           "ime-mode",                  "inline-size",
                        "isolation",                 "justify-content",           "left",                      "letter-spacing",
                        "line-break",                "line-height",               "list-style",                "list-style-image",
                        "list-style-position",       "list-style-type",           "margin",                    "margin-block-end",
                        "margin-block-start",        "margin-bottom",             "margin-inline-end",         "margin-inline-start",
                        "margin-left",               "margin-right",              "margin-top",                "marks",
                        "mask",                      "mask-type",                 "max-block-size",            "max-height",
                        "max-inline-size",           "max-width",                 "min-block-size",            "min-height",
                        "min-inline-size",           "min-width",                 "mix-blend-mode",            "mq-boolean",
                        "negative",                  "object-fit",                "object-position",           "offset-block-end",
                        "offset-block-end clone",    "offset-block-start",        "offset-inline-end",         "offset-inline-start",
                        "opacity",                   "order",                     "orphans",                   "outline",
                        "outline-color",             "outline-offset",            "outline-style",             "outline-width",
                        "overflow",                  "overflow-clip-box",         "overflow-wrap",             "overflow-x",
                        "overflow-y",                "pad",                       "padding",                   "padding-block-end",
                        "padding-block-start",       "padding-bottom",            "padding-inline-end",        "padding-inline-start",
                        "padding-left",              "padding-right",             "padding-top",               "page-break-after",
                        "page-break-before",         "page-break-inside",         "perspective",               "perspective-origin",
                        "pointer-events",            "position",                  "prefix",                    "quotes",
                        "range",                     "resize",                    "right",                     "ruby-align",
                        "ruby-position",             "scroll-behavior",           "scroll-snap-coordinate",    "scroll-snap-destination",
                        "scroll-snap-points-x",      "scroll-snap-points-y",      "scroll-snap-type",          "shape-image-threshold",
                        "shape-margin",              "shape-outside",             "speak-as",                  "suffix",
                        "symbols",                   "system",                    "tab-size",                  "table-layout",
                        "text-align",                "text-align-last",           "text-decoration",           "text-decoration-color",
                        "text-decoration-line",      "text-decoration-style",     "text-indent",               "text-orientation",
                        "text-overflow",             "text-rendering",            "text-shadow",               "text-transform",
                        "text-underline-position",   "top",                       "touch-action",              "transform",
                        "transform-function",        "transform-origin",          "transform-style",           "transition",
                        "transition-delay",          "transition-duration",       "transition-property",       "transition-timing-function",
                        "unicode-bidi",              "unicode-range",             "vertical-align",            "visibility",
                        "white-space",               "widows",                    "width",                     "will-change",
                        "word-break",                "word-spacing",              "word-wrap",                 "writing-mode",
                        "z-index"

                )

                public var PSEUDO_CLASSES: List<String> = Arrays.asList(

                        "::backdrop",  ":active",         ":any",              ":checked",      ":default",       ":dir",
                        ":disabled",   ":empty",          ":enabled",          ":first",        ":first-child",   ":first-of-type",
                        ":focus",      ":fullscreen",     ":hover",            ":in-range",     ":indeterminate", ":invalid",
                        ":lang",       ":last-child",     ":last-of-type",     ":left",         ":link",          ":not",
                        ":nth-child",  ":nth-last-child", ":nth-last-of-type", ":nth-of-type",  ":only-child",    ":only-of-type",
                        ":optional",   ":out-of-range",   ":read-only",        ":read-write",   ":required",      ":right",
                        ":root",       ":scope",          ":target",           ":valid",        ":visited"


                )

                public var PSEUDO_ELEMENTS: List<String> = Arrays.asList(

                        "::after", "::before", "::first-letter", "::first-line", "::selection"
                )

                public var VALUES: List<String> = Arrays.asList(

                        "auto",     "currentColor", "ease",       "ease-in",     "ease-in-out",
                        "ease-out", "inherit",      "initial",    "linear",      "none",
                        "normal",   "step-end",     "step-start", "transparent", "unset"
                )

        }
}
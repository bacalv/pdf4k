# Content

Now that we've explained the basics of stationary, you probably want to learn about what content you can put
in a block.

Content can consist of:

* Paragraphs.
* Images.
* Tables that can contain paragraphs, images and nested tables.

At each level, you can apply `style`s.

## Styles

The following example shows how styles can be composed...

```kotlin
pdf {
    section {
        content {
            +"This is plain text.\n\n"
            style(fontStyle = Bold) {
                +"This is in bold.\n\n"
                style(colour = RED) {
                    +"This is bold and red.\n\n"
                    "This is bold, red and underlined in blue." and style(underlined = true, underlineColour = BLUE)
                }
            }
        }
    }
}
```

You can also assign styles to variables and use those within the `style` function, for example...

```kotlin
val bold = style(fontStyle = Bold)

pdf {
    section {
        content {
            style(bold) {
                +"Bold!"
            }
        }
    }
}
```

Most of the functions will accept a style attribute. The below example will render the same output as above...

```kotlin
val bold = style(fontStyle = Bold)

pdf {
    section {
        content(style = bold) {
            +"Bold!"
        }
    }
}
```

See [the style reference](./style-reference.md) for more details and examples of using styles.

### Fonts

pdf4k has several built-in fonts. For example...

```kotlin
TODO("Fonts example")
```

## Paragraphs

Paragraphs are the main building block for adding text to a block.

### Leading ('ledding')

## Tables

Tables allow you to render content in a tabular fashion within a section.

For example...

```kotlin
pdf {
    section {
        content {
            table(columns = 4, weights = listOf(2f, 2f, 1f, 3f)) {
                style(header) {
                    textCell("Employee Number")
                    textCell("Name")
                    textCell("DOB")
                    textCell("Address")
                }
                style(oddRow) {
                    textCell("EMP-A3138D")
                    textCell("SMITH, John")
                    textCell("1997-10-30")
                    textCell("123 Fake Street, FA1 K3E")
                }
                style(evenRow) {
                    textCell("EMP-B7727D")
                    textCell("HENDRIX, Jimi")
                    textCell("1942-10-27")
                    textCell("25 Brook Street, W1K 4HB")
                }
            }
        }
    }
}
```

### Nested tables

pdf4k allows you to nest tables, for example...

```kotlin
pdf {
    section {
        content {
            table(columns = 2) {
                tableCell(columns = 2) {
                    textCell("1A")
                    textCell("1B")
                    textCell("1C")
                    textCell("1D")
                }
                textCell("2")
                textCell("3")
                textCell("4")
            }
        }.approve(approver)
    }
}
```

## Images

pdf4k allows you to render images in your PDF files.

To render an image that is stored in the `images/hendrix.png` of the classpath of your project, you can do the
following...

```kotlin
pdf {
    sedtion {
        content {
            image("hendrix.png", width = 200f, height = 200f)
        }
    }
}
```

[Back to concepts](./concepts.md)
# pdf4k.io User Guide

Welcome to the user guide for pdf4k!

## Introduction

### The testing framework

pdf4k encourages you to write tests for your code.

The `testing` [library](../../libraries/testing/README.md) was the first module in this project that was defined.

We need to verify that the current version of our code generates the desired PDF output.

The `testing` module defines a JUnit 5 plugin that can be used to verify that your generated PDF files match an approved
version. It can be used stand-alone without any other pdf4k dependencies.

Based on the [okeydoke](TODO) approval test framework, this module performs the following comparisons between generated 
and approved PDF files...

- It compares pixel for pixel that each page in the generated PDF matches the approved version
- It checks that the metadata of the generated PDF (author, title, custom properties etc) matches the approved version
- It checks that external and internal links match the approved version
- It checks that security attributes and user permissions of the generated document match the approved version

See [PDFAssert](../../libraries/testing/src/main/kotlin/io/pdf4k/testing/PdfAssert.kt) for the code that performs these
assertions.

### Getting started building your own pdf4k application

The best place to start is a simple 'hello world' example.

Use [this GitHub project](TODO) to get started.

#### Structure:

- src/main/kotlin - contains the code to render `Hello, world!` on a blank A4 page.
- src/test/kotlin - contains code that verifies the main code generates a PDF that matches the approved PDF.
- src/test/resources - contains the current approved version of the PDF.
- build.gradle - imports the minimal pdf4k dependencies.
- Boilerplate gradle files - `gradlew` script etc

#### Building `hello world`

`./gradle clean build`

#### What happened?

We just ran our first pdf4k approval test. We can see that our code generates a PDF that matches
[this approved PDF](TODO).

If we change the line...

```kotlin
+"Hello, world!"
```

...in [HelloWorld.kt](TODO) to...

```kotlin
+"Hey there!"
```

...and run the build again, we'd see the following test failure...

```
    Page 1 differes etc TODO
```

In an IDE such as JIdea, we can install the [Okey Doke Plugin](TODO) to make it easier to approve the actual version.

Here is a [short video demonstrating this](TODO).

## Stationary

In order to define what sort of 'paper' to draw content on, pdf4k has the concept of `stationary`.

`stationary` consists of:

- A page from an existing PDF file.
  - By default, this is "page 1" of the built-in "Blank A4 page" PDF file.
- A set of `block`s that will be used to render your content in.
  - e.g. header, footer, main body etc
  - By default, there is a single `block` that fits on an A4 page with a margin.
- Instructions on how to paginate content on the page.
  - By default, there is a single block to fill.

### How content is paginated using stationary

The following is an example of some `stationary` that is based on a blank A4 page. It has two `block`s called `col1` and 
`col2`...

```kotlin
val twoColumns = BlankA4Portrait.withBlocks {
    block(name = "col1", x = 24f, y = 24f, w = 262f, h = 796f)
    block(name = "col2", x = 310f, y = 24f, w = 262f, h = 796f)
    contentFlow("col1", "col2")
}
```

It specifies that the content of a `section` should fill `col1` first and then `col2`.

If we define a `section` based on this `stationary` then the `section`'s main `content` will start to fill up the block 
`col1`.

If `col1` runs out of space, the remainder of the `content` will be rendered in `col2`.

If `col2` runs out of space, then the next page will be started which will be based on the same page template starting
at `col1`again.

For example...

```kotlin
pdf {
    section(stationary = listOf(twoColumns)) {
        content {
            +"A very very ...(very * 1000)... very long string"
        }
    }
}
```

The content will be paginated over the two columns (`col1` and `col2`) on the first page and when `col2` is full, a new 
page will be started and the content will start to fill `col1` on this new page.

This process will continue until all the content has been rendered.

#### The coordinate system

The `x` and `y` coordinates define where to start drawing the block and the `w` and `h` define the width and height.

The origin is *the bottom-left hand corner*, so `x=10` and `y=20` means 10 units from the *bottom* of the page and 20
units from the left.

### Paginating a section's content using multiple stationary items 

In the real world, you probably would like to use a different page template for the second page than what you did for
the first page.

For example, imagine your energy supplier sends you a letter advising you that the already extortionate amount of money
that you pay each month for gas and electricity is going to increase at the end of the year. To justify this, the text 
in the letter is so large that it will fill more than one page.

The template they would use for the first page will typically be different to the continuation page(s).

pdf4k allows you to do this by specifying more than one element in the `stationary` list of a section, for example...

```kotlin
pdf {
    section(stationary = listOf(firstOPage, continuationPage)) {
        content {
            +"A very very ...(very * 1000)... very long string"
        }
    }
}
```

In the example above, when all the blocks in the `firstPage` run out of space, the content will continue to be
rendered on the `continuationPage`. If all the blocks in the second page are exhausted, then a second
`continuationPage` will be used to render the rest of the content.

### Blocks that are not paginated

Pages typically consist of other blocks that you would like to fill with content, but without pagination.

For example, if we wanted to render a "page number' in the footer of each page, we could define a `footer` block in our
stationary. For example...

```kotlin
val pageWithFooter = BlankA4Portrait.withBlocks {
    block(name = "content", x = 50f, y = 600f, w = 120f, h = 120f)
    block(name = "footer", x = 310f, y = 24f, w = 262f, h = 796f)
    contentFlow("content")
}
```

We would like to use the `content` block to render the main content of the section, but we'd also like to render the
current page number in the footer.

This stationary can be used as follows...

```kotlin
pdf {
    section(stationary = listOf(pageWithFooter)) {
        content {
            +"Main content goes here!"
        }
      
        block("footer") {
            style(align = Center) {
                +"- "
                pageNumber()
                +" -"
            }
        }
    }
}
```

Each time a new page is started, all the blocks defined within a section are rendered on that page. For example...

```kotlin
pdf {
    section(stationary = listOf(pageWithFooter)) {
        content {
            +"This text should be on the first page"
            pagebreak()
            +"This text should be on the second page"
        }
      
        block("footer") {
            style(align = Center) {
                +"- "
                pageNumber()
                +" -"
            }
        }
    }
}
```

So on page 1, the `content` block will contain the text `This text should be on the first page` and the footer will
contain the text `- 1 -` and on the second page the `content` block will contain the text `This text should be on the 
second page` and the footer will contain the text `- 2 -`.

If a block that is defined in a `section` is not defined in the current `stationary`, it will be ignored.

For example, if we added `BlankA4Portrait` as the second stationary item in the list for the section, the footer
wouldn't be rendered on the second page because `BlankA4Portrait` does not define a footer. For example...

```kotlin
pdf {
    section(stationary = listOf(pageWithFooter, BlankA4Portrait)) {
        content {
            +"This text should be on the first page - has a footer"
            pagebreak()
            +"This text should be on the second page - different page template used - so no footer"
        }
      
        block("footer") {
            style(align = Center) {
                +"- "
                pageNumber()
                +" -"
            }
        }
    }
}
```

## Content

Now that we've explained the basics of stationary, you probably want to learn about what content you can put
in a block.

Content can consist of:

* Paragraphs.
* Images.
* Tables that can contain paragraphs, images and nested tables.

At each level, you can apply `style`s.

### Styles

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

See [the style reference](TODO) for more details and examples of using styles.

#### Fonts

pdf4k has several built-in fonts. For example...

```kotlin
TODO("Fonts example")
```

### Paragraphs

Paragraphs are the main building block for adding text to a block.

#### Leading ('ledding')

### Tables

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

#### Nested tables

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

### Images

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

## Resources

## Plugins

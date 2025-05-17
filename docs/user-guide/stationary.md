# Stationary

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

[Back to concepts](./concepts.md)
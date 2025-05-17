# pdf4k.io

A Kotlin DSL for generating PDF files.

```kotlin
pdf {
    section {
        content { +"Hello, world!" }
    }
}
```

## Example

```shell
git clone https://github.com/bacalv/hello-pdf4k
cd hello-pdf4k
./gradlew clean build
open src/test/resources/HelloTest.hello\ world.approved.pdf
```

## Goals

* Provide an idiomatic way of generating PDF files using Kotlin.
* Simplify using iText / OpenPDF.
* Provide a JUnit plugin to help verify that generated PDF files are correct.
* Enjoy creating production-ready PDF server applications.

## Project Structure

| Directory                                | Contents                                       |
|------------------------------------------|------------------------------------------------|
| [applications](./applications/README.md) | Dockerized runnable applications and examples. |
| [libraries](./libraries/README.md)       | Shared libraries                               |
| [plugins](./plugins/README.md)           | Non-essential plugins                          |
| plugins/XYZ/domain/src                   | A plugin's common domain                       |
| plugins/XYZ/dsl/src                      | DSL extensions for the plugin                  |
| plugins/XYZ/server/src                   | Runtime/server implementation of the plugin    |

* Libraries depend on zero ot more third party dependencies
* Plugins depend on libraries
* Applications depend on plugins and libraries and generate runnable Docker images
* Every directory underneath this one (up until `src`) should have a README.md

## Resources

* [Documentation](docs/README.md)
* [Decision record](ADR.md)
* [TODO list](TODO.md)
* [Wish list](WISHLIST.md)
* Open source fonts
* Okeydoke IntelliJ plugin

## Acknowledgements

* iText
* Okeydoke
* PDF Box
* Http4K
* Bouncycastle
* Jackson

## Handy links

* Open source fonts
* Okeydoke IntelliJ plugin

## Authors

- Bret Calvey (Work: bac@juxt.pro / Home: bacalv@gmail.com)
- Please contribute :)

## License

- TODO

## Disclaimer

- All code supplied 'as is'
- Use at your own risk - no liability accepted
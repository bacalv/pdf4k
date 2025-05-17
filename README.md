# pdf4k.io

## Project Goals

* Provide an idiomatic way of generating PDF files using Kotlin.
* Simplify using iText / OpenPDF.
* Provide a JUnit plugin to help verify that generated PDF files are correct.
* Provide a framework to create production-ready PDF server applications.

## Hello World

```kotlin
pdf {
    section { 
        content { +"Hello, world!" }
    }
}
```

[Hello World GitHub Project](https://github.com/bacalv/hello-pdf4k).

```shell
git clone https://github.com/bacalv/hello-pdf4k
cd hello-pdf4k
./gradlew clean build
open src/test/resources/HelloTest.hello\ world.approved.pdf
```

## Documentation

Documentation can be found [here](docs/README.md).

## Project Structure

| Directory                      | Contents                                       |
|--------------------------------|------------------------------------------------|
| [applications](./applications/README.md)            | Dockerized runnable applications and examples. |
| [libraries](./libraries/README.md)               | Shared libraries.                              |
| [plugins](./plugins/README.md)                 | Non-essential plugins.                         |
| [scripts](./scripts/README.md) | Handy scripts.                                 |

* Libraries depend on zero or more third party dependencies.
* Plugins depend on libraries.
* Applications depend on libraries and optional plugins and generate runnable Docker images.
* Every subproject should have a README.md containing more details.

## Handy Links

* [iText](https://itextpdf.com/) - pdf4k is based on iText.
* [OpenPDF](https://librepdf.github.io/OpenPDF/) - Open PDF is a fork of iText.
* [PDF Box](https://pdfbox.apache.org/) - PDF Box is used to render PDFs into images (used by the testing framework).
* [http4k](https://www.http4k.org/) - http4k is used by the pdf4k server components.
* [Okey-doke IntelliJ plugin](https://plugins.jetbrains.com/plugin/9424-okey-doke-support) - IntelliJ plugin for working with approval tests.
* [PDF Viewer IntelliJ plugin](https://plugins.jetbrains.com/plugin/14494-pdf-viewer) - IntelliJ plugin to view PDF files.
* [thumbnailator](https://github.com/coobird/thumbnailator) - used to create thumbnails of images (used by thumbnails plugin).
* [qrcode-kotlin](https://github.com/g0dkar/qrcode-kotlin) - used to create QR codes (used by qrcode plugin).
* [common-mark](https://commonmark.org/) - used by the markdown plugin.
* [Fontspace](https://www.fontspace.com/category/open-source) - Open source fonts.
* [Maven Central](https://central.sonatype.com/search?q=io.pdf4k) - pdf4k artefacts on Maven Central.
* [jReleaser](https://jreleaser.org/guide/latest/tools/jreleaser-gradle.html) - gradle plugin used to release to Maven Central.

## TODO

[TODO List](./TODO.md)

## Authors

- [Bret Calvey](mailto:bacalv@gmail.com) - original author.
- Please contribute :)

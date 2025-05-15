# pdf4k.io plugins

Supported core plugins

* [markdown](./markdown/README.md) - Markdown DSL plugin
* [qrcode](./qrcode/README.md) - Render QR codes
* [thumbnails](./thumbnails/README.md) - Render reduced quality images

Each folder under this directory is either:

* A plugin that only extends the DSL (e.g. the markdown plugin)
* A plugin that not only contains DSL components, but server-side components too
  * These plugins typically would have their own `domain` subproject
  * They have a DSL subproject to allow the plugin to be used in an idiomatic way
  * They have s `server` subproject that contains components to run in a pdf4k server instance
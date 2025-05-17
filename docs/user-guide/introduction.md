# Introduction

pdf4k is a free, open source library written in Kotlin that can be used to generate arbitrary PDF documents
using existing PDF templates.

It defines a language (DSL) that can be used to compose PDF files containing paginated, variable-length content.

It also defines a framework to test your generated PDFs match an approved version.

It can be used to build microservices that can be used to generate PDF files on demand or asynchronously.

## Why pdf4k?

Several times in my career I've had to generate PDF files. The most notable project that I worked on to do this was when
I led a team that was tasks with developing an auditing system that captured hundreds of data points and then generated
a PDF report of the audit. It contained variable-length text and allowed auditors to upload images that they took during
the audit.

Some of these audits ended up containing over 100 pages of information and dozens of images.

We used the excellent iText library to do this.

However, it wasn't very Kotlin-ey, so I spent quite a lot of my spare time putting together a DSL that allowed the team
to generate these audit reports and a microservice that did the heavy lifting.

The system worked rather well but due to tight deadlines, I never managed to develop it to it's full potential.

Once the project was delivered successfully and the client was pleased with what was delivered, my contract was ended.

The code stayed with them.

After this, I decided to create a better version of that project in my spare time from scratch and give it away for free.

## pdf4k Servers

A dockerized server that allows immediate rendering of PDFs is currently under development.

Because generating large PDFs with lots of images can be computationally expensive, you might not want to embed the
actual PDF generation in your services (although this is probably OK for small PDFs). Therefore, it is recommended that
the actual PDF generation be done in a separate microservice.

The server components will be designed so that you can generate a PDF immediately if desired, but it will also allow
you to generate PDFs asynchronously in the background so that you don't end up effecting the performance of your overall
system. They will also be designed to scale so you can run multiple instances of the server components for resilience
purposes and to meet the workload of your application.

Clients that use these services will also have the advantages of using minimal dependencies as they will not actually be
doing the heavy-lifting of generating PDFs themselves.

## Never buy version 1 of anything, help build it!

There is still much work to do, but as of version 0.0.1, the DSL and renderer should be stable enough for you to use in 
your own projects.

If you would like to contribute to pdf4k, please get in touch with me at [bacalv@gmail.com](mailto:bacalv@gmail.com).

[Back to index](./README.md)
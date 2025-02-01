package io.pdf4k.provider

import io.pdf4k.domain.KeyName
import io.pdf4k.domain.PdfOutcome
import io.pdf4k.domain.failure
import io.pdf4k.domain.success
import io.pdf4k.renderer.PdfError.KeyParseError
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

interface KeyProvider {
    data class Key(val privateKey: PrivateKey, val certificateChain: Collection<Certificate>)

    fun lookup(keyName: KeyName): Key

    companion object {
        fun toPrivateKey(pem: String): PdfOutcome<PrivateKey> =
            pem.extractCertificate().flatMap { cert ->
                runCatching {
                    KeyFactory.getInstance("RSA").generatePrivate(
                        PKCS8EncodedKeySpec(
                            Base64.getMimeDecoder().decode(cert.toByteArray())
                        )
                    )
                }.map { success(it) }.getOrElse { failure(KeyParseError) }
            }

        fun toCertificateChain(pem: String): Collection<Certificate> {
            return CertificateFactory.getInstance("X.509").generateCertificates(pem.byteInputStream())
        }

        private fun String.extractCertificate(): PdfOutcome<String> =
            split("\n").let { lines ->
                when {
                    lines.size < 2 -> failure(KeyParseError)
                    lines[0] != "-----BEGIN PRIVATE KEY-----" -> failure(KeyParseError)
                    lines[lines.size - 1] != "-----END PRIVATE KEY-----" -> failure(KeyParseError)
                    else -> success(lines.subList(1, lines.size - 1).joinToString("\n"))
                }
            }
    }
}

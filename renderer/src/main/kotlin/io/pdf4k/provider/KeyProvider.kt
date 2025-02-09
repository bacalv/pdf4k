package io.pdf4k.provider

import io.pdf4k.domain.KeyName
import io.pdf4k.domain.PdfError.KeyParseError
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
        fun toPrivateKey(pem: String): PrivateKey =
            pem.extractCertificate().let { cert ->
                runCatching {
                    KeyFactory.getInstance("RSA").generatePrivate(
                        PKCS8EncodedKeySpec(
                            Base64.getMimeDecoder().decode(cert.toByteArray())
                        )
                    )
                }.getOrElse { throw KeyParseError() }
            }

        fun toCertificateChain(pem: String): Collection<Certificate> {
            return CertificateFactory.getInstance("X.509").generateCertificates(pem.byteInputStream())
        }

        private fun String.extractCertificate(): String =
            split("\n").let { lines ->
                when {
                    lines.size < 2 -> throw KeyParseError()
                    lines[0] != "-----BEGIN PRIVATE KEY-----" -> throw KeyParseError()
                    lines[lines.size - 1] != "-----END PRIVATE KEY-----" -> throw KeyParseError()
                    else -> lines.subList(1, lines.size - 1).joinToString("\n")
                }
            }
    }
}

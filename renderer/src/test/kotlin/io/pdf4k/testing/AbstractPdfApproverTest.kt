package io.pdf4k.testing

import io.pdf4k.domain.KeyName
import io.pdf4k.domain.Pdf
import io.pdf4k.renderer.KeyProvider
import io.pdf4k.renderer.PdfRenderer.render
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@ExtendWith(PdfApproverExtension::class)
abstract class AbstractPdfApproverTest {

    companion object {
        private val unwrapRegex = Regex(
            "-----BEGIN PRIVATE KEY-----\\s+(.*)\\s+-----END PRIVATE KEY-----",
            RegexOption.DOT_MATCHES_ALL
        )
        private val privateKey = AbstractPdfApproverTest::class.java.getResource("/certs/private-key.pem")
            ?.readText()?.let(::toPrivateKey) ?: fail("Could not load default private key")
        private val certificateChain = AbstractPdfApproverTest::class.java.getResource("/certs/cert.pem")
            ?.readText()?.let(::toCertificateChain)?.toList() ?: fail("Could not load default cert chain")

        val defaultKeyName = KeyName("default")
        val keys = mutableMapOf(defaultKeyName to KeyProvider.Keys(privateKey, certificateChain))

        fun Pdf.approve(approver: PdfApprover) = approver.assertApproved(ByteArrayOutputStream().also { stream ->
            stream.use {
                render(it, object : KeyProvider {
                    override fun lookup(keyName: KeyName) = keys[keyName] ?: fail("Key not found: $keyName")
                })
            }
        }.toByteArray())

        fun toPrivateKey(pem: String): PrivateKey {
            val privateKeyBase64 = unwrapRegex.find(pem)?.groups?.get(1)?.value
                ?: throw IllegalStateException("No private key found in pem")
            val privateKeyBytes = Base64.getMimeDecoder().decode(privateKeyBase64.toByteArray())
            return KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes))
        }

        fun toCertificateChain(pem: String): Collection<Certificate> {
            return CertificateFactory.getInstance("X.509").generateCertificates(pem.byteInputStream())
        }
    }
}
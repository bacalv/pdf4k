package io.pdf4k.renderer

import io.pdf4k.domain.KeyName
import java.security.PrivateKey
import java.security.cert.Certificate

interface KeyProvider {
    data class Keys(val privateKey: PrivateKey, val certificateChain: Collection<Certificate>)

    fun lookup(keyName: KeyName): Keys
}
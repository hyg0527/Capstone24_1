package com.credential.cubrism.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.credential.cubrism.BuildConfig
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

object KeystoreEncryptor {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = BuildConfig.KEYSTORE_ENCRYPTOR_KEY_ALIAS
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SEPARATOR = "]"

    init {
        generateKey()
    }

    // 암호화 키를 생성하고 Keystore에 저장
    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        keyGenerator.generateKey()
    }

    // 생성된 키를 사용하여 문자열을 암호화
    fun encrypt(input: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encryption = cipher.doFinal(input.toByteArray(Charset.forName("UTF-8")))
        return "${android.util.Base64.encodeToString(iv, android.util.Base64.DEFAULT)}$IV_SEPARATOR${android.util.Base64.encodeToString(encryption, android.util.Base64.DEFAULT)}"
    }

    // 생성된 키를 사용하여 문자열을 복호화
    fun decrypt(input: String): String {
        val fields = input.split(IV_SEPARATOR.toRegex()).toTypedArray()
        val iv = android.util.Base64.decode(fields[0], android.util.Base64.DEFAULT)
        val cipherText = android.util.Base64.decode(fields[1], android.util.Base64.DEFAULT)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(cipherText), Charset.forName("UTF-8"))
    }

    // Keystore에서 키를 가져옴
    private fun getSecretKey(): Key {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null)
    }
}
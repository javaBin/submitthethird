package no.java.submit

import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


object CryptoUtils {

    private val secretKey:SecretKeySpec

    init {
        val password = Setup.serverEncryptPassword()
        // The salt (probably) can be stored along with the encrypted data
        val salt = "12345678".toByteArray()

        // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
        val iterationCount = 40000
        // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
        val keyLength = 128

        secretKey = createSecretKey(password.toCharArray(),
                salt, iterationCount, keyLength)
    }


    fun encrypt(value:String):String {
        return encrypt(value, secretKey)
    }

    fun decrypt(encryptedValue:String):String {
        return decrypt(encryptedValue, secretKey);
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun createSecretKey(password: CharArray, salt: ByteArray, iterationCount: Int, keyLength: Int): SecretKeySpec {
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
        val keySpec = PBEKeySpec(password, salt, iterationCount, keyLength)
        val keyTmp = keyFactory.generateSecret(keySpec)
        return SecretKeySpec(keyTmp.encoded, "AES")
    }

    @Throws(GeneralSecurityException::class, UnsupportedEncodingException::class)
    private fun encrypt(property: String, key: SecretKeySpec): String {
        val pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        pbeCipher.init(Cipher.ENCRYPT_MODE, key)
        val parameters = pbeCipher.parameters
        val ivParameterSpec = parameters.getParameterSpec(IvParameterSpec::class.java)
        val cryptoText = pbeCipher.doFinal(property.toByteArray(charset("UTF-8")))
        val iv = ivParameterSpec.iv
        return base64Encode(iv) + ":" + base64Encode(cryptoText)
    }

    private fun base64Encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    private fun decrypt(string: String, key: SecretKeySpec): String {
        val iv = string.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val property = string.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        pbeCipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(base64Decode(iv)))
        return String(pbeCipher.doFinal(base64Decode(property)), Charset.forName("UTF-8"))
    }

    @Throws(IOException::class)
    private fun base64Decode(property: String): ByteArray {
        return Base64.getDecoder().decode(property)
    }
}
package bob.e2e.controller

import org.springframework.web.bind.annotation.*
import java.security.MessageDigest
import java.util.Base64

@RestController
@RequestMapping("/api")
class HashController {

    @PostMapping("/hash")
    fun getHash(@RequestBody request: HashRequest): Map<String, String> {
        val hash = generateHash(request.value)
        return mapOf("hash" to hash)
    }

    private fun generateHash(value: String): String {
        val digest = MessageDigest.getInstance("RSA")
        val hashBytes = digest.digest(value.toByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}

data class HashRequest(val value: String)

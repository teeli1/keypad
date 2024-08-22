package bob.e2e.controller

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.MessageDigest
import java.time.Instant
import java.util.*
import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Autowired

@Controller
class KeypadController {

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    private val buttons = listOf("_0.png", "_1.png", "_2.png", "_3.png", "_4.png", "_5.png", "_6.png", "_7.png", "_8.png", "_9.png", "_blank.png", "_blank.png")

    @GetMapping("/")
    fun showKeypad(model: Model, session: HttpSession): String {
        val shuffledButtons = buttons.shuffled()

        // Generate hash values for each button
        val buttonHashes = shuffledButtons.associateWith { generateHash(it) }

        // Store the hash values in the session
        session.setAttribute("buttonHashes", buttonHashes)

        // Pass the buttons and hashes to the view
        model.addAttribute("buttons", shuffledButtons)
        model.addAttribute("buttonHashes", buttonHashes)
        return "keypad"
    }

    @PostMapping("/submit")
    fun submitKeypad(
        @RequestParam("userId") userId: String,
        @RequestParam("userInput") userInput: String,
        session: HttpSession,
        model: Model
    ): String {
        // Retrieve the hash values from the session
        @Suppress("UNCHECKED_CAST")
        val buttonHashes = session.getAttribute("buttonHashes") as? Map<String, String>

        if (buttonHashes == null) {
            model.addAttribute("errorMessage", "Session expired or invalid.")
            return "redirect:/"
        }

        // Generate hash values for the user input based on the shuffled buttons
        val inputHashes = userInput.map { digit ->
            val hash = buttonHashes["_$digit.png"] ?: "No hash found"
            hash
        }

        // Combine input hashes into a single string
        val combinedHashes = inputHashes.joinToString("")

        // Generate a timestamp-based hash for the combined input
        val timestamp = Instant.now().epochSecond.toString()
        val timestampedHash = generateHash(combinedHashes + timestamp)

        // Encode the timestamped hash with Base64
        val encodedValue = Base64.getEncoder().encodeToString(timestampedHash.toByteArray(Charsets.UTF_8))

        // Store the encoded value in Redis with a key
        val redisKey = "user:$userId:$timestamp"
        redisTemplate.opsForValue().set(redisKey, encodedValue)

        // Pass the encoded value and hash to the model
        model.addAttribute("encodedValue", encodedValue)
        model.addAttribute("inputHashes", inputHashes)
        model.addAttribute("timestamp", timestamp)

        return "keypad" // This will render a result page to show the output
    }

    private fun generateHash(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(value.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

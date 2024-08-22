package bob.e2e.model

data class Keypad(
    val keys: List<Key> = generateRandomKeys()
) {
    companion object {
        private const val KEYPAD_LAYOUT = "0123456789"

        // 이미지 경로 리스트
        private val imagePaths = mapOf(
            "0" to "/_0.png",
            "1" to "/_1.png",
            "2" to "/_2.png",
            "3" to "/_3.png",
            "4" to "/_4.png",
            "5" to "/_5.png",
            "6" to "/_6.png",
            "7" to "/_7.png",
            "8" to "/_8.png",
            "9" to "/_9.png"
        )

        fun generateRandomKeys(): List<Key> {
            val shuffledKeys = KEYPAD_LAYOUT.toList().shuffled() // 키 배열을 랜덤하게 섞음
            return shuffledKeys.map { Key(it.toString(), imagePaths[it.toString()] ?: "/_blank.png") }
        }
    }

    data class Key(val number: String, val image: String)
}

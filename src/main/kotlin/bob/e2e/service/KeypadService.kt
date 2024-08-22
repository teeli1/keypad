package bob.e2e.service

import bob.e2e.model.Keypad
import org.springframework.stereotype.Service

@Service
class KeypadService {

    // getKeypad 메서드 정의
    fun getKeypad(): Keypad {
        return Keypad() // Keypad 객체 생성
    }
}

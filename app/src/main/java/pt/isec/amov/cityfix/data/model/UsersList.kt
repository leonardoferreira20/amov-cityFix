package pt.isec.amov.cityfix.data.model

import java.text.Collator
import java.util.Locale

class UsersList(initialUsers: List<User> = emptyList()) {
    private val _users = initialUsers.toMutableList()

    fun getPendingCitizensSorted(): List<User> {
        val collator = Collator.getInstance(Locale.getDefault()).apply {
            strength = Collator.PRIMARY
        }

        return _users
            .filter { it.role == UserRole.CITIZEN && it.status == AccountStatus.PENDING }
            .sortedWith { u1, u2 -> collator.compare(u1.name, u2.name) }
    }

    fun findById(id: String): User? = _users.find { it.id == id }

    fun hasNif(nif: String, role: UserRole): Boolean = _users.any { it.nif == nif && it.role == role }

    fun rawList(): List<User> = _users.toList()

    fun upsert(user: User) {
        val index = _users.indexOfFirst { it.id == user.id }

        if (index >= 0) {
            _users[index] = user
        } else {
            _users.add(user)
        }
    }
}

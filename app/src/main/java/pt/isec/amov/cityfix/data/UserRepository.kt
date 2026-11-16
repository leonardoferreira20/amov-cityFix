package pt.isec.amov.cityfix.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.data.model.UsersList

class UserRepository {
    private val collection = Firebase.firestore.collection("users")

    suspend fun loadUsers(): UsersList = try {
        UsersList(collection.get().await().toObjects(User::class.java))
    } catch (_: Exception) {
        UsersList()
    }

    suspend fun loadUser(id: String): User? = try {
        collection.document(id).get().await().toObject(User::class.java)
    } catch (_: Exception) {
        null
    }

    suspend fun hasNif(nif: String, role: UserRole): Boolean =
        !collection
            .whereEqualTo("nif", nif)
            .whereEqualTo("role", role.name)
            .get()
            .await()
            .isEmpty

    suspend fun createUser(user: User) {
        collection.document(user.id).set(user).await()
    }

    fun saveUser(user: User) {
        collection.document(user.id).set(user)
    }
}

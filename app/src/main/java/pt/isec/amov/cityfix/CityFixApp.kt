package pt.isec.amov.cityfix

import android.app.Application
import pt.isec.amov.cityfix.data.CategoryRepository
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.UserRepository

class CityFixApp : Application() {
    val incidentRepository by lazy { IncidentRepository(this) }
    val categoryRepository by lazy { CategoryRepository(this) }
    val userRepository by lazy { UserRepository(this) }
}

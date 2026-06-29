package eden.movieq

import com.github.ajalt.clikt.core.CliktCommand
import eden.movieq.services.SqliteStorageService

class MigrateCommand: CliktCommand("migrate") {
    override fun run() {
        val db = SqliteStorageService()
        db.migrate()
    }
}

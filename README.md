# MovieQ
Web app to track movies you want to watch, with automatic lookup of various details from The Movie DB and score from 
RottenTomatoes.

# Compile and run from source
```
./gradlew :run
```

Then open a browser to `http://localhost:8080/`. It runs off an SQLite database stored at `movieq.db` next to this 
README file. This and other settings can be configured in MovieQApp.kt, in the companion object.
# Ktorx Server Arch


## Configuration

application.conf:
```hocon
ktor{
  
  config{
    path = "/path/to/config/folder"
    modules = [
      "module1",
      "module2"
    ]
    imports = [
      
    ]
  }
  
}
```
Create folder ```/path/to/config/folder``` on the server
In this folder create a folder ```module```, for each module create a folder with name equal to  the module name
```
/path/to/config/folder
    |- module
        |- module1
        |- module1
```
### Boundary
Create

Place kts-files describing the boundary configuration under the folder
```
    moduleX/boundary
```
or any subfolder, e.g.
```
    moduleX/boundary
        |-apis
        |-databases
        |-translations
        |-any other dir 
```
Example: 
```
/path/to/config/folder
    - module
        |- module1/boundary
            |-apis.kts
            |-databaese.kts
        - module1/boundary
            |-apis.kts
            |-translations.kts
```

Examples of config files
```kotlin
apis {
    physical("chat/api") {
        name = "chat/api"
        scheme = "http"
        host = "chat_api"
        port = 8081
        requests {
            post(
                name = "add-member-to-chat-group",
                route = "/chat-groups/add-member"
            )
        }
    }
}
```


```kotlin
databases{
    database("users") {
        type = DatabaseType.MySql
        name = "users_db"
        host = "users_db"
        port = 3306
        scheme = "jdbc:mysql"
        params = "?useSSL=false"
        driver = "com.mysql.jdbc.Driver"
        user = "root"
        password = "password"
    }
}
```
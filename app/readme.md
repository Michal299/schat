# Description

Chat application created for Security of Computer Systems lectures on GUT.

# Build
In order to build an application from source please use command
```
mvn clean package
```
This command will create two `jar` files in `target` directory. The one that name
contains `with-dependencies` phrase is standalone version of `jar` file that includes
all required dependencies used in the project.

# Run
To run app you can use
```
java -jar ./target/<name_of_jar_with_dependencies>
```

# Author
Michał Błajet, 180564\
Gdansk University Of Technology\
2022
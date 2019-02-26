Service responsible for managing user data, including registration, login and authentication.

1. Build the app:
* if you have docker engine: mvn clean install
* if not: mvn clean install -DskipDocker

2. Run the app:
* mvn spring-boot:run -Dspring.profiles.active=dev
(the dev profile uses an in-memory database. If you have postgreSQL db running locally on the default port you might decide to leave out this profile flag)


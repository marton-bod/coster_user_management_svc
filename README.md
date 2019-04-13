## Coster.io - User management service

Microservice responsible for managing user data, including registration, login and authentication.
Developed in spring-boot.

### Build the app:
* Prerequisites: Maven, JDK11
* `mvn clean install -Pdocker` - if you have docker engine
* `mvn clean install` - if not
    
### REST Interface:
- Swagger UI: localhost:9001/swagger-ui.html

### Actuator endpoints:
- Health: localhost:9001/actuator/health
- Beans: localhost:9001/actuator/beans
- Status: localhost:9001/actuator/status

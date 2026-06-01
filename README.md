# Small_Application_Homework k12133704

# How to Run

# Start local Neo4j Instance
Database Connection URL: bolt://localhost:7687
Authentication Username: neo4j
Authentication Password: our_password_v1

# Run the Spring Boot Application
Open the project in your IDE and execute the main method inside:
src/main/java/com/DanielRocky/small_application/SmallApplication.java

# Open web browser and naviagte to:
http://localhost:8081

Note: You can modify these deployment parameters inside:
src/main/resources/application.properties

#Application Architecture
Model Layer (`.model`): Holds the Java domain entity schemas (`Student.java`, `Lecture.java`) mapped directly onto Neo4j graph nodes using Spring Data Neo4j (SDN) annotations.
Repository Layer (`.repository`): Contains data access components interface extensions (`StudentRepository.java`, `LectureRepository.java`) driving graph execution using native Cypher `@Query` annotations.
Control Layer (`.controller`): The `WebController.java` maps user interface request forms parameters, invokes the respective repository routines, and handles routing objects back onto web template views.
UserInterface Layer (`templates/index.html`): Minimalistic Thymeleaf configurations to display responsive HTML views.


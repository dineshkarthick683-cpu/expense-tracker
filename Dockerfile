FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=$PORT -jar target/*.jar"]

#Render Deployment Flow

#1. Create Dockerfile in the application root directory.

#2. Dockerfile contains instructions such as:
#   - Java 21 Base Image
#   - Copy Application Source Code
#  - Build JAR File
#   - Dependencies
#   - Linux Environment
#
#3. Render reads the Dockerfile and creates a Docker Image.
#   Docker Image = Packaged Application (App Installation Package)

#  Contains:
#   - Java 21
#   - Spring Boot JAR
#   - Maven Dependencies
#   - Linux Runtime Environment

#4. Render starts a Docker Container from the Docker Image.
#   Docker Container = Running Application

#5. Spring Boot starts inside the Docker Container.

#6. Spring Boot reads application.properties/application.yml.

#7. Datasource configuration is loaded:
#   - Database URL
#   - Username
#   - Password

#8. Spring Boot connects to PostgreSQL Database.

#-------------------------------------------------

#PostgreSQL Setup in Render

#Render Dashboard
#    ↓
#New
#    ↓
#PostgreSQL
#    ↓
#Create Database

#Render automatically generates:

#- Host
#- Port
#- Database Name
#- Username
#- Password

#Example:

#spring.datasource.url=jdbc:postgresql://HOST:5432/DATABASE?sslmode=require
#spring.datasource.username=USERNAME
#spring.datasource.password=PASSWORD

#These values are copied from Render and configured in
#application.properties or Render Environment Variables.

#-------------------------------------------------

#Complete Flow

#Developer
#    ↓
#GitHub
#    ↓
#Render
#    ↓
#Reads Dockerfile
#    ↓
#Builds Docker Image
#    ↓
#Creates Docker Container
#    ↓
#Starts Spring Boot Application
#    ↓
#Reads application.properties
#    ↓
#Connects PostgreSQL
#    ↓
#Application Available at Render URL -->After successful deployment, Render gives you a public URL.
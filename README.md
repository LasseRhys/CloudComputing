# CloudComputing

## Projektbeschreibung

Dieses Repository iat ein Fork von dem Projekt EventTom, welches im Rahmen des Kurses "Softwarearchitektur und Qualitätssicherung" im Wintersemester 2024/2025 entwickelt wurde.



 Modellschema
![grafik](https://github.com/user-attachments/assets/8064acf3-c6d3-47a9-9bba-2a307e7e0642)


Cloud Infrastruktur
![Brainboard - Brainboard](https://github.com/user-attachments/assets/af51b0c6-2459-43c9-9de0-4029a001fa83)


## Technologien

- **Backend:** Spring Boot
- **Frontend:** Vue.js
- **Datenbank:** MySQL
- **Authentifizierung:** JWT (JSON Web Tokens)
- **State Management:** Vuex
- **API-Kommunikation:** REST-API
- **Echtzeit-Kommunikation:** WebSockets

  

git clone https://github.com/LasseRhys/CloudComputing.git
cd CloudComputing

2. Backend (Spring Boot) Setup
2.1 MySQL Datenbank

Erstelle eine neue MySQL-Datenbank für das Projekt:

CREATE DATABASE eventtom;

2.2 Konfiguration der Anwendung

Ändere die MySQL-Verbindungsdaten in der application.properties Datei unter src/main/resources:

spring.datasource.url=jdbc:mysql://localhost:3306/eventtom
spring.datasource.username=root
spring.datasource.password=dein_passwort
spring.jpa.hibernate.ddl-auto=update

2.3 Backend starten

Wechsle in das Backend-Verzeichnis und baue das Projekt mit Maven:

cd backend
mvn clean install
mvn spring-boot:run

Das Backend läuft nun auf http://localhost:8080.
3. Frontend (Vue.js) Setup
3.1 Abhängigkeiten installieren

Wechsle in das Frontend-Verzeichnis und installiere alle benötigten npm-Pakete:

cd frontend
npm install

3.2 Frontend starten

Starte das Frontend:

npm run serve

Das Frontend läuft nun auf http://localhost:8081.


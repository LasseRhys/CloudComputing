# CloudComputing

## Projektbeschreibung

Dieses Repository iat ein Fork von dem Projekt EventTom, welches im Rahmen des Kurses "Softwarearchitektur und Qualitätssicherung" im Wintersemester 2024/2025 entwickelt wurde.

Das Projekt beinhaltet eine Webseite des Zweckes für das managen von Events, die es Nutzern ermöglicht, Tickets für alle gegebenen Veranstaltungen zu erwerben sowie die Anwendung von Rabatten mittels Coupons. 

 Modellschema
![grafik](https://github.com/user-attachments/assets/8064acf3-c6d3-47a9-9bba-2a307e7e0642)


## Cloud Infrastruktur
- VPC mit öffentlichen und privaten Subnetzen
- Internet Gateway und NAT Gateway
- EC2-Instanz für die Anwendung
- RDS (PostgreSQL)
- Application Load Balancer (ALB)
- AWS Secrets Manager für das DB-Passwort



## Technologien

- **Backend:** Spring Boot
- **Frontend:** Vue.js
- **Datenbank:** Postgres
- **Authentifizierung:** JWT (JSON Web Tokens)
- **State Management:** Vuex
- **API-Kommunikation:** REST-API
- **Echtzeit-Kommunikation:** WebSockets

  
```bash
git clone https://github.com/LasseRhys/CloudComputing.git
cd CloudComputing
```




Konfiguration der Anwendung sofern man nicht die Docker-Container benutzen will

Ändere die postgres-Verbindungsdaten in der application.properties Datei unter src/main/resources:
```bash
spring.datasource.url=jdbc:postgres://localhost:5432/postgres
spring.datasource.username=root
spring.datasource.password=dein_passwort
spring.jpa.hibernate.ddl-auto=update
```
Backend starten

Wechsle in das Backend-Verzeichnis und baue das Projekt mit Maven:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Das Backend läuft nun auf http://localhost:8080.

Frontend (Vue.js) Setup
Abhängigkeiten installieren

Wechsle in das Frontend-Verzeichnis und installiere alle benötigten npm-Pakete:
```bash
cd frontend
npm install
```
Frontend starten

Starte das Frontend:
```bash
npm run serve
```

Das Frontend läuft nun auf http://localhost:8081.


# Lokal Ausführen
um das Projekt lokal ausführne zu wollen, muss man folgende 3 Container starten:
dabei ist wichtig, dass das Frontend und Backend Container jeweils SO heißen sowie in einem gemeinsamen Netzwerk vorhanden sind

```bash
 docker run --name postgres-db \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_DB=mydatabase \
  -d postgres
 docker network create mynet
              docker run -d -p 8080:8080 --name backend --network mynet --restart unless-stopped -e DATABASE_URL=jdbc:postgresql://localhost:5432/postgres  -e DATABASE_USERNAME=myuser -e DATABASE_PASSWORD=mysecretpassword rhysling/guenther4587:latest
              docker run -d -p 80:80 --name frontend --network mynet --restart unless-stopped rhysling/hartmut1029:latest

```



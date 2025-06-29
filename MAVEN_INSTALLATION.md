# Maven Installatie Gids voor Windows

Deze gids helpt je stap voor stap bij het installeren van Apache Maven op Windows, zodat je lokaal je TeleportPlugin kunt builden.

## Wat is Maven?

Apache Maven is een build automation tool die voornamelijk wordt gebruikt voor Java projecten. Het beheert dependencies, compileert code, en pakt alles in een JAR bestand.

## Vereisten

- **Java Development Kit (JDK) 17 of hoger** - Maven heeft Java nodig om te werken
- **Windows Command Prompt of PowerShell** - Voor het uitvoeren van Maven commando's

## Stap 1: Controleer of Java geïnstalleerd is

Open Command Prompt (`cmd`) en voer uit:
```cmd
java -version
javac -version
```

Als je een foutmelding krijgt of Java niet gevonden wordt, installeer dan eerst Java:

### Java 17 Installeren (indien nodig)
1. Ga naar [Eclipse Temurin](https://adoptium.net/temurin/releases/)
2. Download **JDK 17** voor Windows (x64)
3. Installeer het met de installer
4. Controleer opnieuw met `java -version`

## Stap 2: Maven Downloaden

1. Ga naar de [Apache Maven website](https://maven.apache.org/download.cgi)
2. Download de **Binary zip archive** (bijvoorbeeld `apache-maven-3.9.6-bin.zip`)
3. Extract de zip naar een permanente locatie, bijvoorbeeld:
   ```
   C:\Program Files\Apache\maven\
   ```

## Stap 3: Omgevingsvariabelen Instellen

### Optie A: Via System Properties (Aanbevolen)
1. Klik met rechtermuisknop op **This PC** → **Properties**
2. Klik op **Advanced system settings**
3. Klik op **Environment Variables**
4. Onder **System Variables**, klik **New**:
   - **Variable name**: `MAVEN_HOME`
   - **Variable value**: `D:\apache-maven-3.9.10-bin\apache-maven-3.9.10`
5. Zoek de **Path** variabele en klik **Edit**
6. Klik **New** en voeg toe: `%MAVEN_HOME%\bin`
7. Klik **OK** op alle dialogs

### Optie B: Via Command Prompt (Tijdelijk)
```cmd
set MAVEN_HOME=D:\apache-maven-3.9.10-bin\apache-maven-3.9.10
set PATH=%PATH%;%MAVEN_HOME%\bin
```

## Stap 4: Maven Installatie Verifiëren

1. **Herstart Command Prompt** (belangrijk na het instellen van omgevingsvariabelen)
2. Test Maven:
   ```cmd
   mvn -version
   ```

Je zou iets zoals dit moeten zien:
```
Apache Maven 3.9.6
Maven home: C:\Program Files\Apache\maven\apache-maven-3.9.6
Java version: 17.0.x, vendor: Eclipse Adoptium
```

## Stap 5: Je TeleportPlugin Builden

Nu kun je je plugin lokaal builden:

### Optie A: Met Maven in PATH (na environment variables setup)
```cmd
cd "d:\New WinRAR ZIP archive\mcplugin-teleport"
mvn clean package
```

### Optie B: Direct pad naar Maven (als environment variables niet werken)
```cmd
cd "d:\New WinRAR ZIP archive\mcplugin-teleport"
D:\apache-maven-3.9.10-bin\apache-maven-3.9.10\bin\mvn clean package
```

### Optie C: Tijdelijke environment variables (met jouw Java pad)
```cmd
set MAVEN_HOME=D:\apache-maven-3.9.10-bin\apache-maven-3.9.10
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot
set PATH=%PATH%;%MAVEN_HOME%\bin
cd /d "d:\New WinRAR ZIP archive\mcplugin-teleport"
mvn clean package
```

### Optie D: Alles in één commando (aanbevolen)
```cmd
set MAVEN_HOME=D:\apache-maven-3.9.10-bin\apache-maven-3.9.10 && set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot && cd /d "d:\New WinRAR ZIP archive\mcplugin-teleport" && "%MAVEN_HOME%\bin\mvn" clean package
```

Het gebouwde JAR bestand komt in de `target/` folder.

## Alternatieve Installatie Methoden

### Via Chocolatey (Voor gevorderde gebruikers)
Als je Chocolatey package manager hebt:
```cmd
choco install maven
```

### Via Scoop (Voor gevorderde gebruikers)
Als je Scoop package manager hebt:
```cmd
scoop install maven
```

## Troubleshooting

### "mvn is not recognized as an internal or external command"
- Controleer of `MAVEN_HOME` correct is ingesteld
- Controleer of `%MAVEN_HOME%\bin` in je PATH staat
- Herstart Command Prompt na het wijzigen van omgevingsvariabelen

### "JAVA_HOME environment variable is not set"
Maven kan problemen hebben met het vinden van Java. Los dit op:

**Stap 1: Zoek je Java installatie**
```cmd
where java
```

**Stap 2: Stel JAVA_HOME in (vervang het pad met jouw Java locatie)**
Typische locaties zijn:
- `C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot` (jouw installatie)
- `C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x\`
- `C:\Program Files\Java\jdk-17.x.x\`
- `C:\Program Files (x86)\Java\jdk-17.x.x\`

**Stap 3: Voeg JAVA_HOME toe als omgevingsvariabele:**
1. Open Environment Variables (zoals bij Maven)
2. Onder System Variables, klik "New":
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x` (jouw pad)
3. Klik OK

**Stap 4: Test opnieuw**
```cmd
echo %JAVA_HOME%
mvn -version
```

**Als Java niet geïnstalleerd is:**
1. Download JDK 17 van [Eclipse Temurin](https://adoptium.net/temurin/releases/)
2. Installeer het
3. Stel JAVA_HOME in naar de installatie locatie

### Maven kan dependencies niet downloaden
- Controleer je internetverbinding
- Maven download dependencies automatisch bij eerste gebruik
- Dependencies worden opgeslagen in `C:\Users\[username]\.m2\repository\`

## Nuttige Maven Commando's

```cmd
# Project builden
mvn clean package

# Dependencies downloaden
mvn dependency:resolve

# Project reinigen
mvn clean

# Tests uitvoeren
mvn test

# Versie informatie
mvn -version
```

## Volgende Stappen

Na installatie kun je:
1. Je TeleportPlugin lokaal builden met `mvn clean package`
2. Het gegenereerde JAR bestand vinden in `target/TeleportPlugin-1.0.jar`
3. Dit JAR bestand uploaden naar je Spigot server

Voor meer informatie, zie de andere documentatie bestanden in dit project.

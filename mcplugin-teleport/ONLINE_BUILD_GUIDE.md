# Online Build Services voor TeleportPlugin

## Optie 1: GitHub Actions (Aanbevolen)

### Stappen:
1. **GitHub Account**: Maak gratis account op github.com
2. **New Repository**: Klik "New" → "Repository"
3. **Upload Files**: Sleep alle plugin bestanden naar de repository
4. **Auto Build**: GitHub bouwt automatisch je plugin

### Voordelen:
✅ Volledig gratis
✅ Automatische builds
✅ Download JAR direct van GitHub
✅ Geen lokale software nodig

### Na upload:
- Ga naar "Actions" tab
- Wacht tot build klaar is (groen vinkje)
- Download JAR van "Artifacts" sectie

---

## Optie 2: GitLab CI

### Stappen:
1. **GitLab Account**: Maak account op gitlab.com
2. **New Project**: Upload je code
3. **Auto Build**: GitLab compileert automatisch

### Voordelen:
✅ Gratis
✅ 2000 minuten build tijd per maand
✅ Eenvoudige setup

---

## Optie 3: Online IDE's

### Replit.com
1. Ga naar replit.com
2. Import GitHub repository
3. Installeer Maven in terminal
4. Run: `mvn clean package`

### CodeSandbox.io
1. Ga naar codesandbox.io
2. Import project
3. Open terminal
4. Build project

### Gitpod.io
1. Ga naar gitpod.io
2. Open GitHub repo met Gitpod
3. Terminal: `mvn clean package`

---

## Optie 4: Build Services

### Jitpack.io
1. Upload naar GitHub
2. Ga naar jitpack.io
3. Voer je GitHub repo URL in
4. Download gebouwde JAR

### GitHub Codespaces
1. Activeer Codespaces op je GitHub repo
2. Open in browser IDE
3. Terminal: `mvn clean package`
4. Download JAR

---

## Snelste Methode: GitHub Actions

1. **Upload alles naar GitHub**
2. **Voeg `.github/workflows/build.yml` toe** (al aangemaakt)
3. **Push/upload bestanden**
4. **Wacht op groene build**
5. **Download JAR van Actions → Artifacts**

### Direct download link na build:
`https://github.com/JOUW_USERNAME/JOUW_REPO/actions`

---

## Alternatieven zonder account:

### Online Java Compilers:
- **JDoodle.com** (Java compiler)
- **OneCompiler.com** (Java)
- **Programiz.com** (Online Java IDE)

⚠️ **Let op**: Deze ondersteunen mogelijk geen Maven projects.

---

## Makkelijkste optie: Upload naar GitHub

GitHub Actions is verreweg het makkelijkst:
1. Alle bestanden uploaden
2. Wachten op automatische build
3. JAR downloaden
4. Klaar voor server installatie!

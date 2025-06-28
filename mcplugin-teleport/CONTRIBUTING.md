# Contributing to TeleportPlugin

Bedankt voor je interesse in het bijdragen aan TeleportPlugin! 

## 🚀 Hoe bij te dragen

### 1. Fork het project
- Klik op "Fork" in de GitHub interface
- Clone je fork lokaal

### 2. Setup development environment
```bash
git clone https://github.com/p-astro16/serverplugins.git
cd serverplugins
```

### 3. Maak een feature branch
```bash
git checkout -b feature/mijn-nieuwe-feature
```

### 4. Maak je wijzigingen
- Volg de bestaande code style
- Voeg comments toe waar nodig
- Test je wijzigingen

### 5. Test je code
```bash
mvn clean package
```

### 6. Commit je wijzigingen
```bash
git add .
git commit -m "Add: beschrijving van je feature"
```

### 7. Push naar je fork
```bash
git push origin feature/mijn-nieuwe-feature
```

### 8. Maak een Pull Request
- Ga naar GitHub
- Klik "New Pull Request"
- Beschrijf je wijzigingen

## 📋 Code Style Guidelines

### Java Code Style
- **Indentation**: 4 spaces
- **Braces**: Opening brace op dezelfde lijn
- **Naming**: camelCase voor variabelen en methoden
- **Comments**: Javadoc voor publieke methoden

```java
public class ExampleClass {
    private String exampleVariable;
    
    /**
     * Example method with proper documentation
     * @param parameter Description of parameter
     * @return Description of return value
     */
    public String exampleMethod(String parameter) {
        if (parameter != null) {
            return parameter.toLowerCase();
        }
        return "default";
    }
}
```

### Commit Message Guidelines
- **Format**: `Type: Description`
- **Types**: 
  - `Add:` nieuwe feature
  - `Fix:` bug fix
  - `Update:` wijziging aan bestaande feature
  - `Remove:` verwijdering van code
  - `Docs:` documentatie wijzigingen

### Examples:
```
Add: trade confirmation dialog
Fix: item duplication bug in trading
Update: improve trade UI responsiveness
Remove: deprecated spawn commands
Docs: update installation instructions
```

## 🐛 Bug Reports

### Voordat je een bug report
1. **Zoek** eerst in bestaande issues
2. **Test** met de nieuwste versie
3. **Reproduceer** de bug consistent

### Bug Report Template
```
**Beschrijving**
Korte beschrijving van het probleem

**Reproductie Stappen**
1. Ga naar...
2. Klik op...
3. Zie fout...

**Verwacht Gedrag**
Wat er zou moeten gebeuren

**Screenshots**
Als van toepassing

**Environment**
- Minecraft versie:
- Server software:
- Plugin versie:
- Java versie:
```

## 💡 Feature Requests

### Feature Request Template
```
**Is your feature request related to a problem?**
Beschrijf het probleem

**Describe the solution you'd like**
Beschrijf de gewenste oplossing

**Describe alternatives you've considered**
Alternatieven die je hebt overwogen

**Additional context**
Extra context of screenshots
```

## 🧪 Testing

### Voor nieuwe features:
1. Test in single-player
2. Test met meerdere spelers
3. Test edge cases
4. Test met verschillende inventory states

### Test Checklist:
- [ ] Compilatie succesvol
- [ ] Geen console errors
- [ ] Feature werkt zoals verwacht
- [ ] Geen conflicts met bestaande features
- [ ] Performance impact minimaal

## 📚 Development Setup

### Vereisten:
- Java 17+
- Maven 3.6+
- IDE (IntelliJ IDEA aanbevolen)
- Paper/Spigot test server

### Project importeren in IntelliJ:
1. File → Open
2. Selecteer de project folder
3. Import as Maven project
4. Wacht tot dependencies geladen zijn

### Build en test:
```bash
# Build plugin
mvn clean package

# Run met test server
java -jar paper-1.21.jar
```

## 🤝 Code Review Process

1. **Automated checks** moeten slagen (GitHub Actions)
2. **Manual review** door maintainers
3. **Testing** door community (indien mogelijk)
4. **Merge** na approval

## 🏷️ Versioning

We gebruiken [Semantic Versioning](https://semver.org/):
- **MAJOR**: Breaking changes
- **MINOR**: Nieuwe features (backwards compatible)
- **PATCH**: Bug fixes

## 📞 Vragen?

- **GitHub Issues**: Voor bugs en features
- **GitHub Discussions**: Voor vragen en discussie  
- **Repository**: https://github.com/p-astro16/serverplugins

Bedankt voor je bijdrage! 🎉

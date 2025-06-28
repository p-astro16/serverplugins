# 🚀 GitHub Setup Instructies

Je TeleportPlugin is nu volledig GitHub-ready! Hier is hoe je het uploadt en automatisch bouwt.

## 📋 Wat er klaar is:

✅ **Maven configuratie** (pom.xml)  
✅ **GitHub Actions** voor automatische builds  
✅ **Professional README** met badges en documentatie  
✅ **Issue templates** voor bug reports en feature requests  
✅ **Contributing guidelines**  
✅ **Security policy**  
✅ **License** (MIT)  
✅ **Gitignore** voor clean repository  

## 🔄 Upload naar GitHub:

### Stap 1: Upload naar bestaande repository
Omdat je al een repository hebt (https://github.com/p-astro16/serverplugins), kun je deze plugin daar toevoegen:

1. Ga naar je repository: [p-astro16/serverplugins](https://github.com/p-astro16/serverplugins)
2. Klik **"Add file"** → **"Upload files"**
3. Sleep alle bestanden uit de `mcplugin-teleport` map naar GitHub
4. Of maak een nieuwe folder `teleportplugin/` en upload daar
5. Commit message: `Add: TeleportPlugin with advanced trading system`
6. Klik **"Commit changes"**

### Stap 2: Alternatief - Eigen repository

**Optie A: Drag & Drop naar bestaande repo**
1. Open https://github.com/p-astro16/serverplugins
2. Klik **"Add file"** → **"Upload files"**
3. Sleep de bestanden naar GitHub
4. Commit message: `Add: TeleportPlugin with trading system`
5. Klik **"Commit changes"**

**Optie B: Git Command Line (naar bestaande repo)**
```bash
cd "d:\New WinRAR ZIP archive\mcplugin-teleport"
git init
git add .
git commit -m "Add: TeleportPlugin with trading system"
git branch -M main
git remote add origin https://github.com/p-astro16/serverplugins.git
git push -u origin main
```

### Stap 3: Automatische Build starten
1. Ga naar je repository op GitHub
2. Klik op **"Actions"** tab
3. GitHub detecteert automatisch de workflow
4. De eerste build start automatisch
5. Wacht op groene ✅ (betekent success)

## 📦 JAR Downloaden:

### Na successful build:
1. Ga naar **"Actions"** tab
2. Klik op de laatste **groene** build
3. Scroll naar beneden naar **"Artifacts"**
4. Download **"TeleportPlugin-[commit-hash]"**
5. Pak de ZIP uit → daar zit je **JAR** file!

## 🎯 Repository URL's zijn al correct:

De bestanden zijn al geconfigureerd voor:
- **Repository**: `https://github.com/p-astro16/serverplugins`
- **Author**: `p-astro16`
- **Server**: `Spigot (Paper compatible)`
- **Version**: `Minecraft 1.21`

## 🏷️ Release maken (Optioneel):

Voor een officiele release:
1. Ga naar je repository
2. Klik **"Releases"** → **"Create a new release"**
3. Tag: `v1.0.0`
4. Title: `TeleportPlugin v1.0.0`
5. Beschrijving: automatisch ingevuld
6. Klik **"Publish release"**
7. JAR wordt automatisch toegevoegd!

## 🔍 Verification Checklist:

Na upload, controleer:
- [ ] Repository is zichtbaar
- [ ] README wordt mooi weergegeven
- [ ] Actions tab toont build status
- [ ] Eerste build is succesvol (groen ✅)
- [ ] JAR is downloadbaar van Artifacts
- [ ] Issue templates werken
- [ ] License is zichtbaar

## 🎉 Success! 

Je plugin is nu:
- ✅ **Professional opgezet** op GitHub
- ✅ **Automatisch gebouwd** bij elke update
- ✅ **Ready voor contributors**
- ✅ **Easy te installeren** voor users

## 📞 Hulp nodig?

Als er problemen zijn:
1. **Build fails**: Check de Actions tab voor error details
2. **Upload issues**: Probeer drag & drop methode
3. **Permission errors**: Check repository settings

## 🔄 Volgende stappen:

1. **Upload naar GitHub** ✅
2. **Download JAR** van Artifacts ✅  
3. **Test op je server** ✅
4. **Share met anderen** ✅
5. **Accept contributions** ✅

**Happy coding!** 🎮✨

## ☕ Java Setup Guide

If you get error: **"JAVA_HOME is not set and no 'java' command could be found in your PATH"**

Follow this guide to fix it.

---

## 🔧 Option 1: Use Android Studio's Built-in JDK (Easiest)

Android Studio comes with a JDK. Use that!

### Step 1: Find Android Studio's JDK

**Windows:**
```
C:\Program Files\Android\Android Studio\jbr
```

**macOS:**
```
/Applications/Android Studio.app/Contents/jbr
```

**Linux:**
```
~/android-studio/jbr
```

### Step 2: Set JAVA_HOME

**Windows (Command Prompt):**
```cmd
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
```

**Windows (PowerShell):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
```

**macOS/Linux (Bash/Zsh):**
```bash
export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr
```

### Step 3: Verify it works

```bash
java -version
```

Should show version 11 or higher.

---

## 🔧 Option 2: Install Java Separately

### Download JDK 11 or Higher

Visit: https://www.oracle.com/java/technologies/downloads/

Download: **Java 21** (latest) or **Java 11** (stable)

### Install

Follow the installer prompts.

### Set JAVA_HOME

**Windows:**
1. Open: Settings → System → About → Advanced system settings
2. Click "Environment Variables"
3. Click "New..." under System variables
4. Variable name: `JAVA_HOME`
5. Variable value: `C:\Program Files\Java\jdk-21` (or your JDK path)
6. Click OK → OK
7. Restart Command Prompt

**macOS/Linux:**
Add to `~/.bashrc` or `~/.zshrc`:
```bash
export JAVA_HOME=$(/usr/libexec/java_home)
# or specific path:
export JAVA_HOME=/usr/local/java/jdk-21
```

Then reload: `source ~/.bashrc`

### Step 3: Verify

```bash
java -version
```

---

## ✅ After Setup

### Test it works:

```bash
cd E:\Prashant-Work\PortfolioWatcher
./gradlew --version
```

Should show Gradle version and Java version.

### Run tests:

```bash
./gradlew test
```

Should compile and run tests successfully.

### Build the app:

```bash
./gradlew build
```

---

## 🚨 Troubleshooting

### Issue: "java: command not found" after setting JAVA_HOME

**Solution:** 
- Restart terminal/command prompt after setting JAVA_HOME
- Verify path exists: `ls /path/to/java/bin/java`

### Issue: "JAVA_HOME points to invalid location"

**Solution:**
- Check path has `bin/java` file inside
- Correct path: `C:\Program Files\Java\jdk-21` (not `jdk-21\bin`)

### Issue: "java version too old"

**Solution:**
- Need Java 11 or higher
- Current: `java -version`
- Download newer version

---

## 🎯 Quick Reference

| OS | JAVA_HOME Path |
|----|----|
| Windows | `C:\Program Files\Java\jdk-21` |
| macOS | `/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home` |
| Linux | `/usr/lib/jvm/java-21-openjdk` |
| Android Studio | `[Android Studio Path]/jbr` |

---

## ✨ You're Done!

After this, you can:
- Run: `./gradlew test`
- Run: `./gradlew build`
- Use Android Studio (which already has Java built-in)


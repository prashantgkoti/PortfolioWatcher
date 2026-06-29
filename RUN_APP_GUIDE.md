## 🚀 Portfolio Watcher - How to Run the App

This guide shows you how to run the Portfolio Watcher Android app on your machine.

---

## 📋 Prerequisites

Before running the app, ensure you have:

1. **Android Studio** (latest version)
   - Download from: https://developer.android.com/studio

2. **Java Development Kit (JDK)** version 11 or higher
   - Download from: https://www.oracle.com/java/technologies/downloads/

3. **Android SDK**
   - Installed automatically with Android Studio
   - Minimum SDK: API 26 (Android 8.0)
   - Target SDK: API 34 (Android 14)

4. **Git** (to clone the repository)
   - Download from: https://git-scm.com/

---

## 🎯 Step 1: Get the Code

### Option A: Clone from GitHub (if pushed)
```bash
git clone https://github.com/prashantgkoti/PortfolioWatcher.git
cd PortfolioWatcher
```

### Option B: Open existing project
```bash
# If you already have the code locally
cd E:\Prashant-Work\PortfolioWatcher
```

---

## 🔧 Step 2: Open in Android Studio

1. **Launch Android Studio**

2. **Open the project:**
   - Click "Open" (or File → Open)
   - Navigate to `E:\Prashant-Work\PortfolioWatcher`
   - Click "Open"

3. **Wait for sync:**
   - Android Studio will download dependencies
   - Bottom shows progress: "Gradle build running..."
   - Wait for it to complete (may take 2-5 minutes)

4. **Verify no errors:**
   - Check bottom-right corner
   - Should show "✓ Gradle sync finished successfully"

---

## 📱 Step 3: Set Up Emulator or Device

### Option A: Use Android Emulator (Recommended for testing)

**Step 1: Open Device Manager**
- Click "Device Manager" in Android Studio toolbar (right side)
- Or: Tools → Device Manager

**Step 2: Create Virtual Device**
- Click "Create Virtual Device"
- Select device: "Pixel 6 Pro" (or any recent Pixel phone)
- Click "Next"

**Step 3: Select Android Version**
- Choose "API 34" (Android 14) or higher
- Click "Download" if needed
- Click "Next"

**Step 4: Verify Settings**
- Keep default settings
- Click "Finish"

**Step 5: Launch Emulator**
- Look for your device in Device Manager
- Click the green "Play" button to launch
- Wait 30-60 seconds for emulator to boot

**You'll see:** Android home screen on simulated phone

### Option B: Use Physical Device

**Step 1: Enable Developer Mode**
- Open device Settings
- Go to About Phone
- Find "Build Number"
- Tap 7 times (until you see "You are a developer!")

**Step 2: Enable USB Debugging**
- Go to Settings → Developer Options
- Enable "USB Debugging"

**Step 3: Connect to Computer**
- Plug phone into computer with USB cable
- You should see a prompt on phone
- Tap "Allow" to grant debugging permission

**Step 4: Verify Connection**
- Open Terminal/Command Prompt
- Run: `adb devices`
- Should show your device listed

---

## ▶️ Step 4: Run the App

### Method 1: Click "Run" Button (Easiest)

1. **Open MainActivity.kt**
   - In Android Studio, click on `MainActivity.kt`
   - It's in: `app/src/main/java/.../MainActivity.kt`

2. **Click "Run" button**
   - Green play button in toolbar
   - Or: Shift + F10 (Windows) / Control + R (Mac)

3. **Select device/emulator**
   - Choose your emulator or physical device
   - Click "Run"

4. **Wait for build and deploy**
   - Building takes 30-60 seconds (first time)
   - Subsequent runs are faster (10-20 seconds)
   - You'll see build progress in "Build" tab at bottom

5. **App launches!**
   - Should see "Portfolio Watcher" app on device/emulator
   - Displays "Hello, World!" greeting

### Method 2: Use Command Line

```bash
# Terminal/Command Prompt in project directory

# Build and run on connected device
./gradlew installDebug

# Then launch the app manually on device
# Or use:
adb shell am start -n com.example.portfoliowatcher/.MainActivity
```

### Method 3: Use Gradle

```bash
# Build APK
./gradlew assembleDebug

# Run on all connected devices
./gradlew installDebug
```

---

## ✅ Verify App is Running

You should see:

1. **On emulator/phone:**
   - App icon: "Portfolio Watcher" appears on home screen
   - App opens and shows: "Hello, World!" greeting
   - Material Design 3 theme (modern, clean look)

2. **In Android Studio:**
   - Logcat tab shows: `System.out: App is running!`
   - No red error messages

3. **Build complete:**
   - Bottom-right shows: "Build successful" ✓

---

## 🐛 Troubleshooting

### Problem: "SDK not found"
**Solution:**
1. File → Project Structure
2. SDK Location → Edit
3. Point to Android SDK folder (usually C:\Users\[YourUsername]\AppData\Local\Android\Sdk)

### Problem: "Emulator won't start"
**Solution:**
1. Close all emulators
2. Tools → SDK Manager
3. Check "Android Emulator" is installed
4. Restart Android Studio

### Problem: "Build fails with Gradle error"
**Solution:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### Problem: "Device not recognized"
**Solution:**
```bash
# Restart ADB
adb kill-server
adb start-server
adb devices
```

### Problem: "App crashes on startup"
**Check Logcat (bottom of Android Studio):**
- Filter: Error (red)
- Look for exception message
- Common cause: Missing dependencies (run `./gradlew build` again)

---

## 🔍 View App Logs

1. **Open Logcat**
   - View → Tool Windows → Logcat
   - Or: Alt + 6

2. **Filter by app name**
   - Filter field: type "portfoliowatcher"
   - Shows only your app's logs

3. **View different log levels**
   - Dropdown shows: Verbose, Debug, Info, Warn, Error
   - Start with "Info" or "Debug"

---

## 📊 Understanding the Current App

The app currently shows:

```
┌─────────────────────────────┐
│   Portfolio Watcher         │
│                             │
│      Hello, World!          │  ← This is the Greeting composable
│                             │
│   [Material Design Theme]   │
└─────────────────────────────┘
```

This is the foundation. Next steps will add:
- Portfolio list screen
- Holdings display
- NAV updates
- Firebase integration
- Settings screen

---

## 🚀 Running Tests

### Run All Tests
```bash
# Unit tests (fast, no emulator needed)
./gradlew test

# Integration tests (requires emulator)
./gradlew connectedAndroidTest

# Both
./gradlew test connectedAndroidTest
```

### View Test Results
- Results appear in: `app/build/reports/tests/`
- Open in browser to see detailed report

---

## 📱 Hot Reload (Live Changes)

After running the app, you can:

1. **Make code changes**
2. **Press Ctrl + S** (save)
3. **Click "Apply Changes"** button (toolbar)
   - App updates WITHOUT restarting (much faster!)
   - Only works for simple UI changes
   - Full rebuild needed for architecture changes

---

## 🎯 Common Run Workflows

### Development (Making UI Changes)
```bash
1. ./gradlew run (or click Run button)
2. Make UI changes
3. Ctrl + S (save)
4. Click "Apply Changes" button
5. See changes instantly!
```

### Testing
```bash
./gradlew test                    # Unit tests (30 seconds)
./gradlew connectedAndroidTest   # Integration tests (2-3 minutes)
```

### Before Committing
```bash
./gradlew clean
./gradlew build      # Ensures everything compiles
./gradlew test       # Ensures tests pass
git status          # Check what changed
git add .
git commit -m "..."
git push
```

---

## 💡 Tips for Smooth Development

1. **Use emulator over phone** - Faster iterations
2. **Keep emulator running** - Don't close between runs
3. **Use hot reload** - Don't rebuild full app each time
4. **Check Logcat** - Always watch for errors
5. **Run tests before committing** - Catch bugs early
6. **Use breakpoints** - Debug with Ctrl + F8
7. **Keep gradle sync clean** - File → Sync Now if unsure

---

## 🔄 Development Loop

```
1. Write code
   ↓
2. Save (Ctrl + S)
   ↓
3. Run app (Shift + F10)
   ↓
4. Check results
   ↓
5. Run tests (./gradlew test)
   ↓
6. If all good → Commit (git commit)
   ↓
7. If issues → Fix code → Go to step 1
```

---

## ✨ After App Runs Successfully

Next steps to build the app:

1. **Complete remaining entity/DAO files** (PortfolioEntity, HoldingEntity, etc.)
2. **Create UI screens** (HomeScreen, DetailScreen, etc.)
3. **Integrate Firebase** for backend
4. **Add MFapi.in integration** for portfolio data
5. **Write more tests** for new features

---

## 📞 Getting Help

If something goes wrong:

1. **Check the error message** in Logcat
2. **Search Google** for the error message
3. **Check official Android documentation** at developer.android.com
4. **Restart Android Studio** (often fixes mysterious issues)
5. **Clean and rebuild:** `./gradlew clean && ./gradlew build`

---

## 🎉 You're Ready!

Everything is set up. Click "Run" and see your app come to life! 🚀


## 🔧 Android Emulator Troubleshooting Guide

The emulator isn't starting? Follow this guide to diagnose and fix the issue.

---

## 🚨 What's Happening?

### Symptom 1: Emulator hangs/freezes on boot
**Shows Android logo but never loads home screen**

### Symptom 2: Emulator crashes immediately
**Error message appears or window closes**

### Symptom 3: "No bootable device"
**Device Manager shows error instead of running**

### Symptom 4: Very slow/stuck on "Android" splash screen
**Takes more than 2 minutes to boot**

---

## 🔍 Step 1: Check System Requirements

Your computer needs:
- **RAM:** At least 8GB total (prefer 16GB+)
- **CPU:** Modern processor with virtualization support
- **Storage:** At least 20GB free space
- **Virtualization:** Enabled in BIOS

### Check Available RAM:
**Windows:**
- Right-click "This PC" → Properties
- Look for "Installed RAM"
- Should be 8GB or more

**macOS:**
- Apple menu → About This Mac
- Look for "Memory"
- Should be 8GB or more

---

## ✅ Step 2: Enable Virtualization (Windows)

**Check if enabled:**
1. Press **Ctrl + Shift + Esc** (Task Manager)
2. Click **Performance** tab
3. Click **CPU**
4. Look for "Virtualization: Enabled"
   - ✅ If it says "Enabled" → Skip to Step 3
   - ❌ If it says "Disabled" → Continue below

**Enable in BIOS:**
1. Restart computer
2. Press **F2**, **F10**, **Del**, or **Esc** during boot (varies by computer)
3. Find "Virtualization" or "Intel VT-x" setting
4. Change from "Disabled" to "Enabled"
5. Save and reboot

---

## 🧹 Step 3: Clear Emulator Cache

Corrupted cache can prevent emulator from starting.

1. **In Device Manager,** right-click your emulator
2. Click **Wipe Data**
3. Wait for operation to complete
4. Click ▶️ Play to start emulator

---

## ⚙️ Step 4: Reduce Emulator Resources

Too much RAM allocated can cause issues.

1. **In Device Manager,** click the ✏️ **Edit** button
2. Look for **Memory:**
   - Change from 2048 MB to **1024 MB**
   - Or try **512 MB** if still failing

3. Click **Finish**
4. Try starting again

---

## 🗑️ Step 5: Delete and Recreate Emulator

If emulator is corrupted, recreate it.

1. **In Device Manager,** click 🗑️ **Delete**
2. Confirm deletion
3. Click **Create Device**
4. Follow these settings:
   - Device: **Pixel 6 Pro**
   - API Level: **API 34** (Android 14)
   - RAM: **1024 MB** (not 2048)
   - Storage: **2048 MB**
5. Click **Finish**
6. Click ▶️ **Play** to start

---

## 🔄 Step 6: Restart Android Studio & Gradle Daemon

Sometimes services get stuck.

**Close everything:**
1. Close the emulator window
2. Close Android Studio completely
3. Wait 10 seconds

**Clear Gradle cache:**
- Open Terminal/Command Prompt
- Run: `./gradlew --stop`

**Restart:**
1. Open Android Studio
2. Wait for Gradle to sync (bottom right)
3. Try emulator again

---

## 🔍 Step 7: Check Logcat for Errors

**Open Logcat to see error messages:**
1. Android Studio → View → Tool Windows → Logcat
2. Try to start emulator
3. Look for red error messages
4. Search error online or note it down

---

## 📊 Common Issues & Solutions

### Issue: "QEMU: Graphics initialization failure"
**Cause:** GPU acceleration not working
**Solution:**
1. Device Manager → Edit
2. Find **Graphics**
3. Change from "Auto" to "Software"
4. Click Finish and try again

### Issue: "KVM device not found" (Linux)
**Cause:** Virtualization not enabled
**Solution:** Follow Step 2 (Enable Virtualization)

### Issue: "Emulator: ERROR: x86 emulation currently requires nested virtualization"
**Cause:** CPU doesn't support emulation
**Solution:**
1. Use a different device (ARM instead of x86)
2. Or use a physical device instead

### Issue: "Emulator is already running"
**Cause:** Previous emulator didn't close properly
**Solution:**
1. Open Task Manager (Ctrl + Shift + Esc)
2. Find "emulator" or "qemu"
3. Right-click → End Task
4. Try again

### Issue: "Starting in Kotlin 2.0, the Compose Compiler..." warning
**Cause:** Just a build warning (NOT emulator issue)
**Solution:** Safe to ignore - emulator unrelated

---

## 🆘 If Nothing Works

### Option 1: Use Physical Device
- Connect Android phone via USB
- Enable USB Debugging (Settings → About Phone → Build Number, tap 7 times)
- Click Run in Android Studio
- Select your phone

### Option 2: Restart Everything
1. Restart computer (full shutdown)
2. Open Android Studio
3. Device Manager → Create new emulator
4. Start emulator

### Option 3: Reinstall Android Studio
- Uninstall Android Studio
- Delete `~/.android` folder
- Reinstall Android Studio (fresh install)

---

## 📝 Diagnostic Checklist

Before asking for help, verify:

- [ ] Computer has 8GB+ RAM
- [ ] Virtualization enabled in BIOS
- [ ] At least 20GB free disk space
- [ ] Android Studio is fully updated
- [ ] Emulator RAM set to 1024 MB (not 2048)
- [ ] Tried deleting and recreating emulator
- [ ] Tried restarting computer
- [ ] Tried running `./gradlew --stop`

---

## 💡 Performance Tips

Once emulator is working:

1. **Keep it running** - Don't close between app tests
2. **Allocate enough RAM** - 1024-2048 MB is good
3. **Don't use "Use Host GPU"** if it causes crashes
4. **Close other apps** - Chrome, Slack, heavy apps
5. **Use wired internet** - More stable than WiFi

---

## 📞 Getting Help

When emulator won't start, provide:
1. **Error message** (copy from Device Manager or Logcat)
2. **Computer specs** (RAM, CPU model)
3. **OS** (Windows/Mac/Linux and version)
4. **Steps already tried**
5. **Screenshot** of the issue

This helps diagnose faster!

---

## 🎯 Next Steps

1. Try Step 2 (Enable Virtualization) if you haven't
2. Try Step 4 (Reduce RAM to 1024 MB)
3. Try Step 5 (Delete and recreate)
4. If still stuck → Use physical device as alternative

**Most common fix:** Reduce emulator RAM from 2048 MB to 1024 MB and enable virtualization. Try that first!


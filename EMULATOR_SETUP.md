## 📱 How to Create & Select an Android Emulator

If you don't see the emulator option when clicking Run, follow this guide.

---

## 🎯 Step 1: Open Device Manager

In **Android Studio**, look for the **Device Manager** icon in the toolbar (right side):

```
┌─────────────────────────────────────────────────┐
│ File Edit View Navigate Code Refactor Tools ... │
│                                              [D] │  ← Device Manager (looks like phone icon)
└─────────────────────────────────────────────────┘
```

### Not seeing it? Use menu instead:
- Click **Tools** → **Device Manager**

---

## ➕ Step 2: Create Virtual Device

1. **Click "Create Device"** button in Device Manager panel

2. **Select a device** (phone model):
   - Choose "Pixel 6 Pro" or any "Pixel" phone
   - Click **Next**

```
┌─────────────────────────────────────────┐
│ Phone         Tablet      Wear OS        │
│                                          │
│ ☐ Pixel 3a    ☑ Pixel 6 Pro             │
│ ☐ Pixel 4     ☐ Pixel 7                 │
│ ☐ Pixel 5     ☐ Pixel Tablet            │
│                                          │
│              [Next]                      │
└─────────────────────────────────────────┘
```

---

## 🔄 Step 3: Select Android Version

1. **Choose API level:** Select **API 34** (Android 14) or higher
   - This is shown on the right side

2. **If not downloaded:**
   - Click **Download** button
   - Wait for download to complete (5-10 minutes)

3. **Click Next**

```
┌──────────────────────────────────────────────┐
│ Select a system image:                       │
│                                              │
│ Recommended                                  │
│ ☑ API 34 (Android 14) - arm64-v8a           │
│                                              │
│ API 33 (Android 13) - arm64-v8a             │
│ API 32 (Android 12) - arm64-v8a             │
│                                              │
│    [Download]           [Next]               │
└──────────────────────────────────────────────┘
```

---

## ⚙️ Step 4: Verify Settings

1. **Name:** Keep default (e.g., "Pixel 6 Pro API 34")

2. **RAM:** Should show **2048 MB** or more
   - If less, increase it if your computer has memory

3. **Storage:** Keep default

4. **Click Finish**

```
┌──────────────────────────────────────────────┐
│ Android Virtual Device Configuration         │
│                                              │
│ Name: Pixel 6 Pro API 34                     │
│ Device: Pixel 6 Pro                          │
│ API Level: 34                                │
│ RAM: 2048 MB                                 │
│ Internal Storage: 800 MB                     │
│ SD Card: None                                │
│ Keyboard: ☑ Enable keyboard input           │
│                                              │
│              [Finish]                        │
└──────────────────────────────────────────────┘
```

---

## ▶️ Step 5: Launch the Emulator

1. **In Device Manager,** you'll see your new device listed

2. **Click the green "Play" button** ▶️ on the right
   ```
   Pixel 6 Pro API 34    [▶️ Start]  [✎ Edit]  [🗑️ Delete]
   ```

3. **Wait 30-60 seconds** for emulator to boot
   - You'll see an Android phone simulator appear
   - Wait until you see the home screen (Android logo)

---

## ▶️ Step 6: Run Your App

Once emulator is running and shows home screen:

1. **Open Android Studio** (switch back from emulator window)

2. **Click the green "Run" button** (top toolbar)
   ```
   ▶️ Run 'app'
   ```

3. **Select your emulator** from the dialog:
   ```
   ┌─────────────────────────────────┐
   │ Select Deployment Target        │
   │                                 │
   │ ☑ Pixel 6 Pro API 34 (Running)  │
   │                                 │
   │        [OK]      [Cancel]        │
   └─────────────────────────────────┘
   ```

4. **Click OK**

---

## ✅ App Should Launch!

After ~30-60 seconds, your app will appear on the emulator:

```
┌──────────────────────┐
│   Portfolio Watcher  │
│                      │
│    Hello, World!     │
│                      │
│   [Material3 Theme]  │
└──────────────────────┘
```

---

## 🚨 If Emulator Doesn't Start

### Issue: "Emulator starts but gets stuck"
**Solution:**
- Close emulator (click X)
- Restart: Device Manager → Click Play again
- Wait longer (sometimes takes 2-3 minutes first time)

### Issue: "Not enough RAM"
**Solution:**
- Close other applications (Chrome, Slack, etc.)
- Or increase emulator RAM in Device Manager → Edit

### Issue: "Computer fans spinning loud"
**Solution:**
- Emulation is CPU-intensive
- This is normal
- Close emulator when not testing to save CPU

### Issue: "Cannot download API 34"
**Solution:**
1. Tools → SDK Manager
2. Ensure "Android 14 (API 34)" is checked
3. Click Apply
4. Wait for download

---

## 💡 Tips

1. **Keep emulator running** while developing
   - Don't close it between app runs
   - Much faster to reload app than restart emulator

2. **Use hot reload** after changes:
   - Edit code
   - Save (Ctrl + S)
   - Click "Apply Changes" button (instead of full Run)
   - Much faster!

3. **Check emulator is running** before clicking Run
   - If you see the Android home screen → emulator ready
   - If you see black screen → still loading

---

## 🎯 Quick Checklist

- [ ] Device Manager opened
- [ ] Virtual device created (Pixel 6 Pro, API 34)
- [ ] API 34 downloaded
- [ ] Emulator launched and showing home screen
- [ ] Click Run in Android Studio
- [ ] App appears on emulator screen
- [ ] See "Hello, World!" message

---

## ✨ You're Done!

Your emulator is ready to test the app. You can now:
- Click Run to test changes
- Use hot reload for faster iterations
- Run tests on a real Android environment


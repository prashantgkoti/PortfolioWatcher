/**
 * MainActivity.kt - Main entry point of Portfolio Watcher app
 *
 * This is the root Activity that:
 * - Is launched when app starts
 * - Sets up the Compose UI framework
 * - Applies the theme
 * - Initializes the navigation system
 *
 * ComponentActivity - Base class for activities using Jetpack Compose
 * Unlike AppCompatActivity, it's optimized for Compose and doesn't need legacy support
 */

package com.example.portfoliowatcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.portfoliowatcher.ui.theme.PortfolioWatcherTheme

/**
 * MainActivity - Root activity for Portfolio Watcher
 *
 * When app launches, Android creates this activity
 * The onCreate() method is called once during initialization
 */
class MainActivity : ComponentActivity() {

    /**
     * onCreate - Called when activity is created
     *
     * This is where we:
     * 1. Initialize Compose
     * 2. Apply the app theme
     * 3. Set up the UI hierarchy
     *
     * @param savedInstanceState - State from previous instance (if app was killed and restored)
     *                            null if this is first launch
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Always call super.onCreate() first
        // This initializes the Activity base class
        super.onCreate(savedInstanceState)

        // enableEdgeToEdge() - Allows app content to extend behind system bars
        // Makes the status bar transparent so app uses full screen
        // Requires appropriate padding in Compose to avoid content under bars
        enableEdgeToEdge()

        // setContent - Replaces old XML layout system with Compose
        // Everything inside the lambda is the Compose UI tree
        // This lambda is called to build the UI
        setContent {
            // Apply the PortfolioWatcherTheme to all child composables
            // Theme provides colors, typography, shapes from Material3 design system
            PortfolioWatcherTheme {
                // Scaffold - Material3 layout that provides:
                // - TopAppBar area
                // - BottomAppBar area
                // - FloatingActionButton area
                // - Content area with innerPadding to avoid overlap with system bars
                Scaffold(
                    // Modifier.fillMaxSize() - Takes up all available space
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // innerPadding contains padding for system bars
                    // We pass this to Greeting so content doesn't go under bars
                    Greeting(
                        name = "Portfolio Watcher",
                        // Apply padding to prevent content going under system bars
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Greeting - Simple composable to display text
 *
 * A Composable is a function that describes part of the UI
 * @Composable annotation tells Compose this function builds UI
 *
 * @param name - Text to display
 * @param modifier - Optional Modifier for styling/layout (padding, size, etc.)
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // Text - Composable that displays text on screen
    Text(
        // String to display
        text = "Hello $name!",
        // Apply the modifier (padding from Scaffold)
        modifier = modifier
    )
}

/**
 * GreetingPreview - Preview for Android Studio's design tool
 *
 * @Preview annotation tells Compose to show this in the design preview
 * Useful for rapid UI development without running app on device/emulator
 *
 * showBackground = true - Adds a white background to preview
 * So we can see the text clearly in design view
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    // Apply theme to preview
    PortfolioWatcherTheme {
        // Show what Greeting looks like
        Greeting("Portfolio Watcher")
    }
}
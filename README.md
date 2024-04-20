 
# Dynamic theme export

This simple Compose app allows you to preview and export the available colors from your device's current dynamic theme. Use these colors as the starting point to create a new theme for your application.

## How to install

- **Option 1:** Download the latest APK from the releases page.
- **Option 2:** Clone the repository and run the app like any other Android project.

**NOTE:** This app depends on dynamic themes to work and it can only be installed on devices running [Android 12 (API level 31) or higher](https://developer.android.com/develop/ui/views/theming/dynamic-colors).

## How to use

### Preview theme colors

Use the **Preview** tab to explore all available [Color Roles](https://m3.material.io/styles/color/roles) for the current dynamic theme.

<video src="https://github.com/spun/android-dynamic-theme-export/assets/1004332/ad37bf1f-03b1-4e07-b354-b9e039e5f600" width=200 controls="true" muted="true" loop="true"></video>

Change your device's wallpaper to discover a theme you like, and use it as the foundation for your new theme.

<video src="https://github.com/spun/android-dynamic-theme-export/assets/1004332/960e8fba-e13c-419e-a66e-8c5b7d74484a" width=200 controls="true" muted="true" loop="true"></video>

### Export current theme

Use the **Export** tab to copy all the dynamic theme values as [Material3 Compose ColorSchemes](https://developer.android.com/reference/kotlin/androidx/compose/material3/ColorScheme).

<video src="https://github.com/spun/android-dynamic-theme-export/assets/1004332/a722dbc2-6804-4512-9f0d-3359daa6b58e" width=200 controls="true" muted="true" loop="true"></video>

## TODO

- [ ] Copy color values from Preview table
- [ ] Fix Preview zoom
- [ ] Export as xml file? (views)
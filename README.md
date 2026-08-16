# Dynamic theme export

This simple Compose app allows you to preview and export the available colors from your device's current dynamic theme. Use these colors as the starting point to create a new theme for your application.

## How to install

- **Option 1:** Download the [latest APK from the releases page](https://github.com/spun/android-dynamic-theme-export/releases/latest).
- **Option 2:** Clone the repository and run the app like any other Android project.

**NOTE:** This app depends on dynamic themes to work and it can only be installed on devices running [Android 12 (API level 31) or higher](https://developer.android.com/develop/ui/views/theming/dynamic-colors).

## How to use

### Preview theme colors

Use the **Preview** tab to explore all available [Color Roles](https://m3.material.io/styles/color/roles) for the current dynamic theme.

<video src="https://github.com/user-attachments/assets/9fa3c891-8afe-4fa8-bfc0-fb19b14ff2a2" width=200 controls="true" muted="true" loop="true"></video>

Change your device's wallpaper to discover a theme you like, and use it as the foundation for your new theme.

<video src="https://github.com/user-attachments/assets/36a0c466-a684-46b8-b01f-6f2b2222f104" width=200 controls="true" muted="true" loop="true"></video>

### Export current theme

Use the **Export** tab to copy all the dynamic theme values as [Material3 Compose ColorSchemes](https://developer.android.com/reference/kotlin/androidx/compose/material3/ColorScheme).

<video src="https://github.com/user-attachments/assets/5f796d99-0c0e-45f6-9066-fb83e0421c77" width=200 controls="true" muted="true" loop="true"></video>

## TODO

- [ ] Fix Preview zoom
- [ ] Export as xml file? (views)

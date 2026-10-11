# Modern Android TV / Google TV IPTV Player

Built specifically for Google TV / Android TV using the **10-foot UI** guidelines, D-pad remote navigation, and Jetpack Compose for TV.

## Project Structure
- **Root**: `C:\Users\athul\.gemini\antigravity-ide\scratch\iptv-player\`
- **Settings & Gradle**:
  - `settings.gradle.kts`
  - `build.gradle.kts`
  - `gradle/libs.versions.toml`
  - `app/build.gradle.kts`
- **Application Code**:
  - `app/src/main/AndroidManifest.xml` (Leanback & Landscape configuration)
  - `app/src/main/java/com/example/iptvplayer/MainActivity.kt` (Root Compose activity)
  - `app/src/main/java/com/example/iptvplayer/model/Channel.kt` (Domain model)
  - `app/src/main/java/com/example/iptvplayer/data/ChannelRepository.kt` (Streaming M3U parser & OkHttp client)
  - `app/src/main/java/com/example/iptvplayer/ui/viewmodel/ChannelViewModel.kt` (Reactive StateFlow management)
  - `app/src/main/java/com/example/iptvplayer/ui/components/ChannelCard.kt` (TV Card with 1.1x focus scale and accent border)
  - `app/src/main/java/com/example/iptvplayer/ui/screens/HomeScreen.kt` (TvLazyRow categories + TvLazyVerticalGrid channel browser)
  - `app/src/main/java/com/example/iptvplayer/ui/screens/PlayerScreen.kt` (Fullscreen Media3 ExoPlayer with HLS & BackHandler)

## How to Open in Android Studio
1. Open Android Studio.
2. Select **Open an Existing Project**.
3. Navigate to:
   `C:\Users\athul\.gemini\antigravity-ide\scratch\iptv-player`
4. Let Gradle sync and run the project on an Android TV Emulator (Google TV 1080p).

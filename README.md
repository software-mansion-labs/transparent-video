# Expo Transparent Video

`expo-transparent-video` provides support for performant transparent video, on Android, for React Native and Expo.

<video autoplay playsinline controls>
  <source src="https://github.com/user-attachments/assets/c779d542-b55d-46cb-92ff-a9a367f77452" type="video/mp4">
  Your browser does not support the video tag.
</video>

## Installation

1. Install the package

```bash
# using npm
npm install expo-transparent-video

# or using yarn
yarn add expo-transparent-video
```

2. Build the package

With `expo`:

`expo-transparent-video` relies on native code, so you will need to create a development build to use it.

```bash
npx expo prebuild
npx expo run:android
```

With `react-native`:

```bash
npx react-native run-android
```

## Usage

Since this package only supports Android, it's recommended to create a platform-specific component.

VideoPlayer.android.tsx:
```tsx
import { TransparentVideoView, useVideoPlayer } from 'expo-transparent-video';

export const VideoPlayer = () => {
  const player = useVideoPlayer('https://cdn.example.com/video.webm');

  player.loop = true;
  player.play();

  return (
    <TransparentVideoView player={player} />
  );
};
```

> [!WARNING]
> `expo-transparent-video` only support external links to video files. You can't use local files.

## How to create a transparent video for Android?

The source must be a video compatible with [Android](https://developer.android.com/media/platform/supported-formats#video-codecs).

The source must be a composition of two videos vertically superposed:

1. The upper part of the video must display the rgb channels

2. The lower part of the video must display the alpha mask in grayscale
  (black -> alpha = 0% opacity, white -> alpha = 100% opacity) to apply to the rgb part.

```txt
|-----------------------|
|                       |
|                       |
|       rgb video       |
|                       |
|                       |
|-----------------------|
|                       |
|                       |
|  alpha mask video     |
|                       |
|                       |
|-----------------------|
```

> [!WARNING]
> This cannot display a video that has an alpha channel like transparent webm. It only blends rgb data with alpha data.

## But... why?

Well, the default's Android ExoPlayer [does not support alpha channel videos](https://github.com/google/ExoPlayer/issues/7789). Meaning, if your `.webm` (or other supported format) has transparency, it will be rendered as black.

This package relies on a technique explained in [this Medium article](https://medium.com/go-electra/unlock-transparency-in-videos-on-android-5dc43776cc72) to enable transparency in videos on Android.

## License

expo-transparent-video library is licensed under [The MIT License](./LICENSE).

## Credits

This project has been built and is maintained thanks to the support from [Software Mansion](https://swmansion.com)

[![swm](https://logo.swmansion.com/logo?color=white&variant=desktop&width=150&tag=react-native-reanimated-github 'Software Mansion')](https://swmansion.com)

## expo-transparent-video is created by Software Mansion

Since 2012 [Software Mansion](https://swmansion.com) is a software agency with experience in building web and mobile apps. We are Core React Native Contributors and experts in dealing with all kinds of React Native issues. We can help you build your next dream product – [Hire us](https://swmansion.com/contact/projects?utm_source=transparent-video&utm_medium=readme).

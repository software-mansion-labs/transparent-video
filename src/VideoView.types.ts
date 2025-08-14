import { ViewProps } from "react-native";

/**
 * A class that represents an instance of the video player.
 */
export declare class VideoPlayer {
  /**
   * Boolean value whether the player is currently playing.
   * > This property is get-only, use `play` and `pause` methods to control the playback.
   */
  playing: boolean;

  /**
   * Boolean value whether the player is currently paused.
   * > This property is get-only, use `play` and `pause` methods to control the playback.
   */
  paused: boolean;

  /**
   * Determines whether the player should automatically replay after reaching the end of the video.
   * @default false
   */
  loop: boolean;

  /**
   * Boolean value whether the player is currently muted.
   * @default false
   */
  muted: boolean;

  /**
   * Integer value representing the current position in seconds.
   */
  currentTime: number;

  /**
   * Integer value representing the duration of the current content in seconds.
   */
  playableDuration: number;

  /**
   * Float value between 0 and 1 representing the current volume.
   * Muting the player doesn't affect the volume. In other words, when the player is muted, the volume is the same as
   * when unmuted. Similarly, setting the volume doesn't unmute the player.
   * @default 1.0
   */
  volume: number;

  /**
   * Boolean value indicating if the player should correct audio pitch when the playback speed changes.
   * > On web, changing this property is not supported, the player will always correct the pitch.
   * @default true
   */
  preservesPitch: boolean;

  /**
   * Float value between 0 and 16 indicating the current playback speed of the player.
   * @default 1.0
   */
  playbackRate: number;

  /**
   * Determines whether the player should continue playing after the app enters the background.
   * @default false
   */
  staysActiveInBackground: boolean;

  /**
   * Resumes the player.
   */
  play(): void;

  /**
   * Pauses the player.
   */
  pause(): void;

  /**
   * Replaces the current source with a new one.
   */
  replace(source: VideoSource): void;

  /**
   * Seeks the playback by the given number of seconds.
   */
  seekBy(seconds: number): void;

  /**
   * Seeks the playback to the beginning.
   */
  replay(): void;

  /**
   * deallocates the instance of video player.
   */
  deallocate(): void;
}

interface NativeErrorEvent {
  nativeEvent: {
    error: string;
  };
}

interface NativeOnProgressEvent {
  nativeEvent: {
    currentTime: number;
    playableDuration: number;
    seekableDuration: number;
  };
}

export interface TransparentVideoViewProps extends ViewProps {
  /**
   * A player instance – use `useVideoPlayer()` to create one.
   */
  player: VideoPlayer;

  videoAspectRatio?: number;

  onEnd?: () => void;

  onError?: (event: NativeErrorEvent) => void;

  onProgress?: (event: NativeOnProgressEvent) => void;

  /**
   * Determines whether the player allows the user to skip media content.
   * @default false
   */
  requiresLinearPlayback?: boolean;
}

/**
 * Specifies which type of DRM to use. Android supports Widevine, PlayReady and ClearKey, iOS supports FairPlay.
 * */
type DRMType = "clearkey" | "fairplay" | "playready" | "widevine";

/**
 * Specifies DRM options which will be used by the player while loading the video.
 */
type DRMOptions = {
  /**
   * Determines which type of DRM to use.
   */
  type: DRMType;

  /**
   * Determines the license server URL.
   */
  licenseServer: string;

  /**
   * Determines headers sent to the license server on license requests.
   */
  headers?: { [key: string]: string };

  /**
   * Specifies whether the DRM is a multi-key DRM.
   */
  multiKey?: boolean;
};

export type VideoSource = string | { uri: string; drm?: DRMOptions } | null;

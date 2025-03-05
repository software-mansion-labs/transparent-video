import { TransparentVideoView, useVideoPlayer } from "expo-transparent-video";
import { StyleSheet, SafeAreaView } from "react-native";

export default function App() {
  /** Acknowledgement: video source coming from https://github.com/tritus/android_transparent_video */
  const player = useVideoPlayer(
    "https://github.com/tritus/android_transparent_video/raw/refs/heads/main/sample/src/main/res/raw-hdpi/thunder_loader.webm",
  );

  player.loop = true;
  player.play();

  return (
    <SafeAreaView style={styles.container}>
      <TransparentVideoView style={styles.videoPlayer} player={player} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",

    backgroundColor: "#eee",
  },

  videoPlayer: {
    width: 250,
    height: 250,
  },
});

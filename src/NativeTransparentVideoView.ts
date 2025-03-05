import { requireNativeViewManager } from "expo-modules-core";
import { Platform } from "react-native";

const NativeView =
  Platform.OS === "android"
    ? requireNativeViewManager("ExpoTransparentVideo")
    : (_: any) => null;

export default NativeView;

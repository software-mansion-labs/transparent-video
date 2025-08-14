import { requireNativeModule } from "expo-modules-core";
import { Platform } from "react-native";

const module =
  Platform.OS === "android"
    ? requireNativeModule("ExpoTransparentVideo")
    : undefined;

export default module;

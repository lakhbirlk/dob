module.exports = function (api) {
  api.cache(true);
  return {
    presets: [
      ["babel-preset-expo", { jsxImportSource: "nativewind" }],
    ],
    plugins: [
      // Use the core NativeWind CSS-interop babel plugin directly
      // (avoids nativewind/babel preset which requires react-native-worklets)
      [require.resolve("react-native-css-interop/dist/babel-plugin")],
    ],
  };
};

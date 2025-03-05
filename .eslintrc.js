/** @type {import("eslint").Linter.Config} */
module.exports = {
  extends: ["./node_modules/expo-module-scripts/eslintrc.base.js"],
  overrides: [
    {
      files: ["*.config.js", ".*rc.js"],
      extends: ["universe/node"],
    },
  ],
};

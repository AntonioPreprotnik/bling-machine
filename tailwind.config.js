const defaultTheme = require("tailwindcss/defaultTheme");

module.exports = {
  purge:
    process.env.NODE_ENV == "production"
      ? ["./resources/app/public/js/main.js"]
      : ["./resources/app/public/js/cljs-runtime/**/*.js"],

  mode: "jit",
  darkMode: "class",
};

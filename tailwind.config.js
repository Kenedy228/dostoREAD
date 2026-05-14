module.exports = {
  content: [
    "./src/main/resources/templates/**/*.html",
    "./src/main/resources/static/scripts/**/*.js"
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: "#eef7ff",
          100: "#d8ecff",
          600: "#1768ac",
          700: "#12558e",
          800: "#104a78"
        }
      }
    }
  }
};

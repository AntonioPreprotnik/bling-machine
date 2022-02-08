
module.exports = {  
  content: 
    process.env.NODE_ENV == "production"  
    ? ["resources/public/assets/js/compiled/app.js"]
    : ["resources/public/assets/js/compiled/cljs-runtime/**/*.js"],
  theme: {
    extend: {}
  },  
  plugins: []}
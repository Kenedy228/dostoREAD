import { svelte } from "@sveltejs/vite-plugin-svelte";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";

export default defineConfig({
  root: "src/main/frontend",
  plugins: [svelte(), tailwindcss()],
  build: {
    outDir: "../resources/static",
    emptyOutDir: true,
    assetsDir: "assets"
  },
  server: {
    proxy: {
      "/api": "http://localhost:8080",
      "/logout": "http://localhost:8080"
    }
  }
});

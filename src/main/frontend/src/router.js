import { writable } from "svelte/store";

export const route = writable(parseRoute());

export function parseRoute() {
  return {
    path: window.location.pathname,
    query: new URLSearchParams(window.location.search)
  };
}

export function navigate(path) {
  window.history.pushState({}, "", path);
  route.set(parseRoute());
  window.scrollTo({ top: 0, behavior: "smooth" });
}

window.addEventListener("popstate", () => route.set(parseRoute()));

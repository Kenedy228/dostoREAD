import { writable } from "svelte/store";
import { api } from "./api";

export const session = writable({ authenticated: false, username: null, role: null, loaded: false });

export async function loadSession() {
  const data = await api("/api/auth/session");
  session.set({ ...data, loaded: true });
  return data;
}

export function canUseAdmin(value) {
  return value?.authenticated && value.role === "ADMIN";
}

export function canUseLibrary(value) {
  return value?.authenticated && value.role === "USER";
}

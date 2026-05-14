<script>
  import { BookOpen, LibraryBig, LogOut, Menu, Shield, UserRound, X } from "lucide-svelte";
  import { api } from "../api";
  import { navigate } from "../router";
  import { loadSession } from "../session";

  export let current;
  export let session;

  let open = false;

  const publicLinks = [
    { href: "/", label: "Главная" },
    { href: "/login", label: "Войти" },
    { href: "/registration", label: "Регистрация" }
  ];

  $: links = session?.role === "ADMIN"
    ? [
        { href: "/admin-panel", label: "Админ-панель" },
        { href: "/admin-panel/books/view", label: "Книги" },
        { href: "/admin-panel/accounts/view", label: "Аккаунты" }
      ]
    : session?.role === "USER"
      ? [
          { href: "/all-books", label: "Каталог" },
          { href: "/user-panel", label: "Моя библиотека" }
        ]
      : publicLinks;

  async function logout() {
    await api("/api/auth/logout", { method: "POST" });
    await loadSession();
    navigate("/");
  }
</script>

<header class="sticky top-0 z-40 border-b border-white/60 bg-white/78 backdrop-blur-xl">
  <div class="container flex h-18 items-center justify-between">
    <button class="flex items-center gap-3" on:click={() => navigate("/")} aria-label="На главную">
      <span class="grid h-11 w-11 place-items-center rounded-2xl bg-brand text-white shadow-lg shadow-orange-900/20">
        <BookOpen size={23} />
      </span>
      <span>
        <span class="block text-lg font-black tracking-tight text-ink">dostoREAD</span>
        <span class="hidden text-xs font-semibold text-muted sm:block">электронная библиотека</span>
      </span>
    </button>

    <nav class="hidden items-center gap-1 rounded-2xl border border-slate-200 bg-white/85 p-1 md:flex">
      {#each links as item}
        <button
          class="rounded-xl px-4 py-2 text-sm font-bold transition {current === item.href ? 'bg-ink text-white' : 'text-slate-700 hover:bg-slate-100'}"
          on:click={() => navigate(item.href)}
        >
          {item.label}
        </button>
      {/each}
    </nav>

    <div class="hidden items-center gap-3 md:flex">
      {#if session?.authenticated}
        <span class="badge">
          {#if session.role === "ADMIN"}<Shield size={15} />{:else}<LibraryBig size={15} />{/if}
          <span class="ml-1">{session.username}</span>
        </span>
        <button class="btn btn-secondary" on:click={logout}><LogOut size={17} /> Выйти</button>
      {:else}
        <button class="btn btn-primary" on:click={() => navigate("/login")}><UserRound size={17} /> Войти</button>
      {/if}
    </div>

    <button class="grid h-11 w-11 place-items-center rounded-xl border border-slate-200 bg-white md:hidden" on:click={() => (open = !open)} aria-label="Меню">
      {#if open}<X />{:else}<Menu />{/if}
    </button>
  </div>

  {#if open}
    <div class="container pb-4 md:hidden">
      <div class="panel grid gap-2 rounded-2xl p-3">
        {#each links as item}
          <button class="rounded-xl px-4 py-3 text-left font-bold hover:bg-orange-50" on:click={() => { open = false; navigate(item.href); }}>
            {item.label}
          </button>
        {/each}
        {#if session?.authenticated}
          <button class="btn btn-secondary" on:click={logout}>Выйти</button>
        {/if}
      </div>
    </div>
  {/if}
</header>

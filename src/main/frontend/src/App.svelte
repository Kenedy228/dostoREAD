<script>
  import { onMount } from "svelte";
  import NavBar from "./components/NavBar.svelte";
  import Home from "./pages/Home.svelte";
  import Auth from "./pages/Auth.svelte";
  import Books from "./pages/Books.svelte";
  import BookDetails from "./pages/BookDetails.svelte";
  import Reader from "./pages/Reader.svelte";
  import AdminDashboard from "./pages/AdminDashboard.svelte";
  import AdminBookForm from "./pages/AdminBookForm.svelte";
  import AdminAccounts from "./pages/AdminAccounts.svelte";
  import Forbidden from "./components/Forbidden.svelte";
  import { route, navigate } from "./router";
  import { loadSession, session } from "./session";

  onMount(loadSession);

  $: path = $route.path;
  $: query = $route.query;
  $: user = $session;

  function protectedView(role, render) {
    if (!user.loaded) return "loading";
    if (!user.authenticated) return "login";
    if (role && user.role !== role) return "forbidden";
    return render;
  }
</script>

<div class="shell">
  <NavBar current={path} session={user} />

  {#if !user.loaded}
    <section class="container py-16"><div class="panel rounded-2xl p-8 text-center font-bold text-muted">Загрузка приложения...</div></section>
  {:else if path === "/"}
    <Home session={user} />
  {:else if path === "/login"}
    <Auth mode="login" />
  {:else if path === "/registration" || path === "/registration/email-confirmation"}
    <Auth mode="registration" />
  {:else if path.startsWith("/password-recovery")}
    <Auth mode="recovery" />
  {:else if path === "/all-books"}
    {#if protectedView("USER", "ok") === "ok"}<Books />{:else if protectedView("USER", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else if path === "/user-panel"}
    {#if protectedView("USER", "ok") === "ok"}<Books library />{:else if protectedView("USER", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else if path === "/book-info"}
    {#if protectedView("USER", "ok") === "ok"}<BookDetails id={Number(query.get("id"))} />{:else if protectedView("USER", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else if path === "/read-book"}
    {#if protectedView("USER", "ok") === "ok"}<Reader bookId={Number(query.get("bookID"))} initialPage={Number(query.get("page") || 0)} initialScreen={Number(query.get("screen") || 0)} />{:else if protectedView("USER", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else if path === "/admin-panel"}
    {#if protectedView("ADMIN", "ok") === "ok"}<AdminDashboard />{:else if protectedView("ADMIN", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else if path === "/admin-panel/books/view"}
    {#if protectedView("ADMIN", "ok") === "ok"}<Books admin />{:else if protectedView("ADMIN", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else if path === "/admin-panel/books/create"}
    {#if protectedView("ADMIN", "ok") === "ok"}<AdminBookForm />{:else if protectedView("ADMIN", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else if path === "/admin-panel/books/update"}
    {#if protectedView("ADMIN", "ok") === "ok"}<AdminBookForm id={Number(query.get("id"))} />{:else if protectedView("ADMIN", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else if path === "/admin-panel/accounts/view" || path === "/admin-panel/accounts/create"}
    {#if protectedView("ADMIN", "ok") === "ok"}<AdminAccounts />{:else if protectedView("ADMIN", "ok") === "login"}<Auth mode="login" />{:else}<Forbidden />{/if}
  {:else}
    <section class="container py-16">
      <div class="panel rounded-[2rem] p-10 text-center">
        <h1 class="section-title">Страница не найдена</h1>
        <button class="btn btn-primary mt-6" on:click={() => navigate("/")}>На главную</button>
      </div>
    </section>
  {/if}
</div>

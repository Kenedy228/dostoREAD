<script>
  import { ArrowRight, BookMarked, ShieldCheck, Sparkles } from "lucide-svelte";
  import { navigate } from "../router";

  export let session;
</script>

<section class="container grid min-h-[calc(100vh-72px)] items-center gap-10 py-12 lg:grid-cols-[1.05fr_.95fr]">
  <div class="max-w-3xl">
    <span class="badge mb-6"><Sparkles size={15} /> современная библиотека</span>
    <h1 class="page-title text-ink">dostoREAD</h1>
    <p class="mt-6 max-w-2xl text-xl font-medium leading-8 text-slate-700">
      Удобный сервис для загрузки, каталогизации и чтения EPUB-книг с личной библиотекой, прогрессом чтения и аккуратной админ-панелью.
    </p>
    <div class="mt-8 flex flex-wrap gap-3">
      {#if session?.role === "ADMIN"}
        <button class="btn btn-primary" on:click={() => navigate("/admin-panel")}>Открыть админ-панель <ArrowRight size={18} /></button>
      {:else if session?.role === "USER"}
        <button class="btn btn-primary" on:click={() => navigate("/all-books")}>Перейти в каталог <ArrowRight size={18} /></button>
        <button class="btn btn-secondary" on:click={() => navigate("/user-panel")}>Моя библиотека</button>
      {:else}
        <button class="btn btn-primary" on:click={() => navigate("/login")}>Войти <ArrowRight size={18} /></button>
        <button class="btn btn-secondary" on:click={() => navigate("/registration")}>Создать аккаунт</button>
      {/if}
    </div>
  </div>

  <div class="panel rounded-[2rem] p-5">
    <div class="rounded-[1.5rem] bg-gradient-to-br from-brand via-amber-700 to-brand-2 p-7 text-white">
      <div class="grid gap-5">
        <div class="rounded-2xl bg-white/12 p-5 ring-1 ring-white/20">
          <BookMarked size={34} />
          <h2 class="mt-5 text-3xl font-black">Читайте без лишнего шума</h2>
          <p class="mt-3 text-white/82">Каталог, прогресс, детальная карточка книги и reader в одном интерфейсе.</p>
        </div>
        <div class="grid gap-3 sm:grid-cols-2">
          <div class="rounded-2xl bg-white/12 p-4 ring-1 ring-white/20">
            <p class="text-3xl font-black">EPUB</p>
            <p class="text-sm text-white/80">разбор страниц</p>
          </div>
          <div class="rounded-2xl bg-white/12 p-4 ring-1 ring-white/20">
            <ShieldCheck />
            <p class="mt-3 text-sm text-white/80">роли USER и ADMIN</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

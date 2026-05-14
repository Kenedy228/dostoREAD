<script>
  import { BookPlus, LibraryBig, UsersRound } from "lucide-svelte";
  import { navigate } from "../router";
</script>

<section class="container py-10">
  <span class="badge mb-4">admin</span>
  <h1 class="section-title">Панель управления</h1>
  <div class="mt-8 grid gap-5 md:grid-cols-3">
    <button class="panel rounded-3xl p-6 text-left transition hover:-translate-y-1" on:click={() => navigate("/admin-panel/books/view")}>
      <LibraryBig class="text-brand" size={34} />
      <h2 class="mt-5 text-xl font-black">Книги</h2>
      <p class="mt-2 text-slate-600">Поиск, редактирование и удаление книг.</p>
    </button>
    <button class="panel rounded-3xl p-6 text-left transition hover:-translate-y-1" on:click={() => navigate("/admin-panel/books/create")}>
      <BookPlus class="text-brand-2" size={34} />
      <h2 class="mt-5 text-xl font-black">Добавить книгу</h2>
      <p class="mt-2 text-slate-600">Загрузка EPUB, обложки и метаданных.</p>
    </button>
    <button class="panel rounded-3xl p-6 text-left transition hover:-translate-y-1" on:click={() => navigate("/admin-panel/accounts/view")}>
      <UsersRound class="text-accent" size={34} />
      <h2 class="mt-5 text-xl font-black">Администраторы</h2>
      <p class="mt-2 text-slate-600">Создание и блокировка аккаунтов.</p>
    </button>
  </div>
</section>

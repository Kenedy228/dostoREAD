<script>
  import { onMount } from "svelte";
  import { Search, SlidersHorizontal } from "lucide-svelte";
  import { api } from "../api";
  import BookCard from "../components/BookCard.svelte";
  import Pagination from "../components/Pagination.svelte";

  export let library = false;
  export let admin = false;

  let pageData = null;
  let genres = [];
  let pickedGenres = [];
  let searchQuery = "";
  let loading = true;

  async function load(page = 0) {
    loading = true;
    const params = new URLSearchParams({ page });
    if (searchQuery) params.set("searchQuery", searchQuery);
    pickedGenres.forEach((genre) => params.append("pickedGenres", genre));
    const endpoint = admin ? "/api/admin/books" : library ? "/api/library/books" : "/api/books";
    pageData = await api(`${endpoint}?${params.toString()}`);
    loading = false;
  }

  onMount(async () => {
    if (!library && !admin) genres = await api("/api/genres");
    await load();
  });
</script>

<section class="container py-10">
  <div class="mb-8 flex flex-col justify-between gap-5 md:flex-row md:items-end">
    <div>
      <span class="badge mb-4">{admin ? "admin" : library ? "library" : "catalog"}</span>
      <h1 class="section-title">{admin ? "Управление книгами" : library ? "Моя библиотека" : "Каталог книг"}</h1>
    </div>

    {#if admin}
      <a class="btn btn-primary" href="/admin-panel/books/create">Добавить книгу</a>
    {/if}
  </div>

  {#if !library}
    <div class="panel mb-8 rounded-2xl p-4">
      <form class="grid gap-4 lg:grid-cols-[1fr_auto]" on:submit|preventDefault={() => load(0)}>
        <label>
          <span class="label"><Search size={15} class="inline" /> Поиск</span>
          <input class="field" placeholder="Название книги" bind:value={searchQuery} />
        </label>
        <button class="btn btn-primary self-end">Найти</button>
      </form>

      {#if genres.length}
        <div class="mt-5">
          <p class="label"><SlidersHorizontal size={15} class="inline" /> Жанры</p>
          <div class="flex flex-wrap gap-2">
            {#each genres as genre}
              <label class="badge cursor-pointer">
                <input class="mr-2" type="checkbox" bind:group={pickedGenres} value={genre} on:change={() => load(0)} />
                {genre}
              </label>
            {/each}
          </div>
        </div>
      {/if}
    </div>
  {/if}

  {#if loading}
    <div class="panel rounded-2xl p-8 text-center font-bold text-muted">Загрузка...</div>
  {:else if !pageData?.cards?.length}
    <div class="panel rounded-2xl p-8 text-center font-bold text-muted">Книги не найдены</div>
  {:else}
    <div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
      {#each pageData.cards as book}
        <BookCard {book} {admin}>
          <button
            slot="delete"
            class="btn btn-danger"
            on:click={async () => {
              await api(`/api/admin/books/${book.id}`, { method: "DELETE" });
              await load(pageData.currentPage);
            }}
          >
            Удалить
          </button>
        </BookCard>
      {/each}
    </div>
    <Pagination {pageData} onPage={load} />
  {/if}
</section>

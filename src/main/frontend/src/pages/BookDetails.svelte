<script>
  import { onMount } from "svelte";
  import { BookOpen, Calendar, Layers, UserRound } from "lucide-svelte";
  import { api } from "../api";
  import { navigate } from "../router";

  export let id;

  let book;
  let position = { page: 0, screen: 0 };

  onMount(async () => {
    book = await api(`/api/books/${id}`);
    try {
      position = await api(`/api/books/${id}/reading-position`);
    } catch {
      position = { page: 0, screen: 0 };
    }
  });
</script>

<section class="container py-10">
  {#if !book}
    <div class="panel rounded-2xl p-8 text-center font-bold text-muted">Загрузка...</div>
  {:else}
    <div class="grid gap-8 lg:grid-cols-[360px_1fr]">
      <div class="panel rounded-[2rem] p-4">
        <img class="aspect-[4/5] w-full rounded-[1.5rem] object-cover" src={book.coverUrl} alt={"Обложка " + book.title} />
      </div>
      <div class="panel rounded-[2rem] p-7">
        <div class="flex flex-wrap gap-2">
          {#each book.genreNames as genre}<span class="badge">{genre}</span>{/each}
        </div>
        <h1 class="section-title mt-5">{book.title}</h1>
        <p class="mt-2 flex items-center gap-2 text-lg font-bold text-muted"><UserRound size={18} /> {book.author}</p>
        <p class="mt-6 max-w-3xl text-lg leading-8 text-slate-700">{book.description}</p>
        <div class="mt-7 grid gap-3 sm:grid-cols-3">
          <div class="rounded-2xl bg-orange-50 p-4"><Calendar size={18} /><p class="mt-2 font-bold">{book.publishDate}</p></div>
          <div class="rounded-2xl bg-teal-50 p-4"><Layers size={18} /><p class="mt-2 font-bold">{book.pageCount} стр.</p></div>
          <div class="rounded-2xl bg-slate-100 p-4"><BookOpen size={18} /><p class="mt-2 font-bold">{book.ageRestrictionCode}</p></div>
        </div>
        <button class="btn btn-primary mt-8" on:click={() => navigate(`/read-book?bookID=${book.id}&page=${position.page}&screen=${position.screen}`)}>
          Читать
        </button>
      </div>
    </div>
  {/if}
</section>

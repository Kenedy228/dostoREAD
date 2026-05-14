<script>
  import { ArrowRight } from "lucide-svelte";
  import { navigate } from "../router";

  export let book;
  export let admin = false;
</script>

<article class="group overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-xl">
  <div class="aspect-[4/5] bg-slate-100">
    <img class="h-full w-full object-cover" src={book.coverUrl} alt={"Обложка " + book.title} />
  </div>
  <div class="grid gap-3 p-5">
    <div>
      <h3 class="line-clamp-2 text-lg font-black text-ink">{book.title}</h3>
      <p class="mt-1 text-sm font-semibold text-muted">{book.author}</p>
    </div>

    {#if admin}
      <div class="grid grid-cols-2 gap-2">
        <button class="btn btn-secondary" on:click={() => navigate(`/admin-panel/books/update?id=${book.id}`)}>Изменить</button>
        <slot name="delete" />
      </div>
    {:else}
      <button class="btn btn-primary w-full" on:click={() => navigate(`/book-info?id=${book.id}`)}>
        Подробнее <ArrowRight size={17} />
      </button>
    {/if}
  </div>
</article>

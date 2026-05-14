<script>
  import { onMount } from "svelte";
  import { ChevronLeft, ChevronRight, X } from "lucide-svelte";
  import { api } from "../api";
  import { navigate } from "../router";

  export let bookId;
  export let initialPage = 0;
  export let initialScreen = 0;

  let page;
  let font = "Inter";
  let screen = initialScreen;
  let data;

  async function load(next = initialPage) {
    page = Math.max(0, next);
    data = await api(`/api/books/${bookId}/read?page=${page}&screen=${screen}&font=${encodeURIComponent(font)}`);
  }

  async function exit() {
    await api(`/api/books/${bookId}/progress?page=${data?.currentPage || 0}&screen=${screen}`, { method: "POST" });
    navigate("/user-panel");
  }

  onMount(() => load(initialPage));
</script>

<section class="min-h-[calc(100vh-72px)] bg-[#fbf7ef] py-6">
  <div class="container">
    <div class="mb-5 flex flex-wrap items-center justify-between gap-3">
      <div class="flex gap-2">
        <select class="field w-40" bind:value={font}>
          <option>Inter</option><option>Georgia</option><option>Arial</option>
        </select>
        <select class="field w-44" bind:value={screen} on:change={() => load(data?.currentPage || 0)}>
          <option value="0">Стандарт</option><option value="1">Компактно</option><option value="2">Широко</option>
        </select>
      </div>
      <button class="btn btn-secondary" on:click={exit}><X size={17} /> Закрыть</button>
    </div>

    <article class="panel mx-auto min-h-[62vh] max-w-4xl rounded-[2rem] p-8 md:p-12" style={`font-family:${font}`}>
      {#if !data}
        <p class="text-center font-bold text-muted">Загрузка страницы...</p>
      {:else}
        <div class="space-y-5 text-xl leading-9 text-slate-900">
          {#each data.lines as line}<p>{line}</p>{/each}
        </div>
      {/if}
    </article>

    {#if data}
      <div class="mx-auto mt-6 flex max-w-4xl items-center justify-between">
        <button class="btn btn-secondary" disabled={data.currentPage === 0} on:click={() => load(data.currentPage - 1)}><ChevronLeft /> Назад</button>
        <span class="badge">{data.currentPage + 1} / {data.totalPages}</span>
        <button class="btn btn-primary" disabled={data.currentPage >= data.totalPages - 1} on:click={() => load(data.currentPage + 1)}>Вперед <ChevronRight /></button>
      </div>
    {/if}
  </div>
</section>

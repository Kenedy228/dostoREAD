<script>
  export let pageData;
  export let onPage;
</script>

{#if pageData?.totalPages > 1}
  <div class="mt-10 flex flex-wrap justify-center gap-2">
    {#each Array(pageData.totalPages) as _, index}
      <button
        class="h-10 min-w-10 rounded-xl px-3 text-sm font-black {index === pageData.currentPage ? 'bg-ink text-white' : 'bg-white text-slate-700 ring-1 ring-slate-200'}"
        on:click={() => onPage(index)}
      >
        {index + 1}
      </button>
    {/each}
  </div>
{/if}

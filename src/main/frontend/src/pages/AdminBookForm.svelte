<script>
  import { onMount } from "svelte";
  import Errors from "../components/Errors.svelte";
  import { api, toFormData } from "../api";
  import { navigate } from "../router";

  export let id = null;

  let genres = [];
  let errors = [];
  let busy = false;
  let values = {
    title: "", author: "", genresList: [], description: "", ageRestriction: "NOT_SELECTED",
    license: "", publishDate: "", publisher: "", pageCount: 1, bookFile: null, coverFile: null
  };

  onMount(async () => {
    genres = await api("/api/genres");
    if (id) {
      const book = await api(`/api/books/${id}`);
      values = {
        ...values,
        title: book.title,
        author: book.author,
        genresList: book.genreNames,
        description: book.description,
        ageRestriction: "NOT_SELECTED",
        license: book.license,
        publishDate: book.publishDate,
        publisher: book.publisher,
        pageCount: book.pageCount
      };
    }
  });

  async function submit() {
    errors = [];
    busy = true;
    try {
      const body = toFormData(values);
      await api(id ? `/api/admin/books/${id}` : "/api/admin/books", { method: id ? "PUT" : "POST", body });
      navigate("/admin-panel/books/view");
    } catch (error) {
      errors = error.errors || [error.message];
    } finally {
      busy = false;
    }
  }
</script>

<section class="container py-10">
  <div class="panel mx-auto max-w-4xl rounded-[2rem] p-7">
    <span class="badge mb-4">book form</span>
    <h1 class="section-title">{id ? "Редактировать книгу" : "Добавить книгу"}</h1>
    <form class="mt-8 grid gap-5" on:submit|preventDefault={submit}>
      <Errors {errors} />
      <div class="grid gap-5 md:grid-cols-2">
        <label><span class="label">Название</span><input class="field" bind:value={values.title} /></label>
        <label><span class="label">Автор</span><input class="field" bind:value={values.author} /></label>
      </div>
      <label><span class="label">Описание</span><textarea class="field min-h-32" bind:value={values.description}></textarea></label>
      <div class="grid gap-5 md:grid-cols-3">
        <label><span class="label">Возраст</span><select class="field" bind:value={values.ageRestriction}>
          <option value="NOT_SELECTED">Выберите</option><option value="ZERO_PLUS">0+</option><option value="SIX_PLUS">6+</option><option value="TWELVE_PLUS">12+</option><option value="SIXTEEN_PLUS">16+</option><option value="EIGHTEEN_PLUS">18+</option>
        </select></label>
        <label><span class="label">Дата публикации</span><input class="field" type="date" bind:value={values.publishDate} /></label>
        <label><span class="label">Страниц</span><input class="field" type="number" min="1" bind:value={values.pageCount} /></label>
      </div>
      <div class="grid gap-5 md:grid-cols-2">
        <label><span class="label">Лицензия</span><input class="field" bind:value={values.license} /></label>
        <label><span class="label">Издательство</span><input class="field" bind:value={values.publisher} /></label>
      </div>
      <div>
        <p class="label">Жанры</p>
        <div class="flex flex-wrap gap-2">
          {#each genres as genre}
            <label class="badge cursor-pointer"><input class="mr-2" type="checkbox" bind:group={values.genresList} value={genre} />{genre}</label>
          {/each}
        </div>
      </div>
      <div class="grid gap-5 md:grid-cols-2">
        <label><span class="label">EPUB файл</span><input class="field" type="file" accept=".epub" on:change={(e) => (values.bookFile = e.currentTarget.files[0])} /></label>
        <label><span class="label">Обложка</span><input class="field" type="file" accept=".png,.jpg,.jpeg" on:change={(e) => (values.coverFile = e.currentTarget.files[0])} /></label>
      </div>
      <button class="btn btn-primary" disabled={busy}>{busy ? "Сохраняю..." : "Сохранить"}</button>
    </form>
  </div>
</section>

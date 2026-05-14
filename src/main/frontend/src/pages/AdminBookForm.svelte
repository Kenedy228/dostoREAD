<script>
  import { onMount } from "svelte";
  import Errors from "../components/Errors.svelte";
  import { api, toFormData } from "../api";
  import { navigate } from "../router";

  export let id = null;

  const BOOK_FILE_LIMIT_MB = 20;
  const COVER_FILE_LIMIT_MB = 10;
  const BOOK_FILE_LIMIT_BYTES = BOOK_FILE_LIMIT_MB * 1024 * 1024;
  const COVER_FILE_LIMIT_BYTES = COVER_FILE_LIMIT_MB * 1024 * 1024;
  const DEFAULT_GENRES = [
    "Фантастика",
    "Фэнтези",
    "Детектив",
    "Роман",
    "Приключения",
    "Драма",
    "Триллер",
    "Классика",
    "История",
    "Научная фантастика",
    "Боевик",
    "Ужасы"
  ];

  let genres = [];
  let errors = [];
  let genreLoadError = "";
  let busy = false;
  let values = {
    title: "", author: "", genresList: [], description: "", ageRestriction: "NOT_SELECTED",
    license: "", publishDate: "", publisher: "", pageCount: 1, bookFile: null, coverFile: null
  };

  onMount(async () => {
    try {
      genres = await api("/api/genres");
      if (!genres.length) {
        genres = DEFAULT_GENRES;
      }
    } catch (error) {
      genres = DEFAULT_GENRES;
      genreLoadError = error.errors?.length ? error.errors[0] : "Не удалось загрузить жанры, используется список по умолчанию.";
    }

    if (id) {
      const book = await api(`/api/books/${id}`);
      values = {
        ...values,
        title: book.title,
        author: book.author,
        genresList: book.genreNames,
        description: book.description,
        ageRestriction: toSelectAgeRestriction(book.ageRestrictionCode),
        license: book.license,
        publishDate: book.publishDate,
        publisher: book.publisher,
        pageCount: book.pageCount
      };
    }
  });

  function validateFileSizes() {
    const fileErrors = [];

    if (values.bookFile && values.bookFile.size > BOOK_FILE_LIMIT_BYTES) {
      fileErrors.push(`Файл книги должен быть не больше ${BOOK_FILE_LIMIT_MB} МБ`);
    }

    if (values.coverFile && values.coverFile.size > COVER_FILE_LIMIT_BYTES) {
      fileErrors.push(`Файл обложки должен быть не больше ${COVER_FILE_LIMIT_MB} МБ`);
    }

    return fileErrors;
  }

  function toSelectAgeRestriction(code) {
    return {
      "0+": "PG_0",
      "6+": "PG_6",
      "12+": "PG_12",
      "18+": "PG_18"
    }[code] || "NOT_SELECTED";
  }

  async function submit() {
    errors = [];

    const fileErrors = validateFileSizes();
    if (fileErrors.length > 0) {
      errors = fileErrors;
      return;
    }

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
          <option value="NOT_SELECTED">Выберите</option><option value="PG_0">0+</option><option value="PG_6">6+</option><option value="PG_12">12+</option><option value="PG_18">18+</option>
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
        {#if genreLoadError}
          <p class="mb-3 rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-semibold text-amber-800">{genreLoadError}</p>
        {/if}
        {#if genres.length}
          <div class="flex flex-wrap gap-2">
            {#each genres as genre}
              <label class="badge cursor-pointer"><input class="mr-2" type="checkbox" bind:group={values.genresList} value={genre} />{genre}</label>
            {/each}
          </div>
        {:else}
          <p class="text-sm text-slate-500">Жанры загружаются. Если список пуст, обновите страницу после запуска приложения.</p>
        {/if}
      </div>
      <div class="grid gap-5 md:grid-cols-2">
        <label>
          <span class="label">EPUB файл</span>
          <input class="field" type="file" accept=".epub" on:change={(e) => (values.bookFile = e.currentTarget.files[0])} />
          <p class="mt-2 text-xs text-slate-500">Максимальный размер файла книги: 20 МБ.</p>
        </label>
        <label>
          <span class="label">Обложка</span>
          <input class="field" type="file" accept=".png,.jpg,.jpeg" on:change={(e) => (values.coverFile = e.currentTarget.files[0])} />
          <p class="mt-2 text-xs text-slate-500">Максимальный размер обложки: 10 МБ.</p>
        </label>
      </div>
      <button class="btn btn-primary" disabled={busy}>{busy ? "Сохраняю..." : "Сохранить"}</button>
    </form>
  </div>
</section>

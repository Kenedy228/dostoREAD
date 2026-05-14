<script>
  import { onMount } from "svelte";
  import Errors from "../components/Errors.svelte";
  import { api, toFormData } from "../api";

  let accounts = [];
  let username = "";
  let rawPassword = "";
  let errors = [];

  async function load() {
    accounts = await api("/api/admin/accounts");
  }

  async function create() {
    errors = [];
    try {
      await api("/api/admin/accounts", { method: "POST", body: toFormData({ username, rawPassword }) });
      username = "";
      rawPassword = "";
      await load();
    } catch (error) {
      errors = error.errors || [error.message];
    }
  }

  async function toggle(account) {
    const action = account.enabled ? "disable" : "enable";
    await api(`/api/admin/accounts/${action}?userId=${account.id}`, { method: "PATCH" });
    await load();
  }

  onMount(load);
</script>

<section class="container grid gap-8 py-10 lg:grid-cols-[380px_1fr]">
  <div class="panel h-fit rounded-[2rem] p-7">
    <span class="badge mb-4">accounts</span>
    <h1 class="section-title">Новый администратор</h1>
    <form class="mt-6 grid gap-4" on:submit|preventDefault={create}>
      <Errors {errors} />
      <label><span class="label">Имя пользователя</span><input class="field" bind:value={username} /></label>
      <label><span class="label">Пароль</span><input class="field" type="password" bind:value={rawPassword} /></label>
      <button class="btn btn-primary">Создать</button>
    </form>
  </div>

  <div class="panel rounded-[2rem] p-7">
    <h2 class="text-2xl font-black">Администраторы</h2>
    <div class="mt-6 grid gap-3">
      {#each accounts as account}
        <div class="flex items-center justify-between rounded-2xl border border-slate-200 bg-white p-4">
          <div>
            <p class="font-black">{account.username}</p>
            <p class="text-sm font-semibold {account.enabled ? 'text-teal-700' : 'text-red-700'}">{account.enabled ? "Активен" : "Заблокирован"}</p>
          </div>
          <button class="btn {account.enabled ? 'btn-danger' : 'btn-secondary'}" on:click={() => toggle(account)}>
            {account.enabled ? "Заблокировать" : "Разблокировать"}
          </button>
        </div>
      {/each}
    </div>
  </div>
</section>

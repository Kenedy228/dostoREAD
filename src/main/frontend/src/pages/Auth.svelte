<script>
  import Errors from "../components/Errors.svelte";
  import { api } from "../api";
  import { loadSession } from "../session";
  import { navigate } from "../router";

  export let mode = "login";

  let username = "";
  let email = "";
  let rawPassword = "";
  let rawPasswordConfirmation = "";
  let enteredCode = "";
  let step = "form";
  let errors = [];
  let busy = false;

  $: title = mode === "login" ? "Вход" : mode === "registration" ? "Регистрация" : "Восстановление пароля";

  async function submit() {
    errors = [];
    busy = true;
    try {
      if (mode === "login") {
        await api("/api/auth/login", { method: "POST", body: JSON.stringify({ username, rawPassword }) });
        const session = await loadSession();
        navigate(session.role === "ADMIN" ? "/admin-panel" : "/all-books");
      } else if (mode === "registration" && step === "form") {
        await api("/api/auth/registration/start", { method: "POST", body: JSON.stringify({ username, email, rawPassword, rawPasswordConfirmation }) });
        step = "code";
      } else if (mode === "registration") {
        await api("/api/auth/registration/confirm", { method: "POST", body: JSON.stringify({ enteredCode }) });
        navigate("/login");
      } else if (mode === "recovery" && step === "form") {
        await api("/api/auth/password-recovery/start", { method: "POST", body: JSON.stringify({ email }) });
        step = "code";
      } else if (mode === "recovery" && step === "code") {
        await api("/api/auth/password-recovery/confirm", { method: "POST", body: JSON.stringify({ enteredCode }) });
        step = "password";
      } else {
        await api("/api/auth/password-recovery/change", { method: "POST", body: JSON.stringify({ rawPassword, rawPasswordConfirmation }) });
        navigate("/login");
      }
    } catch (error) {
      errors = error.errors || [error.message];
    } finally {
      busy = false;
    }
  }
</script>

<section class="container grid min-h-[calc(100vh-72px)] place-items-center py-12">
  <div class="panel w-full max-w-xl rounded-[2rem] p-7">
    <div class="mb-8">
      <p class="badge mb-4">{mode === "login" ? "session" : "account"}</p>
      <h1 class="section-title">{title}</h1>
      <p class="mt-3 text-slate-600">
        {#if mode === "login"}Войдите, чтобы продолжить чтение или администрирование.{:else if step === "code"}Введите код подтверждения из письма.{:else}Заполните форму, поля проверяются на сервере.{/if}
      </p>
    </div>

    <form class="grid gap-5" on:submit|preventDefault={submit}>
      <Errors {errors} />

      {#if mode !== "recovery" && step === "form"}
        <label><span class="label">Имя пользователя</span><input class="field" bind:value={username} /></label>
      {/if}

      {#if (mode === "registration" || mode === "recovery") && step === "form"}
        <label><span class="label">Email</span><input class="field" type="email" bind:value={email} /></label>
      {/if}

      {#if mode === "login" || step === "form" && mode === "registration" || step === "password"}
        <label><span class="label">Пароль</span><input class="field" type="password" bind:value={rawPassword} /></label>
      {/if}

      {#if mode === "registration" && step === "form" || step === "password"}
        <label><span class="label">Подтверждение пароля</span><input class="field" type="password" bind:value={rawPasswordConfirmation} /></label>
      {/if}

      {#if step === "code"}
        <label><span class="label">Код подтверждения</span><input class="field" bind:value={enteredCode} /></label>
      {/if}

      <button class="btn btn-primary w-full" disabled={busy}>{busy ? "Подождите..." : "Продолжить"}</button>

      {#if mode === "login"}
        <button type="button" class="text-sm font-bold text-brand" on:click={() => navigate("/password-recovery")}>Забыли пароль?</button>
      {/if}
    </form>
  </div>
</section>

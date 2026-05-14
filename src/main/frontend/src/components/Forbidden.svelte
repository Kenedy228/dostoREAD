<script>
  import { navigate } from "../router";
</script>

<section class="container py-16">
  <div class="panel mx-auto max-w-2xl rounded-[2rem] p-10 text-center">
    <h1 class="section-title">Доступ закрыт</h1>
    <p class="mt-4 text-slate-600">У вашей учетной записи нет прав для этого раздела.</p>
    <button class="btn btn-primary mt-6" on:click={() => navigate("/")}>На главную</button>
  </div>
</section>

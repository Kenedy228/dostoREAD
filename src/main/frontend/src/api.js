export async function api(path, options = {}) {
  const headers = options.body instanceof FormData ? {} : { "Content-Type": "application/json" };
  const response = await fetch(path, {
    credentials: "include",
    ...options,
    headers: { ...headers, ...(options.headers || {}) }
  });

  const payload = await response.json().catch(() => ({ ok: false, errors: ["Сервер вернул некорректный ответ"] }));

  if (!response.ok || payload.ok === false) {
    const error = new Error((payload.errors || ["Ошибка запроса"]).join("\n"));
    error.errors = payload.errors || [error.message];
    throw error;
  }

  return payload.data;
}

export function toFormData(values) {
  const formData = new FormData();

  Object.entries(values).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") return;
    if (Array.isArray(value)) {
      value.forEach((item) => formData.append(key, item));
      return;
    }
    formData.append(key, value);
  });

  return formData;
}

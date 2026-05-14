export async function api(path, options = {}) {
  const headers = options.body instanceof FormData ? {} : { "Content-Type": "application/json" };
  const response = await fetch(path, {
    credentials: "include",
    ...options,
    headers: { ...headers, ...(options.headers || {}) }
  });

  const rawBody = await response.text();
  let payload = null;

  if (rawBody) {
    try {
      payload = JSON.parse(rawBody);
    } catch {
      payload = null;
    }
  }

  if (!response.ok || payload?.ok === false) {
    const errors = payload?.errors?.length
      ? payload.errors
      : [fallbackErrorMessage(response.status, rawBody)];
    const error = new Error(errors.join("\n"));
    error.errors = errors;
    throw error;
  }

  return payload.data;
}

function fallbackErrorMessage(status, rawBody) {
  if (status === 413) {
    return "Файл слишком большой. Обложка должна быть не больше 10 МБ, книга — не больше 20 МБ.";
  }

  if (rawBody && rawBody.trim()) {
    return rawBody.trim();
  }

  if (status === 400) {
    return "Запрос не прошел проверку";
  }

  return "Ошибка запроса";
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

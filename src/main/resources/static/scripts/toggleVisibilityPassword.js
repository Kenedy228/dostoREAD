let icon = document.querySelector("#show_hide_password");
let passwordFields = document.querySelectorAll("input[type='password']");

icon.addEventListener("click", (e) => {
    e.preventDefault();

    if (icon.classList.contains("hide")) {
        icon.classList.remove("hide");
        icon.classList.add("show");
        icon.src = "http://localhost:8080/assets/show.png";
        show();
    } else if (icon.classList.contains("show")) {
        icon.classList.remove("show");
        icon.classList.add("hide");
        icon.src = "http://localhost:8080/assets/hide.png";
        hide();
    }
})
function show() {
    passwordFields.forEach(field => {
        field.type = "text";
    })
}

function hide() {
    passwordFields.forEach(field => {
        field.type = "password";
    })
}
const toggleBtn = document.querySelector(".toggleVisibility");
const form = document.querySelector(".old-book");

toggleBtn.addEventListener("click", (e)=> {
    e.preventDefault();

    if (form.classList.contains("show")) {
        hide(form);
    } else if (form.classList.contains("hide")) {
        show(form);
    }
})

function hide(form ){
    form.classList.remove("show");
    form.classList.add("hide");
    form.style.display = "none";
}

function show(form) {
    form.classList.remove("hide");
    form.classList.add("show");
    form.style.display = "block";
}
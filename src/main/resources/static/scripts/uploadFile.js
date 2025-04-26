let bookFile = document.querySelector("#bookFile");
let bookFileHandler = document.querySelector("#upload-book-text");

let bookCover = document.querySelector("#bookCover");
let bookCoverHandler = document.querySelector("#upload-cover-text");

bookFile.addEventListener("change", (e) => {
    validate(e.target, bookFileHandler, "Файл книги: ");
})

bookCover.addEventListener("change", (e) => {
    validate(e.target, bookCoverHandler, "Обложка книги: ");
})

function validate(target, handler, text) {
    const files  = target.files;

    if (!files || files.length == 0) {
        handler.textContent = "Файл не выбран";
        return;
    }

    const file = files[0];
    handler.textContent = text + `${file.name}`;
}
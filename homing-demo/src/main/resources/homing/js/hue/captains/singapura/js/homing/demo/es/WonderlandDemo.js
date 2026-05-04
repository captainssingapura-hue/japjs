function appMain(rootElement) {
    const h1 = document.createElement("h1");
    h1.textContent = "Wonderland Demo";
    rootElement.appendChild(h1);

    const p1 = document.createElement("p");
    rootElement.appendChild(p1);

    const p2 = document.createElement("p");
    rootElement.appendChild(p2);

    const gallery = document.createElement("div");
    gallery.style.cssText = "display: flex; gap: 16px; flex-wrap: wrap; margin-top: 16px;";
    rootElement.appendChild(gallery);

    const bob = new Bob();
    p1.textContent = bob.test1();
    p2.textContent = bob.test2();

    for (const svg of bob.wonderlandCharacters()) {
        gallery.appendChild(svg);
    }
}

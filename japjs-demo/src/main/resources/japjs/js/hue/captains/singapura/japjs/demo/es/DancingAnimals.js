function appMain(rootElement) {
    const h1 = document.createElement("h1");
    css.setClass(h1, subway_title);
    h1.textContent = "Dancing Animals";
    rootElement.appendChild(h1);

    const hint = document.createElement("p");
    css.setClass(hint, subway_hint);
    hint.textContent = "Press \u2190 or \u2192 arrow keys to make them dance! Space to jump.";
    rootElement.appendChild(hint);

    const grid = document.createElement("div");
    css.setClass(grid, subway_grid);
    rootElement.appendChild(grid);

    const GRAVITY = 1.8;
    const JUMP_STRENGTH = 8;

    const cells = [];
    const reversed = [];
    const physicsArr = [];
    const offsets = [];

    for (let i = 0; i < 25; i++) {
        const cell = createAnimalCell(css.className(subway_cell));
        grid.appendChild(cell);
        cells.push(cell);
        reversed.push(Math.random() < 0.5);
        physicsArr.push(createJumpPhysics(GRAVITY, JUMP_STRENGTH));
        offsets.push(0);
    }

    document.addEventListener("keydown", (e) => {
        if (e.key === "ArrowLeft") {
            cells.forEach((cell, i) => cell.style.transform = reversed[i] ? "scaleX(1)" : "scaleX(-1)");
        } else if (e.key === "ArrowRight") {
            cells.forEach((cell, i) => cell.style.transform = reversed[i] ? "scaleX(-1)" : "scaleX(1)");
        } else if (e.key === " ") {
            e.preventDefault();
            physicsArr.forEach((p) => p.jump());
        }
    });

    function frame() {
        for (let i = 0; i < cells.length; i++) {
            offsets[i] = physicsArr[i].update(offsets[i], 0);
            cells[i].style.marginTop = offsets[i] + "px";
        }
        requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);
}

function appMain(rootElement) {
    const h1 = document.createElement("h1");
    css.setClass(h1, spin_title);
    h1.textContent = "Spinning Animals";
    rootElement.appendChild(h1);

    const hint = document.createElement("p");
    css.setClass(hint, spin_hint);
    hint.textContent = "Click an animal to pause/resume it. Use the controls to adjust speed and direction.";
    rootElement.appendChild(hint);

    const controls = document.createElement("div");
    css.setClass(controls, spin_controls);

    const speedLabel = document.createElement("label");
    speedLabel.textContent = "Speed ";
    const speedSlider = document.createElement("input");
    speedSlider.type = "range";
    speedSlider.min = "1";
    speedSlider.max = "20";
    speedSlider.value = "5";
    speedLabel.appendChild(speedSlider);

    const reverseBtn = document.createElement("button");
    reverseBtn.textContent = "Reverse";

    var selector = createAnimalSelector();

    controls.appendChild(speedLabel);
    controls.appendChild(reverseBtn);
    controls.appendChild(selector);
    rootElement.appendChild(controls);

    const grid = document.createElement("div");
    css.setClass(grid, spin_grid);
    rootElement.appendChild(grid);

    let direction = 1;
    let speed = 5;
    const COUNT = 12;
    const cells = [];
    const angles = [];
    const pausedState = [];
    const offsets = [];

    for (let i = 0; i < COUNT; i++) {
        const cell = createAnimalCell(css.className(spin_cell));
        grid.appendChild(cell);
        cells.push(cell);
        angles.push(0);
        pausedState.push(false);
        offsets.push((Math.random() - 0.5) * 0.6);

        cell.addEventListener("click", () => {
            pausedState[i] = !pausedState[i];
            css.toggleClass(cell, paused, pausedState[i]);
        });
    }

    reverseBtn.addEventListener("click", () => {
        direction *= -1;
    });

    speedSlider.addEventListener("input", (e) => {
        speed = parseInt(e.target.value);
    });

    let last = 0;
    function frame(ts) {
        const dt = last ? (ts - last) / 1000 : 0;
        last = ts;
        for (let i = 0; i < COUNT; i++) {
            if (pausedState[i]) continue;
            const rate = speed * (1 + offsets[i]);
            angles[i] += direction * rate * 360 * dt;
            cells[i].style.transform = "rotate(" + angles[i] + "deg)";
        }
        requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);
}

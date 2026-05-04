const allAnimals = [
    { name: "Turtle", svg: turtle },
    { name: "Ghost", svg: ghost },
    { name: "Broom", svg: broom },
    { name: "Penguin", svg: penguin },
    { name: "Crocodile", svg: crocodile },
    { name: "Whale", svg: whale }
];

var selectedIndex = 0;
var liveCells = [];

function getAnimalSvg() {
    if (selectedIndex < 0) {
        return allAnimals[Math.floor(Math.random() * allAnimals.length)].svg;
    }
    return allAnimals[selectedIndex].svg;
}

function refreshCells() {
    for (var i = 0; i < liveCells.length; i++) {
        var wrapper = liveCells[i].querySelector("div");
        if (wrapper) {
            wrapper.innerHTML = selectedIndex < 0
                ? allAnimals[Math.floor(Math.random() * allAnimals.length)].svg
                : allAnimals[selectedIndex].svg;
        }
    }
}

function createAnimalCell(className) {
    var cell = document.createElement("div");
    cell.className = className;
    var wrapper = document.createElement("div");
    wrapper.innerHTML = getAnimalSvg();
    cell.appendChild(wrapper);
    liveCells.push(cell);
    return cell;
}

function createAnimalSelector() {
    var container = document.createElement("label");
    container.textContent = "Animal ";
    var select = document.createElement("select");

    var randomOpt = document.createElement("option");
    randomOpt.value = "-1";
    randomOpt.textContent = "Random";
    select.appendChild(randomOpt);

    for (var i = 0; i < allAnimals.length; i++) {
        var opt = document.createElement("option");
        opt.value = String(i);
        opt.textContent = allAnimals[i].name;
        if (i === selectedIndex) opt.selected = true;
        select.appendChild(opt);
    }

    select.addEventListener("change", function () {
        selectedIndex = parseInt(select.value);
        refreshCells();
    });

    container.appendChild(select);
    return container;
}

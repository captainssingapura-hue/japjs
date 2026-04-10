const animals = [turtle];

function createAnimalCell(className) {
    const cell = document.createElement("div");
    cell.className = className;
    const wrapper = document.createElement("div");
    wrapper.innerHTML = animals[Math.floor(Math.random() * animals.length)];
    cell.appendChild(wrapper);
    return cell;
}

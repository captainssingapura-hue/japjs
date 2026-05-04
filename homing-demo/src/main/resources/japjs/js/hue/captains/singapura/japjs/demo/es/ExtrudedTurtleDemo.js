function appMain(rootElement) {
    // --- all available SVGs ---
    const animals = { turtle, ghost, broom, penguin, crocodile, whale };
    let currentAnimal = "turtle";

    // --- scene setup ---
    const scene = new Scene();
    scene.background = new Color(0x1a1a2e);

    const camera = new PerspectiveCamera(50, 1, 0.1, 100);
    camera.position.set(0, 0, 8);
    camera.lookAt(0, 0, 0);

    const renderer = new WebGLRenderer({ antialias: true });
    renderer.setPixelRatio(window.devicePixelRatio);

    // lighting
    const ambient = new AmbientLight(0xffffff, 0.6);
    scene.add(ambient);
    const keyLight = new DirectionalLight(0xffffff, 1.0);
    keyLight.position.set(4, 4, 8);
    scene.add(keyLight);
    const fillLight = new DirectionalLight(0x6688cc, 0.3);
    fillLight.position.set(-4, -2, 4);
    scene.add(fillLight);

    // --- coin state ---
    let coinThickness = 0.3;
    const coinMat = new MeshStandardMaterial({ color: 0xd4a843, metalness: 0.7, roughness: 0.3 });
    const coin = new Group();
    let coinBody = null;
    let frontFace = null;
    let backFace = null;
    let coinRadius = 2;

    scene.add(coin);

    function buildSvgFaces(svgString) {
        // remove old faces
        if (frontFace) { coin.remove(frontFace); }
        if (backFace) { coin.remove(backFace); }

        const svgShapes = extrudeSvg(svgString, { scale: 0.01 });
        svgShapes.updateMatrixWorld(true);

        const svgBox = new Box3().setFromObject(svgShapes);
        const svgSize = svgBox.getSize(new Vector3());
        const svgCenter = svgBox.getCenter(new Vector3());

        coinRadius = Math.max(svgSize.x, svgSize.y) * 0.6;

        // center x/y but keep z layering relative to 0
        svgShapes.position.set(-svgCenter.x, -svgCenter.y, 0);

        frontFace = svgShapes;
        frontFace.position.z = coinThickness / 2 + 0.01;
        coin.add(frontFace);

        backFace = svgShapes.clone();
        backFace.scale.z *= -1;
        backFace.position.z = -(coinThickness / 2 + 0.01);
        coin.add(backFace);

        rebuildCoinBody();
    }

    function createCoinBody(radius, thickness) {
        const geo = new CylinderGeometry(radius, radius, thickness, 64);
        const mesh = new Mesh(geo, coinMat);
        mesh.rotation.x = Math.PI / 2;
        return mesh;
    }

    function rebuildCoinBody() {
        if (coinBody) {
            coin.remove(coinBody);
            coinBody.geometry.dispose();
        }
        coinBody = createCoinBody(coinRadius, coinThickness);
        coin.add(coinBody);
        if (frontFace) frontFace.position.z = coinThickness / 2 + 0.01;
        if (backFace) backFace.position.z = -(coinThickness / 2 + 0.01);
    }

    // initial build
    buildSvgFaces(animals[currentAnimal]);

    // --- controls ---
    const controls = document.createElement("div");
    controls.style.cssText = "padding:12px;display:flex;align-items:center;gap:16px;font-family:sans-serif;color:#ccc;flex-wrap:wrap;";

    // animal selector
    const selectLabel = document.createElement("label");
    selectLabel.textContent = "Animal ";
    selectLabel.style.whiteSpace = "nowrap";
    const select = document.createElement("select");
    select.style.cssText = "padding:4px 8px;font-size:14px;";
    for (const name of Object.keys(animals)) {
        const opt = document.createElement("option");
        opt.value = name;
        opt.textContent = name;
        if (name === currentAnimal) opt.selected = true;
        select.appendChild(opt);
    }
    select.addEventListener("change", (e) => {
        currentAnimal = e.target.value;
        buildSvgFaces(animals[currentAnimal]);
    });
    selectLabel.appendChild(select);
    controls.appendChild(selectLabel);

    // thickness slider
    const thickLabel = document.createElement("label");
    thickLabel.textContent = "Thickness ";
    thickLabel.style.whiteSpace = "nowrap";
    const slider = document.createElement("input");
    slider.type = "range";
    slider.min = "0.05";
    slider.max = "2.0";
    slider.step = "0.05";
    slider.value = String(coinThickness);
    slider.style.width = "160px";
    const display = document.createElement("span");
    display.textContent = coinThickness.toFixed(2);
    display.style.minWidth = "3em";

    slider.addEventListener("input", (e) => {
        coinThickness = parseFloat(e.target.value);
        display.textContent = coinThickness.toFixed(2);
        rebuildCoinBody();
    });

    thickLabel.appendChild(slider);
    controls.appendChild(thickLabel);
    controls.appendChild(display);

    // zoom slider
    let camZ = 8;
    const zoomLabel = document.createElement("label");
    zoomLabel.textContent = "Zoom ";
    zoomLabel.style.whiteSpace = "nowrap";
    const zoomSlider = document.createElement("input");
    zoomSlider.type = "range";
    zoomSlider.min = "3";
    zoomSlider.max = "16";
    zoomSlider.step = "0.5";
    zoomSlider.value = String(camZ);
    zoomSlider.style.width = "160px";
    // slider direction: left = close, right = far → invert for intuitive zoom
    zoomSlider.style.direction = "rtl";

    zoomSlider.addEventListener("input", (e) => {
        camZ = parseFloat(e.target.value);
        camera.position.z = camZ;
    });

    zoomLabel.appendChild(zoomSlider);
    controls.appendChild(zoomLabel);

    rootElement.appendChild(controls);
    rootElement.appendChild(renderer.domElement);

    // --- sizing ---
    function resize() {
        const width = Math.min(rootElement.clientWidth, 800);
        const height = Math.round(width * 0.75);
        renderer.setSize(width, height);
        camera.aspect = width / height;
        camera.updateProjectionMatrix();
    }
    resize();
    window.addEventListener("resize", resize);

    // --- drag-to-rotate ---
    let dragging = false;
    let prevX = 0;
    let prevY = 0;
    let rotY = 0;
    let rotX = 0;

    renderer.domElement.addEventListener("pointerdown", (e) => {
        dragging = true;
        prevX = e.clientX;
        prevY = e.clientY;
        renderer.domElement.setPointerCapture(e.pointerId);
    });
    renderer.domElement.addEventListener("pointermove", (e) => {
        if (!dragging) return;
        rotY += (e.clientX - prevX) * 0.01;
        rotX += (e.clientY - prevY) * 0.01;
        rotX = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, rotX));
        prevX = e.clientX;
        prevY = e.clientY;
    });
    renderer.domElement.addEventListener("pointerup", () => { dragging = false; });

    // mouse wheel zoom
    renderer.domElement.addEventListener("wheel", (e) => {
        e.preventDefault();
        camZ = Math.max(3, Math.min(16, camZ + e.deltaY * 0.01));
        camera.position.z = camZ;
        zoomSlider.value = String(camZ);
    }, { passive: false });

    // --- animate ---
    function animate() {
        requestAnimationFrame(animate);
        if (!dragging) {
            rotY += 0.008;
        }
        coin.rotation.y = rotY;
        coin.rotation.x = rotX;
        renderer.render(scene, camera);
    }
    animate();
}

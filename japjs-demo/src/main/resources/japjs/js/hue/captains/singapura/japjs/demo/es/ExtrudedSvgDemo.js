function appMain(rootElement) {
    const animals = { turtle, ghost, broom, penguin, crocodile, whale };
    let currentAnimal = "turtle";

    // --- scene ---
    const scene = new Scene();
    scene.background = new Color(0x0d1117);

    const camera = new PerspectiveCamera(50, 1, 0.1, 200);
    camera.position.set(0, 0, 14);
    camera.lookAt(0, 0, 0);

    const renderer = new WebGLRenderer({ antialias: true });
    renderer.setPixelRatio(window.devicePixelRatio);

    const ambient = new AmbientLight(0xffffff, 0.7);
    scene.add(ambient);
    const keyLight = new DirectionalLight(0xffffff, 0.8);
    keyLight.position.set(5, 5, 10);
    scene.add(keyLight);
    const fillLight = new DirectionalLight(0x6688cc, 0.3);
    fillLight.position.set(-5, -3, 6);
    scene.add(fillLight);

    // --- state ---
    let depth = 0.3;
    let useExtrude = true;
    let useMirror = true;
    let camZ = 14;
    let model = null;

    function buildModel(svgString) {
        if (model) scene.remove(model);

        model = extrudeSvg(svgString, {
            depth: depth,
            extrude: useExtrude,
            mirror: useMirror,
            scale: 0.01,
            curveSegments: 5
        });
        model.updateMatrixWorld(true);

        const box = new Box3().setFromObject(model);
        const center = box.getCenter(new Vector3());
        model.position.set(-center.x, -center.y, -center.z);

        scene.add(model);
    }

    buildModel(animals[currentAnimal]);

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
        buildModel(animals[currentAnimal]);
    });
    selectLabel.appendChild(select);
    controls.appendChild(selectLabel);

    // depth slider
    const depthLabel = document.createElement("label");
    depthLabel.textContent = "Depth ";
    depthLabel.style.whiteSpace = "nowrap";
    const depthSlider = document.createElement("input");
    depthSlider.type = "range";
    depthSlider.min = "0.05";
    depthSlider.max = "2";
    depthSlider.step = "0.05";
    depthSlider.value = String(depth);
    depthSlider.style.width = "160px";
    const depthDisplay = document.createElement("span");
    depthDisplay.textContent = depth.toFixed(2);
    depthDisplay.style.minWidth = "3em";

    depthSlider.addEventListener("input", (e) => {
        depth = parseFloat(e.target.value);
        depthDisplay.textContent = depth.toFixed(2);
        buildModel(animals[currentAnimal]);
    });
    depthLabel.appendChild(depthSlider);
    controls.appendChild(depthLabel);
    controls.appendChild(depthDisplay);

    // extrude toggle
    const extrudeLabel = document.createElement("label");
    extrudeLabel.style.cssText = "display:flex;align-items:center;gap:4px;cursor:pointer;white-space:nowrap;";
    const extrudeCheck = document.createElement("input");
    extrudeCheck.type = "checkbox";
    extrudeCheck.checked = useExtrude;
    extrudeCheck.addEventListener("change", () => {
        useExtrude = extrudeCheck.checked;
        buildModel(animals[currentAnimal]);
    });
    extrudeLabel.appendChild(extrudeCheck);
    extrudeLabel.appendChild(document.createTextNode("3D extrude"));
    controls.appendChild(extrudeLabel);

    // mirror toggle
    const mirrorLabel = document.createElement("label");
    mirrorLabel.style.cssText = "display:flex;align-items:center;gap:4px;cursor:pointer;white-space:nowrap;";
    const mirrorCheck = document.createElement("input");
    mirrorCheck.type = "checkbox";
    mirrorCheck.checked = useMirror;
    mirrorCheck.addEventListener("change", () => {
        useMirror = mirrorCheck.checked;
        buildModel(animals[currentAnimal]);
    });
    mirrorLabel.appendChild(mirrorCheck);
    mirrorLabel.appendChild(document.createTextNode("Mirror back"));
    controls.appendChild(mirrorLabel);

    // zoom slider
    const zoomLabel = document.createElement("label");
    zoomLabel.textContent = "Zoom ";
    zoomLabel.style.whiteSpace = "nowrap";
    const zoomSlider = document.createElement("input");
    zoomSlider.type = "range";
    zoomSlider.min = "4";
    zoomSlider.max = "60";
    zoomSlider.step = "1";
    zoomSlider.value = String(camZ);
    zoomSlider.style.width = "160px";
    zoomSlider.style.direction = "rtl";

    zoomSlider.addEventListener("input", (e) => {
        camZ = parseFloat(e.target.value);
        camera.position.z = camZ;
    });
    zoomLabel.appendChild(zoomSlider);
    controls.appendChild(zoomLabel);

    // auto-rotate toggle
    const rotateLabel = document.createElement("label");
    rotateLabel.style.cssText = "display:flex;align-items:center;gap:4px;cursor:pointer;white-space:nowrap;";
    const rotateCheck = document.createElement("input");
    rotateCheck.type = "checkbox";
    rotateCheck.checked = true;
    let autoRotate = true;
    rotateCheck.addEventListener("change", () => { autoRotate = rotateCheck.checked; });
    rotateLabel.appendChild(rotateCheck);
    rotateLabel.appendChild(document.createTextNode("Auto-rotate"));
    controls.appendChild(rotateLabel);

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
    let rotY = 0.4;
    let rotX = 0.3;

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
        camZ = Math.max(4, Math.min(60, camZ + e.deltaY * 0.02));
        camera.position.z = camZ;
        zoomSlider.value = String(camZ);
    }, { passive: false });

    // --- animate ---
    function animate() {
        requestAnimationFrame(animate);
        if (autoRotate && !dragging) {
            rotY += 0.003;
        }
        if (model) {
            model.rotation.y = rotY;
            model.rotation.x = rotX;
        }
        renderer.render(scene, camera);
    }
    animate();
}

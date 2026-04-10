function appMain(rootElement) {
    const scene = new Scene();
    scene.background = new Color(0x87ceeb);

    const camera = new PerspectiveCamera(50, 1, 0.1, 100);
    camera.position.set(0, 3, 6);
    camera.lookAt(0, 0.5, 0);

    const renderer = new WebGLRenderer({ antialias: true });
    renderer.setPixelRatio(window.devicePixelRatio);
    rootElement.appendChild(renderer.domElement);

    const ambient = new AmbientLight(0xffffff, 0.6);
    scene.add(ambient);

    const sun = new DirectionalLight(0xffffff, 1.0);
    sun.position.set(5, 8, 4);
    scene.add(sun);

    // --- build turtle ---
    const turtle = new Group();

    const shellMat = new MeshStandardMaterial({ color: 0x2e7d32 });
    const skinMat = new MeshStandardMaterial({ color: 0x66bb6a });
    const eyeMat = new MeshStandardMaterial({ color: 0x111111 });

    // shell (flattened sphere)
    const shellGeo = new SphereGeometry(1, 32, 24);
    const shell = new Mesh(shellGeo, shellMat);
    shell.scale.set(1, 0.55, 1.15);
    shell.position.y = 0.55;
    turtle.add(shell);

    // belly
    const bellyGeo = new SphereGeometry(0.85, 32, 24);
    const bellyMat = new MeshStandardMaterial({ color: 0xa5d6a7 });
    const belly = new Mesh(bellyGeo, bellyMat);
    belly.scale.set(0.95, 0.35, 1.1);
    belly.position.y = 0.3;
    turtle.add(belly);

    // head
    const headGeo = new SphereGeometry(0.35, 24, 18);
    const head = new Mesh(headGeo, skinMat);
    head.position.set(0, 0.65, 1.25);
    turtle.add(head);

    // eyes
    const eyeGeo = new SphereGeometry(0.07, 12, 10);
    const leftEye = new Mesh(eyeGeo, eyeMat);
    leftEye.position.set(-0.15, 0.78, 1.5);
    turtle.add(leftEye);
    const rightEye = new Mesh(eyeGeo, eyeMat);
    rightEye.position.set(0.15, 0.78, 1.5);
    turtle.add(rightEye);

    // legs
    const legGeo = new CylinderGeometry(0.13, 0.15, 0.45, 12);
    const legPositions = [
        [-0.6, 0.22, 0.7],
        [0.6, 0.22, 0.7],
        [-0.6, 0.22, -0.7],
        [0.6, 0.22, -0.7]
    ];
    for (const [lx, ly, lz] of legPositions) {
        const leg = new Mesh(legGeo, skinMat);
        leg.position.set(lx, ly, lz);
        turtle.add(leg);
    }

    // tail
    const tailGeo = new CylinderGeometry(0.04, 0.1, 0.4, 8);
    const tail = new Mesh(tailGeo, skinMat);
    tail.position.set(0, 0.4, -1.2);
    tail.rotation.x = Math.PI / 4;
    turtle.add(tail);

    scene.add(turtle);

    // --- ground ---
    const groundGeo = new CylinderGeometry(3.5, 3.5, 0.08, 48);
    const groundMat = new MeshStandardMaterial({ color: 0xc8e6c9 });
    const ground = new Mesh(groundGeo, groundMat);
    ground.position.y = -0.04;
    scene.add(ground);

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

    // --- animate ---
    let dragging = false;
    let prevX = 0;
    let angle = 0;

    renderer.domElement.addEventListener("pointerdown", (e) => {
        dragging = true;
        prevX = e.clientX;
        renderer.domElement.setPointerCapture(e.pointerId);
    });
    renderer.domElement.addEventListener("pointermove", (e) => {
        if (!dragging) return;
        angle += (e.clientX - prevX) * 0.01;
        prevX = e.clientX;
    });
    renderer.domElement.addEventListener("pointerup", () => { dragging = false; });

    function animate() {
        requestAnimationFrame(animate);
        if (!dragging) {
            angle += 0.005;
        }
        turtle.rotation.y = angle;

        // gentle bobbing
        turtle.position.y = Math.sin(Date.now() * 0.001) * 0.05;

        renderer.render(scene, camera);
    }
    animate();
}

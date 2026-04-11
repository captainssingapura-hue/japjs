function appMain(rootElement) {
    var ANIMAL_SIZE = 50;
    var STEP = 5;
    var SKY_H = 120;
    var VIEWPORT_H = 500;
    var LAVA_H = 40;
    var PLATFORM_H = 16;
    var DEFAULT_GRAVITY = 0.6;
    var JUMP_STRENGTH = 12;

    // --- Title & hint ---
    var h1 = document.createElement("h1");
    css.setClass(h1, pg_title);
    h1.textContent = "Animal Platformer";
    rootElement.appendChild(h1);

    var hint = document.createElement("p");
    css.setClass(hint, pg_hint);
    hint.textContent = "Arrow keys to move. Space to jump. Don\u2019t fall into the lava!";
    rootElement.appendChild(hint);

    // --- Sound effects (lazy-init on first user gesture) ---
    var audioReady = false;
    var moveSynth = new Synth({ oscillator: { type: "square" }, envelope: { attack: 0.005, decay: 0.06, sustain: 0, release: 0.05 }, volume: -26 }).toDestination();
    var jumpSynth = new MembraneSynth({ pitchDecay: 0.08, octaves: 3, envelope: { attack: 0.005, decay: 0.15, sustain: 0, release: 0.1 }, volume: -12 }).toDestination();
    var deathSynth = new Synth({ oscillator: { type: "triangle" }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -12 }).toDestination();

    function ensureAudio() {
        if (!audioReady) {
            return start().then(function () {
                audioReady = true;
                startBgm();
            });
        }
        return Promise.resolve();
    }

    var moveNoteIndex = 0;
    var moveNotes = ["C5", "D5", "E5", "D5"];
    function playMoveSound() {
        moveSynth.triggerAttackRelease(moveNotes[moveNoteIndex], "32n");
        moveNoteIndex = (moveNoteIndex + 1) % moveNotes.length;
    }
    function playJumpSound() {
        jumpSynth.triggerAttackRelease("C3", "8n");
    }
    function playDeathJingle() {
        // Mario-style death: short rising note then descending sequence
        var notes = ["B4", "F5", "F5", "F5", "E5", "D5", "C5", "E4", "E4", "C4"];
        var timing = [0, 120, 240, 400, 520, 640, 760, 920, 1040, 1200];
        for (var di = 0; di < notes.length; di++) {
            (function (note, t) {
                setTimeout(function () { deathSynth.triggerAttackRelease(note, "16n"); }, t);
            })(notes[di], timing[di]);
        }
    }

    // --- BGM loop ---
    var bgmSynth = new Synth({ oscillator: { type: "triangle" }, envelope: { attack: 0.02, decay: 0.15, sustain: 0.1, release: 0.2 }, volume: -24 }).toDestination();
    var bgmData = getBgm();
    var bgmIndex = 0;
    var bgmTimer = null;
    var bgmPlaying = false;

    function durationToMs(dur, bpm) {
        var beat = 60000 / bpm;
        switch (dur) {
            case "2n": return beat * 2;
            case "4n": return beat;
            case "8n": return beat / 2;
            case "16n": return beat / 4;
            case "32n": return beat / 8;
            default: return beat;
        }
    }

    function bgmStep() {
        if (!bgmPlaying || gameOver) return;
        var note = bgmData.notes[bgmIndex];
        var dur = bgmData.durations[bgmIndex];
        if (note !== null) {
            bgmSynth.triggerAttackRelease(note, dur);
        }
        var ms = durationToMs(dur, bgmData.bpm);
        bgmIndex = (bgmIndex + 1) % bgmData.notes.length;
        bgmTimer = setTimeout(bgmStep, ms);
    }

    function startBgm() {
        if (bgmPlaying) return;
        bgmPlaying = true;
        bgmIndex = 0;
        bgmStep();
    }

    function stopBgm() {
        bgmPlaying = false;
        if (bgmTimer !== null) {
            clearTimeout(bgmTimer);
            bgmTimer = null;
        }
    }

    // --- Landing sound: simultaneous triad mapped to platform height ---
    var landSynthRoot = new Synth({ oscillator: { type: "triangle" }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -16 }).toDestination();
    var landSynthThird = new Synth({ oscillator: { type: "triangle" }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -18 }).toDestination();
    var landSynthFifth = new Synth({ oscillator: { type: "triangle" }, envelope: { attack: 0.01, decay: 0.2, sustain: 0.03, release: 0.15 }, volume: -18 }).toDestination();

    var NOTE_NAMES = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"];
    var LAND_BASE_OCTAVE = 4;
    var LAND_SEMITONE_RANGE = 24; // C4 to C6
    var lastLandedPlatY = null;

    function noteFromIndex(i) {
        var octave = LAND_BASE_OCTAVE + Math.floor(i / 12);
        return NOTE_NAMES[i % 12] + octave;
    }

    function playLandingChord(platformY) {
        var range = engine.getYRange();
        // Higher platform (lower Y) → higher pitch
        var t = 1 - (platformY - range.minY) / (range.maxY - range.minY);
        t = Math.max(0, Math.min(1, t));
        var rootIndex = Math.round(t * LAND_SEMITONE_RANGE);
        // Minor chord when descending to a lower platform, major otherwise
        var thirdOffset = (lastLandedPlatY !== null && platformY > lastLandedPlatY) ? 3 : 4;
        var root = noteFromIndex(rootIndex);
        var third = noteFromIndex(rootIndex + thirdOffset);
        var fifth = noteFromIndex(rootIndex + 7);
        landSynthRoot.triggerAttackRelease(root, "8n");
        landSynthThird.triggerAttackRelease(third, "8n");
        landSynthFifth.triggerAttackRelease(fifth, "8n");
        lastLandedPlatY = platformY;
    }

    // --- Controls ---
    var controls = document.createElement("div");
    css.setClass(controls, pg_controls);

    var gravityLabel = document.createElement("label");
    gravityLabel.textContent = "Gravity ";
    var gravitySlider = document.createElement("input");
    gravitySlider.type = "range";
    gravitySlider.min = "1";
    gravitySlider.max = "20";
    gravitySlider.step = "1";
    gravitySlider.value = String(DEFAULT_GRAVITY * 10);
    gravityLabel.appendChild(gravitySlider);

    var gravityDisplay = document.createElement("span");
    css.setClass(gravityDisplay, pg_size_display);
    gravityDisplay.textContent = DEFAULT_GRAVITY.toFixed(1);

    var DEFAULT_BGM_VOL = -24;
    var bgmLabel = document.createElement("label");
    bgmLabel.textContent = "BGM ";
    var bgmSlider = document.createElement("input");
    bgmSlider.type = "range";
    bgmSlider.min = "0";
    bgmSlider.max = "100";
    bgmSlider.step = "1";
    bgmSlider.value = "40";
    bgmLabel.appendChild(bgmSlider);

    var bgmDisplay = document.createElement("span");
    css.setClass(bgmDisplay, pg_size_display);
    bgmDisplay.textContent = "40%";

    controls.appendChild(gravityLabel);
    controls.appendChild(gravityDisplay);
    controls.appendChild(bgmLabel);
    controls.appendChild(bgmDisplay);
    rootElement.appendChild(controls);

    // --- Theme switcher ---
    var themes = [
        { key: "light", label: "Light" },
        { key: "dark", label: "Dark" },
        { key: "beach", label: "Beach" },
        { key: "dracula", label: "Dracula\u2019s Castle" },
        { key: "alpine", label: "Alpine Mountain" }
    ];

    var currentTheme = new URLSearchParams(window.location.search).get("theme") || "light";

    var themeSwitcher = document.createElement("div");
    css.setClass(themeSwitcher, pg_theme_switcher);

    var themeLabel = document.createElement("span");
    css.setClass(themeLabel, pg_theme_label);
    themeLabel.textContent = "Theme";
    themeSwitcher.appendChild(themeLabel);

    for (var ti = 0; ti < themes.length; ti++) {
        (function (t) {
            var btn = document.createElement("button");
            css.setClass(btn, pg_theme_btn);
            if (t.key === currentTheme) css.addClass(btn, pg_theme_btn_active);
            btn.textContent = t.label;
            btn.addEventListener("click", function () {
                if (t.key === currentTheme) return;
                var url = new URL(window.location.href);
                url.searchParams.set("theme", t.key);
                window.location.href = url.toString();
            });
            themeSwitcher.appendChild(btn);
        })(themes[ti]);
    }

    rootElement.appendChild(themeSwitcher);

    // --- Measure available width ---
    var viewportW = rootElement.clientWidth || 700;

    // --- Engines ---
    var physics = createJumpPhysics(DEFAULT_GRAVITY, JUMP_STRENGTH);
    var engine = createPlatformEngine({
        viewportW: viewportW,
        viewportH: VIEWPORT_H,
        animalW: ANIMAL_SIZE,
        animalH: ANIMAL_SIZE,
        platformH: PLATFORM_H,
        lavaH: LAVA_H,
        skyH: SKY_H
    });

    // --- Game state ---
    var worldX = 100;
    var worldY = 0;
    var cameraX = 0;
    var facingRight = true;
    var gameOver = false;
    var score = 0;
    var animFrameId = 0;

    // --- DOM: viewport (full width) ---
    var playground = document.createElement("div");
    css.setClass(playground, pg_playground);
    playground.style.height = VIEWPORT_H + "px";
    rootElement.appendChild(playground);

    // --- DOM: sky decoration ---
    var sky = document.createElement("div");
    css.setClass(sky, pg_sky);
    sky.style.height = SKY_H + "px";
    playground.appendChild(sky);

    // --- DOM: world container (scrolls) ---
    var world = document.createElement("div");
    css.setClass(world, pg_world);
    playground.appendChild(world);

    // --- DOM: lava (fixed at bottom of viewport) ---
    var lava = document.createElement("div");
    css.setClass(lava, pg_lava);
    lava.style.height = LAVA_H + "px";
    playground.appendChild(lava);

    // --- DOM: animal ---
    var animal = createAnimalCell(css.className(pg_animal));
    world.appendChild(animal);

    // --- DOM: score ---
    var scoreEl = document.createElement("div");
    css.setClass(scoreEl, pg_score);
    scoreEl.textContent = "0";
    playground.appendChild(scoreEl);

    // --- DOM: game over overlay (hidden) ---
    var overlay = document.createElement("div");
    css.setClass(overlay, pg_gameover);
    overlay.style.display = "none";

    var overTitle = document.createElement("h2");
    overTitle.textContent = "GAME OVER";
    overlay.appendChild(overTitle);

    var finalScore = document.createElement("p");
    css.setClass(finalScore, pg_final_score);
    overlay.appendChild(finalScore);

    var restartBtn = document.createElement("button");
    restartBtn.textContent = "Play Again";
    overlay.appendChild(restartBtn);
    playground.appendChild(overlay);

    // --- Platform DOM management ---
    var platformEls = new Map();
    var activePlatform = null;

    function syncPlatformDom() {
        var currentPlats = engine.getPlatforms();
        var currentSet = new Set(currentPlats);

        // Remove old
        platformEls.forEach(function (el, plat) {
            if (!currentSet.has(plat)) {
                el.remove();
                platformEls.delete(plat);
            }
        });

        // Add new & position
        for (var i = 0; i < currentPlats.length; i++) {
            var p = currentPlats[i];
            var el = platformEls.get(p);
            if (!el) {
                el = document.createElement("div");
                css.setClass(el, pg_platform);
                el.style.height = PLATFORM_H + "px";
                world.appendChild(el);
                platformEls.set(p, el);
            }
            el.style.left = p.x + "px";
            el.style.top = p.y + "px";
            el.style.width = p.w + "px";
            css.toggleClass(el, pg_platform_active, p === activePlatform);
        }
    }

    // --- Resize handling ---
    function handleResize() {
        var newW = rootElement.clientWidth || 700;
        if (newW !== viewportW) {
            viewportW = newW;
            engine.resize(viewportW);
        }
    }
    window.addEventListener("resize", handleResize);

    // --- Init world ---
    function initGame() {
        handleResize();
        worldX = 100;
        worldY = 0;
        cameraX = 0;
        facingRight = true;
        gameOver = false;
        score = 0;
        scoreEl.textContent = "0";
        overlay.style.display = "none";

        // Clear old platform DOM
        platformEls.forEach(function (el) { el.remove(); });
        platformEls.clear();

        lastLandedPlatY = null;
        activePlatform = null;
        engine.init(worldX, worldY);
        // Place animal on starting platform
        var hit = engine.findGround(worldX, worldY, 1);
        if (hit !== null) {
            worldY = hit.groundY;
            activePlatform = hit.platform;
        }
        engine.generateAhead(worldX + viewportW * 2);

        updateFlip();
        render();
    }

    function updateFlip() {
        animal.style.transform = facingRight ? "scaleX(1)" : "scaleX(-1)";
    }

    function render() {
        world.style.transform = "translateX(" + (-cameraX) + "px)";
        animal.style.left = worldX + "px";
        animal.style.top = worldY + "px";
        syncPlatformDom();
    }

    function die() {
        gameOver = true;
        stopBgm();
        ensureAudio().then(function () { playDeathJingle(); });
        finalScore.textContent = "Score: " + score;
        overlay.style.display = "";
    }

    // --- Input ---
    var keys = {};
    var moveHeld = false;

    document.addEventListener("keydown", function (e) {
        if (gameOver) return;
        keys[e.key] = true;
        if (e.key === " ") {
            e.preventDefault();
            ensureAudio().then(function () {
                if (!physics.isJumping()) playJumpSound();
                physics.jump();
            });
        }
        if (e.key === "ArrowLeft" || e.key === "ArrowRight") {
            e.preventDefault();
            if (!moveHeld) {
                moveHeld = true;
                ensureAudio();
            }
        }
    });

    document.addEventListener("keyup", function (e) {
        keys[e.key] = false;
        if (!keys["ArrowLeft"] && !keys["ArrowRight"]) {
            moveHeld = false;
        }
    });

    gravitySlider.addEventListener("input", function (e) {
        var g = parseInt(e.target.value) / 10;
        physics.setGravity(g);
        gravityDisplay.textContent = g.toFixed(1);
    });

    bgmSlider.addEventListener("input", function (e) {
        var pct = parseInt(e.target.value);
        bgmDisplay.textContent = pct + "%";
        if (pct === 0) {
            bgmSynth.volume.value = -Infinity;
        } else {
            // Map 1-100% to -40dB...-8dB range
            bgmSynth.volume.value = -40 + (pct / 100) * 32;
        }
    });

    restartBtn.addEventListener("click", function () {
        initGame();
        if (audioReady) startBgm();
        animFrameId = requestAnimationFrame(frame);
    });

    // --- Game loop ---
    var moveFrameCount = 0;

    function frame() {
        if (gameOver) return;

        var moved = false;

        if (keys["ArrowLeft"]) {
            worldX -= STEP;
            if (facingRight) { facingRight = false; updateFlip(); }
            moved = true;
        }
        if (keys["ArrowRight"]) {
            worldX += STEP;
            if (!facingRight) { facingRight = true; updateFlip(); }
            moved = true;
        }

        // Platform collision
        var wasInAir = physics.isJumping();
        var hit = engine.findGround(worldX, worldY, physics.getVy());
        if (hit !== null) {
            worldY = physics.update(worldY, hit.groundY);
            activePlatform = hit.platform;
        } else {
            physics.fall();
            worldY = physics.update(worldY, VIEWPORT_H + 100);
            activePlatform = null;
        }

        // Landing detection: was airborne, now grounded
        if (wasInAir && !physics.isJumping() && audioReady && hit !== null) {
            playLandingChord(hit.platform.y);
        }

        // Move sound: only when moving along a surface (grounded)
        if (moved && audioReady && !physics.isJumping()) {
            moveFrameCount++;
            if (moveFrameCount % 4 === 0) playMoveSound();
        } else {
            moveFrameCount = 0;
        }

        // Lava death
        if (engine.isInLava(worldY)) {
            die();
            render();
            return;
        }

        // Camera
        cameraX = engine.updateCamera(cameraX, worldX);

        // Score
        var dist = Math.max(0, Math.floor(worldX / 10));
        if (dist > score) {
            score = dist;
            scoreEl.textContent = String(score);
        }

        // Generate & prune
        engine.generateAhead(cameraX + viewportW * 2);
        engine.pruneBehind(cameraX);

        render();
        animFrameId = requestAnimationFrame(frame);
    }

    initGame();
    animFrameId = requestAnimationFrame(frame);
}

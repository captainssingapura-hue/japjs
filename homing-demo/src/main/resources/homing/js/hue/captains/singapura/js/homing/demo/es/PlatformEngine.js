function createPlatformEngine(config) {
    var viewportW = config.viewportW;
    var viewportH = config.viewportH;
    var animalW = config.animalW;
    var animalH = config.animalH;
    var platformH = config.platformH;
    var lavaH = config.lavaH;
    var skyH = config.skyH || 0;

    var platforms = [];
    var lastPlatRight = 0;
    var lastPlatY = 0;

    var MIN_GAP = 60;
    var MAX_GAP = 140;
    var MIN_WIDTH = 80;
    var MAX_WIDTH = 160;
    var groundZone = viewportH - lavaH;
    var MIN_Y = Math.floor(groundZone * 0.55);
    var MAX_Y = groundZone - platformH - 20;
    var DELTA_Y_UP = -60;
    var DELTA_Y_DOWN = 50;
    var PRUNE_MARGIN = 400;
    var CAMERA_LEAD = viewportW / 3;

    function clampY(y) {
        return Math.max(MIN_Y, Math.min(MAX_Y, y));
    }

    return {
        init: function (startX, startY) {
            platforms.length = 0;
            var startPlatY = Math.floor((MIN_Y + MAX_Y) / 2);
            var startPlat = { x: startX - 40, y: startPlatY, w: 200 };
            platforms.push(startPlat);
            lastPlatRight = startPlat.x + startPlat.w;
            lastPlatY = startPlat.y;
        },

        generateAhead: function (untilWorldX) {
            while (lastPlatRight < untilWorldX) {
                var gap = MIN_GAP + Math.random() * (MAX_GAP - MIN_GAP);
                var newX = lastPlatRight + gap;
                var deltaY = DELTA_Y_UP + Math.random() * (DELTA_Y_DOWN - DELTA_Y_UP);
                var newY = clampY(lastPlatY + deltaY);
                var w = MIN_WIDTH + Math.random() * (MAX_WIDTH - MIN_WIDTH);
                var plat = { x: newX, y: newY, w: w };
                platforms.push(plat);
                lastPlatRight = newX + w;
                lastPlatY = newY;
            }
        },

        pruneBehind: function (worldX) {
            var cutoff = worldX - PRUNE_MARGIN;
            while (platforms.length > 0 && platforms[0].x + platforms[0].w < cutoff) {
                platforms.shift();
            }
        },

        getPlatforms: function () {
            return platforms;
        },

        findGround: function (animalX, animalY, vy) {
            var animalBottom = animalY + animalH;
            var animalRight = animalX + animalW;
            var best = null;

            for (var i = 0; i < platforms.length; i++) {
                var p = platforms[i];
                // Horizontal overlap check
                if (animalRight <= p.x || animalX >= p.x + p.w) continue;
                // Platform top must be at or below animal bottom (within tolerance)
                // and animal must be falling (vy >= 0)
                var platTop = p.y;
                if (vy >= 0 && animalBottom <= platTop + 8 && animalBottom >= platTop - animalH) {
                    var groundY = platTop - animalH;
                    if (best === null || groundY < best.groundY) {
                        best = { groundY: groundY, platform: p };
                    }
                }
            }
            return best;
        },

        updateCamera: function (cameraX, animalX) {
            var target = animalX - CAMERA_LEAD;
            return Math.max(cameraX, target);
        },

        isInLava: function (animalY) {
            return animalY + animalH >= viewportH - lavaH;
        },

        getYRange: function () {
            return { minY: MIN_Y, maxY: MAX_Y };
        },

        resize: function (newViewportW) {
            viewportW = newViewportW;
            CAMERA_LEAD = viewportW / 3;
        },

        reset: function () {}
    };
}

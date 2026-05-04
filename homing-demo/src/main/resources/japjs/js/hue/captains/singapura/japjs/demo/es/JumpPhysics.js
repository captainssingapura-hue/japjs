function createJumpPhysics(gravity, jumpStrength) {
    let vy = 0;
    let jumping = false;
    let g = gravity;

    return {
        jump() {
            if (!jumping) {
                vy = -jumpStrength;
                jumping = true;
            }
        },
        fall() {
            if (!jumping) {
                vy = 0;
                jumping = true;
            }
        },
        setGravity(value) {
            g = value;
        },
        update(y, groundY) {
            if (!jumping) return y;
            vy += g;
            y += vy;
            if (y >= groundY) {
                y = groundY;
                vy = 0;
                jumping = false;
            }
            return y;
        },
        isJumping() {
            return jumping;
        },
        getVy() {
            return vy;
        }
    };
}

/**
 * Turn a flat SVG string into a 3D Three.js Group.
 *
 * By default uses ShapeGeometry (lightweight, flat with depth via offset).
 * Set opts.extrude = true for full ExtrudeGeometry (heavier but true 3D walls).
 *
 * @param {string}  svgString  - raw SVG markup
 * @param {object}  [opts]     - optional overrides
 * @param {number}  [opts.depth=0.3]          - depth offset (ShapeGeometry) or extrusion depth
 * @param {boolean} [opts.extrude=false]      - use ExtrudeGeometry instead of ShapeGeometry
 * @param {number}  [opts.curveSegments=5]    - shape curve smoothness
 * @param {number}  [opts.scale=0.01]         - uniform scale (SVG px → world units)
 * @param {boolean} [opts.mirror=false]       - add a mirrored copy on the back face
 * @returns {THREE.Group}
 */
function extrudeSvg(svgString, opts) {
    const o = Object.assign({
        depth:         0.3,
        extrude:       false,
        curveSegments: 5,
        scale:         0.01,
        mirror:        false
    }, opts);

    // Strip XML declaration, comments and gradient defs — SVGLoader expects pure SVG markup
    const cleanSvg = svgString
        .replace(/<\?xml[^?]*\?>/g, "")
        .replace(/<!--[\s\S]*?-->/g, "")
        .replace(/<defs[\s\S]*?<\/defs>/gi, "")
        .trim();

    const loader = new SVGLoader();
    const svgData = loader.parse(cleanSvg);
    const group = new Group();

    // Small z-increment per path prevents z-fighting between overlapping shapes.
    // Later SVG paths are "on top", so they get a higher z.
    const Z_LAYER_STEP = 0.1;
    let layerIndex = 0;
    const layers = [];              // collected for optional mirror pass

    for (const path of svgData.paths) {
        const fillColor = path.userData.style.fill;
        if (!fillColor || fillColor === "none" || fillColor.startsWith("url(")) continue;

        const material = new MeshStandardMaterial({
            color: new Color().setStyle(fillColor),
            side: DoubleSide
        });

        const shapes = SVGLoader.createShapes(path);

        if (o.mirror && o.extrude) {
            layers.push({ shapes, material, li: layerIndex });
        }

        for (const shape of shapes) {
            let geometry;
            if (o.extrude) {
                geometry = new ExtrudeGeometry(shape, {
                    depth:          o.depth / o.scale,
                    bevelEnabled:   false,
                    curveSegments:  o.curveSegments
                });
            } else {
                geometry = new ShapeGeometry(shape, o.curveSegments);
            }
            const mesh = new Mesh(geometry, material);
            mesh.position.z = layerIndex * Z_LAYER_STEP;
            group.add(mesh);
        }
        layerIndex++;
    }

    // Add flat caps on the back face with reversed layer order so the SVG
    // image reads correctly when viewed from behind.
    // BackSide ensures these caps are invisible from the front.
    if (o.mirror && o.extrude) {
        const totalLayers = layerIndex;
        const backZ = o.depth / o.scale;
        for (const { shapes, material, li } of layers) {
            const backMat = new MeshStandardMaterial({
                color: material.color,
                side: BackSide
            });
            for (const shape of shapes) {
                const cap = new ShapeGeometry(shape, o.curveSegments);
                const mesh = new Mesh(cap, backMat);
                mesh.position.z = backZ + (totalLayers - 1 - li) * Z_LAYER_STEP;
                group.add(mesh);
            }
        }
    }

    // Apply scale; flip Y (SVG y-axis is inverted)
    group.scale.set(o.scale, -o.scale, o.scale);

    return group;
}

/**
 * Decompose a flat SVG into exploded 3D layers spread along the z-axis.
 * Each SVG path becomes a separate layer, visually separated for inspection.
 * Layers can optionally be extruded to give them visible thickness.
 *
 * @param {string}  svgString               - raw SVG markup
 * @param {object}  [opts]                  - optional overrides
 * @param {number}  [opts.spread=1]         - distance between layers in world units
 * @param {number}  [opts.depth=0]          - extrusion depth per layer (0 = flat)
 * @param {number}  [opts.scale=0.01]       - uniform scale (SVG px → world units)
 * @param {number}  [opts.curveSegments=5]  - shape curve smoothness
 * @returns {THREE.Group}
 */
function decomposeSvg(svgString, opts) {
    const o = Object.assign({
        spread:        1,
        depth:         0,
        scale:         0.01,
        curveSegments: 5
    }, opts);

    const cleanSvg = svgString
        .replace(/<\?xml[^?]*\?>/g, "")
        .replace(/<!--[\s\S]*?-->/g, "")
        .replace(/<defs[\s\S]*?<\/defs>/gi, "")
        .trim();

    const loader = new SVGLoader();
    const svgData = loader.parse(cleanSvg);
    const group = new Group();

    let layerIndex = 0;

    for (const path of svgData.paths) {
        const fillColor = path.userData.style.fill;
        if (!fillColor || fillColor === "none" || fillColor.startsWith("url(")) continue;

        const material = new MeshStandardMaterial({
            color: new Color().setStyle(fillColor),
            side: DoubleSide
        });

        const shapes = SVGLoader.createShapes(path);

        for (const shape of shapes) {
            let geometry;
            if (o.depth > 0) {
                geometry = new ExtrudeGeometry(shape, {
                    depth: o.depth / o.scale,
                    bevelEnabled: false,
                    curveSegments: o.curveSegments
                });
            } else {
                geometry = new ShapeGeometry(shape, o.curveSegments);
            }
            const mesh = new Mesh(geometry, material);
            // Always offset layers slightly to prevent z-fighting when spread ≈ 0
            const Z_LAYER_STEP = 0.1;
            mesh.position.z = layerIndex * (o.spread / o.scale + Z_LAYER_STEP);
            group.add(mesh);
        }
        layerIndex++;
    }

    group.scale.set(o.scale, -o.scale, o.scale);

    return group;
}

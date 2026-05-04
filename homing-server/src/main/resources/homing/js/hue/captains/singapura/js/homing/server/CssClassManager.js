const CssClassManagerInstance = (() => {
    const loaded = new Set();

    async function loadCss(cssBeing, theme) {
        const key = cssBeing + (theme ? ":" + theme : "");
        if (loaded.has(key)) return;

        let url = "/css?class=" + encodeURIComponent(cssBeing);
        if (theme) url += "&theme=" + encodeURIComponent(theme);

        const resp = await fetch(url);
        if (!resp.ok) throw new Error("Failed to resolve CSS for " + cssBeing + ": " + resp.status);

        const entries = await resp.json();

        const promises = [];
        for (const entry of entries) {
            const entryKey = entry.name + (theme ? ":" + theme : "");
            if (loaded.has(entryKey)) continue;
            loaded.add(entryKey);
            promises.push(appendLink(entry.href));
        }
        await Promise.all(promises);
    }

    function appendLink(href) {
        return new Promise((resolve, reject) => {
            const link = document.createElement("link");
            link.rel = "stylesheet";
            link.href = href;
            link.onload = resolve;
            link.onerror = () => reject(new Error("Failed to load CSS: " + href));
            document.head.appendChild(link);
        });
    }

    function cls(cssName) {
        return Object.freeze({ _n: cssName });
    }

    function resolve(cssClass) {
        if (!cssClass || typeof cssClass._n !== "string")
            throw new Error("Invalid CSS class object: " + JSON.stringify(cssClass));
        return cssClass._n;
    }

    return {
        loadCss, cls,
        addClass(el, ...classes) { for (const c of classes) el.classList.add(resolve(c)); },
        removeClass(el, ...classes) { for (const c of classes) el.classList.remove(resolve(c)); },
        toggleClass(el, cssClass, force) {
            arguments.length === 3
                ? el.classList.toggle(resolve(cssClass), force)
                : el.classList.toggle(resolve(cssClass));
        },
        setClass(el, ...classes) { el.className = classes.map(resolve).join(" "); },
        hasClass(el, cssClass) { return el.classList.contains(resolve(cssClass)); },
        className(cssClass) { return resolve(cssClass); }
    };
})();

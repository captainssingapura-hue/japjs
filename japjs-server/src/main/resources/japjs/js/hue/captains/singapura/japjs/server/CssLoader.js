const CssLoaderInstance = (() => {
    const loaded = new Set();

    async function load(cssBeing, theme) {
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

    return { load };
})();

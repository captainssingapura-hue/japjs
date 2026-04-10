// Dracula's Castle BGM — dark minor key, chromatic, slow and eerie
function getBgm() {
    return {
        bpm: 80,
        notes: [
            "D3", "F3", "A3", "G#3", "A3", "F3", "E3", "D3",
            "C#3", "D3", "F3", "E3", "D3", null, "A2", null,
            "D3", "E3", "F3", "G3", "A3", "Bb3", "A3", "G#3",
            "A3", "F3", "E3", "D3", "C#3", "D3", null, null
        ],
        durations: [
            "4n", "8n", "4n", "8n", "4n", "8n", "8n", "4n",
            "8n", "8n", "4n", "8n", "2n", "4n", "2n", "4n",
            "8n", "8n", "4n", "8n", "8n", "4n", "8n", "8n",
            "4n", "8n", "8n", "4n", "8n", "2n", "4n", "4n"
        ]
    };
}

// Army BGM — steady marching cadence in A minor, with a recurring
// drum-step rhythm and a short fanfare bridge.
function getBgm() {
    return {
        bpm: 116,
        notes: [
            "A3", "E4", "A3", "E4",  "A3", "C4", "E4", "A4",
            "G3", "D4", "G3", "D4",  "G3", "B3", "D4", "G4",
            "A3", "E4", "A4", "C5",  "B4", "A4", "G4", "E4",
            "F4", "E4", "D4", "C4",  "B3", "A3", "E3", null
        ],
        durations: [
            "8n", "8n", "8n", "8n",  "8n", "8n", "8n", "4n",
            "8n", "8n", "8n", "8n",  "8n", "8n", "8n", "4n",
            "8n", "8n", "4n", "4n",  "8n", "8n", "8n", "8n",
            "8n", "8n", "8n", "8n",  "4n", "4n", "4n", "4n"
        ]
    };
}

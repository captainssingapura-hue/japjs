// Navy BGM — bright fanfare with rolling fourths and fifths, evoking
// "Anchors Aweigh" without quoting it. C major, brisk march.
function getBgm() {
    return {
        bpm: 124,
        notes: [
            "C4", "G4", "E4", "G4",  "C5", "G4", "E4", "G4",
            "D4", "A4", "F4", "A4",  "D5", "A4", "F4", "A4",
            "E4", "G4", "C5", "G4",  "E4", "C5", "G4", "E4",
            "D4", "F4", "A4", "F4",  "D4", "G4", "C4", null
        ],
        durations: [
            "8n", "8n", "8n", "8n",  "4n", "8n", "8n", "8n",
            "8n", "8n", "8n", "8n",  "4n", "8n", "8n", "8n",
            "8n", "8n", "4n", "8n",  "8n", "4n", "8n", "8n",
            "8n", "8n", "4n", "8n",  "8n", "8n", "4n", "4n"
        ]
    };
}

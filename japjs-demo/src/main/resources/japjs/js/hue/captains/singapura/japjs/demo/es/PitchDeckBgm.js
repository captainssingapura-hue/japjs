// Presentation-appropriate BGM — calm, ambient, loopable pad-plus-melody in E minor.
function getBgm() {
    return {
        bpm: 80,
        padNotes: ["E3", "G3", "B3", "D4"],
        melody: [
            "E4", null, "G4", null, "B4", "A4", "G4", null,
            "E4", null, "D4", null, "B3", null, "D4", null,
            "E4", null, "G4", null, "B4", "C5", "B4", "A4",
            "G4", null, "F#4", null, "E4", null, null,  null
        ],
        durations: [
            "8n","8n","8n","8n","8n","8n","8n","8n",
            "8n","8n","8n","8n","8n","8n","8n","8n",
            "8n","8n","8n","8n","8n","8n","8n","8n",
            "8n","8n","8n","8n","8n","8n","8n","8n"
        ]
    };
}

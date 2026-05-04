// Default BGM — gentle major key melody (used for light/dark themes)
function getBgm() {
    return {
        bpm: 120,
        notes: [
            "C4", "E4", "G4", "E4", "F4", "A4", "G4", "E4",
            "D4", "F4", "A4", "G4", "E4", "D4", "C4", null,
            "C4", "G4", "F4", "E4", "D4", "E4", "F4", "D4",
            "C4", "E4", "G4", "A4", "G4", "F4", "E4", null
        ],
        durations: [
            "8n", "8n", "4n", "8n", "8n", "4n", "8n", "8n",
            "8n", "8n", "4n", "8n", "8n", "8n", "4n", "4n",
            "8n", "8n", "4n", "8n", "8n", "8n", "8n", "4n",
            "8n", "8n", "4n", "8n", "8n", "8n", "4n", "4n"
        ]
    };
}

// Beach BGM — calypso/tropical, upbeat pentatonic
function getBgm() {
    return {
        bpm: 140,
        notes: [
            "C5", "A4", "G4", "E4", "G4", "A4", "C5", "A4",
            "G4", "E4", "D4", "E4", "G4", null, "G4", "A4",
            "C5", "D5", "C5", "A4", "G4", "A4", "G4", "E4",
            "D4", "E4", "G4", "A4", "G4", null, "E4", null
        ],
        durations: [
            "8n", "8n", "8n", "8n", "4n", "8n", "8n", "8n",
            "8n", "8n", "8n", "4n", "4n", "8n", "8n", "8n",
            "8n", "4n", "8n", "8n", "8n", "8n", "8n", "8n",
            "8n", "8n", "4n", "8n", "4n", "4n", "4n", "4n"
        ]
    };
}

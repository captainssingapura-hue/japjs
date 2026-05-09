// Air Force BGM — soaring melody with wide leaps, evoking "Wild Blue
// Yonder" without quoting it. F major, brisk + bright.
function getBgm() {
    return {
        bpm: 132,
        notes: [
            "F4", "A4",  "C5", "F5",  "C5", "A4", "F4", "C4",
            "D4", "F4",  "A4", "D5",  "A4", "F4", "D4", "F4",
            "E4", "G4",  "B4", "E5",  "G5", "E5", "B4", "G4",
            "F4", "A4",  "C5", "F5",  "E5", "D5", "C5", null
        ],
        durations: [
            "8n", "8n",  "4n", "4n",  "8n", "8n", "8n", "8n",
            "8n", "8n",  "4n", "4n",  "8n", "8n", "4n", "4n",
            "8n", "8n",  "4n", "4n",  "8n", "8n", "8n", "8n",
            "8n", "8n",  "4n", "4n",  "8n", "8n", "4n", "4n"
        ]
    };
}

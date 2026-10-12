(() => {
    const modes = ['light', 'dark'];
    const accents = ['red', 'orange', 'green', 'blue', 'purple'];
    let saved = {};
    try { saved = JSON.parse(localStorage.getItem('postmate.appearance')) || {}; } catch (_) { /* Storage can be unavailable. */ }
    let mode = modes.includes(saved.mode) ? saved.mode : 'light';
    let accent = accents.includes(saved.accent) ? saved.accent : 'blue';
    function apply() {
        document.documentElement.dataset.bsTheme = mode;
        document.documentElement.dataset.accent = accent;
    }
    apply();
    document.addEventListener('DOMContentLoaded', () => {
        document.querySelector(`input[name="appearance-mode"][value="${mode}"]`).checked = true;
        document.querySelector(`input[name="appearance-accent"][value="${accent}"]`).checked = true;
        document.getElementById('appearanceModal').addEventListener('change', (event) => {
            const { name, value } = event.target;
            if (name === 'appearance-mode' && modes.includes(value)) mode = value;
            else if (name === 'appearance-accent' && accents.includes(value)) accent = value;
            else return;
            apply();
            try { localStorage.setItem('postmate.appearance', JSON.stringify({ mode, accent })); } catch (_) { /* Keep the current selection in memory. */ }
        });
    });
})();

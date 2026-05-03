// LocalZero frontend logic

let currentUser = null;
let allInitiatives = [];
let activeFilter = '';

// Bootstrap
async function init() {
    await loadCurrentUser();
    await loadInitiatives();
    setupFilters();
}

async function loadCurrentUser() {
    try {
        const res = await fetch('/api/auth/me');
        if (!res.ok) { window.location.href = '/login'; return; }
        currentUser = await res.json();
        const name = currentUser.name || currentUser.username || 'User';
        document.getElementById('nav-username').textContent = name;
    } catch {
        window.location.href = '/login';
    }
}

// Initiatives
async function loadInitiatives() {
    try {
        const res = await fetch('/api/initiatives');
        if (!res.ok) throw new Error('Failed to load initiatives');
        allInitiatives = await res.json();
    } catch {
        // TODO: initiatives will be added here once BlueFlamingPenguin's endpoint is ready
        allInitiatives = [];
    }
    renderInitiatives();
}

function renderInitiatives() {
    const list = document.getElementById('initiatives-list');
    const filtered = activeFilter
        ? allInitiatives.filter(i => i.category === activeFilter)
        : allInitiatives;

    document.getElementById('feed-count').textContent =
        filtered.length ? `${filtered.length} initiative${filtered.length > 1 ? 's' : ''}` : '';

    if (!filtered.length) {
        list.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon"></div>
                <h3>No initiatives yet</h3>
                <p>Be the first to start something in your neighborhood.</p>
            </div>`;
        return;
    }

    list.innerHTML = '';
    filtered.forEach((item, idx) => {
        const card = document.createElement('div');
        card.className = 'initiative-card';
        card.style.animationDelay = `${idx * 0.06}s`;
        card.innerHTML = `
            <div class="initiative-card-header">
                <div class="initiative-title">${escHtml(item.title)}</div>
                ${item.category ? `<span class="initiative-badge">${escHtml(item.category.replace('_', ' '))}</span>` : ''}
            </div>
            <div class="initiative-desc">${escHtml(item.description || '')}</div>
            <div class="initiative-footer">
                <div class="initiative-meta">
                    <span>${escHtml(item.location || 'Malmö')}</span>
                    ${item.startDate ? `<span>${formatDate(item.startDate)}</span>` : ''}
                </div>
                <button class="btn-join ${item._joined ? 'joined' : ''}"
                    onclick="joinInitiative(event, ${item.id})">
                    ${item._joined ? '✓ Joined' : 'Join'}
                </button>
            </div>`;
        card.addEventListener('click', (e) => {
            if (e.target.classList.contains('btn-join')) return;
            window.location.href = `/initiative?id=${item.id}`;
        });
        list.appendChild(card);
    });
}

// Join
async function joinInitiative(e, id) {
    e.stopPropagation();
    const btn = e.target;
    if (btn.classList.contains('joined')) return;
    try {
        const res = await fetch(`/api/participation/join/${id}`, { method: 'POST' });
        if (res.ok) {
            const item = allInitiatives.find(i => i.id === id);
            if (item) item._joined = true;
            btn.textContent = '✓ Joined';
            btn.classList.add('joined');
            showToast('You joined the initiative.');
        } else {
            showToast('Could not join. Try again.');
        }
    } catch {
        showToast('Network error. Please retry.');
    }
}

// Create
async function createInitiative() {
    const title     = document.getElementById('f-title').value.trim();
    const desc      = document.getElementById('f-desc').value.trim();
    const category  = document.getElementById('f-category').value;
    const location  = document.getElementById('f-location').value.trim();
    const visibility = document.getElementById('f-visibility').value;
    const startDate = document.getElementById('f-date').value;

    if (!title) { showToast('Please enter a title.'); return; }

    const payload = { title, description: desc, category, location, visibility, startDate };

    try {
        const res = await fetch('/api/initiatives', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            const created = await res.json();
            allInitiatives.unshift(created);
            renderInitiatives();
            clearForm();
            showToast('Initiative created!');
        } else {
            showToast('Failed to create. Please try again.');
        }
    } catch {
        showToast('Could not reach server. Try again.');
    }
}

function clearForm() {
    ['f-title', 'f-desc', 'f-location', 'f-date'].forEach(id => document.getElementById(id).value = '');
    document.getElementById('f-category').value = '';
    document.getElementById('f-visibility').value = 'PUBLIC';
}

// Logout
async function logout() {
    await fetch('/api/auth/logout', { method: 'POST' });
    window.location.href = '/login';
}

// Filters
function setupFilters() {
    document.getElementById('filter-tags').addEventListener('click', (e) => {
        const tag = e.target.closest('.tag');
        if (!tag) return;
        document.querySelectorAll('.tag').forEach(t => t.classList.remove('active'));
        tag.classList.add('active');
        activeFilter = tag.dataset.cat;
        renderInitiatives();
    });
}

// Helpers
function showToast(msg) {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 3000);
}

function escHtml(str) {
    const d = document.createElement('div');
    d.appendChild(document.createTextNode(str));
    return d.innerHTML;
}

function formatDate(d) {
    if (!d) return '';
    return new Date(d).toLocaleDateString('en-SE', { month: 'short', day: 'numeric' });
}

// Run
init();
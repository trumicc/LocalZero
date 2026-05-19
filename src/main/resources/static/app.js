// LocalZero frontend logic

let currentUser = null;
let allInitiatives = [];
let activeFilter = '';
let notifications = [];

// Bootstrap
async function init() {
    await loadCurrentUser();
    await loadInitiatives();
    await loadNotifications();
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

// ___Initiatives___________________________________________________________________________________________________
async function loadInitiatives() {
    try {
        const res = await fetch('/api/initiatives');
        if (!res.ok) throw new Error('Failed to load initiatives');
        allInitiatives = await res.json();
    } catch (err) {
        console.error('Could not load initiatives:', err);
        allInitiatives = [];
        showToast('Could not load initiatives. Please refresh.');
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
        const joined = (item.participants || []).some(user => user.id === currentUser.id);
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
                <button class="btn-join ${joined ? 'joined' : ''}"
                    onclick="toggleJoinInitiative(event, ${item.id}, ${joined})">
                    ${joined ? '✓ Joined' : 'Join'}
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
async function toggleJoinInitiative(e, id, joined) {
    e.stopPropagation();
    const url = joined
        ? `/api/participation/unjoin/${id}`
        : `/api/participation/join/${id}`;
    try {
        const res = await fetch(url, { method: 'POST' });

        if (res.ok) {
            await loadInitiatives();
            showToast(joined ? 'You left the initiative.' : 'You joined the initiative.');
        } else {
            showToast('Could not update participation.');
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

//___AUTH_______________________________________________________________________________________________________________
async function logout() {
    await fetch('/api/auth/logout', { method: 'POST' });
    window.location.href = '/login';
}

//___Filters____________________________________________________________________________________________________________
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

//___Helpers____________________________________________________________________________________________________________
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

function openSustainability() {
    document.getElementById("initiatives-list").style.display = "none";
    document.getElementById("sustainability-view").style.display = "block";

    loadSustainabilityData();
    loadCommunityData();
}

function closeSustainability() {
    document.getElementById("sustainability-view").style.display = "none";
    document.getElementById("initiatives-list").style.display = "block";
}

async function logAction(type) {
    await fetch("/api/sustainability/log", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            actionType: type,
            note: ""
        })
    });

    loadSustainabilityData();
    loadCommunityData();
}

async function loadSustainabilityData() {

    const totalRes = await fetch("/api/sustainability/my-total");
    const total = await totalRes.json();

    document.getElementById("co2-total").innerText =
        total + " kg CO₂ saved";

    const actionsRes = await fetch("/api/sustainability/my-actions");
    const actions = await actionsRes.json();

    const recentActions = actions
        .slice()
        .reverse()
        .slice(0, 5);

    document.getElementById("actions-list").innerHTML =
        recentActions.map(a => `
            <div class="eco-action-item">
                <div>
                    <strong>${formatAction(a.actionType)}</strong>
                    <div class="eco-date">
                        ${formatDateTime(a.createdAt)}
                    </div>
                </div>

                <div class="eco-co2">
                    +${a.carbonSaved} kg CO₂
                </div>
            </div>
        `).join("");
}

async function loadCommunityData() {

    const totalRes = await fetch("/api/sustainability/community-total");
    const total = await totalRes.json();
    document.getElementById("community-total").innerText =
        total + " kg CO₂ saved by community";

    const lbRes = await fetch("/api/sustainability/leaderboard");
    const users = await lbRes.json();

    document.getElementById("leaderboard").innerHTML =
        users.map((u, index) => `
            <div>
                #${index + 1} ${u.name}
            </div>
        `).join("");
}

function formatAction(action) {
    return action
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(/\b\w/g, c => c.toUpperCase());
}

function formatDateTime(date) {
    return new Date(date).toLocaleString("en-SE", {
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}

//___Message mailbox____________________________________________________________________________________________________
function goToInbox() {
    window.location.href = '/messages.html';
}

// ___Notification______________________________________________________________________________________________________
async function loadNotifications() {
    try {
        const res = await fetch('/notifications', { credentials: 'include' });
        if (!res.ok) return;
        notifications = await res.json();
        updateNotifBadge();
    } catch (e) {
        console.error('Failed to load notifications', e);
    }
}

function toggleNotifications() {
    const dropdown = document.getElementById('notif-dropdown');
    const isOpen = dropdown.classList.contains('show');
    if(!isOpen){
        loadNotifications().then(renderNotifications);
    }
    dropdown.classList.toggle('show');
    renderNotifications();
}

function renderNotifications() {
    const list = document.getElementById('notif-list');
    const empty = document.getElementById('notif-empty');

    if (!notifications.length) {
        list.innerHTML = '';
        empty.style.display = 'block';
        return;
    }

    empty.style.display = 'none';
    list.innerHTML = notifications.map(n =>`
        <div class="notif-item ${n.read ? '' : 'unread'}" onclick="handleNotifClick('${n.id}', '${n.type}')">
            <span class="notif-type-icon">${notifIcon(n.type)}</span>
            ${escHtml(n.content)}
        </div>
    `).join('');
}

function notifIcon(type){
    const icons = {
        NEW_MESSAGE:    '✉️',
        NEW_UPDATE:     '📢',
        NEW_COMMENT:    '💬',
        LIKE:           '👍',
        JOIN_INITIATIVE:'🌱',
    };
    return icons[type] || '🔔';
}

async function handleNotifClick(id, type) {
    // Mark all as read on click (simple UX — can be made per-item)
    await markAllRead();

    // Navigate based on type
    if (type === 'NEW_MESSAGE') {
        window.location.href = '/mailbox';
    } else {
        window.location.href = '/';
    }
}

async function markAllRead() {
    try {
        await fetch('/notifications/mark-all-read', {
            method: 'POST',
            credentials: 'include'
        });
        notifications.forEach(n => n.read = true);
        updateNotifBadge();
        renderNotifications();
    } catch (e) {
        console.error('Failed to mark notifications as read', e);
    }
}

function updateNotifBadge() {
    const unread = notifications.filter(n => !n.read).length;
    const badge  = document.getElementById('notif-count');
    if (!badge) return;
    badge.textContent = unread > 9 ? '9+' : unread;
    badge.classList.toggle('hidden', unread === 0);
}

// Close dropdown when clicking outside
document.addEventListener('click', (e) => {
    const wrapper  = document.getElementById('notif-wrapper');
    const dropdown = document.getElementById('notif-dropdown');
    if (wrapper && dropdown && !wrapper.contains(e.target)) {
        dropdown.classList.remove('show');
    }
});

document.addEventListener('click', (e) => {
    const wrapper = document.getElementById('notif-wrapper');
    const dropdown = document.getElementById('notif-dropdown');

    if (!wrapper.contains(e.target)) {
        dropdown.classList.remove('show');
    }
});

// Run
init();
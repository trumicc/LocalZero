
let selectedUserEmail = null;

// Load inbox
async function loadInbox() {
    const res = await fetch('/api/messages/inbox', {
        credentials: 'include'
    });
    const messages = await res.json();
    const list = document.getElementById('inbox-list');

    if (!messages.length) {
        list.innerHTML = `<p style="font-size:0.85rem;color:#6b7280;">No messages</p>`;
        return;
    }

    list.innerHTML = messages.map(m => `
        <div class="inbox-item" onclick="openMessage(${m.id})">
            <strong>${m.senderId}</strong><br/>
            <span>${m.content.substring(0, 40)}...</span>
        </div>
    `).join('');
}

// Open message
async function openMessage(id) {
    const res = await fetch(`/api/messages/${id}`, {
        credentials: 'include'
    });
    const m = await res.json();
    const view = document.getElementById('message-view');

    view.innerHTML = `
        <div class="card-title">Message</div>
        <p><strong>From:</strong> ${m.senderId}</p>
        <p><strong>Date:</strong> ${new Date(m.timestamp).toLocaleString()}</p>
        <hr style="margin:1rem 0;border:none;border-top:1px solid #eee;" />
        <p style="line-height:1.6;">${m.content}</p>
    `;
}


// Send message
async function sendMessage() {
    const email = document.getElementById('msg-to').value.trim();
    const content = document.getElementById('msg-content').value.trim();

    if (!email || !content) {
        showToast('Please fill all fields.');
        return;
    }

    try {
        const res = await fetch('/api/messages', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({
                receiverEmail: email,
                content: content
            })
        });
        if (res.ok) {
            showToast('Message sent successfully');
            document.getElementById('msg-content').value = '';
            loadInbox();
        } else {
            const err = await res.json();
            showToast(err.error || 'Failed to send message');
        }
    } catch {
        showToast('Network error. Try again.');
    }
}

//search and select receiver
async function searchUsers() {
    const query = document.getElementById('msg-to').value;
    if (query.length < 2) return;

    const res = await fetch(`/api/users/search?query=${query}`);
    const users = await res.json();
    const container = document.getElementById('user-results');
    container.innerHTML = users.map(u => `
        <div class="user-result" onclick="selectUser('${u.email}')">
            ${u.email}
        </div>
    `).join('');
}

function selectUser(email) {
    selectedUserEmail = email;
    document.getElementById('msg-to').value = email;
    document.getElementById('user-results').innerHTML = '';
}


// Init
loadInbox();
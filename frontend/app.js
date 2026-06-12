// ============================================================
//  Configuração
// ============================================================
const API_BASE = "http://localhost:8080";

// Estado em memória (token e dados auxiliares)
let token = null;
let userRole = null;

// ============================================================
//  Autenticação
// ============================================================
async function login() {
    const email = document.getElementById("login-email").value;
    const senha = document.getElementById("login-senha").value;
    const errorEl = document.getElementById("login-error");
    errorEl.classList.add("hidden");

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, senha })
        });

        if (!res.ok) {
            throw new Error("Email ou senha inválidos.");
        }

        const data = await res.json();
        token = data.token;
        userRole = data.role;

        // Troca de tela
        document.getElementById("login-section").classList.add("hidden");
        document.getElementById("main-panel").classList.remove("hidden");
        document.getElementById("user-info").textContent = `${email} (${userRole})`;

        // Carrega os dados iniciais
        await carregarSalas();
        await carregarUsuarios();
        await carregarReservas();
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.classList.remove("hidden");
    }
}

function logout() {
    token = null;
    userRole = null;
    document.getElementById("main-panel").classList.add("hidden");
    document.getElementById("login-section").classList.remove("hidden");
}

// Helper para requisições autenticadas
function authHeaders() {
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    };
}

// ============================================================
//  Salas
// ============================================================
async function carregarSalas() {
    try {
        const res = await fetch(`${API_BASE}/api/v1/rooms`, { headers: authHeaders() });
        const salas = await res.json();
        renderSalas(salas);
        preencherSelectSalas(salas);
    } catch (err) {
        console.error("Erro ao carregar salas:", err);
    }
}

function renderSalas(salas) {
    const list = document.getElementById("salas-list");
    list.innerHTML = "";
    salas.forEach(sala => {
        const li = document.createElement("li");
        li.innerHTML = `
            <div>
                <span class="item-title">${sala.nome}</span>
                <div class="item-detail">Capacidade: ${sala.capacidade} &middot; ${sala.localizacao || "—"}</div>
            </div>
            <span class="badge ${sala.ativa ? "badge-ativa" : "badge-cancelada"}">
                ${sala.ativa ? "Ativa" : "Inativa"}
            </span>
        `;
        list.appendChild(li);
    });
}

async function criarSala(event) {
    event.preventDefault();
    const nome = document.getElementById("sala-nome").value;
    const capacidade = parseInt(document.getElementById("sala-capacidade").value);
    const localizacao = document.getElementById("sala-localizacao").value;

    try {
        const res = await fetch(`${API_BASE}/api/v1/rooms`, {
            method: "POST",
            headers: authHeaders(),
            body: JSON.stringify({ nome, capacidade, localizacao })
        });
        if (!res.ok) throw new Error("Erro ao criar sala");
        event.target.reset();
        await carregarSalas();
    } catch (err) {
        alert(err.message);
    }
}

// ============================================================
//  Usuários
// ============================================================
async function carregarUsuarios() {
    try {
        const res = await fetch(`${API_BASE}/api/v1/users`, { headers: authHeaders() });
        const usuarios = await res.json();
        renderUsuarios(usuarios);
        preencherSelectUsuarios(usuarios);
    } catch (err) {
        console.error("Erro ao carregar usuários:", err);
    }
}

function renderUsuarios(usuarios) {
    const list = document.getElementById("usuarios-list");
    list.innerHTML = "";
    usuarios.forEach(user => {
        const li = document.createElement("li");
        li.innerHTML = `
            <div>
                <span class="item-title">${user.nome}</span>
                <div class="item-detail">${user.email}</div>
            </div>
        `;
        list.appendChild(li);
    });
}

async function criarUsuario(event) {
    event.preventDefault();
    const nome = document.getElementById("user-nome").value;
    const email = document.getElementById("user-email").value;

    try {
        const res = await fetch(`${API_BASE}/api/v1/users`, {
            method: "POST",
            headers: authHeaders(),
            body: JSON.stringify({ nome, email })
        });
        if (!res.ok) throw new Error("Erro ao criar usuário");
        event.target.reset();
        await carregarUsuarios();
    } catch (err) {
        alert(err.message);
    }
}

// ============================================================
//  Reservas
// ============================================================
async function carregarReservas() {
    try {
        const res = await fetch(`${API_BASE}/api/v1/bookings`, { headers: authHeaders() });
        const reservas = await res.json();
        renderReservas(reservas);
    } catch (err) {
        console.error("Erro ao carregar reservas:", err);
    }
}

function renderReservas(reservas) {
    const list = document.getElementById("reservas-list");
    list.innerHTML = "";
    reservas.forEach(reserva => {
        const li = document.createElement("li");
        const inicio = formatarData(reserva.inicio);
        const fim = formatarData(reserva.fim);
        li.innerHTML = `
            <div>
                <span class="item-title">Sala #${reserva.roomId} &middot; Usuário #${reserva.userId}</span>
                <div class="item-detail">${inicio} → ${fim}</div>
            </div>
            <span class="badge ${reserva.status === "ATIVA" ? "badge-ativa" : "badge-cancelada"}">
                ${reserva.status}
            </span>
        `;
        list.appendChild(li);
    });
}

async function criarReserva(event) {
    event.preventDefault();
    const roomId = parseInt(document.getElementById("reserva-sala").value);
    const userId = parseInt(document.getElementById("reserva-usuario").value);
    const inicio = document.getElementById("reserva-inicio").value;
    const fim = document.getElementById("reserva-fim").value;
    const msgEl = document.getElementById("reserva-msg");
    msgEl.classList.add("hidden");

    try {
        const res = await fetch(`${API_BASE}/api/v1/bookings`, {
            method: "POST",
            headers: authHeaders(),
            body: JSON.stringify({ roomId, userId, inicio, fim })
        });

        if (res.status === 422) {
            throw new Error("Conflito de horário: já existe uma reserva nesse intervalo para esta sala.");
        }
        if (!res.ok) {
            throw new Error("Erro ao criar reserva.");
        }

        event.target.reset();
        msgEl.textContent = "Reserva criada com sucesso!";
        msgEl.className = "error-msg success-msg";
        msgEl.classList.remove("hidden");
        await carregarReservas();
    } catch (err) {
        msgEl.textContent = err.message;
        msgEl.className = "error-msg";
        msgEl.classList.remove("hidden");
    }
}

// ============================================================
//  Helpers de UI
// ============================================================
function preencherSelectSalas(salas) {
    const select = document.getElementById("reserva-sala");
    select.innerHTML = '<option value="">Selecione a sala</option>';
    salas.forEach(sala => {
        const opt = document.createElement("option");
        opt.value = sala.id;
        opt.textContent = `${sala.nome} (cap. ${sala.capacidade})`;
        select.appendChild(opt);
    });
}

function preencherSelectUsuarios(usuarios) {
    const select = document.getElementById("reserva-usuario");
    select.innerHTML = '<option value="">Selecione o usuário</option>';
    usuarios.forEach(user => {
        const opt = document.createElement("option");
        opt.value = user.id;
        opt.textContent = user.nome;
        select.appendChild(opt);
    });
}

function formatarData(isoString) {
    if (!isoString) return "—";
    const d = new Date(isoString);
    return d.toLocaleString("pt-BR", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit"
    });
}

/**
 * MyPyme Stock - Aplicación de Gestión de Inventario
 * JavaScript para interacción con la API REST
 */

// Configuración de la API
const API_BASE = '/api';

// Estado de la aplicación
let estadoApp = {
    articulos: [],
    categorias: [],
    ubicaciones: [],
    movimientos: [],
    paginaActual: 'dashboard'
};

// =====================================================
// UTILIDADES
// =====================================================

/**
 * Realiza una petición a la API
 */
async function fetchAPI(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const config = {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        },
        ...options
    };
    
    try {
        const response = await fetch(url, config);
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.mensaje || `Error ${response.status}`);
        }
        
        // Si no hay contenido, retornar null
        if (response.status === 204) {
            return null;
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error en API:', error);
        throw error;
    }
}

/**
 * Formatea un número como moneda
 */
function formatearMoneda(valor) {
    return new Intl.NumberFormat('es-AR', {
        style: 'currency',
        currency: 'ARS'
    }).format(valor || 0);
}

/**
 * Formatea una fecha
 */
function formatearFecha(fecha) {
    if (!fecha) return '-';
    return new Date(fecha).toLocaleString('es-AR', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Muestra un toast de notificación
 */
function mostrarToast(mensaje, tipo = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${tipo}`;
    toast.innerHTML = `
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            ${tipo === 'success' 
                ? '<polyline points="20 6 9 17 4 12"/>'
                : tipo === 'error'
                    ? '<circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>'
                    : '<path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>'
            }
        </svg>
        <span>${mensaje}</span>
    `;
    
    container.appendChild(toast);
    
    // Remover después de 4 segundos
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

/**
 * Obtiene el badge de estado para un artículo
 */
function getBadgeEstado(articulo) {
    if (articulo.cantidadStock === 0) {
        return '<span class="badge badge-error">Agotado</span>';
    } else if (articulo.cantidadStock <= articulo.stockMinimo) {
        return '<span class="badge badge-warning">Stock Bajo</span>';
    } else {
        return '<span class="badge badge-success">Normal</span>';
    }
}

// =====================================================
// NAVEGACIÓN
// =====================================================

/**
 * Maneja la navegación entre páginas
 */
function navegarA(pagina) {
    // Ocultar todas las páginas
    document.querySelectorAll('.page').forEach(p => p.style.display = 'none');
    
    // Mostrar la página seleccionada
    const paginaElement = document.getElementById(`page-${pagina}`);
    if (paginaElement) {
        paginaElement.style.display = 'block';
    }
    
    // Actualizar navegación activa
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
        if (item.dataset.page === pagina) {
            item.classList.add('active');
        }
    });
    
    estadoApp.paginaActual = pagina;
    
    // Cargar datos específicos de la página
    switch (pagina) {
        case 'dashboard':
            cargarDashboard();
            break;
        case 'articulos':
            cargarArticulosCompleto();
            break;
        case 'categorias':
            cargarCategorias();
            break;
        case 'movimientos':
            cargarMovimientos();
            break;
    }
}

// =====================================================
// MODALES
// =====================================================

function abrirModal(id) {
    document.getElementById(id).classList.add('active');
}

function cerrarModal(id) {
    document.getElementById(id).classList.remove('active');
}

function abrirModalArticulo(articulo = null) {
    const modal = document.getElementById('modal-articulo');
    const titulo = document.getElementById('modal-articulo-titulo');
    const form = document.getElementById('form-articulo');
    
    if (articulo) {
        titulo.textContent = 'Editar Artículo';
        document.getElementById('articulo-id').value = articulo.id;
        document.getElementById('articulo-nombre').value = articulo.nombre;
        document.getElementById('articulo-descripcion').value = articulo.descripcion || '';
        document.getElementById('articulo-categoria').value = articulo.categoriaId;
        document.getElementById('articulo-stock').value = articulo.cantidadStock;
        document.getElementById('articulo-stock-minimo').value = articulo.stockMinimo;
        document.getElementById('articulo-precio').value = articulo.precioUnitario;
    } else {
        titulo.textContent = 'Nuevo Artículo';
        form.reset();
        document.getElementById('articulo-id').value = '';
    }
    
    cargarCategoriasSelect();
    abrirModal('modal-articulo');
}

function abrirModalCategoria() {
    document.getElementById('form-categoria').reset();
    abrirModal('modal-categoria');
}

function abrirModalMovimiento(tipo) {
    const titulo = document.getElementById('modal-movimiento-titulo');
    titulo.textContent = tipo === 'ENTRADA' ? 'Registrar Entrada' : 'Registrar Salida';
    document.getElementById('movimiento-tipo').value = tipo;
    document.getElementById('form-movimiento').reset();
    document.getElementById('movimiento-tipo').value = tipo;
    
    cargarArticulosSelect();
    abrirModal('modal-movimiento');
}

// =====================================================
// DASHBOARD
// =====================================================

async function cargarDashboard() {
    try {
        // Cargar estadísticas
        const reporteData = await fetchAPI('/reportes/stock');
        
        if (reporteData && reporteData.resumen) {
            document.getElementById('stat-total-articulos').textContent = 
                reporteData.resumen.totalArticulos || 0;
            document.getElementById('stat-valor-total').textContent = 
                formatearMoneda(reporteData.resumen.valorTotalStock);
            document.getElementById('stat-stock-bajo').textContent = 
                reporteData.resumen.articulosStockBajo || 0;
            document.getElementById('stat-agotados').textContent = 
                reporteData.resumen.articulosAgotados || 0;
        }
        
        // Cargar artículos recientes
        await cargarArticulos();
    } catch (error) {
        console.error('Error cargando dashboard:', error);
        // Valores por defecto si falla
        document.getElementById('stat-total-articulos').textContent = '0';
        document.getElementById('stat-valor-total').textContent = '$0';
        document.getElementById('stat-stock-bajo').textContent = '0';
        document.getElementById('stat-agotados').textContent = '0';
    }
}

// =====================================================
// ARTÍCULOS
// =====================================================

async function cargarArticulos() {
    const tbody = document.getElementById('articulos-table-body');
    tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 20px;">Cargando...</td></tr>';
    
    try {
        const data = await fetchAPI('/articulos?page=0&size=10');
        estadoApp.articulos = data.contenido || [];
        
        if (estadoApp.articulos.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align: center; padding: 40px; color: var(--text-secondary);">
                        No hay artículos registrados
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = estadoApp.articulos.map(art => `
            <tr>
                <td>
                    <strong>${art.nombre}</strong>
                    <br><span style="color: var(--text-tertiary); font-size: 0.8rem;">${art.codigo || ''}</span>
                </td>
                <td>${art.categoriaNombre || '-'}</td>
                <td><strong>${art.cantidadStock}</strong> unidades</td>
                <td>${formatearMoneda(art.precioUnitario)}</td>
                <td>${getBadgeEstado(art)}</td>
                <td>
                    <button class="btn btn-icon" onclick='abrirModalArticulo(${JSON.stringify(art)})' title="Editar">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 40px; color: var(--accent-error);">
                    Error al cargar artículos: ${error.message}
                </td>
            </tr>
        `;
    }
}

async function cargarArticulosCompleto() {
    const tbody = document.getElementById('articulos-full-table-body');
    tbody.innerHTML = '<tr><td colspan="10" style="text-align: center; padding: 20px;">Cargando...</td></tr>';
    
    try {
        const data = await fetchAPI('/articulos?page=0&size=100');
        estadoApp.articulos = data.contenido || [];
        
        if (estadoApp.articulos.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="10" style="text-align: center; padding: 40px; color: var(--text-secondary);">
                        No hay artículos registrados. ¡Crea el primero!
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = estadoApp.articulos.map(art => `
            <tr>
                <td>${art.id}</td>
                <td><strong>${art.nombre}</strong></td>
                <td style="max-width: 200px; overflow: hidden; text-overflow: ellipsis;">${art.descripcion || '-'}</td>
                <td><span class="badge badge-info">${art.categoriaNombre || '-'}</span></td>
                <td><strong>${art.cantidadStock}</strong></td>
                <td>${art.stockMinimo}</td>
                <td>${formatearMoneda(art.precioUnitario)}</td>
                <td>${formatearMoneda(art.valorTotal)}</td>
                <td>${getBadgeEstado(art)}</td>
                <td>
                    <div style="display: flex; gap: 4px;">
                        <button class="btn btn-icon" onclick='abrirModalArticulo(${JSON.stringify(art)})' title="Editar">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                            </svg>
                        </button>
                        <button class="btn btn-icon" onclick="eliminarArticulo(${art.id})" title="Eliminar" style="color: var(--accent-error);">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="3 6 5 6 21 6"/>
                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                            </svg>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        tbody.innerHTML = `
            <tr>
                <td colspan="10" style="text-align: center; padding: 40px; color: var(--accent-error);">
                    Error al cargar artículos: ${error.message}
                </td>
            </tr>
        `;
    }
}

async function guardarArticulo(event) {
    event.preventDefault();
    
    const id = document.getElementById('articulo-id').value;
    const datos = {
        nombre: document.getElementById('articulo-nombre').value,
        descripcion: document.getElementById('articulo-descripcion').value,
        categoriaId: parseInt(document.getElementById('articulo-categoria').value),
        cantidadStock: parseInt(document.getElementById('articulo-stock').value),
        stockMinimo: parseInt(document.getElementById('articulo-stock-minimo').value),
        precioUnitario: parseFloat(document.getElementById('articulo-precio').value)
    };
    
    try {
        if (id) {
            await fetchAPI(`/articulos/${id}`, {
                method: 'PUT',
                body: JSON.stringify(datos)
            });
            mostrarToast('Artículo actualizado correctamente');
        } else {
            await fetchAPI('/articulos', {
                method: 'POST',
                body: JSON.stringify(datos)
            });
            mostrarToast('Artículo creado correctamente');
        }
        
        cerrarModal('modal-articulo');
        
        // Recargar según página actual
        if (estadoApp.paginaActual === 'dashboard') {
            cargarDashboard();
        } else {
            cargarArticulosCompleto();
        }
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

async function eliminarArticulo(id) {
    if (!confirm('¿Estás seguro de eliminar este artículo?')) return;
    
    try {
        await fetchAPI(`/articulos/${id}`, { method: 'DELETE' });
        mostrarToast('Artículo eliminado');
        cargarArticulosCompleto();
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

async function cargarArticulosSelect() {
    try {
        const data = await fetchAPI('/articulos?page=0&size=100');
        const select = document.getElementById('movimiento-articulo');
        select.innerHTML = '<option value="">Seleccionar artículo</option>';
        
        (data.contenido || []).forEach(art => {
            select.innerHTML += `<option value="${art.id}">${art.nombre} (Stock: ${art.cantidadStock})</option>`;
        });
    } catch (error) {
        console.error('Error cargando artículos:', error);
    }
}

// =====================================================
// CATEGORÍAS
// =====================================================

async function cargarCategorias() {
    const grid = document.getElementById('categorias-grid');
    grid.innerHTML = '<div class="skeleton" style="height: 120px;"></div>';
    
    try {
        const data = await fetchAPI('/categorias');
        estadoApp.categorias = data || [];
        
        if (estadoApp.categorias.length === 0) {
            grid.innerHTML = `
                <div style="text-align: center; padding: 40px; color: var(--text-secondary);">
                    No hay categorías registradas. ¡Crea la primera!
                </div>
            `;
            return;
        }
        
        grid.innerHTML = estadoApp.categorias.map(cat => `
            <div class="card">
                <div class="card-header">
                    <span class="card-title">${cat.nombre}</span>
                    <button class="btn btn-icon" onclick="eliminarCategoria(${cat.id})" title="Eliminar">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                        </svg>
                    </button>
                </div>
                <p style="font-size: 0.875rem;">${cat.descripcion || 'Sin descripción'}</p>
                <div style="margin-top: 12px;">
                    <span class="badge badge-info">${cat.totalArticulos || 0} artículos</span>
                </div>
            </div>
        `).join('');
    } catch (error) {
        grid.innerHTML = `
            <div style="text-align: center; padding: 40px; color: var(--accent-error);">
                Error al cargar categorías: ${error.message}
            </div>
        `;
    }
}

async function cargarCategoriasSelect() {
    try {
        const data = await fetchAPI('/categorias');
        const select = document.getElementById('articulo-categoria');
        select.innerHTML = '<option value="">Seleccionar categoría</option>';
        
        (data || []).forEach(cat => {
            select.innerHTML += `<option value="${cat.id}">${cat.nombre}</option>`;
        });
    } catch (error) {
        console.error('Error cargando categorías:', error);
    }
}

async function guardarCategoria(event) {
    event.preventDefault();
    
    const datos = {
        nombre: document.getElementById('categoria-nombre').value,
        descripcion: document.getElementById('categoria-descripcion').value
    };
    
    try {
        await fetchAPI('/categorias', {
            method: 'POST',
            body: JSON.stringify(datos)
        });
        
        mostrarToast('Categoría creada correctamente');
        cerrarModal('modal-categoria');
        cargarCategorias();
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

async function eliminarCategoria(id) {
    if (!confirm('¿Estás seguro de eliminar esta categoría?')) return;
    
    try {
        await fetchAPI(`/categorias/${id}`, { method: 'DELETE' });
        mostrarToast('Categoría eliminada');
        cargarCategorias();
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

// =====================================================
// MOVIMIENTOS
// =====================================================

async function cargarMovimientos() {
    const tbody = document.getElementById('movimientos-table-body');
    tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 20px;">Cargando...</td></tr>';
    
    try {
        // Obtener movimientos de los últimos 30 días
        const hoy = new Date();
        const hace30Dias = new Date(hoy.getTime() - (30 * 24 * 60 * 60 * 1000));
        
        const fechaInicio = hace30Dias.toISOString().split('T')[0];
        const fechaFin = hoy.toISOString().split('T')[0];
        
        const data = await fetchAPI(`/movimientos/rango-fechas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
        estadoApp.movimientos = data || [];
        
        if (estadoApp.movimientos.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" style="text-align: center; padding: 40px; color: var(--text-secondary);">
                        No hay movimientos registrados
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = estadoApp.movimientos.map(mov => `
            <tr>
                <td>${formatearFecha(mov.fechaMovimiento)}</td>
                <td><strong>${mov.articuloNombre || 'Artículo #' + mov.articuloId}</strong></td>
                <td>
                    <span class="badge ${mov.tipoMovimiento === 'ENTRADA' ? 'badge-success' : mov.tipoMovimiento === 'SALIDA' ? 'badge-error' : 'badge-warning'}">
                        ${mov.tipoMovimiento}
                    </span>
                </td>
                <td><strong>${mov.cantidad}</strong></td>
                <td>${mov.stockAnterior !== null ? mov.stockAnterior : '-'}</td>
                <td>${mov.stockNuevo !== null ? mov.stockNuevo : '-'}</td>
                <td>${mov.motivo || '-'}</td>
            </tr>
        `).join('');
    } catch (error) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 40px; color: var(--accent-error);">
                    Error al cargar movimientos: ${error.message}
                </td>
            </tr>
        `;
    }
}

async function guardarMovimiento(event) {
    event.preventDefault();
    
    const tipo = document.getElementById('movimiento-tipo').value;
    const datos = {
        articuloId: parseInt(document.getElementById('movimiento-articulo').value),
        cantidad: parseInt(document.getElementById('movimiento-cantidad').value),
        motivo: document.getElementById('movimiento-motivo').value,
        numeroReferencia: document.getElementById('movimiento-referencia').value
    };
    
    const endpoint = tipo === 'ENTRADA' ? '/movimientos/entrada' : '/movimientos/salida';
    
    try {
        await fetchAPI(endpoint, {
            method: 'POST',
            body: JSON.stringify(datos)
        });
        
        mostrarToast(`${tipo === 'ENTRADA' ? 'Entrada' : 'Salida'} registrada correctamente`);
        cerrarModal('modal-movimiento');
        cargarMovimientos();
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

// =====================================================
// INICIALIZACIÓN
// =====================================================

document.addEventListener('DOMContentLoaded', () => {
    // Configurar navegación
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const pagina = item.dataset.page;
            if (pagina) {
                navegarA(pagina);
            }
        });
    });
    
    // Cerrar modales al hacer clic fuera
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                overlay.classList.remove('active');
            }
        });
    });
    
    // Cerrar modales con Escape
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-overlay.active').forEach(modal => {
                modal.classList.remove('active');
            });
        }
    });
    
    // Cargar dashboard inicial
    cargarDashboard();
});

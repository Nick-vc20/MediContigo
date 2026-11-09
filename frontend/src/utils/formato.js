// Funciones de formato compartidas por el mapa y el perfil del centro

const DIAS_CORTOS = ['', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];

// El backend usa 1 = lunes ... 7 = domingo; JavaScript usa 0 = domingo
export const diaDeHoy = () => new Date().getDay() || 7;

export function formatoHora(hora) {
  if (Array.isArray(hora)) {
    return hora.slice(0, 2).map((n) => String(n).padStart(2, '0')).join(':');
  }
  return String(hora).slice(0, 5);
}

function rangoHoras(inicio, fin) {
  const a = formatoHora(inicio);
  const b = formatoHora(fin);
  return a === '00:00' && b >= '23:59' ? '24 horas' : `${a} - ${b}`;
}

// [1,2,3,4,5] -> "Lun a Vie" | [1,3,5] -> "Lun, Mié, Vie" | [6,7] -> "Sáb y Dom"
function nombrarDias(dias) {
  const tramos = [];
  for (const dia of [...new Set(dias)].sort((a, b) => a - b)) {
    const ultimo = tramos[tramos.length - 1];
    if (ultimo && dia === ultimo[ultimo.length - 1] + 1) ultimo.push(dia);
    else tramos.push([dia]);
  }
  return tramos
    .map((t) => {
      if (t.length === 1) return DIAS_CORTOS[t[0]];
      if (t.length === 2) return `${DIAS_CORTOS[t[0]]} y ${DIAS_CORTOS[t[1]]}`;
      return `${DIAS_CORTOS[t[0]]} a ${DIAS_CORTOS[t[t.length - 1]]}`;
    })
    .join(', ');
}

// Agrupa los dias que comparten las mismas horas para mostrar menos lineas
export function resumirHorarios(horarios) {
  const grupos = new Map();
  for (const h of horarios) {
    const horas = rangoHoras(h.horaInicio, h.horaFin);
    if (!grupos.has(horas)) grupos.set(horas, []);
    grupos.get(horas).push(h.diaSemana);
  }
  return [...grupos]
    .map(([horas, dias]) => ({
      horas,
      dias: nombrarDias(dias),
      primerDia: Math.min(...dias),
      incluyeHoy: dias.includes(diaDeHoy()),
    }))
    .sort((a, b) => a.primerDia - b.primerDia || a.horas.localeCompare(b.horas));
}

export const atiendeHoy = (horarios) => horarios.some((h) => h.diaSemana === diaDeHoy());

const OCUPACION = {
  BAJO: { etiqueta: 'Ocupación baja', clase: 'bajo' },
  MEDIO: { etiqueta: 'Ocupación media', clase: 'medio' },
  ALTO: { etiqueta: 'Ocupación alta', clase: 'alto' },
};

export const ocupacionDe = (nivel) =>
  OCUPACION[nivel] ?? { etiqueta: 'Ocupación no informada', clase: 'desconocido' };

export const formatoCosto = (costo) =>
  costo > 0 ? `S/ ${Number(costo).toFixed(2)}` : 'No especificado';

// Color y letra del marcador segun el tipo de establecimiento
export function estiloTipo(tipo) {
  const nombre = (tipo || '').toLowerCase();
  if (nombre.includes('hospital')) return { color: '#dc2626', letra: 'H' };
  if (nombre.includes('clínica') || nombre.includes('clinica')) return { color: '#2563eb', letra: 'C' };
  if (nombre.includes('posta')) return { color: '#16a34a', letra: 'P' };
  if (nombre.includes('centro')) return { color: '#7c3aed', letra: 'M' };
  return { color: '#6b7280', letra: '+' };
}

// Leyenda del mapa: un ejemplo de cada tipo de establecimiento
export const TIPOS_LEYENDA = ['Hospital', 'Clínica', 'Posta', 'Centro Médico'].map((etiqueta) => ({
  etiqueta,
  ...estiloTipo(etiqueta),
}));

export const urlComoLlegar = (lat, lng) =>
  `https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`;

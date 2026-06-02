package com.contabilidad.service;

import com.contabilidad.model.*;
import com.contabilidad.model.*;
import com.contabilidad.repository.AsientoContableRepository;
import com.contabilidad.repository.CuentaContableRepository;
import com.contabilidad.repository.LineaAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private AsientoContableRepository asientoRepository;

    @Autowired
    private CuentaContableRepository cuentaRepository;

    @Autowired
    private LineaAsientoRepository lineaRepository;

    public Map<String, Object> generarBalanceGeneral() {
        Map<String, Object> balance = new LinkedHashMap<>();

        double totalActivo = calcularSaldoPorTipo(TipoCuenta.ACTIVO);
        double totalPasivo = calcularSaldoPorTipo(TipoCuenta.PASIVO);
        double totalPatrimonio = calcularSaldoPorTipo(TipoCuenta.PATRIMONIO);

        balance.put("ACTIVOS", detallePorTipo(TipoCuenta.ACTIVO));
        balance.put("TOTAL_ACTIVO", totalActivo);
        balance.put("PASIVOS", detallePorTipo(TipoCuenta.PASIVO));
        balance.put("TOTAL_PASIVO", totalPasivo);
        balance.put("PATRIMONIO", detallePorTipo(TipoCuenta.PATRIMONIO));
        balance.put("TOTAL_PATRIMONIO", totalPatrimonio);
        balance.put("TOTAL_PASIVO_PATRIMONIO", totalPasivo + totalPatrimonio);
        balance.put("CUADRA", Math.abs(totalActivo - (totalPasivo + totalPatrimonio)) < 0.01);

        return balance;
    }

    public Map<String, Object> generarEstadoResultados() {
        Map<String, Object> estado = new LinkedHashMap<>();

        double totalIngresos = calcularSaldoPorTipo(TipoCuenta.INGRESO);
        double totalGastos = calcularSaldoPorTipo(TipoCuenta.GASTO);
        double utilidad = totalIngresos - totalGastos;

        estado.put("INGRESOS", detallePorTipo(TipoCuenta.INGRESO));
        estado.put("TOTAL_INGRESOS", totalIngresos);
        estado.put("GASTOS", detallePorTipo(TipoCuenta.GASTO));
        estado.put("TOTAL_GASTOS", totalGastos);
        estado.put("UTILIDAD_NETA", utilidad);
        estado.put("RESULTADO", utilidad >= 0 ? "UTILIDAD" : "PÉRDIDA");

        return estado;
    }

    public List<Map<String, Object>> generarLibroDiario() {
        List<AsientoContable> asientos = asientoRepository.findByEstado(EstadoAsiento.ACTIVO);
        asientos.sort(Comparator.comparing(AsientoContable::getFecha));

        List<Map<String, Object>> libro = new ArrayList<>();
        for (AsientoContable asiento : asientos) {
            Map<String, Object> entrada = new LinkedHashMap<>();
            entrada.put("id", asiento.getId());
            entrada.put("fecha", asiento.getFecha());
            entrada.put("descripcion", asiento.getDescripcion());
            entrada.put("estado", asiento.getEstado());
            entrada.put("usuarioCreador", asiento.getUsuarioCreadorId());

            List<Map<String, Object>> lineasInfo = new ArrayList<>();
            for (LineaAsiento linea : asiento.getLineas()) {
                Map<String, Object> lineaMap = new LinkedHashMap<>();
                lineaMap.put("cuenta", linea.getCuentaContable() != null
                        ? linea.getCuentaContable().getCodigo() + " - " + linea.getCuentaContable().getNombre()
                        : "N/A");
                lineaMap.put("debito", linea.getDebito());
                lineaMap.put("credito", linea.getCredito());
                lineaMap.put("tercero", linea.getTercero() != null ? linea.getTercero().getNombre() : "-");
                lineasInfo.add(lineaMap);
            }
            entrada.put("lineas", lineasInfo);
            entrada.put("totalDebitos", asiento.getLineas().stream().mapToDouble(LineaAsiento::getDebito).sum());
            entrada.put("totalCreditos", asiento.getLineas().stream().mapToDouble(LineaAsiento::getCredito).sum());
            libro.add(entrada);
        }
        return libro;
    }

    public Map<String, Object> generarLibroMayor(String cuentaId) {
        CuentaContable cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + cuentaId));

        List<LineaAsiento> movimientos = lineaRepository.findByCuentaContable(cuenta);

        double totalDebitos = movimientos.stream().mapToDouble(LineaAsiento::getDebito).sum();
        double totalCreditos = movimientos.stream().mapToDouble(LineaAsiento::getCredito).sum();
        double saldo = totalDebitos - totalCreditos;

        Map<String, Object> mayor = new LinkedHashMap<>();
        mayor.put("cuenta", cuenta.getCodigo() + " - " + cuenta.getNombre());
        mayor.put("tipo", cuenta.getTipo());
        mayor.put("movimientos", movimientos.stream().map(l -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", l.getId());
            m.put("debito", l.getDebito());
            m.put("credito", l.getCredito());
            m.put("tercero", l.getTercero() != null ? l.getTercero().getNombre() : "-");
            return m;
        }).collect(Collectors.toList()));
        mayor.put("totalDebitos", totalDebitos);
        mayor.put("totalCreditos", totalCreditos);
        mayor.put("saldo", saldo);

        return mayor;
    }

    private double calcularSaldoPorTipo(TipoCuenta tipo) {
        List<CuentaContable> cuentas = cuentaRepository.findByTipo(tipo);
        double total = 0;
        for (CuentaContable cuenta : cuentas) {
            List<LineaAsiento> movimientos = lineaRepository.findByCuentaContable(cuenta);
            double debitos = movimientos.stream().mapToDouble(LineaAsiento::getDebito).sum();
            double creditos = movimientos.stream().mapToDouble(LineaAsiento::getCredito).sum();
            total += (tipo == TipoCuenta.ACTIVO || tipo == TipoCuenta.GASTO)
                    ? (debitos - creditos)
                    : (creditos - debitos);
        }
        return total;
    }

    private List<Map<String, Object>> detallePorTipo(TipoCuenta tipo) {
        List<CuentaContable> cuentas = cuentaRepository.findByTipo(tipo);
        List<Map<String, Object>> detalle = new ArrayList<>();
        for (CuentaContable cuenta : cuentas) {
            List<LineaAsiento> movimientos = lineaRepository.findByCuentaContable(cuenta);
            if (movimientos.isEmpty()) continue;
            double debitos = movimientos.stream().mapToDouble(LineaAsiento::getDebito).sum();
            double creditos = movimientos.stream().mapToDouble(LineaAsiento::getCredito).sum();
            double saldo = (tipo == TipoCuenta.ACTIVO || tipo == TipoCuenta.GASTO)
                    ? (debitos - creditos) : (creditos - debitos);
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("codigo", cuenta.getCodigo());
            fila.put("nombre", cuenta.getNombre());
            fila.put("saldo", saldo);
            detalle.add(fila);
        }
        return detalle;
    }
}

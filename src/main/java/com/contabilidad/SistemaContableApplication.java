package com.contabilidad;

import com.contabilidad.model.*;
import com.contabilidad.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication
public class SistemaContableApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaContableApplication.class, args);
    }

    @Autowired private UsuarioService usuarioService;
    @Autowired private RolService rolService;
    @Autowired private CuentaContableService cuentaService;
    @Autowired private TerceroService terceroService;
    @Autowired private AsientoContableService asientoService;
    @Autowired private ReporteService reporteService;
    @Autowired private AuditoriaService auditoriaService;

    @Bean
    @Profile("!test")
    public CommandLineRunner menuInteractivo() {
        return args -> {
            Thread.sleep(1500);

            Scanner scanner = new Scanner(System.in);
            String usuarioSesion = null;

            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║     SISTEMA CONTABLE - Spring Boot       ║");
            System.out.println("║          MongoDB Atlas + Java 21          ║");
            System.out.println("╚══════════════════════════════════════════╝");

            // ─── Login inicial ────────────────────────────────────────────────
            while (usuarioSesion == null) {
                System.out.println("\n─── INICIO DE SESIÓN ───────────────────────");
                System.out.print("Correo: ");
                String correo = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();

                Optional<Usuario> userOpt = usuarioService.buscarPorCorreo(correo);
                if (userOpt.isPresent() && userOpt.get().getPassword().equals(password) && userOpt.get().isActivo()) {
                    usuarioSesion = userOpt.get().getId();
                    System.out.println("Bienvenido, " + userOpt.get().getNombre()
                            + " [" + (userOpt.get().getRol() != null ? userOpt.get().getRol().getNombre() : "Sin rol") + "]");
                } else {
                    System.out.println("Credenciales inválidas o usuario inactivo. Intente de nuevo.");
                }
            }

            boolean salir = false;
            while (!salir) {
                imprimirMenuPrincipal();
                System.out.print("Opción: ");
                String opcion = scanner.nextLine().trim();

                switch (opcion) {
                    case "1" -> menuUsuarios(scanner, usuarioSesion);
                    case "2" -> menuRoles(scanner, usuarioSesion);
                    case "3" -> menuCuentas(scanner, usuarioSesion);
                    case "4" -> menuAsientos(scanner, usuarioSesion);
                    case "5" -> menuTerceros(scanner, usuarioSesion);
                    case "6" -> menuReportes(scanner, usuarioSesion);
                    case "7" -> menuAuditoria(scanner);
                    case "0" -> {
                        System.out.println("\n Sesión cerrada. ¡Hasta luego!");
                        salir = true;
                    }
                    default -> System.out.println("Opción inválida.");
                }
            }
            scanner.close();
        };
    }

    private void imprimirMenuPrincipal() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║         MENÚ PRINCIPAL           ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Gestión de Usuarios          ║");
        System.out.println("║  2. Gestión de Roles             ║");
        System.out.println("║  3. Plan de Cuentas              ║");
        System.out.println("║  4. Asientos Contables           ║");
        System.out.println("║  5. Terceros                     ║");
        System.out.println("║  6. Reportes Financieros         ║");
        System.out.println("║  7. Auditoría                    ║");
        System.out.println("║  0. Salir                        ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    private void menuUsuarios(Scanner sc, String sesionId) {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── GESTIÓN DE USUARIOS ────────────────────");
            System.out.println("  1. Crear usuario");
            System.out.println("  2. Listar usuarios");
            System.out.println("  3. Actualizar usuario");
            System.out.println("  4. Desactivar usuario");
            System.out.println("  5. Asignar rol a usuario");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Nombre: "); String nombre = sc.nextLine().trim();
                    System.out.print("Correo: "); String correo = sc.nextLine().trim();
                    System.out.print("Password: "); String pass = sc.nextLine().trim();
                    try {
                        Usuario u = usuarioService.crearUsuario(nombre, correo, pass);
                        System.out.println("Usuario creado: " + u);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "2" -> {
                    List<Usuario> usuarios = usuarioService.listarTodos();
                    if (usuarios.isEmpty()) { System.out.println("No hay usuarios registrados."); }
                    else usuarios.forEach(u -> System.out.println("  » " + u));
                }
                case "3" -> {
                    System.out.print("ID del usuario: "); String id = sc.nextLine().trim();
                    System.out.print("Nuevo nombre (Enter para omitir): "); String n = sc.nextLine().trim();
                    System.out.print("Nuevo correo (Enter para omitir): "); String c = sc.nextLine().trim();
                    try {
                        Usuario u = usuarioService.actualizarUsuario(id, n.isEmpty() ? null : n, c.isEmpty() ? null : c);
                        System.out.println("Actualizado: " + u);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "4" -> {
                    System.out.print("ID del usuario a desactivar: "); String id = sc.nextLine().trim();
                    try {
                        usuarioService.desactivarUsuario(id);
                        System.out.println("Usuario desactivado.");
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "5" -> {
                    System.out.print("ID del usuario: "); String uid = sc.nextLine().trim();
                    System.out.println("Roles disponibles:");
                    rolService.listarTodos().forEach(r -> System.out.println("  » " + r.getId() + " | " + r.getNombre()));
                    System.out.print("ID del rol: "); String rid = sc.nextLine().trim();
                    try {
                        Usuario u = usuarioService.asignarRol(uid, rid);
                        System.out.println("Rol asignado: " + u);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void menuRoles(Scanner sc, String sesionId) {
        if (!usuarioService.esAdmin(sesionId)) {
            System.out.println("Acceso denegado. Solo los administradores pueden gestionar roles.");
            return;
        }
        boolean back = false;
        while (!back) {
            System.out.println("\n─── GESTIÓN DE ROLES ───────────────────────");
            System.out.println("  1. Crear rol");
            System.out.println("  2. Listar roles");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Nombre del rol (ej: ADMIN, CONTADOR): "); String nombre = sc.nextLine().trim();
                    try {
                        Rol r = rolService.crearRol(nombre);
                        System.out.println("Rol creado: " + r);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "2" -> rolService.listarTodos().forEach(r -> System.out.println("  » " + r));
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void menuCuentas(Scanner sc, String sesionId) {
        if (!usuarioService.esContador(sesionId)) {
            System.out.println("Acceso denegado. Se requiere rol ADMIN o CONTADOR.");
            return;
        }
        boolean back = false;
        while (!back) {
            System.out.println("\n─── PLAN DE CUENTAS ────────────────────────");
            System.out.println("  1. Crear cuenta contable");
            System.out.println("  2. Listar cuentas activas");
            System.out.println("  3. Buscar cuenta por código");
            System.out.println("  4. Actualizar cuenta");
            System.out.println("  5. Desactivar cuenta");
            System.out.println("  6. Listar cuentas por tipo");
            System.out.println("  7. Ver subcuentas");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Código: "); String codigo = sc.nextLine().trim();
                    System.out.print("Nombre: "); String nombre = sc.nextLine().trim();
                    System.out.println("Tipo (ACTIVO/PASIVO/PATRIMONIO/INGRESO/GASTO): ");
                    System.out.print("> "); String tipoStr = sc.nextLine().trim().toUpperCase();
                    System.out.print("ID Cuenta Padre (Enter para omitir): "); String padre = sc.nextLine().trim();
                    try {
                        TipoCuenta tipo = TipoCuenta.valueOf(tipoStr);
                        CuentaContable c = cuentaService.crearCuenta(codigo, nombre, tipo,
                                padre.isEmpty() ? null : padre, sesionId);
                        System.out.println("Cuenta creada: " + c);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "2" -> {
                    List<CuentaContable> cuentas = cuentaService.listarActivas();
                    if (cuentas.isEmpty()) System.out.println("No hay cuentas activas.");
                    else cuentas.forEach(c -> System.out.printf("  » [%s] %-10s %-30s (%s)%n",
                            c.getId().substring(0, 8), c.getCodigo(), c.getNombre(), c.getTipo()));
                }
                case "3" -> {
                    System.out.print("Código de la cuenta: "); String cod = sc.nextLine().trim();
                    cuentaService.buscarPorCodigo(cod)
                            .ifPresentOrElse(c -> System.out.println("  » " + c),
                                    () -> System.out.println("Cuenta no encontrada."));
                }
                case "4" -> {
                    System.out.print("ID de la cuenta: "); String id = sc.nextLine().trim();
                    System.out.print("Nuevo nombre (Enter para omitir): "); String n = sc.nextLine().trim();
                    System.out.print("Nuevo tipo (Enter para omitir): "); String t = sc.nextLine().trim();
                    try {
                        TipoCuenta tipo = t.isEmpty() ? null : TipoCuenta.valueOf(t.toUpperCase());
                        CuentaContable c = cuentaService.actualizarCuenta(id, n.isEmpty() ? null : n, tipo, sesionId);
                        System.out.println("Actualizada: " + c);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "5" -> {
                    System.out.print("ID de la cuenta a desactivar: "); String id = sc.nextLine().trim();
                    try {
                        cuentaService.desactivarCuenta(id, sesionId);
                        System.out.println("Cuenta desactivada.");
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "6" -> {
                    System.out.println("Tipos: ACTIVO / PASIVO / PATRIMONIO / INGRESO / GASTO");
                    System.out.print("Tipo: "); String t = sc.nextLine().trim().toUpperCase();
                    try {
                        cuentaService.listarPorTipo(TipoCuenta.valueOf(t))
                                .forEach(c -> System.out.println("  » " + c));
                    } catch (Exception e) { System.out.println("Tipo inválido."); }
                }
                case "7" -> {
                    System.out.print("ID de la cuenta padre: "); String id = sc.nextLine().trim();
                    List<CuentaContable> subs = cuentaService.listarSubcuentas(id);
                    if (subs.isEmpty()) System.out.println("Sin subcuentas.");
                    else subs.forEach(c -> System.out.println("  » " + c));
                }
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void menuAsientos(Scanner sc, String sesionId) {
        if (!usuarioService.esContador(sesionId)) {
            System.out.println("Acceso denegado. Se requiere rol ADMIN o CONTADOR.");
            return;
        }
        boolean back = false;
        while (!back) {
            System.out.println("\n─── ASIENTOS CONTABLES ─────────────────────");
            System.out.println("  1. Crear asiento contable");
            System.out.println("  2. Listar todos los asientos");
            System.out.println("  3. Consultar asiento por ID");
            System.out.println("  4. Anular asiento");
            System.out.println("  5. Movimientos por cuenta");
            System.out.println("  6. Filtrar por rango de fechas");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");
            switch (sc.nextLine().trim()) {
                case "1" -> crearAsientoInteractivo(sc, sesionId);
                case "2" -> {
                    List<AsientoContable> asientos = asientoService.listarTodos();
                    if (asientos.isEmpty()) System.out.println("No hay asientos registrados.");
                    else asientos.forEach(a -> System.out.printf(
                            "  » [%s] %s | %s | %s | %d líneas%n",
                            a.getId().substring(0, 8), a.getFecha(), a.getDescripcion(),
                            a.getEstado(), a.getLineas().size()));
                }
                case "3" -> {
                    System.out.print("ID del asiento: "); String id = sc.nextLine().trim();
                    asientoService.buscarPorId(id).ifPresentOrElse(a -> {
                        System.out.println("\n  Asiento: " + a.getDescripcion());
                        System.out.println("  Fecha  : " + a.getFecha());
                        System.out.println("  Estado : " + a.getEstado());
                        System.out.println("  Líneas :");
                        a.getLineas().forEach(l -> System.out.println("    » " + l));
                    }, () -> System.out.println("Asiento no encontrado."));
                }
                case "4" -> {
                    System.out.print("ID del asiento a anular: "); String id = sc.nextLine().trim();
                    try {
                        AsientoContable a = asientoService.anularAsiento(id, sesionId);
                        System.out.println("Asiento anulado: " + a);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "5" -> {
                    System.out.print("ID de la cuenta: "); String id = sc.nextLine().trim();
                    try {
                        List<LineaAsiento> movs = asientoService.listarMovimientosPorCuenta(id);
                        if (movs.isEmpty()) System.out.println("Sin movimientos.");
                        else {
                            double totD = movs.stream().mapToDouble(LineaAsiento::getDebito).sum();
                            double totC = movs.stream().mapToDouble(LineaAsiento::getCredito).sum();
                            movs.forEach(l -> System.out.println("  » " + l));
                            System.out.printf("  TOTAL  Débitos: %.2f | Créditos: %.2f | Saldo: %.2f%n",
                                    totD, totC, totD - totC);
                        }
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "6" -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        System.out.print("Fecha desde (yyyy-MM-dd): "); Date desde = sdf.parse(sc.nextLine().trim());
                        System.out.print("Fecha hasta (yyyy-MM-dd): "); Date hasta = sdf.parse(sc.nextLine().trim());
                        asientoService.buscarPorRangoFechas(desde, hasta)
                                .forEach(a -> System.out.printf("  » [%s] %s | %s%n",
                                        a.getId().substring(0, 8), a.getFecha(), a.getDescripcion()));
                    } catch (Exception e) { System.out.println("Formato de fecha inválido (use yyyy-MM-dd)."); }
                }
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void crearAsientoInteractivo(Scanner sc, String sesionId) {
        try {
            System.out.print("Descripción del asiento: "); String desc = sc.nextLine().trim();
            System.out.print("Fecha (yyyy-MM-dd): ");
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(sc.nextLine().trim());

            List<AsientoContableService.LineaDTO> lineas = new ArrayList<>();
            boolean agregarLinea = true;

            System.out.println("\n─── LÍNEAS DEL ASIENTO ─────────────────────");
            System.out.println("Ingrese cada línea. Escriba 'listo' en el ID de cuenta para terminar.");

            while (agregarLinea) {
                System.out.println("\nLínea #" + (lineas.size() + 1));
                System.out.print("ID Cuenta (o 'listo' para terminar): "); String cuentaId = sc.nextLine().trim();
                if (cuentaId.equalsIgnoreCase("listo")) {
                    if (lineas.isEmpty()) { System.out.println("Debe agregar al menos una línea."); continue; }
                    agregarLinea = false;
                    break;
                }
                System.out.print("Débito  (0 si no aplica): "); double deb = parseDouble(sc.nextLine().trim());
                System.out.print("Crédito (0 si no aplica): "); double cre = parseDouble(sc.nextLine().trim());
                System.out.print("ID Tercero (Enter para omitir): "); String tercId = sc.nextLine().trim();

                lineas.add(new AsientoContableService.LineaDTO(
                        cuentaId, deb, cre, tercId.isEmpty() ? null : tercId));
                System.out.printf("Línea agregada — Débito: %.2f | Crédito: %.2f%n", deb, cre);
            }

            AsientoContable asiento = asientoService.crearAsiento(desc, fecha, lineas, sesionId);
            System.out.println("Asiento creado exitosamente: " + asiento.getId());
            System.out.printf("Total Débitos: %.2f | Total Créditos: %.2f%n",
                    lineas.stream().mapToDouble(AsientoContableService.LineaDTO::debito).sum(),
                    lineas.stream().mapToDouble(AsientoContableService.LineaDTO::credito).sum());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void menuTerceros(Scanner sc, String sesionId) {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── GESTIÓN DE TERCEROS ────────────────────");
            System.out.println("  1. Crear tercero");
            System.out.println("  2. Listar terceros");
            System.out.println("  3. Buscar por identificación");
            System.out.println("  4. Actualizar tercero");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    // RF-05.1
                    System.out.print("Nombre: "); String n = sc.nextLine().trim();
                    System.out.print("Identificación (NIT/CC): "); String id = sc.nextLine().trim();
                    try {
                        Tercero t = terceroService.crearTercero(n, id, sesionId);
                        System.out.println("Tercero creado: " + t);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "2" -> {
                    // RF-05.2
                    List<Tercero> terceros = terceroService.listarTodos();
                    if (terceros.isEmpty()) System.out.println("No hay terceros registrados.");
                    else terceros.forEach(t -> System.out.println("  » " + t));
                }
                case "3" -> {
                    System.out.print("Identificación: "); String ident = sc.nextLine().trim();
                    terceroService.buscarPorIdentificacion(ident)
                            .ifPresentOrElse(t -> System.out.println("  » " + t),
                                    () -> System.out.println("Tercero no encontrado."));
                }
                case "4" -> {
                    // RF-05.3
                    System.out.print("ID del tercero: "); String id = sc.nextLine().trim();
                    System.out.print("Nuevo nombre (Enter para omitir): "); String n = sc.nextLine().trim();
                    System.out.print("Nueva identificación (Enter para omitir): "); String ident = sc.nextLine().trim();
                    try {
                        Tercero t = terceroService.actualizarTercero(id, n.isEmpty() ? null : n,
                                ident.isEmpty() ? null : ident, sesionId);
                        System.out.println("Actualizado: " + t);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void menuReportes(Scanner sc, String sesionId) {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── REPORTES FINANCIEROS ───────────────────");
            System.out.println("  1. Balance General");
            System.out.println("  2. Estado de Resultados");
            System.out.println("  3. Libro Diario");
            System.out.println("  4. Libro Mayor (por cuenta)");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    Map<String, Object> bg = reporteService.generarBalanceGeneral();
                    System.out.println("\n══════ BALANCE GENERAL ══════");
                    imprimirReporte(bg);
                }
                case "2" -> {
                    Map<String, Object> er = reporteService.generarEstadoResultados();
                    System.out.println("\n══════ ESTADO DE RESULTADOS ══════");
                    imprimirReporte(er);
                }
                case "3" -> {
                    System.out.println("\n══════ LIBRO DIARIO ══════");
                    List<Map<String, Object>> ld = reporteService.generarLibroDiario();
                    if (ld.isEmpty()) { System.out.println("Sin asientos activos."); break; }
                    ld.forEach(asiento -> {
                        System.out.println("\n  ─ Asiento: " + asiento.get("descripcion"));
                        System.out.println("    Fecha  : " + asiento.get("fecha"));
                        System.out.println("    Estado : " + asiento.get("estado"));
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> lineas = (List<Map<String, Object>>) asiento.get("lineas");
                        lineas.forEach(l -> System.out.printf("      %-40s Déb: %10.2f  Cré: %10.2f%n",
                                l.get("cuenta"), l.get("debito"), l.get("credito")));
                        System.out.printf("    TOTALES → Débitos: %.2f | Créditos: %.2f%n",
                                asiento.get("totalDebitos"), asiento.get("totalCreditos"));
                    });
                }
                case "4" -> {
                    System.out.print("ID de la cuenta: "); String id = sc.nextLine().trim();
                    try {
                        Map<String, Object> lm = reporteService.generarLibroMayor(id);
                        System.out.println("\n══════ LIBRO MAYOR ══════");
                        imprimirReporte(lm);
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                }
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void menuAuditoria(Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("\n─── AUDITORÍA Y TRAZABILIDAD ───────────────");
            System.out.println("  1. Ver historial completo");
            System.out.println("  2. Buscar por texto");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    List<Auditoria> auditorias = auditoriaService.listarTodas();
                    if (auditorias.isEmpty()) System.out.println("Sin registros de auditoría.");
                    else auditorias.forEach(a -> System.out.printf("  » [%s] %s%n", a.getFecha(), a.getAccion()));
                }
                case "2" -> {
                    System.out.print("Texto a buscar: "); String txt = sc.nextLine().trim();
                    auditoriaService.buscarPorAccion(txt)
                            .forEach(a -> System.out.printf("  » [%s] %s%n", a.getFecha(), a.getAccion()));
                }
                case "0" -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void imprimirReporte(Map<String, Object> reporte) {
        reporte.forEach((k, v) -> {
            if (v instanceof List<?> lista) {
                System.out.println("  " + k + ":");
                lista.forEach(item -> {
                    if (item instanceof Map<?, ?> m) {
                        m.forEach((mk, mv) -> System.out.printf("    %-20s: %s%n", mk, mv));
                        System.out.println();
                    }
                });
            } else {
                System.out.printf("  %-25s: %s%n", k, v);
            }
        });
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.replace(",", ".")); }
        catch (NumberFormatException e) { return 0.0; }
    }
}
package com.losalerces.sistematurnos.Excel;

import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.Clases.ClasePaciente;
import com.losalerces.sistematurnos.Controladores.ControladorReportePami;
import com.losalerces.sistematurnos.DAO.ObraSocialDAO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Excel {

    /**
     * Genera el reporte en Excel procesando la lista de ClasePaciente.
     */
    public static File generarReportePacientes(List<ClasePaciente> listaPacientes, String obraSocialFiltro, String carpetaDestino) throws Exception {

        File carpeta = new File(carpetaDestino);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String nombreArchivo = (obraSocialFiltro != null && !obraSocialFiltro.trim().isEmpty())
                ? "Reporte_Pacientes_" + obraSocialFiltro.replaceAll("\\s+", "_") + ".xlsx"
                : "Reporte_Pacientes_Clinica.xlsx";

        File excelFile = new File(carpeta, nombreArchivo);

        ObraSocialDAO obraSocialDAO = new ObraSocialDAO();
        List<ClaseObraSocial> listaObrasSociales = obraSocialDAO.listarTodos();
        Map<Integer, String> mapaObrasSociales = new HashMap<>();

        if (listaObrasSociales != null) {
            for (ClaseObraSocial os : listaObrasSociales) {
                mapaObrasSociales.put(os.idObraSocial(), os.nombre());
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pacientes");
            sheet.setDisplayGridlines(true);

            byte[] blueRGB = new byte[]{(byte) 30, (byte) 58, (byte) 138};
            CellStyle styleTitulo = crearEstiloTitulo(workbook, blueRGB);
            CellStyle styleHeaderTabla = crearEstiloHeaderTabla(workbook, blueRGB);
            CellStyle styleDataCenter = crearEstiloDatos(workbook, HorizontalAlignment.CENTER);
            CellStyle styleDataLeft = crearEstiloDatos(workbook, HorizontalAlignment.LEFT);

            // Encabezado
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 6));
            Row rowTitulo = sheet.createRow(0);
            sheet.createRow(1);
            Cell cellTitulo = rowTitulo.createCell(0);

            String tituloReporte = (obraSocialFiltro != null && !obraSocialFiltro.trim().isEmpty())
                    ? "CLÍNICA LOS ALERCES — PACIENTES " + obraSocialFiltro.toUpperCase()
                    : "CLÍNICA LOS ALERCES — REGISTRO GENERAL DE PACIENTES";

            cellTitulo.setCellValue(tituloReporte);
            cellTitulo.setCellStyle(styleTitulo);

            Row rowInfo1 = sheet.createRow(3);
            rowInfo1.createCell(0).setCellValue("Especialista:");
            rowInfo1.createCell(1).setCellValue("Gastroenterología & Endoscopía Digestiva");
            rowInfo1.createCell(4).setCellValue("M.P. / M.N.:");
            rowInfo1.createCell(5).setCellValue("1624 / 80.094");

            Row rowInfo2 = sheet.createRow(4);
            rowInfo2.createCell(0).setCellValue("Institución:");
            rowInfo2.createCell(1).setCellValue("Centro Médico Los Alerces");
            rowInfo2.createCell(4).setCellValue("Fecha Reporte:");

            String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            rowInfo2.createCell(5).setCellValue(fechaActual);

            String[] cabeceras = {
                    "ID Paciente", "Nombre Completo", "Fecha Nac.", "Teléfono", "Email", "ID Obra Social", "Obra Social"
            };

            Row rowHeader = sheet.createRow(6);
            rowHeader.setHeightInPoints(24);
            for (int i = 0; i < cabeceras.length; i++) {
                Cell cell = rowHeader.createCell(i);
                cell.setCellValue(cabeceras[i]);
                cell.setCellStyle(styleHeaderTabla);
            }

            int startRow = 7;
            if (listaPacientes != null) {
                for (ClasePaciente pac : listaPacientes) {
                    Row row = sheet.createRow(startRow++);
                    row.setHeightInPoints(18);

                    row.createCell(0).setCellValue(pac.idPaciente());
                    row.getCell(0).setCellStyle(styleDataCenter);

                    String nombreCompleto = (pac.apellido() != null ? pac.apellido() : "") + " " +
                            (pac.nombre() != null ? pac.nombre() : "");
                    row.createCell(1).setCellValue(nombreCompleto.trim());
                    row.getCell(1).setCellStyle(styleDataLeft);

                    row.createCell(2).setCellValue(pac.fechaNacimiento() != null ? pac.fechaNacimiento() : "");
                    row.getCell(2).setCellStyle(styleDataCenter);

                    row.createCell(3).setCellValue(pac.telefono() != null ? pac.telefono() : "");
                    row.getCell(3).setCellStyle(styleDataLeft);

                    row.createCell(4).setCellValue(pac.email() != null ? pac.email() : "");
                    row.getCell(4).setCellStyle(styleDataLeft);

                    row.createCell(5).setCellValue(pac.idObraSocial());
                    row.getCell(5).setCellStyle(styleDataCenter);

                    String nombreOS = mapaObrasSociales.getOrDefault(pac.idObraSocial(), "Sin especificación");
                    row.createCell(6).setCellValue(nombreOS);
                    row.getCell(6).setCellStyle(styleDataLeft);
                }
            }

            Row rowTotal = sheet.createRow(startRow + 1);
            rowTotal.createCell(0).setCellValue("Total Pacientes:");
            rowTotal.getCell(0).setCellStyle(styleDataLeft);

            Cell cellTotalVal = rowTotal.createCell(1);
            cellTotalVal.setCellFormula("COUNTA(A8:A" + startRow + ")");
            cellTotalVal.setCellStyle(styleDataCenter);

            for (int i = 0; i < cabeceras.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(excelFile)) {
                workbook.write(out);
            }
        }

        return excelFile;
    }

    public static File generarReportePacientes(List<ClasePaciente> listaPacientes, String carpetaDestino) throws Exception {
        return generarReportePacientes(listaPacientes, null, carpetaDestino);
    }

    /**
     * Genera el reporte en Excel procesando la lista de FilaReportePami con diseño optimizado.
     */
    public static File generarReportePami(List<ControladorReportePami.FilaReportePami> listaReporte, String carpetaDestino) throws Exception {

        File carpeta = new File(carpetaDestino);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String nombreArchivo = "Reporte_Turnos_PAMI.xlsx";
        File excelFile = new File(carpeta, nombreArchivo);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Turnos PAMI");
            sheet.setDisplayGridlines(true);

            // ===== ESTILOS Y COLORES =====
            byte[] blueRGB = new byte[]{(byte) 30, (byte) 58, (byte) 138};
            CellStyle styleTitulo = crearEstiloTitulo(workbook, blueRGB);
            CellStyle styleHeaderTabla = crearEstiloHeaderTabla(workbook, blueRGB);
            CellStyle styleDataCenter = crearEstiloDatos(workbook, HorizontalAlignment.CENTER);
            CellStyle styleDataLeft = crearEstiloDatos(workbook, HorizontalAlignment.LEFT);

            // ===== 1. ENCABEZADO ARTÍSTICO / INSTITUCIONAL =====
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 4)); // A1:E2
            Row rowTitulo = sheet.createRow(0);
            sheet.createRow(1);
            Cell cellTitulo = rowTitulo.createCell(0);

            cellTitulo.setCellValue("CLÍNICA LOS ALERCES  •  GESTIÓN DE PRESTACIONES PAMI");
            cellTitulo.setCellStyle(styleTitulo);

            Row rowInfo2 = sheet.createRow(4);
            rowInfo2.createCell(0).setCellValue("Convenio:");
            rowInfo2.createCell(1).setCellValue("PAMI - Atenciones Médicas Validadas");
            rowInfo2.createCell(3).setCellValue("Fecha Emisión:");

            String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            rowInfo2.createCell(4).setCellValue(fechaActual);

            // ===== 2. TABLA DE TURNOS PAMI =====
            String[] cabeceras = {
                    "Fecha", "Hora", "Paciente", "Doctor", "Estado"
            };

            Row rowHeader = sheet.createRow(6);
            rowHeader.setHeightInPoints(24);
            for (int i = 0; i < cabeceras.length; i++) {
                Cell cell = rowHeader.createCell(i);
                cell.setCellValue(cabeceras[i]);
                cell.setCellStyle(styleHeaderTabla);
            }

            // ===== 3. CARGA DINÁMICA DE DATOS =====
            int startRow = 7;
            if (listaReporte != null) {
                for (var fila : listaReporte) {
                    Row row = sheet.createRow(startRow++);
                    row.setHeightInPoints(18);

                    row.createCell(0).setCellValue(fila.getFecha() != null ? fila.getFecha() : "");
                    row.getCell(0).setCellStyle(styleDataCenter);

                    row.createCell(1).setCellValue(fila.getHora() != null ? fila.getHora() : "");
                    row.getCell(1).setCellStyle(styleDataCenter);

                    row.createCell(2).setCellValue(fila.getPaciente() != null ? fila.getPaciente() : "");
                    row.getCell(2).setCellStyle(styleDataLeft);

                    row.createCell(3).setCellValue(fila.getDoctor() != null ? fila.getDoctor() : "");
                    row.getCell(3).setCellStyle(styleDataLeft);

                    row.createCell(4).setCellValue(fila.getEstado() != null ? fila.getEstado() : "");
                    row.getCell(4).setCellStyle(styleDataCenter);
                }
            }

            // ===== 4. TOTALES =====
            Row rowTotal = sheet.createRow(startRow + 1);
            rowTotal.createCell(0).setCellValue("Total Turnos:");
            rowTotal.getCell(0).setCellStyle(styleDataLeft);

            Cell cellTotalVal = rowTotal.createCell(1);
            cellTotalVal.setCellFormula("COUNTA(A8:A" + startRow + ")");
            cellTotalVal.setCellStyle(styleDataCenter);

            // ===== 5. ANCHO DE COLUMNAS PERSONALIZADO =====
            // Tiempos más chicos, nombres más grandes
            sheet.setColumnWidth(0, 3200);  // Fecha (más chica)
            sheet.setColumnWidth(1, 2600);  // Hora (más chica)
            sheet.setColumnWidth(2, 8500);  // Paciente (más grande)
            sheet.setColumnWidth(3, 8500);  // Doctor (más grande)
            sheet.setColumnWidth(4, 3800);  // Estado (mediana)

            try (FileOutputStream out = new FileOutputStream(excelFile)) {
                workbook.write(out);
            }
        }

        return excelFile;
    }

    // --- ESTILOS AUXILIARES ---
    private static CellStyle crearEstiloTitulo(Workbook wb, byte[] rgb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 13);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        if (style instanceof XSSFCellStyle xssfStyle) {
            XSSFColor color = new XSSFColor(rgb, null);
            xssfStyle.setFillForegroundColor(color);
            xssfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }

    private static CellStyle crearEstiloHeaderTabla(Workbook wb, byte[] rgb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        if (style instanceof XSSFCellStyle xssfStyle) {
            XSSFColor color = new XSSFColor(rgb, null);
            xssfStyle.setFillForegroundColor(color);
            xssfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle crearEstiloDatos(Workbook wb, HorizontalAlignment align) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(align);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
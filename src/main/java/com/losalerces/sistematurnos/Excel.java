package com.losalerces.sistematurnos;

import com.losalerces.sistematurnos.Clases.ClaseObraSocial;
import com.losalerces.sistematurnos.Clases.ClasePaciente;
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
     * Utiliza ObraSocialDAO para traducir el idObraSocial al nombre real en la planilla.
     */
    public static File generarReportePacientes(List<ClasePaciente> listaPacientes, String obraSocialFiltro, String carpetaDestino) throws Exception {

        File carpeta = new File(carpetaDestino);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // 1. Nombre de archivo dinámico
        String nombreArchivo = (obraSocialFiltro != null && !obraSocialFiltro.trim().isEmpty())
                ? "Reporte_Pacientes_" + obraSocialFiltro.replaceAll("\\s+", "_") + ".xlsx"
                : "Reporte_Pacientes_Clinica.xlsx";

        File excelFile = new File(carpeta, nombreArchivo);

        // 2. Consulta de obras sociales para mapear idObraSocial -> Nombre
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

            // ===== ESTILOS Y COLORES =====
            byte[] blueRGB = new byte[]{(byte) 30, (byte) 58, (byte) 138};
            CellStyle styleTitulo = crearEstiloTitulo(workbook, blueRGB);
            CellStyle styleHeaderTabla = crearEstiloHeaderTabla(workbook, blueRGB);
            CellStyle styleDataCenter = crearEstiloDatos(workbook, HorizontalAlignment.CENTER);
            CellStyle styleDataLeft = crearEstiloDatos(workbook, HorizontalAlignment.LEFT);

            // ===== 1. ENCABEZADO MÉDICO =====
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 6)); // A1:G2
            Row rowTitulo = sheet.createRow(0);
            sheet.createRow(1);
            Cell cellTitulo = rowTitulo.createCell(0);

            String tituloReporte = (obraSocialFiltro != null && !obraSocialFiltro.trim().isEmpty())
                    ? "CENTRO MÉDICO DE GASTROENTEROLOGÍA - PACIENTES " + obraSocialFiltro.toUpperCase()
                    : "CENTRO MÉDICO DE GASTROENTEROLOGÍA - REGISTRO DE PACIENTES";

            cellTitulo.setCellValue(tituloReporte);
            cellTitulo.setCellStyle(styleTitulo);

            // Datos del Profesional
            Row rowInfo1 = sheet.createRow(3);
            rowInfo1.createCell(0).setCellValue("Especialista:");
            rowInfo1.createCell(1).setCellValue("Gastroenterología & Endoscopía Digestiva");
            rowInfo1.createCell(4).setCellValue("M.P. / M.N.:");
            rowInfo1.createCell(5).setCellValue("1624 / 80.094");

            Row rowInfo2 = sheet.createRow(4);
            rowInfo2.createCell(0).setCellValue("Afiliación:");
            rowInfo2.createCell(1).setCellValue("Soc. Argentina de Gastroenterología (SAGE)");
            rowInfo2.createCell(4).setCellValue("Fecha Reporte:");

            String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            rowInfo2.createCell(5).setCellValue(fechaActual);

            // ===== 2. TABLA DE PACIENTES (Basada en ClasePaciente) =====
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

            // ===== 3. CARGA DINÁMICA DE ClasePaciente =====
            int startRow = 7;
            if (listaPacientes != null) {
                for (ClasePaciente pac : listaPacientes) {
                    Row row = sheet.createRow(startRow++);
                    row.setHeightInPoints(18);

                    // ID Paciente
                    row.createCell(0).setCellValue(pac.getIdPaciente());
                    row.getCell(0).setCellStyle(styleDataCenter);

                    // Nombre Completo (Concatenando Nombre y Apellido)
                    String nombreCompleto = (pac.getApellido() != null ? pac.getApellido() : "") + " " +
                            (pac.getNombre() != null ? pac.getNombre() : "");
                    row.createCell(1).setCellValue(nombreCompleto.trim());
                    row.getCell(1).setCellStyle(styleDataLeft);

                    // Fecha de Nacimiento
                    row.createCell(2).setCellValue(pac.getFechaNacimiento() != null ? pac.getFechaNacimiento() : "");
                    row.getCell(2).setCellStyle(styleDataCenter);

                    // Teléfono
                    row.createCell(3).setCellValue(pac.getTelefono() != null ? pac.getTelefono() : "");
                    row.getCell(3).setCellStyle(styleDataLeft);

                    // Email
                    row.createCell(4).setCellValue(pac.getEmail() != null ? pac.getEmail() : "");
                    row.getCell(4).setCellStyle(styleDataLeft);

                    // ID Obra Social
                    row.createCell(5).setCellValue(pac.getIdObraSocial());
                    row.getCell(5).setCellStyle(styleDataCenter);

                    // Nombre de la Obra Social (Obtenido del Map)
                    String nombreOS = mapaObrasSociales.getOrDefault(pac.getIdObraSocial(), "Sin especificación");
                    row.createCell(6).setCellValue(nombreOS);
                    row.getCell(6).setCellStyle(styleDataLeft);
                }
            }

            // ===== 4. TOTALES =====
            Row rowTotal = sheet.createRow(startRow + 1);
            rowTotal.createCell(0).setCellValue("Total Pacientes:");
            rowTotal.getCell(0).setCellStyle(styleDataLeft);

            Cell cellTotalVal = rowTotal.createCell(1);
            cellTotalVal.setCellFormula("COUNTA(A8:A" + startRow + ")");
            cellTotalVal.setCellStyle(styleDataCenter);

            // Ajuste automático de columnas
            for (int i = 0; i < cabeceras.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(excelFile)) {
                workbook.write(out);
            }
        }

        return excelFile;
    }

    // Sobrecarga para mantener compatibilidad sin filtro de obra social
    public static File generarReportePacientes(List<ClasePaciente> listaPacientes, String carpetaDestino) throws Exception {
        return generarReportePacientes(listaPacientes, null, carpetaDestino);
    }

    // --- ESTILOS AUXILIARES ---
    private static CellStyle crearEstiloTitulo(Workbook wb, byte[] rgb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 14);
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
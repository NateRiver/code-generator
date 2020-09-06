package com.nate.codegen.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * file export utils for export file to web response
 *
 * @author Nate
 */
public class FileExportUtils {

    private FileExportUtils() {
    }

    private static final Map<String, String> MEDIA_TYPES = Maps.newConcurrentMap();

    static {
        MEDIA_TYPES.put("pdf", "application/pdf");
        MEDIA_TYPES.put("xls", "application/vnd.ms-excel");
        MEDIA_TYPES.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    }

    /**
     * 导出文件
     *
     * @param fileName 文件名称
     * @param content  文件内容
     * @param response HttpServletResponse
     * @throws IOException 如果导出失败
     */
    public static void export(String fileName, byte[] content, HttpServletResponse response) throws IOException {
        export(fileName, new ByteArrayInputStream(content), response);
    }

    private static String getContentType(String ext) {
        String contentType = MEDIA_TYPES.get(ext);
        if (contentType == null) {
            return "application/octet-stream";
        }
        return contentType;
    }

    /**
     * 文件导出
     *
     * @param fileName 文件名称
     * @param in       输入流
     * @param response HttpServletResponse
     * @throws IOException 如果导出失败
     */
    public static void export(String fileName, InputStream in, HttpServletResponse response) throws IOException {
        OutputStream out = null;
        try {
            response.reset();
            String ext = Files.getFileExtension(fileName);
            response.setContentType(getContentType(ext));
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            out = response.getOutputStream();
            ByteStreams.copy(in, out);
            out.flush();
        } finally {
            out.close();
        }
    }

    /**
     * 导出文件
     *
     * @param file     文件
     * @param response HttpServletResponse
     * @throws IOException 如果导出失败
     */
    public static void export(File file, HttpServletResponse response) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            export(file.getName(), in, response);
        }
    }
}

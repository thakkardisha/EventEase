package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/uploads/*")
public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the requested path (everything after /uploads/)
        String requestedPath = request.getPathInfo();

        System.out.println("========== IMAGE SERVLET ==========");
        System.out.println("Full Request URI: " + request.getRequestURI());
        System.out.println("Path Info: " + requestedPath);

        if (requestedPath == null || requestedPath.length() <= 1) {
            System.err.println("ERROR: Invalid path");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid image path");
            return;
        }

        // Remove leading slash
        String filename = requestedPath.substring(1);
        System.out.println("Looking for file: " + filename);

        // Get the real path to the uploads folder
        String uploadsPath = getServletContext().getRealPath("/uploads");
        System.out.println("Uploads directory: " + uploadsPath);

        File file = new File(uploadsPath, filename);
        System.out.println("Complete file path: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());
        System.out.println("Is file: " + file.isFile());
        System.out.println("Can read: " + file.canRead());

        if (!file.exists() || !file.isFile()) {
            System.err.println("ERROR: File not found or not a file");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found: " + filename);
            return;
        }

        // Set content type based on file extension
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        System.out.println("Content type: " + contentType);

        response.setContentType(contentType);
        response.setContentLength((int) file.length());

        // Set cache headers for better performance
        response.setHeader("Cache-Control", "public, max-age=31536000");

        // Copy file to response output stream
        try {
            Files.copy(file.toPath(), response.getOutputStream());
            System.out.println("SUCCESS: Image served successfully");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to copy file to output stream");
            e.printStackTrace();
            throw e;
        }
    }
}

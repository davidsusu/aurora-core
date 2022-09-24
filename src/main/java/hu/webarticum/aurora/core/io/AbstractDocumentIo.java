package hu.webarticum.aurora.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import hu.webarticum.aurora.core.model.Document;

public abstract class AbstractDocumentIo implements DocumentIo {

    private static final long serialVersionUID = 1L;
    

    private String documentIoLabel = "";
    
    private String[] documentIoExtensions = new String[] {};
    

    protected AbstractDocumentIo() {
    }

    protected AbstractDocumentIo(String documentIoLabel, String... documentIoExtensions) {
        this.documentIoLabel = documentIoLabel;
        int extensionCount = documentIoExtensions.length;
        this.documentIoExtensions = new String[extensionCount];
        System.arraycopy(documentIoExtensions, 0, this.documentIoExtensions, 0, extensionCount);
    }
    
    
    @Override
    public String getLabel() {
        return documentIoLabel;
    }

    @Override
    public String[] getExtensions() {
        return documentIoExtensions;
    }

    @Override
    public boolean supportsExtension(String extension) {
        return Arrays.asList(documentIoExtensions).contains(extension);
    }

    @Override
    public void save(Document document, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            save(document, new FileOutputStream(file));
        }
    }

    @Override
    public Document load(URL url) throws IOException, ParseException {
        try (InputStream urlInputStream = url.openStream()) {
            return load(urlInputStream);
        }
    }

    @Override
    public Document load(File file) throws IOException, ParseException {
        try (InputStream fileInputStream = new FileInputStream(file)) {
            return load(fileInputStream);
        }
    }

}

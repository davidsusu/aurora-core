package hu.webarticum.aurora.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import hu.webarticum.aurora.core.model.Document;
import hu.webarticum.aurora.core.model.Labeled;

public interface DocumentIo extends Labeled {
    
    public String[] getExtensions();
    
    public boolean supportsExtension(String extension);
    
    public void save(Document document, File file) throws IOException;

    public void save(Document document, OutputStream outputStream) throws IOException;

    public Document load(URL url) throws IOException, ParseException;
    
    public Document load(File file) throws IOException, ParseException;

    public Document load(InputStream inputStream) throws IOException, ParseException;

    public static class DocumentIoQueue extends ArrayList<DocumentIo> {
        
        private static final long serialVersionUID = 1L;
        
        public DocumentIo getByExtension(String extension) {
            for (DocumentIo documentIo: this) {
                if (documentIo.supportsExtension(extension)) {
                    return documentIo;
                }
            }
            return null;
        }
        
    }
    
    public static class ParseException extends Exception {

        private static final long serialVersionUID = 1L;

        public ParseException() {
            super();
        }

        public ParseException(String message) {
            super(message);
        }
        
    }
    
}

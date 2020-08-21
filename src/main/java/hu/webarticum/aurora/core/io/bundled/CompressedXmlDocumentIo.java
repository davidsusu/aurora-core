package hu.webarticum.aurora.core.io.bundled;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import hu.webarticum.aurora.core.io.AbstractDocumentIo;
import hu.webarticum.aurora.core.model.Document;
import hu.webarticum.aurora.core.text.CoreText;


public class CompressedXmlDocumentIo extends AbstractDocumentIo {

    private static final long serialVersionUID = 1L;


    private final XmlDocumentIo xmlDocumentIo;

    
    public CompressedXmlDocumentIo() {
        super(CoreText.get("io.compressed_xml.name", "AUR timetable file"), "aur");
        xmlDocumentIo = new XmlDocumentIo();
    }
    
    
    @Override
    public void save(Document document, OutputStream outputStream) throws IOException {
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        xmlDocumentIo.save(document, gzipOutputStream);
        gzipOutputStream.close();
    }

    @Override
    public Document load(InputStream inputStream) throws IOException, ParseException {
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        return xmlDocumentIo.load(gzipInputStream);
    }
    
}

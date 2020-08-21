package hu.webarticum.aurora.core.io.bundled;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import hu.webarticum.aurora.core.io.AbstractDocumentIo;
import hu.webarticum.aurora.core.model.Document;
import hu.webarticum.aurora.core.text.CoreText;


public class SerializeDocumentIo extends AbstractDocumentIo {

    private static final long serialVersionUID = 1L;

    
    public SerializeDocumentIo() {
        super(CoreText.get("io.serialize.name", "Java serialize"), "ser");
    }
    
    
    @Override
    public void save(Document document, OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(document);
        } finally {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        }
    }

    @Override
    public Document load(InputStream stream) throws IOException, ParseException {
        Document document;
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(stream);
            document = (Document)objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new ParseException();
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
        }
        return document;
    }

}
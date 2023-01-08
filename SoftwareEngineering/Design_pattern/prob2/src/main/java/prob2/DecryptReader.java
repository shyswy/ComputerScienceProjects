package prob2;

import java.io.IOException;
import java.io.Reader;

public class DecryptReader extends Reader{
    EncryptMode encryptMode;

    private final Reader reader;
    public DecryptReader(Reader reader) {
        //read한다.
        this.reader =reader;
    }
    public void setMode(EncryptMode encryptMode){
        this.encryptMode=encryptMode;
    }



    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int result=reader.read(cbuf, off, len);
        char c[]= encryptMode.Decode(cbuf,off, len);
        return result;


    }

    @Override
    public void close() throws IOException {
        reader.close();

    }







}

package com.finprin.sezame.util;

import sun.security.provider.X509Factory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

public class CertificateWriter {

    private Certificate certificate;

    public CertificateWriter(Certificate certificate) {
        this.certificate = certificate;
    }

    public void writeAsPemToFile(String pathToFile) throws CertificateEncodingException, IOException {

        File file = new File(pathToFile);
        byte[] buf = certificate.getEncoded();

        FileOutputStream os = new FileOutputStream(file);
        Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"));
        wr.write(X509Factory.BEGIN_CERT);
        wr.write(new sun.misc.BASE64Encoder().encode(buf));
        wr.write(X509Factory.END_CERT);
        wr.flush();
    }

}

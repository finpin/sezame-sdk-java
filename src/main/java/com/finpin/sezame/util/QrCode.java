package com.finpin.sezame.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class QrCode {

    public static final int DEFAULT_IMAGE_WIDTH = 1200;
    public static final int DEFAULT_IMAGE_HEIGHT = 1200;
    private QrCodePairingData pairingData;

    public QrCode(QrCodePairingData pairingData) {
        this.pairingData = pairingData;
    }

    public void writeToImageFile(String imgFilePath) throws Exception {
        writeToImageFile(imgFilePath, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }

    public void writeToImageFile(String imgFilePath, int imgWidth, int imgHeight) throws Exception {
        BufferedImage img = generateImage();
        File imgOutputFile = new File(imgFilePath);
        ImageIO.write(img, "png", imgOutputFile);
    }

    public BufferedImage generateImage() throws Exception {
        return MatrixToImageWriter.toBufferedImage( generateQrCode(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT) );
    }

    public BufferedImage generateImage(int imgWidth, int imgHeight) throws Exception {
        return MatrixToImageWriter.toBufferedImage( generateQrCode(imgWidth, imgHeight) );
    }

    private BitMatrix generateQrCode(int width, int height) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String qrCodeDataAsJson = mapper.writeValueAsString(pairingData);
        return new QRCodeWriter().encode(qrCodeDataAsJson, BarcodeFormat.QR_CODE, width, height);
    }
}

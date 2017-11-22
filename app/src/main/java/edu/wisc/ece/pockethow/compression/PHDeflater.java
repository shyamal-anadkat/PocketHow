package edu.wisc.ece.pockethow.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by shyamal on 11/22/2017.
 */

public class PHDeflater {

    public PHDeflater() {
    }

    /**
     * @param rev
     * @return
     */
    public static byte[] deflate(String rev) {
        try {
            byte[] in = rev.getBytes("UTF-8");
            Deflater deflater = new Deflater();
            deflater.setInput(in);
            deflater.finish();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            while (!deflater.finished()) {
                int byteCnt = deflater.deflate(buf);
                baos.write(buf, 0, byteCnt);
            }
            deflater.end();
            return baos.toByteArray();
        } catch (UnsupportedEncodingException ex) {
        } catch (Exception e) {
        }
        //shldnt reach here
        return null;
    }

    /**
     * @param data
     * @return
     * @throws IOException
     * @throws DataFormatException
     */
    public static byte[] inflate(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

    /**
     * @param data
     * @return
     * @throws IOException
     */
    public static byte[] deflate(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

}

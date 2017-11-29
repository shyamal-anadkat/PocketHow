package edu.wisc.ece.pockethow.compression;

import org.junit.Test;

import static org.junit.Assert.*;

public class PHDeflaterTest {

    PHDeflater phDeflater = new PHDeflater();

    @Test
    public void simpleDeflateInflateTest() throws Exception {
        final String sample = "The quick brown fox jumps over the lazy dog $[]<>!@#$%^&*()?|";
        byte[] deflated = phDeflater.deflate(sample);
        assertNotEquals(0, deflated.length);
        assertNotEquals(sample, deflated.toString());
        byte[] inflated = phDeflater.inflate(deflated);
        assertEquals(sample, new String(inflated));

        byte[] deflatedFromBytes = phDeflater.deflate(sample.getBytes());
        assertNotEquals(0, deflatedFromBytes.length);
        assertNotEquals(sample, deflatedFromBytes.toString());
        byte[] inflated1 = phDeflater.inflate(deflatedFromBytes);
        assertEquals(sample, new String(inflated1));
    }


}
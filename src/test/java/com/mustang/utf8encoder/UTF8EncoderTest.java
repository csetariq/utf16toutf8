package com.mustang.utf8encoder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class UTF8EncoderTest {

    private static final String TEST_FILE = "alskdjfhg.txt";
    private static UTF8Encoder encodeTestInstance;
    private static Method encodeMethod;
    
    @BeforeClass
    public static void setUp() {
        
        try {
            encodeMethod = UTF8Encoder.class.getDeclaredMethod("encode", int.class);
            encodeMethod.setAccessible(true);
            
            encodeTestInstance = new UTF8Encoder(new ByteArrayInputStream(new byte[] {}),
                    new ByteArrayOutputStream(), Charset.forName("UTF-32"));
            
        } catch (UnsupportedEncodingException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * tests the code-point in the range
     * 
     *      0x00        -   0x7F
     *      0b000_0000  -   0b111_1111
     * 
     * encoded as
     * 
     *      0b0_xxxxxxx
     */
    @Test
    public void test7BitsEncode() {
        byte[] result;
        try {
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b000_0000);
            assertArrayEquals(new byte[]{(byte) 0b0_000_0000}, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b111_1111);
            assertArrayEquals(new byte[]{(byte) 0b0_111_1111}, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b101_0101);
            assertArrayEquals(new byte[]{(byte) 0b0_101_0101}, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b010_1010);
            assertArrayEquals(new byte[]{(byte) 0b0_010_1010}, result);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * tests the code-point in the range
     * 
     *      0x80            -   0x7FF
     *      0b000_1000_0000 -   0b111_1111_1111
     * 
     * encoded as
     * 
     *      0b110_xxxxx 0b10_xxxxxx
     */
    @Test
    public void test11BitsEncode() {
        byte[] result;
        try {
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b000_1000_0000);
            assertArrayEquals(new byte[] { (byte) 0b110_00010, (byte) 0b10_000000 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b111_1111_1111);
            assertArrayEquals(new byte[] { (byte) 0b110_11111, (byte) 0b10_111111 }, result);

            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b101_0101_0101);
            assertArrayEquals(new byte[] { (byte) 0b110_10101, (byte) 0b10_010101 }, result);

            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b010_1010_1010);
            assertArrayEquals(new byte[] { (byte) 0b110_01010, (byte) 0b10_101010 }, result);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * tests the code-point in the range
     * 
     *      0x800            -   0xFFFF
     *      0b1000_0000_0000 -   0b1111_1111_1111_1111
     * 
     * encoded as
     * 
     *      0b1110_xxxx 0b10_xxxxxx 0b10_xxxxxx
     */
    @Test
    public void test16BitsEncode() {
        byte[] result;
        try {
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b1000_0000_0000);
            assertArrayEquals(new byte[] { (byte) 0b1110_0000, (byte) 0b10_100000, (byte) 0b10_000000 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b1111_1111_1111_1111);
            assertArrayEquals(new byte[] { (byte) 0b1110_1111, (byte) 0b10_111111, (byte) 0b10_111111 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b1010_1010_1010_1010);
            assertArrayEquals(new byte[] { (byte) 0b1110_1010, (byte) 0b10_101010, (byte) 0b10_101010 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b0101_0101_0101_0101);
            assertArrayEquals(new byte[] { (byte) 0b1110_0101, (byte) 0b10_010101, (byte) 0b10_010101 }, result);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * tests the code-point in the range
     * 
     *      0x10000                 -   0x1FFFFF
     *      0b1_0000_0000_0000_0000 -   0b1_1111_1111_1111_1111_1111
     * 
     * encoded as
     * 
     *      0b11110_xxx 0b10_xxxxxx 0b10_xxxxxx 0b10_xxxxxx
     */
    @Test
    public void test21BitsEncode() {
        byte[] result;
        try {
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b1_0000_0000_0000_0000);
            assertArrayEquals(
                    new byte[] { (byte) 0b11110_000, (byte) 0b10_010000, (byte) 0b10_000000, (byte) 0b10_000000 },
                    result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b1_0101_1010_0101_1010);
            assertArrayEquals(
                    new byte[] { (byte) 0b11110_000, (byte) 0b10_010101, (byte) 0b10_101001, (byte) 0b10_011010 },
                    result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b1_1111_1111_1111_1111_1111);
            assertArrayEquals(
                    new byte[] { (byte) 0b11110_111, (byte) 0b10_111111, (byte) 0b10_111111, (byte) 0b10_111111 },
                    result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b1_0101_0101_0101_0101_0101);
            assertArrayEquals(
                    new byte[] { (byte) 0b11110_101, (byte) 0b10_010101, (byte) 0b10_010101, (byte) 0b10_010101 },
                    result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b0_1010_1010_1010_1010_1010);
            assertArrayEquals(
                    new byte[] { (byte) 0b11110_010, (byte) 0b10_101010, (byte) 0b10_101010, (byte) 0b10_101010 },
                    result);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * tests the code-point in the range
     * 
     *      0x200000                        -   0x3FFFFFF
     *      0b10_0000_0000_0000_0000_0000   -   0b11_1111_1111_1111_1111_1111_1111
     * 
     * encoded as
     * 
     *      0b111110_xx 0b10_xxxxxx 0b10_xxxxxx 0b10_xxxxxx 0b10_xxxxxx
     */
    @Test
    public void test26BitsEncode() {
        byte[] result;
        try {
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b10_0000_0000_0000_0000_0000);
            assertArrayEquals(new byte[] { (byte) 0b111110_00, (byte) 0b10_001000, (byte) 0b10_000000,
                    (byte) 0b10_000000, (byte) 0b10_000000 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b11_1111_1111_1111_1111_1111_1111);
            assertArrayEquals(new byte[] { (byte) 0b111110_11, (byte) 0b10_111111, (byte) 0b10_111111,
                    (byte) 0b10_111111, (byte) 0b10_111111 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b10_1010_1010_1010_1010_1010);
            assertArrayEquals(new byte[] { (byte) 0b111110_00, (byte) 0b10_001010, (byte) 0b10_101010,
                    (byte) 0b10_101010, (byte) 0b10_101010 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b10_1010_1010_1010_1010_1010_1010);
            assertArrayEquals(new byte[] { (byte) 0b111110_10, (byte) 0b10_101010, (byte) 0b10_101010,
                    (byte) 0b10_101010, (byte) 0b10_101010 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b11_1010_0101_1010_0101_1010_0101);
            assertArrayEquals(new byte[] { (byte) 0b111110_11, (byte) 0b10_101001, (byte) 0b10_011010,
                    (byte) 0b10_010110, (byte) 0b10_100101 }, result);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * tests the code-point in the range
     * 
     *      0x4000000                           -   0x7FFFFFFF
     *      0b100_0000_0000_0000_0000_0000_0000 -   0b111_1111_1111_1111_1111_1111_1111_1111
     * 
     * encoded as
     * 
     *      0b1111110_x 0b10_xxxxxx 0b10_xxxxxx 0b10_xxxxxx 0b10_xxxxxx 0b10_xxxxxx
     */
    @Test
    public void test31BitsEncode() {
        byte[] result;
        try {
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b100_0000_0000_0000_0000_0000_0000);
            assertArrayEquals(new byte[] { (byte) 0b1111110_0, (byte) 0b10_000100, (byte) 0b10_000000,
                    (byte) 0b10_000000, (byte) 0b10_000000, (byte) 0b10_000000 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b111_1111_1111_1111_1111_1111_1111);
            assertArrayEquals(new byte[] { (byte) 0b1111110_0, (byte) 0b10_000111, (byte) 0b10_111111,
                    (byte) 0b10_111111, (byte) 0b10_111111, (byte) 0b10_111111 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b111_1111_1111_1111_1111_1111_1111_1111);
            assertArrayEquals(new byte[] { (byte) 0b1111110_1, (byte) 0b10_111111, (byte) 0b10_111111,
                    (byte) 0b10_111111, (byte) 0b10_111111, (byte) 0b10_111111 }, result);
            
            result = (byte[]) encodeMethod.invoke(encodeTestInstance, 0b101_1010_1111_1010_1111_1010_1111_1010);
            assertArrayEquals(new byte[] { (byte) 0b1111110_1, (byte) 0b10_011010, (byte) 0b10_111110,
                    (byte) 0b10_101111, (byte) 0b10_101011, (byte) 0b10_111010 }, result);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    @Test( expected = IllegalStateException.class )
    public void testEncoderAsAWhole() {
        byte[] buf = {0x00, 0x00, 0x56, 0x05};
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
                FileOutputStream outputStream = new FileOutputStream(TEST_FILE)) {
            UTF8Encoder utf8Encoder = new UTF8Encoder(inputStream, outputStream, Charset.forName("UTF-32"));
            utf8Encoder.setWriteBOM(true);
            
            assertFalse(utf8Encoder.isConversionDone());
            assertTrue(utf8Encoder.isWriteBOM());
            
            utf8Encoder.convert();
            
            assertTrue(utf8Encoder.isConversionDone());
                        
            
            byte[] generatedBuf = new byte[1024]; 
            try (InputStream in = new FileInputStream(TEST_FILE)) {
                int read = in.read(generatedBuf);

                byte[] generatedBufTrimmed = Arrays.copyOf(generatedBuf, read);

                byte[] expected = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF, (byte) 0b1110_0101,
                        (byte) 0b10_011000, (byte) 0b10_000101 };

                assertArrayEquals(expected, generatedBufTrimmed);
            }
            
            utf8Encoder.convert();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @Test (expected = UnsupportedEncodingException.class )
    public void testUnknownEncoding() throws UnsupportedEncodingException {
        try {
            new UTF8Encoder(new ByteArrayInputStream(new byte[] {}), new ByteArrayOutputStream(),
                    Charset.forName("UTF-16"));
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }
    
    @AfterClass
    public static void cleanUp() {
        File file = new File(TEST_FILE);
        if (file.exists())
            file.delete();
    }
}

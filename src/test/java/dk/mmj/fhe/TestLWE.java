package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.fhe.interfaces.SecretKey;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class TestLWE {
    private final LWEParameters params = new LWEParameters();
    private LWE lwe;
    private FHE.KeyPair keyPair;

    @Before
    public void setup() {
        lwe = new LWE();
        keyPair = lwe.generateKey(params);
        assertNotNull(keyPair);
    }

    @Test
    public void testKeyGeneration() {
        FHE.KeyPair keyPair2 = lwe.generateKey(params);

        assertNotNull(keyPair2);
        assertNotEquals(
                "Keypairs not allowed to be the same, for two calls (only with very small prob.)",
                keyPair, keyPair2
        );
    }

    @Test
    public void testRandomnessInEncrypt() {
        boolean m = true;
        Ciphertext c1 = lwe.encrypt(m, keyPair.getPublicKey());
        Ciphertext c2 = lwe.encrypt(m, keyPair.getPublicKey());

        assertNotEquals("Ciphertext should not match for two different encryptions", c1, c2);
    }

    @Test
    public void testEncryptDecrypt() {
        for (boolean m : new Boolean[]{false, true}) {
            Ciphertext c = lwe.encrypt(m, keyPair.getPublicKey());
            boolean decrypt = lwe.decrypt(c, keyPair.getSecretKey());

            assertEquals("Dec(Enc(m))!=m, for m=" + (m ? "True" : "False"), m, decrypt);
        }
    }

    @Test
    public void testNot() {
        final Boolean[] options = {false, true};

        final PublicKey pk = keyPair.getPublicKey();
        final SecretKey sk = keyPair.getSecretKey();

        for (Boolean m : options) {
            final Ciphertext c = lwe.encrypt(m, pk);
            final Ciphertext res = lwe.not(c, pk);

            final boolean decrypt = lwe.decrypt(res, sk);

            assertEquals("Homomorphic NOT did not match result of java NOT", !m, decrypt);
        }
    }

    @Test
    public void testNand() {
        final Boolean[] options = {false, true};

        final PublicKey pk = keyPair.getPublicKey();
        final SecretKey sk = keyPair.getSecretKey();

        for (Boolean m1 : options) {
            for (Boolean m2 : options) {

                final Ciphertext c1 = lwe.encrypt(m1, pk);
                final Ciphertext c2 = lwe.encrypt(m2, pk);
                final Ciphertext nand = lwe.nand(c1, c2, pk);

                final boolean decrypt = lwe.decrypt(nand, sk);

                assertEquals("Homomorphic NAND did not match result of java NAND", !(m1 & m2), decrypt);
            }
        }
    }

    @Test
    public void testAnd() {
        final Boolean[] options = {false, true};

        final PublicKey pk = keyPair.getPublicKey();
        final SecretKey sk = keyPair.getSecretKey();

        for (Boolean m1 : options) {
            for (Boolean m2 : options) {

                final Ciphertext c1 = lwe.encrypt(m1, pk);
                final Ciphertext c2 = lwe.encrypt(m2, pk);
                final Ciphertext res = lwe.and(c1, c2, pk);

                final boolean decrypt = lwe.decrypt(res, sk);

                assertEquals("Homomorphic AND did not match result of java AND", (m1 & m2), decrypt);
            }
        }
    }

    @Test
    public void testOr() {
        final Boolean[] options = {false, true};

        final PublicKey pk = keyPair.getPublicKey();
        final SecretKey sk = keyPair.getSecretKey();

        for (Boolean m1 : options) {
            for (Boolean m2 : options) {

                final Ciphertext c1 = lwe.encrypt(m1, pk);
                final Ciphertext c2 = lwe.encrypt(m2, pk);
                final Ciphertext res = lwe.or(c1, c2, pk);

                final boolean decrypt = lwe.decrypt(res, sk);

                assertEquals("Homomorphic OR did not match result of normal OR", (m1 | m2), decrypt);
            }
        }
    }

    @Test
    public void testXor() {
        final Boolean[] options = {false, true};

        final PublicKey pk = keyPair.getPublicKey();
        final SecretKey sk = keyPair.getSecretKey();

        for (Boolean m1 : options) {
            for (Boolean m2 : options) {

                final Ciphertext c1 = lwe.encrypt(m1, pk);
                final Ciphertext c2 = lwe.encrypt(m2, pk);
                final Ciphertext res = lwe.xor(c1, c2, pk);

                final boolean decrypt = lwe.decrypt(res, sk);

                assertEquals(
                        "Homomorphic XOR did not match result of normal XOR values: m1=" + m1 + ", m2=" + m2,
                        (m1 ^ m2),
                        decrypt
                );
            }
        }
    }

}

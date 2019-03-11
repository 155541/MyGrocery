package revolhope.splanes.com.mygrocery.helpers.database;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;

class Cryptography {

    private static final String alias = "AppMyGroceryEntry";
    private static final char[] pwd = "jd!eNR_$288db/beRGAJnd-bF3Kb:jTd03lk·(040".toCharArray();
    private static Cryptography instance;
    private KeyStore ks;
    private Cipher cipher;

    private Cryptography() throws Exception {
        ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
    }

    @Nullable
    static Cryptography getInstance() {
        try {
            if (instance == null) {
                instance = new Cryptography();
            }
            return instance;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    CryptographyObject encrypt(@NonNull byte[] data) {
        try {
            if (!ks.containsAlias(alias)) {
                newKey();
            }
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                                               KeyProperties.BLOCK_MODE_GCM + "/" +
                                               KeyProperties.ENCRYPTION_PADDING_NONE);
            cipher.init(Cipher.ENCRYPT_MODE, ks.getKey(alias, pwd));
            byte[] bytes = cipher.doFinal(data);
            GCMParameterSpec spec = cipher.getParameters().getParameterSpec(GCMParameterSpec.class);
            return new CryptographyObject(bytes, spec.getIV(), spec.getTLen());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    String decrypt(@NonNull CryptographyObject cryptographyObject) {

        try {
            //SecretKey k = (SecretKey) ks.getKey(alias, null);
            if (cipher == null) {
                cipher = Cipher.getInstance( KeyProperties.KEY_ALGORITHM_AES + "/" +
                        KeyProperties.BLOCK_MODE_GCM + "/" +
                        KeyProperties.ENCRYPTION_PADDING_NONE);
            }
            GCMParameterSpec spec = new GCMParameterSpec(cryptographyObject.tLength,
                    cryptographyObject.iv);
            cipher.init(Cipher.DECRYPT_MODE, ks.getKey(Cryptography.alias, pwd), spec);
            return new String(cipher.doFinal(cryptographyObject.data));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void newKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore");
        KeyGenParameterSpec.Builder builder =  new KeyGenParameterSpec.Builder(
                alias,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
        builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setUserAuthenticationRequired(false)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);
        kg.init(builder.build());
        kg.generateKey();
    }

    static class CryptographyObject {
        byte[] data;
        byte[] iv;
        int tLength;
        CryptographyObject (byte[] data, byte[] iv, int tLength) {
            this.data = data;
            this.iv = iv;
            this.tLength = tLength;
        }
    }
}

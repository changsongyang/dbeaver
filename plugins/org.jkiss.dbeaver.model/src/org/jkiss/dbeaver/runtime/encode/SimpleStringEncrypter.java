/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.runtime.encode;

import org.jkiss.utils.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Encryption util
 * <p>
 * <b>Note:</b> This class does not provide a strong encryption mechanism and its usage is discouraged. It is kept for backward compatibility only.
 */
public class SimpleStringEncrypter implements PasswordEncrypter {

    public static final SimpleStringEncrypter INSTANCE = new SimpleStringEncrypter();

    //public static final String SCHEME_DES = "DES";
    private static final byte[] PASSWORD_ENCRYPTION_KEY = "sdf@!#$verf^wv%6Fwe%$$#FFGwfsdefwfe135s$^H)dg".getBytes(Charset.defaultCharset());

    private static final String CHARSET = "UTF8";

    //private String encryptionKey;
    //private DESKeySpec keySpec;
    //private Cipher cipher;

    private SimpleStringEncrypter()
    {
/*
        try {
            byte[] keyAsBytes = encryptionKey.getBytes(CHARSET);

            keySpec = new DESKeySpec(keyAsBytes);

            //keyFactory = SecretKeyFactory.getInstance(encryptionScheme);
            cipher = Cipher.getInstance(SCHEME_DES);

        } catch (InvalidKeyException e) {
            throw new EncryptionException(e);
        } catch (UnsupportedEncodingException e) {
            throw new EncryptionException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException(e);
        }
*/

    }

    private void xorStringByKey(byte[] plainBytes) throws UnsupportedEncodingException
    {
        int keyOffset = 0;
        for (int i = 0; i < plainBytes.length; i++) {
            byte keyChar = PASSWORD_ENCRYPTION_KEY[keyOffset];
            keyOffset++;
            if (keyOffset >= PASSWORD_ENCRYPTION_KEY.length) {
                keyOffset = 0;
            }
            plainBytes[i] ^= keyChar;
        }
    }

    @Override
    public String decrypt(String encryptedString) throws EncryptionException
    {
        if (encryptedString == null || encryptedString.trim().length() <= 0) {
            throw new IllegalArgumentException("Empty encrypted string");
        }

        try {
            byte[] cleartext = Base64.decode(encryptedString);
            xorStringByKey(cleartext);
            if (cleartext[cleartext.length - 2] != 0 || cleartext[cleartext.length - 1] != -127) {
                throw new EncryptionException("Invalid encrypted string");
            }
            return new String(cleartext, 0, cleartext.length - 2, CHARSET);
/*
            SecretKey key = makeSecretKey();
            cipher.init(Cipher.DECRYPT_MODE, key*/
/*, SecureRandom.getInstance("SHA1PRNG")*//*
);
            byte[] cleartext = Base64.decode(encryptedString);
            byte[] ciphertext = cipher.doFinal(cleartext);

            return new String(ciphertext, CHARSET);
*/
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

/*
    private SecretKey makeSecretKey()
    {
        return new SecretKeySpec(keySpec.getKey(), "DES");
    }
*/

}

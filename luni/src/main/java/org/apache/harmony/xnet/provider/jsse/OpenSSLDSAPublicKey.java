/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.harmony.xnet.provider.jsse;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

public class OpenSSLDSAPublicKey implements DSAPublicKey {
    private final OpenSSLKey key;

    private OpenSSLDSAParams params;

    OpenSSLDSAPublicKey(OpenSSLKey key) {
        this.key = key;
    }

    OpenSSLKey getOpenSSLKey() {
        return key;
    }

    OpenSSLDSAPublicKey(DSAPublicKeySpec dsaKeySpec) throws InvalidKeySpecException {
        try {
            key = new OpenSSLKey(NativeCrypto.EVP_PKEY_new_DSA(
                    dsaKeySpec.getP().toByteArray(),
                    dsaKeySpec.getQ().toByteArray(),
                    dsaKeySpec.getG().toByteArray(),
                    dsaKeySpec.getY().toByteArray(),
                    null));
        } catch (Exception e) {
            throw new InvalidKeySpecException(e);
        }
    }

    private void ensureReadParams() {
        if (params == null) {
            params = new OpenSSLDSAParams(key);
        }
    }

    static OpenSSLKey getInstance(DSAPublicKey dsaPublicKey) throws InvalidKeyException {
        try {
            final DSAParams dsaParams = dsaPublicKey.getParams();
            return new OpenSSLKey(NativeCrypto.EVP_PKEY_new_DSA(
                    dsaParams.getP().toByteArray(),
                    dsaParams.getQ().toByteArray(),
                    dsaParams.getG().toByteArray(),
                    dsaPublicKey.getY().toByteArray(),
                    null));
        } catch (Exception e) {
            throw new InvalidKeyException(e);
        }
    }

    @Override
    public DSAParams getParams() {
        ensureReadParams();
        return params;
    }

    @Override
    public String getAlgorithm() {
        return "DSA";
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    @Override
    public byte[] getEncoded() {
        return NativeCrypto.i2d_PUBKEY(key.getPkeyContext());
    }

    @Override
    public BigInteger getY() {
        ensureReadParams();
        return params.getY();
    }
}

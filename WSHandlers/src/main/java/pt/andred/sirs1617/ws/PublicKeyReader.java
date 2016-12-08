package pt.andred.sirs1617.ws;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class PublicKeyReader {

  public static PublicKey get(String filename)
    throws Exception {

    byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());

    X509EncodedKeySpec spec =
      new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePublic(spec);
  }
}
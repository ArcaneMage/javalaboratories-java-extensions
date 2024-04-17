package org.javalaboratories.core.cryptography.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessageTest {

    private static final String TEXT_SIGNED = "AAABJjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALXvARIORX4JcyvIqqy6uJWZF" +
            "J0/PkOrTGFBTOXIjUr86OeJawYyo04Qr0fA4TJHrryf3nhlMPWyAj7mTUhx8BkkxvS0n0jGM/MWLrlt3FVel1GuGlKMNkV5uJE+/+NP+C/l" +
            "5jwQd3zWDICYwEBEOd9xus9CwcgjGETCQhFtSd47nQJlk9GicevKSh1WjobLCORgUhA8b94ugTazSHVzL9XCoVOOiNoKOXgAZz+qXPu+BCx" +
            "bCg3fY1YWeL8TJomCo3t38p/j/Trybtsw5dhbi/O8CNHOYkqxHTZR3sR0MC9CSCrS8W0F29kpzvDMCVmeinD9+2Kec3VB2l96wnnYDOkCAw" +
            "EAAQAAAQBdcdTMuX4nJenexUNEEDcEDIP4kcbeRhO6dCETvZ6m4VU9MUTrLKasaKjH/5l0jKF2ZSztggD5QjUk4lkUzT74B4Fv4SI6xnLHB" +
            "kWedJVxy3yut/0bX4WLmVhtIiynVdzrXK+eITAZhhg3/IfSMEZx2B1vPKvK3TRCXVkkQb+UHFON0fitEtKq3qEmzX/MIZRxR+966agx2jEg" +
            "NLe6eDa1olbrZicwhTwg4di729+K1b6RthZdMPW5SHgrwE1/Jvw/NP3qgu2MZXioHCU75Hh6DnudyI+G0+hBoF6CWF/Udq3v8OO7WEASts9" +
            "HL10cEoEdtCouhopS2zKTRc0GBkFjAAABAC1LTS3xvi4qVmdL4xNBBeGLvWzmB/V8QC1j3m4pBRd9k2+8rNaa5Hre2FH4mGJJQ067XMkWDG" +
            "LCiLSh6aNSw/qEa+c5xDHUah5QBO36fTvIb4rcn68zFtz4LasA2Eu8MSkPa95MDvuWwSPucc+WKUJs6NiExo8CCcUkjXcgu+5XH5JALUJ97" +
            "3R1xJH6Gmv7hlB7rF0nuXIWHuFj1hwElVTJJSEeAlzv5zqOxmazUmla2UbgbW/EJu/MgKuehAqubz8jhWyP51VmOzQOky5p0kYRDOoNmbsz" +
            "SC3qKrZMVI81oQs7Z9tiSrRrgIuoRKw9N9+nihCwW+lZI8wc7Spn2MhRk21pfPdBwbzXx4Uu+9X0dXDn4gNVL7BBV4v5tjObDR8zh5L4L++" +
            "tWBXjyUyJVTQOchlSKpnJzDqsFVjz9YL2glN2JcnQrI1QjiqGaewjRXlCPVvV9GrY/oswSa/i5ziSKxRc15jcHgFl+bpOqGbZ";

    private static Message message;

    @BeforeEach
    public void setup() {
        message = new Message(Base64.getDecoder().decode(TEXT_SIGNED));
    }

    @Test
    public void testSigned() {
        assertNotNull(message.getSigned());
        assertNotNull(message.getPublicKey());
        assertNotNull(message.getSignature());
        assertNotNull(message.getData());
    }

    @Test
    public void testSignedAsBase64() {
        assertNotNull(message.getSignedAsBase64());
        assertEquals(TEXT_SIGNED,message.getSignedAsBase64());
    }

    @Test
    public void testToString() {
        assertEquals("[signature=true,signedHeaderBlock=true]",message.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(message,new Message(Base64.getDecoder().decode(TEXT_SIGNED)));
    }
}
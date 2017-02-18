package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;
import org.springframework.util.Assert;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Port extends ReflectionObjectTreeAware implements Serializable {
    private Protocol protocol;
    private int portNumber;
    private JonTester jonTester;

    // don't use auto-generation here, enforce use of setters
    public static Port from(@NonNull final Protocol _protocol, final int _portNumber) {
        final Port port = new Port();
        port.setProtocol(_protocol);
        port.setPortNumber(_portNumber);
        port.setJonTester(new JonTester(new int[]{1,2,3}));

        return port;
    }

    public void setPortNumber(final int _portNumber) {
        Assert.isTrue(_portNumber >= 0 && _portNumber <= 65535, String.format("port number %d not within 0-65535 range", _portNumber));
        this.portNumber = _portNumber;
    }
}

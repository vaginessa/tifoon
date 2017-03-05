package com.elixlogic.tifoon.domain.model.scanner;

import com.elixlogic.tifoon.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OpenHost extends ReflectionObjectTreeAware implements Serializable {
    private Map<Integer, Port> openPorts;

    public static OpenHost from(@NonNull final List<Port> _ports) {
        return new OpenHost(_ports
                .stream()
                .collect(Collectors.toMap(Port::hashCode, Function.identity())));
    }
}

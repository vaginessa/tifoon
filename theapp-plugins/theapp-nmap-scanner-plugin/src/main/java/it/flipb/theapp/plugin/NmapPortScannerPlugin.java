package it.flipb.theapp.plugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.AbstractScannerPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.host.Address;
import org.nmap4j.data.nmaprun.Host;
import org.nmap4j.parser.OnePassParser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class NmapPortScannerPlugin extends AbstractScannerPlugin {
    private static final String PROVIDES = "nmap";

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    public NetworkResult scan(@NonNull final PortScannerJob _request,
                              @NonNull final ExecutorPlugin _executorPlugin) {
        try {
            final String scanResultFilename = String.format("nmap_scan_result_%s.xml", UUID.randomUUID().toString());
            final String[] commandWithArguments = buildNmapCommandWithArguments(_request, scanResultFilename);
            final byte result[] = _executorPlugin.dispatch("nmap", commandWithArguments, scanResultFilename);

            return mapXmlToPortScannerResult(_request.getDescription(), result);
        }
        catch (Exception _e) {
            log.error("Error running nmap", _e);

            return null;
        }
    }

    @NonNull
    private String[] buildNmapCommandWithArguments(@NonNull final PortScannerJob _request,
                                                   @NonNull final String _scanResultFilename) {
        final String nmapPortRanges = _request.getPortRanges()
                .stream()
                .map(PortRange::toSingleOrIntervalString)
                .collect(Collectors.joining(","));

        final List<String> targetHosts = _request.getAddresses()
                .stream()
                .map(InetAddress::getHostAddress)
                .collect(Collectors.toList());

        final List<String> argumentsList = Lists.newArrayList("-oX", _scanResultFilename, "-p", nmapPortRanges);
        argumentsList.addAll(targetHosts);

        return argumentsList.toArray(new String[argumentsList.size()]);
    }

    @NonNull
    private NetworkResult mapXmlToPortScannerResult(@NonNull final String _description,
                                                    final byte[] _result) {
        final Map<InetAddress, List<Port>> openPortsMap = Maps.newHashMap();

        if (_result != null) {
            final OnePassParser opp = new OnePassParser();
            final NMapRun nmapRun = opp.parse(new String(_result, StandardCharsets.UTF_8), OnePassParser.STRING_INPUT);

            for(Host host : nmapRun.getHosts()) {
                final List<Port> openPorts = host.getPorts().getPorts().stream()
                        .filter(port -> port.getState().getState().equals("open"))
                        .map(port -> Port.from(mapProtocol(port.getProtocol()), (int) port.getPortId()))
                        .collect(Collectors.toList());

                if (openPorts.isEmpty()) {
                    // nothing to see here, carry on, please... :)
                    continue;
                }

                for(Address address : host.getAddresses()) {
                    try {
                        final InetAddress inetAddress = InetAddress.getByName(address.getAddr());
                        openPortsMap.put(inetAddress, openPorts);
                    } catch (UnknownHostException _e) {
                        // ignore
                    }
                }
            }
        }

        return new NetworkResult(_description, openPortsMap);
    }

    @NonNull
    private Protocol mapProtocol(@NonNull final String _protocol) {
        switch(_protocol) {
            case "tcp":
                return Protocol.TCP;
            case "udp":
                return Protocol.UDP;
            case "stcp":
                return Protocol.STCP;
            case "ip":
                return Protocol.IP;
            default:
                throw new IllegalArgumentException("Unknown protocol: " + _protocol);
        }
    }
}

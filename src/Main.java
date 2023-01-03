import backendtv.server.ServerApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import datafetch.DataFetch;

import java.io.File;
import java.io.IOException;

/**
 * <p>Entry point for the backendTV application.</p>
 *
 * @see ObjectMapper
 * @see ArrayNode
 * @since 1.0.0
 * @author Mihai Negru
 */
public final class Main {
    static int counter = 1;

    private Main() {
        // Do not let anyone instantiate this class
    }

    /**
     * <p>Takes two {@code String} objects as two file paths.</p>
     *
     * <p>Opens first file for readying and fetches the data into the
     * {@code DataFetch} class which will be processed in the main
     * application.</p>
     *
     * <p>Opens second file for writing and prints all the collected
     * information into file as a JSON object.</p>
     * @param args files that need to be processed.
     */
    public static void main(final String[] args) throws IOException, IllegalArgumentException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Bad main args list");
        }

        final ObjectMapper objectMapper = new ObjectMapper();

        DataFetch inputData = objectMapper.readValue(new File(args[0]), DataFetch.class);

        final ArrayNode outputData = objectMapper.createArrayNode();

        ServerApp.connect().start(inputData, outputData);

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(args[1]), outputData);

        if (counter == 8) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("my_result_" + 7 + ".json"),
                    outputData);
        }

        ++counter;
    }
}
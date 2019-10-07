package sukumaar;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LogAnalysis {

    public static void main(String[] args) throws IOException {
        LogAnalysis logAnalysis = new LogAnalysis();
        String logFileName = "sample.log";
        ApiInfo apiInfo = logAnalysis.scanForApiUsage(logFileName);
        logAnalysis.printApi(apiInfo);
    }

    /**
     * Prints usage count and percentage per API usage along with API name
     *
     * @param apiInfo
     */
    private void printApi(ApiInfo apiInfo) {
        Map sortedMap = sortByValue(apiInfo.getCountsPerApiMap());
        System.out.format("%32s%10s%16s%n", "API", "Count", "Percentage");
        System.out.println("----------------------------------------------------------");
        for (Map.Entry<String, Integer> entry : ((Map<String, Integer>) sortedMap).entrySet()) {
            System.out.format("%32s%10s%16s%n",
                    entry.getKey(),
                    entry.getValue(),
                    (((float) entry.getValue() / (float) apiInfo.getTotalApiCount()) * 100f));
        }
    }

    /**
     * Scans for api name with its usage count
     *
     * @param logFileName
     * @return Total API count and per API usage count with overall percentage
     * @throws IOException
     */
    private ApiInfo scanForApiUsage(String logFileName) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(readResourceAsStream(logFileName), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        Map<String, Integer> countsPerApiMap = new HashMap<>();
        int total = 0;
        for (String line; (line = reader.readLine()) != null; total++) {
            String apiName = extractApiName(line);
            countsPerApiMap.put(apiName, countsPerApiMap.getOrDefault(apiName, 0) + 1);
        }
        return new ApiInfo(total, countsPerApiMap);
    }

    /**
     * Works as per the name
     *
     * @param name
     * @return
     */
    private InputStream readResourceAsStream(String name) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(name);
    }

    /**
     * Extracting name of API calls with some string splits
     *
     * @param logLine
     * @return
     */
    private String extractApiName(String logLine) {
        String stringUnderQuotes = StringUtils.substringBetween(logLine, "\"", "\"");
        /*
         * Splitting by double quotes can be removed and replaced with other code,
         * current input data decreases importance of the above code
         */
        List<String> l = Arrays.asList(stringUnderQuotes.split(" "));
        String requestUrl = l.get(l.size() - 2);
        List<String> l2 = Arrays.asList(requestUrl.split("\\?")[0].split("/"));
        return l2.get(l2.size() - 1).split("\\.")[0];
    }


    /**
     * Sorting map by values in descending order
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private class ApiInfo {
        private int totalApiCount;
        private Map<String, Integer> countsPerApi;

        public ApiInfo(int totalApiCount, Map<String, Integer> countsPerApi) {
            this.totalApiCount = totalApiCount;
            this.countsPerApi = countsPerApi;
        }

        public int getTotalApiCount() {
            return totalApiCount;
        }

        public Map<String, Integer> getCountsPerApiMap() {
            return countsPerApi;
        }
    }
}

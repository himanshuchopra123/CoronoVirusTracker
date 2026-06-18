package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.model.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.http.HttpRequest;

import java.net.http.HttpClient;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = Collections.synchronizedList(new ArrayList<>());

    private final ExecutorService threadPool = Executors.newFixedThreadPool(100);

    private final ScheduledExecutorService monitorPool = Executors.newScheduledThreadPool(4);

    private final Object lock = new Object();

    private final AtomicInteger processedCount = new AtomicInteger(0);

    private volatile boolean isProcessing = false;

    public List<LocationStats> getAllStats() {
        synchronized (lock) {
            while (isProcessing) {
                try {
                    lock.wait(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return new ArrayList<>(allStats);
        }
    }

    @PostConstruct
    public void init() {
        monitorPool.scheduleAtFixedRate(this::monitorProgress, 0, 500, TimeUnit.MILLISECONDS);
        monitorPool.scheduleAtFixedRate(this::computeRunningTotals, 0, 1, TimeUnit.SECONDS);
        try {
            fetchVirusData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void monitorProgress() {
        synchronized (lock) {
            int count = processedCount.get();
            int size = allStats.size();
            for (int i = 0; i < size; i++) {
                allStats.get(i).getLatestTotal();
            }
        }
    }

    private void computeRunningTotals() {
        synchronized (lock) {
            int runningTotal = 0;
            for (int i = 0; i < allStats.size(); i++) {
                runningTotal += allStats.get(i).getLatestTotal();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Scheduled(cron = " * * 1 * * *")
    public void fetchVirusData() throws Exception, InterruptedException {
        synchronized (lock) {
            isProcessing = true;
        }

        List<LocationStats> newStats = Collections.synchronizedList(new ArrayList<>());
        processedCount.set(0);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        List<CSVRecord> recordList = new ArrayList<>();
        for (CSVRecord record : records) {
            recordList.add(record);
        }

        CountDownLatch latch = new CountDownLatch(recordList.size());
        List<Future<LocationStats>> futures = new ArrayList<>();

        for (CSVRecord record : recordList) {
            Future<LocationStats> future = threadPool.submit(() -> {
                try {
                    Thread.sleep(10);

                    LocationStats locationStats = new LocationStats();

                    synchronized (lock) {
                        locationStats.setState(record.get("Province/State"));
                        locationStats.setCountry(record.get("Country/Region"));
                    }

                    int latestCases = Integer.parseInt(record.get(record.size() - 1));
                    int previousDayCases = Integer.parseInt(record.get(record.size() - 2));

                    synchronized (lock) {
                        locationStats.getLatestTotal(latestCases);
                        locationStats.setDiffFromPreviousDay(latestCases - previousDayCases);
                    }

                    synchronized (newStats) {
                        newStats.add(locationStats);
                    }

                    processedCount.incrementAndGet();
                    return locationStats;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await();

        for (Future<LocationStats> future : futures) {
            future.get();
        }

        synchronized (lock) {
            this.allStats = Collections.synchronizedList(new ArrayList<>(newStats));
            isProcessing = false;
            lock.notifyAll();
        }
    }

    @PreDestroy
    public void shutdown() {
        threadPool.shutdown();
        monitorPool.shutdown();
        try {
            threadPool.awaitTermination(30, TimeUnit.SECONDS);
            monitorPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            monitorPool.shutdownNow();
        }
    }
}



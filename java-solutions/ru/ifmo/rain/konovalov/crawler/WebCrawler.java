package ru.ifmo.rain.konovalov.crawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler implements Crawler {
    Downloader downloader;
    ExecutorService executorDownload;
    ExecutorService executorExtractor;
    public WebCrawler(Downloader downloader, int downgrades, int extractors, int ignore) {
        this.executorDownload = Executors.newFixedThreadPool(downgrades);
        this.executorExtractor = Executors.newFixedThreadPool(extractors);
        this.downloader = downloader;
    }

    /**
     * Downloads web site up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @return download result.
     */
    @Override
    public Result download(String url, int depth) {

        return null;
    }

    /**
     * Closes this web-crawler, relinquishing any allocated resources.
     */
    @Override
    public void close() {
        executorDownload.shutdown();
        executorExtractor.shutdown();
        if (!executorDownload.isTerminated())
            executorDownload.shutdownNow();
        if (!executorExtractor.isTerminated())
            executorExtractor.shutdownNow();
    }

    private static int safeGet(String[] args, int index) {
        if (args.length <= index)
            return 1;
        return  Integer.parseInt(args[index]);
    }

    public static void main(String[] args) {
        if (args == null || args.length < 1 || args.length > 5) {
            System.out.println("Invalid input");
            return;
        }
        try {
            Downloader downloader = new CachingDownloader();
            String url = args[0];
            int depth = safeGet(args, 1), downloads = safeGet(args, 2), extractors = safeGet(args, 3), perHost = safeGet(args, 4);
            try (WebCrawler webCrawler = new WebCrawler(downloader, downloads, extractors, perHost)) {
                Result result = webCrawler.download(url, depth);
            }
        } catch (IOException e) {
            System.out.println("Error creating CachingDownloader");
        }
    }
}

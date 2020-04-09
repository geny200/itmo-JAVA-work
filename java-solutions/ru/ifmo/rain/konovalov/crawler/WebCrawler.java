package ru.ifmo.rain.konovalov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link JarImpler} interface.
 *
 * @author Geny200
 * @see Crawler
 * @see info.kgeorgiy.java.advanced.crawler.Crawler
 */
public class WebCrawler implements Crawler {
    private Downloader downloader;
    private ExecutorService executorDownload;
    private ExecutorService executorExtractor;

    /**
     * Constructs a new WebCrawler.
     */
    public WebCrawler(Downloader downloader, int downgrades, int extractors, int ignore) {
        this.executorDownload = Executors.newFixedThreadPool(downgrades);
        this.executorExtractor = Executors.newFixedThreadPool(extractors);
        this.downloader = downloader;
    }

    /**
     * Exception class for WebCrawler.
     */
    protected static class WebCrawlerException extends Exception {
        public WebCrawlerException(String message) {
            super(message);
        }
    }

    private static class Vertex {
        private final String url;
        private final long level;

        Vertex(String url, long level) {
            this.url = url;
            this.level = level;
        }

        String getUrl() {
            return url;
        }

        long getLevel() {
            return level;
        }
    }

    private static class DownloadTask {
        private final Downloader downloader;
        private final List<String> downloads;
        private final ConcurrentMap<String, IOException> badDownloads;
        private final ConcurrentMap<String, Boolean> passedUrl;
        private final BlockingQueue<Vertex> queue;
        private AtomicInteger work;
        private final ExecutorService exeDownload;
        private final ExecutorService exeExtractor;

        public DownloadTask(Downloader downloader, ExecutorService exeDownload, ExecutorService exeExtractor) {
            this.downloader = downloader;
            this.exeDownload = exeDownload;
            this.exeExtractor = exeExtractor;
            this.work = new AtomicInteger();
            this.queue = new LinkedBlockingQueue<>();
            this.badDownloads = new ConcurrentHashMap<>();
            this.downloads = new LinkedList<>();
            this.passedUrl = new ConcurrentHashMap<>();
        }

        private void extract(final Vertex vertex, final Document document) {
            try {
                for (String linkUrl : document.extractLinks())
                    queue.put(new Vertex(linkUrl, vertex.getLevel() + 1));
                synchronized (downloads) {
                    downloads.add(vertex.getUrl());
                }
            } catch (IOException e) {
                badDownloads.putIfAbsent(vertex.getUrl(), e);
            } catch (InterruptedException ignored) {

            } finally {
                if (work.decrementAndGet() == 0) {
                    try {
                        queue.put(vertex);
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }

        private void download(final Vertex vertex) {
            try {
                final Document document = downloader.download(vertex.url);
                exeExtractor.submit(() -> extract(vertex, document));
                work.incrementAndGet();
            } catch (IOException e) {
                badDownloads.putIfAbsent(vertex.getUrl(), e);
            } catch (RejectedExecutionException ignored) {

            } finally {
                if (work.decrementAndGet() == 0) {
                    try {
                        queue.put(vertex);
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }

        public void runBFS(String startUrl, int maxDepth) throws InterruptedException {
            queue.put(new Vertex(startUrl, 1));
            while (work.get() > 0 || queue.size() > 0) {
                Vertex localVertex = queue.take();
                if (localVertex.getLevel() > maxDepth)
                    continue;
                if (passedUrl.putIfAbsent(localVertex.getUrl(), true) == null) {
                    try {
                        work.incrementAndGet();
                        exeDownload.submit(() -> download(localVertex));
                    } catch (RejectedExecutionException ignore) {

                    }
                }
            }
        }

        public Result getResult() {
            return new Result(downloads, badDownloads);
        }
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
        DownloadTask task = new DownloadTask(downloader, executorDownload, executorExtractor);
        try {
            task.runBFS(url, depth);
        } catch (InterruptedException e) {
            return null;
        }
        return task.getResult();
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

    private static int safeGet(String[] args, int index) throws WebCrawlerException {
        if (args.length <= index) {
            return 1;
        }
        if (args[index] != null) {
            return Integer.parseInt(args[index]);
        }
        throw new WebCrawlerException("Invalid input");
    }

    /**
     * Start WebCrawler.
     * Use to start:
     * <ul>
     *         <li> {@code WebCrawler url [depth [downloads [extractors [perHost]]]]}
     *         calls {@link WebCrawler#download(String, int)}
     *         </li>
     * </ul>
     *
     * @param args array of input parameters ({@link java.lang.String}).
     * @see WebCrawler#download(String, int)
     */
    public static void main(String[] args) {
        try {
            if (args == null || args.length < 1 || args.length > 5) {
                throw new WebCrawlerException("Invalid input");
            }
            try {
                Downloader downloader = new CachingDownloader();
                String url = args[0];
                int depth = safeGet(args, 1), downloads = safeGet(args, 2), extractors = safeGet(args, 3), perHost = safeGet(args, 4);
                try (WebCrawler webCrawler = new WebCrawler(downloader, downloads, extractors, perHost)) {
                    Result result = webCrawler.download(url, depth);

                    if (result.getDownloaded().size() > 0) {
                        System.out.println("\"OK\" urls " + result.getDownloaded().size() + " :");
                        result.getDownloaded().forEach(System.out::println);
                    }

                    if (result.getErrors().size() > 0) {
                        System.out.println("\"Error\" urls " + result.getErrors().size() + " :");
                        result.getErrors().forEach((s, e) -> System.out.println(s + " - " + e.getMessage()));
                    }
                    System.out.println("Finish downloaded");
                }
            } catch (IOException e) {
                throw new WebCrawlerException("Error creating CachingDownloader");
            }
        } catch (WebCrawlerException e) {
            System.out.println(e.getMessage());
        }
    }
}

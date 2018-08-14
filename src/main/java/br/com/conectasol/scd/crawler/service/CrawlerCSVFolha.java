package br.com.conectasol.scd.crawler.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.conectasol.scd.util.CloseUtil;
import br.com.conectasol.scd.util.PathProperties;

@Service
public class CrawlerCSVFolha {

	@Autowired
	private PathProperties prop;
	private ThreadPoolExecutor executor;
	private static AtomicInteger at = new AtomicInteger(0);

	@PostConstruct
	public void init() {
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	}

	public List<String> buscarArquivos() throws IOException {
		Document document = Jsoup.connect(prop.getUrlfolha()).get();

//		document.select("div[id=listing]").select("a").parallelStream().forEach((element) -> {
//			final String urlDownload = element.attr("abs:href");
//			final String filename = element.attr("href");
//			this.executor.execute(() -> {
//				downloadUsingNIO(urlDownload, prop.getCsv() + filename.split("/")[1]);
//				System.out.println(at.incrementAndGet());
//			});
//		});

//		ForkJoinPool forkJoinPool = new ForkJoinPool(4);
//		this.executor.submit(() -> {
			document.select("div[id=listing]").select("a").parallelStream().forEach((element) -> {
				final String urlDownload = element.attr("abs:href");
				final String filename = element.attr("href");
				this.executor.submit(() -> {
					downloadUsingNIO(urlDownload, prop.getCsv() + filename.split("/")[1]);
					System.out.println(at.incrementAndGet());
				});
			});
//		});

		while (at.get() <= 78) {
		}

		return Collections.emptyList();
	}

	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			new CrawlerCSVFolha().buscarArquivos();
			long elapsed = System.currentTimeMillis() - start;
			System.out.println(elapsed);
		} catch (IOException e) {
			Logger.getLogger("finish").info(e.getMessage());
		}
	}

	private void downloadUsingNIO(String urlStr, String filepath) {
		if (Files.exists(Paths.get(filepath))) {
			return;
		}
		ReadableByteChannel rbc = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(urlStr);
			rbc = Channels.newChannel(url.openStream());
			fos = new FileOutputStream(filepath);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			Logger.getLogger("download").info(e.getMessage());
		} finally {
			CloseUtil.close(fos, rbc);
		}
	}

}

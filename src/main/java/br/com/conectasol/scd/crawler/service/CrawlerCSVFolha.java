package br.com.conectasol.scd.crawler.service;

import java.io.File;
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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.conectasol.scd.util.CloseUtil;
import br.com.conectasol.scd.util.PathProperties;
import br.com.conectasol.scd.util.UTF8ToAnsiUtils;

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

	private void downloadUsingNIO(String urlStr, String filepath) {
		if (Files.exists(Paths.get(filepath))) {
			return;
		}
		
		UTF8ToAnsiUtils.convert(urlStr, filepath);
	}

	private static void run() {
		String urlStr = "/home/sergio/Downloads/files/";
		String urlStr2 = "/home/sergio/Downloads/files2/";
		File folder = new File(urlStr2);
		for (File file : folder.listFiles()) {
			String absolutePath = file.getAbsolutePath();
			if (!absolutePath.endsWith(".csv")) {
				continue;
			}

			UTF8ToAnsiUtils.convert(absolutePath, urlStr + file.getName());
		}

	}

	public static void main(String[] args) {
		run();

//		try {
//			long start = System.currentTimeMillis();
//			new CrawlerCSVFolha().buscarArquivos();
//			long elapsed = System.currentTimeMillis() - start;
//			System.out.println(elapsed);
//		} catch (IOException e) {
//			Logger.getLogger("finish").info(e.getMessage());
//		}
	}

}

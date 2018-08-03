package br.com.conectasol.scd.crawler.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import br.com.conectasol.scd.util.CloseUtil;

public class CrawlerCSVFolha {

	private static final String URL = "http://www.transparencia.go.gov.br/dadosabertos/index.php?dir=FolhaPagamento%2F";
	private static final String PATH = "/home/sergio/Downloads/files/";
//	private final ThreadPoolExecutor executor;
//	private static AtomicInteger at = new AtomicInteger(0);

	public CrawlerCSVFolha() {
//		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(25);
	}

	public List<String> buscarArquivos() throws IOException {
		Document document = Jsoup.connect(URL).get();

		document.select("div[id=listing]").select("a").parallelStream().forEach((element) -> {
			final String urlDownload = element.attr("abs:href");
			final String filename = element.attr("href");
			downloadUsingNIO(urlDownload, PATH + filename.split("/")[1]);
		});

//		for (Element element : document.select("div[id=listing]").select("a")) {
//			final String urlDownload = element.attr("abs:href");
//			final String filename = element.attr("href");
////			this.executor.submit(() -> {
////				try {
////					Thread.sleep(20000);
////				} catch (InterruptedException e) {
////					e.printStackTrace();
////				}
////				System.out.println(at.incrementAndGet());
//////				downloadUsingNIO(urlDownload, PATH + filename.split("/")[1]);
////			});
//			
//		}

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

	private void downloadUsingNIO(String urlStr, String file) {
		ReadableByteChannel rbc = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(urlStr);
			rbc = Channels.newChannel(url.openStream());
			fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			Logger.getLogger("download").info(e.getMessage());
		} finally {
			CloseUtil.close(fos, rbc);
		}
	}

}
